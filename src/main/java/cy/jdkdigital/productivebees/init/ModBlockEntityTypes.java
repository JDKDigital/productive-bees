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
                    ModBlocks.SPRUCE_WOOD_NEST.get(),
                    ModBlocks.CHERRY_WOOD_NEST.get(),
                    ModBlocks.MANGROVE_WOOD_NEST.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<NetherBeeNestBlockEntity>> NETHER_BEE_NEST = BLOCK_ENTITIES.register("nether_bee_nest", () ->
            BlockEntityType.Builder.of(NetherBeeNestBlockEntity::new,
                    ModBlocks.CRIMSON_BEE_NEST.get(),
                    ModBlocks.WARPED_BEE_NEST.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<AmberBlockEntity>> AMBER = BLOCK_ENTITIES.register("amber", () ->
            BlockEntityType.Builder.of(AmberBlockEntity::new,
                    ModBlocks.AMBER.get()
            ).build(null)
    );

    public static final RegistryObject<BlockEntityType<SugarbagNestBlockEntity>> SUGARBAG_NEST = register("sugarbag_nest", SugarbagNestBlockEntity::new, ModBlocks.SUGARBAG_NEST);
    public static final RegistryObject<BlockEntityType<AdvancedBeehiveBlockEntity>> DRACONIC_BEEHIVE = register("draconic_beehive", DragonEggHiveBlockEntity::new, ModBlocks.DRAGON_EGG_HIVE);
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
    public static final RegistryObject<BlockEntityType<CryoStasisBlockEntity>> CRYO_STASIS = register("cryo_stasis", CryoStasisBlockEntity::new, ModBlocks.CRYO_STASIS);
    public static final RegistryObject<BlockEntityType<BreedingChamberBlockEntity>> BREEDING_CHAMBER = register("breeding_chamber", BreedingChamberBlockEntity::new, ModBlocks.BREEDING_CHAMBER);

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<Block> block) {
        return BLOCK_ENTITIES.register(name, () ->
                BlockEntityType.Builder.of(factory, block.get()).build(null)
        );
    }
}
