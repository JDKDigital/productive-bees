package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures
{
    public static Supplier<ConfiguredFeature<?, ?>> SAND_NEST_FEATURE = () -> ModFeatures.SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SAND.getDefaultState(), ModBlocks.SAND_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> COARSE_DIRT_NEST_FEATURE = () -> ModFeatures.COARSE_DIRT_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.COARSE_DIRT.getDefaultState(), ModBlocks.COARSE_DIRT_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SPRUCE_WOOD_NEST_FEATURE = () -> ModFeatures.SPRUCE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.SPRUCE_LOG.getDefaultState(), ModBlocks.SPRUCE_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> ACACIA_WOOD_NEST_FEATURE = () -> ModFeatures.ACACIA_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.ACACIA_LOG.getDefaultState(), ModBlocks.ACACIA_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> JUNGLE_WOOD_NEST_FEATURE = () -> ModFeatures.JUNGLE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.JUNGLE_LOG.getDefaultState(), ModBlocks.JUNGLE_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> OAK_WOOD_NEST_FEATURE = () -> ModFeatures.OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.OAK_LOG.getDefaultState(), ModBlocks.OAK_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> DARK_OAK_WOOD_NEST_FEATURE = () -> ModFeatures.DARK_OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.DARK_OAK_LOG.getDefaultState(), ModBlocks.DARK_OAK_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> BIRCH_WOOD_NEST_FEATURE = () -> ModFeatures.BIRCH_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.BIRCH_LOG.getDefaultState(), ModBlocks.BIRCH_WOOD_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> STONE_NEST_FEATURE = () -> ModFeatures.STONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), ModBlocks.STONE_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SNOW_NEST_FEATURE = () -> ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SNOW_NEST_BLOCK_FEATURE = () -> ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW_BLOCK.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SLIMY_NEST_FEATURE = () -> ModFeatures.SLIMY_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.SLIMY_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> GLOWSTONE_NEST_FEATURE = () -> ModFeatures.GLOWSTONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GLOWSTONE.getDefaultState(), ModBlocks.GLOWSTONE_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> NETHER_QUARTZ_NEST_FEATURE = () -> ModFeatures.NETHER_QUARTZ_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), ModBlocks.NETHER_QUARTZ_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> NETHER_QUARTZ_NEST_HIGH_FEATURE = () -> ModFeatures.NETHER_QUARTZ_NEST_HIGH.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), ModBlocks.NETHER_QUARTZ_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> NETHER_FORTRESS_NEST_FEATURE = () -> ModFeatures.NETHER_FORTRESS_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_BRICKS.getDefaultState(), ModBlocks.NETHER_BRICK_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SOUL_SAND_NEST_FEATURE = () -> ModFeatures.SOUL_SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SOUL_SAND.getDefaultState(), ModBlocks.SOUL_SAND_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> GRAVEL_NEST_FEATURE = () -> ModFeatures.GRAVEL_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRAVEL.getDefaultState(), ModBlocks.GRAVEL_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> OBSIDIAN_PILLAR_NEST_FEATURE = () -> ModFeatures.OBSIDIAN_PILLAR_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.OBSIDIAN.getDefaultState(), ModBlocks.OBSIDIAN_PILLAR_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> END_NEST_FEATURE = () -> ModFeatures.END_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.END_STONE.getDefaultState(), ModBlocks.END_NEST.get().getDefaultState()));
    public static Supplier<ConfiguredFeature<?, ?>> SUGAR_CANE_NEST_FEATURE = () -> ModFeatures.SUGAR_CANE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SUGAR_CANE.getDefaultState(), ModBlocks.SUGAR_CANE_NEST.get().getDefaultState()));

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;

        Registry.register(registry, rLoc("sand_nest_feature"), SAND_NEST_FEATURE.get());
        Registry.register(registry, rLoc("snow_nest_feature"), SNOW_NEST_FEATURE.get());
        Registry.register(registry, rLoc("snow_block_nest_feature"), SNOW_NEST_BLOCK_FEATURE.get());
        Registry.register(registry, rLoc("stone_nest_feature"), STONE_NEST_FEATURE.get());
        Registry.register(registry, rLoc("coarse_dirt_nest_feature"), COARSE_DIRT_NEST_FEATURE.get());
        Registry.register(registry, rLoc("gravel_nest_feature"), GRAVEL_NEST_FEATURE.get());
        Registry.register(registry, rLoc("slimy_nest_feature"), SLIMY_NEST_FEATURE.get());
        Registry.register(registry, rLoc("sugar_cane_nest_feature"), SUGAR_CANE_NEST_FEATURE.get());
        Registry.register(registry, rLoc("glowstone_nest_feature"), GLOWSTONE_NEST_FEATURE.get());
        Registry.register(registry, rLoc("nether_quartz_nest_feature"), NETHER_QUARTZ_NEST_FEATURE.get());
        Registry.register(registry, rLoc("nether_quarts_high_nest_feature"), NETHER_QUARTZ_NEST_HIGH_FEATURE.get());
        Registry.register(registry, rLoc("nether_fortress_nest_feature"), NETHER_FORTRESS_NEST_FEATURE.get());
        Registry.register(registry, rLoc("soul_sand_nest_feature"), SOUL_SAND_NEST_FEATURE.get());
        Registry.register(registry, rLoc("end_nest_feature"), END_NEST_FEATURE.get());
        Registry.register(registry, rLoc("obsidian_pillar_nest_feature"), OBSIDIAN_PILLAR_NEST_FEATURE.get());

        Registry.register(registry, rLoc("oak_wood_nest_feature"), OAK_WOOD_NEST_FEATURE.get());
        Registry.register(registry, rLoc("spruce_wood_nest_feature"), SPRUCE_WOOD_NEST_FEATURE.get());
        Registry.register(registry, rLoc("birch_wood_nest_feature"), BIRCH_WOOD_NEST_FEATURE.get());
        Registry.register(registry, rLoc("dark_oak_wood_nest_feature"), DARK_OAK_WOOD_NEST_FEATURE.get());
        Registry.register(registry, rLoc("jungle_wood_nest_feature"), JUNGLE_WOOD_NEST_FEATURE.get());
        Registry.register(registry, rLoc("acacia_wood_nest_feature"), ACACIA_WOOD_NEST_FEATURE.get());
    }
    
    private static ResourceLocation rLoc(String name) {
        return new ResourceLocation(ProductiveBees.MODID, name);
    }
}
