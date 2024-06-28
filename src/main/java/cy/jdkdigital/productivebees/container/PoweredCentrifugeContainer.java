package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.PoweredCentrifuge;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PoweredCentrifugeContainer extends CentrifugeContainer
{
    public final PoweredCentrifugeBlockEntity tileEntity;

    public PoweredCentrifugeContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public PoweredCentrifugeContainer(final int windowId, final Inventory playerInventory, final PoweredCentrifugeBlockEntity tileEntity) {
        super(ModContainerTypes.POWERED_CENTRIFUGE.get(), windowId, playerInventory, tileEntity);

        this.tileEntity = tileEntity;

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.energyHandler.getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (tileEntity.energyHandler.getEnergyStored() > 0) {
                    tileEntity.energyHandler.extractEnergy(tileEntity.energyHandler.getEnergyStored(), false);
                }
                if (value > 0) {
                    tileEntity.energyHandler.receiveEnergy(value, false);
                }
            }
        });
    }

    private static PoweredCentrifugeBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof PoweredCentrifugeBlockEntity) {
            return (PoweredCentrifugeBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof PoweredCentrifuge && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return tileEntity;
    }
}
