package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.recipe.provider.HiveRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProductiveBeeDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        gen.addProvider(event.includeClient(), new BlockstateProvider(output));

        gen.addProvider(event.includeServer(), new BlockLootProvider(output));
//        gen.addProvider(event.includeServer(), new CentrifugeRecipeProvider(output));
        gen.addProvider(event.includeServer(), new HiveRecipeProvider(output));

        BlockTagProvider blockTags = new BlockTagProvider(output, provider, helper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new ItemTagProvider(output, provider, blockTags.contentsGetter(), helper));
    }
}
