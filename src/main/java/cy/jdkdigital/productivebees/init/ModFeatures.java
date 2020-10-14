package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
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
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, ProductiveBees.MODID);

    public static final RegistryObject<Feature<ReplaceBlockConfig>> SAND_NEST = register("sand_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SNOW_NEST = register("snow_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> STONE_NEST = register("stone_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> COARSE_DIRT_NEST = register("coarse_dirt_nest", () -> new SolitaryNestFeature(0.30F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GRAVEL_NEST = register("gravel_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SLIMY_NEST = register("slimy_nest", () -> new SolitaryNestFeature(0.10F, ReplaceBlockConfig::deserialize, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SUGAR_CANE_NEST = register("sugar_cane_nest", () -> new ReedSolitaryNestFeature(0.40F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GLOWSTONE_NEST = register("glowstone_nest", () -> new CavernSolitaryNestFeature(0.90F, ReplaceBlockConfig::deserialize, false));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST = register("nether_quartz_nest", () -> new OreSolitaryNestFeature(0.50F, ReplaceBlockConfig::deserialize, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST_HIGH = register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature(1.00F, ReplaceBlockConfig::deserialize, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_FORTRESS_NEST = register("nether_fortress_nest", () -> new StructureSolitaryNestFeature(0.90F, ReplaceBlockConfig::deserialize, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SOUL_SAND_NEST = register("soul_sand_nest", () -> new CavernSolitaryNestFeature(0.10F, ReplaceBlockConfig::deserialize, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> END_NEST = register("end_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> OBSIDIAN_PILLAR_NEST = register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature(1.00F, ReplaceBlockConfig::deserialize, 25));

    public static final RegistryObject<Feature<ReplaceBlockConfig>> OAK_WOOD_NEST_FEATURE = register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SPRUCE_WOOD_NEST_FEATURE = register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> BIRCH_WOOD_NEST_FEATURE = register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> DARK_OAK_WOOD_NEST_FEATURE = register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> JUNGLE_WOOD_NEST_FEATURE = register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.10F, ReplaceBlockConfig::deserialize));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> ACACIA_WOOD_NEST_FEATURE = register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig::deserialize));

    private static <E extends IFeatureConfig> RegistryObject<Feature<E>> register(String name, Supplier<Feature<E>> supplier) {
        return FEATURES.register(name, supplier);
    }

    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> featureRegistry = event.getRegistry();

        registerFeature(featureRegistry, SAND_NEST.get(), SAND_NEST.getId());
        registerFeature(featureRegistry, SNOW_NEST.get(), SNOW_NEST.getId());
        registerFeature(featureRegistry, STONE_NEST.get(), STONE_NEST.getId());
        registerFeature(featureRegistry, COARSE_DIRT_NEST.get(), COARSE_DIRT_NEST.getId());
        registerFeature(featureRegistry, GRAVEL_NEST.get(), GRAVEL_NEST.getId());
        registerFeature(featureRegistry, SLIMY_NEST.get(), SLIMY_NEST.getId());
        registerFeature(featureRegistry, SUGAR_CANE_NEST.get(), SUGAR_CANE_NEST.getId());
        registerFeature(featureRegistry, GLOWSTONE_NEST.get(), GLOWSTONE_NEST.getId());
        registerFeature(featureRegistry, NETHER_QUARTZ_NEST.get(), NETHER_QUARTZ_NEST.getId());
        registerFeature(featureRegistry, NETHER_QUARTZ_NEST_HIGH.get(), NETHER_QUARTZ_NEST_HIGH.getId());
        registerFeature(featureRegistry, NETHER_FORTRESS_NEST.get(), NETHER_FORTRESS_NEST.getId());
        registerFeature(featureRegistry, SOUL_SAND_NEST.get(), SOUL_SAND_NEST.getId());
        registerFeature(featureRegistry, END_NEST.get(), END_NEST.getId());
        registerFeature(featureRegistry, OBSIDIAN_PILLAR_NEST.get(), OBSIDIAN_PILLAR_NEST.getId());

        registerFeature(featureRegistry, OAK_WOOD_NEST_FEATURE.get(), OAK_WOOD_NEST_FEATURE.getId());
        registerFeature(featureRegistry, SPRUCE_WOOD_NEST_FEATURE.get(), SPRUCE_WOOD_NEST_FEATURE.getId());
        registerFeature(featureRegistry, BIRCH_WOOD_NEST_FEATURE.get(), BIRCH_WOOD_NEST_FEATURE.getId());
        registerFeature(featureRegistry, DARK_OAK_WOOD_NEST_FEATURE.get(), DARK_OAK_WOOD_NEST_FEATURE.getId());
        registerFeature(featureRegistry, JUNGLE_WOOD_NEST_FEATURE.get(), JUNGLE_WOOD_NEST_FEATURE.getId());
        registerFeature(featureRegistry, ACACIA_WOOD_NEST_FEATURE.get(), ACACIA_WOOD_NEST_FEATURE.getId());
    }

    public static <F extends Feature<?>> void registerFeature(IForgeRegistry<Feature<?>> registry, F feature, ResourceLocation resourceLocation) {
        Registry.register(Registry.FEATURE, resourceLocation, feature);
    }
}
