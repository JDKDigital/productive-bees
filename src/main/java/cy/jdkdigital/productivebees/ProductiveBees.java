package cy.jdkdigital.productivebees;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.ZombieBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.solitary.BlueBandedBeeEntity;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.ClientProxy;
import cy.jdkdigital.productivebees.setup.ClientSetup;
import cy.jdkdigital.productivebees.setup.IProxy;
import cy.jdkdigital.productivebees.setup.ServerProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(ProductiveBees.MODID)
@EventBusSubscriber(modid = ProductiveBees.MODID)
public final class ProductiveBees
{
    public static final String MODID = "productivebees";
    public static final Random rand = new Random();

    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final Logger LOGGER = LogManager.getLogger();

    public static Map<String, Integer> modPreference = new HashMap<>();

    public ProductiveBees() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModPointOfInterestTypes.POINT_OF_INTEREST_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.HIVE_BEES.register(modEventBus);
        ModEntities.SOLITARY_BEES.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(EventPriority.LOWEST, ClientSetup::registerItemColors);
            modEventBus.addListener(EventPriority.LOWEST, ClientSetup::registerBlockColors);
        });

        modEventBus.addListener(ClientSetup::init);
        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::loadComplete);

        // Config loading
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ProductiveBeesConfig.CONFIG);
        ProductiveBeesConfig.loadConfig(ProductiveBeesConfig.CONFIG, FMLPaths.CONFIGDIR.get().resolve("productivebees-server.toml").toString());

        int priority = 0;
        for(String modId: ProductiveBeesConfig.GENERAL.preferredTagSource.get()) {
            modPreference.put(modId, ++priority);
        }
    }

    public void preInit(FMLCommonSetupEvent event) {
        CapabilityBee.register();

        DeferredWorkQueue.runLater(() -> {
            //Entity attribute assignments
            for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
                EntityType<ProductiveBeeEntity> bee = (EntityType<ProductiveBeeEntity>) registryObject.get();
                GlobalEntityTypeAttributes.put(bee, ProductiveBeeEntity.getDefaultAttributes().create());
            }
            for (RegistryObject<EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
                EntityType<ProductiveBeeEntity> bee = (EntityType<ProductiveBeeEntity>) registryObject.get();
                GlobalEntityTypeAttributes.put(bee, ProductiveBeeEntity.getDefaultAttributes().create());
            }
            GlobalEntityTypeAttributes.put(ModEntities.ZOMBIE_BEE.get(), ZombieBeeEntity.getDefaultAttributes().create());
            GlobalEntityTypeAttributes.put(ModEntities.BLUE_BANDED_BEE.get(), BlueBandedBeeEntity.getDefaultAttributes().create());
        });

        this.fixPOI(event);
    }

    public void loadComplete(FMLLoadCompleteEvent event) {
        // Add biome features
        for (Biome biome : ForgeRegistries.BIOMES) {

            Biome.Category category = biome.getCategory();
            if (category.equals(Biome.Category.DESERT)) {
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SAND.getDefaultState(), ModBlocks.SAND_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.SAVANNA) || category.equals(Biome.Category.TAIGA)) {
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.COARSE_DIRT_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.COARSE_DIRT.getDefaultState(), ModBlocks.COARSE_DIRT_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.SPRUCE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.SPRUCE_LOG.getDefaultState(), ModBlocks.SPRUCE_WOOD_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.ACACIA_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.ACACIA_LOG.getDefaultState(), ModBlocks.ACACIA_WOOD_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.JUNGLE)) {
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.JUNGLE_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.JUNGLE_LOG.getDefaultState(), ModBlocks.JUNGLE_WOOD_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.FOREST)) {
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.OAK_LOG.getDefaultState(), ModBlocks.OAK_WOOD_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.DARK_OAK_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.DARK_OAK_LOG.getDefaultState(), ModBlocks.DARK_OAK_WOOD_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.BIRCH_WOOD_NEST_FEATURE.get().withConfiguration(new ReplaceBlockConfig(Blocks.BIRCH_LOG.getDefaultState(), ModBlocks.BIRCH_WOOD_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.EXTREME_HILLS)) {
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.STONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), ModBlocks.STONE_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SNOW_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SNOW_BLOCK.getDefaultState(), ModBlocks.SNOW_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.SWAMP)) {
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SLIMY_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.SLIMY_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.NETHER)) {
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModFeatures.GLOWSTONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GLOWSTONE.getDefaultState(), ModBlocks.GLOWSTONE_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ModFeatures.NETHER_QUARTZ_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_QUARTZ_ORE.getDefaultState(), ModBlocks.NETHER_QUARTZ_NEST.get().getDefaultState())));
                biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.NETHER_FORTRESS_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_BRICKS.getDefaultState(), ModBlocks.NETHER_BRICK_NEST.get().getDefaultState())));
            }
            else if (category.equals(Biome.Category.RIVER) || category.equals(Biome.Category.BEACH)) {
                if (biome.getTempCategory() != Biome.TempCategory.COLD) {
                    biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.GRAVEL_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRAVEL.getDefaultState(), ModBlocks.GRAVEL_NEST.get().getDefaultState())));
                }
            }
            else if (category.equals(Biome.Category.THEEND)) {
                if (biome == Biomes.THE_END) {
                    // Pillar nests
                    biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.OBSIDIAN_PILLAR_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.OBSIDIAN.getDefaultState(), ModBlocks.OBSIDIAN_PILLAR_NEST.get().getDefaultState())));
                }
                else {
                    // Must spawn where chorus fruit exist
                    biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.END_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.END_STONE.getDefaultState(), ModBlocks.END_NEST.get().getDefaultState())));
                }
            }
            if (!category.equals(Biome.Category.THEEND) && !category.equals(Biome.Category.NETHER)) {
                biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModFeatures.SUGAR_CANE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SUGAR_CANE.getDefaultState(), ModBlocks.SUGAR_CANE_NEST.get().getDefaultState())));
            }
        }
    }

    private void fixPOI(final FMLCommonSetupEvent event) {
        for (RegistryObject<PointOfInterestType> poi : ModPointOfInterestTypes.POINT_OF_INTEREST_TYPES.getEntries()) {
            ModPointOfInterestTypes.fixPOITypeBlockStates(poi.get());
        }

        PointOfInterestType.BEEHIVE.blockStates = this.makePOIStatesMutable(PointOfInterestType.BEEHIVE.blockStates);
        ImmutableList<Block> BEEHIVES = ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof AdvancedBeehive).collect(ImmutableList.toImmutableList());
        for (Block block: BEEHIVES) {
            for (BlockState state: block.getStateContainer().getValidStates()) {
                PointOfInterestType.POIT_BY_BLOCKSTATE.put(state, PointOfInterestType.BEEHIVE);
                try {
                    PointOfInterestType.BEEHIVE.blockStates.add(state);
                } catch (Exception e) {
                    LOGGER.warn("Could not add blockstate to beehive POI " + state);
                }
            };
        };
    }

    private Set<BlockState> makePOIStatesMutable(Set<BlockState> toCopy) {
        Set<BlockState> copy = Sets.newHashSet();
        copy.addAll(toCopy);
        return copy;
    }
}
