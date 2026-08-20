package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.gen.feature.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures
{
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ProductiveBees.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, ProductiveBees.MODID);

    public static final DeferredHolder<Feature<?>, NestFeature> NEST = FEATURES.register("nest", NestFeature::new);

    public static DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<NetherBeehiveDecorator>> NETHER_BEEHIVE = TREE_DECORATORS.register("nether_beehive", () -> new TreeDecoratorType<>(NetherBeehiveDecorator.CODEC));
    public static DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<WoodNestDecorator>> WOOD_NEST = TREE_DECORATORS.register("wood_nest", () -> new TreeDecoratorType<>(WoodNestDecorator.CODEC));

    public static final DeferredHolder<Feature<?>, OreFeature> NETHER_QUARTZ_NEST_ORE = FEATURES.register("nether_quartz_nest_ore", () -> new OreFeature(OreConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, DecoratedHugeFungusFeature> DECORATED_HUGE_FUNGUS = FEATURES.register("decorated_huge_fungus", () -> new DecoratedHugeFungusFeature(DecoratedHugeFungusConfiguration.CODEC));
}
