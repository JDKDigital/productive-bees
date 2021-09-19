package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeeCage extends Item
{
    public BeeCage(Properties properties) {
        super(properties);
    }

    public static boolean isFilled(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return !itemStack.isEmpty() && tag != null && tag.contains("entity");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level playerWorld = context.getPlayer().getCommandSenderWorld();
        ItemStack stack = context.getItemInHand();

        if (playerWorld.isClientSide() || !isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Bee entity = getEntityFromStack(stack, worldIn, true);

        if (entity != null) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                entity.hivePos = null;
            }

            BlockPos blockPos = pos.relative(context.getClickedFace());
            entity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            worldIn.addFreshEntity(entity);

            postItemUse(context);
        }

        return InteractionResult.SUCCESS;
    }

    protected void postItemUse(UseOnContext context) {
        // Delete stack
        if (context.getPlayer() != null) {
            context.getPlayer().getInventory().removeItem(context.getItemInHand());
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity targetIn, InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof Bee) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return InteractionResult.PASS;
        }

        Bee target = (Bee) targetIn;

        boolean addToInventory = true;
        ItemStack cageStack = new ItemStack(itemStack.getItem());
        if (itemStack.getCount() == 1) {
            cageStack = itemStack;
            addToInventory = false;
        }

        captureEntity(target, cageStack);

        if (addToInventory || player.isCreative()) {
            if (!player.getInventory().add(cageStack)) {
                player.drop(cageStack, false);
            }
            itemStack.shrink(1);
        }

        player.swing(hand);

        if (player instanceof ServerPlayer) {
            ModAdvancements.CATCH_BEE.trigger((ServerPlayer) player, cageStack);
        }
        target.discard();

        return InteractionResult.SUCCESS;
    }

    public static void captureEntity(Bee target, ItemStack cageStack) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getString());
        } else {
            nbt.putString("name", target.getName().getString());
        }
        target.saveWithoutId(nbt);

        nbt.remove("Motion");
        nbt.remove("Pos");
        nbt.remove("Rotation");

        nbt.putBoolean("isProductiveBee", target instanceof ProductiveBee);

        String modId = target.getType().getRegistryName().getNamespace();
        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }
        nbt.putString("mod", modName);

        cageStack.setTag(nbt);
    }

    @Nullable
    public static Bee getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
        return getEntityFromStack(stack.getTag(), world, withInfo);
    }

    @Nullable
    public static Bee getEntityFromStack(@Nullable CompoundTag tag, Level world, boolean withInfo) {
        if (tag != null) {
            EntityType<?> type = EntityType.byString(tag.getString("entity")).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (withInfo) {
                    entity.load(tag);
                }

                if (entity instanceof Bee) {
                    if (entity instanceof ConfigurableBee && !withInfo) {
                        ((ConfigurableBee) entity).setBeeType(tag.getString("type"));
                    }
                    return (Bee) entity;
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Component getName(ItemStack stack) {
        if (!isFilled(stack)) {
            return new TranslatableComponent(this.getDescriptionId());
        }

        String entityId = stack.getTag().getString("name");
        return new TranslatableComponent(this.getDescriptionId()).append(new TextComponent(" (" + entityId + ")"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && !tag.equals(new CompoundTag())) {
            if (Screen.hasShiftDown()) {
                boolean hasStung = tag.getBoolean("HasStung");
                if (hasStung) {
                    list.add(new TranslatableComponent("productivebees.information.health.dying").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
                }
                BeeHelper.populateBeeInfoFromTag(tag, list);

                if (tag.contains("HivePos")) {
                    list.add(new TranslatableComponent("productivebees.information.cage_release"));
                }
            } else {
                list.add(new TranslatableComponent("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
