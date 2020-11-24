package cy.jdkdigital.productivebees;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.crafting.conditions.FluidTagEmptyCondition;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.hive.ZombieBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BlueBandedBeeEntity;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.integrations.top.TopPlugin;
import cy.jdkdigital.productivebees.network.PacketHandler;
import cy.jdkdigital.productivebees.setup.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Mod(ProductiveBees.MODID)
@EventBusSubscriber(modid = ProductiveBees.MODID)
public final class ProductiveBees
{
    public static final String MODID = "productivebees";
    public static final Random rand = new Random();

    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static final Logger LOGGER = LogManager.getLogger();


    public ProductiveBees() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModPointOfInterestTypes.POINT_OF_INTEREST_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.HIVE_BEES.register(modEventBus);
        ModEntities.SOLITARY_BEES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            modEventBus.addListener(ClientSetup::init);
            modEventBus.addListener(EventPriority.LOWEST, ClientSetup::registerItemColors);
            modEventBus.addListener(EventPriority.LOWEST, ClientSetup::registerBlockColors);
            modEventBus.addListener(EventPriority.LOWEST, ClientSetup::registerParticles);
        });

        modEventBus.addListener(this::onInterModEnqueue);
        modEventBus.addGenericListener(Feature.class, this::onRegisterFeatures);
        modEventBus.addListener(this::onCommonSetup);

        // Config loading
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ProductiveBeesConfig.CONFIG);

        CraftingHelper.register(FluidTagEmptyCondition.Serializer.INSTANCE);
    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe")) {
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopPlugin::new);
        }
    }

    public void onServerStarting(AddReloadListenerEvent event) {
        BeeReloadListener.recipeManager = event.getDataPackRegistries().getRecipeManager();
        event.addListener(BeeReloadListener.INSTANCE);
    }

    public void onRegisterFeatures(final RegistryEvent.Register<Feature<?>> event)
    {
        ModFeatures.registerFeatures(event);
        ModConfiguredFeatures.registerConfiguredFeatures();
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityBee.register();
        PacketHandler.init();

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

    private void onBiomeLoad(BiomeLoadingEvent event) {
        Biome.Category category = event.getCategory();
        // Add biome features
        if (category.equals(Biome.Category.DESERT)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.SAVANNA) || category.equals(Biome.Category.TAIGA)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.COARSE_DIRT_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SPRUCE_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.ACACIA_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.JUNGLE)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.JUNGLE_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.FOREST)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.OAK_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.DARK_OAK_WOOD_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.BIRCH_WOOD_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.EXTREME_HILLS)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.STONE_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SNOW_NEST_BLOCK_FEATURE);
        }
        else if (category.equals(Biome.Category.SWAMP)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.SLIMY_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.NETHER)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.GLOWSTONE_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ModConfiguredFeatures.NETHER_QUARTZ_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_QUARTZ_NEST_HIGH_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.NETHER_FORTRESS_NEST_FEATURE);
            event.getGeneration().withFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SOUL_SAND_NEST_FEATURE);
        }
        else if (category.equals(Biome.Category.RIVER) || category.equals(Biome.Category.BEACH)) {
            if (event.getClimate().temperatureModifier != Biome.TemperatureModifier.FROZEN) {
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.GRAVEL_NEST_FEATURE);
            }
        }
        else if (category.equals(Biome.Category.THEEND)) {
            if (event.getName().getPath().equals("the_end")) {
                // Pillar nests
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.OBSIDIAN_PILLAR_NEST_FEATURE);
            } else {
                // Must spawn where chorus fruit exist
                event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModConfiguredFeatures.END_NEST_FEATURE);
            }
        }
        if (!category.equals(Biome.Category.THEEND) && !category.equals(Biome.Category.NETHER)) {
            event.getGeneration().withFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SUGAR_CANE_NEST_FEATURE);
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
