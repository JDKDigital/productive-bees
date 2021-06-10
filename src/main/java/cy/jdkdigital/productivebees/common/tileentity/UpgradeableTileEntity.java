package cy.jdkdigital.productivebees.common.tileentity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface UpgradeableTileEntity
{
    LazyOptional<IItemHandlerModifiable> getUpgradeHandler();

    default boolean acceptsUpgrades() {
        return true;
    }

    default int getUpgradeCount(Item item) {
        AtomicInteger numberOfUpgrades = new AtomicInteger();
        getUpgradeHandler().ifPresent(handler -> {
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.getItem().equals(item)) {
                    numberOfUpgrades.getAndIncrement();
                }
            }
        });
        return numberOfUpgrades.get();
    }

    default List<ItemStack> getInstalledUpgrades(@Nullable Item upgradeItem) {
        List<ItemStack> upgrades = new ArrayList<>();
        getUpgradeHandler().ifPresent(handler -> {
            for (int slot = 0; slot < handler.getSlots(); ++slot) {
                if (upgradeItem == null || handler.getStackInSlot(slot).getItem().equals(upgradeItem)) {
                    upgrades.add(handler.getStackInSlot(slot));
                }
            }
        });
        return upgrades;
    }
}
