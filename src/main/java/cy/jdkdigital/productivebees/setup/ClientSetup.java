package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.FallingNectarParticle;
import cy.jdkdigital.productivebees.client.particle.LavaNectarParticle;
import cy.jdkdigital.productivebees.client.particle.PoppingNectarParticle;
import cy.jdkdigital.productivebees.client.render.block.BottlerTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.FeederTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.JarTileEntityRenderer;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import cy.jdkdigital.productivebees.container.gui.*;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.POWERED_CENTRIFUGE.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.FEEDER.get(), FeederScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.INCUBATOR.get(), IncubatorScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.CATCHER.get(), CatcherScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);

        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.BOTTLER.get(), BottlerTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.FEEDER.get(), FeederTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.JAR.get(), JarTileEntityRenderer::new);

        ModEntities.registerRendering();
        ModBlocks.registerRendering();
    }

    public static void registerItemColors(final ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        for (RegistryObject<Item> eggItem : ModItems.SPAWN_EGGS) {
            if (ObfuscationReflectionHelper.getPrivateValue(RegistryObject.class, eggItem, "value") != null) {
                Item item = eggItem.get();
                if (item instanceof SpawnEgg) {
                    colors.register((stack, tintIndex) -> ((SpawnEgg) item).getColor(tintIndex, stack), item);
                }
            }
        }

        // Honeycomb colors
        for (RegistryObject<Item> registryItem : ModItems.ITEMS.getEntries()) {
            Item item = registryItem.get();
            if (item instanceof Honeycomb) {
                colors.register((stack, tintIndex) -> ((Honeycomb) item).getColor(stack), item);
            }
            else if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof CombBlock) {
                    colors.register((stack, tintIndex) -> ((CombBlock) block).getColor(stack), item);
                }
            }
        }
    }

    public static void registerBlockColors(final ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getGrassColor(lightReader, pos) : -1;
        }, ModBlocks.SUGAR_CANE_NEST.get());

        for (RegistryObject<Block> registryBlock : ModBlocks.BLOCKS.getEntries()) {
            Block block = registryBlock.get();
            if (block instanceof CombBlock) {
                colors.register((blockState, lightReader, pos, tintIndex) -> ((CombBlock) block).getColor(lightReader, pos), block);
            }
        }
    }

    public static void registerParticles(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particles.registerFactory(ModParticles.COLORED_FALLING_NECTAR.get(), FallingNectarParticle.FallingNectarFactory::new);
        Minecraft.getInstance().particles.registerFactory(ModParticles.COLORED_POPPING_NECTAR.get(), PoppingNectarParticle.PoppingNectarFactory::new);
        Minecraft.getInstance().particles.registerFactory(ModParticles.COLORED_LAVA_NECTAR.get(), LavaNectarParticle.LavaNectarFactory::new);
    }
}
