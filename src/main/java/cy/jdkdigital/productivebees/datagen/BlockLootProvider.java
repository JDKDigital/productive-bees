package cy.jdkdigital.productivebees.datagen;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BlockLootProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public BlockLootProvider(PackOutput packOutput, List<LootTableProvider.SubProviderEntry> providers, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_table");
        this.subProviders = providers;
        this.registries = registries;
    }

    @Override
    public String getName() {
        return "Productive Bees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return this.registries.thenCompose(provider -> this.run(pOutput, provider));
    }

    private CompletableFuture<?> run(CachedOutput pOutput, HolderLookup.Provider pProvider) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().apply(pProvider).generate((resourceKey, builder) -> {
                builder.setRandomSequence(resourceKey.location());
                if (map.put(resourceKey.location(), builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceKey.location());
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(pOutput, pProvider, LootTable.DIRECT_CODEC, entry.getValue(), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();
        private List<Block> knownBlocks = new ArrayList<>();

        public LootProvider(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        static LootItemCondition.Builder HAS_SILK;

        @Override
        protected void generate() {
            Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

            HAS_SILK = this.hasSilkTouch();

            ModBlocks.HIVELIST.forEach((modid, strings) -> {
                if (ProductiveBees.includeMod(modid)) {
                    strings.forEach((name, type) -> {
                        name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                        Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, LootProvider::genHiveDrop);
                        this.add(hive, hiveFunc.apply(hive));
                        Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, LootProvider::genExpansionDrop);
                        this.add(box, expansionFunc.apply(box));
                    });
                }
            });

            ModBlocks.hiveStyles.forEach(style -> {
                Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();
                Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, LootProvider::genHiveDrop);
                this.add(hive, hiveFunc.apply(hive));
                Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, LootProvider::genExpansionDrop);
                this.add(box, expansionFunc.apply(box));
            });

            this.add(ModBlocks.DRAGON_EGG_HIVE.get(), functionTable.getOrDefault(ModBlocks.DRAGON_EGG_HIVE.get(), LootProvider::genHiveDrop).apply(ModBlocks.DRAGON_EGG_HIVE.get()));
            this.add(ModBlocks.OAK_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.OAK_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.OAK_WOOD_NEST.get()));
            this.add(ModBlocks.SPRUCE_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.SPRUCE_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SPRUCE_WOOD_NEST.get()));
            this.add(ModBlocks.DARK_OAK_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.DARK_OAK_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.DARK_OAK_WOOD_NEST.get()));
            this.add(ModBlocks.BIRCH_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.BIRCH_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.BIRCH_WOOD_NEST.get()));
            this.add(ModBlocks.JUNGLE_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.JUNGLE_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.JUNGLE_WOOD_NEST.get()));
            this.add(ModBlocks.ACACIA_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.ACACIA_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.ACACIA_WOOD_NEST.get()));
            this.add(ModBlocks.CHERRY_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.CHERRY_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.CHERRY_WOOD_NEST.get()));
            this.add(ModBlocks.MANGROVE_WOOD_NEST.get(), functionTable.getOrDefault(ModBlocks.MANGROVE_WOOD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.MANGROVE_WOOD_NEST.get()));
            this.add(ModBlocks.BAMBOO_HIVE.get(), functionTable.getOrDefault(ModBlocks.BAMBOO_HIVE.get(), LootProvider::genHiveDrop).apply(ModBlocks.BAMBOO_HIVE.get()));
            this.add(ModBlocks.DRAGON_EGG_HIVE.get(), functionTable.getOrDefault(ModBlocks.DRAGON_EGG_HIVE.get(), LootProvider::genHiveDrop).apply(ModBlocks.DRAGON_EGG_HIVE.get()));
            this.add(ModBlocks.STONE_NEST.get(), functionTable.getOrDefault(ModBlocks.STONE_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.STONE_NEST.get()));
            this.add(ModBlocks.COARSE_DIRT_NEST.get(), functionTable.getOrDefault(ModBlocks.COARSE_DIRT_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.COARSE_DIRT_NEST.get()));
            this.add(ModBlocks.SAND_NEST.get(), functionTable.getOrDefault(ModBlocks.SAND_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SAND_NEST.get()));
            this.add(ModBlocks.SNOW_NEST.get(), functionTable.getOrDefault(ModBlocks.SNOW_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SNOW_NEST.get()));
            this.add(ModBlocks.GRAVEL_NEST.get(), functionTable.getOrDefault(ModBlocks.GRAVEL_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.GRAVEL_NEST.get()));
            this.add(ModBlocks.SUGAR_CANE_NEST.get(), functionTable.getOrDefault(ModBlocks.SUGAR_CANE_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SUGAR_CANE_NEST.get()));
            this.add(ModBlocks.SLIMY_NEST.get(), functionTable.getOrDefault(ModBlocks.SLIMY_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SLIMY_NEST.get()));
            this.add(ModBlocks.GLOWSTONE_NEST.get(), functionTable.getOrDefault(ModBlocks.GLOWSTONE_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.GLOWSTONE_NEST.get()));
            this.add(ModBlocks.SOUL_SAND_NEST.get(), functionTable.getOrDefault(ModBlocks.SOUL_SAND_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.SOUL_SAND_NEST.get()));
            this.add(ModBlocks.NETHER_QUARTZ_NEST.get(), functionTable.getOrDefault(ModBlocks.NETHER_QUARTZ_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.NETHER_QUARTZ_NEST.get()));
            this.add(ModBlocks.NETHER_GOLD_NEST.get(), functionTable.getOrDefault(ModBlocks.NETHER_GOLD_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.NETHER_GOLD_NEST.get()));
            this.add(ModBlocks.NETHER_BRICK_NEST.get(), functionTable.getOrDefault(ModBlocks.NETHER_BRICK_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.NETHER_BRICK_NEST.get()));
            this.add(ModBlocks.END_NEST.get(), functionTable.getOrDefault(ModBlocks.END_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.END_NEST.get()));
            this.add(ModBlocks.OBSIDIAN_PILLAR_NEST.get(), functionTable.getOrDefault(ModBlocks.OBSIDIAN_PILLAR_NEST.get(), LootProvider::genHiveDrop).apply(ModBlocks.OBSIDIAN_PILLAR_NEST.get()));

            this.add(ModBlocks.AMBER.get(), functionTable.getOrDefault(ModBlocks.AMBER.get(), LootProvider::amberDrop).apply(ModBlocks.AMBER.get()));

            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(ModBlocks.PETRIFIED_HONEY.get(), this::genBlockDrop);
            this.add(ModBlocks.PETRIFIED_HONEY.get(), func.apply(ModBlocks.PETRIFIED_HONEY.get()));
            ModBlocks.PETRIFIED_HONEY_BLOCKS.forEach(registryObject -> {
                Function<Block, LootTable.Builder> petrifiedFunc = functionTable.getOrDefault(registryObject.get(), this::genBlockDrop);
                this.add(registryObject.get(), petrifiedFunc.apply(registryObject.get()));
            });
        }

        @Override
        protected void add(Block block, LootTable.Builder builder) {
            super.add(block, builder);
            knownBlocks.add(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return knownBlocks;
        }

        public static LootTable.Builder genHiveDrop(Block hive) {
            LootPoolEntryContainer.Builder<?> hiveNoHoney = OptionalLootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion())
                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES));

            LootPoolEntryContainer.Builder<?> hiveHoney;
            if (hive.defaultBlockState().hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                hiveHoney = OptionalLootItem.lootTableItem(hive).when(HAS_SILK)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES))
                        .apply(CopyBlockState.copyState(hive).copy(BeehiveBlock.HONEY_LEVEL));
            } else {
                hiveHoney = OptionalLootItem.lootTableItem(hive).when(HAS_SILK)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES));
            }

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(hiveHoney.otherwise(hiveNoHoney)));
        }

        public static LootTable.Builder amberDrop(Block hive) {
            LootPoolEntryContainer.Builder<?> hiveNoHoney = LootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion())
                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.ENTITY_DATA));

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(hiveNoHoney));
        }

        public static LootTable.Builder genExpansionDrop(Block expansion) {
            LootPoolEntryContainer.Builder<?> expansionBox = OptionalLootItem.lootTableItem(expansion).when(ExplosionCondition.survivesExplosion());

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(expansionBox));
        }

        public LootTable.Builder genBlockDrop(Block expansion) {
            LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(expansion).when(ExplosionCondition.survivesExplosion());

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(builder));
        }
    }
}
