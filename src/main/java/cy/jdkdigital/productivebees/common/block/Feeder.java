package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Feeder extends SlabBlock
{
    public static final BooleanProperty HONEYLOGGED = BooleanProperty.create("honeylogged");

    public Feeder(Properties properties) {
        super(properties);
        setDefaultState(this.getDefaultState().with(HONEYLOGGED, false));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HONEYLOGGED);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return state.get(TYPE) != SlabType.DOUBLE && (!state.get(BlockStateProperties.WATERLOGGED) && (fluidIn == Fluids.WATER || fluidIn.isEquivalentTo(ModFluids.HONEY.get())));
    }

    @Override
    public boolean receiveFluid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.get(BlockStateProperties.WATERLOGGED)) {
            boolean isHoney = fluidState.getFluid().isEquivalentTo(ModFluids.HONEY.get()) && fluidState.isSource();
            if (fluidState.getFluid() == Fluids.WATER || isHoney) {
                if (!world.isRemote()) {
                    world.setBlockState(pos, state.with(BlockStateProperties.WATERLOGGED, true).with(HONEYLOGGED, isHoney), 3);
                    world.getPendingFluidTicks().scheduleTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
                }
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(HONEYLOGGED) ? ModFluids.HONEY.get().getStillFluidState(false) : super.getFluidState(state);
    }

    @Nonnull
    @Override
    public Fluid pickupFluid(IWorld world, BlockPos pos, BlockState state) {
        if (state.get(HONEYLOGGED)) {
            world.setBlockState(pos, state.with(HONEYLOGGED, false).with(BlockStateProperties.WATERLOGGED, false), 3);
            return ModFluids.HONEY.get();
        }
        else {
            return super.pickupFluid(world, pos, state);
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState oldState, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof FeederTileEntity) {
                // Drop inventory
                tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
            }
        }
        super.onReplaced(oldState, worldIn, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            final TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof FeederTileEntity) {
                openGui((ServerPlayerEntity) player, (FeederTileEntity) tileEntity);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.FEEDER.get().create();
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new FeederTileEntity();
    }

    public void openGui(ServerPlayerEntity player, FeederTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getPos()));
    }
}