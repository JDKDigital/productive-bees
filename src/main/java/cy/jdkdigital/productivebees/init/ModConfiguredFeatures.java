package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.DecoratedHugeFungusConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

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

    public static ConfiguredFeature<?, ?> CRIMSON_FUNGUS_BEES;
    public static PlacedFeature CRIMSON_FUNGUS_BEES_PLACED;

    public static ConfiguredFeature<?, ?> WARPED_FUNGUS_BEES;
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
    }

    public static void registerPlacedFeatures() {
        Registry<PlacedFeature> registry = BuiltinRegistries.PLACED_FEATURE;

        CRIMSON_FUNGUS_BEES_PLACED = Registry.register(registry, rLoc("crimson_fungus_bees"), new PlacedFeature(Holder.direct(CRIMSON_FUNGUS_BEES), List.of(BiomeFilter.biome())));
        WARPED_FUNGUS_BEES_PLACED = Registry.register(registry, rLoc("warped_fungus_bees"), new PlacedFeature(Holder.direct(WARPED_FUNGUS_BEES), List.of(BiomeFilter.biome())));
    }

    private static ResourceLocation rLoc(String name) {
        return new ResourceLocation(ProductiveBees.MODID, name);
    }
}
