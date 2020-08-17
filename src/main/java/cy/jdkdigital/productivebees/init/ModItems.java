package cy.jdkdigital.productivebees.init;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.item.WoodChipRenderer;
import cy.jdkdigital.productivebees.item.*;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProductiveBees.MODID);
    public static final List<RegistryObject<Item>> SPAWN_EGGS = Lists.newArrayList();

    public static final RegistryObject<Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY, new Item.Properties().maxStackSize(1).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCage(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE_BOTTLE = createItem("gene_bottle", () -> new GeneBottle(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE = createItem("gene", () -> new Gene(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> MILK_BOTTLE = createItem("milk_bottle", () -> new Item(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB = createItem("bee_bomb", () -> new BeeBomb(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB_ANGRY = createItem("bee_bomb_angry", () -> new BeeBombAngry(new Item.Properties().group(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> HONEYCOMB_AMBER = createItem("honeycomb_amber", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xd2ab00"));
    public static final RegistryObject<Item> HONEYCOMB_BAUXITE = createItem("honeycomb_bauxite", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BLAZING = createItem("honeycomb_blazing", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BONE = createItem("honeycomb_bone", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BRAZEN = createItem("honeycomb_brazen", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xDAAA4C"));
    public static final RegistryObject<Item> HONEYCOMB_BRONZE = createItem("honeycomb_bronze", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xC98C52"));
    public static final RegistryObject<Item> HONEYCOMB_COPPER = createItem("honeycomb_copper", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xF48702"));
    public static final RegistryObject<Item> HONEYCOMB_DIAMOND = createItem("honeycomb_diamond", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x3ddfe1"));
    public static final RegistryObject<Item> HONEYCOMB_DRACONIC = createItem("honeycomb_draconic", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_EMERALD = createItem("honeycomb_emerald", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x26ac43"));
    public static final RegistryObject<Item> HONEYCOMB_ELECTRUM = createItem("honeycomb_electrum", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xD5BB4F"));
    public static final RegistryObject<Item> HONEYCOMB_ENDER = createItem("honeycomb_ender", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_FOSSILISED = createItem("honeycomb_fossilised", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x222525"));
    public static final RegistryObject<Item> HONEYCOMB_GOLD = createItem("honeycomb_gold", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xc8df24"));
    public static final RegistryObject<Item> HONEYCOMB_GLOWING = createItem("honeycomb_glowing", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_INVAR = createItem("honeycomb_invar", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xADB7B2"));
    public static final RegistryObject<Item> HONEYCOMB_IRON = createItem("honeycomb_iron", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xcdcdcd"));
    public static final RegistryObject<Item> HONEYCOMB_LAPIS = createItem("honeycomb_lapis", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x3537bc"));
    public static final RegistryObject<Item> HONEYCOMB_LEADEN = createItem("honeycomb_leaden", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x677193"));
    public static final RegistryObject<Item> HONEYCOMB_MAGMATIC = createItem("honeycomb_magmatic", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_MILKY = createItem("honeycomb_milky", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_NICKEL = createItem("honeycomb_nickel", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xD8CC93"));
    public static final RegistryObject<Item> HONEYCOMB_OSMIUM = createItem("honeycomb_osmium", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x4c9db6"));
    public static final RegistryObject<Item> HONEYCOMB_PLATINUM = createItem("honeycomb_platinum", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x6FEAEF"));
    public static final RegistryObject<Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_QUARTZ = createItem("honeycomb_quartz", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xede5dd"));
    public static final RegistryObject<Item> HONEYCOMB_RADIOACTIVE = createItem("honeycomb_radioactive", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x60AE11"));
    public static final RegistryObject<Item> HONEYCOMB_REDSTONE = createItem("honeycomb_redstone", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xd03621"));
    public static final RegistryObject<Item> HONEYCOMB_ROTTEN = createItem("honeycomb_rotten", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_SILVER = createItem("honeycomb_silver", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xA9DBE5"));
    public static final RegistryObject<Item> HONEYCOMB_SLIMY = createItem("honeycomb_slimy", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_STEEL = createItem("honeycomb_steel", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x737373"));
    public static final RegistryObject<Item> HONEYCOMB_TIN = createItem("honeycomb_tin", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x9ABDD6"));
    public static final RegistryObject<Item> HONEYCOMB_TITANIUM = createItem("honeycomb_titanium", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xD0D1DA"));
    public static final RegistryObject<Item> HONEYCOMB_TUNGSTEN = createItem("honeycomb_tungsten", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0x616669"));
    public static final RegistryObject<Item> HONEYCOMB_WITHERED = createItem("honeycomb_withered", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ZINC = createItem("honeycomb_zinc", () -> new HoneyComb((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES), "0xE9EBE7"));

    public static final RegistryObject<Item> DRACONIC_DUST = createItem("draconic_dust", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> DRACONIC_CHUNK = createItem("draconic_chunk", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", () -> new Item((new Item.Properties()).group(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WOOD_CHIP = createItem("wood_chip", () -> new WoodChip((new Item.Properties().setISTER(() -> WoodChipRenderer::new)).group(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> ADV_BREED_BEE = createItem("adv_breed_bee", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", () -> new Item(new Item.Properties()));

    public static <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
        return ITEMS.register(name, supplier);
    }
}
