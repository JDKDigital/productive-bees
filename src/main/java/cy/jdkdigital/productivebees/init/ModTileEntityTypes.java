package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.*;
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

    public static final RegistryObject<TileEntityType<SugarbagNestTileEntity>> SUGARBAG_NEST = TILE_ENTITY_TYPES.register("sugarbag_nest", () ->
            TileEntityType.Builder.create(SugarbagNestTileEntity::new,
                    ModBlocks.SUGARBAG_NEST.get()
            ).build(null)
    );

    public static final RegistryObject<TileEntityType<DragonEggHiveTileEntity>> DRACONIC_BEEHIVE = TILE_ENTITY_TYPES.register("draconic_beehive", () ->
            TileEntityType.Builder.create(DragonEggHiveTileEntity::new,
                    ModBlocks.DRAGON_EGG_HIVE.get()
            ).build(null)
    );

    public static final RegistryObject<TileEntityType<SolitaryHiveTileEntity>> SOLITARY_HIVE = TILE_ENTITY_TYPES.register("solitary_hive", () ->
            TileEntityType.Builder.create(SolitaryHiveTileEntity::new,
                    ModBlocks.BAMBOO_HIVE.get()
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
                    ModBlocks.SOUL_SAND_NEST.get(),
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

    public static final RegistryObject<TileEntityType<ExpansionBoxTileEntity>> EXPANSION_BOX = TILE_ENTITY_TYPES.register("expansion_box", () ->
            TileEntityType.Builder.create(ExpansionBoxTileEntity::new,
                    ModBlocks.EXPANSION_BOX_SPRUCE.get(),
                    ModBlocks.EXPANSION_BOX_BIRCH.get(),
                    ModBlocks.EXPANSION_BOX_JUNGLE.get(),
                    ModBlocks.EXPANSION_BOX_ACACIA.get(),
                    ModBlocks.EXPANSION_BOX_DARK_OAK.get(),
                    ModBlocks.EXPANSION_BOX_CRIMSON.get(),
                    ModBlocks.EXPANSION_BOX_WARPED.get(),
                    ModBlocks.EXPANSION_BOX_SNAKE_BLOCK.get(),
                    ModBlocks.EXPANSION_BOX_ROSEWOOD.get(),
                    ModBlocks.EXPANSION_BOX_YUCCA.get(),
                    ModBlocks.EXPANSION_BOX_KOUSA.get(),
                    ModBlocks.EXPANSION_BOX_ASPEN.get(),
                    ModBlocks.EXPANSION_BOX_GRIMWOOD.get(),
                    ModBlocks.EXPANSION_BOX_WILLOW.get(),
                    ModBlocks.EXPANSION_BOX_WISTERIA.get(),
                    ModBlocks.EXPANSION_BOX_BAMBOO.get(),
                    ModBlocks.EXPANSION_BOX_MAPLE.get(),
                    ModBlocks.EXPANSION_BOX_DRIFTWOOD.get(),
                    ModBlocks.EXPANSION_BOX_RIVER.get(),
                    ModBlocks.EXPANSION_BOX_POISE.get(),
                    ModBlocks.EXPANSION_BOX_BOP_FIR.get(),
                    ModBlocks.EXPANSION_BOX_BOP_DEAD.get(),
                    ModBlocks.EXPANSION_BOX_BOP_PALM.get(),
                    ModBlocks.EXPANSION_BOX_BOP_MAGIC.get(),
                    ModBlocks.EXPANSION_BOX_BOP_CHERRY.get(),
                    ModBlocks.EXPANSION_BOX_BOP_UMBRAN.get(),
                    ModBlocks.EXPANSION_BOX_BOP_WILLOW.get(),
                    ModBlocks.EXPANSION_BOX_BOP_REDWOOD.get(),
                    ModBlocks.EXPANSION_BOX_BOP_HELLBARK.get(),
                    ModBlocks.EXPANSION_BOX_BOP_MAHOGANY.get(),
                    ModBlocks.EXPANSION_BOX_BOP_JACARANDA.get()
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

    public static final RegistryObject<TileEntityType<FeederTileEntity>> FEEDER = TILE_ENTITY_TYPES.register("feeder", () ->
            TileEntityType.Builder.create(FeederTileEntity::new,
                    ModBlocks.FEEDER.get()
            ).build(null)
    );

    public static final RegistryObject<TileEntityType<JarTileEntity>> JAR = TILE_ENTITY_TYPES.register("jar", () ->
            TileEntityType.Builder.create(JarTileEntity::new,
                    ModBlocks.JAR.get()
            ).build(null)
    );

    public static final RegistryObject<TileEntityType<CombBlockTileEntity>> COMB_BLOCK = TILE_ENTITY_TYPES.register("comb_block", () ->
            TileEntityType.Builder.create(CombBlockTileEntity::new,
                    ModBlocks.CONFIGURABLE_COMB.get()
            ).build(null)
    );
}
