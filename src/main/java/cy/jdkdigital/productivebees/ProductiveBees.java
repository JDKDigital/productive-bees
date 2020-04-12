package cy.jdkdigital.productivebees;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import cy.jdkdigital.productivebees.event.EventHandler;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.network.PacketOpenGui;
import cy.jdkdigital.productivebees.setup.ClientProxy;
import cy.jdkdigital.productivebees.setup.ClientSetup;
import cy.jdkdigital.productivebees.setup.IProxy;
import cy.jdkdigital.productivebees.setup.ServerProxy;
import cy.jdkdigital.productivebees.world.storage.loot.conditions.EntityIsProductiveBee;
import cy.jdkdigital.productivebees.world.storage.loot.functions.ProductivityBonus;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod(ProductiveBees.MODID)
@EventBusSubscriber(modid = ProductiveBees.MODID)
public final class ProductiveBees
{
	public static final String MODID = "productivebees";

	public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static final SimpleChannel NETWORK_CHANNEL;

    public static final Logger LOGGER = LogManager.getLogger();

    public ProductiveBees()
    {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    	IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModFluids.FLUIDS.register((modEventBus));
		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModEntities.HIVE_BEES.register(modEventBus);
		ModEntities.SOLITARY_BEES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
		ModFeatures.FEATURES.register(modEventBus);
		ModPointOfInterestTypes.POINT_OF_INTEREST_TYPES.register(modEventBus);

		DistExecutor.runWhenOn(Dist.CLIENT, () -> this::initSetupClient);

		modEventBus.addListener(ClientSetup::init);
		modEventBus.addListener(this::preInit);
		modEventBus.addListener(this::loadComplete);
		MinecraftForge.EVENT_BUS.register(new EventHandler());

		// Config loading
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ProductiveBeesConfig.CONFIG);
		final CommentedFileConfig configData = CommentedFileConfig.
				builder(FMLPaths.CONFIGDIR.get().resolve("productivebees.toml")).
				sync().
				autosave().
				writingMode(WritingMode.REPLACE).
				build();

