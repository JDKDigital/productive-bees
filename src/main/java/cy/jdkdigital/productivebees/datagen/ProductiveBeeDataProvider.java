package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.recipe.provider.BeeBreedingRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.BeeConversionRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.BeeProduceRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.BeeSpawningRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.CentrifugeRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.CompatJsonRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.CompatRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.ConversionRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.MiscRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.OritechRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.RecipeProvider;
import cy.jdkdigital.productivebees.gametest.GameTestStructureProvider;
import cy.jdkdigital.productivebees.gametest.TestEntriesProvider;
import cy.jdkdigital.productivebees.init.ModTrades;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
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
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ProductiveBees.MODID)
public class ProductiveBeeDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = CompletableFuture.supplyAsync(() -> getProvider().full());

        BeeProvider.populateDatagenBridge();
        gen.addProvider(true, new BlockLootProvider(output, List.of(new LootTableProvider.SubProviderEntry(BlockLootProvider.LootProvider::new, LootContextParamSets.BLOCK)), provider));
        gen.addProvider(true, new RecipeProvider.Runner(output, provider));
        gen.addProvider(true, new BeeProduceRecipeProvider.Runner(output, provider));
        gen.addProvider(true, new CentrifugeRecipeProvider(output));
        gen.addProvider(true, new BeeConversionRecipeProvider(output));
        gen.addProvider(true, new BeeBreedingRecipeProvider(output));
        gen.addProvider(true, new BeeSpawningRecipeProvider(output));
        gen.addProvider(true, new ConversionRecipeProvider(output));
        gen.addProvider(true, new MiscRecipeProvider(output));
        gen.addProvider(true, new OritechRecipeProvider.Runner(output, provider));
        gen.addProvider(true, new CompatRecipeProvider.Runner(output, provider));
        gen.addProvider(true, new CompatJsonRecipeProvider(output));
        gen.addProvider(true, new LootModifierProvider(output, provider));
        BlockTagProvider blockTags = new BlockTagProvider(output, provider);
        gen.addProvider(true, blockTags);
        gen.addProvider(true, new ItemTagProvider(output, provider, blockTags.contentsGetter()));
        gen.addProvider(true, new EntityTagProvider(output, provider));
        gen.addProvider(true, new FluidTagProvider(output, provider));
        gen.addProvider(true, new PoiTagProvider(output, provider));
        gen.addProvider(true, new BiomeTagProvider(output, provider));
        gen.addProvider(true, new GameTestStructureProvider(output));
        gen.addProvider(true, new TestEntriesProvider(output));

        gen.addProvider(true, new DatapackBuiltinEntriesProvider(
                output,
                CompletableFuture.supplyAsync(ProductiveBeeDataProvider::getProvider),
                BeeProvider::appendConditions,
                Set.of(ProductiveBees.MODID))
        );

        gen.addProvider(true, new BlockstateProvider(output));
        gen.addProvider(true, new EnglishLangProvider(output));
        gen.addProvider(true, new PBAdvancementProvider(output, provider));
    }

    private static RegistrySetBuilder.PatchedRegistries getProvider() {
        final RegistrySetBuilder registryBuilder = new RegistrySetBuilder();

        registryBuilder.add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, SpawnerDataProvider::bootstrap);
        registryBuilder.add(Registries.BIOME, $ -> {});
        registryBuilder.add(Registries.VILLAGER_TRADE, ModTrades::bootstrapTrades);
        registryBuilder.add(Registries.TRADE_SET, ModTrades::bootstrapTradeSets);
        registryBuilder.add(BeeRegistries.BEE_DATA, BeeProvider::bootstrap);

        RegistryAccess.Frozen regAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        Cloner.Factory cloner$factory = new Cloner.Factory();

        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().forEach(data -> data.runWithArguments(cloner$factory::addCodec));

        return registryBuilder.buildPatch(regAccess, VanillaRegistries.createLookup(), cloner$factory);
    }
}
