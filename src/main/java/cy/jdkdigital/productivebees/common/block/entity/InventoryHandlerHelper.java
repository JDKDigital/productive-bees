package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.UpgradeItem;
import cy.jdkdigital.productivebees.common.item.WoodChip;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class InventoryHandlerHelper
{
    public static final int BOTTLE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int FLUID_ITEM_OUTPUT_SLOT = 11;

    public static final int[] OUTPUT_SLOTS = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};

    private static int getAvailableOutputSlot(ItemHandler handler, ItemStack insertStack) {
        return getAvailableOutputSlot(handler, insertStack, new ArrayList<>());
    }

    private static int getAvailableOutputSlot(ItemHandler handler, ItemStack insertStack, List<Integer> blacklistedSlots) {
        int emptySlot = 0;
        for (int slot : handler.getOutputSlots()) {
            if (blacklistedSlots.contains(slot)) {
                continue;
            }
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            } else if (stack.getItem().equals(insertStack.getItem()) && (stack.getCount() + insertStack.getCount()) <= stack.getMaxStackSize()) {
                // Check tags
                if (WoodChip.getBlock(insertStack) != null) {
                    Block block = WoodChip.getBlock(stack);
                    if (block != null && block.equals(WoodChip.getBlock(insertStack))) {
                        return slot;
                    }
                } else if (!Gene.getAttributeName(insertStack).isEmpty()) {
                    if (
                        Gene.getAttributeName(stack).equals(Gene.getAttributeName(insertStack)) &&
                        Gene.getValue(stack).equals(Gene.getValue(insertStack)) &&
                        Gene.getPurity(stack).equals(Gene.getPurity(insertStack))
                    ) {
                        return slot;
                    }
                } else if (stack.isEmpty() || areItemsAndTagsEqual(stack, insertStack)) {
                    return slot;
                }
            }
        }
        return emptySlot;
    }

    public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
        return (
                    stack1.isEmpty() && stack2.isEmpty()
                ) ||
                (
                    stack1.getItem() == stack2.getItem() && areItemStackTagsEqual(stack1, stack2)
                );
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        if (!stackA.isEmpty() && !stackB.isEmpty()) {
            CompoundTag tagA = stackA.getTag();
            CompoundTag tagB = stackB.getTag();
            if ((tagA == null || tagA.isEmpty()) && tagB != null && !tagB.isEmpty()) {
                return false;
            } else {
                return (tagA == null || tagA.isEmpty() || tagA.equals(stackB.getTag())) && stackA.areCapsCompatible(stackB);
            }
        } else {
            return false;
        }
    }

    public static class ItemHandler extends ItemStackHandler
    {
        protected BlockEntity tileEntity;

        public ItemHandler(int size) {
            this(size, null);
        }

        public ItemHandler(int size, @Nullable BlockEntity tileEntity) {
            super(size);
            this.tileEntity = tileEntity;
        }

        public int[] getOutputSlots() {
            return OUTPUT_SLOTS;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (tileEntity != null) {
                tileEntity.setChanged();
            }
        }

        public boolean isInputSlot(int slot) {
            return slot == BOTTLE_SLOT || slot == INPUT_SLOT;
        }

        public boolean isInsertableSlot(int slot) {
            return slot != BOTTLE_SLOT && slot != INPUT_SLOT && slot != FLUID_ITEM_OUTPUT_SLOT;
        }

        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE;
        }

        public boolean isInputSlotItem(int slot, ItemStack item) {
            return (slot == BOTTLE_SLOT && isContainerItem(item.getItem())) || (slot == FLUID_ITEM_OUTPUT_SLOT && !isContainerItem(item.getItem()));
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isItemValid(slot, stack, true);
        }

        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            // Always allow an input item into an input slot
            if (isInputSlotItem(slot, stack)) {
                return true;
            }

            return !fromAutomation && isInsertableSlot(slot);
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
                    setStackInSlot(slot, stack.copy());
                } else {
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
        public void deserializeNBT(CompoundTag nbt) {
            int size = nbt.contains("Size", 3) ? nbt.getInt("Size") : stacks.size();
            if (size < stacks.size()) {
                nbt.putInt("Size", stacks.size());
            }
            super.deserializeNBT(nbt);
        }
    }

    public static class UpgradeHandler extends ItemHandler
    {
        public UpgradeHandler(int size, BlockEntity tileEntity) {
            super(size, tileEntity);
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return item.getItem() instanceof UpgradeItem;
        }
    }


    public static class FluidHandler extends FluidTank implements INBTSerializable<CompoundTag>
    {
        public FluidHandler(int capacity) {
            super(capacity);
        }

        public FluidHandler(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            this.fluid.writeToNBT(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            fluid = FluidStack.loadFluidStackFromNBT(nbt);
        }
    }
}
