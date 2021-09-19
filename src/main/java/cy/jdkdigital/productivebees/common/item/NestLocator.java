package cy.jdkdigital.productivebees.common.item;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestLocator extends Item
{
    private static final String KEY = "productivebees_locator_nest";

    public NestLocator(Properties properties) {
        super(properties);
    }

    public static String getNestName(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("nestName") ? nbt.getString("nestName") : null;
    }

    public static String getNestRegistryName(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("nest") ? nbt.getString("nest") : null;
    }

    public static Block getNestBlock(ItemStack stack) {
        String registryName = getNestRegistryName(stack);
        if (registryName != null) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
        }
        return null;
    }

    public static void setNestBlock(ItemStack stack, @Nullable Block nest, Player player) {
        CompoundTag nbt = stack.getOrCreateTag().getCompound(KEY);

        nbt.remove("nest");
        if (nest != null && nest.getRegistryName() != null) {
            nbt.putString("nest", nest.getRegistryName().toString());
            nbt.putString("nestName", new TranslatableComponent(nest.getDescriptionId()).getString());

            player.displayClientMessage(new TranslatableComponent("productivebees.nest_locator.tuned", nbt.getString("nestName")), false);
        }

        stack.getOrCreateTag().put(KEY, nbt);
    }

    public static boolean hasNest(ItemStack stack) {
        return getNestRegistryName(stack) != null;
    }

    public static BlockPos getPosition(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("position") ? BlockPos.of(nbt.getLong("position")) : null;
    }

    public static void setPosition(ItemStack stack, @Nullable BlockPos pos) {
        CompoundTag nbt = stack.getOrCreateTag().getCompound(KEY);

        if (pos != null) {
            nbt.putLong("position", pos.asLong());
        } else {
            nbt.remove("position");
        }
        stack.getOrCreateTag().put(KEY, nbt);
    }

    public static boolean hasPosition(ItemStack stack) {
        return getPosition(stack) != null;
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
                setNestBlock(stack, ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", "bee_nest")), context.getPlayer());
            } else if (block instanceof SolitaryNest) {
                setNestBlock(stack, block, context.getPlayer());
            } else {
                // Set block if it's a component in crafting a nest
                ItemStack in = new ItemStack(block.asItem());
                done:
                for (Recipe<CraftingContainer> recipe : world.getRecipeManager().byType(RecipeType.CRAFTING).values()) {
                    out:
                    for (Ingredient s : recipe.getIngredients()) {
                        for (ItemStack ss : s.getItems()) {
                            if (ss.getItem().equals(in.getItem())) {
                                ItemStack output = recipe.getResultItem();
                                if (output.getItem() instanceof BlockItem) {
                                    Block foundBlock = ForgeRegistries.BLOCKS.getValue(output.getItem().getRegistryName());
                                    if (foundBlock instanceof SolitaryNest) {
                                        setNestBlock(stack, foundBlock, context.getPlayer());
                                        break done;
                                    }
                                }
                                break out;
                            }
                        }
                    }
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
                    player.displayClientMessage(new TranslatableComponent("productivebees.nest_locator.found_hive", Math.round(nearest.getFirst() * 100.0) / 100.0), false);
                    setPosition(stack, nearest.getSecond());
                } else {
                    // Unset position
                    player.displayClientMessage(new TranslatableComponent("productivebees.nest_locator.not_found_hive", getNestName(stack)), false);
                    setPosition(stack, null);
                }
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);

        if (hasNest(stack)) {
            tooltip.add(new TranslatableComponent("productivebees.information.nestlocator.configured", getNestName(stack)).withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(new TranslatableComponent("productivebees.information.nestlocator.unconfigured").withStyle(ChatFormatting.GOLD));
        }
    }

    private Pair<Double, BlockPos> findNearestNest(ServerLevel world, BlockPos pos, int distance, Predicate<Block> predicate) {
        Vec3 playerPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        PoiManager poiManager = world.getPoiManager();
        Stream<PoiRecord> stream = poiManager.getInSquare((poiType) ->
                poiType == PoiType.BEEHIVE ||
                        poiType == PoiType.BEE_NEST ||
                        poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
                        poiType == ModPointOfInterestTypes.SOLITARY_NEST.get() ||
                        poiType == ModPointOfInterestTypes.DRACONIC_NEST.get() ||
                        poiType == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get() ||
                        poiType == ModPointOfInterestTypes.SUGARBAG_NEST.get(), pos, distance, PoiManager.Occupancy.ANY);

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
