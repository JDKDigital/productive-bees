package cy.jdkdigital.productivebees.init;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ProductiveBees.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, ProductiveBees.MODID);

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SAND_NEST = FEATURES.register("sand_nest", () -> new SolitaryNestFeature("sand_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SNOW_NEST = FEATURES.register("snow_nest", () -> new SolitaryNestFeature("snow_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> STONE_NEST = FEATURES.register("stone_nest", () -> new SolitaryNestFeature("stone_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> COARSE_DIRT_NEST = FEATURES.register("coarse_dirt_nest", () -> new SolitaryNestFeature("coarse_dirt_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GRAVEL_NEST = FEATURES.register("gravel_nest", () -> new SolitaryNestFeature("gravel_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SLIMY_NEST = FEATURES.register("slimy_nest", () -> new SolitaryNestFeature("slimy_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SUGAR_CANE_NEST = FEATURES.register("sugar_cane_nest", () -> new ReedSolitaryNestFeature("sugar_cane_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GLOWSTONE_NEST = FEATURES.register("glowstone_nest", () -> new CavernSolitaryNestFeature("glowstone_nest", ReplaceBlockConfiguration.CODEC, false));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST = FEATURES.register("nether_quartz_nest", () -> new OreSolitaryNestFeature("nether_quartz_nest", ReplaceBlockConfiguration.CODEC, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST_HIGH = FEATURES.register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature("nether_quartz_nest", ReplaceBlockConfiguration.CODEC, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_FORTRESS_NEST = FEATURES.register("nether_fortress_nest", () -> new StructureSolitaryNestFeature("nether_brick_nest", ReplaceBlockConfiguration.CODEC, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SOUL_SAND_NEST = FEATURES.register("soul_sand_nest", () -> new CavernSolitaryNestFeature("soul_sand_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> END_NEST = FEATURES.register("end_nest", () -> new SolitaryNestFeature("end_stone_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OBSIDIAN_PILLAR_NEST = FEATURES.register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature("obsidian_nest", ReplaceBlockConfiguration.CODEC, 25));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BUMBLE_BEE_NEST = FEATURES.register("bumble_bee_nest", () -> new SolitaryNestFeature("bumble_bee_nest", ReplaceBlockConfiguration.CODEC));

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OAK_WOOD_NEST_FEATURE = FEATURES.register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature("oak_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SPRUCE_WOOD_NEST_FEATURE = FEATURES.register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature("spruce_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BIRCH_WOOD_NEST_FEATURE = FEATURES.register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature("birch_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> DARK_OAK_WOOD_NEST_FEATURE = FEATURES.register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature("dark_oak_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> JUNGLE_WOOD_NEST_FEATURE = FEATURES.register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature("jungle_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> ACACIA_WOOD_NEST_FEATURE = FEATURES.register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature("acacia_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static RegistryObject<TreeDecoratorType<NetherBeehiveDecorator>> NETHER_BEEHIVE = TREE_DECORATORS.register("nether_beehive", () -> new TreeDecoratorType<>(NetherBeehiveDecorator.CODEC));
    public static RegistryObject<TreeDecoratorType<WoodNestDecorator>> WOOD_NEST = TREE_DECORATORS.register("wood_nest", () -> new TreeDecoratorType<>(WoodNestDecorator.CODEC));

    public static final RegistryObject<Feature<BlockStateConfiguration>> GLOWSTONE_NEST_BLOB = FEATURES.register("glowstone_nest_blob", () -> new GlowstoneNestFeature(GlowstoneNestFeature.CODEC));
    public static final RegistryObject<Feature<OreConfiguration>> NETHER_QUARTZ_NEST_ORE = FEATURES.register("nether_quartz_nest_ore", () -> new OreFeature(OreConfiguration.CODEC)); // TODO
    public static final RegistryObject<Feature<TreeConfiguration>> SOLITARY_NEST_TREE = FEATURES.register("solitary_nest_tree", () -> new SolitaryNestTreeFeature(TreeConfiguration.CODEC));
    public static final RegistryObject<Feature<DecoratedHugeFungusConfiguration>> DECORATED_HUGE_FUNGUS = FEATURES.register("decorated_huge_fungus", () -> new DecoratedHugeFungusFeature(DecoratedHugeFungusConfiguration.CODEC));

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ProductiveBees.MODID);
    public static final RegistryObject<Codec<NestBiomeModifier>> NEST_BIOME_MODIFIER = BIOME_MODIFIERS.register("nest", NestBiomeModifier::makeCodec);
}
