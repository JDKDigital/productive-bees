package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 26.1 retired {@code VillagerTradesEvent} — trades are now JSON entries in the
 * {@code Registries.VILLAGER_TRADE} datapack registry, grouped by {@link TradeSet}, and a
 * {@link net.minecraft.world.entity.npc.villager.VillagerProfession} maps tiers to TradeSets via
 * {@code tradeSetsByLevel}. This class bootstraps the beekeeper trades into both registries and
 * exposes the per-tier {@code ResourceKey<TradeSet>} so {@link ModProfessions} can wire them onto
 * the profession.
 *
 * <p>The pool-style trades from the pre-26.1 implementation (random hive, random expansion box,
 * random master-tier spawn egg, two-way honey treat) are unrolled into individual entries — the
 * trade-set's random selection picks {@code amount} of them per refresh, matching the original
 * randomised feel.
 */
public final class ModTrades
{
    private ModTrades() {}

    public static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_1 = tradeSetKey("beekeeper/level_1");
    public static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_2 = tradeSetKey("beekeeper/level_2");
    public static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_3 = tradeSetKey("beekeeper/level_3");
    public static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_4 = tradeSetKey("beekeeper/level_4");
    public static final ResourceKey<TradeSet> BEEKEEPER_LEVEL_5 = tradeSetKey("beekeeper/level_5");

    private static final String[] BASE_HIVE_WOODS = {
            "jungle", "acacia", "birch", "dark_oak", "mangrove", "spruce", "cherry", "oak"
    };

    private static final String[] MASTER_SPAWN_EGGS = {
            "spawn_egg_quarry_bee", "spawn_egg_rancher_bee", "spawn_egg_farmer_bee",
            "spawn_egg_cupid_bee", "spawn_egg_collector_bee", "spawn_egg_dye_bee",
            "spawn_egg_lumber_bee"
    };

    // Trade keys, pre-declared so both bootstraps share them. Populated lazily in
    // {@link #buildTradeKeys}.
    private static List<ResourceKey<VillagerTrade>> tier1Keys;
    private static List<ResourceKey<VillagerTrade>> tier2Keys;
    private static List<ResourceKey<VillagerTrade>> tier3Keys;
    private static List<ResourceKey<VillagerTrade>> tier4Keys;
    private static List<ResourceKey<VillagerTrade>> tier5Keys;

    private static synchronized void buildTradeKeys() {
        if (tier1Keys != null) return;
        tier1Keys = List.of(tradeKey("level_1/campfire_emerald"), tradeKey("level_1/emerald_glass_bottle"), tradeKey("level_1/emerald_shears"));
        tier2Keys = List.of(
                tradeKey("level_2/honey_bottle_emerald"),
                tradeKey("level_2/emerald_bee_cage"),
                tradeKey("level_2/emerald_sugarbag_honeycomb"),
                tradeKey("level_2/emerald_treat_on_a_stick"),
                tradeKey("level_2/honey_treat_emerald"),
                tradeKey("level_2/emerald_honey_treat"));
        List<ResourceKey<VillagerTrade>> t3 = new ArrayList<>();
        for (String wood : BASE_HIVE_WOODS) {
            t3.add(tradeKey("level_3/advanced_" + wood + "_beehive"));
            t3.add(tradeKey("level_3/expansion_box_" + wood));
        }
        tier3Keys = List.copyOf(t3);
        tier4Keys = List.of(tradeKey("level_4/sturdy_bee_cage"), tradeKey("level_4/emerald_jar"));
        List<ResourceKey<VillagerTrade>> t5 = new ArrayList<>();
        t5.add(tradeKey("level_5/emerald_bee_nest"));
        for (String eggName : MASTER_SPAWN_EGGS) {
            t5.add(tradeKey("level_5/" + eggName));
        }
        tier5Keys = List.copyOf(t5);
    }

    public static void bootstrapTrades(BootstrapContext<VillagerTrade> context) {
        buildTradeKeys();

        // Tier 1 — Novice
        context.register(tier1Keys.get(0), new VillagerTrade(
                new TradeCost(Items.CAMPFIRE, 1), template(Items.EMERALD, 1), 12, 1, 0.2F, Optional.empty(), List.of()));
        context.register(tier1Keys.get(1), new VillagerTrade(
                new TradeCost(Items.EMERALD, 1), template(Items.GLASS_BOTTLE, 4), 32, 1, 0.2F, Optional.empty(), List.of()));
        context.register(tier1Keys.get(2), new VillagerTrade(
                new TradeCost(Items.EMERALD, 3), template(Items.SHEARS, 1), 12, 3, 0.2F, Optional.empty(), List.of()));

        // Tier 2 — Apprentice
        context.register(tier2Keys.get(0), new VillagerTrade(
                new TradeCost(Items.HONEY_BOTTLE, 2), template(Items.EMERALD, 1), 10, 3, 0.2F, Optional.empty(), List.of()));
        context.register(tier2Keys.get(1), new VillagerTrade(
                new TradeCost(Items.EMERALD, 1), template(ModItems.BEE_CAGE.get(), 4), 64, 3, 0.2F, Optional.empty(), List.of()));
        context.register(tier2Keys.get(2), new VillagerTrade(
                new TradeCost(Items.EMERALD, 2), template(ModItems.SUGARBAG_HONEYCOMB.get(), 1), 32, 3, 0.2F, Optional.empty(), List.of()));
        context.register(tier2Keys.get(3), new VillagerTrade(
                new TradeCost(Items.EMERALD, 1), template(ModItems.TREAT_ON_A_STICK.get(), 1), 8, 3, 0.2F, Optional.empty(), List.of()));
        // The pre-26.1 implementation flipped a coin between selling and buying honey treats; both
        // entries are pooled here so the trade-set lottery preserves the feel.
        context.register(tier2Keys.get(4), new VillagerTrade(
                new TradeCost(ModItems.HONEY_TREAT.get(), 4), template(Items.EMERALD, 1), 50, 3, 0.2F, Optional.empty(), List.of()));
        context.register(tier2Keys.get(5), new VillagerTrade(
                new TradeCost(Items.EMERALD, 1), template(ModItems.HONEY_TREAT.get(), 2), 100, 3, 0.2F, Optional.empty(), List.of()));

        // Tier 3 — Journeyman — one trade per hive/box wood; the trade-set picks two at random.
        int tier3Idx = 0;
        for (String wood : BASE_HIVE_WOODS) {
            Block hive = ModBlocks.HIVES.get("advanced_" + wood + "_beehive").get();
            context.register(tier3Keys.get(tier3Idx++), new VillagerTrade(
                    new TradeCost(Items.BEEHIVE, 1), Optional.of(new TradeCost(Items.EMERALD, 6)),
                    template(hive.asItem(), 1), 12, 6, 0.2F, Optional.empty(), List.of()));
            Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + wood).get();
            context.register(tier3Keys.get(tier3Idx++), new VillagerTrade(
                    new TradeCost(Items.EMERALD, 4), template(box.asItem(), 1), 12, 6, 0.2F, Optional.empty(), List.of()));
        }

