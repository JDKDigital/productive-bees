package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class ModBlockEntityTypes
{
    public static Supplier<BlockEntityType<AdvancedBeehiveBlockEntity>> ADVANCED_HIVE;
    public static Supplier<BlockEntityType<ExpansionBoxBlockEntity>> EXPANSION_BOX;
    public static void registerHiveBlockEntities() {
        ADVANCED_HIVE = registerBlockEntity("advanced_hive", () -> createBlockEntityType(AdvancedBeehiveBlockEntity::new, ModBlocks.HIVES.values().stream().map(DeferredHolder::get).toList().toArray(new Block[0])));
        EXPANSION_BOX = registerBlockEntity("expansion_box", () -> createBlockEntityType(ExpansionBoxBlockEntity::new, ModBlocks.EXPANSIONS.values().stream().map(DeferredHolder::get).toList().toArray(new Block[0])));
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolitaryNestBlockEntity>> SOLITARY_NEST = ProductiveBees.BLOCK_ENTITIES.register("solitary_nest", () ->
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
                    ModBlocks.SPRUCE_WOOD_NEST.get(),
                    ModBlocks.CHERRY_WOOD_NEST.get(),
                    ModBlocks.MANGROVE_WOOD_NEST.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NetherBeeNestBlockEntity>> NETHER_BEE_NEST = ProductiveBees.BLOCK_ENTITIES.register("nether_bee_nest", () ->
            BlockEntityType.Builder.of(NetherBeeNestBlockEntity::new,
                    ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.WARPED_BEE_NEST.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AmberBlockEntity>> AMBER = ProductiveBees.BLOCK_ENTITIES.register("amber", () ->
            BlockEntityType.Builder.of(AmberBlockEntity::new,
                    ModBlocks.AMBER.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SugarbagNestBlockEntity>> SUGARBAG_NEST = registerBlockEntity("sugarbag_nest", SugarbagNestBlockEntity::new, ModBlocks.SUGARBAG_NEST);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AdvancedBeehiveBlockEntity>> DRACONIC_BEEHIVE = registerBlockEntity("draconic_beehive", DragonEggHiveBlockEntity::new, ModBlocks.DRAGON_EGG_HIVE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolitaryHiveBlockEntity>> SOLITARY_HIVE = registerBlockEntity("solitary_hive", SolitaryHiveBlockEntity::new, ModBlocks.BAMBOO_HIVE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BumbleBeeNestBlockEntity>> BUMBLE_BEE_NEST = registerBlockEntity("bumble_bee_nest", BumbleBeeNestBlockEntity::new, ModBlocks.BUMBLE_BEE_NEST);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CentrifugeBlockEntity>> CENTRIFUGE = registerBlockEntity("centrifuge", CentrifugeBlockEntity::new, ModBlocks.CENTRIFUGE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PoweredCentrifugeBlockEntity>> POWERED_CENTRIFUGE = registerBlockEntity("powered_centrifuge", PoweredCentrifugeBlockEntity::new, ModBlocks.POWERED_CENTRIFUGE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HeatedCentrifugeBlockEntity>> HEATED_CENTRIFUGE = registerBlockEntity("heated_centrifuge", HeatedCentrifugeBlockEntity::new, ModBlocks.HEATED_CENTRIFUGE);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BottlerBlockEntity>> BOTTLER = registerBlockEntity("bottler", BottlerBlockEntity::new, ModBlocks.BOTTLER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FeederBlockEntity>> FEEDER = registerBlockEntity("feeder", FeederBlockEntity::new, ModBlocks.FEEDER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<JarBlockEntity>> JAR = registerBlockEntity("jar", JarBlockEntity::new, ModBlocks.JAR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombBlockBlockEntity>> COMB_BLOCK = registerBlockEntity("comb_block", CombBlockBlockEntity::new, ModBlocks.CONFIGURABLE_COMB);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HoneyGeneratorBlockEntity>> HONEY_GENERATOR = registerBlockEntity("honey_generator", HoneyGeneratorBlockEntity::new, ModBlocks.HONEY_GENERATOR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CatcherBlockEntity>> CATCHER = registerBlockEntity("catcher", CatcherBlockEntity::new, ModBlocks.CATCHER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncubatorBlockEntity>> INCUBATOR = registerBlockEntity("incubator", IncubatorBlockEntity::new, ModBlocks.INCUBATOR);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneIndexerBlockEntity>> GENE_INDEXER = registerBlockEntity("gene_indexer", GeneIndexerBlockEntity::new, ModBlocks.GENE_INDEXER);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CryoStasisBlockEntity>> CRYO_STASIS = registerBlockEntity("cryo_stasis", CryoStasisBlockEntity::new, ModBlocks.CRYO_STASIS);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BreedingChamberBlockEntity>> BREEDING_CHAMBER = registerBlockEntity("breeding_chamber", BreedingChamberBlockEntity::new, ModBlocks.BREEDING_CHAMBER);

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> factory, DeferredHolder<Block, ? extends Block> block) {
        return ProductiveBees.BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    public static <E extends BlockEntity, T extends BlockEntityType<E>> Supplier<T> registerBlockEntity(String id, Supplier<T> supplier) {
        return ProductiveBees.BLOCK_ENTITIES.register(id, supplier);
    }

    public static <E extends BlockEntity> BlockEntityType<E> createBlockEntityType(BlockEntityType.BlockEntitySupplier<E> factory, Block... blocks) {
        return BlockEntityType.Builder.of(factory, blocks).build(null);
    }
}
