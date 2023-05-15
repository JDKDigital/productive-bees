package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.recipe.provider.CentrifugeRecipeProvider;
import cy.jdkdigital.productivebees.datagen.recipe.provider.HiveRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProductiveBeeDataProvider
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeClient(), new BlockstateProvider(gen.getPackOutput()));

        gen.addProvider(event.includeServer(), new BlockLootProvider(gen.getPackOutput()));
//        gen.addProvider(event.includeServer(), new CentrifugeRecipeProvider(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new HiveRecipeProvider(gen.getPackOutput()));
    }
}
