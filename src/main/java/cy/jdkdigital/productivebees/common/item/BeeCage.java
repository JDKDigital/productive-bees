package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
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
        CompoundNBT tag = itemStack.getTag();
        return !itemStack.isEmpty() && tag != null && tag.contains("entity");
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World playerWorld = context.getPlayer().getCommandSenderWorld();
        ItemStack stack = context.getItemInHand();

        if (playerWorld.isClientSide() || !isFilled(stack)) {
            return ActionResultType.FAIL;
        }

        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BeeEntity entity = getEntityFromStack(stack, worldIn, true);

        if (entity != null) {
            if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
                entity.hivePos = null;
            }

            BlockPos blockPos = pos.relative(context.getClickedFace());
            entity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);
            worldIn.addFreshEntity(entity);

            postItemUse(context);
        }

        return ActionResultType.SUCCESS;
    }

    protected void postItemUse(ItemUseContext context) {
        // Delete stack
        if (context.getPlayer() != null) {
            context.getPlayer().inventory.removeItem(context.getItemInHand());
        }
    }

    @Nonnull
    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return ActionResultType.PASS;
        }

        BeeEntity target = (BeeEntity) targetIn;

        boolean addToInventory = true;
        ItemStack cageStack = new ItemStack(itemStack.getItem());
        if (itemStack.getCount() == 1) {
            cageStack = itemStack;
            addToInventory = false;
        }

        captureEntity(target, cageStack);

        if (addToInventory || player.isCreative()) {
            if (!player.inventory.add(cageStack)) {
                player.drop(cageStack, false);
            }
            itemStack.shrink(1);
        }

        player.swing(hand);

        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.CATCH_BEE.trigger((ServerPlayerEntity) player, cageStack);
        }
        target.remove(true);

        return ActionResultType.SUCCESS;
    }

    public static void captureEntity(BeeEntity target, ItemStack cageStack) {
        CompoundNBT nbt = new CompoundNBT();
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

        nbt.putBoolean("isProductiveBee", target instanceof ProductiveBeeEntity);

        String modId = target.getType().getRegistryName().getNamespace();
        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }
        nbt.putString("mod", modName);

        cageStack.setTag(nbt);
    }

    @Nullable
    public static BeeEntity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        return getEntityFromStack(stack.getTag(), world, withInfo);
    }

    @Nullable
    public static BeeEntity getEntityFromStack(@Nullable CompoundNBT tag, World world, boolean withInfo) {
        if (tag != null) {
            EntityType<?> type = EntityType.byString(tag.getString("entity")).orElse(null);
            if (type != null) {
                Entity entity = type.create(world);
                if (withInfo) {
                    entity.load(tag);
                }

                if (entity instanceof BeeEntity) {
                    if (entity instanceof ConfigurableBeeEntity && !withInfo) {
                        ((ConfigurableBeeEntity) entity).setBeeType(tag.getString("type"));
                    }
                    return (BeeEntity) entity;
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public ITextComponent getName(ItemStack stack) {
        if (!isFilled(stack)) {
            return new TranslationTextComponent(this.getDescriptionId());
        }

        String entityId = stack.getTag().getString("name");
        return new TranslationTextComponent(this.getDescriptionId()).append(new StringTextComponent(" (" + entityId + ")"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundNBT tag = stack.getTag();
        if (tag != null && !tag.equals(new CompoundNBT())) {
            if (Screen.hasShiftDown()) {
                boolean hasStung = tag.getBoolean("HasStung");
                if (hasStung) {
                    list.add(new TranslationTextComponent("productivebees.information.health.dying").withStyle(TextFormatting.RED).withStyle(TextFormatting.ITALIC));
                }
                BeeHelper.populateBeeInfoFromTag(tag, list);

                if (tag.contains("HivePos")) {
                    list.add(new TranslationTextComponent("productivebees.information.cage_release"));
                }
            } else {
                list.add(new TranslationTextComponent("productivebees.information.hold_shift").withStyle(TextFormatting.WHITE));
            }
        }
    }
}
