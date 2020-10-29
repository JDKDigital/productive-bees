package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.PoweredCentrifuge;
import cy.jdkdigital.productivebees.common.tileentity.PoweredCentrifugeTileEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PoweredCentrifugeContainer extends CentrifugeContainer
{
    public final PoweredCentrifugeTileEntity tileEntity;

    public PoweredCentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public PoweredCentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final PoweredCentrifugeTileEntity tileEntity) {
        super(ModContainerTypes.POWERED_CENTRIFUGE.get(), windowId, playerInventory, tileEntity);

        this.tileEntity = tileEntity;

//        // Energy
//        trackInt(new IntReferenceHolder()
//        {
//            @Override
//            public int get() {
//                return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
//            }
//
//            @Override
//            public void set(int value) {
//                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
//                    if (handler.getEnergyStored() > 0) {
//                        handler.extractEnergy(handler.getEnergyStored(), false);
//                    }
//                    if (value > 0) {
//                        handler.receiveEnergy(value, false);
//                    }
//                });
//            }
//        });
    }

    private static PoweredCentrifugeTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof PoweredCentrifugeTileEntity) {
            return (PoweredCentrifugeTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof PoweredCentrifuge && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
