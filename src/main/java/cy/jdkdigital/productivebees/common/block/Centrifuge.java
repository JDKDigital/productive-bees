package cy.jdkdigital.productivebees.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivelib.common.block.CapabilityContainerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Centrifuge extends CapabilityContainerBlock
{
    public static final MapCodec<Catcher> CODEC = simpleCodec(Catcher::new);
    public static final BooleanProperty RUNNING = BooleanProperty.create("running");

    private static final VoxelShape INSIDE = box(1.0D, 8.1D, 1.0D, 15.0D, 16.0D, 15.0D);
    protected static final VoxelShape SHAPE = Shapes.join(
            Shapes.block(),
            Shapes.or(
                    box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D),
                    box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D),
                    box(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D),
                    INSIDE
            ), BooleanOp.ONLY_FIRST);

    private static final VoxelShape BLOCK_ABOVE_SHAPE = box(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
    public static VoxelShape COLLECTION_AREA_SHAPE = Shapes.or(INSIDE, BLOCK_ABOVE_SHAPE);

    public Centrifuge(Block.Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(RUNNING, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.CENTRIFUGE.get(), CentrifugeBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return INSIDE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide() && FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pPos, null)) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof CentrifugeBlockEntity centrifugeBlockEntity) {
            pPlayer.openMenu(centrifugeBlockEntity, pPos);
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHitResult);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUNNING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CentrifugeBlockEntity(pos, state);
    }

    private void openGui(ServerPlayer player, CentrifugeBlockEntity tileEntity) {
        player.openMenu(tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}