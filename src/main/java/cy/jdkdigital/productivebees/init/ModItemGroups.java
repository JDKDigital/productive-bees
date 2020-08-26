package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

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
            for (Map.Entry<ResourceLocation, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                // Add spawn egg item
                ItemStack egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
                setTag(entry.getKey().toString(), egg);

                items.add(egg);

                // Add comb item
                ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                setTag(entry.getKey().toString(), comb);

                items.add(comb);

                // Add comb block
                ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                setTag(entry.getKey().toString(), combBlock);

                items.add(combBlock);
            }

            super.fill(items);
        }

        public static void setTag(String type, ItemStack stack) {
            CompoundNBT tag = stack.getOrCreateChildTag("EntityTag");
            tag.putString("type", type);
        }
    }

}
