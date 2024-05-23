package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.HeatedCentrifuge;
import cy.jdkdigital.productivebees.common.block.entity.HeatedCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class HeatedCentrifugeContainer extends CentrifugeContainer
{
    public final HeatedCentrifugeBlockEntity blockEntity1;

    public HeatedCentrifugeContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public HeatedCentrifugeContainer(final int windowId, final Inventory playerInventory, final HeatedCentrifugeBlockEntity blockEntity) {
        super(ModContainerTypes.HEATED_CENTRIFUGE.get(), windowId, playerInventory, blockEntity);

        this.blockEntity1 = blockEntity;

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.energyHandler.getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (blockEntity.energyHandler.getEnergyStored() > 0) {
                    blockEntity.energyHandler.extractEnergy(blockEntity.energyHandler.getEnergyStored(), false);
                }
                if (value > 0) {
                    blockEntity.energyHandler.receiveEnergy(value, false);
                }
            }
        });
    }

    private static HeatedCentrifugeBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof HeatedCentrifugeBlockEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof HeatedCentrifuge && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity1;
    }
}
