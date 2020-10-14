package cy.jdkdigital.productivebees.item;

import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

        this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            private double rotation;
            @OnlyIn(Dist.CLIENT)
            private double rota;
            @OnlyIn(Dist.CLIENT)
            private long lastUpdateTick;

            @OnlyIn(Dist.CLIENT)
            public float call(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity player) {
                if ((player != null || stack.isOnItemFrame()) && hasPosition(stack)) {
                    boolean flag = player != null;
                    Entity entity = flag ? player : stack.getItemFrame();
                    if (world == null) {
                        world = entity.world;
                    }

                    double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
                    d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                    double d2 = this.getPositionToAngle(getPosition(stack), entity) / (double)((float)Math.PI * 2F);
                    double d0 = 0.5D - (d1 - 0.25D - d2);

                    if (flag) {
                        d0 = this.wobble(world, d0);
                    }

                    return MathHelper.positiveModulo((float) d0, 1.0F);
                } else {
                    return 0.0F;
                }
            }

            @OnlyIn(Dist.CLIENT)
            private double wobble(World worldIn, double amount) {
                if (worldIn.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = amount - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            @OnlyIn(Dist.CLIENT)
            private double getFrameRotation(ItemFrameEntity frameEntity) {
                return MathHelper.wrapDegrees(180 + frameEntity.getHorizontalFacing().getHorizontalIndex() * 90);
            }

            @OnlyIn(Dist.CLIENT)
            private double getPositionToAngle(BlockPos blockpos, Entity entityIn) {
                return Math.atan2((double)blockpos.getZ() - entityIn.getPosZ(), (double)blockpos.getX() - entityIn.getPosX());
            }
        });
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

        if (nest != null && nest.getRegistryName() != null) {
            nbt.putString("nest", nest.getRegistryName().toString());
        } else {
            nbt.remove("nest");
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
            }
            else if (block instanceof SolitaryNest) {
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
            else {
                // Clear nest config
//                setNestBlock(stack, null);
            }
            return ActionResult.resultSuccess(player.getHeldItem(hand));
        }

        return ActionResult.resultPass(player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);

        if (hasNest(stack)) {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.configured", getNestName(stack)).applyTextStyle(TextFormatting.GOLD));
        } else {
            tooltip.add(new TranslationTextComponent("productivebees.information.nestlocator.unconfigured").applyTextStyle(TextFormatting.GOLD));
        }
    }

    private Map.Entry<Double, BlockPos> findNearestNest(World world, BlockPos pos, int distance, Predicate<Block> predicate) {
        Vec3d playerPos = new Vec3d(pos);
        TreeMap<Double, BlockPos> nearbyNestPositions = new TreeMap<>();
        BlockPos.getAllInBox(pos.add(-distance, -distance, -distance), pos.add(distance, distance, distance)).forEach(blockPos -> {
            BlockState state = world.getBlockState(blockPos);
            if (predicate.test(state.getBlock())) {
                double distanceToNest = playerPos.distanceTo(new Vec3d(blockPos));
                if (!nearbyNestPositions.containsKey(distanceToNest)) {
                    nearbyNestPositions.put(distanceToNest, new BlockPos(blockPos));
                }
            }
        });
        if (!nearbyNestPositions.isEmpty()) {
            return nearbyNestPositions.pollFirstEntry();
        }
        return null;
    }
}
