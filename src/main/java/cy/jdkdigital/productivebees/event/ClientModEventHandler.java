package cy.jdkdigital.productivebees.event;

import com.mojang.serialization.DataResult;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.model.BeeNestHelmetModel;
import cy.jdkdigital.productivebees.client.particle.*;
import cy.jdkdigital.productivebees.client.render.block.*;
import cy.jdkdigital.productivebees.client.render.entity.DyeBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.HoarderBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.RancherBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.model.*;
import cy.jdkdigital.productivebees.common.block.CanvasBeehive;
import cy.jdkdigital.productivebees.common.block.CanvasExpansionBox;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasExpansionBoxBlockEntity;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.*;
import cy.jdkdigital.productivebees.container.gui.*;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.ColorUtil;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.common.item.AbstractUpgradeItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler
{
    @SubscribeEvent
    public static void tabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ProductiveBees.TAB_KEY)) {
            for (DeferredHolder<Item, ? extends Item> item: ProductiveBees.ITEMS.getEntries()) {
                if (
                        !item.equals(ModItems.CONFIGURABLE_HONEYCOMB) &&
                        !item.equals(ModItems.CONFIGURABLE_COMB_BLOCK) &&
                        !item.equals(ModItems.CONFIGURABLE_SPAWN_EGG) &&
                        !item.equals(ModItems.GENE) &&
                        !item.equals(ModItems.GENE_BOTTLE) &&
                        !item.equals(ModItems.ADV_BREED_ALL_BEES) &&
                        !item.equals(ModItems.ADV_BREED_BEE) &&
                        !item.equals(ModItems.UPGRADE_BASE) &&
                        !(item.get() instanceof SpawnEggItem) &&
                        !(item.get() instanceof AbstractUpgradeItem)
                ) {
                    event.accept(new ItemStack(item.get(), 1));
                }
            }

            for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                ResourceLocation beeType = entry.getKey();

                // Add comb item
                if (entry.getValue().getBoolean("createComb")) {
                    ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    BeeCreator.setType(beeType, comb);

                    event.accept(comb);

                    // Add comb block
                    ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                    BeeCreator.setType(beeType, combBlock);

                    event.accept(combBlock);
                }
            }

            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_MEDIUM, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_HIGH, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.PRODUCTIVITY, GeneValue.PRODUCTIVITY_VERY_HIGH, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_NONE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_RAIN, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_ANY, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_DIURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_NOCTURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_METATURNAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_PASSIVE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_HOSTILE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.TEMPER, GeneValue.TEMPER_AGGRESSIVE, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_WEAK, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_NORMAL, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_MEDIUM, 1, 100));
            event.accept(Gene.getStack(GeneAttribute.ENDURANCE, GeneValue.ENDURANCE_STRONG, 1, 100));
        }

        if (event.getTabKey().equals(ProductiveBees.TAB_KEY) || event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            for (DeferredHolder<Item, ? extends Item> spawnEgg: ModItems.SPAWN_EGGS) {
                if (!spawnEgg.equals(ModItems.CONFIGURABLE_SPAWN_EGG)) {
                    event.accept(new ItemStack(spawnEgg));
                }
            }
            for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                ResourceLocation beeType = entry.getKey();
                // Add spawn egg item
                event.accept(BeeCreator.getSpawnEgg(beeType));
            }
        }
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        for (DeferredHolder<Item, ? extends Item> eggItem : ModItems.SPAWN_EGGS) {
            Item item = eggItem.get();
            if (item instanceof SpawnEgg) {
                event.register((stack, tintIndex) -> ((SpawnEgg) item).getColor(tintIndex, stack), item);
            }
        }

        // Honeycomb colors
        for (DeferredHolder<Item, ? extends Item> registryItem : ProductiveBees.ITEMS.getEntries()) {
            Item item = registryItem.get();
            if (item instanceof Honeycomb) {
                event.register(((Honeycomb) item)::getColor, item);
            } else if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof CombBlock) {
                    event.register((stack, tintIndex) -> ((CombBlock) block).getColor(stack), item);
                }
                // tinted opening for nests
                if (block instanceof WoodNest) {
                    event.register((stack, tintIndex) -> ((WoodNest) block).getColor(tintIndex), block);
                }
            }
        }

        event.register((stack, tintIndex) -> {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, tintIndex);
        }, ModBlocks.BUMBLE_BEE_NEST.get());

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    if (!type.hasTexture()) {
                        name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        DataResult<TextColor> primary = TextColor.parseColor(type.primary());
                        event.register((stack, tintIndex) -> tintIndex == 0 ? primary.result().get().getValue() : -1, ModBlocks.HIVES.get("advanced_" + name + "_beehive").get(), ModBlocks.EXPANSIONS.get("expansion_box_" + name).get());
                    }
                });
            }
        });
        ModBlocks.hiveStyles.forEach(style -> event.register((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof BlockItem blockItem) {
                if ((blockItem.getBlock() instanceof CanvasBeehive || blockItem.getBlock() instanceof CanvasExpansionBox) && stack.has(DataComponents.DYED_COLOR)) {
                    return stack.get(DataComponents.DYED_COLOR).rgb();
                }
            }
            return 16777215;
        }, ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get(), ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get()));
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : -1;
        }, ModBlocks.SUGAR_CANE_NEST.get());

        event.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : GrassColor.get(0.5D, 1.0D);
        }, ModBlocks.BUMBLE_BEE_NEST.get());

        for (DeferredHolder<Block, ? extends Block> registryBlock : ProductiveBees.BLOCKS.getEntries()) {
            Block block = registryBlock.get();
            if (block instanceof CombBlock) {
                event.register((blockState, lightReader, pos, tintIndex) -> tintIndex == 0 ? ((CombBlock) block).getColor(lightReader, pos) : -1, block);
            }
            if (block instanceof WoodNest) {
                event.register((blockState, lightReader, pos, tintIndex) -> ((WoodNest) block).getColor(tintIndex), block);
            }
        }

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((name, type) -> {
                    if (!type.hasTexture()) {
                        name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        event.register((blockState, lightReader, pos, tintIndex) -> tintIndex == 0 && type.primary() != null ? ColorUtil.getCacheColor(type.primary()) : -1, ModBlocks.HIVES.get("advanced_" + name + "_beehive").get(), ModBlocks.EXPANSIONS.get("expansion_box_" + name).get());
                    }
                });
            }
        });

        ModBlocks.hiveStyles.forEach(style -> event.register((blockState, lightReader, pos, tintIndex) -> {
            if (tintIndex == 0 && pos != null && (blockState.getBlock() instanceof CanvasBeehive || blockState.getBlock() instanceof CanvasExpansionBox) && lightReader != null) {
                if (lightReader.getBlockEntity(pos) instanceof CanvasBeehiveBlockEntity canvasBlockEntity) {
                    return canvasBlockEntity.getColor(tintIndex);
                }
                if (lightReader.getBlockEntity(pos) instanceof CanvasExpansionBoxBlockEntity canvasBlockEntity) {
                    return canvasBlockEntity.getColor(tintIndex);
                }
            }
            return -1;
        }, ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get(), ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get()));
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_MAIN_LAYER, ProductiveBeeModel::createBodyLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_HOARDER_LAYER, HoarderBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_RANCHER_LAYER, RancherBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_THICC_LAYER, ThiccBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_DEFAULT_LAYER, MediumBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_DEFAULT_CRYSTAL_LAYER, MediumCrystalBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_DEFAULT_SHELL_LAYER, MediumShellBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_DEFAULT_FOLIAGE_LAYER, MediumFoliageBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_ELVIS_LAYER, MediumElvisBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_SLIM_LAYER, SlimBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_SLIMY_LAYER, SlimyBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_SMALL_LAYER, SmallBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_SMALL_LAYER, SmallBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_TINY_LAYER, TinyBeeModel::createLayer);

        event.registerLayerDefinition(BeeNestHelmetModel.LAYER_LOCATION, BeeNestHelmetModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRendering(EntityRenderersEvent.RegisterRenderers event) {
        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
            EntityType<?> bee = registryObject.get();
            String key = bee.getDescriptionId();
            if (key.contains("dye_bee")) {
                event.registerEntityRenderer((EntityType<? extends ProductiveBee>) bee, DyeBeeRenderer::new);
            } else if (key.contains("rancher_bee") || key.contains("farmer_bee")) {
                event.registerEntityRenderer((EntityType<? extends ProductiveBee>) bee, RancherBeeRenderer::new);
            } else if (key.contains("hoarder_bee")) {
                event.registerEntityRenderer((EntityType<? extends ProductiveBee>) bee, HoarderBeeRenderer::new);
            } else {
                event.registerEntityRenderer((EntityType<? extends ProductiveBee>) bee, ProductiveBeeRenderer::new);
            }
        }

        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            event.registerEntityRenderer((EntityType<? extends ProductiveBee>) registryObject.get(), ProductiveBeeRenderer::new);
        }

        event.registerEntityRenderer(ModEntities.BEE_BOMB.get(), ThrownItemRenderer::new);
    }

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
            ItemProperties.register(ModItems.BEE_CAGE.get(), ResourceLocation.withDefaultNamespace("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.STURDY_BEE_CAGE.get(), ResourceLocation.withDefaultNamespace("filled"), (stack, world, entity, i) -> BeeCage.isFilled(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.BEE_BOMB.get(), ResourceLocation.withDefaultNamespace("loaded"), (stack, world, entity, i) -> BeeBomb.isLoaded(stack) ? 1.0F : 0.0F);
            ItemProperties.register(ModItems.HONEY_TREAT.get(), ResourceLocation.withDefaultNamespace("genetic"), (stack, world, entity, i) -> HoneyTreat.hasGene(stack) ? (HoneyTreat.hasBeeType(stack) ? 0.5F : 1.0F) : 0.0F);
            ItemProperties.register(ModItems.GENE.get(), ResourceLocation.withDefaultNamespace("genetic"), (stack, world, entity, i) -> Gene.color(stack));
            ItemProperties.register(ModItems.NEST_LOCATOR.get(), ResourceLocation.withDefaultNamespace("angle"), new ClampedItemPropertyFunction() {
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
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.BOTTLER.get(), BottlerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.FEEDER.get(), FeederBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.JAR.get(), JarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.AMBER.get(), AmberBlockEntityRenderer::new);
    }

//    @SubscribeEvent
//    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
//        event.registerFluidType(new IClientFluidTypeExtensions() {
//            @Override
//            public ResourceLocation getStillTexture() {
//                return HoneyFluid.STILL;
//            }
//
//            @Override
//            public ResourceLocation getFlowingTexture() {
//                return HoneyFluid.FLOWING;
//            }
//
//            @Override
//            public ResourceLocation getOverlayTexture() {
//                return HoneyFluid.OVERLAY;
//            }
//
//            @Override
//            public int getTintColor() {
//                return 0xffffc916;
//            }
//        }, ModFluids.HONEY_FLUID_TYPE.get());
//    }
}
