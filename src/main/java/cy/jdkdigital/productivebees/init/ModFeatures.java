package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ProductiveBees.MODID);

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SAND_NEST = register("sand_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SNOW_NEST = register("snow_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> STONE_NEST = register("stone_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> COARSE_DIRT_NEST = register("coarse_dirt_nest", () -> new SolitaryNestFeature(0.30F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GRAVEL_NEST = register("gravel_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SLIMY_NEST = register("slimy_nest", () -> new SolitaryNestFeature(0.10F, ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SUGAR_CANE_NEST = register("sugar_cane_nest", () -> new ReedSolitaryNestFeature(0.40F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> GLOWSTONE_NEST = register("glowstone_nest", () -> new CavernSolitaryNestFeature(0.90F, ReplaceBlockConfiguration.CODEC, false));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST = register("nether_quartz_nest", () -> new OreSolitaryNestFeature(0.20F, ReplaceBlockConfiguration.CODEC, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_QUARTZ_NEST_HIGH = register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature(0.70F, ReplaceBlockConfiguration.CODEC, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> NETHER_FORTRESS_NEST = register("nether_fortress_nest", () -> new StructureSolitaryNestFeature(0.90F, ReplaceBlockConfiguration.CODEC, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SOUL_SAND_NEST = register("soul_sand_nest", () -> new CavernSolitaryNestFeature(0.10F, ReplaceBlockConfiguration.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> END_NEST = register("end_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OBSIDIAN_PILLAR_NEST = register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature(1.00F, ReplaceBlockConfiguration.CODEC, 25));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BUMBLE_BEE_NEST = register("bumble_bee_nest", () -> new SolitaryNestFeature(0.01F, ReplaceBlockConfiguration.CODEC));

    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> OAK_WOOD_NEST_FEATURE = register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> SPRUCE_WOOD_NEST_FEATURE = register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> BIRCH_WOOD_NEST_FEATURE = register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> DARK_OAK_WOOD_NEST_FEATURE = register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> JUNGLE_WOOD_NEST_FEATURE = register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.10F, ReplaceBlockConfiguration.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfiguration>> ACACIA_WOOD_NEST_FEATURE = register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfiguration.CODEC));

    private static <E extends FeatureConfiguration> RegistryObject<Feature<E>> register(String name, Supplier<Feature<E>> supplier) {
        return FEATURES.register(name, supplier);
    }

    public static void registerFeatures(BiomeLoadingEvent event) {
        Biome.BiomeCategory category = event.getCategory();
        // Add biome features
        if (category.equals(Biome.BiomeCategory.DESERT)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.SAVANNA) || category.equals(Biome.BiomeCategory.TAIGA)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.COARSE_DIRT_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SPRUCE_WOOD_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.ACACIA_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.JUNGLE)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.JUNGLE_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.FOREST)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.OAK_WOOD_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.DARK_OAK_WOOD_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.BIRCH_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.EXTREME_HILLS)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.STONE_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_BLOCK_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.SWAMP)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SLIMY_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.PLAINS)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.BUMBLE_BEE_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.NETHER)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.GLOWSTONE_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.NETHER_QUARTZ_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_QUARTZ_NEST_HIGH_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_FORTRESS_NEST_FEATURE);
            event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SOUL_SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.BiomeCategory.RIVER) || category.equals(Biome.BiomeCategory.BEACH)) {
            if (event.getClimate().temperatureModifier != Biome.TemperatureModifier.FROZEN) {
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.GRAVEL_NEST_FEATURE);
            }
        }
        else if (category.equals(Biome.BiomeCategory.THEEND)) {
            if (event.getName().getPath().equals("the_end")) {
                // Pillar nests
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.OBSIDIAN_PILLAR_NEST_FEATURE);
            }
            else {
                // Must spawn where chorus fruit exist
                event.getGeneration().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.END_NEST_FEATURE);
            }
        }
        if (!category.equals(Biome.BiomeCategory.THEEND) && !category.equals(Biome.BiomeCategory.NETHER)) {
            event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SUGAR_CANE_NEST_FEATURE);
        }
    }
}
