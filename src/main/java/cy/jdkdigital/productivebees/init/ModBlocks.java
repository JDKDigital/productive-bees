package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.*;
import cy.jdkdigital.productivebees.common.block.nest.BumbleBeeNest;
import cy.jdkdigital.productivebees.common.block.nest.SugarCaneNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

    public static final RegistryObject<Block> BOTTLER = createBlock("bottler", () -> new Bottler(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> CENTRIFUGE = createBlock("centrifuge", () -> new Centrifuge(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> POWERED_CENTRIFUGE = createBlock("powered_centrifuge", () -> new PoweredCentrifuge(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> HONEY_GENERATOR = createBlock("honey_generator", () -> new HoneyGenerator(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> CATCHER = createBlock("catcher", () -> new Catcher(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INCUBATOR = createBlock("incubator", () -> new Incubator(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INACTIVE_DRAGON_EGG = createBlock("inactive_dragon_egg", () -> new InactiveDragonEgg(Block.Properties.copy(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INVISIBLE_REDSTONE_BLOCK = createBlock("invisible_redstone_block", () -> new InvisibleRedstone(Block.Properties.copy(Blocks.REDSTONE_BLOCK).noOcclusion().noCollission()), null);
    public static final RegistryObject<Block> FEEDER = createBlock("feeder", () -> new Feeder(Block.Properties.copy(Blocks.STONE_SLAB).noOcclusion()), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> JAR = createBlock("jar_oak", () -> new Jar(Block.Properties.copy(Blocks.GLASS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> QUARTZ_NETHERRACK = createBlock("quartz_netherrack", () -> new Block(Block.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> WAX_BLOCK = createBlock("wax_block", () -> new Block(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GENE_INDEXER = createBlock("gene_indexer", () -> new GeneIndexer(Block.Properties.copy(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> OAK_WOOD_NEST = createBlock("oak_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SPRUCE_WOOD_NEST = createBlock("spruce_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DARK_OAK_WOOD_NEST = createBlock("dark_oak_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> BIRCH_WOOD_NEST = createBlock("birch_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> JUNGLE_WOOD_NEST = createBlock("jungle_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ACACIA_WOOD_NEST = createBlock("acacia_wood_nest", () -> new WoodNest(Block.Properties.copy(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.of(Material.DECORATION, MaterialColor.SAND).sound(SoundType.SCAFFOLDING).strength(0.3F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.copy(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.COARSE_DIRT)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SNOW_NEST = createBlock("snow_nest", () -> new SolitaryNest(Block.Properties.of(Material.SAND).strength(0.2F).sound(SoundType.SNOW)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GRAVEL_NEST = createBlock("gravel_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.GRAVEL)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SUGAR_CANE_NEST = createBlock("sugar_cane_nest", () -> new SugarCaneNest(Block.Properties.copy(Blocks.SUGAR_CANE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SLIME_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.GLOWSTONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SOUL_SAND_NEST = createBlock("soul_sand_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.SOUL_SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_GOLD_NEST = createBlock("nether_gold_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_GOLD_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.NETHER_BRICKS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.END_STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new SolitaryNest(Block.Properties.copy(Blocks.OBSIDIAN)), ModItemGroups.PRODUCTIVE_BEES);
//    public static final RegistryObject<LiquidBlock> HONEY = createBlock("honey",
//            () -> new HoneyFluidBlock(
//                    ModFluids.HONEY,
//                    Block.Properties.of(ModFluids.MATERIAL_HONEY).noCollission().strength(100.0F).noDrops().speedFactor(0.3F)
//            ),
//            ModItemGroups.PRODUCTIVE_BEES,
//            false
//    );

    public static final RegistryObject<Block> CONFIGURABLE_COMB = createBlock("configurable_comb", () -> new ConfigurableCombBlock(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK), "#c8df24"), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> COMB_GHOSTLY = createBlock("comb_ghostly", () -> new TranslucentCombBlock(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK).noOcclusion().noCollission()), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MILKY = createBlock("comb_milky", () -> new Block(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_POWDERY = createBlock("comb_powdery", () -> new Block(Block.Properties.copy(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> BUMBLE_BEE_NEST = createBlock("bumble_bee_nest", () -> new BumbleBeeNest(Block.Properties.copy(Blocks.GRASS_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SUGARBAG_NEST = createBlock("sugarbag_nest", () -> new SugarbagNest(Block.Properties.copy(Blocks.BEE_NEST)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_OAK_BEEHIVE = createBlock("advanced_oak_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_OAK = createBlock("expansion_box_oak", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> ADVANCED_SPRUCE_BEEHIVE = createBlock("advanced_spruce_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BIRCH_BEEHIVE = createBlock("advanced_birch_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_JUNGLE_BEEHIVE = createBlock("advanced_jungle_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ACACIA_BEEHIVE = createBlock("advanced_acacia_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DARK_OAK_BEEHIVE = createBlock("advanced_dark_oak_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_CRIMSON_BEEHIVE = createBlock("advanced_crimson_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WARPED_BEEHIVE = createBlock("advanced_warped_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_SNAKE_BLOCK_BEEHIVE = createBlock("advanced_snake_block_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> ADVANCED_ROSEWOOD_BEEHIVE = createBlockCompat("atmospheric", "advanced_rosewood_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_YUCCA_BEEHIVE = createBlockCompat("atmospheric", "advanced_yucca_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_KOUSA_BEEHIVE = createBlockCompat("atmospheric", "advanced_kousa_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ASPEN_BEEHIVE = createBlockCompat("atmospheric,byg", "advanced_aspen_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_GRIMWOOD_BEEHIVE = createBlockCompat("atmospheric", "advanced_grimwood_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WILLOW_BEEHIVE = createBlockCompat("swampexpansion,byg", "advanced_willow_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WISTERIA_BEEHIVE = createBlockCompat("environmental", "advanced_wisteria_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BAMBOO_BEEHIVE = createBlockCompat("bamboo_blocks", "advanced_bamboo_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_MAPLE_BEEHIVE = createBlockCompat("autumnity,byg", "advanced_maple_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DRIFTWOOD_BEEHIVE = createBlockCompat("upgrade_aquatic", "advanced_driftwood_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_RIVER_BEEHIVE = createBlockCompat("upgrade_aquatic", "advanced_river_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_POISE_BEEHIVE = createBlockCompat("endergetic", "advanced_poise_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_FIR_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_fir_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_DEAD_BEEHIVE = createBlockCompat("biomesoplenty", "advanced_bop_dead_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_PALM_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_palm_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAGIC_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_magic_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_CHERRY_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_cherry_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_UMBRAN_BEEHIVE = createBlockCompat("biomesoplenty", "advanced_bop_umbran_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_WILLOW_BEEHIVE = createBlockCompat("biomesoplenty", "advanced_bop_willow_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_REDWOOD_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_redwood_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_HELLBARK_BEEHIVE = createBlockCompat("biomesoplenty", "advanced_bop_hellbark_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAHOGANY_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_mahogany_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_JACARANDA_BEEHIVE = createBlockCompat("biomesoplenty,byg", "advanced_bop_jacaranda_beehive", () -> new AdvancedBeehive(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> EXPANSION_BOX_SPRUCE = createBlock("expansion_box_spruce", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BIRCH = createBlock("expansion_box_birch", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_JUNGLE = createBlock("expansion_box_jungle", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ACACIA = createBlock("expansion_box_acacia", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DARK_OAK = createBlock("expansion_box_dark_oak", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_CRIMSON = createBlock("expansion_box_crimson", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WARPED = createBlock("expansion_box_warped", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_SNAKE_BLOCK = createBlock("expansion_box_snake_block", () -> new ExpansionBox(Block.Properties.copy(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> EXPANSION_BOX_ROSEWOOD = createBlockCompat("atmospheric", "expansion_box_rosewood", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_YUCCA = createBlockCompat("atmospheric", "expansion_box_yucca", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_KOUSA = createBlockCompat("atmospheric", "expansion_box_kousa", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ASPEN = createBlockCompat("atmospheric,byg", "expansion_box_aspen", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_GRIMWOOD = createBlockCompat("atmospheric", "expansion_box_grimwood", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WILLOW = createBlockCompat("swampexpansion,byg", "expansion_box_willow", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WISTERIA = createBlockCompat("bloomful", "expansion_box_wisteria", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BAMBOO = createBlockCompat("bamboo_blocks", "expansion_box_bamboo", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_MAPLE = createBlockCompat("autumnity,byg", "expansion_box_maple", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DRIFTWOOD = createBlockCompat("upgrade_aquatic", "expansion_box_driftwood", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_RIVER = createBlockCompat("upgrade_aquatic", "expansion_box_river", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_POISE = createBlockCompat("endergetic", "expansion_box_poise", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_FIR = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_fir", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_DEAD = createBlockCompat("biomesoplenty", "expansion_box_bop_dead", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_PALM = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_palm", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAGIC = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_magic", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_CHERRY = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_cherry", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_UMBRAN = createBlockCompat("biomesoplenty", "expansion_box_bop_umbran", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_WILLOW = createBlockCompat("biomesoplenty", "expansion_box_bop_willow", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_REDWOOD = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_redwood", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_HELLBARK = createBlockCompat("biomesoplenty", "expansion_box_bop_hellbark", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAHOGANY = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_mahogany", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_JACARANDA = createBlockCompat("biomesoplenty,byg", "expansion_box_bop_jacaranda", () -> new ExpansionBox(Block.Properties.copy(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mods, String name, Supplier<? extends B> supplier, CreativeModeTab itemGroup) {
        return createBlockCompat(mods, name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mods, String name, Supplier<? extends B> supplier, CreativeModeTab itemGroup, boolean createItem) {
        String[] modNames = mods.split(",");
        CreativeModeTab group = itemGroup != null && ModList.get().isLoaded(modNames[0]) || (modNames.length > 1 && ModList.get().isLoaded(modNames[1])) ? itemGroup : null;
        return createBlock(name, supplier, group, createItem);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, CreativeModeTab itemGroup) {
        return createBlock(name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, CreativeModeTab itemGroup, boolean createItem) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);
        if (createItem) {
            Item.Properties properties = new Item.Properties().tab(itemGroup);

            if (name.equals("configurable_comb")) {
                ModItems.CONFIGURABLE_COMB_BLOCK = ModItems.ITEMS.register(name, () -> new CombBlockItem(block.get(), properties));
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
