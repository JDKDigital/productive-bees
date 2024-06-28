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
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
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

        protected LootItemCondition.Builder hasSilkTouch() {
            HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return MatchTool.toolMatches(
                    ItemPredicate.Builder.item()
                            .withSubPredicate(
                                    ItemSubPredicates.ENCHANTMENTS,
                                    ItemEnchantmentsPredicate.enchantments(
                                            List.of(new EnchantmentPredicate(registrylookup.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))
                                    )
                            )
            );
        }

        @Override
        protected void generate() {
            Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

            ModBlocks.HIVELIST.forEach((modid, strings) -> {
                if (ProductiveBees.includeMod(modid)) {
                    strings.forEach((name, type) -> {
                        name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                        Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                        Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, this::genHiveDrop);
                        this.add(hive, hiveFunc.apply(hive));
                        Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, this::genExpansionDrop);
                        this.add(box, expansionFunc.apply(box));
                    });
                }
            });

            ModBlocks.hiveStyles.forEach(style -> {
                Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();
                Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, this::genHiveDrop);
                this.add(hive, hiveFunc.apply(hive));
                Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, this::genExpansionDrop);
                this.add(box, expansionFunc.apply(box));
            });

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


        public LootTable.Builder genHiveDrop(Block hive) {
            LootPoolEntryContainer.Builder<?> hiveNoHoney = OptionalLootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion())
                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES));
            LootPoolEntryContainer.Builder<?> hiveHoney = OptionalLootItem.lootTableItem(hive).when(this.hasSilkTouch())
                    .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES))
                    .apply(CopyBlockState.copyState(hive).copy(BeehiveBlock.HONEY_LEVEL));

            return LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(hiveHoney.otherwise(hiveNoHoney)));
        }

        public LootTable.Builder genExpansionDrop(Block expansion) {
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
