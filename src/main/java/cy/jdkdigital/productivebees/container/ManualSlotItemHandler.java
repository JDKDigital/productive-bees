package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ManualSlotItemHandler extends SlotItemHandler
{
    InventoryHandlerHelper.ItemHandler handler;

    public ManualSlotItemHandler(InventoryHandlerHelper.ItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        handler = itemHandler;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return handler.isItemValid(this.getSlotIndex(), stack, false);
    }

    @Override
    public boolean mayPickup(PlayerEntity playerIn) {
        return !this.handler.extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return this.handler.extractItem(this.getSlotIndex(), amount, false, false);
    }
}
