package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModTileEntityTypes
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ProductiveBees.MODID);

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

    public static final RegistryObject<TileEntityType<SugarbagNestTileEntity>> SUGARBAG_NEST = register("sugarbag_nest", SugarbagNestTileEntity::new, ModBlocks.SUGARBAG_NEST);
    public static final RegistryObject<TileEntityType<DragonEggHiveTileEntity>> DRACONIC_BEEHIVE = register("draconic_beehive", DragonEggHiveTileEntity::new, ModBlocks.DRAGON_EGG_HIVE);
    public static final RegistryObject<TileEntityType<SolitaryHiveTileEntity>> SOLITARY_HIVE = register("solitary_hive", SolitaryHiveTileEntity::new, ModBlocks.BAMBOO_HIVE);
    public static final RegistryObject<TileEntityType<CentrifugeTileEntity>> CENTRIFUGE = register("centrifuge", CentrifugeTileEntity::new, ModBlocks.CENTRIFUGE);
    public static final RegistryObject<TileEntityType<PoweredCentrifugeTileEntity>> POWERED_CENTRIFUGE = register("powered_centrifuge", PoweredCentrifugeTileEntity::new, ModBlocks.POWERED_CENTRIFUGE);
    public static final RegistryObject<TileEntityType<BottlerTileEntity>> BOTTLER = register("bottler", BottlerTileEntity::new, ModBlocks.BOTTLER);
    public static final RegistryObject<TileEntityType<FeederTileEntity>> FEEDER = register("feeder", FeederTileEntity::new, ModBlocks.FEEDER);
    public static final RegistryObject<TileEntityType<JarTileEntity>> JAR = register("jar", JarTileEntity::new, ModBlocks.JAR);
    public static final RegistryObject<TileEntityType<CombBlockTileEntity>> COMB_BLOCK = register("comb_block", CombBlockTileEntity::new, ModBlocks.CONFIGURABLE_COMB);
    public static final RegistryObject<TileEntityType<HoneyGeneratorTileEntity>> HONEY_GENERATOR = register("honey_generator", HoneyGeneratorTileEntity::new, ModBlocks.HONEY_GENERATOR);
    public static final RegistryObject<TileEntityType<CatcherTileEntity>> CATCHER = register("catcher", CatcherTileEntity::new, ModBlocks.CATCHER);
    public static final RegistryObject<TileEntityType<IncubatorTileEntity>> INCUBATOR = register("incubator", IncubatorTileEntity::new, ModBlocks.INCUBATOR);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String name, Supplier<T> factory, Supplier<Block> block) {
        return TILE_ENTITY_TYPES.register(name, () ->
            TileEntityType.Builder.create(factory, block.get()).build(null)
        );
    }
}
