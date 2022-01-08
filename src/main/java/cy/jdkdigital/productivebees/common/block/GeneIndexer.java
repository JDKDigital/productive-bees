package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.GeneIndexerBlockEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GeneIndexer extends CapabilityContainerBlock
{
    public GeneIndexer(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.ENABLED, true).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ENABLED, HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModTileEntityTypes.GENE_INDEXER.get(), GeneIndexerBlockEntity::tick);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_220069_5_, boolean p_220069_6_) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(BlockStateProperties.ENABLED)) {
            level.setBlock(pos, state.setValue(BlockStateProperties.ENABLED, flag), 4);
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof GeneIndexerBlockEntity) {
                ((GeneIndexerBlockEntity) tile).setDirty();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide()) {
            final BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof GeneIndexerBlockEntity) {
                openGui((ServerPlayer) player, (GeneIndexerBlockEntity) tileEntity);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneIndexerBlockEntity(pos, state);
    }

    public void openGui(ServerPlayer player, GeneIndexerBlockEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}