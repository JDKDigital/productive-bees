package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.item.BeeCage;
import cy.jdkdigital.productivebees.item.HoneyTreat;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Foods;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ProductiveBees.MODID);
    public static final List<RegistryObject<Item>> SPAWN_EGGS = Lists.newArrayList();

    public static final RegistryObject<Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY, new Item.Properties().maxStackSize(1).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCage(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> HONEYCOMB_AMBER = createItem("honeycomb_amber", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BAUXITE = createItem("honeycomb_bauxite", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BLAZING = createItem("honeycomb_blazing", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BONE = createItem("honeycomb_bone", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BRAZEN = createItem("honeycomb_brazen", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BRONZE = createItem("honeycomb_bronze", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_COPPER = createItem("honeycomb_copper", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_DIAMOND = createItem("honeycomb_diamond", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_DRACONIC = createItem("honeycomb_draconic", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_EMERALD = createItem("honeycomb_emerald", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ELECTRUM = createItem("honeycomb_electrum", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ENDER = createItem("honeycomb_ender", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_FOSSILISED = createItem("honeycomb_fossilised", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_GOLD = createItem("honeycomb_gold", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_GLOWING = createItem("honeycomb_glowing", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_INVAR = createItem("honeycomb_invar", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_IRON = createItem("honeycomb_iron", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_LAPIS = createItem("honeycomb_lapis", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_LEADEN = createItem("honeycomb_leaden", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_MAGMATIC = createItem("honeycomb_magmatic", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_NICKEL = createItem("honeycomb_nickel", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_OSMIUM = createItem("honeycomb_osmium", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_PLATINUM = createItem("honeycomb_platinum", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_QUARTZ = createItem("honeycomb_quartz", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_RADIOACTIVE = createItem("honeycomb_radioactive", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_REDSTONE = createItem("honeycomb_redstone", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ROTTEN = createItem("honeycomb_rotten", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_SILVER = createItem("honeycomb_silver", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_SLIMY = createItem("honeycomb_slimy", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_STEEL = createItem("honeycomb_steel", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_TIN = createItem("honeycomb_tin", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_TITANIUM = createItem("honeycomb_titanium", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_TUNGSTEN = createItem("honeycomb_tungsten", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_WITHERED = createItem("honeycomb_withered", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ZINC = createItem("honeycomb_zinc", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> DRACONIC_DUST = createItem("draconic_dust", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> DRACONIC_CHUNK = createItem("draconic_chunk", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WOOD_CHIP = createItem("wood_chip", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> ADV_BREED_BEE = createItem("adv_breed_bee", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", () -> new Item(new Item.Properties()));

    public static <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
        return ITEMS.register(name, supplier);
    }
}
