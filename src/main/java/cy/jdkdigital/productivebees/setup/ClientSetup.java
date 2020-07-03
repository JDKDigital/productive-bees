package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.block.BottlerTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeTileEntityRenderer;
import cy.jdkdigital.productivebees.container.gui.AdvancedBeehiveScreen;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);

        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BOTTLER.get(), BottlerTileEntityRenderer::new);
    }
}
