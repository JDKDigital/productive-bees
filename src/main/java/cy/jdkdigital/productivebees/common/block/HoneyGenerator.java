package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.HoneyGeneratorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class HoneyGenerator extends CapabilityContainerBlock
{
    public static final BooleanProperty ON = BooleanProperty.create("on");
    public static final BooleanProperty FULL = BooleanProperty.create("full");

    protected static final VoxelShape SHAPE = VoxelShapes.join(
            VoxelShapes.block(),
            VoxelShapes.or(
                    box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D),
                    box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D),
                    box(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D)
            ), IBooleanFunction.ONLY_FIRST);

    public HoneyGenerator(Properties builder) {
        super(builder);
        this.registerDefaultState(this.defaultBlockState().setValue(ON, Boolean.FALSE).setValue(FULL, Boolean.FALSE).setValue(HorizontalBlock.FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(ON, FULL, HorizontalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.defaultBlockState().setValue(HorizontalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HorizontalBlock.FACING, rot.rotate(state.getValue(HorizontalBlock.FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(HorizontalBlock.FACING)));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HoneyGeneratorTileEntity();
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.getValue(ON)) {
            double d0 = (double) pos.getX() + 0.5D;
            double d1 = pos.getY();
            double d2 = (double) pos.getZ() + 0.5D;
            if (rand.nextDouble() < 0.1D) {
                worldIn.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = stateIn.getValue(HorizontalBlock.FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * d3 : d4;
            double d6 = rand.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * d3 : d4;
            worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);
            boolean itemUsed = false;

            if (heldItem.getItem() instanceof BucketItem) {
                if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, null)) {
                    itemUsed = true;
                }
            }

            if (!itemUsed) {
                final TileEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof HoneyGeneratorTileEntity) {
                    openGui((ServerPlayerEntity) player, (HoneyGeneratorTileEntity) tileEntity);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onPlace(BlockState state, World world, BlockPos pos, BlockState newState, boolean something) {
        TileEntity generatorTile = world.getBlockEntity(pos);
        if (generatorTile instanceof HoneyGeneratorTileEntity) {
            ((HoneyGeneratorTileEntity) generatorTile).refreshConnectedTileEntityCache();
        }
        super.onPlace(state, world, pos, newState, something);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState stae, IWorld world, BlockPos pos, BlockPos facingPos) {
        TileEntity generatorTile = world.getBlockEntity(pos);
        if (generatorTile instanceof HoneyGeneratorTileEntity) {
            ((HoneyGeneratorTileEntity) generatorTile).refreshConnectedTileEntityCache();
        }
        return super.updateShape(state, direction, stae, world, pos, facingPos);
    }

    public void openGui(ServerPlayerEntity player, HoneyGeneratorTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}
