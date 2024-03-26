package cy.jdkdigital.productivebees.datagen;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivelib.loot.condition.OptionalCopyBlockState;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BlockLootProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<LootTableProvider.SubProviderEntry> subProviders;

    private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
            .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    public BlockLootProvider(PackOutput packOutput, List<LootTableProvider.SubProviderEntry> providers) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        this.subProviders = providers;
    }

    @Override
    public String getName() {
        return "Productive Bees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        final Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.subProviders.forEach((providerEntry) -> {
            providerEntry.provider().get().generate((resourceLocation, builder) -> {
                builder.setRandomSequence(resourceLocation);
                if (map.put(resourceLocation, builder.setParamSet(providerEntry.paramSet()).build()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + resourceLocation);
                }
            });
        });

        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            return DataProvider.saveStable(cache, LootDataType.TABLE.parser().toJsonTree(entry.getValue()), this.pathProvider.json(entry.getKey()));
        }).toArray(CompletableFuture[]::new));
    }

    public static LootTable.Builder genHiveDrop(Block hive) {
        LootPoolEntryContainer.Builder<?> hiveNoHoney = OptionalLootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion()).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("BeeList", "BlockEntityTag.BeeList"));
        LootPoolEntryContainer.Builder<?> hiveHoney = OptionalLootItem.lootTableItem(hive).when(SILK_TOUCH).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("BeeList", "BlockEntityTag.BeeList")).apply(OptionalCopyBlockState.copyState(hive).copy(BeehiveBlock.HONEY_LEVEL));

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(hiveHoney.otherwise(hiveNoHoney)));
    }

    public static LootTable.Builder genExpansionDrop(Block expansion) {
        LootPoolEntryContainer.Builder<?> expansionBox = OptionalLootItem.lootTableItem(expansion).when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(expansionBox));
    }

    public static LootTable.Builder genBlockDrop(Block expansion) {
        LootPoolEntryContainer.Builder<?> builder = LootItem.lootTableItem(expansion).when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(builder));
    }

    public static class LootProvider extends BlockLootSubProvider
    {
        private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();
        private List<Block> knownBlocks = new ArrayList<>();

        public LootProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

            ModBlocks.HIVELIST.forEach((modid, strings) -> {
                strings.forEach((name, type) -> {
                    name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                    Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                    Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, BlockLootProvider::genHiveDrop);
                    this.add(hive, hiveFunc.apply(hive));
                    Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, BlockLootProvider::genExpansionDrop);
                    this.add(box, expansionFunc.apply(box));
                });
            });

            ModBlocks.hiveStyles.forEach(style -> {
                Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();
                Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, BlockLootProvider::genHiveDrop);
                this.add(hive, hiveFunc.apply(hive));
                Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, BlockLootProvider::genExpansionDrop);
                this.add(box, expansionFunc.apply(box));
            });

            Function<Block, LootTable.Builder> func = functionTable.getOrDefault(ModBlocks.PETRIFIED_HONEY.get(), BlockLootProvider::genBlockDrop);
            this.add(ModBlocks.PETRIFIED_HONEY.get(), func.apply(ModBlocks.PETRIFIED_HONEY.get()));
            ModBlocks.PETRIFIED_HONEY_BLOCKS.forEach(registryObject -> {
                Function<Block, LootTable.Builder> petrifiedFunc = functionTable.getOrDefault(registryObject.get(), BlockLootProvider::genBlockDrop);
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
    }
}
