package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.HeatedCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class HeatedCentrifugeContainer<T extends CentrifugeBlockEntity> extends CentrifugeContainer<T>
{
    public HeatedCentrifugeContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, (T)getTileEntity(playerInventory, data));
    }

    public HeatedCentrifugeContainer(final int windowId, final Inventory playerInventory, final T blockEntity) {
        super(ModContainerTypes.HEATED_CENTRIFUGE.get(), windowId, playerInventory, blockEntity);

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.getEnergyHandler().getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (blockEntity.getEnergyHandler().getEnergyStored() > 0) {
                    blockEntity.getEnergyHandler().extractEnergy(blockEntity.getEnergyHandler().getEnergyStored(), false);
                }
                if (value > 0) {
                    blockEntity.getEnergyHandler().receiveEnergy(value, false);
                }
            }
        });
    }

    private static HeatedCentrifugeBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof HeatedCentrifugeBlockEntity tile) {
            return tile;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
