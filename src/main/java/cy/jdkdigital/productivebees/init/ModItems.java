package cy.jdkdigital.productivebees.init;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.item.StoneChipRenderer;
import cy.jdkdigital.productivebees.client.render.item.WoodChipRenderer;
import cy.jdkdigital.productivebees.common.item.*;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final Food SUGARBAG = (new Food.Builder()).nutrition(3).saturationMod(0.2F).alwaysEat()
            .effect(() -> new EffectInstance(Effects.REGENERATION, 1200, 1), 0.2f)
            .effect(() -> new EffectInstance(Effects.ABSORPTION, 1200, 1), 0.1f)
            .effect(() -> new EffectInstance(Effects.HEAL, 1200, 1), 0.3f)
            .effect(() -> new EffectInstance(Effects.DIG_SPEED, 1200, 1), 0.1f)
            .effect(() -> new EffectInstance(Effects.HEALTH_BOOST, 1200, 1), 0.1f)
            .effect(() -> new EffectInstance(Effects.DAMAGE_BOOST, 1200, 1), 0.1f)
            .effect(() -> new EffectInstance(Effects.MOVEMENT_SPEED, 1200, 1), 0.1f)
            .effect(() -> new EffectInstance(Effects.DAMAGE_RESISTANCE, 1200, 1), 0.2f)
            .effect(() -> new EffectInstance(Effects.LUCK, 1200, 1), 0.1f)
            .build();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ProductiveBees.MODID);

    public static final List<RegistryObject<Item>> SPAWN_EGGS = Lists.newArrayList();

    public static final RegistryObject<Item> HONEY_BUCKET = createItem("honey_bucket", () -> new BucketItem(ModFluids.HONEY, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_CAGE = createItem("bee_cage", () -> new BeeCage(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> STURDY_BEE_CAGE = createItem("sturdy_bee_cage", () -> new SturdyBeeCage(new Item.Properties().stacksTo(1).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEY_TREAT = createItem("honey_treat", () -> new HoneyTreat(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE_BOTTLE = createItem("gene_bottle", () -> new GeneBottle(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> GENE = createItem("gene", () -> new Gene(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> MILK_BOTTLE = createItem("milk_bottle", () -> new Item(new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB = createItem("bee_bomb", () -> new BeeBomb(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> BEE_BOMB_ANGRY = createItem("bee_bomb_angry", () -> new BeeBombAngry(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> NEST_LOCATOR = createItem("nest_locator", () -> new NestLocator(new Item.Properties().tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> SUGARBAG_HONEYCOMB = createItem("sugarbag_honeycomb", () -> new Item((new Item.Properties().food(SUGARBAG).rarity(Rarity.EPIC)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> TREAT_ON_A_STICK = createItem("treat_on_a_stick", () -> new TreatOnAStick((new Item.Properties()).durability(28).tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> CONFIGURABLE_HONEYCOMB = createItem("configurable_honeycomb", () -> new Honeycomb((new Item.Properties()).tab(null), "#d2ab00"));

    public static final RegistryObject<Item> HONEYCOMB_ALFSTEEL = createItem("honeycomb_alfsteel", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mythicbotany")), "#ffd238"));
    public static final RegistryObject<Item> HONEYCOMB_ALLTHEMODIUM = createItem("honeycomb_allthemodium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("allthemodium")), "#f2f24f"));
    public static final RegistryObject<Item> HONEYCOMB_AMBER = createItem("honeycomb_amber", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#d2ab00"));
    public static final RegistryObject<Item> HONEYCOMB_BASALZ = createItem("honeycomb_basalz", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("thermal")), "#ff8219"));
    public static final RegistryObject<Item> HONEYCOMB_BAUXITE = createItem("honeycomb_bauxite", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BISMUTH = createItem("honeycomb_bismuth", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ece386"));
    public static final RegistryObject<Item> HONEYCOMB_BLAZING = createItem("honeycomb_blazing", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BLITZ = createItem("honeycomb_blitz", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("thermal")), "#e9edf3"));
    public static final RegistryObject<Item> HONEYCOMB_BLIZZ = createItem("honeycomb_blizz", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("thermal")), "#1d7cf1"));
    public static final RegistryObject<Item> HONEYCOMB_BLOODY = createItem("honeycomb_bloody", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("bloodmagic")), "#7a0300"));
    public static final RegistryObject<Item> HONEYCOMB_BONE = createItem("honeycomb_bone", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_BRAZEN = createItem("honeycomb_brazen", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#DAAA4C"));
    public static final RegistryObject<Item> HONEYCOMB_BRONZE = createItem("honeycomb_bronze", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#C98C52"));
    public static final RegistryObject<Item> HONEYCOMB_CHOCOLATE = createItem("honeycomb_chocolate", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#914139"));
    public static final RegistryObject<Item> HONEYCOMB_CINNABAR = createItem("honeycomb_cinnabar", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#d73e4a"));
    public static final RegistryObject<Item> HONEYCOMB_COMMON_SALVAGE = createItem("honeycomb_common_salvage", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mmorpg")), "#495f58"));
    public static final RegistryObject<Item> HONEYCOMB_CONSTANTAN = createItem("honeycomb_constantan", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#fc8669"));
    public static final RegistryObject<Item> HONEYCOMB_COPPER = createItem("honeycomb_copper", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#F48702"));
    public static final RegistryObject<Item> HONEYCOMB_DIAMOND = createItem("honeycomb_diamond", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#3ddfe1"));
    public static final RegistryObject<Item> HONEYCOMB_DRACONIC = createItem("honeycomb_draconic", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ELECTRUM = createItem("honeycomb_electrum", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#D5BB4F"));
    public static final RegistryObject<Item> HONEYCOMB_ELEMENTIUM = createItem("honeycomb_elementium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("botania")), "#dc5af8"));
    public static final RegistryObject<Item> HONEYCOMB_EMERALD = createItem("honeycomb_emerald", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#26ac43"));
    public static final RegistryObject<Item> HONEYCOMB_ENDER = createItem("honeycomb_ender", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ENDER_BIOTITE = createItem("honeycomb_ender_biotite", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#0f1318"));
    public static final RegistryObject<Item> HONEYCOMB_ENDERIUM = createItem("honeycomb_enderium", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#58a28b"));
    public static final RegistryObject<Item> HONEYCOMB_EPIC_SALVAGE = createItem("honeycomb_epic_salvage", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mmorpg")), "#af1281"));
    public static final RegistryObject<Item> HONEYCOMB_EXPERIENCE = createItem("honeycomb_experience", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#00fc1a"));
    public static final RegistryObject<Item> HONEYCOMB_FOSSILISED = createItem("honeycomb_fossilised", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#222525"));
    public static final RegistryObject<Item> HONEYCOMB_FLUORITE = createItem("honeycomb_fluorite", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#32e1f6"));
    public static final RegistryObject<Item> HONEYCOMB_FROSTY = createItem("honeycomb_frosty", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#86aefd"));
    public static final RegistryObject<Item> HONEYCOMB_GHOSTLY = createItem("honeycomb_ghostly", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_GLOWING = createItem("honeycomb_glowing", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_GOLD = createItem("honeycomb_gold", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#fffd6e"));
    public static final RegistryObject<Item> HONEYCOMB_IMPERIUM = createItem("honeycomb_imperium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#007FDB"));
    public static final RegistryObject<Item> HONEYCOMB_INFERIUM = createItem("honeycomb_inferium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#748E00"));
    public static final RegistryObject<Item> HONEYCOMB_INSANIUM = createItem("honeycomb_insanium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagradditions")), "#4d086d"));
    public static final RegistryObject<Item> HONEYCOMB_INVAR = createItem("honeycomb_invar", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ADB7B2"));
    public static final RegistryObject<Item> HONEYCOMB_IRON = createItem("honeycomb_iron", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#cdcdcd"));
    public static final RegistryObject<Item> HONEYCOMB_LAPIS = createItem("honeycomb_lapis", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#3537bc"));
    public static final RegistryObject<Item> HONEYCOMB_LEGENDARY_SALVAGE = createItem("honeycomb_legendary_salvage", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mmorpg")), "#af8912"));
    public static final RegistryObject<Item> HONEYCOMB_LEADEN = createItem("honeycomb_leaden", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#677193"));
    public static final RegistryObject<Item> HONEYCOMB_LUMIUM = createItem("honeycomb_lumium", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#f4ffc3"));
    public static final RegistryObject<Item> HONEYCOMB_MAGMATIC = createItem("honeycomb_magmatic", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_MANASTEEL = createItem("honeycomb_manasteel", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("botania")), "#4aa7ef"));
    public static final RegistryObject<Item> HONEYCOMB_MENRIL = createItem("honeycomb_menril", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#5a7088"));
    public static final RegistryObject<Item> HONEYCOMB_MILKY = createItem("honeycomb_milky", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_NETHERITE = createItem("honeycomb_netherite", () -> new Honeycomb((new Item.Properties().fireResistant()).tab(ModItemGroups.PRODUCTIVE_BEES), "#4d494d"));
    public static final RegistryObject<Item> HONEYCOMB_NICKEL = createItem("honeycomb_nickel", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#D8CC93"));
    public static final RegistryObject<Item> HONEYCOMB_NITER = createItem("honeycomb_niter", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#e9edf3"));
    public static final RegistryObject<Item> HONEYCOMB_OBSIDIAN = createItem("honeycomb_obsidian", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#3b2754"));
    public static final RegistryObject<Item> HONEYCOMB_OSMIUM = createItem("honeycomb_osmium", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#4c9db6"));
    public static final RegistryObject<Item> HONEYCOMB_PINK_SLIMY = createItem("honeycomb_pink_slimy", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#b969ba"));
    public static final RegistryObject<Item> HONEYCOMB_PLASTIC = createItem("honeycomb_plastic", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#d3d3d3"));
    public static final RegistryObject<Item> HONEYCOMB_PLATINUM = createItem("honeycomb_platinum", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#6FEAEF"));
    public static final RegistryObject<Item> HONEYCOMB_POWDERY = createItem("honeycomb_powdery", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_PROSPERITY = createItem("honeycomb_prosperity", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#ddfbfb"));
    public static final RegistryObject<Item> HONEYCOMB_PRUDENTIUM = createItem("honeycomb_prudentium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#008C23"));
    public static final RegistryObject<Item> HONEYCOMB_QUARTZ = createItem("honeycomb_quartz", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ede5dd"));
    public static final RegistryObject<Item> HONEYCOMB_RADIOACTIVE = createItem("honeycomb_radioactive", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#60AE11"));
    public static final RegistryObject<Item> HONEYCOMB_RARE_SALVAGE = createItem("honeycomb_rare_salvage", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mmorpg")), "#1286af"));
    public static final RegistryObject<Item> HONEYCOMB_REDSTONE = createItem("honeycomb_redstone", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#d03621"));
    public static final RegistryObject<Item> HONEYCOMB_ROTTEN = createItem("honeycomb_rotten", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_REFINED_GLOWSTONE = createItem("honeycomb_refined_glowstone", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#feee7c"));
    public static final RegistryObject<Item> HONEYCOMB_REFINED_OBSIDIAN = createItem("honeycomb_refined_obsidian", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#5e5077"));
    public static final RegistryObject<Item> HONEYCOMB_SIGNALUM = createItem("honeycomb_signalum", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#e7917d"));
    public static final RegistryObject<Item> HONEYCOMB_SILICON = createItem("honeycomb_silicon", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#918d96"));
    public static final RegistryObject<Item> HONEYCOMB_SILKY = createItem("honeycomb_silky", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ffffff"));
    public static final RegistryObject<Item> HONEYCOMB_SILVER = createItem("honeycomb_silver", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#A9DBE5"));
    public static final RegistryObject<Item> HONEYCOMB_SLIMY = createItem("honeycomb_slimy", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_SOULIUM = createItem("honeycomb_soulium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#301b10"));
    public static final RegistryObject<Item> HONEYCOMB_SPACIAL = createItem("honeycomb_spacial", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#dfe5f6"));
    public static final RegistryObject<Item> HONEYCOMB_STEEL = createItem("honeycomb_steel", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#737373"));
    public static final RegistryObject<Item> HONEYCOMB_SULFUR = createItem("honeycomb_sulfur", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#e4ff95"));
    public static final RegistryObject<Item> HONEYCOMB_SUPREMIUM = createItem("honeycomb_supremium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#C40000"));
    public static final RegistryObject<Item> HONEYCOMB_TEA = createItem("honeycomb_tea", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ca7157"));
    public static final RegistryObject<Item> HONEYCOMB_TERRASTEEL = createItem("honeycomb_terrasteel", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("botania")), "#49cc1d"));
    public static final RegistryObject<Item> HONEYCOMB_TERTIUM = createItem("honeycomb_tertium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mysticalagriculture")), "#B74900"));
    public static final RegistryObject<Item> HONEYCOMB_TIN = createItem("honeycomb_tin", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#9ABDD6"));
    public static final RegistryObject<Item> HONEYCOMB_TITANIUM = createItem("honeycomb_titanium", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#D0D1DA"));
    public static final RegistryObject<Item> HONEYCOMB_TUNGSTEN = createItem("honeycomb_tungsten", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#616669"));
    public static final RegistryObject<Item> HONEYCOMB_UNCOMMON_SALVAGE = createItem("honeycomb_uncommon_salvage", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("mmorpg")), "#12af4d"));
    public static final RegistryObject<Item> HONEYCOMB_UNOBTAINIUM = createItem("honeycomb_unobtainium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("allthemodium")), "#bc2feb"));
    public static final RegistryObject<Item> HONEYCOMB_URANINITE = createItem("honeycomb_uraninite", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("powah")), "#00FF00"));
    public static final RegistryObject<Item> HONEYCOMB_VIBRANIUM = createItem("honeycomb_vibranium", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("allthemodium")), "#73ffb9"));
    public static final RegistryObject<Item> HONEYCOMB_WITHERED = createItem("honeycomb_withered", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> HONEYCOMB_ZINC = createItem("honeycomb_zinc", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#E9EBE7"));
    public static final RegistryObject<Item> HONEYCOMB_RUBY = createItem("honeycomb_ruby", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#c62415"));
    public static final RegistryObject<Item> HONEYCOMB_SAPPHIRE = createItem("honeycomb_sapphire", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#5241f3"));
    public static final RegistryObject<Item> HONEYCOMB_APATITE = createItem("honeycomb_apatite", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#69ffff"));
    public static final RegistryObject<Item> HONEYCOMB_COBALT = createItem("honeycomb_cobalt", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#1d77eb"));
    public static final RegistryObject<Item> HONEYCOMB_HEPATIZON = createItem("honeycomb_hepatizon", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#675072"));
    public static final RegistryObject<Item> HONEYCOMB_MANYULLYN = createItem("honeycomb_manyullyn", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#ab6cd7"));
    public static final RegistryObject<Item> HONEYCOMB_KNIGHTSLIME = createItem("honeycomb_knightslime", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#c882f5"));
    public static final RegistryObject<Item> HONEYCOMB_PIG_IRON = createItem("honeycomb_pig_iron", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#dbaaa9"));
    public static final RegistryObject<Item> HONEYCOMB_QUEENS_SLIME = createItem("honeycomb_queens_slime", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#267049"));
    public static final RegistryObject<Item> HONEYCOMB_ROSE_GOLD = createItem("honeycomb_rose_gold", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#eeb9a0"));
    public static final RegistryObject<Item> HONEYCOMB_SLIMESTEEL = createItem("honeycomb_slimesteel", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#7ae7e0"));
    public static final RegistryObject<Item> HONEYCOMB_SOULSTEEL = createItem("honeycomb_soulsteel", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#5c4436"));
    public static final RegistryObject<Item> HONEYCOMB_TINKERS_BRONZE = createItem("honeycomb_tinkers_bronze", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#ffdb7e"));
    public static final RegistryObject<Item> HONEYCOMB_SKY_SLIMY = createItem("honeycomb_sky_slimy", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#80d4d2"));
    public static final RegistryObject<Item> HONEYCOMB_ENDER_SLIMY = createItem("honeycomb_ender_slimy", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#d17bfc"));
    public static final RegistryObject<Item> HONEYCOMB_ICHOR_SLIMY = createItem("honeycomb_ichor_slimy", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("tconstruct")), "#fcb77b"));
    public static final RegistryObject<Item> HONEYCOMB_SPECTRUM = createItem("honeycomb_spectrum", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#ffc9a7"));
    public static final RegistryObject<Item> HONEYCOMB_COSMIC_DUST = createItem("honeycomb_cosmic_dust", () -> new Honeycomb((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES), "#2394cc"));
    public static final RegistryObject<Item> HONEYCOMB_STARMETAL = createItem("honeycomb_starmetal", () -> new Honeycomb((new Item.Properties()).tab(itemGroupCompat("astralsorcery")), "#0545b2"));

    public static final RegistryObject<Item> DRACONIC_DUST = createItem("draconic_dust", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> DRACONIC_CHUNK = createItem("draconic_chunk", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WITHER_SKULL_CHIP = createItem("wither_skull_chip", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> WOOD_CHIP = createItem("wood_chip", () -> new WoodChip((new Item.Properties().setISTER(() -> WoodChipRenderer::new)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> STONE_CHIP = createItem("stone_chip", () -> new StoneChip((new Item.Properties().setISTER(() -> StoneChipRenderer::new)).tab(ModItemGroups.PRODUCTIVE_BEES)));

    // Hive upgrades
    public static final RegistryObject<Item> UPGRADE_BASE = createItem("upgrade_base", () -> new Item((new Item.Properties()).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_PRODUCTIVITY = createItem("upgrade_productivity", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_TIME = createItem("upgrade_time", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_BREEDING = createItem("upgrade_breeding", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_COMB_BLOCK = createItem("upgrade_comb_block", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_ANTI_TELEPORT = createItem("upgrade_anti_teleport", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_RANGE = createItem("upgrade_range", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_FILTER = createItem("upgrade_filter", () -> new FilterUpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));
    public static final RegistryObject<Item> UPGRADE_BEE_SAMPLER = createItem("upgrade_bee_sampler", () -> new UpgradeItem((new Item.Properties().stacksTo(1)).tab(ModItemGroups.PRODUCTIVE_BEES)));

    public static final RegistryObject<Item> ADV_BREED_BEE = createItem("adv_breed_bee", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADV_BREED_ALL_BEES = createItem("adv_breed_all_bees", () -> new Item(new Item.Properties()));

    public static RegistryObject<Item> CONFIGURABLE_SPAWN_EGG;
    public static RegistryObject<Item> CONFIGURABLE_COMB_BLOCK;

    public static ItemGroup itemGroupCompat(String mods) {
        String[] modNames = mods.split(",");
        return ModList.get().isLoaded(modNames[0]) || (modNames.length > 1 && ModList.get().isLoaded(modNames[1])) ? ModItemGroups.PRODUCTIVE_BEES : null;
    }

    public static <I extends Item> RegistryObject<I> createItem(String name, Supplier<? extends I> supplier) {
        return ITEMS.register(name, supplier);
    }
}
