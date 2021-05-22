package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.*;
import cy.jdkdigital.productivebees.common.block.nest.BumbleBeeNest;
import cy.jdkdigital.productivebees.common.block.nest.SugarCaneNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.item.CombBlockItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ProductiveBees.MODID);

    public static final RegistryObject<Block> BOTTLER = createBlock("bottler", () -> new Bottler(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> CENTRIFUGE = createBlock("centrifuge", () -> new Centrifuge(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> POWERED_CENTRIFUGE = createBlock("powered_centrifuge", () -> new PoweredCentrifuge(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> HONEY_GENERATOR = createBlock("honey_generator", () -> new HoneyGenerator(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> CATCHER = createBlock("catcher", () -> new Catcher(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INCUBATOR = createBlock("incubator", () -> new Incubator(Block.Properties.from(Blocks.CAULDRON)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INACTIVE_DRAGON_EGG = createBlock("inactive_dragon_egg", () -> new InactiveDragonEgg(Block.Properties.from(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> INVISIBLE_REDSTONE_BLOCK = createBlock("invisible_redstone_block", () -> new InvisibleRedstone(Block.Properties.from(Blocks.REDSTONE_BLOCK).notSolid().doesNotBlockMovement()), null);
    public static final RegistryObject<Block> FEEDER = createBlock("feeder", () -> new Feeder(Block.Properties.from(Blocks.STONE_SLAB).notSolid()), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> JAR = createBlock("jar_oak", () -> new Jar(Block.Properties.from(Blocks.GLASS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> QUARTZ_NETHERRACK = createBlock("quartz_netherrack", () -> new Block(Block.Properties.from(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> PURPLE_HOPPER = createBlock("purple_hopper", () -> new HopperBlock(Block.Properties.from(Blocks.HOPPER)), null);

    public static final RegistryObject<Block> OAK_WOOD_NEST = createBlock("oak_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SPRUCE_WOOD_NEST = createBlock("spruce_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DARK_OAK_WOOD_NEST = createBlock("dark_oak_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> BIRCH_WOOD_NEST = createBlock("birch_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> JUNGLE_WOOD_NEST = createBlock("jungle_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ACACIA_WOOD_NEST = createBlock("acacia_wood_nest", () -> new WoodNest(Block.Properties.from(Blocks.OAK_LOG)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> BAMBOO_HIVE = createBlock("bamboo_hive", () -> new BambooHive(Block.Properties.from(Blocks.SCAFFOLDING).hardnessAndResistance(0.3F)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> DRAGON_EGG_HIVE = createBlock("dragon_egg_hive", () -> new DragonEggHive(Block.Properties.from(Blocks.DRAGON_EGG)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> STONE_NEST = createBlock("stone_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COARSE_DIRT_NEST = createBlock("coarse_dirt_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.COARSE_DIRT)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SAND_NEST = createBlock("sand_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SNOW_NEST = createBlock("snow_nest", () -> new SolitaryNest(Block.Properties.create(Material.SAND).hardnessAndResistance(0.2F).sound(SoundType.SNOW)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GRAVEL_NEST = createBlock("gravel_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.GRAVEL)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SUGAR_CANE_NEST = createBlock("sugar_cane_nest", () -> new SugarCaneNest(Block.Properties.from(Blocks.SUGAR_CANE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SLIMY_NEST = createBlock("slimy_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.SLIME_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> GLOWSTONE_NEST = createBlock("glowstone_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.GLOWSTONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SOUL_SAND_NEST = createBlock("soul_sand_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.SOUL_SAND)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_QUARTZ_NEST = createBlock("nether_quartz_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_GOLD_NEST = createBlock("nether_gold_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.NETHER_QUARTZ_ORE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> NETHER_BRICK_NEST = createBlock("nether_brick_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.NETHER_BRICKS)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> END_NEST = createBlock("end_stone_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.END_STONE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> OBSIDIAN_PILLAR_NEST = createBlock("obsidian_nest", () -> new SolitaryNest(Block.Properties.from(Blocks.OBSIDIAN)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<FlowingFluidBlock> HONEY = createBlock("honey",
            () -> new HoneyFluidBlock(
                    ModFluids.HONEY,
                    Block.Properties.create(ModFluids.MATERIAL_HONEY).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops().speedFactor(0.3F)
            ),
            ModItemGroups.PRODUCTIVE_BEES,
            false
    );

    public static final RegistryObject<Block> CONFIGURABLE_COMB = createBlock("configurable_comb", () -> new ConfigurableCombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#c8df24"), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> COMB_ALFSTEEL = createBlockCompat("mythicbotany", "comb_alfsteel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ffd238"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ALLTHEMODIUM = createBlockCompat("allthemodium", "comb_allthemodium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#f2f24f"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_AMBER = createBlock("comb_amber", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#d2ab00"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BASALZ = createBlockCompat("thermal", "comb_basalz", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ff8219"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BAUXITE = createBlock("comb_bauxite", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BISMUTH = createBlock("comb_bismuth", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ece386"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BLAZING = createBlock("comb_blazing", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BLITZ = createBlockCompat("thermal", "comb_blitz", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#e9edf3"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BLIZZ = createBlockCompat("thermal", "comb_blizz", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#1d7cf1"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BLOODY = createBlockCompat("bloodmagic", "comb_bloody", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#7a0300"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BONE = createBlock("comb_bone", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BRAZEN = createBlock("comb_brazen", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#DAAA4C"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_BRONZE = createBlock("comb_bronze", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#C98C52"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_CHOCOLATE = createBlock("comb_chocolate", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#914139"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_CINNABAR = createBlock("comb_cinnabar", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#d73e4a"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_COMMON_SALVAGE = createBlockCompat("mmorpg", "comb_common_salvage", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#495f58"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_CONSTANTAN = createBlock("comb_constantan", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#fc8669"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_COPPER = createBlock("comb_copper", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#F48702"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_DIAMOND = createBlock("comb_diamond", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#3ddfe1"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_DRACONIC = createBlock("comb_draconic", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ELECTRUM = createBlock("comb_electrum", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#D5BB4F"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ELEMENTIUM = createBlockCompat("botania", "comb_elementium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#dc5af8"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_EMERALD = createBlock("comb_emerald", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#26ac43"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ENDER = createBlock("comb_ender", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ENDER_BIOTITE = createBlock("comb_ender_biotite", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#0f1318"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ENDERIUM = createBlock("comb_enderium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#58a28b"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_EPIC_SALVAGE = createBlockCompat("mmorpg", "comb_epic_salvage", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#af1281"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_EXPERIENCE = createBlock("comb_experience", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#00fc1a"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_FOSSILISED = createBlock("comb_fossilised", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#222525"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_FLUORITE = createBlock("comb_fluorite", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#32e1f6"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_FROSTY = createBlock("comb_frosty", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#86aefd"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_GHOSTLY = createBlock("comb_ghostly", () -> new TranslucentCombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK).notSolid().doesNotBlockMovement()), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_GLOWING = createBlock("comb_glowing", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_GOLD = createBlock("comb_gold", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#fffd6e"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_IMPERIUM =createBlockCompat("mysticalagriculture", "comb_imperium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#007FDB"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_INFERIUM = createBlockCompat("mysticalagriculture", "comb_inferium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#748E00"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_INSANIUM = createBlockCompat("mysticalagradditions", "comb_insanium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#4d086d"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_INVAR = createBlock("comb_invar", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ADB7B2"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_IRON = createBlock("comb_iron", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#cdcdcd"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_LAPIS = createBlock("comb_lapis", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#3537bc"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_LEADEN = createBlock("comb_leaden", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#677193"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_LEGENDARY_SALVAGE = createBlockCompat("mmorpg", "comb_legendary_salvage", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#af8912"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_LUMIUM = createBlock("comb_lumium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#f4ffc3"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MAGMATIC = createBlock("comb_magmatic", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MANASTEEL = createBlockCompat("botania", "comb_manasteel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#4aa7ef"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MENRIL = createBlock("comb_menril", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#5a7088"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MILKY = createBlock("comb_milky", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_NETHERITE = createBlock("comb_netherite", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#4d494d"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_NICKEL = createBlock("comb_nickel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#D8CC93"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_NITER = createBlock("comb_niter", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#e9edf3"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_OBSIDIAN = createBlock("comb_obsidian", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#3b2754"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_OSMIUM = createBlock("comb_osmium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#4c9db6"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PINK_SLIMY = createBlock("comb_pink_slimy", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#b969ba"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PLASTIC = createBlock("comb_plastic", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#d3d3d3"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PLATINUM = createBlock("comb_platinum", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#6FEAEF"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_POWDERY = createBlock("comb_powdery", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PROSPERITY = createBlockCompat("mysticalagriculture", "comb_prosperity", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ddfbfb"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PRUDENTIUM = createBlockCompat("mysticalagriculture", "comb_prudentium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#008C23"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_QUARTZ = createBlock("comb_quartz", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ede5dd"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_RADIOACTIVE = createBlock("comb_radioactive", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#60AE11"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_RARE_SALVAGE = createBlockCompat("mmorpg", "comb_rare_salvage", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#1286af"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_REDSTONE = createBlock("comb_redstone", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#d03621"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ROTTEN = createBlock("comb_rotten", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_REFINED_GLOWSTONE = createBlock("comb_refined_glowstone", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#feee7c"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_REFINED_OBSIDIAN = createBlock("comb_refined_obsidian", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#5e5077"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SIGNALUM = createBlock("comb_signalum", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#e7917d"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SILICON = createBlock("comb_silicon", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#918d96"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SILKY = createBlock("comb_silky", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ffffff"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SILVER = createBlock("comb_silver", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#A9DBE5"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SLIMY = createBlock("comb_slimy", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SOULIUM = createBlockCompat("mysticalagriculture", "comb_soulium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#301b10"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SPACIAL = createBlock("comb_spacial", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#dfe5f6"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_STEEL = createBlock("comb_steel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#737373"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SULFUR = createBlock("comb_sulfur", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#e4ff95"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SUPREMIUM = createBlockCompat("mysticalagriculture", "comb_supremium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#C40000"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TEA = createBlock("comb_tea", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ca7157"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TERRASTEEL = createBlockCompat("botania", "comb_terrasteel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#49cc1d"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TERTIUM = createBlockCompat("mysticalagriculture", "comb_tertium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#B74900"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TIN = createBlock("comb_tin", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#9ABDD6"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TITANIUM = createBlock("comb_titanium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#D0D1DA"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TUNGSTEN = createBlock("comb_tungsten", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#616669"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_UNCOMMON_SALVAGE = createBlockCompat("mmorpg", "comb_uncommon_salvage", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#12af4d"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_UNOBTAINIUM = createBlockCompat("allthemodium", "comb_unobtainium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#bc2feb"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_URANINITE = createBlockCompat("powah", "comb_uraninite", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#00FF00"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_VIBRANIUM = createBlockCompat("allthemodium", "comb_vibranium", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#73ffb9"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_WITHERED = createBlock("comb_withered", () -> new Block(Block.Properties.from(Blocks.HONEYCOMB_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ZINC = createBlock("comb_zinc", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#E9EBE7"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_RUBY = createBlock("comb_ruby", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#c62415"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SAPPHIRE = createBlock("comb_sapphire", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#5241f3"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_APATITE = createBlock("comb_apatite", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#69ffff"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_COBALT = createBlock("comb_cobalt", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#1d77eb"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_HEPATIZON = createBlock("comb_hepatizon", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#675072"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_MANYULLYN = createBlock("comb_manyullyn", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ab6cd7"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_KNIGHTSLIME = createBlock("comb_knightslime", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#c882f5"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_PIG_IRON = createBlock("comb_pig_iron", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#dbaaa9"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_QUEENS_SLIME = createBlock("comb_queens_slime", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#267049"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ROSE_GOLD = createBlock("comb_rose_gold", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#eeb9a0"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SLIMESTEEL = createBlock("comb_slimesteel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#7ae7e0"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SOULSTEEL = createBlock("comb_soulsteel", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#5c4436"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_TINKERS_BRONZE = createBlock("comb_tinkers_bronze", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ffdb7e"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SKY_SLIMY = createBlock("comb_sky_slimy", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#80d4d2"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ENDER_SLIMY = createBlock("comb_ender_slimy", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#d17bfc"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_ICHOR_SLIMY = createBlock("comb_ichor_slimy", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#fcb77b"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_SPECTRUM = createBlock("comb_spectrum", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#ffc9a7"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_COSMIC_DUST = createBlock("comb_cosmic_dust", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#2394cc"), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> COMB_STARMETAL = createBlockCompat("astralsorcery", "comb_starmetal", () -> new CombBlock(Block.Properties.from(Blocks.HONEYCOMB_BLOCK), "#0545b2"), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> BUMBLE_BEE_NEST = createBlock("bumble_bee_nest", () -> new BumbleBeeNest(Block.Properties.from(Blocks.GRASS_BLOCK)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> SUGARBAG_NEST = createBlock("sugarbag_nest", () -> new SugarbagNest(Block.Properties.from(Blocks.BEE_NEST)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_OAK_BEEHIVE = createBlock("advanced_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_OAK = createBlock("expansion_box_oak", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> ADVANCED_SPRUCE_BEEHIVE = createBlock("advanced_spruce_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BIRCH_BEEHIVE = createBlock("advanced_birch_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_JUNGLE_BEEHIVE = createBlock("advanced_jungle_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ACACIA_BEEHIVE = createBlock("advanced_acacia_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DARK_OAK_BEEHIVE = createBlock("advanced_dark_oak_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_CRIMSON_BEEHIVE = createBlock("advanced_crimson_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WARPED_BEEHIVE = createBlock("advanced_warped_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_SNAKE_BLOCK_BEEHIVE = createBlock("advanced_snake_block_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> ADVANCED_ROSEWOOD_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_rosewood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_YUCCA_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_yucca_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_KOUSA_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_kousa_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_ASPEN_BEEHIVE = createBlockCompatBB("atmospheric,byg", "advanced_aspen_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_GRIMWOOD_BEEHIVE = createBlockCompatBB("atmospheric", "advanced_grimwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WILLOW_BEEHIVE = createBlockCompatBB("swampexpansion,byg", "advanced_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_WISTERIA_BEEHIVE = createBlockCompatBB("environmental", "advanced_wisteria_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BAMBOO_BEEHIVE = createBlockCompatBB("bamboo_blocks", "advanced_bamboo_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_MAPLE_BEEHIVE = createBlockCompatBB("autumnity,byg", "advanced_maple_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_DRIFTWOOD_BEEHIVE = createBlockCompatBB("upgrade_aquatic", "advanced_driftwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_RIVER_BEEHIVE = createBlockCompatBB("upgrade_aquatic", "advanced_river_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_POISE_BEEHIVE = createBlockCompatBB("endergetic", "advanced_poise_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_FIR_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_fir_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_DEAD_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_dead_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_PALM_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_palm_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAGIC_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_magic_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_CHERRY_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_cherry_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_UMBRAN_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_umbran_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_WILLOW_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_willow_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_REDWOOD_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_redwood_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_HELLBARK_BEEHIVE = createBlockCompatBB("biomesoplenty", "advanced_bop_hellbark_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_MAHOGANY_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_mahogany_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> ADVANCED_BOP_JACARANDA_BEEHIVE = createBlockCompatBB("biomesoplenty,byg", "advanced_bop_jacaranda_beehive", () -> new AdvancedBeehive(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static final RegistryObject<Block> EXPANSION_BOX_SPRUCE = createBlock("expansion_box_spruce", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BIRCH = createBlock("expansion_box_birch", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_JUNGLE = createBlock("expansion_box_jungle", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ACACIA = createBlock("expansion_box_acacia", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DARK_OAK = createBlock("expansion_box_dark_oak", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_CRIMSON = createBlock("expansion_box_crimson", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WARPED = createBlock("expansion_box_warped", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_SNAKE_BLOCK = createBlock("expansion_box_snake_block", () -> new ExpansionBox(Block.Properties.from(Blocks.DARK_PRISMARINE)), null);

    public static final RegistryObject<Block> EXPANSION_BOX_ROSEWOOD = createBlockCompatBB("atmospheric", "expansion_box_rosewood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_YUCCA = createBlockCompatBB("atmospheric", "expansion_box_yucca", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_KOUSA = createBlockCompatBB("atmospheric", "expansion_box_kousa", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_ASPEN = createBlockCompatBB("atmospheric,byg", "expansion_box_aspen", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_GRIMWOOD = createBlockCompatBB("atmospheric", "expansion_box_grimwood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WILLOW = createBlockCompatBB("swampexpansion,byg", "expansion_box_willow", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_WISTERIA = createBlockCompatBB("bloomful", "expansion_box_wisteria", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BAMBOO = createBlockCompatBB("bamboo_blocks", "expansion_box_bamboo", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_MAPLE = createBlockCompatBB("autumnity,byg", "expansion_box_maple", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_DRIFTWOOD = createBlockCompatBB("upgrade_aquatic", "expansion_box_driftwood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_RIVER = createBlockCompatBB("upgrade_aquatic", "expansion_box_river", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_POISE = createBlockCompatBB("endergetic", "expansion_box_poise", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_FIR = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_fir", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_DEAD = createBlockCompatBB("biomesoplenty", "expansion_box_bop_dead", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_PALM = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_palm", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAGIC = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_magic", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_CHERRY = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_cherry", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_UMBRAN = createBlockCompatBB("biomesoplenty", "expansion_box_bop_umbran", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_WILLOW = createBlockCompatBB("biomesoplenty", "expansion_box_bop_willow", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_REDWOOD = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_redwood", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_HELLBARK = createBlockCompatBB("biomesoplenty", "expansion_box_bop_hellbark", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_MAHOGANY = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_mahogany", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);
    public static final RegistryObject<Block> EXPANSION_BOX_BOP_JACARANDA = createBlockCompatBB("biomesoplenty,byg", "expansion_box_bop_jacaranda", () -> new ExpansionBox(Block.Properties.from(Blocks.BEEHIVE)), ModItemGroups.PRODUCTIVE_BEES);

    public static <B extends Block> RegistryObject<B> createBlockCompatBB(String mods, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mods, name, supplier, ModList.get().isLoaded("buzzier_bees") ? itemGroup : null);
    }

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mods, String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlockCompat(mods, name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlockCompat(String mods, String name, Supplier<? extends B> supplier, ItemGroup itemGroup, boolean createItem) {
        String[] modNames = mods.split(",");
        ItemGroup group = itemGroup != null && ModList.get().isLoaded(modNames[0]) || (modNames.length > 1 && ModList.get().isLoaded(modNames[1])) ? itemGroup : null;
        return createBlock(name, supplier, group, createItem);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, ItemGroup itemGroup) {
        return createBlock(name, supplier, itemGroup, true);
    }

    public static <B extends Block> RegistryObject<B> createBlock(String name, Supplier<? extends B> supplier, ItemGroup itemGroup, boolean createItem) {
        RegistryObject<B> block = BLOCKS.register(name, supplier);
        if (createItem) {
            Item.Properties properties = new Item.Properties().group(itemGroup);

            if (name.equals("configurable_comb")) {
                ModItems.CONFIGURABLE_COMB_BLOCK = ModItems.ITEMS.register(name, () -> new CombBlockItem(block.get(), properties));
            }
            else {
                if (name.equals("comb_netherite")) {
                    properties.isImmuneToFire();
                }
                ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
            }
        }
        return block;
    }
}
