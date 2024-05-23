package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Feeder extends SlabBlock implements EntityBlock
{
    public static final BooleanProperty HONEYLOGGED = BooleanProperty.create("honeylogged");

    public Feeder(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HONEYLOGGED, false));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : FeederBlockEntity::tick;
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
        if (fluidstate.is(ModTags.HONEY) && fluidstate.isSource() && state != null) {
            return state.setValue(HONEYLOGGED, true);
        }
        return state;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluidIn) {
        return state.getValue(TYPE) != SlabType.DOUBLE && (!state.getValue(BlockStateProperties.WATERLOGGED) && (fluidIn == Fluids.WATER || fluidIn.isSame(ModFluids.HONEY.get())));
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(BlockStateProperties.WATERLOGGED)) {
            boolean isHoney = fluidState.getType().isSame(ModFluids.HONEY.get()) && fluidState.isSource();
            if (fluidState.getType() == Fluids.WATER || isHoney) {
                if (!level.isClientSide()) {
                    if (level.getBlockEntity(pos) instanceof FeederBlockEntity feederBlockEntity) {
                        var nbt = new CompoundTag();
                        feederBlockEntity.savePacketNBT(nbt);
                        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true).setValue(HONEYLOGGED, isHoney), 3);
                        level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
                        feederBlockEntity.loadPacketNBT(nbt);
                    }
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

    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        // Refresh inventory handler
        if (level.getBlockEntity(pos) instanceof FeederBlockEntity feederBlockEntity) {
            var nbt = new CompoundTag();
            feederBlockEntity.savePacketNBT(nbt, level.registryAccess());
            feederBlockEntity.refreshInventoryHandler();
            feederBlockEntity.loadPacketNBT(nbt, level.registryAccess());
        }
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if (pState.getValue(HONEYLOGGED)) {
            pLevel.setBlock(pPos, pState.setValue(HONEYLOGGED, false).setValue(BlockStateProperties.WATERLOGGED, false), 3);
            return new ItemStack(ModItems.HONEY_BUCKET.get());
        }
        return super.pickupBlock(pPlayer, pLevel, pPos, pState);
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
            if (tileEntity instanceof FeederBlockEntity feederBlockEntity) {
                // Drop inventory
                for (int slot = 0; slot < feederBlockEntity.inventoryHandler.getSlots(); ++slot) {
                    Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), feederBlockEntity.inventoryHandler.getStackInSlot(slot));
                }
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        if (heldItem.getItem() instanceof BlockItem) {
            Block heldBlock = ((BlockItem) heldItem.getItem()).getBlock();
            if (heldBlock instanceof SlabBlock && !(heldBlock instanceof Feeder)) {
                final BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if (blockEntity instanceof FeederBlockEntity) {
                    ((FeederBlockEntity) blockEntity).baseBlock = heldBlock;
                    blockEntity.setChanged();
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            final BlockEntity tileEntity = pLevel.getBlockEntity(pos);

            if (tileEntity instanceof FeederBlockEntity) {
                openGui((ServerPlayer) pPlayer, (FeederBlockEntity) tileEntity);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FeederBlockEntity(pos, state);
    }

    public void openGui(ServerPlayer player, FeederBlockEntity tileEntity) {
        player.openMenu(tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}