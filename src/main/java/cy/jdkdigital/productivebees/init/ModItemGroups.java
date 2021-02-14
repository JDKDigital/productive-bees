package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public class ModItemGroups
{
    public static final ItemGroup PRODUCTIVE_BEES = new ModItemGroup(ProductiveBees.MODID, () -> new ItemStack(Items.BEE_NEST));

    public static class ModItemGroup extends ItemGroup
    {
        private final Supplier<ItemStack> iconSupplier;

        public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Nonnull
        @Override
        public ItemStack createIcon() {
            return iconSupplier.get();
        }

        @Override
        public void fill(@Nonnull NonNullList<ItemStack> items) {
            for (Map.Entry<String, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
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

            super.fill(items);
        }
    }
}
