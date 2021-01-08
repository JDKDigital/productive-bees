package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.CentrifugeTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.IncubatorTileEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Incubator extends ContainerBlock
{
    public Incubator(Properties builder) {
        super(builder);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.INCUBATOR.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new IncubatorTileEntity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState oldState, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof CentrifugeTileEntity) {
                // Drop inventory
                tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
                ((CentrifugeTileEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
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
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            final TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof IncubatorTileEntity) {
                openGui((ServerPlayerEntity) player, (IncubatorTileEntity) tileEntity);
            }
        }
        return ActionResultType.SUCCESS;
    }

    public void openGui(ServerPlayerEntity player, IncubatorTileEntity tileEntity) {
        NetworkHooks.openGui(player, tileEntity, packetBuffer -> {
            packetBuffer.writeBlockPos(tileEntity.getPos());
        });
    }
}
