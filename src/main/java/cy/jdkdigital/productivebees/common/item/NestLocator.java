package cy.jdkdigital.productivebees.common.item;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestLocator extends Item
{
    public NestLocator(Properties properties) {
        super(properties);
    }

    public static String getNestName(ItemStack stack) {
        if (!hasNest(stack)) {
            return Blocks.BEE_NEST.getName().getString();
        }

        var nestBlock = getNestBlock(stack);
        return nestBlock != null ? nestBlock.getName().getString() : "";
    }

    public static ResourceLocation getNestRegistryName(ItemStack stack) {
        return stack.get(ModDataComponents.NEST_BLOCK);
    }

    public static Block getNestBlock(ItemStack stack) {
        ResourceLocation registryName = getNestRegistryName(stack);
        if (registryName != null) {
            return BuiltInRegistries.BLOCK.get(registryName);
        }
        return null;
    }

    public static void setNestBlock(ItemStack stack, @Nullable Block nest, Player player) {
        if (nest != null) {
            stack.set(ModDataComponents.NEST_BLOCK, BuiltInRegistries.BLOCK.getKey(nest));

            player.displayClientMessage(Component.translatable("productivebees.nest_locator.tuned", nest.getName().getString()), false);
        }
    }

    public static boolean hasNest(ItemStack stack) {
        return getNestRegistryName(stack) != null;
    }

    public static BlockPos getPosition(ItemStack stack) {
        return hasPosition(stack) ? stack.get(ModDataComponents.POSITION) : null;
    }

    public static void setPosition(ItemStack stack, @Nullable BlockPos pos) {
        if (pos != null) {
            stack.set(ModDataComponents.POSITION, pos);
        } else {
            stack.remove(ModDataComponents.POSITION);
        }
    }

    public static boolean hasPosition(ItemStack stack) {
        return stack.has(ModDataComponents.POSITION);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            BlockState state = world.getBlockState(context.getClickedPos());
            Block block = state.getBlock();

            // Special case for vanilla
            if (block instanceof BeehiveBlock || block instanceof AdvancedBeehive) {
                // Locate vanilla styled bee nests
                setNestBlock(stack, Blocks.BEE_NEST, context.getPlayer());
            } else if (block instanceof SolitaryNest) {
                setNestBlock(stack, block, context.getPlayer());
            } else if (BuiltInRegistries.BLOCK.getKey(block).getPath().contains("warped")) {
                setNestBlock(stack, ModBlocks.WARPED_BEE_NEST.get(), context.getPlayer());
            } else if (BuiltInRegistries.BLOCK.getKey(block).getPath().contains("crimson")) {
                setNestBlock(stack, ModBlocks.CRIMSON_BEE_NEST.get(), context.getPlayer());
            } else {
                // Set block if it's a component in crafting a nest
                Block nest = SolitaryNest.BLOCK_TO_NEST.get().get(block);
                if (nest instanceof SolitaryNest) {
                    setNestBlock(stack, nest, context.getPlayer());
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        if (!world.isClientSide && world instanceof ServerLevel) {
            // If it has a type specified
            ItemStack stack = player.getItemInHand(hand);
            if (!player.isShiftKeyDown()) {
                Predicate<Block> predicate = o -> o instanceof BeehiveBlock;
                if (hasNest(stack)) {
                    predicate = o -> o.equals(getNestBlock(stack));
                }

                Pair<Double, BlockPos> nearest = findNearestNest((ServerLevel) world, player.blockPosition(), ProductiveBeesConfig.GENERAL.nestLocatorDistance.get(), predicate);

                if (nearest != null) {
                    // Show distance in chat
                    player.displayClientMessage(Component.translatable("productivebees.nest_locator.found_hive", Math.round(nearest.getFirst() * 100.0) / 100.0), false);
                    setPosition(stack, nearest.getSecond());
                } else {
                    // Unset position
                    player.displayClientMessage(Component.translatable("productivebees.nest_locator.not_found_hive", getNestName(stack)), false);
                    setPosition(stack, null);
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        if (hasNest(pStack)) {
            pTooltipComponents.add(Component.translatable("productivebees.information.nestlocator.configured", getNestName(pStack)).withStyle(ChatFormatting.GOLD));
        } else {
            pTooltipComponents.add(Component.translatable("productivebees.information.nestlocator.unconfigured").withStyle(ChatFormatting.GOLD));
        }
    }

    private Pair<Double, BlockPos> findNearestNest(ServerLevel world, BlockPos pos, int distance, Predicate<Block> predicate) {
        Vec3 playerPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        PoiManager poiManager = world.getPoiManager();
        Stream<PoiRecord> stream = poiManager.getInSquare((poi) ->
                        poi.is(PoiTypeTags.BEE_HOME) ||
                        poi.value() == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
                        poi.value() == ModPointOfInterestTypes.SOLITARY_NEST.get() ||
                        poi.value() == ModPointOfInterestTypes.DRACONIC_NEST.get() ||
                        poi.value() == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get() ||
                        poi.value() == ModPointOfInterestTypes.NETHER_NEST.get() ||
                        poi.value() == ModPointOfInterestTypes.SUGARBAG_NEST.get(), pos, distance, PoiManager.Occupancy.ANY);

        List<BlockPos> nearbyNestPositions = stream.map(PoiRecord::getPos).filter((nestPos) -> {
            BlockState state = world.getBlockState(nestPos);
            return predicate.test(state.getBlock());
        }).sorted(Comparator.comparingDouble((vec) -> vec.distSqr(pos))).collect(Collectors.toList());

        if (!nearbyNestPositions.isEmpty()) {
            BlockPos nearestPos = nearbyNestPositions.iterator().next();
            double distanceToNest = playerPos.distanceTo(new Vec3(nearestPos.getX(), nearestPos.getY(), nearestPos.getZ()));
            return new Pair<>(distanceToNest, nearestPos);
        }
        return null;
    }
}
