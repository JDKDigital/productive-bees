package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.CryoStasisBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class CryoStasisContainer extends AbstractContainer<CryoStasisBlockEntity>
{
    public CryoStasisContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CryoStasisContainer(final int windowId, final Inventory playerInventory, final CryoStasisBlockEntity blockEntity) {
        super(ModContainerTypes.CRYO_STASIS.get(), blockEntity, windowId);

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), CryoStasisBlockEntity.SLOT_INPUT, 108, 18));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), CryoStasisBlockEntity.SLOT_CAGE, 108, 36));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), CryoStasisBlockEntity.SLOT_OUT, 108, 54));

        layoutPlayerInventorySlots(playerInventory, 0, 108, 84);
    }

    private static CryoStasisBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CryoStasisBlockEntity) {
            return (CryoStasisBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
