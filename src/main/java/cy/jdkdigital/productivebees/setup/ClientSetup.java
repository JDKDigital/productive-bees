package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.*;
import cy.jdkdigital.productivebees.client.render.block.BottlerTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.FeederBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.JarTileEntityRenderer;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.container.gui.*;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
            MenuScreens.register(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
            MenuScreens.register(ModContainerTypes.POWERED_CENTRIFUGE.get(), CentrifugeScreen::new);
            MenuScreens.register(ModContainerTypes.HEATED_CENTRIFUGE.get(), CentrifugeScreen::new);
            MenuScreens.register(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);
            MenuScreens.register(ModContainerTypes.FEEDER.get(), FeederScreen::new);
            MenuScreens.register(ModContainerTypes.INCUBATOR.get(), IncubatorScreen::new);
            MenuScreens.register(ModContainerTypes.CATCHER.get(), CatcherScreen::new);
            MenuScreens.register(ModContainerTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);
            MenuScreens.register(ModContainerTypes.GENE_INDEXER.get(), GeneIndexerScreen::new);
            MenuScreens.register(ModContainerTypes.BREEDING_CHAMBER.get(), BreedingChamberScreen::new);

            ItemProperties.register(ModItems.BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.STURDY_BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BEE_BOMB.get(), new ResourceLocation("loaded"), (stack, world, entity, i) -> BeeBomb.isLoaded(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.HONEY_TREAT.get(), new ResourceLocation("genetic"), (stack, world, entity, i) -> HoneyTreat.hasGene(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.GENE.get(), new ResourceLocation("genetic"), (stack, world, entity, i) -> Gene.color(stack));
            ItemProperties.register(ModItems.NEST_LOCATOR.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
                public float unclampedCall(@Nonnull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity player, int i) {
                    if ((player != null || stack.isFramed()) && NestLocator.hasPosition(stack)) {
                        boolean flag = player != null;
                        Entity entity = flag ? player : stack.getFrame();
                        if (world == null && entity != null && entity.level instanceof ClientLevel) {
                            world = (ClientLevel) entity.level;
                        }
                        BlockPos pos = NestLocator.getPosition(stack);
                        if (entity != null && world != null && pos != null) {
                            double d1 = flag ? (double) entity.getYRot() : this.getFrameRotation((ItemFrame) entity);
                            d1 = Mth.positiveModulo(d1 / 360.0D, 1.0D);
                            double d2 = this.getPositionToAngle(pos, entity) / (double) ((float) Math.PI * 2F);
                            double d0 = 0.5D - (d1 - 0.25D - d2);

                            return Mth.positiveModulo((float) d0, 1.0F);
                        }
                    }
                    return 0.5F;
                }

                private double getFrameRotation(ItemFrame frameEntity) {
                    return Mth.wrapDegrees(180 + frameEntity.getDirection().get2DDataValue() * 90);
                }

                private double getPositionToAngle(BlockPos blockpos, Entity entityIn) {
                    return Math.atan2((double) blockpos.getZ() - entityIn.getZ(), (double) blockpos.getX() - entityIn.getX());
                }
            });
        });

        registerBlockRendering();
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.register(ModParticles.COLORED_FALLING_NECTAR.get(), FallingNectarParticle.FallingNectarFactory::new);
        event.register(ModParticles.COLORED_RISING_NECTAR.get(), RisingNectarParticle.RisingNectarFactory::new);
        event.register(ModParticles.COLORED_POPPING_NECTAR.get(), PoppingNectarParticle.PoppingNectarFactory::new);
        event.register(ModParticles.COLORED_LAVA_NECTAR.get(), LavaNectarParticle.LavaNectarFactory::new);
        event.register(ModParticles.COLORED_PORTAL_NECTAR.get(), PortalNectarParticle.PortalNectarFactory::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.BOTTLER.get(), BottlerTileEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.FEEDER.get(), FeederBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.JAR.get(), JarTileEntityRenderer::new);
    }

    private static void registerBlockRendering() {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_FLOWING.get(), RenderType.translucent());
    }
}
