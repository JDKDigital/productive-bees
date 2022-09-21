package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Incubator;
import cy.jdkdigital.productivebees.common.block.entity.IncubatorBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class IncubatorContainer extends AbstractContainer
{
    public final IncubatorBlockEntity tileEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final IncubatorBlockEntity tileEntity) {
        this(ModContainerTypes.INCUBATOR.get(), windowId, playerInventory, tileEntity);
    }

    public IncubatorContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final IncubatorBlockEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
                    if (handler.getEnergyStored() > 0) {
                        handler.extractEnergy(handler.getEnergyStored(), false);
                    }
                    if (value > 0) {
                        handler.receiveEnergy(value, false);
                    }
                });
            }
        });

        addDataSlot(new DataSlot()
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

        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 0, 52 - 13, 35));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 1, 80 - 13, 53));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, 2, 108 - 13, 35));
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
    }

    private static IncubatorBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof IncubatorBlockEntity) {
            return (IncubatorBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Incubator && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tileEntity;
    }
}
