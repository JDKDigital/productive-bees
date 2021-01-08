package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Incubator;
import cy.jdkdigital.productivebees.common.tileentity.IncubatorTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class IncubatorContainer extends AbstractContainer
{
    public final IncubatorTileEntity tileEntity;

    public final IWorldPosCallable canInteractWithCallable;

    public IncubatorContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public IncubatorContainer(final int windowId, final PlayerInventory playerInventory, final IncubatorTileEntity tileEntity) {
        this(ModContainerTypes.INCUBATOR.get(), windowId, playerInventory, tileEntity);
    }

    public IncubatorContainer(@Nullable ContainerType<?> type, final int windowId, final PlayerInventory playerInventory, final IncubatorTileEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        // Energy
        trackInt(new IntReferenceHolder()
        {
            @Override
            public int get() {
                return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                    if (handler.getEnergyStored() > 0) {
                        handler.extractEnergy(handler.getEnergyStored(), false);
                    }
                    if (value > 0) {
                        handler.receiveEnergy(value, false);
                    }
                });
            }
        });

        trackInt(new IntReferenceHolder()
        {
            @Override
            public int get() {
                return tileEntity.recipeProgress;
            }

            @Override
            public void set(int value) {
                tileEntity.recipeProgress = value;
            }
        });

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 0, 52-13, 35));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 1, 80-13, 53));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 2, 108-13, 35));
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, - 5, 84);
    }

    private static IncubatorTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof IncubatorTileEntity) {
            return (IncubatorTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Incubator && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
