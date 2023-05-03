package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredFeatures
{
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRIMSON_FUNGUS_BEES_GROWN = FeatureUtils.createKey(ProductiveBees.MODID + ":crimson_fungus_bees_grown");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WARPED_FUNGUS_BEES_GROWN = FeatureUtils.createKey(ProductiveBees.MODID + ":warped_fungus_bees_grown");
}
