package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class AdvancedBeehiveAbstract extends ContainerBlock
{
    public AdvancedBeehiveAbstract(Properties properties) {
        super(properties);
    }

    public int getMaxHoneyLevel() {
        return 5;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        CompoundNBT entityNBT = stack.getChildTag("BlockEntityTag");
        CompoundNBT stateNBT = stack.getChildTag("BlockStateTag");
        if (stateNBT != null) {
            if (stateNBT.contains("honey_level")) {
                String honeyLevel = stateNBT.getString("honey_level");
                tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.honey_level", honeyLevel).mergeStyle(TextFormatting.GOLD)); // mergeStyle
            }
        }
        if (entityNBT != null) {
            if (entityNBT.contains("Bees")) {
                ListNBT beeList = entityNBT.getCompound("Bees").getList("Inhabitants", Constants.NBT.TAG_COMPOUND);
                if (beeList.size() > 0) {
                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.bees").mergeStyle(TextFormatting.BOLD));
                    for (int i = 0; i < beeList.size(); ++i) {
                        CompoundNBT tag = beeList.getCompound(i);
                        String name = tag.getString("Name");

                        tooltip.add(new StringTextComponent(name).mergeStyle(TextFormatting.GREEN));
                    }
                }
                else {
                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.empty"));
                }
            }
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, getMaxHoneyLevel() - 5), 3);
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL) && state.get(BeehiveBlock.HONEY_LEVEL) >= getMaxHoneyLevel()) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.dripHoney(world, pos, state);
            }
        }
    }

    private void dripHoney(World world, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && world.rand.nextFloat() >= 0.3F) {
            VoxelShape shape = state.getCollisionShape(world, pos);
            double shapeEnd = shape.getEnd(Direction.Axis.Y);
            if (shapeEnd >= 1.0D && !state.getBlock().isIn(BlockTags.IMPERMEABLE)) {
                double shapeStart = shape.getStart(Direction.Axis.Y);
                if (shapeStart > 0.0D) {
                    this.addHoneyParticle(world, pos, shape, (double) pos.getY() + shapeStart - 0.05D);
                }
                else {
                    BlockPos posDown = pos.down();
                    BlockState stateDown = world.getBlockState(posDown);
                    VoxelShape shapeDown = stateDown.getCollisionShape(world, posDown);
                    double shapeDownEnd = shapeDown.getEnd(Direction.Axis.Y);
                    if ((shapeDownEnd < 1.0D || !stateDown.hasOpaqueCollisionShape(world, posDown)) && stateDown.getFluidState().isEmpty()) {
                        this.addHoneyParticle(world, pos, shape, (double) pos.getY() - 0.05D);
                    }
                }
            }

        }
    }

    private void addHoneyParticle(World world, BlockPos pos, VoxelShape shape, double p_226880_4_) {
        this.addHoneyParticle(world, (double) pos.getX() + shape.getStart(Direction.Axis.X), (double) pos.getX() + shape.getEnd(Direction.Axis.X), (double) pos.getZ() + shape.getStart(Direction.Axis.Z), (double) pos.getZ() + shape.getEnd(Direction.Axis.Z), p_226880_4_);
    }

    private void addHoneyParticle(World world, double d1, double d2, double d3, double d4, double d5) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.rand.nextDouble(), d1, d2), d5, MathHelper.lerp(world.rand.nextDouble(), d3, d4), 0.0D, 0.0D, 0.0D);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.getItem().equals(Items.STICK)) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                ((AdvancedBeehiveTileEntityAbstract) tileEntity).angerBees(player, state, BeehiveTileEntity.State.BEE_RELEASED);
            }
        }
        super.onBlockClicked(state, worldIn, pos, player);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isRemote && player.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                int honeyLevel = 0;
                if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                    honeyLevel = state.get(BeehiveBlock.HONEY_LEVEL);
                }
                boolean hasBees = !beehiveTileEntity.hasNoBees();
                if (!hasBees && honeyLevel == 0) {
                    return;
                }

                ItemStack itemStack = new ItemStack(this);
                CompoundNBT compoundNBT = new CompoundNBT();
                if (hasBees) {
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.put("Inhabitants", beehiveTileEntity.getBeeListAsNBTList());
                    compoundNBT.put("Bees", nbt);
                }

                compoundNBT.putInt("honey_level", honeyLevel);
                itemStack.setTagInfo("BlockStateTag", compoundNBT);
                ItemEntity hiveEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                hiveEntity.setDefaultPickupDelay();
                world.addEntity(hiveEntity);
            }
        }
        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.get(LootParameters.THIS_ENTITY);
        if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity) {
            TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                beehiveTileEntity.angerBees(null, state, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos fireBlockPos) {
        if (world.getBlockState(fireBlockPos).getBlock() instanceof FireBlock) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                beehiveTileEntity.angerBees(null, state, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.updatePostPlacement(state, direction, state1, world, pos, fireBlockPos);
    }
}
