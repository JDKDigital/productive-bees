package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.*;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.ExpansionBoxBlockEntity;
import cy.jdkdigital.productivebees.common.block.nest.BumbleBeeNest;
import cy.jdkdigital.productivebees.common.block.nest.SugarCaneNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.item.AmberItem;
import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import cy.jdkdigital.productivebees.common.item.JarBlockItem;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.util.FakeIngredient;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

    public static final RegistryObject<Block> BOTTLER = createBlock("bottler", () -> new Bottler(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> CENTRIFUGE = createBlock("centrifuge", () -> new Centrifuge(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> POWERED_CENTRIFUGE = createBlock("powered_centrifuge", () -> new PoweredCentrifuge(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> HEATED_CENTRIFUGE = createBlock("heated_centrifuge", () -> new HeatedCentrifuge(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> HONEY_GENERATOR = createBlock("honey_generator", () -> new HoneyGenerator(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> CATCHER = createBlock("catcher", () -> new Catcher(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> INCUBATOR = createBlock("incubator", () -> new Incubator(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> INACTIVE_DRAGON_EGG = createBlock("inactive_dragon_egg", () -> new InactiveDragonEgg(Block.Properties.copy(Blocks.DRAGON_EGG)));
    public static final RegistryObject<Block> INVISIBLE_REDSTONE_BLOCK = createBlock("invisible_redstone_block", () -> new InvisibleRedstone(Block.Properties.copy(Blocks.REDSTONE_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> FEEDER = createBlock("feeder", () -> new Feeder(Block.Properties.copy(Blocks.STONE_SLAB).noOcclusion()));
    public static final RegistryObject<Block> JAR = createBlock("jar_oak", () -> new Jar(Block.Properties.copy(Blocks.GLASS)));
    public static final RegistryObject<Block> QUARTZ_NETHERRACK = createBlock("quartz_netherrack", () -> new Block(Block.Properties.copy(Blocks.NETHER_QUARTZ_ORE)));
    public static final RegistryObject<Block> WAX_BLOCK = createBlock("wax_block", () -> new WaxBlock(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)));
    public static final RegistryObject<Block> GENE_INDEXER = createBlock("gene_indexer", () -> new GeneIndexer(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> BREEDING_CHAMBER = createBlock("breeding_chamber", () -> new BreedingChamber(Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> CRYO_STASIS = createBlock("cryo_stasis", () -> new CryoStasis(Block.Properties.copy(Blocks.CAULDRON)), false);

    public static final RegistryObject<Block> OAK_WOOD_NEST = createBlock("oak_wood_nest", () -> new WoodNest("#382b18", Block.Properties.copy(Blocks.OAK_LOG)));
    public static final RegistryObject<Block> SPRUCE_WOOD_NEST = createBlock("spruce_wood_nest", () -> new WoodNest("#2e1608", Block.Properties.copy(Blocks.SPRUCE_LOG)));
    public static final RegistryObject<Block> DARK_OAK_WOOD_NEST = createBlock("dark_oak_wood_nest", () -> new WoodNest("#292011", Block.Properties.copy(Blocks.DARK_OAK_LOG)));
    public static final RegistryObject<Block> BIRCH_WOOD_NEST = createBlock("birch_wood_nest", () -> new WoodNest("#36342a", Block.Properties.copy(Blocks.BIRCH_LOG)));
    public static final RegistryObject<Block> JUNGLE_WOOD_NEST = createBlock("jungle_wood_nest", () -> new WoodNest("#3e3013", Block.Properties.copy(Blocks.JUNGLE_LOG)));
    public static final RegistryObject<Block> ACACIA_WOOD_NEST = createBlock("acacia_wood_nest", () -> new WoodNest("#504b40", Block.Properties.copy(Blocks.ACACIA_LOG)));
    public static final RegistryObject<Block> CHERRY_WOOD_NEST = createBlock("cherry_wood_nest", () -> new WoodNest("#271620", Block.Properties.copy(Blocks.CHERRY_LOG)));
    public static final RegistryObject<Block> MANGROVE_WOOD_NEST = createBlock("mangrove_wood_nest", () -> new WoodNest("#443522", Block.Properties.copy(Blocks.MANGROVE_LOG)));

    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.copy(Blocks.BAMBOO_BLOCK)));
    public static final RegistryObject<Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.copy(Blocks.DRAGON_EGG)));
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.COARSE_DIRT)));
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SAND)));
    public static final RegistryObject<Block> SNOW_NEST = createBlock("snow_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SNOW_BLOCK).strength(0.2F)));
    public static final RegistryObject<Block> GRAVEL_NEST = createBlock("gravel_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.GRAVEL)));
    public static final RegistryObject<Block> SUGAR_CANE_NEST = createBlock("sugar_cane_nest", () -> new SugarCaneNest(Block.Properties.copy(Blocks.SUGAR_CANE)));
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SLIME_BLOCK)));
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.GLOWSTONE)));
    public static final RegistryObject<Block> SOUL_SAND_NEST = createBlock("soul_sand_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SOUL_SAND)));
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_QUARTZ_ORE)));
    public static final RegistryObject<Block> NETHER_GOLD_NEST = createBlock("nether_gold_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_GOLD_ORE)));
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_BRICKS)));
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.END_STONE)));
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.OBSIDIAN)));
    public static final RegistryObject<LiquidBlock> HONEY = createBlock("honey",
            () -> new HoneyFluidBlock(
                    ModFluids.HONEY,
                    Block.Properties.of().noCollission().strength(100.0F).noLootTable().mapColor(MapColor.TERRACOTTA_ORANGE).noCollission().replaceable().liquid().noLootTable().speedFactor(0.3F).jumpFactor(0.3F).friction(1.0f)
            ),
            false
    );

    public static final RegistryObject<Block> CONFIGURABLE_COMB = createBlock("configurable_comb", () -> new ConfigurableCombBlock(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK), "#c8df24"));

    public static final RegistryObject<Block> COMB_GHOSTLY = createBlock("comb_ghostly", () -> new TranslucentCombBlock(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK).noOcclusion().noCollission()));
    public static final RegistryObject<Block> COMB_MILKY = createBlock("comb_milky", () -> new Block(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)));
    public static final RegistryObject<Block> COMB_POWDERY = createBlock("comb_powdery", () -> new Block(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)));

    public static final RegistryObject<Block> BUMBLE_BEE_NEST = createBlock("bumble_bee_nest", () -> new BumbleBeeNest(Block.Properties.copy(Blocks.GRASS_BLOCK)));
    public static final RegistryObject<Block> SUGARBAG_NEST = createBlock("sugarbag_nest", () -> new SugarbagNest(Block.Properties.copy(Blocks.BEE_NEST)));
    public static final RegistryObject<Block> WARPED_BEE_NEST = createBlock("warped_bee_nest", () -> new NetherBeeNest(Block.Properties.copy(Blocks.BEE_NEST)));
    public static final RegistryObject<Block> CRIMSON_BEE_NEST = createBlock("crimson_bee_nest", () -> new NetherBeeNest(Block.Properties.copy(Blocks.BEE_NEST)));

    public static final RegistryObject<Block> AMBER = createBlock("amber", () -> new Amber(BlockBehaviour.Properties.copy(Blocks.ORANGE_STAINED_GLASS)));

    public static final RegistryObject<Block> PETRIFIED_HONEY = createBlock("petrified_honey", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.3F).noOcclusion().sound(SoundType.BONE_BLOCK)));
    public static final List<RegistryObject<Block>> PETRIFIED_HONEY_BLOCKS = Arrays.stream(DyeColor.values()).map(dyeColor -> {
        return createBlock(dyeColor.getSerializedName() + "_petrified_honey", () -> new Block(BlockBehaviour.Properties.of().mapColor(dyeColor.getMapColor()).strength(0.3F).noOcclusion().sound(SoundType.BONE_BLOCK)));
    }).toList();

    public static final Map<String, RegistryObject<? extends Block>> HIVES = new HashMap<>();
    public static final Map<String, RegistryObject<? extends Block>> EXPANSIONS = new HashMap<>();

    public static final Map<String, Map<String, HiveType>> HIVELIST = new HashMap<>()
    {{
        put(ProductiveBees.MODID, new HashMap<>()
        {{
            put("oak", new HiveType(false, "#c29d62", "oak", Ingredient.of(Items.OAK_PLANKS)));
            put("spruce", new HiveType(false, "#886539", "spruce", Ingredient.of(Items.SPRUCE_PLANKS)));
            put("birch", new HiveType(false, "#d7cb8d", "birch", Ingredient.of(Items.BIRCH_PLANKS)));
            put("jungle", new HiveType(false, "#b88764", "jungle", Ingredient.of(Items.JUNGLE_PLANKS)));
            put("acacia", new HiveType(false, "#c26d3f", "acacia", Ingredient.of(Items.ACACIA_PLANKS)));
            put("dark_oak", new HiveType(false, "#53381a", "dark_oak", Ingredient.of(Items.DARK_OAK_PLANKS)));
            put("crimson", new HiveType(false, "#924160", "crimson", Ingredient.of(Items.CRIMSON_PLANKS)));
            put("warped", new HiveType(false, "#279994", "warped", Ingredient.of(Items.WARPED_PLANKS)));
            put("mangrove", new HiveType(false, "#773934", "mangrove", Ingredient.of(Items.MANGROVE_PLANKS)));
            put("cherry", new HiveType(false, "#e6b3ad", "cherry", Ingredient.of(Items.CHERRY_PLANKS)));
            put("bamboo", new HiveType(false, "#e3cc6a", "bamboo", Ingredient.of(Items.BAMBOO_PLANKS)));
            put("snake_block", new HiveType(false, "#477566", "snake_block", Ingredient.of(Items.PRISMARINE_SHARD)));
        }});
        put("atmospheric", new HashMap<>()
        {{
            put("aspen", new HiveType(false, "#f7cf4c", "aspen", new FakeIngredient("atmospheric:aspen_planks")));
            put("grimwood", new HiveType(false, "#382721", "grimwood", new FakeIngredient("atmospheric:grimwood_planks")));
            put("kousa", new HiveType(false, "#84a083", "kousa", new FakeIngredient("atmospheric:kousa_planks")));
            put("morado", new HiveType(false, "#a64443", "mangrove", new FakeIngredient("atmospheric:morado_planks")));
            put("yucca", new HiveType(false, "#c17d6a", "yucca", new FakeIngredient("atmospheric:yucca_planks")));
            put("rosewood", new HiveType(false, "#956f7b", "rosewood", new FakeIngredient("atmospheric:rosewood_planks")));
        }});
        put("upgrade_aquatic", new HashMap<>()
        {{
            put("driftwood", new HiveType(false, "#82746b", "driftwood", new FakeIngredient("upgrade_aquatic:driftwood_planks")));
            put("river", new HiveType(false, "#8c6646", "river", new FakeIngredient("upgrade_aquatic:river_planks")));
        }});
        put("autumnity", new HashMap<>()
        {{
            put("maple", new HiveType(false, "#aa6e3d", "maple", new FakeIngredient("autumnity:maple_planks")));
        }});
        put("endergetic", new HashMap<>()
        {{
            put("poise", new HiveType(false, "#8f4b90", "oak", new FakeIngredient("endergetic:poise_planks")));
        }});
        put("environmental", new HashMap<>()
        {{
            put("wisteria", new HiveType(false, "#ebe8cc", "wisteria", new FakeIngredient("environmental:wisteria_planks")));
        }});
        put("quark", new HashMap<>()
        {{
            put("azalea", new HiveType(false, "#c1d368", "dark_oak", new FakeIngredient("quark:azalea_planks")));
            put("blossom", new HiveType(false, "#6b3324", "rosewood", new FakeIngredient("quark:blossom_planks")));
        }});
        put("byg", new HashMap<>()
        {{
            put("aspen", new HiveType(false, "#efcd7c", "aspen", new FakeIngredient("byg:aspen_planks")));
            put("baobab", new HiveType(false, "#b3995b", "acacia", new FakeIngredient("byg:baobab_planks")));
            put("blue_enchanted", new HiveType(false, "#6574bb", "magic", new FakeIngredient("byg:blue_enchanted_planks")));
            put("bulbis", new HiveType(false, "#893fb4", "warped", new FakeIngredient("byg:bulbis_planks")));
            put("cika", new HiveType(false, "#a65642", "driftwood", new FakeIngredient("byg:cika_planks")));
            put("cypress", new HiveType(false, "#aca272", "yucca", new FakeIngredient("byg:cypress_planks")));
            put("ebony", new HiveType(false, "#343232", "rosewood", new FakeIngredient("byg:ebony_planks")));
            put("embur", new HiveType(false, "#5d4b3c", "bamboo", new FakeIngredient("byg:embur_planks")));
            put("ether", new HiveType(false, "#0d79ab", "cherry", new FakeIngredient("byg:ether_planks")));
            put("fir", new HiveType(false, "#b28f6c", "fir", new FakeIngredient("byg:fir_planks")));
            put("green_enchanted", new HiveType(false, "#54a564", "magic", new FakeIngredient("byg:green_enchanted_planks")));
            put("holly", new HiveType(false, "#ddb4a2", "crimson", new FakeIngredient("byg:holly_planks")));
            put("imparius", new HiveType(false, "#77b1b5", "dead", new FakeIngredient("byg:imparius_planks")));
            put("jacaranda", new HiveType(false, "#c19b9d", "jacaranda", new FakeIngredient("byg:jacaranda_planks")));
            put("lament", new HiveType(false, "#574fa1", "jungle", new FakeIngredient("byg:lament_planks")));
            put("mahogany", new HiveType(false, "#9e6a88", "mahogany", new FakeIngredient("byg:mahogany_planks")));
            put("maple", new HiveType(false, "#978d83", "wisteria", new FakeIngredient("byg:maple_planks")));
            put("nightshade", new HiveType(false, "#d6781c", "umbran", new FakeIngredient("byg:nightshade_planks")));
            put("palm", new HiveType(false, "#a89b7a", "palm", new FakeIngredient("byg:palm_planks")));
            put("pine", new HiveType(false, "#c5b99a", "dark_oak", new FakeIngredient("byg:pine_planks")));
//            put("rainbow_eucalyptus", new HiveType(new FakeIngredient("byg:rainbow_eucalyptus_planks"))); TODO
            put("redwood", new HiveType(false, "#9c4141", "redwood", new FakeIngredient("byg:redwood_planks")));
            put("skyris", new HiveType(false, "#95bccb", "kousa", new FakeIngredient("byg:skyris_planks")));
            put("sythian", new HiveType(false, "#ccb251", "oak", new FakeIngredient("byg:sythian_planks")));
            put("white_mangrove", new HiveType(false, "#d1d1d1", "mangrove", new FakeIngredient("byg:white_mangrove_planks")));
            put("willow", new HiveType(false, "#55662d", "willow", new FakeIngredient("byg:willow_planks")));
            put("witch_hazel", new HiveType(false, "#3b8c5b", "hellbark", new FakeIngredient("byg:witch_hazel_planks")));
            put("zelkova", new HiveType(false, "#bf6d36", "birch", new FakeIngredient("byg:zelkova_planks")));
        }});
        put("biomesoplenty", new HashMap<>()
        {{
            put("fir", new HiveType(false, "#b3a78c", "fir", new FakeIngredient("biomesoplenty:fir_planks")));
            put("redwood", new HiveType(false, "#a5553a", "redwood", new FakeIngredient("biomesoplenty:redwood_planks")));
            put("mahogany", new HiveType(false, "#cf8987", "mahogany", new FakeIngredient("biomesoplenty:mahogany_planks")));
            put("jacaranda", new HiveType(false, "#dbbfb5", "jacaranda", new FakeIngredient("biomesoplenty:jacaranda_planks")));
            put("palm", new HiveType(false, "#d19445", "palm", new FakeIngredient("biomesoplenty:palm_planks")));
            put("willow", new HiveType(false, "#a2b084", "willow", new FakeIngredient("biomesoplenty:willow_planks")));
            put("dead", new HiveType(false, "#958e85", "dead", new FakeIngredient("biomesoplenty:dead_planks")));
            put("magic", new HiveType(false, "#537abf", "magic", new FakeIngredient("biomesoplenty:magic_planks")));
            put("umbran", new HiveType(false, "#7d6a8f", "umbran", new FakeIngredient("biomesoplenty:umbran_planks")));
            put("hellbark", new HiveType(false, "#3b3031", "hellbark", new FakeIngredient("biomesoplenty:hellbark_planks")));
        }});
        put("regions_unexplored", new HashMap<>()
        {{
            put("alpha_oak", new HiveType(false, "#bc9862", "fir", new FakeIngredient("regions_unexplored:alpha_oak_planks")));
            put("baobab", new HiveType(false, "#f8cfb4", "redwood", new FakeIngredient("regions_unexplored:baobab_planks")));
            put("blackwood", new HiveType(false, "#3d332c", "spruce", new FakeIngredient("regions_unexplored:blackwood_planks")));
            put("blue_bioshroom", new HiveType(false, "#82d9e7", "mahogany", new FakeIngredient("regions_unexplored:blue_bioshroom_planks")));
//            put("brimwood", new HiveType(new FakeIngredient("regions_unexplored:brimwood_planks"))); TODO
            put("cobalt", new HiveType(false, "#19317a", "jacaranda", new FakeIngredient("regions_unexplored:cobalt_planks")));
            put("cypress", new HiveType(false, "#929062", "yucca", new FakeIngredient("regions_unexplored:cypress_planks")));
            put("dead", new HiveType(false, "#786b66", "dead", new FakeIngredient("regions_unexplored:dead_planks")));
            put("eucalyptus", new HiveType(false, "#c38362", "willow", new FakeIngredient("regions_unexplored:eucalyptus_planks")));
            put("green_bioshroom", new HiveType(false, "#9ee487", "mahogany", new FakeIngredient("regions_unexplored:green_bioshroom_planks")));
            put("joshua", new HiveType(false, "#c0a183", "kousa", new FakeIngredient("regions_unexplored:joshua_planks")));
            put("kapok", new HiveType(false, "#b98c7b", "dark_oak", new FakeIngredient("regions_unexplored:kapok_planks")));
            put("larch", new HiveType(false, "#b98c7b", "mangrove", new FakeIngredient("regions_unexplored:larch_planks")));
            put("maple", new HiveType(false, "#c7a45e", "maple", new FakeIngredient("regions_unexplored:maple_planks")));
            put("palm", new HiveType(false, "#e5d09d", "palm", new FakeIngredient("regions_unexplored:palm_planks")));
            put("redwood", new HiveType(false, "#b75d45", "redwood", new FakeIngredient("regions_unexplored:redwood_planks")));
            put("socotra", new HiveType(false, "#b4915c", "magic", new FakeIngredient("regions_unexplored:socotra_planks")));
            put("magnolia", new HiveType(false, "#b74648", "umbran", new FakeIngredient("regions_unexplored:magnolia_planks")));
            put("mauve", new HiveType(false, "#8e6e97", "hellbark", new FakeIngredient("regions_unexplored:mauve_planks")));
            put("pine", new HiveType(false, "#dabb8f", "river", new FakeIngredient("regions_unexplored:pine_planks")));
            put("willow", new HiveType(false, "#c5bda6", "willow", new FakeIngredient("regions_unexplored:willow_planks")));
            put("pink_bioshroom", new HiveType(false, "#dd8dd9", "mahogany", new FakeIngredient("regions_unexplored:pink_bioshroom_planks")));
            put("yellow_bioshroom", new HiveType(false, "#e2cd89", "mahogany", new FakeIngredient("regions_unexplored:yellow_bioshroom_planks")));
        }});
    }};

    public static List<String> hiveStyles = new ArrayList<>() {{
        add("acacia");
        add("aspen");
        add("bamboo");
        add("birch");
        add("cherry");
        add("concrete");
        add("comb");
        add("crimson");
        add("dark_oak");
        add("dead");
        add("driftwood");
        add("fir");
        add("grimwood");
        add("hellbark");
        add("jacaranda");
        add("jungle");
        add("kousa");
        add("magic");
        add("mahogany");
        add("mangrove");
        add("maple");
        add("oak");
        add("palm");
        add("redwood");
        add("river");
        add("rosewood");
        add("snake_block");
        add("spruce");
        add("umbran");
        add("warped");
        add("willow");
        add("wisteria");
        add("yucca");
    }};

    public static void registerHives() {
        HIVELIST.forEach((modid, strings) -> {
            if (FMLLoader.getLaunchHandler().isData() || ModList.get().isLoaded(modid)) {
                strings.forEach((name, type) -> {
                    name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    String hiveName = "advanced_" + name + "_beehive";
                    String boxName = "expansion_box_" + name;
                    if (!HIVES.containsKey(hiveName)) {
                        HIVES.put(hiveName, createBlock(hiveName, () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE), registerBlockEntity(hiveName, () -> createBlockEntityType((pos, state) -> new AdvancedBeehiveBlockEntity((AdvancedBeehive) HIVES.get(hiveName).get(), pos, state), HIVES.get(hiveName).get()))), true));
                        EXPANSIONS.put(boxName, createBlock(boxName, () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE), registerBlockEntity(boxName, () -> createBlockEntityType((pos, state) -> new ExpansionBoxBlockEntity((ExpansionBox) EXPANSIONS.get(boxName).get(), pos, state), EXPANSIONS.get(boxName).get()))), true));
                    }
                });
            }
        });

        hiveStyles.forEach(style -> {
            String canvasHiveName = "advanced_" + style + "_canvas_beehive";
            HIVES.put(canvasHiveName, createBlock(canvasHiveName, () -> new CanvasBeehive(Block.Properties.copy(Blocks.BEEHIVE), registerBlockEntity(canvasHiveName, () -> createBlockEntityType((pos, state) -> new CanvasBeehiveBlockEntity((CanvasBeehive) HIVES.get(canvasHiveName).get(), pos, state), HIVES.get(canvasHiveName).get()))), true));
            String canvasBoxName = "expansion_box_" + style + "_canvas";
            EXPANSIONS.put(canvasBoxName, createBlock(canvasBoxName, () -> new CanvasExpansionBox(Block.Properties.copy(Blocks.BEEHIVE), registerBlockEntity(canvasBoxName, () -> createBlockEntityType((pos, state) -> new CanvasExpansionBoxBlockEntity((CanvasExpansionBox) EXPANSIONS.get(canvasBoxName).get(), pos, state), EXPANSIONS.get(canvasBoxName).get()))), true));
        });
    }

    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
        return ModBlockEntityTypes.BLOCK_ENTITIES.register(id, supplier);
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier) {
        return createBlock(name, supplier, true);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, boolean createItem) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);
        if (createItem) {
            Item.Properties properties = new Item.Properties();

            if (name.equals("configurable_comb")) {
                ModItems.CONFIGURABLE_COMB_BLOCK = ModItems.ITEMS.register(name, () -> new CombBlockItem(block.get(), properties));
            } else if (name.equals("jar_oak")) {
                ModItems.ITEMS.register(name, () -> new JarBlockItem(block.get(), properties));
            } else if (name.equals("amber")) {
                ModItems.ITEMS.register(name, () -> new AmberItem(block.get(), properties));
            } else {
                if (name.equals("comb_netherite")) {
                    properties.fireResistant();
                }
                ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
            }
        }
        return block;
    }
}
