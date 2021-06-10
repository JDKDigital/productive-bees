package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTags;
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
import net.minecraft.item.BlockItemUseContext;
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
        this.registerDefaultState(this.defaultBlockState().setValue(HONEYLOGGED, false));
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HONEYLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);

        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidstate.getType().is(ModTags.HONEY) && state != null) {
            return state.setValue(HONEYLOGGED, true);
        }
        return state;
    }

    @Override
    public boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return state.getValue(TYPE) != SlabType.DOUBLE && (!state.getValue(BlockStateProperties.WATERLOGGED) && (fluidIn == Fluids.WATER || fluidIn.isSame(ModFluids.HONEY.get())));
    }

    @Override
    public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED)) {
            boolean isHoney = fluidState.getType().isSame(ModFluids.HONEY.get()) && fluidState.isSource();
            if (fluidState.getType() == Fluids.WATER || isHoney) {
                if (!world.isClientSide()) {
                    world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true).setValue(HONEYLOGGED, isHoney), 3);
                    world.getLiquidTicks().scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
                }
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(HONEYLOGGED) ? ModFluids.HONEY.get().getSource(false) : super.getFluidState(state);
    }

    @Nonnull
    @Override
    public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state) {
        if (state.getValue(HONEYLOGGED)) {
            world.setBlock(pos, state.setValue(HONEYLOGGED, false).setValue(BlockStateProperties.WATERLOGGED, false), 3);
            return ModFluids.HONEY.get();
        } else {
            return super.takeLiquid(world, pos, state);
        }
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        return tile instanceof INamedContainerProvider ? (INamedContainerProvider) tile : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof FeederTileEntity) {
                // Drop inventory
                tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!world.isClientSide()) {
            final TileEntity tileEntity = world.getBlockEntity(pos);

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
    public TileEntity newBlockEntity(IBlockReader world) {
        return new FeederTileEntity();
    }

    public void openGui(ServerPlayerEntity player, FeederTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}