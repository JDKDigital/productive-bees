package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, ProductiveBees.MODID);

    public static final RegistryObject<TileEntityType<AdvancedBeehiveTileEntity>> ADVANCED_BEEHIVE = TILE_ENTITY_TYPES.register("advanced_beehive", () ->
            TileEntityType.Builder.create(AdvancedBeehiveTileEntity::new,
                    ModBlocks.ADVANCED_OAK_BEEHIVE.get(),
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
                    ModBlocks.ADVANCED_GRIMWOOD_BEEHIVE.get(),
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
            ).build(null)
    );
    public static final RegistryObject<TileEntityType<DragonEggHiveTileEntity>> DRACONIC_BEEHIVE = TILE_ENTITY_TYPES.register("draconic_beehive", () ->
            TileEntityType.Builder.create(DragonEggHiveTileEntity::new,
                    ModBlocks.DRAGON_EGG_HIVE.get()
            ).build(null)
    );
    public static final RegistryObject<TileEntityType<SolitaryNestTileEntity>> SOLITARY_NEST = TILE_ENTITY_TYPES.register("solitary_nest", () ->
            TileEntityType.Builder.create(SolitaryNestTileEntity::new,
                    ModBlocks.SAND_NEST.get(),
                    ModBlocks.SNOW_NEST.get(),
                    ModBlocks.STONE_NEST.get(),
                    ModBlocks.COARSE_DIRT_NEST.get(),
                    ModBlocks.GRAVEL_NEST.get(),
                    ModBlocks.SUGAR_CANE_NEST.get(),
                    ModBlocks.SLIMY_NEST.get(),
                    ModBlocks.NETHER_QUARTZ_NEST.get(),
                    ModBlocks.NETHER_BRICK_NEST.get(),
                    ModBlocks.GLOWSTONE_NEST.get(),
                    ModBlocks.END_NEST.get(),
                    ModBlocks.OBSIDIAN_PILLAR_NEST.get(),
                    ModBlocks.OAK_WOOD_NEST.get(),
                    ModBlocks.JUNGLE_WOOD_NEST.get(),
                    ModBlocks.BIRCH_WOOD_NEST.get(),
                    ModBlocks.DARK_OAK_WOOD_NEST.get(),
                    ModBlocks.ACACIA_WOOD_NEST.get(),
                    ModBlocks.SPRUCE_WOOD_NEST.get()
            ).build(null)
    );
    public static final RegistryObject<TileEntityType<CentrifugeTileEntity>> CENTRIFUGE = TILE_ENTITY_TYPES.register("centrifuge", () ->
            TileEntityType.Builder.create(CentrifugeTileEntity::new,
                    ModBlocks.CENTRIFUGE.get()
            ).build(null)
    );
    public static final RegistryObject<TileEntityType<PoweredCentrifugeTileEntity>> POWERED_CENTRIFUGE = TILE_ENTITY_TYPES.register("powered_centrifuge", () ->
            TileEntityType.Builder.create(PoweredCentrifugeTileEntity::new,
                    ModBlocks.POWERED_CENTRIFUGE.get()
            ).build(null)
    );
    public static final RegistryObject<TileEntityType<BottlerTileEntity>> BOTTLER = TILE_ENTITY_TYPES.register("bottler", () ->
            TileEntityType.Builder.create(BottlerTileEntity::new,
                    ModBlocks.BOTTLER.get()
            ).build(null)
    );
}
