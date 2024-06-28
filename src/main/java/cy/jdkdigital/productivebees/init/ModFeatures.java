package cy.jdkdigital.productivebees.init;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ProductiveBees.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, ProductiveBees.MODID);

    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> SAND_NEST = FEATURES.register("sand_nest", () -> new SolitaryNestFeature("sand_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> SNOW_NEST = FEATURES.register("snow_nest", () -> new SolitaryNestFeature("snow_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> STONE_NEST = FEATURES.register("stone_nest", () -> new SolitaryNestFeature("stone_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> COARSE_DIRT_NEST = FEATURES.register("coarse_dirt_nest", () -> new SolitaryNestFeature("coarse_dirt_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> GRAVEL_NEST = FEATURES.register("gravel_nest", () -> new SolitaryNestFeature("gravel_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> SLIMY_NEST = FEATURES.register("slimy_nest", () -> new SolitaryNestFeature("slimy_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> SUGAR_CANE_NEST = FEATURES.register("sugar_cane_nest", () -> new ReedSolitaryNestFeature("sugar_cane_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> GLOWSTONE_NEST = FEATURES.register("glowstone_nest", () -> new CavernSolitaryNestFeature("glowstone_nest", ReplaceBlockConfiguration.CODEC, false));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> NETHER_QUARTZ_NEST = FEATURES.register("nether_quartz_nest", () -> new OreSolitaryNestFeature("nether_quartz_nest", ReplaceBlockConfiguration.CODEC, 10, 70));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> NETHER_QUARTZ_NEST_HIGH = FEATURES.register("nether_quartz_nest_high", () -> new OreSolitaryNestFeature("nether_quartz_nest", ReplaceBlockConfiguration.CODEC, 70, 100));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> NETHER_FORTRESS_NEST = FEATURES.register("nether_fortress_nest", () -> new StructureSolitaryNestFeature("nether_brick_nest", ReplaceBlockConfiguration.CODEC, 35));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> SOUL_SAND_NEST = FEATURES.register("soul_sand_nest", () -> new CavernSolitaryNestFeature("soul_sand_nest", ReplaceBlockConfiguration.CODEC, true));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> END_NEST = FEATURES.register("end_nest", () -> new SolitaryNestFeature("end_stone_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> OBSIDIAN_PILLAR_NEST = FEATURES.register("obsidian_pillar_nest", () -> new StructureSolitaryNestFeature("obsidian_nest", ReplaceBlockConfiguration.CODEC, 25));
    public static final DeferredHolder<Feature<?>, SolitaryNestFeature> BUMBLE_BEE_NEST = FEATURES.register("bumble_bee_nest", () -> new SolitaryNestFeature("bumble_bee_nest", ReplaceBlockConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> OAK_WOOD_NEST_FEATURE = FEATURES.register("oak_wood_nest", () -> new WoodSolitaryNestFeature("oak_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> SPRUCE_WOOD_NEST_FEATURE = FEATURES.register("spruce_wood_nest", () -> new WoodSolitaryNestFeature("spruce_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> BIRCH_WOOD_NEST_FEATURE = FEATURES.register("birch_wood_nest", () -> new WoodSolitaryNestFeature("birch_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> DARK_OAK_WOOD_NEST_FEATURE = FEATURES.register("dark_oak_wood_nest", () -> new WoodSolitaryNestFeature("dark_oak_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> JUNGLE_WOOD_NEST_FEATURE = FEATURES.register("jungle_wood_nest", () -> new WoodSolitaryNestFeature("jungle_wood_nest", ReplaceBlockConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, WoodSolitaryNestFeature> ACACIA_WOOD_NEST_FEATURE = FEATURES.register("acacia_wood_nest", () -> new WoodSolitaryNestFeature("acacia_wood_nest", ReplaceBlockConfiguration.CODEC));

    public static DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<NetherBeehiveDecorator>> NETHER_BEEHIVE = TREE_DECORATORS.register("nether_beehive", () ->  new TreeDecoratorType<>(NetherBeehiveDecorator.CODEC));
    public static DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<WoodNestDecorator>> WOOD_NEST = TREE_DECORATORS.register("wood_nest", () ->  new TreeDecoratorType<>(WoodNestDecorator.CODEC));

    public static final DeferredHolder<Feature<?>, GlowstoneNestFeature> GLOWSTONE_NEST_BLOB = FEATURES.register("glowstone_nest_blob", () -> new GlowstoneNestFeature(GlowstoneNestFeature.CODEC));
    public static final DeferredHolder<Feature<?>, OreFeature> NETHER_QUARTZ_NEST_ORE = FEATURES.register("nether_quartz_nest_ore", () -> new OreFeature(OreConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, SolitaryNestTreeFeature> SOLITARY_NEST_TREE = FEATURES.register("solitary_nest_tree", () -> new SolitaryNestTreeFeature(TreeConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, DecoratedHugeFungusFeature> DECORATED_HUGE_FUNGUS = FEATURES.register("decorated_huge_fungus", () -> new DecoratedHugeFungusFeature(DecoratedHugeFungusConfiguration.CODEC));

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ProductiveBees.MODID);
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<NestBiomeModifier>> NEST_BIOME_MODIFIER = BIOME_MODIFIERS.register("nest", NestBiomeModifier::makeCodec);
}
