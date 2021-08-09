package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
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

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HONEYLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidstate.getType().is(ModTags.HONEY) && state != null) {
            return state.setValue(HONEYLOGGED, true);
        }
        return state;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return state.getValue(TYPE) != SlabType.DOUBLE && (!state.getValue(BlockStateProperties.WATERLOGGED) && (fluidIn == Fluids.WATER || fluidIn.isSame(ModFluids.HONEY.get())));
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
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
    public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.getValue(HONEYLOGGED)) {
            world.setBlock(pos, state.setValue(HONEYLOGGED, false).setValue(BlockStateProperties.WATERLOGGED, false), 3);
            return new ItemStack(ModItems.HONEY_BUCKET.get());
        }
        return super.pickupBlock(world, pos, state);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof MenuProvider ? (MenuProvider) tile : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, @Nonnull Level worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof FeederBlockEntity) {
                // Drop inventory
                tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (!world.isClientSide()) {
            final BlockEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity instanceof FeederBlockEntity) {
                openGui((ServerPlayer) player, (FeederBlockEntity) tileEntity);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FeederBlockEntity(pos, state);
    }

    public void openGui(ServerPlayer player, FeederBlockEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}