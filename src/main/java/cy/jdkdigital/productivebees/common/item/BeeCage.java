package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import com.mojang.blaze3d.platform.InputConstants;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.fml.ModList;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public class BeeCage extends Item
{
    public BeeCage(Properties properties) {
        super(properties);
    }

    public static boolean isFilled(ItemStack itemStack) {
        var data = itemStack.get(DataComponents.CUSTOM_DATA);
        return !itemStack.isEmpty() && itemStack.getItem() instanceof BeeCage && data != null && data.copyTag().contains("entity");
    }

    public static String getBeeType(ItemStack itemStack) {
        var data = itemStack.get(DataComponents.CUSTOM_DATA);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof BeeCage && data != null && data.copyTag().contains("entity")) {
            var tag = data.copyTag();
            var type = tag.getString("entity").orElse("");
            if (type.equals("productivebees:configurable_bee")) {
                type = tag.getString("type").orElse("");
            }
            return type;
        }
        return null;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level playerWorld = context.getPlayer().level();
        ItemStack stack = context.getItemInHand();

        if (playerWorld.isClientSide() || !isFilled(stack)) {
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Bee entity = getEntityFromStack(stack, level, true);

        if (entity != null) {
            if (entity instanceof ProductiveBee pBeeFlower && pBeeFlower.isFlowerValid(pos)) {
                entity.setSavedFlowerPos(pos);
            } else if ((context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) || (entity.hivePos != null && !level.isLoaded(entity.hivePos))) {
                entity.hivePos = null;
                if (entity instanceof ProductiveBee pBee && level instanceof ServerLevel) {
                    pBee.setHasConverted(false);
                    pBee.setHasNectar(false);
                    PoiManager poiManager = ((ServerLevel) level).getPoiManager();
                    Optional<Holder<PoiType>> poiAtLocation = poiManager.getType(pos);
                    if (poiAtLocation.isPresent() && ((ProductiveBee) entity).getBeehiveInterests().test(poiAtLocation.get())) {
                        entity.hivePos = pos;
                    }
                }
            }

            BlockPos blockPos = pos.relative(context.getClickedFace());
            entity.setPos(blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D);

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
        if ((!(targetIn instanceof Bee target) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return InteractionResult.PASS;
        }

        if (targetIn.level().isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }

        boolean addToInventory = true;
        ItemStack cageStack = new ItemStack(itemStack.getItem());
        if (itemStack.getCount() == 1) {
            cageStack = itemStack;
            addToInventory = false;
        }

        if (target.isLeashed()) {
            target.dropLeash();
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
            ModAdvancements.CATCH_BEE.get().trigger((ServerPlayer) player, cageStack);
        }
        target.discard();

        return InteractionResult.SUCCESS;
    }

    public static void captureEntity(Bee target, ItemStack cageStack) {
        CompoundTag nbt;
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(target.problemPath(), LoggerFactory.getLogger(BeeCage.class))) {
            TagValueOutput out = TagValueOutput.createWithContext(reporter, target.registryAccess());
            target.saveWithoutId(out);
            nbt = out.buildResult();
        }
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getString());
        } else {
            nbt.putString("name", target.getName().getString());
        }

        AdvancedBeehiveBlockEntityAbstract.removeIgnoredTags(nbt);
        if (target.hasHive()) {
            nbt.put("HivePos", BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, target.getHivePos()).getOrThrow());
        }

        nbt.putBoolean("isProductiveBee", target instanceof ProductiveBee);

        String modId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()).getNamespace();
        String modName = ModList.get().getModContainerById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }
        nbt.putString("mod", modName);

        cageStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    @Nullable
    public static Bee getEntityFromStack(ItemStack stack, Level world, boolean withInfo) {
        return getEntityFromStack(stack.get(DataComponents.CUSTOM_DATA), world, withInfo);
    }

    @Nullable
    public static Bee getEntityFromStack(CustomData data, Level world, boolean withInfo) {
        if (data != null) {
            var tag = data.copyTag();
            EntityType<?> type = EntityType.byString(tag.getString("entity").orElse("")).orElse(null);
            if (type != null) {
                Entity entity = type.create(world, EntitySpawnReason.NATURAL);
                if (withInfo) {
                    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LoggerFactory.getLogger(BeeCage.class))) {
                        entity.load(TagValueInput.create(reporter, world.registryAccess(), tag));
                    }
                }

                if (entity instanceof Bee) {
                    if (entity instanceof ConfigurableBee && !withInfo) {
                        ((ConfigurableBee) entity).setBeeType(tag.getString("type").orElse(""));
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

        String entityId = stack.get(DataComponents.CUSTOM_DATA).copyTag().getString("name").orElse("");
        return Component.translatable(this.getDescriptionId()).append(Component.literal(" (" + entityId + ")"));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltipDisplay, pTooltipComponents, pTooltipFlag);

        var data = pStack.get(DataComponents.CUSTOM_DATA);
        if (data != null && !data.copyTag().equals(new CompoundTag())) {
            var tag = data.copyTag();
            var window = Minecraft.getInstance().getWindow();
            if (InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)) {
                boolean hasStung = tag.getBoolean("HasStung").orElse(false);
                if (hasStung) {
                    pTooltipComponents.accept(Component.translatable("productivebees.information.health.dying").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
                }
                BeeHelper.populateBeeInfoFromTag(tag, pTooltipComponents);

                if (tag.contains("HivePos")) {
                    pTooltipComponents.accept(Component.translatable("productivebees.information.cage_release"));
                }
            } else {
                pTooltipComponents.accept(Component.translatable("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
