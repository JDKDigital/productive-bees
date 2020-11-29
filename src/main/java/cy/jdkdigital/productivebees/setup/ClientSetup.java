package cy.jdkdigital.productivebees.setup;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.FallingNectarParticle;
import cy.jdkdigital.productivebees.client.render.block.BottlerTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.FeederTileEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.JarTileEntityRenderer;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.common.block.Jar;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import cy.jdkdigital.productivebees.container.gui.AdvancedBeehiveScreen;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.container.gui.FeederScreen;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.POWERED_CENTRIFUGE.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.BOTTLER.get(), BottlerScreen::new);
        ScreenManager.registerFactory(ModContainerTypes.FEEDER.get(), FeederScreen::new);

        ItemModelsProperties.func_239418_a_(ModItems.BEE_CAGE.get(), new ResourceLocation("filled"), (stack, world, entity) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
        ItemModelsProperties.func_239418_a_(ModItems.BEE_BOMB.get(), new ResourceLocation("loaded"), (stack, world, entity) -> BeeBomb.isLoaded(stack) ? 1.0F : 0.0F);
        ItemModelsProperties.func_239418_a_(ModItems.NEST_LOCATOR.get(), new ResourceLocation("angle"), new IItemPropertyGetter() {
            @OnlyIn(Dist.CLIENT)
            private double rotation;
            @OnlyIn(Dist.CLIENT)
            private double rota;
            @OnlyIn(Dist.CLIENT)
            private long lastUpdateTick;

            @OnlyIn(Dist.CLIENT)
            public float call(@Nonnull ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity player) {
                if ((player != null || stack.isOnItemFrame()) && NestLocator.hasPosition(stack)) {
                    boolean flag = player != null;
                    Entity entity = flag ? player : stack.getItemFrame();
                    if (world == null && entity.world instanceof ClientWorld) {
                        world = (ClientWorld) entity.world;
                    }

                    double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((ItemFrameEntity) entity);
                    d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                    double d2 = this.getPositionToAngle(NestLocator.getPosition(stack), entity) / (double)((float)Math.PI * 2F);
                    double d0 = 0.5D - (d1 - 0.25D - d2);

                    if (flag) {
                        d0 = this.wobble(world, d0);
                    }

                    return MathHelper.positiveModulo((float) d0, 1.0F);
                } else {
                    return 0.0F;
                }
            }

            @OnlyIn(Dist.CLIENT)
            private double wobble(World worldIn, double amount) {
                if (worldIn.getGameTime() != this.lastUpdateTick) {
                    this.lastUpdateTick = worldIn.getGameTime();
                    double d0 = amount - this.rotation;
                    d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
                    this.rota += d0 * 0.1D;
                    this.rota *= 0.8D;
                    this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
                }

                return this.rotation;
            }

            @OnlyIn(Dist.CLIENT)
            private double getFrameRotation(ItemFrameEntity frameEntity) {
                return MathHelper.wrapDegrees(180 + frameEntity.getHorizontalFacing().getHorizontalIndex() * 90);
            }

            @OnlyIn(Dist.CLIENT)
            private double getPositionToAngle(BlockPos blockpos, Entity entityIn) {
                return Math.atan2((double)blockpos.getZ() - entityIn.getPosZ(), (double)blockpos.getX() - entityIn.getPosX());
            }
        });

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
    }
}
