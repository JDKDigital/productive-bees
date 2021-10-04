package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.client.particle.*;
import cy.jdkdigital.productivebees.client.render.block.BottlerTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.FeederTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.JarTileEntityRenderer;
import cy.jdkdigital.productivebees.common.item.BeeBomb;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.item.NestLocator;
import cy.jdkdigital.productivebees.container.gui.*;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        MenuScreens.register(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        MenuScreens.register(ModContainerTypes.POWERED_CENTRIFUGE.get(), CentrifugeScreen::new);
        MenuScreens.register(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);
        MenuScreens.register(ModContainerTypes.FEEDER.get(), FeederScreen::new);
        MenuScreens.register(ModContainerTypes.INCUBATOR.get(), IncubatorScreen::new);
        MenuScreens.register(ModContainerTypes.CATCHER.get(), CatcherScreen::new);
        MenuScreens.register(ModContainerTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);
        MenuScreens.register(ModContainerTypes.GENE_INDEXER.get(), GeneIndexerScreen::new);

        ItemProperties.register(ModItems.BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.STURDY_BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.BEE_BOMB.get(), new ResourceLocation("loaded"), (stack, world, entity, i) -> BeeBomb.isLoaded(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.HONEY_TREAT.get(), new ResourceLocation("genetic"), (stack, world, entity, i) -> HoneyTreat.hasGene(stack) ? 1.0F : 0.0F);
        ItemProperties.register(ModItems.NEST_LOCATOR.get(), new ResourceLocation("angle"), new ClampedItemPropertyFunction()
        {
            private double rotation;
            private double rota;
            private long lastUpdateTick;

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

                        if (flag) {
                            d0 = this.wobble(world, d0);
                        }

                        return Mth.positiveModulo((float) d0, 1.0F);
                    }
                }
                return 0.0F;
            }

            private double wobble(Level worldIn, double amount) {
                if (worldIn.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = amount - this.rotation;
                    d0 = Mth.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            private double getFrameRotation(ItemFrame frameEntity) {
                return Mth.wrapDegrees(180 + frameEntity.getDirection().get2DDataValue() * 90);
            }

            private double getPositionToAngle(BlockPos blockpos, Entity entityIn) {
                return Math.atan2((double) blockpos.getZ() - entityIn.getZ(), (double) blockpos.getX() - entityIn.getX());
            }
        });

        BlockEntityRenderers.register(ModTileEntityTypes.CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        BlockEntityRenderers.register(ModTileEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeTileEntityRenderer::new);
        BlockEntityRenderers.register(ModTileEntityTypes.BOTTLER.get(), BottlerTileEntityRenderer::new);
        BlockEntityRenderers.register(ModTileEntityTypes.FEEDER.get(), FeederTileEntityRenderer::new);
        BlockEntityRenderers.register(ModTileEntityTypes.JAR.get(), JarTileEntityRenderer::new);

        registerBlockRendering();
    }

    public static void registerParticles(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(ModParticles.COLORED_FALLING_NECTAR.get(), FallingNectarParticle.FallingNectarFactory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.COLORED_RISING_NECTAR.get(), RisingNectarParticle.RisingNectarFactory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.COLORED_POPPING_NECTAR.get(), PoppingNectarParticle.PoppingNectarFactory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.COLORED_LAVA_NECTAR.get(), LavaNectarParticle.LavaNectarFactory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.COLORED_PORTAL_NECTAR.get(), PortalNectarParticle.PortalNectarFactory::new);
    }

    private static void registerBlockRendering() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.COMB_GHOSTLY.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SLIMY_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BUMBLE_BEE_NEST.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SUGAR_CANE_NEST.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.JAR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.INVISIBLE_REDSTONE_BLOCK.get(), RenderType.cutout());
    }
}
