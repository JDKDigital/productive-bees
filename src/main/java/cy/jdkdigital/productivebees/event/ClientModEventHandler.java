package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.Identifier;
import cy.jdkdigital.productivebees.client.color.BeeTintSource;
import cy.jdkdigital.productivebees.client.color.CanvasBlockTintSource;
import cy.jdkdigital.productivebees.client.color.CombBlockTintSource;
import cy.jdkdigital.productivebees.client.color.WoodNestTintSource;
import cy.jdkdigital.productivebees.client.particle.*;
import cy.jdkdigital.productivebees.client.render.block.AmberBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.BottlerBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.CentrifugeBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.FeederBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.block.JarBlockEntityRenderer;
import cy.jdkdigital.productivebees.client.render.entity.DyeBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.HoarderBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.RancherBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeNestHelmetLayer;
import cy.jdkdigital.productivebees.client.render.item.JarBlockItemRenderer;
import cy.jdkdigital.productivebees.client.render.item.property.NestAngle;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import cy.jdkdigital.productivebees.client.render.entity.model.HoarderBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.MediumBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.MediumCrystalBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.MediumElvisBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.MediumFoliageBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.MediumShellBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.RancherBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.SlimBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.SlimyBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.SmallBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.ThiccBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.model.TinyBeeModel;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.fluid.HoneyFluid;
import cy.jdkdigital.productivebees.compat.geckolib.client.render.GeckoBeeRenderer;
import cy.jdkdigital.productivebees.container.gui.AdvancedBeehiveScreen;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.container.gui.BreedingChamberScreen;
import cy.jdkdigital.productivebees.container.gui.CatcherScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.container.gui.CryoStasisScreen;
import cy.jdkdigital.productivebees.container.gui.FeederScreen;
import cy.jdkdigital.productivebees.container.gui.GeneIndexerScreen;
import cy.jdkdigital.productivebees.container.gui.HoneyGeneratorScreen;
import cy.jdkdigital.productivebees.container.gui.IncubatorScreen;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModParticles;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.core.RegistryAccess;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import cy.jdkdigital.productivebees.client.render.item.property.GeneAttributeProperty;
import cy.jdkdigital.productivebees.client.render.item.property.HoneyTreatVariantProperty;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

@EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT)
public class ClientModEventHandler
{

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModContainerTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveScreen::new);
        // Centrifuge variants share one Screen class generic over the menu type — wildcards in
        // MenuType<? extends CentrifugeContainer<...>> prevent the bare ::new method reference
        // from resolving, so route each through an explicit lambda.
        event.register((MenuType) ModContainerTypes.CENTRIFUGE.get(),
                (MenuScreens.ScreenConstructor<CentrifugeContainer<CentrifugeBlockEntity>, CentrifugeScreen<CentrifugeContainer<CentrifugeBlockEntity>>>) (menu, inv, title) -> new CentrifugeScreen<>(menu, inv, title));
        event.register((MenuType) ModContainerTypes.POWERED_CENTRIFUGE.get(),
                (MenuScreens.ScreenConstructor<CentrifugeContainer<CentrifugeBlockEntity>, CentrifugeScreen<CentrifugeContainer<CentrifugeBlockEntity>>>) (menu, inv, title) -> new CentrifugeScreen<>(menu, inv, title));
        event.register((MenuType) ModContainerTypes.HEATED_CENTRIFUGE.get(),
                (MenuScreens.ScreenConstructor<CentrifugeContainer<CentrifugeBlockEntity>, CentrifugeScreen<CentrifugeContainer<CentrifugeBlockEntity>>>) (menu, inv, title) -> new CentrifugeScreen<>(menu, inv, title));
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
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee"), BeeTintSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(JarBlockItemRenderer.Unbaked.ID, JarBlockItemRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerRangeSelectItemModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "nest_angle"), NestAngle.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerSelectItemModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "gene_attribute"), GeneAttributeProperty.TYPE);
        event.register(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "honey_treat_variant"), HoneyTreatVariantProperty.TYPE);
    }

    @SubscribeEvent
    public static void onClientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (event.getPlayer() != null) {
            var registries = event.getPlayer().connection.registryAccess();
            BeeRegistries.setRegistries(registries);
            BeeRegistries.evaluateRuntimeGates(registries);
        }
    }

    @SubscribeEvent
    public static void onClientTagsUpdated(TagsUpdatedEvent event) {
        RegistryAccess registries = event.getRegistries();
        BeeRegistries.setRegistries(registries);
        BeeRegistries.evaluateRuntimeGates(registries);
        BeeIngredientFactory.invalidate();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        BeeRegistries.setRegistries(null);
        BeeIngredientFactory.invalidate();
    }

    @SubscribeEvent
    public static void onClientLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            BeeIngredient.clearEntityCache();
        }
    }

    @SubscribeEvent
    public static void registerBlockTintSources(RegisterColorHandlersEvent.BlockTintSources event) {
        registerWoodNestTint(event, ModBlocks.OAK_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.SPRUCE_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.DARK_OAK_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.BIRCH_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.JUNGLE_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.ACACIA_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.CHERRY_WOOD_NEST.get());
        registerWoodNestTint(event, ModBlocks.MANGROVE_WOOD_NEST.get());

        // Grass-tinted nests. sugarCane() returns -1 in inventory; grass() supplies the
        // default grass colour there.
        event.register(List.of(BlockTintSources.sugarCane()), ModBlocks.SUGAR_CANE_NEST.get());
        event.register(List.of(BlockTintSources.grass()), ModBlocks.BUMBLE_BEE_NEST.get());

        // Configurable / fixed comb blocks. Each CombBlock-subclass renders tintindex 0 only.
        for (var holder : ProductiveBees.BLOCKS.getEntries()) {
            if (holder.get() instanceof CombBlock combBlock) {
                event.register(List.of(new CombBlockTintSource(combBlock)), combBlock);
            }
        }

        // Non-textured advanced beehives + expansion boxes — tintindex 0 = primary type colour.
        // Iterates HIVELIST so per-mod entries with a primary colour become constant tint sources.
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            if (ProductiveBees.includeMod(modid)) {
                strings.forEach((typeName, type) -> {
                    if (!type.hasTexture()) {
                        registerNonTexturedHiveTint(event, modid, typeName, type);
                    }
                });
            }
        });

        // Canvas hives + expansion boxes — tintindex 0 reads dye colour from the canvas BE.
        ModBlocks.CANVAS_HIVES.values().forEach(holder -> event.register(List.of(CanvasBlockTintSource.INSTANCE, NO_TINT), holder.get()));
        ModBlocks.CANVAS_EXPANSIONS.values().forEach(holder -> event.register(List.of(CanvasBlockTintSource.INSTANCE, NO_TINT), holder.get()));
    }

    // Wood-nest opening face uses tintindex 1; tintindex 0 keeps the native log texture.
    private static final BlockTintSource NO_TINT = new BlockTintSource() {
        @Override public int color(BlockState state) { return -1; }
    };

    private static void registerWoodNestTint(RegisterColorHandlersEvent.BlockTintSources event, Block block) {
        if (block instanceof WoodNest woodNest) {
            event.register(List.of(NO_TINT, new WoodNestTintSource(woodNest.getColor(1))), block);
        }
    }

    private static void registerNonTexturedHiveTint(RegisterColorHandlersEvent.BlockTintSources event, String modid, String typeName, HiveType type) {
        String prefix = modid.equals(ProductiveBees.MODID) ? typeName : modid + "_" + typeName;
        Block hive = ModBlocks.HIVES.get("advanced_" + prefix + "_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + prefix).get();
        int color = TextColor.parseColor(type.primary()).result().map(TextColor::getValue).orElse(-1);
        BlockTintSource primaryTint = BlockTintSources.constant(color);
        event.register(List.of(primaryTint, NO_TINT), hive, box);
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(
                new FluidModel.Unbaked(
                        new Material(HoneyFluid.STILL),
                        new Material(HoneyFluid.FLOWING),
                        new Material(HoneyFluid.OVERLAY),
                        BlockTintSources.constant(0xffffc916)),
                ModFluids.HONEY, ModFluids.HONEY_FLOWING);
    }

    @SubscribeEvent
    public static void addBeeNestHelmetLayer(EntityRenderersEvent.AddLayers event) {
        for (PlayerModelType skin : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> playerRenderer = event.getPlayerRenderer(skin);
            if (playerRenderer != null) {
                playerRenderer.addLayer(new BeeNestHelmetLayer<>(playerRenderer, event.getContext().getBlockModelResolver()));
            }
            AvatarRenderer<ClientMannequin> mannequinRenderer = event.getMannequinRenderer(skin);
            if (mannequinRenderer != null) {
                mannequinRenderer.addLayer(new BeeNestHelmetLayer<>(mannequinRenderer, event.getContext().getBlockModelResolver()));
            }
        }
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
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_TINY_LAYER, TinyBeeModel::createLayer);
    }

    @SuppressWarnings("unchecked")
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
            } else if (key.contains("configurable") && ModList.get().isLoaded("geckolib")) {
                event.registerEntityRenderer((EntityType<? extends GeckoBee>) bee, GeckoBeeRenderer::new);
            } else {
                event.registerEntityRenderer((EntityType<? extends ProductiveBee>) bee, ProductiveBeeRenderer::new);
            }
        }

        for (DeferredHolder<EntityType<?>, ? extends EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            event.registerEntityRenderer((EntityType<? extends ProductiveBee>) registryObject.get(), ProductiveBeeRenderer::new);
        }

        event.registerEntityRenderer(ModEntities.BEE_BOMB.get(), ThrownItemRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), CentrifugeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.BOTTLER.get(), BottlerBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.FEEDER.get(), FeederBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.JAR.get(), JarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.AMBER.get(), AmberBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.COLORED_FALLING_NECTAR.get(), FallingNectarParticle.FallingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_RISING_NECTAR.get(), RisingNectarParticle.RisingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_POPPING_NECTAR.get(), PoppingNectarParticle.PoppingNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_LAVA_NECTAR.get(), LavaNectarParticle.LavaNectarFactory::new);
        event.registerSpriteSet(ModParticles.COLORED_PORTAL_NECTAR.get(), PortalNectarParticle.PortalNectarFactory::new);
    }
}
