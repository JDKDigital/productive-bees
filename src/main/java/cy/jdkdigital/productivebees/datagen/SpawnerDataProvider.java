package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SpawnerDataProvider
{
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.BIOME, BiomeData::bootstrap);

    public static final ResourceKey<BiomeModifier> ADD_BEEBEE_SPAWNS = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS,
            Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "add_beebee_spawns")
    );

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ADD_BEEBEE_SPAWNS,
                new BiomeModifiers.AddSpawnsBiomeModifier(
                        biomes.getOrThrow(ModTags.BEEBEE_SPAWN_BIOMES),
                        WeightedList.<MobSpawnSettings.SpawnerData>builder().add(new MobSpawnSettings.SpawnerData(ModEntities.CONFIGURABLE_BEE.get(), 1, 1), 10).build()
                )
        );
    }
}
