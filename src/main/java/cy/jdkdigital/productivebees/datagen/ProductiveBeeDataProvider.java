package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.recipe.provider.RecipeProvider;
import net.minecraft.core.Cloner;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveBees.MODID)
public class ProductiveBeeDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = CompletableFuture.supplyAsync(() -> getProvider().full());

        ExistingFileHelper helper = event.getExistingFileHelper();

        gen.addProvider(event.includeServer(), new BeeProvider(output));
        gen.addProvider(event.includeClient(), new BlockstateProvider(output));
        gen.addProvider(event.includeServer(), new BlockLootProvider(output, List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        gen.addProvider(event.includeServer(), new RecipeProvider(output, provider));
        gen.addProvider(event.includeServer(), new LootModifierProvider(output, provider));
        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
        gen.addProvider(event.includeServer(), new EntityTagProvider(output, provider, helper));

        gen.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                output, CompletableFuture.supplyAsync(ProductiveBeeDataProvider::getProvider), Set.of(ProductiveBees.MODID)));
    }

    private static RegistrySetBuilder.PatchedRegistries getProvider() {
        final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();

        registryBuilder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SpawnerDataProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, $ -> {});

        RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        Cloner.Factory cloner$factory = new Cloner.Factory();

        net.neoforged.neoforge.registries.DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().forEach(data -> data.runWithArguments(cloner$factory::addCodec));

        return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup(), cloner$factory);
    }
}
