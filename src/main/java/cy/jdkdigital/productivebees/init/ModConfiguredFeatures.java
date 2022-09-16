package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.DecoratedHugeFungusConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.OptionalInt;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures
{
    public static ConfiguredFeature<?, ?> SAND_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> COARSE_DIRT_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> SPRUCE_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> ACACIA_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> JUNGLE_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> OAK_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> DARK_OAK_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> BIRCH_WOOD_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> STONE_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> SNOW_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> SNOW_NEST_BLOCK_FEATURE;
    public static ConfiguredFeature<?, ?> SLIMY_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> GLOWSTONE_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> NETHER_QUARTZ_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> NETHER_QUARTZ_NEST_HIGH_FEATURE;
    public static ConfiguredFeature<?, ?> NETHER_FORTRESS_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> SOUL_SAND_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> GRAVEL_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> OBSIDIAN_PILLAR_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> END_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> SUGAR_CANE_NEST_FEATURE;
    public static ConfiguredFeature<?, ?> BUMBLE_BEE_NEST_FEATURE;

    public static ConfiguredFeature<BlockStateConfiguration, ?> GLOWSTONE_NEST;
    public static ConfiguredFeature<OreConfiguration, ?> NETHER_QUARTZ_NEST;
    public static PlacedFeature GLOWSTONE_NEST_EXTRA_PLACED;
    public static PlacedFeature GLOWSTONE_NEST_PLACED;
    public static PlacedFeature NETHER_QUARTZ_NEST_PLACED;

    public static ConfiguredFeature<TreeConfiguration, ?> OAK_SOLITARY_NEST;
    public static ConfiguredFeature<TreeConfiguration, ?> BIRCH_SOLITARY_NEST;
    public static ConfiguredFeature<TreeConfiguration, ?> SPRUCE_SOLITARY_NEST;
    public static ConfiguredFeature<TreeConfiguration, ?> ACACIA_SOLITARY_NEST;
    public static ConfiguredFeature<TreeConfiguration, ?> DARK_OAK_SOLITARY_NEST;
    public static ConfiguredFeature<TreeConfiguration, ?> JUNGLE_SOLITARY_NEST;
    public static PlacedFeature OAK_SOLITARY_NEST_PLACED;
    public static PlacedFeature BIRCH_SOLITARY_NEST_PLACED;
    public static PlacedFeature SPRUCE_SOLITARY_NEST_PLACED;
    public static PlacedFeature ACACIA_SOLITARY_NEST_PLACED;
    public static PlacedFeature DARK_OAK_SOLITARY_NEST_PLACED;
    public static PlacedFeature JUNGLE_SOLITARY_NEST_PLACED;

    public static ConfiguredFeature<?, ?> CRIMSON_FUNGUS_BEES;
    public static ConfiguredFeature<?, ?> CRIMSON_FUNGUS_BEES_GROW;
    public static ConfiguredFeature<?, ?> WARPED_FUNGUS_BEES;
    public static ConfiguredFeature<?, ?> WARPED_FUNGUS_BEES_GROW;
    public static PlacedFeature CRIMSON_FUNGUS_BEES_PLACED;
    public static PlacedFeature WARPED_FUNGUS_BEES_PLACED;

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = BuiltinRegistries.CONFIGURED_FEATURE;

        SAND_NEST_FEATURE = Registry.register(registry, rLoc("sand_nest_feature"), new ConfiguredFeature<>(ModFeatures.SAND_NEST.get(), new ReplaceBlockConfiguration(Blocks.SAND.defaultBlockState(), ModBlocks.SAND_NEST.get().defaultBlockState())));
        COARSE_DIRT_NEST_FEATURE = Registry.register(registry, rLoc("coarse_dirt_nest_feature"), new ConfiguredFeature<>(ModFeatures.COARSE_DIRT_NEST.get(), new ReplaceBlockConfiguration(Blocks.COARSE_DIRT.defaultBlockState(), ModBlocks.COARSE_DIRT_NEST.get().defaultBlockState())));
        SPRUCE_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("spruce_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.SPRUCE_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.SPRUCE_LOG.defaultBlockState(), ModBlocks.SPRUCE_WOOD_NEST.get().defaultBlockState()))));
        ACACIA_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("acacia_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.ACACIA_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.ACACIA_LOG.defaultBlockState(), ModBlocks.ACACIA_WOOD_NEST.get().defaultBlockState()))));
        JUNGLE_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("jungle_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.JUNGLE_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.JUNGLE_LOG.defaultBlockState(), ModBlocks.JUNGLE_WOOD_NEST.get().defaultBlockState()))));
        OAK_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("oak_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.OAK_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.OAK_LOG.defaultBlockState(), ModBlocks.OAK_WOOD_NEST.get().defaultBlockState()))));
        DARK_OAK_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("dark_oak_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.DARK_OAK_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.DARK_OAK_LOG.defaultBlockState(), ModBlocks.DARK_OAK_WOOD_NEST.get().defaultBlockState()))));
        BIRCH_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("birch_wood_nest_feature"), new ConfiguredFeature<>(ModFeatures.BIRCH_WOOD_NEST_FEATURE.get(), (new ReplaceBlockConfiguration(Blocks.BIRCH_LOG.defaultBlockState(), ModBlocks.BIRCH_WOOD_NEST.get().defaultBlockState()))));
        STONE_NEST_FEATURE = Registry.register(registry, rLoc("stone_nest_feature"), new ConfiguredFeature<>(ModFeatures.STONE_NEST.get(), (new ReplaceBlockConfiguration(Blocks.STONE.defaultBlockState(), ModBlocks.STONE_NEST.get().defaultBlockState()))));
        SNOW_NEST_FEATURE = Registry.register(registry, rLoc("snow_nest_feature"), new ConfiguredFeature<>(ModFeatures.SNOW_NEST.get(), (new ReplaceBlockConfiguration(Blocks.SNOW.defaultBlockState(), ModBlocks.SNOW_NEST.get().defaultBlockState()))));
        SNOW_NEST_BLOCK_FEATURE = Registry.register(registry, rLoc("snow_nest_block_feature"), new ConfiguredFeature<>(ModFeatures.SNOW_NEST.get(), (new ReplaceBlockConfiguration(Blocks.SNOW_BLOCK.defaultBlockState(), ModBlocks.SNOW_NEST.get().defaultBlockState()))));
        SLIMY_NEST_FEATURE = Registry.register(registry, rLoc("slimy_nest_feature"), new ConfiguredFeature<>(ModFeatures.SLIMY_NEST.get(), (new ReplaceBlockConfiguration(Blocks.GRASS_BLOCK.defaultBlockState(), ModBlocks.SLIMY_NEST.get().defaultBlockState()))));
        GLOWSTONE_NEST_FEATURE = Registry.register(registry, rLoc("glowstone_nest_feature"), new ConfiguredFeature<>(ModFeatures.GLOWSTONE_NEST.get(), (new ReplaceBlockConfiguration(Blocks.GLOWSTONE.defaultBlockState(), ModBlocks.GLOWSTONE_NEST.get().defaultBlockState()))));
        NETHER_QUARTZ_NEST_FEATURE = Registry.register(registry, rLoc("nether_quartz_nest_feature"), new ConfiguredFeature<>(ModFeatures.NETHER_QUARTZ_NEST.get(), (new ReplaceBlockConfiguration(Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), ModBlocks.NETHER_QUARTZ_NEST.get().defaultBlockState()))));
        NETHER_QUARTZ_NEST_HIGH_FEATURE = Registry.register(registry, rLoc("nether_quartz_nest_high_feature"), new ConfiguredFeature<>(ModFeatures.NETHER_QUARTZ_NEST_HIGH.get(), (new ReplaceBlockConfiguration(Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), ModBlocks.NETHER_QUARTZ_NEST.get().defaultBlockState()))));
        NETHER_FORTRESS_NEST_FEATURE = Registry.register(registry, rLoc("nether_fortress_nest_feature"), new ConfiguredFeature<>(ModFeatures.NETHER_FORTRESS_NEST.get(), (new ReplaceBlockConfiguration(Blocks.NETHER_BRICKS.defaultBlockState(), ModBlocks.NETHER_BRICK_NEST.get().defaultBlockState()))));
        SOUL_SAND_NEST_FEATURE = Registry.register(registry, rLoc("soul_sand_nest_feature"), new ConfiguredFeature<>(ModFeatures.SOUL_SAND_NEST.get(), (new ReplaceBlockConfiguration(Blocks.SOUL_SAND.defaultBlockState(), ModBlocks.SOUL_SAND_NEST.get().defaultBlockState()))));
        GRAVEL_NEST_FEATURE = Registry.register(registry, rLoc("gravel_nest_feature"), new ConfiguredFeature<>(ModFeatures.GRAVEL_NEST.get(), (new ReplaceBlockConfiguration(Blocks.GRAVEL.defaultBlockState(), ModBlocks.GRAVEL_NEST.get().defaultBlockState()))));
        OBSIDIAN_PILLAR_NEST_FEATURE = Registry.register(registry, rLoc("obsidian_pillar_nest_feature"), new ConfiguredFeature<>(ModFeatures.OBSIDIAN_PILLAR_NEST.get(), (new ReplaceBlockConfiguration(Blocks.OBSIDIAN.defaultBlockState(), ModBlocks.OBSIDIAN_PILLAR_NEST.get().defaultBlockState()))));
        END_NEST_FEATURE = Registry.register(registry, rLoc("end_nest_feature"), new ConfiguredFeature<>(ModFeatures.END_NEST.get(), (new ReplaceBlockConfiguration(Blocks.END_STONE.defaultBlockState(), ModBlocks.END_NEST.get().defaultBlockState()))));
        SUGAR_CANE_NEST_FEATURE = Registry.register(registry, rLoc("sugar_cane_nest_feature"), new ConfiguredFeature<>(ModFeatures.SUGAR_CANE_NEST.get(), (new ReplaceBlockConfiguration(Blocks.SUGAR_CANE.defaultBlockState(), ModBlocks.SUGAR_CANE_NEST.get().defaultBlockState()))));
        BUMBLE_BEE_NEST_FEATURE = Registry.register(registry, rLoc("bumble_bee_nest_feature"), new ConfiguredFeature<>(ModFeatures.BUMBLE_BEE_NEST.get(), (new ReplaceBlockConfiguration(Blocks.GRASS_BLOCK.defaultBlockState(), ModBlocks.BUMBLE_BEE_NEST.get().defaultBlockState()))));
        NETHER_FORTRESS_NEST_FEATURE = Registry.register(registry, rLoc("nether_fortress_nest_feature"), new ConfiguredFeature<>(ModFeatures.NETHER_FORTRESS_NEST.get(), (new ReplaceBlockConfiguration(Blocks.NETHER_BRICKS.defaultBlockState(), ModBlocks.NETHER_BRICK_NEST.get().defaultBlockState()))));

        GLOWSTONE_NEST = Registry.register(registry, rLoc("glowstone_nest"), new ConfiguredFeature<>(ModFeatures.GLOWSTONE_NEST_BLOB.get(), new BlockStateConfiguration(ModBlocks.GLOWSTONE_NEST.get().defaultBlockState())));
        NETHER_QUARTZ_NEST = Registry.register(registry, rLoc("nether_quartz_nest"), new ConfiguredFeature<>(ModFeatures.NETHER_QUARTZ_NEST_ORE.get(), new OreConfiguration(new BlockMatchTest(Blocks.NETHERRACK), Blocks.NETHER_QUARTZ_ORE.defaultBlockState(), 14)));

        OAK_SOLITARY_NEST = Registry.register(registry, rLoc("oak_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), TreeFeatures.createOak().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));
        BIRCH_SOLITARY_NEST = Registry.register(registry, rLoc("bich_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), TreeFeatures.createBirch().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));
        SPRUCE_SOLITARY_NEST = Registry.register(registry, rLoc("spruce_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(5, 2, 1), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new SpruceFoliagePlacer(UniformInt.of(2, 3), UniformInt.of(0, 2), UniformInt.of(1, 2)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));
        ACACIA_SOLITARY_NEST = Registry.register(registry, rLoc("acacia_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.ACACIA_LOG), new ForkingTrunkPlacer(5, 2, 2), BlockStateProvider.simple(Blocks.ACACIA_LEAVES), new AcaciaFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0)), new TwoLayersFeatureSize(1, 0, 2))).ignoreVines().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));
        DARK_OAK_SOLITARY_NEST = Registry.register(registry, rLoc("dark_oak_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.DARK_OAK_LOG), new DarkOakTrunkPlacer(6, 2, 1), BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES), new DarkOakFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0)), new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));
        JUNGLE_SOLITARY_NEST = Registry.register(registry, rLoc("jungle_solitary_nest"), new ConfiguredFeature<>(ModFeatures.SOLITARY_NEST_TREE.get(), TreeFeatures.createJungleTree().decorators(List.of(ModFeatures.WOOD_NEST_DECORATOR)).build()));

        CRIMSON_FUNGUS_BEES = Registry.register(registry, rLoc("crimson_fungus_bees"), new ConfiguredFeature<>(ModFeatures.DECORATED_HUGE_FUNGUS.get(),
                new DecoratedHugeFungusConfiguration(
                        Blocks.CRIMSON_NYLIUM.defaultBlockState(),
                        Blocks.CRIMSON_STEM.defaultBlockState(),
                        Blocks.NETHER_WART_BLOCK.defaultBlockState(),
                        Blocks.SHROOMLIGHT.defaultBlockState(),
                        ModBlocks.CRIMSON_BEE_NEST.get().defaultBlockState(),
                        List.of(ModFeatures.NETHER_BEEHIVE_DECORATOR),
                        false
                )));
        CRIMSON_FUNGUS_BEES_GROW = Registry.register(registry, rLoc("crimson_fungus_bees"), new ConfiguredFeature<>(ModFeatures.DECORATED_HUGE_FUNGUS.get(),
                new DecoratedHugeFungusConfiguration(
                        Blocks.CRIMSON_NYLIUM.defaultBlockState(),
                        Blocks.CRIMSON_STEM.defaultBlockState(),
                        Blocks.NETHER_WART_BLOCK.defaultBlockState(),
                        Blocks.SHROOMLIGHT.defaultBlockState(),
                        ModBlocks.CRIMSON_BEE_NEST.get().defaultBlockState(),
                        List.of(ModFeatures.NETHER_BEEHIVE_DECORATOR_100),
                        false
                )));
        WARPED_FUNGUS_BEES = Registry.register(registry, rLoc("warped_fungus_bees"), new ConfiguredFeature<>(ModFeatures.DECORATED_HUGE_FUNGUS.get(),
                new DecoratedHugeFungusConfiguration(
                        Blocks.WARPED_NYLIUM.defaultBlockState(),
                        Blocks.WARPED_STEM.defaultBlockState(),
                        Blocks.WARPED_WART_BLOCK.defaultBlockState(),
                        Blocks.SHROOMLIGHT.defaultBlockState(),
                        ModBlocks.WARPED_BEE_NEST.get().defaultBlockState(),
                        List.of(ModFeatures.NETHER_BEEHIVE_DECORATOR),
                        false
                )));
        WARPED_FUNGUS_BEES_GROW = Registry.register(registry, rLoc("warped_fungus_bees"), new ConfiguredFeature<>(ModFeatures.DECORATED_HUGE_FUNGUS.get(),
                new DecoratedHugeFungusConfiguration(
                        Blocks.WARPED_NYLIUM.defaultBlockState(),
                        Blocks.WARPED_STEM.defaultBlockState(),
                        Blocks.WARPED_WART_BLOCK.defaultBlockState(),
                        Blocks.SHROOMLIGHT.defaultBlockState(),
                        ModBlocks.WARPED_BEE_NEST.get().defaultBlockState(),
                        List.of(ModFeatures.NETHER_BEEHIVE_DECORATOR_100),
                        false
                )));
    }

    public static void registerPlacedFeatures() {
        Registry<PlacedFeature> registry = BuiltinRegistries.PLACED_FEATURE;

        GLOWSTONE_NEST_EXTRA_PLACED = Registry.register(registry, rLoc("glowstone_nest_extra"), new PlacedFeature(Holder.direct(GLOWSTONE_NEST), List.of(CountPlacement.of(BiasedToBottomInt.of(0, 9)), InSquarePlacement.spread(), PlacementUtils.RANGE_4_4, BiomeFilter.biome())));
        GLOWSTONE_NEST_PLACED = Registry.register(registry, rLoc("glowstone_nest"), new PlacedFeature(Holder.direct(GLOWSTONE_NEST), List.of(CountPlacement.of(10), InSquarePlacement.spread(), PlacementUtils.FULL_RANGE, BiomeFilter.biome())));
        NETHER_QUARTZ_NEST_PLACED = Registry.register(registry, rLoc("nether_quartz_nest"), new PlacedFeature(Holder.direct(NETHER_QUARTZ_NEST), List.of(CountPlacement.of(16), InSquarePlacement.spread(), PlacementUtils.RANGE_10_10, BiomeFilter.biome())));

        OAK_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("oak_solitary_nest"), new PlacedFeature(Holder.direct(OAK_SOLITARY_NEST), List.of(InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING), BiomeFilter.biome())));
        BIRCH_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("birch_solitary_nest"), new PlacedFeature(Holder.direct(BIRCH_SOLITARY_NEST), List.of(InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING), BiomeFilter.biome())));
        SPRUCE_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("spruce_solitary_nest"), new PlacedFeature(Holder.direct(SPRUCE_SOLITARY_NEST), List.of(InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING), BiomeFilter.biome())));
        ACACIA_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("acacia_solitary_nest"), new PlacedFeature(Holder.direct(ACACIA_SOLITARY_NEST), List.of(InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING), BiomeFilter.biome())));
        DARK_OAK_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("dark_oak_solitary_nest"), new PlacedFeature(Holder.direct(DARK_OAK_SOLITARY_NEST), List.of(InSquarePlacement.spread(), RarityFilter.onAverageOnceEvery(10), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING), BiomeFilter.biome())));
        JUNGLE_SOLITARY_NEST_PLACED = Registry.register(registry, rLoc("jungle_solitary_nest"), new PlacedFeature(Holder.direct(JUNGLE_SOLITARY_NEST), List.of(InSquarePlacement.spread(), PlacementUtils.countExtra(1, 0.1F, 6), VegetationPlacements.TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING), BiomeFilter.biome())));

        CRIMSON_FUNGUS_BEES_PLACED = Registry.register(registry, rLoc("crimson_fungus_bees"), new PlacedFeature(Holder.direct(CRIMSON_FUNGUS_BEES), List.of(CountOnEveryLayerPlacement.of(8), BiomeFilter.biome())));
        WARPED_FUNGUS_BEES_PLACED = Registry.register(registry, rLoc("warped_fungus_bees"), new PlacedFeature(Holder.direct(WARPED_FUNGUS_BEES), List.of(CountOnEveryLayerPlacement.of(8), BiomeFilter.biome())));
    }

    private static ResourceLocation rLoc(String name) {
        return new ResourceLocation(ProductiveBees.MODID, name);
    }
}
