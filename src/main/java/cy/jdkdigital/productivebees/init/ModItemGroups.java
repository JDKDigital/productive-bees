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

        public static void setTag(String type, ItemStack stack) {
            CompoundNBT tag = stack.getOrCreateChildTag("EntityTag");
            tag.putString("type", type);
        }
    }

}