        // Tier 4 — Expert
        context.register(tier4Keys.get(0), new VillagerTrade(
                new TradeCost(Items.EMERALD, 12), Optional.of(new TradeCost(ModItems.BEE_CAGE.get(), 1)),
                template(ModItems.STURDY_BEE_CAGE.get(), 1), 12, 6, 0.2F, Optional.empty(), List.of()));
        context.register(tier4Keys.get(1), new VillagerTrade(
                new TradeCost(Items.EMERALD, 6), template(ModBlocks.JAR.get().asItem(), 1), 12, 8, 0.2F, Optional.empty(), List.of()));

        // Tier 5 — Master — bee nest already populated with a single empty bee occupant.
        DataComponentPatch beeNestPatch = DataComponentPatch.builder()
                .set(DataComponents.BEES, new Bees(List.of(BeehiveBlockEntity.Occupant.create(0))))
                .build();
        context.register(tier5Keys.get(0), new VillagerTrade(
                new TradeCost(Items.EMERALD, 32),
                new ItemStackTemplate(Items.BEE_NEST.builtInRegistryHolder(), 1, beeNestPatch),
                3, 16, 0.2F, Optional.empty(), List.of()));
        for (int i = 0; i < MASTER_SPAWN_EGGS.length; i++) {
            context.register(tier5Keys.get(i + 1), new VillagerTrade(
                    new TradeCost(Items.EMERALD, 24),
                    new ItemStackTemplate(itemHolder(MASTER_SPAWN_EGGS[i]), 1, DataComponentPatch.EMPTY),
                    12, 6, 0.2F, Optional.empty(), List.of()));
        }
    }

    public static void bootstrapTradeSets(BootstrapContext<TradeSet> context) {
        buildTradeKeys();
        HolderGetter<VillagerTrade> trades = context.lookup(Registries.VILLAGER_TRADE);

        register(context, BEEKEEPER_LEVEL_1, trades, tier1Keys, 2);
        register(context, BEEKEEPER_LEVEL_2, trades, tier2Keys, 2);
        register(context, BEEKEEPER_LEVEL_3, trades, tier3Keys, 2);
        register(context, BEEKEEPER_LEVEL_4, trades, tier4Keys, 2);
        register(context, BEEKEEPER_LEVEL_5, trades, tier5Keys, 2);
    }

    public static Int2ObjectMap<ResourceKey<TradeSet>> beekeeperTradeSetsByLevel() {
        return Int2ObjectMap.ofEntries(
                Int2ObjectMap.entry(ModProfessions.NOVICE, BEEKEEPER_LEVEL_1),
                Int2ObjectMap.entry(ModProfessions.APPRENTICE, BEEKEEPER_LEVEL_2),
                Int2ObjectMap.entry(ModProfessions.JOURNEYMAN, BEEKEEPER_LEVEL_3),
                Int2ObjectMap.entry(ModProfessions.EXPERT, BEEKEEPER_LEVEL_4),
                Int2ObjectMap.entry(ModProfessions.MASTER, BEEKEEPER_LEVEL_5));
    }

    private static void register(BootstrapContext<TradeSet> context, ResourceKey<TradeSet> key, HolderGetter<VillagerTrade> trades, List<ResourceKey<VillagerTrade>> tradeKeys, int amount) {
        List<Holder<VillagerTrade>> holders = new ArrayList<>(tradeKeys.size());
        for (ResourceKey<VillagerTrade> trade : tradeKeys) {
            holders.add(trades.getOrThrow(trade));
        }
        context.register(key, new TradeSet(
                HolderSet.direct(holders),
                ConstantValue.exactly(amount),
                false,
                Optional.empty()));
    }

    private static ItemStackTemplate template(Item item, int count) {
        return new ItemStackTemplate(item, count);
    }

    private static Holder<Item> itemHolder(String name) {
        return BuiltInRegistries.ITEM.getOrThrow(
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, name)));
    }

    private static ResourceKey<VillagerTrade> tradeKey(String path) {
        return ResourceKey.create(Registries.VILLAGER_TRADE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "beekeeper/" + path));
    }

    private static ResourceKey<TradeSet> tradeSetKey(String path) {
        return ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, path));
    }
}
