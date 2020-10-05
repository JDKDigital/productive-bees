package cy.jdkdigital.productivebees.init;

import com.google.common.base.Supplier;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, ProductiveBees.MODID);

    public static final RegistryObject<Feature<ReplaceBlockConfig>> SAND_NEST = register("sand_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SNOW_NEST = register("snow_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> STONE_NEST = register("stone_nest", () -> new SolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> COARSE_DIRT_NEST = register("coarse_dirt_nest", () -> new SolitaryNestFeature(0.30F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GRAVEL_NEST = register("gravel_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SLIMY_NEST = register("slimy_nest", () -> new SolitaryNestFeature(0.10F, ReplaceBlockConfig.field_236604_a_, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SUGAR_CANE_NEST = register("sugar_cane_nest", () -> new ReedSolitaryNestFeature(0.70F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> GLOWSTONE_NEST = register("glowstone_nest", () -> new CavernSolitaryNestFeature(0.90F, ReplaceBlockConfig.field_236604_a_, false));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST = register("nether_quartz_nest", () -> new OreSolitaryNestFeature(0.50F, ReplaceBlockConfig.field_236604_a_, 10, 70));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_QUARTZ_NEST_HIGH = register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature(1.00F, ReplaceBlockConfig.field_236604_a_, 70, 100));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> NETHER_FORTRESS_NEST = register("nether_fortress_nest", () -> new StructureSolitaryNestFeature(0.90F, ReplaceBlockConfig.field_236604_a_, 35));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SOUL_SAND_NEST = register("soul_sand_nest", () -> new CavernSolitaryNestFeature(0.10F, ReplaceBlockConfig.field_236604_a_, true));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> END_NEST = register("end_nest", () -> new SolitaryNestFeature(0.15F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> OBSIDIAN_PILLAR_NEST = register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature(1.00F, ReplaceBlockConfig.field_236604_a_, 25));

    public static final RegistryObject<Feature<ReplaceBlockConfig>> OAK_WOOD_NEST_FEATURE = register("oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> SPRUCE_WOOD_NEST_FEATURE = register("spruce_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> BIRCH_WOOD_NEST_FEATURE = register("birch_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> DARK_OAK_WOOD_NEST_FEATURE = register("dark_oak_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> JUNGLE_WOOD_NEST_FEATURE = register("jungle_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.10F, ReplaceBlockConfig.field_236604_a_));
    public static final RegistryObject<Feature<ReplaceBlockConfig>> ACACIA_WOOD_NEST_FEATURE = register("acacia_wood_nest_feature", () -> new WoodSolitaryNestFeature(0.05F, ReplaceBlockConfig.field_236604_a_));

    private static <E extends IFeatureConfig> RegistryObject<Feature<E>> register(String name, Supplier<Feature<E>> supplier) {
        return FEATURES.register(name, supplier);
    }
}
