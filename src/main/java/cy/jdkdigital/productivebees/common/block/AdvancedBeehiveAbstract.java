package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
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
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        return te != null ? te.getCapability(CapabilityBee.BEE).map(b -> b.getInhabitants().size()).orElse(0) : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundNBT entityNBT = stack.getTagElement("BlockEntityTag");
        CompoundNBT stateNBT = stack.getTagElement("BlockStateTag");
        if (stateNBT != null) {
            if (stateNBT.contains("honey_level")) {
                String honeyLevel = stateNBT.getString("honey_level");
                tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.honey_level", honeyLevel).withStyle(TextFormatting.GOLD)); // mergeStyle
            }
        }
        if (entityNBT != null) {
            if (entityNBT.contains("Bees")) {
                ListNBT beeList = entityNBT.getCompound("Bees").getList("Inhabitants", Constants.NBT.TAG_COMPOUND);
                if (beeList.size() > 0) {
                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.bees").withStyle(TextFormatting.BOLD));
                    for (int i = 0; i < beeList.size(); ++i) {
                        CompoundNBT tag = beeList.getCompound(i);
                        String name = tag.getString("Name");

                        tooltip.add(new StringTextComponent(name).withStyle(TextFormatting.GREEN));
                    }
                } else {
                    tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.empty"));
                }
            }
        }
    }

    public void takeHoney(World world, BlockState state, BlockPos pos) {
        world.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, getMaxHoneyLevel() - 5));
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL) && state.getValue(BeehiveBlock.HONEY_LEVEL) >= getMaxHoneyLevel()) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.dripHoney(world, pos, state);
            }
        }
    }

    private void dripHoney(World world, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && world.random.nextFloat() >= 0.3F) {
            VoxelShape shape = state.getCollisionShape(world, pos);
            double shapeEnd = shape.max(Direction.Axis.Y);
            if (shapeEnd >= 1.0D && !state.getBlock().is(BlockTags.IMPERMEABLE)) {
                double shapeStart = shape.min(Direction.Axis.Y);
                if (shapeStart > 0.0D) {
                    this.spawnParticle(world, pos, shape, (double) pos.getY() + shapeStart - 0.05D);
                } else {
                    BlockPos posDown = pos.below();
                    BlockState stateDown = world.getBlockState(posDown);
                    VoxelShape shapeDown = stateDown.getCollisionShape(world, posDown);
                    double shapeDownEnd = shapeDown.max(Direction.Axis.Y);
                    if ((shapeDownEnd < 1.0D || !stateDown.isCollisionShapeFullBlock(world, posDown)) && stateDown.getFluidState().isEmpty()) {
                        this.spawnParticle(world, pos, shape, (double) pos.getY() - 0.05D);
                    }
                }
            }

        }
    }

    private void spawnParticle(World world, BlockPos pos, VoxelShape shape, double p_226880_4_) {
        this.spawnFluidParticle(world, (double) pos.getX() + shape.min(Direction.Axis.X), (double) pos.getX() + shape.max(Direction.Axis.X), (double) pos.getZ() + shape.min(Direction.Axis.Z), (double) pos.getZ() + shape.max(Direction.Axis.Z), p_226880_4_);
    }

    private void spawnFluidParticle(World world, double d1, double d2, double d3, double d4, double d5) {
        world.addParticle(ParticleTypes.DRIPPING_HONEY, MathHelper.lerp(world.random.nextDouble(), d1, d2), d5, MathHelper.lerp(world.random.nextDouble(), d3, d4), 0.0D, 0.0D, 0.0D);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void attack(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem().equals(Items.STICK)) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                ((AdvancedBeehiveTileEntityAbstract) tileEntity).emptyAllLivingFromHive(player, state, BeehiveTileEntity.State.BEE_RELEASED);
            }
        }
        super.attack(state, world, pos, player);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClientSide && player.isCreative() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                int honeyLevel = 0;
                if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                    honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);
                }
                boolean hasBees = !beehiveTileEntity.isEmpty();
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
                itemStack.addTagElement("BlockStateTag", compoundNBT);
                ItemEntity hiveEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                hiveEntity.setDefaultPickUpDelay();
                world.addFreshEntity(hiveEntity);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.getOptionalParameter(LootParameters.THIS_ENTITY);
        if (entity instanceof TNTEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TNTMinecartEntity) {
            TileEntity tileEntity = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                beehiveTileEntity.emptyAllLivingFromHive(null, state, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, IWorld world, BlockPos pos, BlockPos fireBlockPos) {
        if (world.getBlockState(fireBlockPos).getBlock() instanceof FireBlock) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveTileEntityAbstract) {
                AdvancedBeehiveTileEntityAbstract beehiveTileEntity = (AdvancedBeehiveTileEntityAbstract) tileEntity;
                beehiveTileEntity.emptyAllLivingFromHive(null, state, BeehiveTileEntity.State.EMERGENCY);
            }
        }

        return super.updateShape(state, direction, state1, world, pos, fireBlockPos);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(null, level);
    }
}
