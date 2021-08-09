package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemGroups
{
    public static final CreativeModeTab PRODUCTIVE_BEES = new ModItemGroup(ProductiveBees.MODID, () -> new ItemStack(Items.BEE_NEST));

    public static class ModItemGroup extends CreativeModeTab
    {
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return iconSupplier.get();
        }

        @Override
        public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
            for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey();

                // Add spawn egg item
                items.add(BeeCreator.getSpawnEgg(beeType));

                // Add comb item
                if (entry.getValue().getBoolean("createComb")) {
                    ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    BeeCreator.setTag(beeType, comb);

                    items.add(comb);

                    // Add comb block
                    ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                    BeeCreator.setTag(beeType, combBlock);

                    items.add(combBlock);
                }
            }

            super.fillItemList(items);
        }
    }
}
