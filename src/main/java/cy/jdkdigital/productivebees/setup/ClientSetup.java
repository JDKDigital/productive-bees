package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.*;
import cy.jdkdigital.productivebees.client.render.block.*;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.container.gui.*;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModParticles;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void init(final RegisterMenuScreensEvent event) {
        event.register(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        event.register(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        event.register(ModContainerTypes.POWERED_CENTRIFUGE.get(), CentrifugeScreen::new);
        event.register(ModContainerTypes.HEATED_CENTRIFUGE.get(), CentrifugeScreen::new);
        event.register(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);
        event.register(ModContainerTypes.FEEDER.get(), FeederScreen::new);
        event.register(ModContainerTypes.INCUBATOR.get(), IncubatorScreen::new);
        event.register(ModContainerTypes.CATCHER.get(), CatcherScreen::new);
        event.register(ModContainerTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);
        event.register(ModContainerTypes.GENE_INDEXER.get(), GeneIndexerScreen::new);
        event.register(ModContainerTypes.BREEDING_CHAMBER.get(), BreedingChamberScreen::new);
        event.register(ModContainerTypes.CRYO_STASIS.get(), CryoStasisScreen::new);
    }

    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(ModItems.BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.STURDY_BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BEE_BOMB.get(), new ResourceLocation("loaded"), (stack, world, entity, i) -> BeeBomb.isLoaded(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.HONEY_TREAT.get(), new ResourceLocation("genetic"), (stack, world, entity, i) -> HoneyTreat.hasGene(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.GENE.get(), new ResourceLocation("genetic"), (stack, world, entity, i) -> Gene.color(stack));
            ItemProperties.register(ModItems.NEST_LOCATOR.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction() {
                public float unclampedCall(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity player, int i) {
                    if ((player != null || stack.isFramed()) && NestLocator.hasPosition(stack)) {
                        boolean flag = player != null;
                        Entity entity = flag ? player : stack.getFrame();
                        if (level == null && entity != null && entity.level() instanceof ClientLevel) {
                            level = (ClientLevel) entity.level();
                        }
                        BlockPos pos = NestLocator.getPosition(stack);
                        if (entity != null && level != null && pos != null) {
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
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.COLORED_FALLING_NECTAR.get(), FallingNectarParticle.FallingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_RISING_NECTAR.get(), RisingNectarParticle.RisingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_POPPING_NECTAR.get(), PoppingNectarParticle.PoppingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_LAVA_NECTAR.get(), LavaNectarParticle.LavaNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_PORTAL_NECTAR.get(), PortalNectarParticle.PortalNectarFactory::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.BOTTLER.get(), BottlerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.FEEDER.get(), FeederBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.JAR.get(), JarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.AMBER.get(), AmberBlockEntityRenderer::new);
    }
}
