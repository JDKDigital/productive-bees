package cy.jdkdigital.productivebees.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerWrapper implements IInventory
{
    private final IItemHandler handler;

    public ItemHandlerWrapper(IItemHandler handler) {
        this.handler = handler;
    }

    public IItemHandler getHandler() {
        return handler;
    }

    @Override
    public int getSizeInventory() {
        return this.handler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        return getSizeInventory() == 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return this.handler.extractItem(slot, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        return this.handler.insertItem(slot, ItemStack.EMPTY, false);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.handler.insertItem(slot, stack, false);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
    }
}
