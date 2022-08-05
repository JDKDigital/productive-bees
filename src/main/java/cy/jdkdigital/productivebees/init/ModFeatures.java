package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ProductiveBees.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, ProductiveBees.MODID);

    public static NetherBeehiveDecorator NETHER_BEEHIVE_DECORATOR = new NetherBeehiveDecorator(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("nether_bee_nest").get().floatValue());

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SAND_NEST = FEATURES.register("sand_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("sand_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SNOW_NEST = FEATURES.register("snow_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("snow_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> STONE_NEST = FEATURES.register("stone_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("stone_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> COARSE_DIRT_NEST = FEATURES.register("coarse_dirt_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("coarse_dirt_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GRAVEL_NEST = FEATURES.register("gravel_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("gravel_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SLIMY_NEST = FEATURES.register("slimy_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("slimy_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SUGAR_CANE_NEST = FEATURES.register("sugar_cane_nest", () -> new ReedSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("sugar_cane_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GLOWSTONE_NEST = FEATURES.register("glowstone_nest", () -> new CavernSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("glowstone_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, false));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST = FEATURES.register("nether_quartz_nest", () -> new OreSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("nether_quartz_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST_HIGH = FEATURES.register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature(0.5F + ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("nether_quartz_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_FORTRESS_NEST = FEATURES.register("nether_fortress_nest", () -> new StructureSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("nether_brick_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SOUL_SAND_NEST = FEATURES.register("soul_sand_nest", () -> new CavernSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("soul_sand_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> END_NEST = FEATURES.register("end_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("end_stone_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OBSIDIAN_PILLAR_NEST = FEATURES.register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("obsidian_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC, 25));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BUMBLE_BEE_NEST = FEATURES.register("bumble_bee_nest", () -> new SolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("bumble_bee_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OAK_WOOD_NEST_FEATURE = FEATURES.register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("oak_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SPRUCE_WOOD_NEST_FEATURE = FEATURES.register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("spruce_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BIRCH_WOOD_NEST_FEATURE = FEATURES.register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("birch_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> DARK_OAK_WOOD_NEST_FEATURE = FEATURES.register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("dark_oak_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> JUNGLE_WOOD_NEST_FEATURE = FEATURES.register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("jungle_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> ACACIA_WOOD_NEST_FEATURE = FEATURES.register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature(ProductiveBeesConfig.WORLD_GEN.nestConfigs.get("acacia_wood_nest").get().floatValue(), ReplaceBlockConfiguration.CODEC));

    public static RegistryObject<TreeDecoratorType<NetherBeehiveDecorator>> NETHER_BEEHIVE = TREE_DECORATORS.register("nether_beehive", () -> new TreeDecoratorType<>(NetherBeehiveDecorator.CODEC));

    public static final RegistryObject<Feature<DecoratedHugeFungusConfiguration>> DECORATED_HUGE_FUNGUS = FEATURES.register("decorated_huge_fungus", () -> new DecoratedHugeFungusFeature(DecoratedHugeFungusConfiguration.CODEC));

    private static Holder<PlacedFeature> place(ConfiguredFeature<?, ?> feature) {
        return Holder.direct(new PlacedFeature(Holder.direct(feature), new ArrayList<>()));
    }

    public static void registerFeatures(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        // Add biome features
        if (category.equals(Biome.BiomeCategory.DESERT)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SAND_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.SAVANNA) || category.equals(Biome.BiomeCategory.TAIGA)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.COARSE_DIRT_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SPRUCE_WOOD_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.ACACIA_WOOD_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.JUNGLE)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.JUNGLE_WOOD_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.FOREST)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.OAK_WOOD_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.DARK_OAK_WOOD_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.BIRCH_WOOD_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.EXTREME_HILLS) || category.equals(Biome.BiomeCategory.MOUNTAIN) || category.equals(Biome.BiomeCategory.ICY)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.STONE_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SNOW_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SNOW_NEST_BLOCK_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.SWAMP)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SLIMY_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.PLAINS)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.BUMBLE_BEE_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.NETHER)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.GLOWSTONE_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.NETHER_QUARTZ_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.NETHER_QUARTZ_NEST_HIGH_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.NETHER_FORTRESS_NEST_FEATURE));
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SOUL_SAND_NEST_FEATURE));
        }
        else if (category.equals(Biome.BiomeCategory.RIVER) || category.equals(Biome.BiomeCategory.BEACH)) {
            if (event.getClimate().temperatureModifier != Biome.TemperatureModifier.FROZEN) {
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.GRAVEL_NEST_FEATURE));
            }
        }
        else if (category.equals(Biome.BiomeCategory.THEEND)) {
            if (event.getName().getPath().equals("the_end")) {
                // Pillar nests
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.OBSIDIAN_PILLAR_NEST_FEATURE));
            } else {
                // Must spawn where chorus fruit exist
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.END_NEST_FEATURE));
            }
        }
        if (category.equals(Biome.BiomeCategory.NETHER)) {
            if (event.getName().toString().equals("minecraft:crimson_forest")) {
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Holder.direct(ModConfiguredFeatures.CRIMSON_FUNGUS_BEES_PLACED));
            } else if (event.getName().toString().equals("minecraft:warped_forest")) {
                event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, Holder.direct(ModConfiguredFeatures.WARPED_FUNGUS_BEES_PLACED));
            }
        }

        if (!category.equals(Biome.BiomeCategory.THEEND) && !category.equals(Biome.BiomeCategory.NETHER)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, place(ModConfiguredFeatures.SUGAR_CANE_NEST_FEATURE));
        }
    }
}
