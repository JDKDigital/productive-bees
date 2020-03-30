package cy.jdkdigital.productivebees.init;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModItemGroups {
	
	public static final ItemGroup PRODUCTIVE_BEES = new ModItemGroup(ProductiveBees.MODID, () -> new ItemStack(Items.BEE_NEST));
	
	public static class ModItemGroup extends ItemGroup {

		private final Supplier<ItemStack> iconSupplier;
		
		public ModItemGroup(@Nonnull final String name, @Nonnull final Supplier<ItemStack> iconSupplier) {
			super(name);
			this.iconSupplier = iconSupplier;
		}

		@Override
		public ItemStack createIcon() {
			return iconSupplier.get();
		}
		
	}
	
}
