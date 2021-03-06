package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.fml.common.Mod;

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

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        SAND_NEST_FEATURE = Registry.register(registry, rLoc("sand_nest_feature"), ModFeatures.SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SAND.getDefaultState(), ModBlocks.SAND_NEST.get().getDefaultState())));
        COARSE_DIRT_NEST_FEATURE = Registry.register(registry, rLoc("coarse_dirt_nest_feature"), ModFeatures.COARSE_DIRT_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.COARSE_DIRT.getDefaultState(), ModBlocks.COARSE_DIRT_NEST.get().getDefaultState())));
        SPRUCE_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("spruce_wood_nest_feature"), ModFeatures.SPRUCE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.SPRUCE_LOG.getDefaultState(), ModBlocks.SPRUCE_WOOD_NEST.get().getDefaultState())));
        ACACIA_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("acacia_wood_nest_feature"), ModFeatures.ACACIA_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.ACACIA_LOG.getDefaultState(), ModBlocks.ACACIA_WOOD_NEST.get().getDefaultState())));
        JUNGLE_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("jungle_wood_nest_feature"), ModFeatures.JUNGLE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.JUNGLE_LOG.getDefaultState(), ModBlocks.JUNGLE_WOOD_NEST.get().getDefaultState())));
        OAK_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("oak_wood_nest_feature"), ModFeatures.OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.OAK_LOG.getDefaultState(), ModBlocks.OAK_WOOD_NEST.get().getDefaultState())));
        DARK_OAK_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("dark_oak_wood_nest_feature"), ModFeatures.DARK_OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.DARK_OAK_LOG.getDefaultState(), ModBlocks.DARK_OAK_WOOD_NEST.get().getDefaultState())));
        BIRCH_WOOD_NEST_FEATURE = Registry.register(registry, rLoc("birch_wood_nest_feature"), ModFeatures.BIRCH_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.BIRCH_LOG.getDefaultState(), ModBlocks.BIRCH_WOOD_NEST.get().getDefaultState())));
        STONE_NEST_FEATURE = Registry.register(registry, rLoc("stone_nest_feature"), ModFeatures.STONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), ModBlocks.STONE_NEST.get().getDefaultState())));
        SNOW_NEST_FEATURE = Registry.register(registry, rLoc("snow_nest_feature"), ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState())));
        SNOW_NEST_BLOCK_FEATURE = Registry.register(registry, rLoc("snow_nest_block_feature"), ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW_BLOCK.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState())));
        SLIMY_NEST_FEATURE = Registry.register(registry, rLoc("slimy_nest_feature"), ModFeatures.SLIMY_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.SLIMY_NEST.get().getDefaultState())));
        GLOWSTONE_NEST_FEATURE = Registry.register(registry, rLoc("glowstone_nest_feature"), ModFeatures.GLOWSTONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GLOWSTONE.getDefaultState(), ModBlocks.GLOWSTONE_NEST.get().getDefaultState())));
        NETHER_QUARTZ_NEST_FEATURE = Registry.register(registry, rLoc("nether_quartz_nest_feature"), ModFeatures.NETHER_QUARTZ_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), ModBlocks.NETHER_QUARTZ_NEST.get().getDefaultState())));
        NETHER_QUARTZ_NEST_HIGH_FEATURE = Registry.register(registry, rLoc("nether_quartz_nest_high_feature"), ModFeatures.NETHER_QUARTZ_NEST_HIGH.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), ModBlocks.NETHER_QUARTZ_NEST.get().getDefaultState())));
        NETHER_FORTRESS_NEST_FEATURE = Registry.register(registry, rLoc("nether_fortress_nest_feature"), ModFeatures.NETHER_FORTRESS_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_BRICKS.getDefaultState(), ModBlocks.NETHER_BRICK_NEST.get().getDefaultState())));
        SOUL_SAND_NEST_FEATURE = Registry.register(registry, rLoc("soul_sand_nest_feature"), ModFeatures.SOUL_SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SOUL_SAND.getDefaultState(), ModBlocks.SOUL_SAND_NEST.get().getDefaultState())));
        GRAVEL_NEST_FEATURE = Registry.register(registry, rLoc("gravel_nest_feature"), ModFeatures.GRAVEL_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRAVEL.getDefaultState(), ModBlocks.GRAVEL_NEST.get().getDefaultState())));
        OBSIDIAN_PILLAR_NEST_FEATURE = Registry.register(registry, rLoc("obsidian_pillar_nest_feature"), ModFeatures.OBSIDIAN_PILLAR_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.OBSIDIAN.getDefaultState(), ModBlocks.OBSIDIAN_PILLAR_NEST.get().getDefaultState())));
        END_NEST_FEATURE = Registry.register(registry, rLoc("end_nest_feature"), ModFeatures.END_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.END_STONE.getDefaultState(), ModBlocks.END_NEST.get().getDefaultState())));
        SUGAR_CANE_NEST_FEATURE = Registry.register(registry, rLoc("sugar_cane_nest_feature"), ModFeatures.SUGAR_CANE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SUGAR_CANE.getDefaultState(), ModBlocks.SUGAR_CANE_NEST.get().getDefaultState())));
        BUMBLE_BEE_NEST_FEATURE = Registry.register(registry, rLoc("bumble_bee_nest_feature"), ModFeatures.BUMBLE_BEE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.BUMBLE_BEE_NEST.get().getDefaultState())));
    }

    private static ResourceLocation rLoc(String name) {
        return new ResourceLocation(ProductiveBees.MODID, name);
    }
}
