package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class AbstractContainer extends Container
{
    protected AbstractContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack slotStack = slot.getStack();
            returnStack = slotStack.copy();

            final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();

            // Move from container to inventory.
            if (index < containerSlots) {
                if (!mergeItemStack(slotStack, containerSlots, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }

            // Move from player inv into container
            int inputCount = containerSlots > 9 ? containerSlots - 9 : containerSlots / 2;
            if (slotStack.getItem() == Items.GLASS_BOTTLE || slotStack.getItem() == Items.BUCKET) {
                // Bottles only go into slot 0
                if (!mergeItemStack(slotStack, InventoryHandlerHelper.BOTTLE_SLOT, InventoryHandlerHelper.BOTTLE_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (inputCount >= 2 && ModTags.HONEYCOMBS.contains(slotStack.getItem())) {
                if (!mergeItemStack(slotStack, InventoryHandlerHelper.INPUT_SLOT, InventoryHandlerHelper.INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (!mergeItemStack(slotStack, inputCount, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, slotStack);
        }
        return returnStack;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof InventoryHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) handler, index, x, y));
            }
            else {
                addSlot(new SlotItemHandler(handler, index, x, y));
            }
            x += dx;
            index++;
        }
        return index;
    }

    protected void addSlotBox(IItemHandler handler, int startIndex, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            startIndex = addSlotRange(handler, startIndex, x, y, horAmount, dx);
            y += dy;
        }
    }

    protected void layoutPlayerInventorySlots(IItemHandler inventory, int startIndex, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(inventory, startIndex + 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(inventory, startIndex, leftCol, topRow, 9, 18);
    }
}
