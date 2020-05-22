package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.tileentity.ItemHandlerHelper;
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
        ProductiveBees.LOGGER.info("transferStackInSlot index:" + index);
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            final ItemStack slotStack = slot.getStack();
            returnStack = slotStack.copy();

            final int containerSlots = this.inventorySlots.size() - player.inventory.mainInventory.size();
            ProductiveBees.LOGGER.info("hasStack: " + slotStack + " slotsSize:" + containerSlots);

            // Move from container to inventory.
            if (index < containerSlots) {
                ProductiveBees.LOGGER.info("index < containerSlots");
                if (!mergeItemStack(slotStack, containerSlots, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }

            // Move from player inv into container
            int inputCount = containerSlots - 9;
            if (slotStack.getItem() == Items.GLASS_BOTTLE) {
                ProductiveBees.LOGGER.info("Glass bottle");
                // Bottles only go into slot 0
                if (!mergeItemStack(slotStack, 0, 1, false)) {
                    ProductiveBees.LOGGER.info("Glass bottle not allowed");
                }
            }
            if (inputCount == 2 && ModTags.HONEYCOMBS.contains(slotStack.getItem())) {
                ProductiveBees.LOGGER.info("Comb");
                if (!mergeItemStack(slotStack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (!mergeItemStack(slotStack, inputCount, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                ProductiveBees.LOGGER.info("putStack");
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                ProductiveBees.LOGGER.info("onSlotChanged");
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                ProductiveBees.LOGGER.info("Slotstack count equal: " + slotStack.getCount());
                return ItemStack.EMPTY;
            }
            ProductiveBees.LOGGER.info("onTake");
            slot.onTake(player, slotStack);
        }
        ProductiveBees.LOGGER.info("returnStack:" + returnStack);
        return returnStack;
    }

    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }


        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ProductiveBees.LOGGER.info("i:" + i + " slot:" + slot);
                ProductiveBees.LOGGER.info(this.inventorySlots);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());
                    ProductiveBees.LOGGER.info("j <= maxSize " + (j <= maxSize) + " j:" + j + " maxSize:" + maxSize + " itemstack.getCount:" + itemstack.getCount());
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.onSlotChanged();
                        ProductiveBees.LOGGER.info("j <= maxSize success");
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.onSlotChanged();
                        ProductiveBees.LOGGER.info("itemstack.getCount() < maxSize");
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                ProductiveBees.LOGGER.info("itemstack1.isEmpty() && slot1.isItemValid(stack) i" + i + " itemstack1.isEmpty:" + itemstack1.isEmpty() + " slot1.isItemValid:" + slot1.isItemValid(stack));
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    if (stack.getCount() > slot1.getSlotStackLimit()) {
                        slot1.putStack(stack.split(slot1.getSlotStackLimit()));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    ProductiveBees.LOGGER.info("itemstack1.isEmpty() && slot1.isItemValid(stack) success");
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            if (handler instanceof ItemHandlerHelper.ItemHandler) {
                addSlot(new ManualSlotItemHandler((ItemHandlerHelper.ItemHandler) handler, index, x, y));
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
