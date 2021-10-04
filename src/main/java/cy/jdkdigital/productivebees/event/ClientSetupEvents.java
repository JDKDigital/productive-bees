package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.model.CombModel;
import cy.jdkdigital.productivebees.client.render.entity.DyeBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.HoarderBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.RancherBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.model.*;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.fmllegacy.RegistryObject;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void registerItemColors(final ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BlockColors blockColors = event.getBlockColors();

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
                colors.register(((Honeycomb) item)::getColor, item);
            } else if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof CombBlock) {
                    colors.register((stack, tintIndex) -> ((CombBlock) block).getColor(stack), item);
                }
            }
        }

        colors.register((stack, tintIndex) -> {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(blockstate, null, null, tintIndex);
        }, ModBlocks.BUMBLE_BEE_NEST.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : -1;
        }, ModBlocks.SUGAR_CANE_NEST.get());

        colors.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getAverageGrassColor(lightReader, pos) : GrassColor.get(0.5D, 1.0D);
        }, ModBlocks.BUMBLE_BEE_NEST.get());

        for (RegistryObject<Block> registryBlock : ModBlocks.BLOCKS.getEntries()) {
            Block block = registryBlock.get();
            if (block instanceof CombBlock) {
                colors.register((blockState, lightReader, pos, tintIndex) -> ((CombBlock) block).getColor(lightReader, pos), block);
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
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_SMALL_LAYER, SmallBeeModel::createLayer);
        event.registerLayerDefinition(ProductiveBeeRenderer.PB_TINY_LAYER, TinyBeeModel::createLayer);
    }

    @SubscribeEvent
    public static void registerEntityRendering(EntityRenderersEvent.RegisterRenderers event) {
        for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
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

        for (RegistryObject<EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            event.registerEntityRenderer((EntityType<? extends ProductiveBee>) registryObject.get(), ProductiveBeeRenderer::new);
        }

        event.registerEntityRenderer(ModEntities.BEE_BOMB.get(), ThrownItemRenderer::new);
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(ProductiveBees.MODID, "comb"), CombModel.Loader.INSTANCE);
    }
}
