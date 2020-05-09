package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemHandlerHelper {
    public static final int BOTTLE_SLOT = 0;
    public static final int INPUT_SLOT = 1;

    public static final int[] OUTPUT_SLOTS = new int[] {1,2,3,4,5,6,7,8,9};

    private static int getAvailableOutputSlot(IItemHandler handler, ItemStack insertStack) {
        return getAvailableOutputSlot(handler, insertStack, new ArrayList<>());
    }
    private static int getAvailableOutputSlot(IItemHandler handler, ItemStack insertStack, List<Integer> blacklistedSlots) {
        int emptySlot = 0;
        for (int slot : OUTPUT_SLOTS) {
            if (blacklistedSlots.contains(slot)) {
                continue;
            }
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.getItem() == insertStack.getItem() && (stack.getMaxStackSize() + insertStack.getCount()) != stack.getCount()) {
                return slot;
            }
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            }
        }
        return emptySlot;
    }

    public static ItemHandler getOutputHandler(TileEntity tileEntity) {
        return new ItemHandler(10, tileEntity);
    }

    static class ItemHandler extends ItemStackHandler {
        private TileEntity tileEntity;

        public ItemHandler(int size, TileEntity tileEntity) {
            super(size);
            this.tileEntity = tileEntity;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            tileEntity.markDirty();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            ProductiveBees.LOGGER.info("Inserting items: slot:" + slot + " stack:" + stack);
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        public boolean addOutput(@Nonnull ItemStack stack) {
            int slot = getAvailableOutputSlot(this, stack);
            if (slot > 0) {
                insertItem(slot, stack, false);
                return true;
            }
            return false;
        }

        public boolean canFitStacks(List<ItemStack> stacks) {
            List<Integer> usedSlots = new ArrayList<>();
            for(ItemStack stack: stacks) {
                int slot = getAvailableOutputSlot(this, stack, usedSlots);
                if (slot == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
