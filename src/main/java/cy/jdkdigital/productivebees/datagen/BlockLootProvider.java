package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.loot.OptionalLootItem;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BlockLootProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();
    private static final LootItemCondition.Builder SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item()
            .hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));

    public BlockLootProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables/blocks");
    }

    @Override
    public String getName() {
        return "Productive Bees Block Loot Table datagen";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                Function<Block, LootTable.Builder> hiveFunc = functionTable.getOrDefault(hive, BlockLootProvider::genHiveDrop);
                tables.put(BuiltInRegistries.BLOCK.getKey(hive), hiveFunc.apply(hive));
                Function<Block, LootTable.Builder> expansionFunc = functionTable.getOrDefault(box, BlockLootProvider::genExpansionDrop);
                tables.put(BuiltInRegistries.BLOCK.getKey(box), expansionFunc.apply(box));
            });
        });

        List<CompletableFuture<?>> output = new ArrayList<>();
        for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
            Path path = pathProvider.json(e.getKey());
            output.add(DataProvider.saveStable(cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.BLOCK).build()), path));
        }
        return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
    }

    protected static LootTable.Builder genHiveDrop(Block hive) {
        LootPoolEntryContainer.Builder<?> hiveNoHoney = OptionalLootItem.lootTableItem(hive).when(ExplosionCondition.survivesExplosion()).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("BeeList", "BlockEntityTag.BeeList"));
        LootPoolEntryContainer.Builder<?> hiveHoney = OptionalLootItem.lootTableItem(hive).when(SILK_TOUCH).apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("BeeList", "BlockEntityTag.BeeList")).apply(CopyBlockState.copyState(hive).copy(BeehiveBlock.HONEY_LEVEL));

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(hiveHoney.otherwise(hiveNoHoney)));
    }

    protected static LootTable.Builder genExpansionDrop(Block expansion) {
        LootPoolEntryContainer.Builder<?> expansionBox = OptionalLootItem.lootTableItem(expansion).when(ExplosionCondition.survivesExplosion());

        return LootTable.lootTable().withPool(
                LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(expansionBox));
    }
}