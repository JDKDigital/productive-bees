package cy.jdkdigital.productivebees.common.tileentity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public interface UpgradeableTileEntity
{
    LazyOptional<IItemHandlerModifiable> getUpgradeHandler();

    default int getUpgradeCount(Item item) {
        AtomicInteger numberOfUpgrades = new AtomicInteger();
        getUpgradeHandler().ifPresent(handler -> {
            IntStream.range(0, 4).forEach(slot -> {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.getItem().equals(item)) {
                    numberOfUpgrades.getAndIncrement();
                }
            });
        });
        return numberOfUpgrades.get();
    }
}
