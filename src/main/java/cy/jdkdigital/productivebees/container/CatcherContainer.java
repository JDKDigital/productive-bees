package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.CatcherBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class CatcherContainer extends AbstractContainer<CatcherBlockEntity>
{
    public CatcherContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CatcherContainer(final int windowId, final Inventory playerInventory, final CatcherBlockEntity blockEntity) {
        super(ModContainerTypes.CATCHER.get(), blockEntity, windowId);

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.BOTTLE_SLOT, 26, 35));

        // Inventory slots
        addSlotBox(this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.OUTPUT_SLOTS[0], 80, 17, 3, 18, 3, 18);

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static CatcherBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CatcherBlockEntity) {
            return (CatcherBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
