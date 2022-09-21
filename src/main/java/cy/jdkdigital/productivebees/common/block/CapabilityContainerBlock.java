package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

abstract class CapabilityContainerBlock extends BaseEntityBlock
{
    protected CapabilityContainerBlock(Properties builder) {
        super(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity != null) {
                // Drop inventory
                tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                });
                if (tileEntity instanceof UpgradeableBlockEntity) {
                    ((UpgradeableBlockEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
                        for (int slot = 0; slot < handler.getSlots(); ++slot) {
                            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                        }
                    });
                }
            }
        }
        super.onRemove(oldState, worldIn, pos, newState, isMoving);
    }
}