		configData.load();
		ProductiveBeesConfig.CONFIG.setConfig(configData);
    }

    public void initSetupClient()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
	}
    
    private void setupClient(final FMLClientSetupEvent event)
    {
		ModEntities.registerRendering();
		ModBlocks.registerRendering();
	}

	public void preInit(FMLCommonSetupEvent event)
	{
		CapabilityBee.register();

		LootFunctionManager.registerFunction(new ProductivityBonus.Serializer());
		LootConditionManager.registerCondition(new EntityIsProductiveBee.Serializer());

		this.fixPOI(event);
	}

	public void loadComplete(FMLLoadCompleteEvent event)
	{
		for (Biome biome : ForgeRegistries.BIOMES) {
			Biome.Category cat = biome.getCategory();
			if(cat.equals(Biome.Category.DESERT)) {
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SAND_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.SAND.getDefaultState(), ModBlocks.SAND_NEST.get().getDefaultState())));
			}
			else if (cat.equals(Biome.Category.SAVANNA) || cat.equals(Biome.Category.TAIGA)) {
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.COARSE_DIRT_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.COARSE_DIRT.getDefaultState(), ModBlocks.COARSE_DIRT_NEST.get().getDefaultState())));
			}
			else if (cat.equals(Biome.Category.EXTREME_HILLS)) {
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.STONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.STONE.getDefaultState(), ModBlocks.STONE_NEST.get().getDefaultState())));
			}
			else if (cat.equals(Biome.Category.SWAMP)) {
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.SLIMY_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GRASS_BLOCK.getDefaultState(), ModBlocks.SLIMY_NEST.get().getDefaultState())));
			}
			else if (cat.equals(Biome.Category.NETHER)) {
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModFeatures.GLOWSTONE_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.GLOWSTONE.getDefaultState(), ModBlocks.GLOWSTONE_NEST.get().getDefaultState())));
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.NETHER_FORTRESS_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.NETHER_BRICKS.getDefaultState(), ModBlocks.NETHER_BRICK_NEST.get().getDefaultState())));
			}
			else if (cat.equals(Biome.Category.THEEND)) {
				biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.END_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.END_STONE.getDefaultState(), ModBlocks.END_NEST.get().getDefaultState())));
				if (biome == Biomes.THE_END) {
					// Pillar nests
					biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, ModFeatures.OBSIDIAN_PILLAR_NEST.get().withConfiguration(new ReplaceBlockConfig(Blocks.OBSIDIAN.getDefaultState(), ModBlocks.OBSIDIAN_PILLAR_NEST.get().getDefaultState())));
				}
			}
		}
	}

	private void fixPOI(final FMLCommonSetupEvent event) {
    	for(RegistryObject<PointOfInterestType> poi : ModPointOfInterestTypes.POINT_OF_INTEREST_TYPES.getEntries()) {
			ModPointOfInterestTypes.fixPOITypeBlockStates(poi.get());
		}

    	// Add all new bee poi, otherwise the bees wont give a shit about them
		final ImmutableList<Block> BEEHIVES = ImmutableList.of(
			ModBlocks.ADVANCED_OAK_BEEHIVE.get(),
			ModBlocks.BAMBOO_HIVE.get(),
			ModBlocks.STONE_NEST.get(),
			ModBlocks.SAND_NEST.get(),
			ModBlocks.COARSE_DIRT_NEST.get(),
			ModBlocks.SLIMY_NEST.get(),
			ModBlocks.GLOWSTONE_NEST.get(),
			ModBlocks.NETHER_QUARTZ_NEST.get(),
			ModBlocks.NETHER_BRICK_NEST.get(),
			ModBlocks.END_NEST.get(),
			ModBlocks.OBSIDIAN_PILLAR_NEST.get(),
			ModBlocks.DRAGON_EGG_HIVE.get(),
			ModBlocks.ADVANCED_SPRUCE_BEEHIVE.get(),
			ModBlocks.ADVANCED_BIRCH_BEEHIVE.get(),
			ModBlocks.ADVANCED_JUNGLE_BEEHIVE.get(),
			ModBlocks.ADVANCED_ACACIA_BEEHIVE.get(),
			ModBlocks.ADVANCED_DARK_OAK_BEEHIVE.get(),
			ModBlocks.ADVANCED_CRIMSON_BEEHIVE.get(),
			ModBlocks.ADVANCED_WARPED_BEEHIVE.get(),
			ModBlocks.ADVANCED_SNAKE_BLOCK_BEEHIVE.get(),
			ModBlocks.ADVANCED_ROSEWOOD_BEEHIVE.get(),
			ModBlocks.ADVANCED_YUCCA_BEEHIVE.get(),
			ModBlocks.ADVANCED_KOUSA_BEEHIVE.get(),
			ModBlocks.ADVANCED_ASPEN_BEEHIVE.get(),
			ModBlocks.ADVANCED_WILLOW_BEEHIVE.get(),
			ModBlocks.ADVANCED_WISTERIA_BEEHIVE.get(),
			ModBlocks.ADVANCED_BAMBOO_BEEHIVE.get(),
			ModBlocks.ADVANCED_MAPLE_BEEHIVE.get(),
			ModBlocks.ADVANCED_DRIFTWOOD_BEEHIVE.get(),
			ModBlocks.ADVANCED_RIVER_BEEHIVE.get(),
			ModBlocks.ADVANCED_POISE_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_FIR_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_DEAD_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_PALM_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_MAGIC_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_CHERRY_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_UMBRAN_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_WILLOW_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_REDWOOD_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_HELLBARK_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_MAHOGANY_BEEHIVE.get(),
			ModBlocks.ADVANCED_BOP_JACARANDA_BEEHIVE.get()
		);

		Set<Block> newSet = new HashSet<>(TileEntityType.BEEHIVE.validBlocks);
		newSet.addAll(BEEHIVES);
		TileEntityType.BEEHIVE.validBlocks = newSet;

		PointOfInterestType.field_226356_s_.field_221075_w = BlockTags.BEEHIVES.getAllElements().stream().flatMap((map) -> {
			return map.getStateContainer().getValidStates().stream();
		}).collect(ImmutableSet.toImmutableSet());

		Map<BlockState,PointOfInterestType> map = new HashMap<>();
		addToMap(Blocks.BEEHIVE, map);
		addToMap(ModBlocks.ADVANCED_OAK_BEEHIVE.get(), map);
		PointOfInterestType.field_221073_u.putAll(map);
	}

	public static void addToMap(Block block, Map<BlockState,PointOfInterestType> pointOfInterestTypeMap) {
		block.getStateContainer().getValidStates().forEach(state -> pointOfInterestTypeMap.put(state, PointOfInterestType.field_226356_s_));
	}

	private static int ID = 0;
	static {

		final String PROTOCOL_VERSION = "1";
		NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
				new ResourceLocation(ProductiveBees.MODID, "bee"),
				() -> PROTOCOL_VERSION,
				PROTOCOL_VERSION::equals,
				PROTOCOL_VERSION::equals
		);
		NETWORK_CHANNEL.registerMessage(ID++,
				PacketOpenGui.class,
				PacketOpenGui::toBytes,
				PacketOpenGui::new,
				PacketOpenGui::handle);
	}
}
