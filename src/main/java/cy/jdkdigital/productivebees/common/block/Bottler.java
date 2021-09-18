package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.BottlerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Bottler extends CapabilityContainerBlock
{
    public static final BooleanProperty HAS_BOTTLE = BooleanProperty.create("has_bottle");

    protected static final VoxelShape SHAPE = VoxelShapes.join(
            VoxelShapes.block(),
            VoxelShapes.or(
                    box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D),
                    box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D),
                    box(1.0D, 0.0D, 1.0D, 15.0D, 3.0D, 15.0D)
            ), IBooleanFunction.ONLY_FIRST);

    public Bottler(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(HAS_BOTTLE, Boolean.FALSE));
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

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide()) {
            final TileEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity != null) {
                ItemStack heldItem = player.getItemInHand(hand);
                boolean itemUsed = false;

                if (heldItem.getItem() instanceof BucketItem) {
                    if (FluidUtil.interactWithFluidHandler(player, hand, world, pos, null)) {
                        itemUsed = true;
                    }
                }

                if (!itemUsed) {
                    if (tileEntity instanceof BottlerTileEntity) {
                        openGui((ServerPlayerEntity) player, (BottlerTileEntity) tileEntity);
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HAS_BOTTLE);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BottlerTileEntity();
    }

    public void openGui(ServerPlayerEntity player, BottlerTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> packetBuffer.writeBlockPos(tileEntity.getBlockPos()));
    }
}