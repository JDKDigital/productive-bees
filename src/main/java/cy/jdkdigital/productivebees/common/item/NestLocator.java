package cy.jdkdigital.productivebees.common.item;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("nestName") ? nbt.getString("nestName") : null;
    }

    public static String getNestRegistryName(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("nest") ? nbt.getString("nest") : null;
    }

    public static Block getNestBlock(ItemStack stack) {
        String registryName = getNestRegistryName(stack);
        if (registryName != null) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(registryName));
        }
        return null;
    }

    public static void setNestBlock(ItemStack stack, @Nullable Block nest) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        nbt.remove("nest");
        if (nest != null && nest.getRegistryName() != null) {
            nbt.putString("nest", nest.getRegistryName().toString());
            nbt.putString("nestName", nest.getName().getString());
        }

        stack.getOrCreateTag().put(KEY, nbt);
    }

    public static boolean hasNest(ItemStack stack) {
        return getNestRegistryName(stack) != null;
    }

    public static BlockPos getPosition(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("position") ? BlockPos.of(nbt.getLong("position")) : null;
    }

    public static void setPosition(ItemStack stack, @Nullable BlockPos pos) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
            BlockState state = world.getBlockState(context.getClickedPos());
            Block block = state.getBlock();

            // Special case for vanilla
            if (block instanceof BeehiveBlock || block instanceof AdvancedBeehive) {
                // Locate vanilla styled bee nests
                setNestBlock(stack, ForgeRegistries.BLOCKS.getValue(new ResourceLocation("minecraft", "bee_nest")));
            } else if (block instanceof SolitaryNest) {
                setNestBlock(stack, block);
            } else {
                // Set block if it's a component in crafting a nest
                ItemStack in = new ItemStack(block.asItem());
                done:
                for (IRecipe<CraftingInventory> recipe : world.getRecipeManager().byType(IRecipeType.CRAFTING).values()) {
                    out:
                    for (Ingredient s : recipe.getIngredients()) {
                        for (ItemStack ss : s.getItems()) {
                            if (ss.getItem().equals(in.getItem())) {
                                ItemStack output = recipe.getResultItem();
                                if (output.getItem() instanceof BlockItem) {
                                    Block foundBlock = ForgeRegistries.BLOCKS.getValue(output.getItem().getRegistryName());
                                    if (foundBlock instanceof SolitaryNest) {
                                        setNestBlock(stack, foundBlock);
                                        break done;
                                    }
                                }
                                break out;
                            }
                        }
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isClientSide && world instanceof ServerWorld) {
            // If it has a type specified
            ItemStack stack = player.getItemInHand(hand);
            if (!player.isShiftKeyDown()) {
                Predicate<Block> predicate = o -> o instanceof BeehiveBlock;
                if (hasNest(stack)) {
                    predicate = o -> o.equals(getNestBlock(stack));
                }

                Pair<Double, BlockPos> nearest = findNearestNest((ServerWorld) world, player.blockPosition(), ProductiveBeesConfig.GENERAL.nestLocatorDistance.get(), predicate);

                if (nearest != null) {
                    // Show distance in chat
                    player.displayClientMessage(new TranslationTextComponent("productivebees.nest_locator.found_hive", Math.round(nearest.getFirst() * 100.0) / 100.0), false);
                    setPosition(stack, nearest.getSecond());
                } else {
                    // Unset position
                    player.displayClientMessage(new TranslationTextComponent("productivebees.nest_locator.not_found_hive"), false);
                    setPosition(stack, null);
                }
            }
            return ActionResult.success(player.getItemInHand(hand));
        }

        return ActionResult.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);

        if (hasNest(stack)) {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.configured", getNestName(stack)).withStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.unconfigured").withStyle(TextFormatting.GOLD));
        }
    }

    private Pair<Double, BlockPos> findNearestNest(ServerWorld world, BlockPos pos, int distance, Predicate<Block> predicate) {
        Vector3d playerPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());

        PointOfInterestManager pointofinterestmanager = world.getPoiManager();
        Stream<PointOfInterest> stream = pointofinterestmanager.getInSquare((poiType) ->
                poiType == PointOfInterestType.BEEHIVE ||
                        poiType == PointOfInterestType.BEE_NEST ||
                        poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
                        poiType == ModPointOfInterestTypes.SOLITARY_NEST.get() ||
                        poiType == ModPointOfInterestTypes.DRACONIC_NEST.get() ||
                        poiType == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get() ||
                        poiType == ModPointOfInterestTypes.SUGARBAG_NEST.get(), pos, distance, PointOfInterestManager.Status.ANY);

        List<BlockPos> nearbyNestPositions = stream.map(PointOfInterest::getPos).filter((nestPos) -> {
            BlockState state = world.getBlockState(nestPos);
            return predicate.test(state.getBlock());
        }).sorted(Comparator.comparingDouble((vec) -> vec.distSqr(pos))).collect(Collectors.toList());

        if (!nearbyNestPositions.isEmpty()) {
            BlockPos nearestPos = nearbyNestPositions.iterator().next();
            double distanceToNest = playerPos.distanceTo(new Vector3d(nearestPos.getX(), nearestPos.getY(), nearestPos.getZ()));
            return new Pair<>(distanceToNest, nearestPos);
        }
        return null;
    }
}
