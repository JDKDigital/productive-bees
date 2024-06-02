package cy.jdkdigital.productivebees.event;

import com.mojang.serialization.DataResult;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.model.BeeNestHelmetModel;
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
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

@EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
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
                        !item.equals(ModItems.ADV_BREED_BEE)
                ) {
                    event.accept(new ItemStack(item.get(), 1));
                }
            }

            for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey();

                // Add comb item
                if (entry.getValue().getBoolean("createComb")) {
                    ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                    BeeCreator.setTag(beeType, comb);

                    event.accept(comb);

                    // Add comb block
                    ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
                    BeeCreator.setTag(beeType, combBlock);

                    event.accept(combBlock);
                }
            }

            event.accept(Gene.getStack(BeeAttributes.PRODUCTIVITY, 0, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.PRODUCTIVITY, 1, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.PRODUCTIVITY, 2, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.PRODUCTIVITY, 3, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.WEATHER_TOLERANCE, 0, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.WEATHER_TOLERANCE, 1, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.WEATHER_TOLERANCE, 2, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.BEHAVIOR, 0, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.BEHAVIOR, 1, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.BEHAVIOR, 2, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.TEMPER, 0, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.TEMPER, 1, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.TEMPER, 2, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.TEMPER, 3, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.ENDURANCE, 0, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.ENDURANCE, 1, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.ENDURANCE, 2, 1, 100));
            event.accept(Gene.getStack(BeeAttributes.ENDURANCE, 3, 1, 100));
        }

        if (event.getTabKey().equals(ProductiveBees.TAB_KEY) || event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            for (DeferredHolder<Item, ? extends Item> spawnEgg: ModItems.SPAWN_EGGS) {
                if (!spawnEgg.equals(ModItems.CONFIGURABLE_SPAWN_EGG)) {
                    event.accept(new ItemStack(spawnEgg));
                }
            }
            for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                String beeType = entry.getKey();

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
                // tinted opening
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
            if (!FMLLoader.isProduction() || ModList.get().isLoaded(modid)) {
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
            if (!FMLLoader.isProduction() || ModList.get().isLoaded(modid)) {
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
}
