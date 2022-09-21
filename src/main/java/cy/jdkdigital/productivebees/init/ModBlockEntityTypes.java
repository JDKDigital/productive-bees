package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlockEntityTypes
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ProductiveBees.MODID);

    public static final RegistryObject<BlockEntityType<AdvancedBeehiveBlockEntity>> ADVANCED_BEEHIVE = BLOCK_ENTITIES.register("advanced_beehive", () ->
            BlockEntityType.Builder.of(AdvancedBeehiveBlockEntity::new,
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

    public static final RegistryObject<BlockEntityType<SolitaryNestBlockEntity>> SOLITARY_NEST = BLOCK_ENTITIES.register("solitary_nest", () ->
            BlockEntityType.Builder.of(SolitaryNestBlockEntity::new,
                    ModBlocks.SAND_NEST.get(),
                    ModBlocks.SNOW_NEST.get(),
                    ModBlocks.STONE_NEST.get(),
                    ModBlocks.COARSE_DIRT_NEST.get(),
                    ModBlocks.GRAVEL_NEST.get(),
                    ModBlocks.SUGAR_CANE_NEST.get(),
                    ModBlocks.SLIMY_NEST.get(),
                    ModBlocks.NETHER_QUARTZ_NEST.get(),
                    ModBlocks.NETHER_BRICK_NEST.get(),
                    ModBlocks.NETHER_GOLD_NEST.get(),
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

    public static final RegistryObject<BlockEntityType<ExpansionBoxBlockEntity>> EXPANSION_BOX = BLOCK_ENTITIES.register("expansion_box", () ->
            BlockEntityType.Builder.of(ExpansionBoxBlockEntity::new,
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

    public static final RegistryObject<BlockEntityType<NetherBeeNestBlockEntity>> NETHER_BEE_NEST = BLOCK_ENTITIES.register("nether_bee_nest", () ->
            BlockEntityType.Builder.of(NetherBeeNestBlockEntity::new,
                    ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.WARPED_BEE_NEST.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<SugarbagNestBlockEntity>> SUGARBAG_NEST = register("sugarbag_nest", SugarbagNestBlockEntity::new, ModBlocks.SUGARBAG_NEST);
    public static final RegistryObject<BlockEntityType<DragonEggHiveBlockEntity>> DRACONIC_BEEHIVE = register("draconic_beehive", DragonEggHiveBlockEntity::new, ModBlocks.DRAGON_EGG_HIVE);
    public static final RegistryObject<BlockEntityType<SolitaryHiveBlockEntity>> SOLITARY_HIVE = register("solitary_hive", SolitaryHiveBlockEntity::new, ModBlocks.BAMBOO_HIVE);
    public static final RegistryObject<BlockEntityType<BumbleBeeNestBlockEntity>> BUMBLE_BEE_NEST = register("bumble_bee_nest", BumbleBeeNestBlockEntity::new, ModBlocks.BUMBLE_BEE_NEST);
    public static final RegistryObject<BlockEntityType<CentrifugeBlockEntity>> CENTRIFUGE = register("centrifuge", CentrifugeBlockEntity::new, ModBlocks.CENTRIFUGE);
    public static final RegistryObject<BlockEntityType<PoweredCentrifugeBlockEntity>> POWERED_CENTRIFUGE = register("powered_centrifuge", PoweredCentrifugeBlockEntity::new, ModBlocks.POWERED_CENTRIFUGE);
    public static final RegistryObject<BlockEntityType<HeatedCentrifugeBlockEntity>> HEATED_CENTRIFUGE = register("heated_centrifuge", HeatedCentrifugeBlockEntity::new, ModBlocks.HEATED_CENTRIFUGE);
    public static final RegistryObject<BlockEntityType<BottlerBlockEntity>> BOTTLER = register("bottler", BottlerBlockEntity::new, ModBlocks.BOTTLER);
    public static final RegistryObject<BlockEntityType<FeederBlockEntity>> FEEDER = register("feeder", FeederBlockEntity::new, ModBlocks.FEEDER);
    public static final RegistryObject<BlockEntityType<JarBlockEntity>> JAR = register("jar", JarBlockEntity::new, ModBlocks.JAR);
    public static final RegistryObject<BlockEntityType<CombBlockBlockEntity>> COMB_BLOCK = register("comb_block", CombBlockBlockEntity::new, ModBlocks.CONFIGURABLE_COMB);
    public static final RegistryObject<BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR = register("honey_generator", HoneyGeneratorBlockEntity::new, ModBlocks.HONEY_GENERATOR);
    public static final RegistryObject<BlockEntityType<CatcherBlockEntity>> CATCHER = register("catcher", CatcherBlockEntity::new, ModBlocks.CATCHER);
    public static final RegistryObject<BlockEntityType<IncubatorBlockEntity>> INCUBATOR = register("incubator", IncubatorBlockEntity::new, ModBlocks.INCUBATOR);
    public static final RegistryObject<BlockEntityType<GeneIndexerBlockEntity>> GENE_INDEXER = register("gene_indexer", GeneIndexerBlockEntity::new, ModBlocks.GENE_INDEXER);
    public static final RegistryObject<BlockEntityType<BreedingChamberBlockEntity>> BREEDING_CHAMBER = register("breeding_chamber", BreedingChamberBlockEntity::new, ModBlocks.BREEDING_CHAMBER);

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITIES.register(name, () ->
                BlockEntityType.Builder.of(factory, block.get()).build(null)
        );
    }
}
