package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class ModConfiguredFeatures
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_FUNGUS_BEES_GROWN = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "crimson_fungus_bees_grown"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> WARPED_FUNGUS_BEES_GROWN = ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "warped_fungus_bees_grown"));
}
