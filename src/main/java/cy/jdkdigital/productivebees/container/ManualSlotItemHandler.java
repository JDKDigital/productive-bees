package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.tileentity.ItemHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ManualSlotItemHandler extends SlotItemHandler {
    ItemHandlerHelper.ItemHandler handler;

    public ManualSlotItemHandler(ItemHandlerHelper.ItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        handler = itemHandler;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return handler.isItemValid(this.getSlotIndex(), stack, false);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return !this.handler.extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return this.handler.extractItem(this.getSlotIndex(), amount, false, false);
    }
}
