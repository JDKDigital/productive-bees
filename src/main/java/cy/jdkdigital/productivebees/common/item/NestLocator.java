package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
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
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class NestLocator extends Item
{
    private static final String KEY = "productivebees_locator_nest";

    public NestLocator(Properties properties) {
        super(properties);
    }

    public static String getNestName(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("nest") ? nbt.getString("nest") : null;
    }

    public static Block getNestBlock(ItemStack stack) {
        String nestName = getNestName(stack);
        if (nestName != null) {
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nestName));
        }
        return null;
    }

    public static void setNestBlock(ItemStack stack, @Nullable Block nest) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        nbt.remove("nest");
        if (nest != null && nest.getRegistryName() != null) {
            nbt.putString("nest", nest.getRegistryName().toString());
        }

        stack.getOrCreateTag().put(KEY, nbt);
    }

    public static boolean hasNest(ItemStack stack) {
        return getNestName(stack) != null;
    }

    public static BlockPos getPosition(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        return nbt.contains("position") ? BlockPos.fromLong(nbt.getLong("position")) : null;
    }

    public static void setPosition(ItemStack stack, @Nullable BlockPos pos) {
        CompoundNBT nbt = stack.getOrCreateTag().getCompound(KEY);

        if (pos != null) {
            nbt.putLong("position", pos.toLong());
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
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote && context.getPlayer() != null && context.getPlayer().isSneaking()) {
            ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
            BlockState state = world.getBlockState(context.getPos());
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
                for (IRecipe<CraftingInventory> recipe: world.getRecipeManager().getRecipes(IRecipeType.CRAFTING).values()) {
                    out:
                    for (Ingredient s : recipe.getIngredients()) {
                        for (ItemStack ss : s.getMatchingStacks()) {
                            if (ss.getItem().equals(in.getItem())) {
                                ItemStack output = recipe.getRecipeOutput();
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
                };
            }
            return ActionResultType.SUCCESS;
        }
        return super.onItemUse(context);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        if (!world.isRemote) {
            // If it has a type specified
            ItemStack stack = player.getHeldItem(hand);
            if (!player.isSneaking()) {
                Predicate<Block> predicate = o -> o instanceof BeehiveBlock;
                if (hasNest(stack)) {
                    predicate = o -> o.equals(getNestBlock(stack));
                }

                Map.Entry<Double, BlockPos> nearest = findNearestNest(world, player.getPosition(), 16, predicate);

                if (nearest != null) {
                    // Show distance in chat
                    setPosition(stack, nearest.getValue());
                } else {
                    // Unset position
                    setPosition(stack, null);
                }
            }
            return ActionResult.resultSuccess(player.getHeldItem(hand));
        }

        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);

        if (hasNest(stack)) {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.configured", getNestName(stack)).mergeStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.unconfigured").mergeStyle(TextFormatting.GOLD));
        }
    }

    private Map.Entry<Double, BlockPos> findNearestNest(World world, BlockPos pos, int distance, Predicate<Block> predicate) {
        Vector3d playerPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        TreeMap<Double, BlockPos> nearbyNestPositions = new TreeMap<>();
        long start = System.currentTimeMillis();
        BlockPos.getAllInBox(pos.add(-distance, -distance, -distance), pos.add(distance, distance, distance)).forEach(blockPos -> {
            BlockState state = world.getBlockState(blockPos);
            if (predicate.test(state.getBlock())) {
                double distanceToNest = playerPos.distanceTo(new Vector3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                if (!nearbyNestPositions.containsKey(distanceToNest)) {
                    nearbyNestPositions.put(distanceToNest, new BlockPos(blockPos));
                }
            }
        });
        long finish = System.currentTimeMillis();
//        ProductiveBees.LOGGER.debug("Locator search time: " + (finish - start) + "ms");
        if (!nearbyNestPositions.isEmpty()) {
            return nearbyNestPositions.pollFirstEntry();
        }
        return null;
    }
}
