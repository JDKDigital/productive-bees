package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
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
import java.util.Optional;

public class BeeCage extends Item
{
    public BeeCage(Properties properties) {
        super(properties);
    }

    public static boolean isFilled(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return !itemStack.isEmpty() && itemStack.getItem() instanceof BeeCage && tag != null && tag.contains("entity");
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level playerWorld = context.getPlayer().getCommandSenderWorld();
        ItemStack stack = context.getItemInHand();

        if (playerWorld.isClientSide() || !isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Bee entity = getEntityFromStack(stack, level, true);

        if (entity != null) {
            if (entity.isFlowerValid(pos)) {
                entity.setSavedFlowerPos(pos);
            } else if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || (entity.hivePos != null && !level.isLoaded(entity.hivePos))) {
                entity.hivePos = null;
                if (entity instanceof ProductiveBee && level instanceof ServerLevel) {
                    PoiManager poiManager = ((ServerLevel) level).getPoiManager();
                    Optional<Holder<PoiType>> poiAtLocation = poiManager.getType(pos);
                    if (poiAtLocation.isPresent() && ((ProductiveBee) entity).getBeehiveInterests().test(poiAtLocation.get())) {
                        entity.hivePos = pos;
                    }
                }
            }

            BlockPos blockPos = pos.relative(context.getClickedFace());
            entity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            level.addFreshEntity(entity);

            postItemUse(context);
        }

        return InteractionResult.SUCCESS;
    }

    protected void postItemUse(UseOnContext context) {
        // Delete stack
        if (context.getPlayer() != null) {
            if (context.getPlayer().isCreative()) {
                context.getPlayer().getInventory().removeItem(context.getItemInHand());
            } else {
                context.getItemInHand().shrink(1);
            }
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity targetIn, InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || (!(targetIn instanceof Bee target) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return InteractionResult.PASS;
        }

        boolean addToInventory = true;
        ItemStack cageStack = new ItemStack(itemStack.getItem());
        if (itemStack.getCount() == 1) {
            cageStack = itemStack;
            addToInventory = false;
        }

        if (target.isLeashed()) {
            target.dropLeash(true, true);
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

        AdvancedBeehiveBlockEntityAbstract.removeIgnoredBeeTags(nbt);
        if (target.hasHive()) {
            nbt.put("HivePos", NbtUtils.writeBlockPos(target.getHivePos()));
        }

        nbt.putBoolean("isProductiveBee", target instanceof ProductiveBee);

        String modId = Registry.ENTITY_TYPE.getKey(target.getType()).getNamespace();
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
            return Component.translatable(this.getDescriptionId());
        }

        String entityId = stack.getTag().getString("name");
        return Component.translatable(this.getDescriptionId()).append(Component.literal(" (" + entityId + ")"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null && !tag.equals(new CompoundTag())) {
            if (Screen.hasShiftDown()) {
                boolean hasStung = tag.getBoolean("HasStung");
                if (hasStung) {
                    list.add(Component.translatable("productivebees.information.health.dying").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
                }
                BeeHelper.populateBeeInfoFromTag(tag, list);

                if (tag.contains("HivePos")) {
                    list.add(Component.translatable("productivebees.information.cage_release"));
                }
            } else {
                list.add(Component.translatable("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
