package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.recipe.provider.CentrifugeRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ProductiveBeeDataProvider {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
//            gen.addProvider(new GeomancyLanguageProvider(gen));
//            gen.addProvider(new GeomancyBlockStates(gen, event.getExistingFileHelper()));
//            gen.addProvider(new GeomancyItemModelProvider(gen, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            gen.addProvider(new CentrifugeRecipeProvider(gen));
        }
    }
}
