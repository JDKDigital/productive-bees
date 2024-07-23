package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.*;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks
{
    public static final DeferredHolder<Block, ? extends Block> BOTTLER = createBlock("bottler", () -> new Bottler(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> CENTRIFUGE = createBlock("centrifuge", () -> new Centrifuge(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> POWERED_CENTRIFUGE = createBlock("powered_centrifuge", () -> new PoweredCentrifuge(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> HEATED_CENTRIFUGE = createBlock("heated_centrifuge", () -> new HeatedCentrifuge(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> HONEY_GENERATOR = createBlock("honey_generator", () -> new HoneyGenerator(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> CATCHER = createBlock("catcher", () -> new Catcher(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> INCUBATOR = createBlock("incubator", () -> new Incubator(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> INACTIVE_DRAGON_EGG = createBlock("inactive_dragon_egg", () -> new InactiveDragonEgg(Block.Properties.ofFullCopy(Blocks.DRAGON_EGG)));
    public static final DeferredHolder<Block, ? extends Block> INVISIBLE_REDSTONE_BLOCK = createBlock("invisible_redstone_block", () -> new InvisibleRedstone(Block.Properties.ofFullCopy(Blocks.REDSTONE_BLOCK).noOcclusion().noCollission()));
    public static final DeferredHolder<Block, ? extends Block> FEEDER = createBlock("feeder", () -> new Feeder(Block.Properties.ofFullCopy(Blocks.STONE_SLAB).noOcclusion()));
    public static final DeferredHolder<Block, ? extends Block> JAR = createBlock("jar_oak", () -> new Jar(Block.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, ? extends Block> QUARTZ_NETHERRACK = createBlock("quartz_netherrack", () -> new Block(Block.Properties.ofFullCopy(Blocks.NETHER_QUARTZ_ORE)));
    public static final DeferredHolder<Block, ? extends Block> WAX_BLOCK = createBlock("wax_block", () -> new WaxBlock(Block.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK)));
    public static final DeferredHolder<Block, ? extends Block> GENE_INDEXER = createBlock("gene_indexer", () -> new GeneIndexer(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> BREEDING_CHAMBER = createBlock("breeding_chamber", () -> new BreedingChamber(Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, ? extends Block> CRYO_STASIS = createBlock("cryo_stasis", () -> new CryoStasis(Block.Properties.ofFullCopy(Blocks.CAULDRON)), false);

    public static final DeferredHolder<Block, ? extends Block> OAK_WOOD_NEST = createBlock("oak_wood_nest", () -> new WoodNest("#382b18", Block.Properties.ofFullCopy(Blocks.OAK_LOG)));
    public static final DeferredHolder<Block, ? extends Block> SPRUCE_WOOD_NEST = createBlock("spruce_wood_nest", () -> new WoodNest("#2e1608", Block.Properties.ofFullCopy(Blocks.SPRUCE_LOG)));
    public static final DeferredHolder<Block, ? extends Block> DARK_OAK_WOOD_NEST = createBlock("dark_oak_wood_nest", () -> new WoodNest("#292011", Block.Properties.ofFullCopy(Blocks.DARK_OAK_LOG)));
    public static final DeferredHolder<Block, ? extends Block> BIRCH_WOOD_NEST = createBlock("birch_wood_nest", () -> new WoodNest("#36342a", Block.Properties.ofFullCopy(Blocks.BIRCH_LOG)));
    public static final DeferredHolder<Block, ? extends Block> JUNGLE_WOOD_NEST = createBlock("jungle_wood_nest", () -> new WoodNest("#3e3013", Block.Properties.ofFullCopy(Blocks.JUNGLE_LOG)));
    public static final DeferredHolder<Block, ? extends Block> ACACIA_WOOD_NEST = createBlock("acacia_wood_nest", () -> new WoodNest("#504b40", Block.Properties.ofFullCopy(Blocks.ACACIA_LOG)));
    public static final DeferredHolder<Block, ? extends Block> CHERRY_WOOD_NEST = createBlock("cherry_wood_nest", () -> new WoodNest("#271620", Block.Properties.ofFullCopy(Blocks.CHERRY_LOG)));
    public static final DeferredHolder<Block, ? extends Block> MANGROVE_WOOD_NEST = createBlock("mangrove_wood_nest", () -> new WoodNest("#443522", Block.Properties.ofFullCopy(Blocks.MANGROVE_LOG)));

    public static final DeferredHolder<Block, ? extends Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.ofFullCopy(Blocks.BAMBOO_BLOCK)));
    public static final DeferredHolder<Block, ? extends Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.ofFullCopy(Blocks.DRAGON_EGG)));
    public static final DeferredHolder<Block, ? extends Block> STONE_NEST = createBlock("stone_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.STONE)));
    public static final DeferredHolder<Block, ? extends Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.COARSE_DIRT)));
    public static final DeferredHolder<Block, ? extends Block> SAND_NEST = createBlock("sand_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.SAND)));
    public static final DeferredHolder<Block, ? extends Block> SNOW_NEST = createBlock("snow_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.SNOW_BLOCK).strength(0.2F)));
    public static final DeferredHolder<Block, ? extends Block> GRAVEL_NEST = createBlock("gravel_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.GRAVEL)));
    public static final DeferredHolder<Block, ? extends Block> SUGAR_CANE_NEST = createBlock("sugar_cane_nest", () -> new SugarCaneNest(Block.Properties.ofFullCopy(Blocks.SUGAR_CANE)));
    public static final DeferredHolder<Block, ? extends Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.SLIME_BLOCK)));
    public static final DeferredHolder<Block, ? extends Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.GLOWSTONE)));
    public static final DeferredHolder<Block, ? extends Block> SOUL_SAND_NEST = createBlock("soul_sand_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.SOUL_SAND)));
    public static final DeferredHolder<Block, ? extends Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.NETHER_QUARTZ_ORE)));
    public static final DeferredHolder<Block, ? extends Block> NETHER_GOLD_NEST = createBlock("nether_gold_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.NETHER_GOLD_ORE)));
    public static final DeferredHolder<Block, ? extends Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));
    public static final DeferredHolder<Block, ? extends Block> END_NEST = createBlock("end_stone_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.END_STONE)));
    public static final DeferredHolder<Block, ? extends Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new SolitaryNest(Block.Properties.ofFullCopy(Blocks.OBSIDIAN)));
    public static final DeferredHolder<Block, LiquidBlock> HONEY = createBlock("honey",
            () -> new HoneyFluidBlock(
                    ModFluids.HONEY,
                    Block.Properties.of().noCollission().strength(100.0F).noLootTable().mapColor(MapColor.TERRACOTTA_ORANGE).noCollission().replaceable().liquid().noLootTable().speedFactor(0.3F).jumpFactor(0.3F).friction(1.0f)
            ),
            false
    );

    public static final DeferredHolder<Block, ? extends Block> CONFIGURABLE_COMB = createBlock("configurable_comb", () -> new ConfigurableCombBlock(Block.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK), "#c8df24"));

    public static final DeferredHolder<Block, ? extends Block> COMB_GHOSTLY = createBlock("comb_ghostly", () -> new TranslucentCombBlock(Block.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK).noCollission()));
    public static final DeferredHolder<Block, ? extends Block> COMB_MILKY = createBlock("comb_milky", () -> new Block(Block.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK)));
    public static final DeferredHolder<Block, ? extends Block> COMB_POWDERY = createBlock("comb_powdery", () -> new Block(Block.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK)));

    public static final DeferredHolder<Block, ? extends Block> BUMBLE_BEE_NEST = createBlock("bumble_bee_nest", () -> new BumbleBeeNest(Block.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    public static final DeferredHolder<Block, ? extends Block> SUGARBAG_NEST = createBlock("sugarbag_nest", () -> new SugarbagNest(Block.Properties.ofFullCopy(Blocks.BEE_NEST)));
    public static final DeferredHolder<Block, ? extends Block> WARPED_BEE_NEST = createBlock("warped_bee_nest", () -> new NetherBeeNest(Block.Properties.ofFullCopy(Blocks.BEE_NEST)));
    public static final DeferredHolder<Block, ? extends Block> CRIMSON_BEE_NEST = createBlock("crimson_bee_nest", () -> new NetherBeeNest(Block.Properties.ofFullCopy(Blocks.BEE_NEST)));

    public static final DeferredHolder<Block, ? extends Block> AMBER = createBlock("amber", () -> new Amber(Block.Properties.ofFullCopy(Blocks.ORANGE_STAINED_GLASS)));

    public static final DeferredHolder<Block, ? extends Block> PETRIFIED_HONEY = createBlock("petrified_honey", () -> new Block(Block.Properties.of().mapColor(MapColor.TERRACOTTA_ORANGE).strength(0.3F).noOcclusion().sound(SoundType.BONE_BLOCK)));
    public static final List<DeferredHolder<Block, Block>> PETRIFIED_HONEY_BLOCKS = Arrays.stream(DyeColor.values()).map(dyeColor -> {
        return createBlock(dyeColor.getSerializedName() + "_petrified_honey", () -> new Block(Block.Properties.of().mapColor(dyeColor.getMapColor()).strength(0.3F).noOcclusion().sound(SoundType.BONE_BLOCK)));
    }).toList();

    public static final Map<String, DeferredHolder<Block, ? extends Block>> HIVES = new HashMap<>();
    public static final Map<String, DeferredHolder<Block, ? extends Block>> EXPANSIONS = new HashMap<>();
    public static final Map<String, DeferredHolder<Block, ? extends Block>> CANVAS_HIVES = new HashMap<>();
    public static final Map<String, DeferredHolder<Block, ? extends Block>> CANVAS_EXPANSIONS = new HashMap<>();

    public static final Map<String, Map<String, HiveType>> HIVELIST = new HashMap<>()
    {{
        put(ProductiveBees.MODID, new HashMap<>()
        {{
            put("oak", new HiveType(false, "#c29d62", "oak", Blocks.OAK_PLANKS, null));
            put("spruce", new HiveType(false, "#886539", "spruce", Blocks.SPRUCE_PLANKS, null));
            put("birch", new HiveType(false, "#d7cb8d", "birch", Blocks.BIRCH_PLANKS, null));
            put("jungle", new HiveType(false, "#b88764", "jungle", Blocks.JUNGLE_PLANKS, null));
            put("acacia", new HiveType(false, "#c26d3f", "acacia", Blocks.ACACIA_PLANKS, null));
            put("dark_oak", new HiveType(false, "#53381a", "dark_oak", Blocks.DARK_OAK_PLANKS, null));
            put("crimson", new HiveType(false, "#924160", "crimson", Blocks.CRIMSON_PLANKS, null));
            put("warped", new HiveType(false, "#279994", "warped", Blocks.WARPED_PLANKS, null));
            put("mangrove", new HiveType(false, "#773934", "mangrove", Blocks.MANGROVE_PLANKS, null));
            put("cherry", new HiveType(false, "#e6b3ad", "cherry", Blocks.CHERRY_PLANKS, null));
            put("bamboo", new HiveType(false, "#e3cc6a", "bamboo", Blocks.BAMBOO_PLANKS, null));
            put("snake_block", new HiveType(false, "#477566", "snake_block", Items.PRISMARINE_SHARD, null));
        }});
        put("atmospheric", new HashMap<>()
        {{
            put("aspen", new HiveType(false, "#f7cf4c", "aspen", null, new FakeIngredient("atmospheric:aspen_planks")));
            put("grimwood", new HiveType(false, "#382721", "grimwood", null, new FakeIngredient("atmospheric:grimwood_planks")));
            put("kousa", new HiveType(false, "#84a083", "kousa", null, new FakeIngredient("atmospheric:kousa_planks")));
            put("morado", new HiveType(false, "#a64443", "mangrove", null, new FakeIngredient("atmospheric:morado_planks")));
            put("yucca", new HiveType(false, "#c17d6a", "yucca", null, new FakeIngredient("atmospheric:yucca_planks")));
            put("rosewood", new HiveType(false, "#956f7b", "rosewood", null, new FakeIngredient("atmospheric:rosewood_planks")));
        }});
        put("upgrade_aquatic", new HashMap<>()
        {{
            put("driftwood", new HiveType(false, "#82746b", "driftwood", null, new FakeIngredient("upgrade_aquatic:driftwood_planks")));
            put("river", new HiveType(false, "#8c6646", "river", null, new FakeIngredient("upgrade_aquatic:river_planks")));
        }});
        put("autumnity", new HashMap<>()
        {{
            put("maple", new HiveType(false, "#aa6e3d", "maple", null, new FakeIngredient("autumnity:maple_planks")));
        }});
        put("endergetic", new HashMap<>()
        {{
            put("poise", new HiveType(false, "#8f4b90", "oak", null, new FakeIngredient("endergetic:poise_planks")));
        }});
        put("environmental", new HashMap<>()
        {{
            put("wisteria", new HiveType(false, "#ebe8cc", "wisteria", null, new FakeIngredient("environmental:wisteria_planks")));
        }});
        put("quark", new HashMap<>()
        {{
            put("azalea", new HiveType(false, "#c1d368", "dark_oak", null, new FakeIngredient("quark:azalea_planks")));
            put("blossom", new HiveType(false, "#6b3324", "rosewood", null, new FakeIngredient("quark:blossom_planks")));
        }});
        put("byg", new HashMap<>()
        {{
            put("aspen", new HiveType(false, "#efcd7c", "aspen", null, new FakeIngredient("byg:aspen_planks")));
            put("baobab", new HiveType(false, "#b3995b", "acacia", null, new FakeIngredient("byg:baobab_planks")));
            put("blue_enchanted", new HiveType(false, "#6574bb", "magic", null, new FakeIngredient("byg:blue_enchanted_planks")));
            put("bulbis", new HiveType(false, "#893fb4", "warped", null, new FakeIngredient("byg:bulbis_planks")));
            put("cika", new HiveType(false, "#a65642", "driftwood", null, new FakeIngredient("byg:cika_planks")));
            put("cypress", new HiveType(false, "#aca272", "yucca", null, new FakeIngredient("byg:cypress_planks")));
            put("ebony", new HiveType(false, "#343232", "rosewood", null, new FakeIngredient("byg:ebony_planks")));
            put("embur", new HiveType(false, "#5d4b3c", "bamboo", null, new FakeIngredient("byg:embur_planks")));
            put("ether", new HiveType(false, "#0d79ab", "cherry", null, new FakeIngredient("byg:ether_planks")));
            put("fir", new HiveType(false, "#b28f6c", "fir", null, new FakeIngredient("byg:fir_planks")));
            put("green_enchanted", new HiveType(false, "#54a564", "magic", null, new FakeIngredient("byg:green_enchanted_planks")));
            put("holly", new HiveType(false, "#ddb4a2", "crimson", null, new FakeIngredient("byg:holly_planks")));
            put("imparius", new HiveType(false, "#77b1b5", "dead", null, new FakeIngredient("byg:imparius_planks")));
            put("jacaranda", new HiveType(false, "#c19b9d", "jacaranda", null, new FakeIngredient("byg:jacaranda_planks")));
            put("lament", new HiveType(false, "#574fa1", "jungle", null, new FakeIngredient("byg:lament_planks")));
            put("mahogany", new HiveType(false, "#9e6a88", "mahogany", null, new FakeIngredient("byg:mahogany_planks")));
            put("maple", new HiveType(false, "#978d83", "wisteria", null, new FakeIngredient("byg:maple_planks")));
            put("nightshade", new HiveType(false, "#d6781c", "umbran", null, new FakeIngredient("byg:nightshade_planks")));
            put("palm", new HiveType(false, "#a89b7a", "palm", null, new FakeIngredient("byg:palm_planks")));
            put("pine", new HiveType(false, "#c5b99a", "dark_oak", null, new FakeIngredient("byg:pine_planks")));
//            put("rainbow_eucalyptus", new HiveType(new FakeIngredient("byg:rainbow_eucalyptus_planks"))); TODO
            put("redwood", new HiveType(false, "#9c4141", "redwood", null, new FakeIngredient("byg:redwood_planks")));
            put("skyris", new HiveType(false, "#95bccb", "kousa", null, new FakeIngredient("byg:skyris_planks")));
            put("sythian", new HiveType(false, "#ccb251", "oak", null, new FakeIngredient("byg:sythian_planks")));
            put("white_mangrove", new HiveType(false, "#d1d1d1", "mangrove", null, new FakeIngredient("byg:white_mangrove_planks")));
            put("willow", new HiveType(false, "#55662d", "willow", null, new FakeIngredient("byg:willow_planks")));
            put("witch_hazel", new HiveType(false, "#3b8c5b", "hellbark", null, new FakeIngredient("byg:witch_hazel_planks")));
            put("zelkova", new HiveType(false, "#bf6d36", "birch", null, new FakeIngredient("byg:zelkova_planks")));
        }});
        put("biomesoplenty", new HashMap<>()
        {{
            put("fir", new HiveType(false, "#b3a78c", "fir", null, new FakeIngredient("biomesoplenty:fir_planks")));
            put("redwood", new HiveType(false, "#a5553a", "redwood", null, new FakeIngredient("biomesoplenty:redwood_planks")));
            put("mahogany", new HiveType(false, "#cf8987", "mahogany", null, new FakeIngredient("biomesoplenty:mahogany_planks")));
            put("jacaranda", new HiveType(false, "#dbbfb5", "jacaranda", null,  new FakeIngredient("biomesoplenty:jacaranda_planks")));
            put("palm", new HiveType(false, "#d19445", "palm", null,  new FakeIngredient("biomesoplenty:palm_planks")));
            put("willow", new HiveType(false, "#a2b084", "willow", null,  new FakeIngredient("biomesoplenty:willow_planks")));
            put("dead", new HiveType(false, "#958e85", "dead", null,  new FakeIngredient("biomesoplenty:dead_planks")));
            put("magic", new HiveType(false, "#537abf", "magic", null,  new FakeIngredient("biomesoplenty:magic_planks")));
            put("umbran", new HiveType(false, "#7d6a8f", "umbran", null,  new FakeIngredient("biomesoplenty:umbran_planks")));
            put("hellbark", new HiveType(false, "#3b3031", "hellbark", null,  new FakeIngredient("biomesoplenty:hellbark_planks")));
        }});
        put("regions_unexplored", new HashMap<>()
        {{
            put("baobab", new HiveType(false, "#f8cfb4", "redwood", null,  new FakeIngredient("regions_unexplored:baobab_planks")));
            put("blackwood", new HiveType(false, "#3d332c", "spruce", null,  new FakeIngredient("regions_unexplored:blackwood_planks")));
            put("blue_bioshroom", new HiveType(false, "#82d9e7", "mahogany", null,  new FakeIngredient("regions_unexplored:blue_bioshroom_planks")));
            put("cobalt", new HiveType(false, "#19317a", "jacaranda", null,  new FakeIngredient("regions_unexplored:cobalt_planks")));
            put("cypress", new HiveType(false, "#929062", "yucca", null,  new FakeIngredient("regions_unexplored:cypress_planks")));
            put("dead", new HiveType(false, "#786b66", "dead", null,  new FakeIngredient("regions_unexplored:dead_planks")));
            put("eucalyptus", new HiveType(false, "#c38362", "willow", null,  new FakeIngredient("regions_unexplored:eucalyptus_planks")));
            put("green_bioshroom", new HiveType(false, "#9ee487", "mahogany", null,  new FakeIngredient("regions_unexplored:green_bioshroom_planks")));
            put("joshua", new HiveType(false, "#c0a183", "kousa", null,  new FakeIngredient("regions_unexplored:joshua_planks")));
            put("kapok", new HiveType(false, "#b98c7b", "dark_oak", null,  new FakeIngredient("regions_unexplored:kapok_planks")));
            put("larch", new HiveType(false, "#b98c7b", "mangrove", null,  new FakeIngredient("regions_unexplored:larch_planks")));
            put("maple", new HiveType(false, "#c7a45e", "maple", null,  new FakeIngredient("regions_unexplored:maple_planks")));
            put("palm", new HiveType(false, "#e5d09d", "palm", null,  new FakeIngredient("regions_unexplored:palm_planks")));
            put("redwood", new HiveType(false, "#b75d45", "redwood", null,  new FakeIngredient("regions_unexplored:redwood_planks")));
            put("socotra", new HiveType(false, "#b4915c", "magic", null,  new FakeIngredient("regions_unexplored:socotra_planks")));
            put("magnolia", new HiveType(false, "#b74648", "umbran", null,  new FakeIngredient("regions_unexplored:magnolia_planks")));
            put("mauve", new HiveType(false, "#8e6e97", "hellbark", null,  new FakeIngredient("regions_unexplored:mauve_planks")));
            put("pine", new HiveType(false, "#dabb8f", "river", null,  new FakeIngredient("regions_unexplored:pine_planks")));
            put("willow", new HiveType(false, "#c5bda6", "willow", null,  new FakeIngredient("regions_unexplored:willow_planks")));
            put("pink_bioshroom", new HiveType(false, "#dd8dd9", "mahogany", null,  new FakeIngredient("regions_unexplored:pink_bioshroom_planks")));
            put("yellow_bioshroom", new HiveType(false, "#e2cd89", "mahogany", null,  new FakeIngredient("regions_unexplored:yellow_bioshroom_planks")));
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
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    String hiveName = "advanced_" + name + "_beehive";
                    String boxName = "expansion_box_" + name;
                    if (!HIVES.containsKey(hiveName)) {
                        var properties = type.planks() instanceof Block plankBlock ? Block.Properties.ofFullCopy(plankBlock).instrument(NoteBlockInstrument.BASS).strength(0.6F) : Block.Properties.ofFullCopy(Blocks.BEEHIVE);
                        HIVES.put(hiveName, createBlock(hiveName, () -> new AdvancedBeehive(properties), true));
                        EXPANSIONS.put(boxName, createBlock(boxName, () -> new ExpansionBox(properties), true));
                    }
                });
            }
        });

        hiveStyles.forEach(style -> {
            String canvasHiveName = "advanced_" + style + "_canvas_beehive";
            var hiveBlock = createBlock(canvasHiveName, () -> new CanvasBeehive(Block.Properties.ofFullCopy(Blocks.BEEHIVE)), true);
            HIVES.put(canvasHiveName, hiveBlock);
            CANVAS_HIVES.put(canvasHiveName, hiveBlock);
            String canvasBoxName = "expansion_box_" + style + "_canvas";
            var boxBlock = createBlock(canvasBoxName, () -> new CanvasExpansionBox(Block.Properties.ofFullCopy(Blocks.BEEHIVE)), true);
            EXPANSIONS.put(canvasBoxName, boxBlock);
            CANVAS_EXPANSIONS.put(canvasBoxName, boxBlock);
        });
        
        ModBlockEntityTypes.registerHiveBlockEntities();
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }

    public static <B extends Block> DeferredHolder<Block, B> createBlock(String name, Supplier<? extends B> supplier) {
        return createBlock(name, supplier, true);
    }

    public static <B extends Block> DeferredHolder<Block, B> createBlock(String name, Supplier<? extends B> supplier, boolean createItem) {
        DeferredHolder<Block, B> block = ProductiveBees.BLOCKS.register(name, supplier);
        if (createItem) {
            Item.Properties properties = new Item.Properties();

            if (name.equals("configurable_comb")) {
                ModItems.CONFIGURABLE_COMB_BLOCK = ProductiveBees.ITEMS.register(name, () -> new CombBlockItem(block.get(), properties));
            } else if (name.equals("jar_oak")) {
                ProductiveBees.ITEMS.register(name, () -> new JarBlockItem(block.get(), properties));
            } else if (name.equals("amber")) {
                ProductiveBees.ITEMS.register(name, () -> new AmberItem(block.get(), properties));
            } else {
                if (name.equals("comb_netherite")) {
                    properties.fireResistant();
                }
                ProductiveBees.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
            }
        }
        return block;
    }
}
