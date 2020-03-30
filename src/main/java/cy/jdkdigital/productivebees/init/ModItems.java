package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.item.BeeCageItem;
import cy.jdkdigital.productivebees.item.HoneyTreat;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ProductiveBees.MODID);

	public static RegistryObject<Item> BEE = createItem("bee", () -> new Item(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
	public static RegistryObject<Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY, new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
	public static RegistryObject<Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCageItem(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
	public static RegistryObject<Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES).food(Foods.HONEY)));

	public static <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
		return ITEMS.register(name, supplier);
	}
}
