package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ProductiveBees.MODID);

    public static final RegistryObject<Feature<ReplaceBlockConfig>> SAND_NEST = register("sand_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SNOW_NEST = register("snow_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> STONE_NEST = register("stone_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> COARSE_DIRT_NEST = register("coarse_dirt_nest", () -> new SolitaryNestFeature(0.30F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GRAVEL_NEST = register("gravel_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SLIMY_NEST = register("slimy_nest", () -> new SolitaryNestFeature(0.10F, ReplaceBlockConfig.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SUGAR_CANE_NEST = register("sugar_cane_nest", () -> new ReedSolitaryNestFeature(0.70F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GLOWSTONE_NEST = register("glowstone_nest", () -> new CavernSolitaryNestFeature(0.90F, ReplaceBlockConfig.CODEC, false));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST = register("nether_quartz_nest", () -> new OreSolitaryNestFeature(0.50F, ReplaceBlockConfig.CODEC, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST_HIGH = register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature(1.00F, ReplaceBlockConfig.CODEC, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_FORTRESS_NEST = register("nether_fortress_nest", () -> new StructureSolitaryNestFeature(0.90F, ReplaceBlockConfig.CODEC, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SOUL_SAND_NEST = register("soul_sand_nest", () -> new CavernSolitaryNestFeature(0.10F, ReplaceBlockConfig.CODEC, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> END_NEST = register("end_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> OBSIDIAN_PILLAR_NEST = register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature(1.00F, ReplaceBlockConfig.CODEC, 25));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> BUMBLE_BEE_NEST = register("bumble_bee_nest", () -> new SolitaryNestFeature(0.01F, ReplaceBlockConfig.CODEC));

    public static final RegistryObject<Feature<ReplaceBlockConfig>> OAK_WOOD_NEST_FEATURE = register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SPRUCE_WOOD_NEST_FEATURE = register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> BIRCH_WOOD_NEST_FEATURE = register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> DARK_OAK_WOOD_NEST_FEATURE = register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> JUNGLE_WOOD_NEST_FEATURE = register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.10F, ReplaceBlockConfig.CODEC));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> ACACIA_WOOD_NEST_FEATURE = register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.CODEC));

    private static <E extends IFeatureConfig> RegistryObject<Feature<E>> register(String name, Supplier<Feature<E>> supplier) {
        return FEATURES.register(name, supplier);
    }

    public static void registerFeatures(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        // Add biome features
        if (category.equals(Biome.Category.DESERT)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.SAVANNA) || category.equals(Biome.Category.TAIGA)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.COARSE_DIRT_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SPRUCE_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.ACACIA_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.JUNGLE)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.JUNGLE_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.FOREST)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.OAK_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.DARK_OAK_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.BIRCH_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.EXTREME_HILLS)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.STONE_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_BLOCK_FEATURE);
        }
        else if (category.equals(Biome.Category.SWAMP)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SLIMY_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.PLAINS)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.BUMBLE_BEE_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.NETHER)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.GLOWSTONE_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.NETHER_QUARTZ_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_QUARTZ_NEST_HIGH_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_FORTRESS_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SOUL_SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.RIVER) || category.equals(Biome.Category.BEACH)) {
            if (event.getClimate().temperatureModifier != Biome.TemperatureModifier.FROZEN) {
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.GRAVEL_NEST_FEATURE);
            }
        }
        else if (category.equals(Biome.Category.THEEND)) {
            if (event.getName().getPath().equals("the_end")) {
                // Pillar nests
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.OBSIDIAN_PILLAR_NEST_FEATURE);
            }
            else {
                // Must spawn where chorus fruit exist
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.END_NEST_FEATURE);
            }
        }
        if (!category.equals(Biome.Category.THEEND) && !category.equals(Biome.Category.NETHER)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SUGAR_CANE_NEST_FEATURE);
        }
    }
}
