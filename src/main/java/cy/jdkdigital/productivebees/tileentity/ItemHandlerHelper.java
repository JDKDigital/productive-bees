package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemHandlerHelper
{
    public static final int BOTTLE_SLOT = 0;
    public static final int INPUT_SLOT = 1;

    public static final int[] OUTPUT_SLOTS = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};

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
            int stackSizeLimit = stack.getMaxStackSize();
            if (stack.getItem() == insertStack.getItem() && (stack.getCount() + insertStack.getCount()) <= stackSizeLimit) {
                return slot;
            }
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            }
        }
        return emptySlot;
    }

    public static ItemHandler getInventoryHandler(TileEntity tileEntity, int inputSize) {
        return new ItemHandler(10 + inputSize, tileEntity);
    }

    public static class ItemHandler extends ItemStackHandler
    {
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

        public boolean isInputSlot(int slot) {
            return slot == BOTTLE_SLOT || slot == INPUT_SLOT;
        }

        public boolean isInputItem(Item item) {
            return item == Items.GLASS_BOTTLE;
        }

        public boolean isInputSlotItem(int slot, Item item) {
            return (slot == BOTTLE_SLOT && item == Items.GLASS_BOTTLE) || (slot == INPUT_SLOT && ModTags.HONEYCOMBS.contains(item));
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isItemValid(slot, stack, true);
        }

        ;

        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            // Always allow an input item into an input slot
            if (isInputSlotItem(slot, stack.getItem())) {
                return true;
            }

            // No putting non-input into input
            if (isInputSlot(slot) && !isInputItem(stack.getItem())) {
                return false;
            }

            // Allow inserting non-input items into output
            if (!isInputSlot(slot) && !isInputItem(stack.getItem())) {
                return true;
            }

            // You can manually insert input items into output
            return !fromAutomation;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return extractItem(slot, amount, simulate, true);
        }

        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean fromAutomation) {
            // Do not extract from input slots
            if (fromAutomation && isInputSlot(slot)) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return insertItem(slot, stack, simulate, true);
        }

        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            return super.insertItem(slot, stack, simulate);
        }

        public boolean addOutput(@Nonnull ItemStack stack) {
            int slot = getAvailableOutputSlot(this, stack);
            if (slot > 0) {
                ItemStack existingStack = this.getStackInSlot(slot);
                if (existingStack.isEmpty()) {
                    setStackInSlot(slot, new ItemStack(stack.getItem(), stack.getCount()));
                }
                else {
                    existingStack.grow(stack.getCount());
                }
                onContentsChanged(slot);
                return true;
            }
            return false;
        }

        public boolean canFitStacks(List<ItemStack> stacks) {
            List<Integer> usedSlots = new ArrayList<>();
            for (ItemStack stack : stacks) {
                int slot = getAvailableOutputSlot(this, stack, usedSlots);
                if (slot == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            int size = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size();
            if (size < stacks.size()) {
                nbt.putInt("Size", stacks.size());
            }
            super.deserializeNBT(nbt);
        }
    }
}
