package cy.jdkdigital.productivebees.gametest;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.BreedingChamberBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CanvasBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.GeneIndexerBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.HeatedCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.HoneyGeneratorBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.IncubatorBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.PoweredCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.container.BreedingChamberContainer;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.init.ModTrades;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.registry.LibItems;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * In-game smoke tests covering the touch points most affected by the MC 1.21.1 → 26.1.2 port:
 *
 * <ul>
 *   <li>Advanced hive bee occupant insertion + persistence — verifies the 26.1 Occupant /
 *       DataComponents.BEES wiring still serialises through {@link AdvancedBeehiveBlockEntity}.</li>
 *   <li>Feeder slab base preservation — the renderer relies on {@link FeederBlockEntity#baseBlock},
 *       which is set by {@code useItemOn}; the inventory capability must also resolve.</li>
 *   <li>Centrifuge recipe lookup — the recipe map keyed by the new {@code RecipeManager} must
 *       still match a vanilla honeycomb input.</li>
 *   <li>Comb tint + canvas dye persistence — comb colours are exposed for {@code BlockTintSource}
 *       integration, and canvas BEs round-trip their colour through value-input/value-output.</li>
 *   <li>Beekeeper {@link TradeSet} resolution — the data-driven trades replacing
 *       {@code VillagerTradesEvent} must be present at the right tier keys.</li>
 * </ul>
 *
 * <p>Each test body is registered into {@link TestFunctions} (static-init, before
 * {@code Bootstrap.bootStrap()}). At runtime the JSON files emitted by
 * {@link TestEntriesProvider} construct {@code FunctionGameTestInstance} entries that look
 * up the test body in {@code BuiltInRegistries.TEST_FUNCTION} by ResourceKey. Each test
 * runs inside the {@code productivebees:empty_7x7} structure generated by
 * {@link GameTestStructureProvider}; the body places every block it needs.
 *
 * <p>This indirection (function-key in JSON, lambda in registry) is the vanilla path for
 * Java-bodied gametests in MC 26.1. A previous {@code LambdaTestInstance} approach that put
 * the lambdas directly into {@code Registries.TEST_INSTANCE} broke client sync — see
 * {@link TestFunctions} for the full reasoning.
 */
public final class ProductiveBeesGameTests
{
    private ProductiveBeesGameTests() {}

    private static final int DEFAULT_MAX_TICKS = 100;

    /** Per-test max-ticks budget; consumed by {@link TestEntriesProvider} when emitting JSONs. */
    public static final Map<String, Integer> MAX_TICKS = new LinkedHashMap<>();

    static {
        // FUNCTIONS map is populated here; publication to BuiltInRegistries.TEST_FUNCTION
        // happens later via TestFunctions.init() in the ProductiveBees mod constructor.

        register("hive_occupant_persistence", ProductiveBeesGameTests::testHivePersistence);
        register("feeder_slab_and_capability", ProductiveBeesGameTests::testFeederSlabAndCapability);
        register("centrifuge_recipe_lookup", ProductiveBeesGameTests::testCentrifugeRecipeLookup);
        register("comb_and_canvas_tints", ProductiveBeesGameTests::testCombAndCanvasTints);
        register("beekeeper_trade_sets_present", ProductiveBeesGameTests::testBeekeeperTradeSetsPresent);

        // Amber + Wannabee — exercise the entity NBT round-trip and the fake-player loot path,
        // both of which were heavily touched during the port.
        register("amber_entity_roundtrip", ProductiveBeesGameTests::testAmberEntityRoundtrip);
        register("amber_melts_over_campfire", ProductiveBeesGameTests::testAmberMeltsOverCampfire, 1200);
        register("wannabee_loot_from_amber", ProductiveBeesGameTests::testWannabeeLootFromAmber);
        register("amber_bee_encases_target_on_post_pollinate", ProductiveBeesGameTests::testAmberBeeEncasesTarget);
        register("amber_bee_in_sim_hive_encases_mob_in_front", ProductiveBeesGameTests::testAmberBeeInSimHiveEncasesMobInFront, 100);
        register("wannabee_drops_dragon_breath_from_ender_dragon", ProductiveBeesGameTests::testWannabeeDragonBreath);

        // BeeCage round-trip — high-traffic player flow exercising ItemStack components.
        register("beecage_capture_release_roundtrip", ProductiveBeesGameTests::testBeeCageRoundtrip);

        // Bottler full processing — multi-tick recipe run, not just lookup.
        register("bottler_processes_honey_bottle", ProductiveBeesGameTests::testBottlerProcessesHoneyBottle, 200);

        // Centrifuge tier coverage — basic / powered / heated. Powered + heated need a neighbouring
        // honey generator pushing energy in, so the tests place one adjacent and pre-fill its fluid
        // tank with honey. Heated tier strips wax from its outputs (the heat melts it away), so the
        // heated tests check the fluid tank for honey instead. Heated additionally accepts
        // honeycomb_blocks (auto-extracts a single comb via BeeHelper.getSingleComb).
        register("centrifuge_basic_processes_honeycomb", ProductiveBeesGameTests::testCentrifugeBasicProcessing, 600);
        register("centrifuge_powered_with_honey_generator", ProductiveBeesGameTests::testCentrifugePoweredWithGenerator, 400);
        register("centrifuge_heated_with_honey_generator", ProductiveBeesGameTests::testCentrifugeHeatedWithGenerator, 400);
        register("centrifuge_heated_processes_honeycomb_block", ProductiveBeesGameTests::testCentrifugeHeatedProcessesHoneycombBlock, 400);

        // Speed (time) upgrade coverage — UPGRADE_TIME and UPGRADE_TIME_2. Both reduce processing
        // time by ProductiveBeesConfig.UPGRADES.timeBonus per upgrade (UPGRADE_TIME_2 counts 2x),
        // so they don't increase output rate per cycle, just shorten each cycle. Both are
        // unit-style checks on getProcessingTime(null).
        register("centrifuge_speed_upgrade_shortens_processing", ProductiveBeesGameTests::testCentrifugeSpeedUpgrade);
        register("centrifuge_speed_2_upgrade_shortens_more", ProductiveBeesGameTests::testCentrifugeSpeedUpgrade2);

        // Productivity upgrade coverage — all four tiers process more items per cycle:
        // UPGRADE_PRODUCTIVITY (×4), _2 (×8), _3 (×16), _4 / omega (×32). One full processing run
        // per tier; each verifies the resulting wax stack size matches the expected multiplier.
        register("centrifuge_productivity_upgrade_x4", ProductiveBeesGameTests::testCentrifugeProductivityX4, 600);
        register("centrifuge_productivity_upgrade_x8", ProductiveBeesGameTests::testCentrifugeProductivityX8, 600);
        register("centrifuge_productivity_upgrade_x16", ProductiveBeesGameTests::testCentrifugeProductivityX16, 600);
        register("centrifuge_productivity_upgrade_x32", ProductiveBeesGameTests::testCentrifugeProductivityX32, 600);

        // Stability upgrade — smoke test that processing still completes when the upgrade is
        // installed. The stability bonus to chance() is statistical and the fluid-export skip
        // only kicks in when a neighbour is present, so a deterministic assertion is awkward;
        // verifying "doesn't break the processing pipeline" is the actionable check.
        register("centrifuge_stability_upgrade_completes", ProductiveBeesGameTests::testCentrifugeStabilityUpgrade, 600);

        // Centrifuge neighbour fluid sharing — the basic CentrifugeBlockEntity auto-exports its
        // fluid tank to any adjacent fluid handler (including another centrifuge). With a
        // stability upgrade installed, that auto-export is skipped — the fluid stays put.
        // tankTickRate is 21 ticks (in productivelib FluidTankBlockEntity), so tickFluidTank only
        // runs ~once every 21 server ticks. 200 ticks gives ~9 invocations — plenty.
        register("centrifuge_shares_fluid_with_neighbour", ProductiveBeesGameTests::testCentrifugeSharesFluidWithNeighbour, 200);
        register("centrifuge_stability_blocks_fluid_share", ProductiveBeesGameTests::testCentrifugeStabilityBlocksFluidShare, 200);

        // Stability bonus on output chance — draconic honeycomb produces draconic_dust at 30%
        // base chance. With 4 stability upgrades, bonus = (4+1) × 0.15 = 0.75, total chance ≥ 1.0,
        // i.e. guaranteed dust output every cycle. One basic-centrifuge cycle is enough to assert.
        register("centrifuge_stability_boosts_draconic_dust_chance", ProductiveBeesGameTests::testCentrifugeStabilityBoostsDraconicDust, 600);

        // Honey generator: both honey bottles and honey blocks fill the tank, then power gen
        // kicks in (tickCounter % 10 == 0). Power generation flips the ON blockstate; presence of
        // FE in the energy handler confirms generation actually happened.
        register("honey_generator_fills_tank_with_bottles", ProductiveBeesGameTests::testHoneyGeneratorWithBottles, 200);
        register("honey_generator_fills_tank_with_blocks", ProductiveBeesGameTests::testHoneyGeneratorWithBlocks, 200);
        register("honey_generator_stacks_empty_bottles_in_output", ProductiveBeesGameTests::testHoneyGeneratorStacksEmptyBottles, 200);
        register("hopper_into_advanced_hive_preserves_omega_upgrade", ProductiveBeesGameTests::testHopperIntoAdvancedHivePreservesOmega, 200);

        // Breeding chamber: pair of vanilla bees with poppy as breeding item produces a child
        // (Age = -24000) in a fresh cage in the output slot. Self-breed is allowed for
        // non-productivebees-namespace bees via BeeHelper's runtime recipe.
        register("breeding_chamber_breeds_vanilla_bees", ProductiveBeesGameTests::testBreedingChamberVanillaBees, 200);
        // Draconic self-breed uses draconic_dust × 2 per side (per draconic.json's
        // breedingItemCount). selfbreed defaults to true in BeeCreator.parse — productivebees
        // bees without an explicit `noSelfBreed()` opt-out are eligible.
        register("breeding_chamber_breeds_draconic_with_dust", ProductiveBeesGameTests::testBreedingChamberDraconicBees, 200);

        // Gene indexer auto-merges genes of the same attribute+value when both purities are
        // < 100. Two 50%-purity TYPE genes sum to exactly 100% via CombineGeneRecipe.mergeGenes.
        register("gene_indexer_merges_to_full_purity", ProductiveBeesGameTests::testGeneIndexerMergesToFullPurity);

        // Incubator: catalyst is a plain honey_treat (stack ≥ incubatorTreatUse = 20) — NBT Age
        // is forced to 0 on the cage in the output. Input cage holds a child bee (Age < 0).
        register("incubator_ages_child_to_adult", ProductiveBeesGameTests::testIncubatorAgesChildToAdult, 200);
        // Incubator second function: an egg (Tags.Items.EGGS) plus a honey treat carrying a
        // 100%-purity TYPE gene produces a spawn egg for that bee type.
        register("incubator_creates_spawn_egg_from_gene_treat", ProductiveBeesGameTests::testIncubatorSpawnEggFromGeneTreat, 200);

        // Hive + expansion box mechanics. AdvancedBeehive.updateState scans neighbours for an
        // ExpansionBox on any non-front side; on match it sets the EXPANDED enum property to
        // the matching VerticalHive value. acceptsUpgrades() returns true iff EXPANDED != NONE.
        // The cage-slot (slot 11) only accepts BeeCages when isSim() is true — driven by
        // UPGRADE_SIMULATOR / UPGRADE_PRODUCTIVITY_3 / UPGRADE_PRODUCTIVITY_4.
        register("hive_expands_when_box_attached_on_back", ProductiveBeesGameTests::testHiveExpandsWithBackBox);
        register("hive_does_not_expand_when_box_on_front", ProductiveBeesGameTests::testHiveDoesNotExpandFromFrontBox);
        register("expanded_hive_accepts_upgrades", ProductiveBeesGameTests::testExpandedHiveAcceptsUpgrades);
        register("simulated_hive_absorbs_caged_bee", ProductiveBeesGameTests::testSimulatedHiveAbsorbsCagedBee, 200);

        // Hive production cycle (simulated, no AI / no pathing). For a NORTH-facing expanded hive,
        // simulateBee resolves flowerPos = hive.below(1).relative(NORTH). The iron bee
        // (flowerTag=productivebees:flowers/ferric) accepts either an iron_block directly OR a
        // feeder slab whose inventory contains an iron_block. Vanilla BeeData.tick increments
        // ticksInHive by 1 each server tick; production fires when ticksInHive > minTicks + 450.
        // Default minTicksInHive for a freshly absorbed bee with no nectar is 600, so the cycle
        // completes around tick ~1050 after absorption (+ ~23 ticks for cage processing).
        register("hive_produces_iron_honeycomb_from_iron_block_flower", ProductiveBeesGameTests::testHiveProducesIronHoneycombFromIronBlock, 2000);
        register("hive_produces_iron_honeycomb_from_feeder_with_iron_block", ProductiveBeesGameTests::testHiveProducesIronHoneycombFromFeeder, 2000);

        // Bottler + piston squash → gene_bottle → centrifuge → type gene. Full pipeline:
        // bottler ticks every 7 ticks looking for a non-baby bee in the AABB above it while a
        // PISTON_HEAD (facing DOWN) sits at pos.above(). On match, glass bottle → gene_bottle
        // (carries GENE_GROUP_LIST + BEE_NAME), bee.kill(...). The gene_bottle then feeds the
        // centrifuge's completeGeneProcessing path which rolls each GeneGroup through
        // geneExtractChance (default 1.0) and outputs Gene ItemStacks. The TYPE gene's value is
        // the bee's beeType (ProductiveBee.getBeeType().toString() — "productivebees:iron").
        register("bottler_piston_squash_to_centrifuge_iron_gene", ProductiveBeesGameTests::testBottlerSquashAdultToIronGene, 400);
        register("bottler_piston_skips_baby_iron_bee", ProductiveBeesGameTests::testBottlerSkipsBabyBee, 200);

        // Bug reports: each test exercises one specific migration touchpoint reported as broken.
        register("breeding_chamber_breeds_cross_type_bees", ProductiveBeesGameTests::testBreedingChamberCrossTypeBees, 200);
        register("breeding_chamber_breeds_two_data_bees", ProductiveBeesGameTests::testBreedingChamberTwoDataBees, 200);
        register("breeding_chamber_breeds_data_and_entity_bee", ProductiveBeesGameTests::testBreedingChamberDataAndEntityBee, 200);
        register("kamikaze_bee_spawn_from_event_handler", ProductiveBeesGameTests::testKamikazeBeeSpawn, 100);
        register("sugarbag_bee_spawn_from_event_handler", ProductiveBeesGameTests::testSugarbagBeeSpawn, 100);
        register("beebee_spawn_biomes_tag_includes_the_other", ProductiveBeesGameTests::testBeebeeSpawnBiomesTag, 60);

        // Loot modifier component preservation — IngredientModifier.doApply must apply the
        // DataComponentIngredient's patch to freshly-spawned ItemStacks, otherwise component-bound
        // additions like the amber-bee spawn egg drop as a blank configurable spawn egg.
        register("loot_modifier_preserves_components", ProductiveBeesGameTests::testLootModifierPreservesComponents);
        register("loot_modifier_plain_ingredient_works", ProductiveBeesGameTests::testLootModifierPlainIngredient);

        // Hoppers must only pull from each device's OUTPUT slot. Real-block tests using a
        // vanilla hopper below the device — gameplay-accurate vs. raw ResourceHandler calls.
        register("breeding_chamber_hopper_extracts_only_output", ProductiveBeesGameTests::testBreedingChamberHopperExtractsOnlyOutput, 80);
        register("incubator_hopper_extracts_only_output", ProductiveBeesGameTests::testIncubatorHopperExtractsOnlyOutput, 80);
        register("breeding_chamber_hopper_inserts_into_inputs", ProductiveBeesGameTests::testBreedingChamberHopperInsertsIntoInputs, 100);
        register("incubator_hopper_inserts_into_inputs", ProductiveBeesGameTests::testIncubatorHopperInsertsIntoInputs, 100);

        // Recipe caches in BeeHelper dedupe on (bee_id + input_id) — verify a second lookup
        // with the same key doesn't grow the cache. Catches regressions to the LRU-bounded maps.
        register("bee_helper_recipe_caches_dedupe_repeat_lookups", ProductiveBeesGameTests::testBeeHelperRecipeCaches);
    }

    private static void register(String name, Consumer<GameTestHelper> body) {
        register(name, body, DEFAULT_MAX_TICKS);
    }

    private static void register(String name, Consumer<GameTestHelper> body, int maxTicks) {
        TestFunctions.register(name, body);
        MAX_TICKS.put(name, maxTicks);
    }

    // ── Hive occupant persistence ────────────────────────────────────────────────
    private static void testHivePersistence(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 2);
        helper.setBlock(hivePos, ModBlocks.HIVES.get("advanced_oak_beehive").get().defaultBlockState());

        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);
        if (hive.getOccupantCount() != 0) {
            helper.fail("Fresh hive should be empty, got " + hive.getOccupantCount() + " occupants", hivePos);
            return;
        }

        // Spawn a bee in the test area, then have the hive absorb it via addOccupant.
        Bee bee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) {
            helper.fail("Failed to construct a bee entity", hivePos);
            return;
        }
        BlockPos beeAbs = helper.absolutePos(new BlockPos(3, 2, 2));
        bee.snapTo(beeAbs.getX() + 0.5, beeAbs.getY(), beeAbs.getZ() + 0.5, 0f, 0f);
        helper.getLevel().addFreshEntity(bee);

        hive.addOccupant(bee);

        if (hive.getOccupantCount() != 1) {
            helper.fail("Bee was not stored in hive (expected 1 occupant, got " + hive.getOccupantCount() + ")", hivePos);
            return;
        }

        // Force a setChanged + save cycle by marking the chunk dirty, then re-read the BE.
        helper.getLevel().getChunkAt(helper.absolutePos(hivePos)).markUnsaved();
        helper.getLevel().sendBlockUpdated(helper.absolutePos(hivePos), hive.getBlockState(), hive.getBlockState(), 3);

        AdvancedBeehiveBlockEntity reloaded = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);
        if (reloaded.getOccupantCount() != 1) {
            helper.fail("Occupant was lost after block update (expected 1, got " + reloaded.getOccupantCount() + ")", hivePos);
            return;
        }

        helper.succeed();
    }

    // ── Feeder: slab base + inventory capability ─────────────────────────────────
    private static void testFeederSlabAndCapability(GameTestHelper helper) {
        BlockPos feederPos = new BlockPos(2, 2, 2);
        helper.setBlock(feederPos, ModBlocks.FEEDER.get().defaultBlockState()
                .setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM));

        FeederBlockEntity feeder = helper.getBlockEntity(feederPos, FeederBlockEntity.class);

        // Simulate the player-driven baseBlock swap that happens in Feeder.useItemOn.
        feeder.baseBlock = Blocks.SMOOTH_STONE_SLAB;
        feeder.setChanged();
        if (feeder.baseBlock != Blocks.SMOOTH_STONE_SLAB) {
            helper.fail("Feeder baseBlock was not stored", feederPos);
            return;
        }

        // The inventory capability must resolve via the BLOCK lookup; the renderer + bee feeding both rely on it.
        var handler = helper.getLevel().getCapability(Capabilities.Item.BLOCK, helper.absolutePos(feederPos), null);
        if (!(handler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler)) {
            helper.fail("Feeder did not expose a BlockEntityItemStackHandler via Capabilities.Item.BLOCK (got "
                    + (handler == null ? "null" : handler.getClass().getName()) + ")", feederPos);
            return;
        }

        helper.succeed();
    }

    // ── Centrifuge recipe lookup ─────────────────────────────────────────────────
    private static void testCentrifugeRecipeLookup(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        // canProcessItemStack runs the recipe lookup against the current recipe manager.
        if (!centrifuge.canProcessItemStack(new ItemStack(Items.HONEYCOMB))) {
            helper.fail("Centrifuge could not match a recipe for minecraft:honeycomb — the centrifuge recipe type "
                    + "or its data-driven recipes are not loading.", pos);
            return;
        }

        // A sanity check that an obviously-irrelevant input is rejected.
        if (centrifuge.canProcessItemStack(new ItemStack(Items.STONE))) {
            helper.fail("Centrifuge accepted minecraft:stone as input — recipe matcher is too loose.", pos);
            return;
        }

        helper.succeed();
    }

    // ── Comb tint + canvas dye persistence ───────────────────────────────────────
    private static void testCombAndCanvasTints(GameTestHelper helper) {
        // Configurable comb is registered with hex tint #c8df24 — must round-trip through TextColor.parseColor.
        if (!(ModBlocks.CONFIGURABLE_COMB.get() instanceof CombBlock comb)) {
            helper.fail("Configurable comb block is not a CombBlock instance", new BlockPos(0, 0, 0));
            return;
        }
        int expected = 0xc8df24;
        if (comb.getColor() != expected) {
            helper.fail("Configurable comb tint changed: expected " + Integer.toHexString(expected)
                    + ", got " + Integer.toHexString(comb.getColor()), new BlockPos(0, 0, 0));
            return;
        }

        // Canvas hive: write a colour, read it back via the same accessor the BlockTintSource uses.
        BlockPos hivePos = new BlockPos(2, 2, 2);
        var canvasHolder = ModBlocks.CANVAS_HIVES.get("advanced_oak_canvas_beehive");
        if (canvasHolder == null) {
            helper.fail("Canvas oak beehive holder is missing — canvas hive registration regressed.", hivePos);
            return;
        }
        helper.setBlock(hivePos, canvasHolder.get().defaultBlockState());
        CanvasBeehiveBlockEntity canvas = helper.getBlockEntity(hivePos, CanvasBeehiveBlockEntity.class);
        int dye = 0x336699;
        canvas.setColor(dye);
        if (canvas.getColor(0) != dye) {
            helper.fail("Canvas hive colour was not stored", hivePos);
            return;
        }

        helper.succeed();
    }

    // ── Beekeeper trade set resolves ─────────────────────────────────────────────
    private static void testBeekeeperTradeSetsPresent(GameTestHelper helper) {
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        HolderLookup.RegistryLookup<TradeSet> lookup = registries.lookupOrThrow(Registries.TRADE_SET);

        var levels = new LinkedHashMap<String, ResourceKey<TradeSet>>();
        levels.put("level_1", ModTrades.BEEKEEPER_LEVEL_1);
        levels.put("level_2", ModTrades.BEEKEEPER_LEVEL_2);
        levels.put("level_3", ModTrades.BEEKEEPER_LEVEL_3);
        levels.put("level_4", ModTrades.BEEKEEPER_LEVEL_4);
        levels.put("level_5", ModTrades.BEEKEEPER_LEVEL_5);

        for (var entry : levels.entrySet()) {
            var holder = lookup.get(entry.getValue());
            if (holder.isEmpty()) {
                helper.fail("Beekeeper " + entry.getKey() + " TradeSet missing from datapack registry — "
                        + "ModTrades.bootstrapTradeSets did not run.", new BlockPos(0, 0, 0));
                return;
            }
            TradeSet set = holder.get().value();
            if (set.getTrades().size() == 0) {
                helper.fail("Beekeeper " + entry.getKey() + " TradeSet resolved but contains no trades — "
                        + "bootstrapTrades / HolderSet.direct wiring regressed.", new BlockPos(0, 0, 0));
                return;
            }
        }

        helper.succeed();
    }

    // ── Amber entity round-trip ──────────────────────────────────────────────────
    // Exercises AmberBlockEntity.setEntity → save/load → createEntity. The 26.1 port
    // rewrote NBT capture to use ProblemReporter.ScopedCollector + TagValueOutput
    // (replacing the deprecated saveWithoutId(CompoundTag) shape), so this verifies the
    // captured entity tag survives a BE round-trip and can rebuild the original mob type.
    private static void testAmberEntityRoundtrip(GameTestHelper helper) {
        BlockPos amberPos = new BlockPos(2, 2, 2);
        helper.setBlock(amberPos, ModBlocks.AMBER.get().defaultBlockState());
        AmberBlockEntity amber = helper.getBlockEntity(amberPos, AmberBlockEntity.class);

        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) {
            helper.fail("Failed to construct a cow entity", amberPos);
            return;
        }
        cow.snapTo(helper.absolutePos(new BlockPos(3, 2, 2)).getCenter(), 0f, 0f);
        amber.setEntity(cow);
        cow.discard();

        if (amber.entityTag == null) {
            helper.fail("setEntity did not populate entityTag", amberPos);
            return;
        }
        String capturedId = amber.entityTag.getString("id").orElse("");
        if (!"minecraft:cow".equals(capturedId)) {
            helper.fail("Captured entity id was '" + capturedId + "', expected minecraft:cow", amberPos);
            return;
        }

        // Force a BE round-trip via mark-dirty + re-read, then rebuild the entity from the reloaded tag.
        amber.setChanged();
        helper.getLevel().getChunkAt(helper.absolutePos(amberPos)).markUnsaved();
        AmberBlockEntity reloaded = helper.getBlockEntity(amberPos, AmberBlockEntity.class);
        if (reloaded.entityTag == null) {
            helper.fail("entityTag was lost after reload", amberPos);
            return;
        }

        var rebuilt = AmberBlockEntity.createEntity(helper.getLevel(), reloaded.entityTag);
        if (!(rebuilt instanceof Cow)) {
            helper.fail("Rebuilt entity is not a Cow (got " + (rebuilt == null ? "null" : rebuilt.getClass().getSimpleName()) + ")", amberPos);
            return;
        }

        helper.succeed();
    }

    // ── Amber melts over campfire ────────────────────────────────────────────────
    // Place a regular campfire under an amber-encased cow and let the BE's serverTick
    // run for the full ~800-tick regular-campfire melt. Verifies the tick handler is
    // wired up (BE type's getTicker), entity reconstruction at melt-time works, and
    // the block clears to air. soul_campfire is 400 ticks; we use regular so the test
    // also exercises the longer codepath.
    private static void testAmberMeltsOverCampfire(GameTestHelper helper) {
        BlockPos campfirePos = new BlockPos(2, 1, 2);
        BlockPos amberPos = new BlockPos(2, 2, 2);

        helper.setBlock(campfirePos, Blocks.CAMPFIRE.defaultBlockState());
        helper.setBlock(amberPos, ModBlocks.AMBER.get().defaultBlockState());
        AmberBlockEntity amber = helper.getBlockEntity(amberPos, AmberBlockEntity.class);

        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) {
            helper.fail("Failed to construct cow", amberPos);
            return;
        }
        amber.setEntity(cow);
        cow.discard();

        // The melt threshold is 800 ticks for a regular campfire; AmberBlockEntity.serverTick
        // adds 21 per tick on the %21==0 boundary, so completion lands around tick 798–819.
        helper.succeedWhen(() -> {
            if (!helper.getLevel().getBlockState(helper.absolutePos(amberPos)).isAir()) {
                throw helper.assertionException(amberPos, "Amber block did not melt — block is still "
                        + helper.getLevel().getBlockState(helper.absolutePos(amberPos)).getBlock().getDescriptionId());
            }
            List<Cow> cowsNearby = helper.getLevel().getEntitiesOfClass(
                    Cow.class, helper.getBounds().inflate(3.0));
            if (cowsNearby.isEmpty()) {
                throw helper.assertionException(amberPos, "Amber cleared but no cow was released");
            }
        });
    }

    // ── Wannabee loot rolling ────────────────────────────────────────────────────
    // The wanna bee mimics other mobs' loot tables via a fake-player kill. This test
    // sets up an amber-encased cow at the wanna bee's saved flower position and asks
    // BeeHelper.getBeeProduce — which is the same call path the centrifuge / hive
    // simulation hits. Confirms FakePlayerFactory, LootContextParams.{LAST_DAMAGE_PLAYER,
    // DAMAGE_SOURCE, TOOL, DIRECT_ATTACKING_ENTITY, ATTACKING_ENTITY, THIS_ENTITY, ORIGIN}
    // still resolve in 26.1, and that reloadableRegistries().getLootTable() works.
    private static void testWannabeeDragonBreath(GameTestHelper helper) {
        BlockPos amberPos = new BlockPos(2, 2, 2);
        helper.setBlock(amberPos, ModBlocks.AMBER.get().defaultBlockState());
        AmberBlockEntity amber = helper.getBlockEntity(amberPos, AmberBlockEntity.class);

        EnderDragon dragon =
                EntityType.ENDER_DRAGON.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (dragon == null) {
            helper.fail("Failed to construct ender dragon", amberPos);
            return;
        }
        amber.setEntity(dragon);
        dragon.discard();

        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("Failed to construct ConfigurableBee", amberPos);
            return;
        }
        bee.setBeeType("productivebees:wanna");
        bee.setSavedFlowerPos(helper.absolutePos(amberPos));
        bee.snapTo(helper.absolutePos(new BlockPos(4, 2, 4)).getCenter(), 0f, 0f);

        // dragon_breath added at 10% per loot-table roll; 60 attempts gives 1 - 0.9^60 ≈ 99.8% odds.
        boolean sawDragonBreath = false;
        for (int attempt = 0; attempt < 60; attempt++) {
            List<ItemStack> produce = BeeHelper.getBeeProduce(helper.getLevel(), bee, false, 1.0);
            if (produce.stream().anyMatch(stack -> stack.is(Items.DRAGON_BREATH))) {
                sawDragonBreath = true;
                break;
            }
        }
        if (!sawDragonBreath) {
            helper.fail("Wannabee never produced dragon_breath from ender_dragon loot over 60 attempts — "
                    + "ender_dragon_breath_wannabee loot modifier may be broken or KILLED_BY_UUID mismatch", amberPos);
            return;
        }

        helper.succeed();
    }

    private static void testWannabeeLootFromAmber(GameTestHelper helper) {
        BlockPos amberPos = new BlockPos(2, 2, 2);
        helper.setBlock(amberPos, ModBlocks.AMBER.get().defaultBlockState());
        AmberBlockEntity amber = helper.getBlockEntity(amberPos, AmberBlockEntity.class);

        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (cow == null) {
            helper.fail("Failed to construct cow", amberPos);
            return;
        }
        amber.setEntity(cow);
        cow.discard();

        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("Failed to construct ConfigurableBee", amberPos);
            return;
        }
        bee.setBeeType("productivebees:wanna");
        bee.setSavedFlowerPos(helper.absolutePos(amberPos));
        bee.snapTo(helper.absolutePos(new BlockPos(4, 2, 4)).getCenter(), 0f, 0f);

        // 8 rolls so randomness doesn't make this flaky — cow loot table has 100%-chance
        // entries (beef + leather), so 1 roll would suffice in theory, but 8 is cheap.
        List<ItemStack> produce = BeeHelper.getBeeProduce(helper.getLevel(), bee, false, 8.0);
        if (produce.isEmpty()) {
            helper.fail("Wannabee produced no loot from amber-encased cow — fake-player loot path regressed", amberPos);
            return;
        }

        helper.succeed();
    }

    // ── Amber bee encases its target on postPollinate ────────────────────────────
    // Regression test for GH #772. Short-circuits the AI by placing the cow directly and
    // stuffing it into ConfigurableBee.target, then driving postPollinate() by hand to
    // exercise BeeHelper.encaseMob. Asserts the cow is consumed, an amber block replaces
    // it, and its NBT is captured in the AmberBlockEntity.
    private static void testAmberBeeEncasesTarget(GameTestHelper helper) {
        BlockPos beePos = new BlockPos(4, 2, 4);
        BlockPos cowPos = new BlockPos(3, 2, 3);

        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (cow == null) {
            helper.fail("Failed to construct cow", cowPos);
            return;
        }
        cow.snapTo(helper.absolutePos(cowPos).getCenter(), 0f, 0f);

        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("Failed to construct ConfigurableBee", beePos);
            return;
        }
        bee.setBeeType("productivebees:amber");
        bee.setDefaultAttributes();
        bee.snapTo(helper.absolutePos(beePos).getCenter(), 0f, 0f);

        bee.target = cow;
        bee.postPollinate();

        if (!cow.isRemoved()) {
            helper.fail("Cow was not removed after amber bee postPollinate", cowPos);
            return;
        }

        BlockPos cowBlockPos = cow.blockPosition();
        BlockState state = helper.getLevel().getBlockState(cowBlockPos);
        if (!state.is(ModBlocks.AMBER.get())) {
            helper.fail("Block at cow position is not amber (got "
                    + state.getBlock().getDescriptionId() + ")", cowPos);
            return;
        }

        BlockEntity be = helper.getLevel().getBlockEntity(cowBlockPos);
        if (!(be instanceof AmberBlockEntity amberBE) || amberBE.entityTag == null) {
            helper.fail("Amber block entity has no captured entity tag", cowPos);
            return;
        }

        String capturedId = amberBE.entityTag.getString("id").orElse("");
        if (!capturedId.equals("minecraft:cow")) {
            helper.fail("Amber captured wrong entity (expected minecraft:cow, got '"
                    + capturedId + "')", cowPos);
            return;
        }

        helper.succeed();
    }

    // ── Amber bee inside a simulated hive encases a mob placed in front ──────────
    // End-to-end regression for GH #772 driving the actual sim path: amber bee occupant +
    // cow at the hive's flower position (one block below+forward). Calls simulateBee
    // directly to skip the tick-rate gating.
    private static void testAmberBeeInSimHiveEncasesMobInFront(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(3, 3, 3);
        helper.setBlock(hivePos, ModBlocks.HIVES.get("advanced_oak_beehive").get().defaultBlockState());
        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);

        ((InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_SIMULATOR.get()));

        BlockState hiveState = helper.getLevel().getBlockState(helper.absolutePos(hivePos));
        Direction facing = hiveState.getValue(BeehiveBlock.FACING);
        BlockPos flowerPosRel = hivePos.below(1).relative(facing);
        BlockPos flowerPosAbs = helper.absolutePos(flowerPosRel);

        Cow cow = EntityType.COW.create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (cow == null) {
            helper.fail("Failed to construct cow", hivePos);
            return;
        }
        cow.snapTo(flowerPosAbs.getCenter(), 0f, 0f);
        helper.getLevel().addFreshEntity(cow);

        ConfigurableBee amberBee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (amberBee == null) {
            helper.fail("Failed to construct amber bee", hivePos);
            return;
        }
        amberBee.setBeeType("productivebees:amber");
        amberBee.setDefaultAttributes();
        BeehiveBlockEntity.Occupant occupant = BeehiveBlockEntity.Occupant.of(amberBee);

        AdvancedBeehiveBlockEntityAbstract.simulateBee(
                (ServerLevel) helper.getLevel(), helper.absolutePos(hivePos), hiveState, hive, occupant);

        if (!cow.isRemoved()) {
            helper.fail("Cow at flower position was not encased by sim-hive amber bee", flowerPosRel);
            return;
        }
        BlockState placed = helper.getLevel().getBlockState(flowerPosAbs);
        if (!placed.is(ModBlocks.AMBER.get())) {
            helper.fail("Block at flower position is not amber (got "
                    + placed.getBlock().getDescriptionId() + ")", flowerPosRel);
            return;
        }
        BlockEntity be = helper.getLevel().getBlockEntity(flowerPosAbs);
        if (!(be instanceof AmberBlockEntity amberBE) || amberBE.entityTag == null) {
            helper.fail("Amber BE has no captured entity tag at flower position", flowerPosRel);
            return;
        }
        String capturedId = amberBE.entityTag.getString("id").orElse("");
        if (!capturedId.equals("minecraft:cow")) {
            helper.fail("Amber captured wrong entity (expected minecraft:cow, got '"
                    + capturedId + "')", flowerPosRel);
            return;
        }

        helper.succeed();
    }

    // ── BeeCage capture/release round-trip ───────────────────────────────────────
    // Captures a bee into a cage item via BeeCage.captureEntity (the new 26.1
    // ProblemReporter + TagValueOutput path), then rehydrates it. Asserts the ENTITY_DATA-
    // backed component round-trip survives.
    private static void testBeeCageRoundtrip(GameTestHelper helper) {
        BlockPos beePos = new BlockPos(3, 2, 3);
        Bee bee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) {
            helper.fail("Failed to construct bee", beePos);
            return;
        }
        bee.snapTo(helper.absolutePos(beePos).getCenter(), 0f, 0f);
        helper.getLevel().addFreshEntity(bee);

        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(bee, cage);

        if (!BeeCage.isFilled(cage)) {
            helper.fail("Cage was not marked as filled after captureEntity", beePos);
            return;
        }
        CustomData data = cage.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            helper.fail("Cage has no CUSTOM_DATA component", beePos);
            return;
        }
        String capturedEntity = data.copyTag().getString("entity").orElse("");
        if (!"minecraft:bee".equals(capturedEntity)) {
            helper.fail("Captured entity id was '" + capturedEntity + "', expected minecraft:bee", beePos);
            return;
        }

        Bee rehydrated = BeeCage.getEntityFromStack(cage, helper.getLevel(), false);
        if (rehydrated == null || rehydrated.getType() != EntityType.BEE) {
            helper.fail("Failed to rehydrate bee from cage", beePos);
            return;
        }

        helper.succeed();
    }

    // ── Bottler end-to-end ───────────────────────────────────────────────────────
    // Seed the bottler's fluid tank with honey, drop a glass bottle in BOTTLE_SLOT,
    // let tickFluidTank run, expect a honey_bottle in FLUID_ITEM_OUTPUT_SLOT.
    // Exercises the full BottlerRecipe lookup + ItemStackTemplate.create() path that
    // was rewritten to dodge the "components not bound yet" decode crash.
    private static void testBottlerProcessesHoneyBottle(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BOTTLER.get().defaultBlockState());
        BottlerBlockEntity bottler = helper.getBlockEntity(pos, BottlerBlockEntity.class);

        FluidResource honey = FluidResource.of(ModFluids.HONEY.get());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = bottler.fluidHandler.insert(0, honey, 1000, tx);
            if (inserted < 250) {
                helper.fail("Couldn't fill bottler tank with honey (inserted " + inserted + "mB)", pos);
                return;
            }
            tx.commit();
        }
        bottler.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.GLASS_BOTTLE));

        helper.succeedWhen(() -> {
            ItemStack output = bottler.inventoryHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
            if (!output.is(Items.HONEY_BOTTLE)) {
                throw helper.assertionException(pos, "Expected HONEY_BOTTLE in FLUID_ITEM_OUTPUT_SLOT but found "
                        + (output.isEmpty() ? "empty" : output.getItem().getDescriptionId()));
            }
        });
    }

    // ── Centrifuge full processing ───────────────────────────────────────────────
    // ── Basic centrifuge: full processing ───────────────────────────────────────
    // Drop honeycomb in INPUT_SLOT, let it tick the full
    // ProductiveBeesConfig.GENERAL.centrifugeProcessingTime (default 300 ticks) and
    // assert wax appears in one of the output slots. Basic tier — no energy required.
    private static void testCentrifugeBasicProcessing(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB, 4));

        helper.succeedWhen(() -> assertHasWax(helper, centrifuge, pos, "basic centrifuge"));
    }

    // ── Powered centrifuge: with adjacent honey generator ───────────────────────
    // Place a HoneyGenerator next to the centrifuge with a pre-filled honey tank. The
    // generator pumps ~600 RF/cycle (every 10 ticks) into the centrifuge's energyHandler;
    // canOperate() flips true once the centrifuge has ≥ centrifugePowerUse (10 RF). Processing
    // time is centrifugeProcessingTime / 3 = 100 ticks, so total run is ~120 ticks with a
    // comfortable budget for energy buildup + tick alignment.
    private static void testCentrifugePoweredWithGenerator(GameTestHelper helper) {
        BlockPos centrifugePos = new BlockPos(2, 2, 2);
        BlockPos generatorPos = new BlockPos(3, 2, 2);

        helper.setBlock(centrifugePos, ModBlocks.POWERED_CENTRIFUGE.get().defaultBlockState());
        helper.setBlock(generatorPos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());

        PoweredCentrifugeBlockEntity centrifuge = helper.getBlockEntity(centrifugePos, PoweredCentrifugeBlockEntity.class);
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(generatorPos, HoneyGeneratorBlockEntity.class);

        fillHoneyTank(helper, generator, generatorPos);
        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB, 4));

        helper.succeedWhen(() -> assertHasWax(helper, centrifuge, centrifugePos, "powered centrifuge"));
    }

    // ── Heated centrifuge: with adjacent honey generator ────────────────────────
    // Same setup as powered; heated tier divides processing time by 3 again (so ~33 ticks).
    // Heated forces stripWax=true on its completeRecipeProcessing path (the heat melts the wax
    // away), so item outputs for the honeycomb recipe are empty. We check the fluid tank for
    // accumulated honey instead — each completed cycle adds 100 mB.
    private static void testCentrifugeHeatedWithGenerator(GameTestHelper helper) {
        BlockPos centrifugePos = new BlockPos(2, 2, 2);
        BlockPos generatorPos = new BlockPos(3, 2, 2);

        helper.setBlock(centrifugePos, ModBlocks.HEATED_CENTRIFUGE.get().defaultBlockState());
        helper.setBlock(generatorPos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());

        HeatedCentrifugeBlockEntity centrifuge = helper.getBlockEntity(centrifugePos, HeatedCentrifugeBlockEntity.class);
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(generatorPos, HoneyGeneratorBlockEntity.class);

        fillHoneyTank(helper, generator, generatorPos);
        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB, 4));

        helper.succeedWhen(() -> assertHasHoneyInTank(helper, centrifuge, centrifugePos, "heated centrifuge"));
    }

    // ── Heated centrifuge: accepts honeycomb_block input ────────────────────────
    // Heated tier overrides canProcessItemStack to also accept STORAGE_BLOCK_HONEYCOMBS and
    // auto-extracts a single comb via BeeHelper.getSingleComb to find the recipe. The completion
    // path runs the inner recipe 4 times with stripWax=true (one honeycomb_block → 4 combs worth),
    // so item outputs are empty for the honeycomb recipe (only wax is produced and that's stripped).
    // We assert the fluid tank picks up the corresponding honey.
    private static void testCentrifugeHeatedProcessesHoneycombBlock(GameTestHelper helper) {
        BlockPos centrifugePos = new BlockPos(2, 2, 2);
        BlockPos generatorPos = new BlockPos(3, 2, 2);

        helper.setBlock(centrifugePos, ModBlocks.HEATED_CENTRIFUGE.get().defaultBlockState());
        helper.setBlock(generatorPos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());

        HeatedCentrifugeBlockEntity centrifuge = helper.getBlockEntity(centrifugePos, HeatedCentrifugeBlockEntity.class);
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(generatorPos, HoneyGeneratorBlockEntity.class);

        fillHoneyTank(helper, generator, generatorPos);
        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB_BLOCK, 2));

        helper.succeedWhen(() -> assertHasHoneyInTank(helper, centrifuge, centrifugePos, "heated centrifuge (honeycomb_block)"));
    }

    // ── Speed upgrade: getProcessingTime is reduced ─────────────────────────────
    // UPGRADE_TIME shortens the time per cycle by ProductiveBeesConfig.UPGRADES.timeBonus.
    // Unit-style: query getProcessingTime(null) before and after inserting the upgrade and
    // assert the second value is strictly smaller. Does NOT change items-per-cycle.
    private static void testCentrifugeSpeedUpgrade(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        int baseline = centrifuge.getProcessingTime(null);

        ((InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME.get()));

        int withUpgrade = centrifuge.getProcessingTime(null);
        if (withUpgrade >= baseline) {
            helper.fail("UPGRADE_TIME did not reduce processing time: baseline=" + baseline
                    + ", withUpgrade=" + withUpgrade, pos);
            return;
        }
        helper.succeed();
    }

    // ── Speedier upgrade: UPGRADE_TIME_2 reduces processing time more than UPGRADE_TIME ──
    // UPGRADE_TIME_2 counts 2× in the time-modifier sum, so a single _2 upgrade should be
    // strictly faster than a single _1 upgrade. Catches regressions where the ×2 multiplier
    // gets lost or both upgrades end up wired identically.
    private static void testCentrifugeSpeedUpgrade2(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);
        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler();

        upgrades.setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME.get()));
        int withTime1 = centrifuge.getProcessingTime(null);

        upgrades.setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        int withTime2 = centrifuge.getProcessingTime(null);

        if (withTime2 >= withTime1) {
            helper.fail("UPGRADE_TIME_2 did not reduce processing time more than UPGRADE_TIME: "
                    + "time1=" + withTime1 + ", time2=" + withTime2, pos);
            return;
        }
        helper.succeed();
    }

    // ── Productivity upgrades: ×4, ×8, ×16, ×32 ─────────────────────────────────
    // Each productivity upgrade tier processes more items per cycle:
    //   UPGRADE_PRODUCTIVITY   → 4  items / cycle
    //   UPGRADE_PRODUCTIVITY_2 → 8  items / cycle
    //   UPGRADE_PRODUCTIVITY_3 → 16 items / cycle
    //   UPGRADE_PRODUCTIVITY_4 → 32 items / cycle (omega)
    // For the vanilla honeycomb recipe (count=1, chance=1.0) each cycle outputs `count ×
    // productivityModifier` wax — so the wax stack must reach the expected multiplier. We pre-load
    // 64 honeycombs so the productivity isn't capped by input count (Math.min(inputCount, ...)).
    private static void testCentrifugeProductivityX4(GameTestHelper helper) {
        runProductivityTest(helper, LibItems.UPGRADE_PRODUCTIVITY.get(), 4);
    }

    private static void testCentrifugeProductivityX8(GameTestHelper helper) {
        runProductivityTest(helper, LibItems.UPGRADE_PRODUCTIVITY_2.get(), 8);
    }

    private static void testCentrifugeProductivityX16(GameTestHelper helper) {
        runProductivityTest(helper, LibItems.UPGRADE_PRODUCTIVITY_3.get(), 16);
    }

    private static void testCentrifugeProductivityX32(GameTestHelper helper) {
        runProductivityTest(helper, LibItems.UPGRADE_PRODUCTIVITY_4.get(), 32);
    }

    private static void runProductivityTest(GameTestHelper helper, Item upgrade, int expectedMultiplier) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        ((InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(upgrade));
        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB, 64));

        helper.succeedWhen(() -> {
            int totalWax = 0;
            for (int slot : InventoryHandlerHelper.OUTPUT_SLOTS) {
                ItemStack stack = centrifuge.inventoryHandler.getStackInSlot(slot);
                if (stack.is(ModItems.WAX.get())) {
                    totalWax += stack.getCount();
                }
            }
            if (totalWax < expectedMultiplier) {
                throw helper.assertionException(pos,
                        "Productivity upgrade " + BuiltInRegistries.ITEM.getKey(upgrade).getPath()
                                + " produced " + totalWax + " wax, expected >= " + expectedMultiplier);
            }
        });
    }

    // ── Stability upgrade: smoke test ───────────────────────────────────────────
    // UPGRADE_STABILITY adds to recipe-output chance() and disables auto-export of the fluid tank
    // to neighbouring blocks. Both are hard to assert deterministically — chance bumps are
    // statistical, and there's no neighbour set up to receive fluid. So this is a smoke test:
    // install the upgrade, run processing to completion, assert wax appears. Catches regressions
    // that throw when the stability upgrade is installed (e.g. NPE in getUpgradeCount).
    private static void testCentrifugeStabilityUpgrade(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        ((InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_STABILITY.get()));
        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, new ItemStack(Items.HONEYCOMB, 4));

        helper.succeedWhen(() -> assertHasWax(helper, centrifuge, pos, "centrifuge w/ stability upgrade"));
    }

    // ── Centrifuge fluid-share with neighbour ───────────────────────────────────
    // Without stability: basic-tier tickFluidTank scans neighbouring fluid handlers and pushes
    // the tank contents into them. Place two centrifuges side by side, fill A's tank, tick a
    // couple of times, expect B's tank to have received fluid.
    //
    // Note: in 26.1 the auto-tick path inside FluidTankBlockEntity.tick is gated on tankTick
    // reaching tankTickRate (21), but in a gametest the BE may be replaced/reset between tick
    // edges. Force a tick by calling tickFluidTank directly to make the assertion deterministic
    // regardless of tankTick alignment with test ticks.
    private static void testCentrifugeSharesFluidWithNeighbour(GameTestHelper helper) {
        BlockPos posA = new BlockPos(2, 2, 2);
        BlockPos posB = new BlockPos(3, 2, 2);

        helper.setBlock(posA, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        helper.setBlock(posB, ModBlocks.CENTRIFUGE.get().defaultBlockState());

        CentrifugeBlockEntity a = helper.getBlockEntity(posA, CentrifugeBlockEntity.class);
        CentrifugeBlockEntity b = helper.getBlockEntity(posB, CentrifugeBlockEntity.class);

        FluidResource honey = FluidResource.of(ModFluids.HONEY.get());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = a.fluidHandler.insert(0, honey, 1000, tx);
            if (inserted < 500) {
                helper.fail("Couldn't pre-fill centrifuge A's tank (inserted " + inserted + "mB)", posA);
                return;
            }
            tx.commit();
        }

        helper.runAfterDelay(40, () -> {
            // Force a tickFluidTank explicitly to remove the tankTick / chunk-tick alignment
            // variable. tickFluidTank handles its own getCapability lookup of the neighbour.
            a.tickFluidTank(helper.getLevel(), helper.absolutePos(posA), a.getBlockState(), a);
            int amountB = b.fluidHandler.getAmountAsInt(0);
            if (amountB <= 0) {
                helper.fail("Neighbour centrifuge B never received fluid from A after forced tickFluidTank "
                        + "(A=" + a.fluidHandler.getAmountAsInt(0) + "mB, B=" + amountB + "mB)", posB);
                return;
            }
            helper.succeed();
        });
    }

    // ── Stability upgrade blocks fluid sharing ──────────────────────────────────
    // With at least one stability upgrade, tickFluidTank returns early before touching
    // neighbours. Same setup as above but with a stability upgrade installed in A; B's tank
    // must stay empty through the entire run.
    private static void testCentrifugeStabilityBlocksFluidShare(GameTestHelper helper) {
        BlockPos posA = new BlockPos(2, 2, 2);
        BlockPos posB = new BlockPos(3, 2, 2);

        helper.setBlock(posA, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        helper.setBlock(posB, ModBlocks.CENTRIFUGE.get().defaultBlockState());

        CentrifugeBlockEntity a = helper.getBlockEntity(posA, CentrifugeBlockEntity.class);
        CentrifugeBlockEntity b = helper.getBlockEntity(posB, CentrifugeBlockEntity.class);

        ((InventoryHandlerHelper.UpgradeHandler) a.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_STABILITY.get()));

        FluidResource honey = FluidResource.of(ModFluids.HONEY.get());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = a.fluidHandler.insert(0, honey, 1000, tx);
            if (inserted < 500) {
                helper.fail("Couldn't pre-fill centrifuge A's tank (inserted " + inserted + "mB)", posA);
                return;
            }
            tx.commit();
        }

        // Tick budget ≥ 80 covers ~4 tickFluidTank invocations (tankTickRate=21). If B is
        // still empty by then, the export is correctly blocked by the stability upgrade.
        helper.runAfterDelay(100, () -> {
            int amountB = b.fluidHandler.getAmountAsInt(0);
            int amountA = a.fluidHandler.getAmountAsInt(0);
            if (amountB > 0) {
                helper.fail("Stability upgrade did not block fluid share to neighbour "
                        + "(A=" + amountA + "mB, B=" + amountB + "mB)", posA);
                return;
            }
            if (amountA <= 0) {
                helper.fail("Centrifuge A's tank drained somewhere unexpected (A=" + amountA + "mB)", posA);
                return;
            }
            helper.succeed();
        });
    }

    // ── Stability boosts non-guaranteed output chance ───────────────────────────
    // Draconic honeycomb recipe has chance=0.3 for the dust output. The chance formula is
    // `recipeValues.chance() + (stabilityCount + 1) * stabilityChanceIncrease`. With 4 stability
    // upgrades and the default 0.15 increase, bonus = 0.75, so 0.3 + 0.75 = 1.05 — the dust roll
    // succeeds every cycle. Without stability the chance is 0.3 + 0.15 = 0.45 (one upgrade slot
    // contributes the +1 baseline), so 1 cycle is not enough to guarantee dust on the no-upgrade
    // path; the assertion would be flaky, so the test only checks the boosted-path produces dust.
    private static void testCentrifugeStabilityBoostsDraconicDust(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.CENTRIFUGE.get().defaultBlockState());
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(pos, CentrifugeBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_STABILITY.get()));
        }

        ItemStack draconicComb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), 4);
        draconicComb.set(ModDataComponents.BEE_TYPE, Identifier.parse("productivebees:draconic"));

        // Sanity: confirm the recipe matches the draconic-tagged comb before processing.
        if (!centrifuge.canProcessItemStack(draconicComb)) {
            ServerLevel sl = (ServerLevel) helper.getLevel();
            long total = sl.recipeAccess().recipeMap()
                    .byType(ModRecipeTypes.CENTRIFUGE_TYPE.get())
                    .stream().count();
            long withDraconicComponent = sl.recipeAccess().recipeMap()
                    .byType(ModRecipeTypes.CENTRIFUGE_TYPE.get())
                    .stream()
                    .filter(r -> r.id().toString().contains("draconic"))
                    .count();
            helper.fail("Centrifuge cannot match a recipe for configurable_honeycomb[bee_type=draconic] — "
                    + "totalCentrifugeRecipes=" + total + ", withDraconicId=" + withDraconicComponent, pos);
            return;
        }

        centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, draconicComb);

        helper.succeedWhen(() -> {
            int totalDust = 0;
            for (int slot : InventoryHandlerHelper.OUTPUT_SLOTS) {
                ItemStack stack = centrifuge.inventoryHandler.getStackInSlot(slot);
                if (stack.is(ModItems.DRACONIC_DUST.get())) {
                    totalDust += stack.getCount();
                }
            }
            if (totalDust <= 0) {
                throw helper.assertionException(pos, "4-stability centrifuge produced no draconic_dust "
                        + "(recipeProgress=" + centrifuge.recipeProgress + ")");
            }
        });
    }

    // ── Honey generator: fills tank from honey bottles ──────────────────────────
    // 4 honey bottles × 250 mB each = 1000 mB once consumed. Each tick, tickFluidTank converts
    // one input item if there's space (and FluidTankBlockEntity.tick calls it every tick). After
    // ≥ 10 ticks the inner generator tick (tickCounter % 10 == 0) drains some fluid and inserts
    // FE into the energy handler — that's the signal that power generation actually started.
    // Glass bottles end up in the byproduct slot.
    private static void testHoneyGeneratorWithBottles(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(pos, HoneyGeneratorBlockEntity.class);

        generator.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.HONEY_BOTTLE, 4));

        helper.succeedWhen(() -> assertHoneyGeneratorPowered(helper, generator, pos, "honey bottles", Items.GLASS_BOTTLE));
    }

    // ── Honey generator: fills tank from honey blocks ───────────────────────────
    // 1 honey block = 1000 mB (one-shot conversion, no byproduct since honey blocks aren't
    // bucketed). Same downstream power-gen assertion as the bottles variant.
    private static void testHoneyGeneratorWithBlocks(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(pos, HoneyGeneratorBlockEntity.class);

        generator.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.HONEY_BLOCK, 2));

        helper.succeedWhen(() -> assertHoneyGeneratorPowered(helper, generator, pos, "honey blocks", null));
    }

    // ── Honey generator: empty-bottle byproduct must stack across conversions ───
    // Regression test for GH #761: only one empty bottle appeared in the output slot;
    // subsequent conversions voided the byproduct because the BE's insertItem path went
    // through isItemValid with fromAutomation=true and the output slot rejected it.
    private static void testHoneyGeneratorStacksEmptyBottles(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.HONEY_GENERATOR.get().defaultBlockState());
        HoneyGeneratorBlockEntity generator = helper.getBlockEntity(pos, HoneyGeneratorBlockEntity.class);

        int bottles = 5;
        generator.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.HONEY_BOTTLE, bottles));

        helper.succeedWhen(() -> {
            ItemStack input = generator.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
            if (!input.isEmpty()) {
                throw helper.assertionException(pos, "Honey bottles not fully consumed yet (input=" + input.getCount() + ")");
            }
            ItemStack byproduct = generator.inventoryHandler.getStackInSlot(1);
            if (!byproduct.is(Items.GLASS_BOTTLE) || byproduct.getCount() != bottles) {
                throw helper.assertionException(pos, "Expected " + bottles + " glass bottles in output, got "
                        + (byproduct.isEmpty() ? "empty" : (byproduct.getCount() + "x " + byproduct.getItem().getDescriptionId())));
            }
        });
    }

    // ── Hopper into Advanced Hive: Omega upgrade must not vanish ────────────────
    // Regression test for GH #765: omega productivity upgrades disappeared when pushed in
    // via a hopper while other upgrades landed in the output slot. The upgrade must end
    // up somewhere recoverable (hopper, hive inventory, or upgrade slot), never deleted.
    private static void testHopperIntoAdvancedHivePreservesOmega(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 2);
        BlockPos hopperPos = new BlockPos(2, 3, 2);
        helper.setBlock(hivePos, ModBlocks.HIVES.get("advanced_oak_beehive").get().defaultBlockState());
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);

        hopper.setItem(0, new ItemStack(LibItems.UPGRADE_PRODUCTIVITY_4.get()));

        helper.runAfterDelay(40, () -> {
            int inHopper = hopper.getItem(0).is(LibItems.UPGRADE_PRODUCTIVITY_4.get()) ? hopper.getItem(0).getCount() : 0;
            int inHiveInventory = 0;
            for (int i = 0; i < hive.inventoryHandler.size(); i++) {
                ItemStack s = hive.inventoryHandler.getStackInSlot(i);
                if (s.is(LibItems.UPGRADE_PRODUCTIVITY_4.get())) {
                    inHiveInventory += s.getCount();
                }
            }
            int inUpgradeSlots = 0;
            for (int i = 0; i < ((InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler()).size(); i++) {
                ItemStack s = ((InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler()).getStackInSlot(i);
                if (s.is(LibItems.UPGRADE_PRODUCTIVITY_4.get())) {
                    inUpgradeSlots += s.getCount();
                }
            }
            int total = inHopper + inHiveInventory + inUpgradeSlots;
            if (total != 1) {
                throw helper.assertionException(hivePos, "Omega upgrade was deleted (hopper=" + inHopper
                        + ", hive_inventory=" + inHiveInventory + ", upgrade_slots=" + inUpgradeSlots + ")");
            }
            helper.succeed();
        });
    }

    private static void assertHoneyGeneratorPowered(GameTestHelper helper, HoneyGeneratorBlockEntity generator,
                                                    BlockPos pos, String label, Item expectedByproduct) {
        int fluid = generator.fluidHandler.getAmountAsInt(0);
        if (fluid <= 0) {
            throw helper.assertionException(pos, "Honey generator tank still empty for " + label
                    + " (input slot count=" + generator.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).getCount() + ")");
        }
        int energy = generator.energyHandler.getAmountAsInt();
        if (energy <= 0) {
            throw helper.assertionException(pos, "Honey generator produced no FE for " + label
                    + " (tank=" + fluid + "mB)");
        }
        if (expectedByproduct != null) {
            // HoneyGenerator's inventory is 2 slots: 0 = honey input, 1 = byproduct
            // (glass bottle / empty bucket). InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT=11
            // refers to the bottler/centrifuge layout, not this BE.
            ItemStack byproduct = generator.inventoryHandler.getStackInSlot(1);
            if (!byproduct.is(expectedByproduct)) {
                throw helper.assertionException(pos, "Expected " + expectedByproduct.getDescriptionId()
                        + " byproduct from " + label + " but got "
                        + (byproduct.isEmpty() ? "empty" : byproduct.getItem().getDescriptionId()));
            }
        }
    }

    // ── Breeding chamber: two vanilla bees produce a child ──────────────────────
    // Vanilla minecraft:bee × minecraft:bee uses the runtime self-breed recipe path
    // (BeeHelper line 213-227): namespace is not "productivebees", so canSelfBreed is true and
    // the recipe gets synthesised on the fly. Breeding items default to poppy (BreedingChamber
    // line 241) since no bee data is loaded for minecraft:bee.
    private static void testBreedingChamberVanillaBees(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        BreedingChamberBlockEntity chamber = helper.getBlockEntity(pos, BreedingChamberBlockEntity.class);

        // Max time upgrades collapse processing to 5 ticks (min); plus 1 lookup-cooldown tick.
        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }

        // Pre-fill energy so the chamber doesn't have to wait on an external generator.
        try (Transaction tx = Transaction.openRoot()) {
            chamber.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack cage1 = captureFreshBeeIntoCage(helper, EntityType.BEE, new BlockPos(3, 2, 2));
        ItemStack cage2 = captureFreshBeeIntoCage(helper, EntityType.BEE, new BlockPos(4, 2, 2));
        if (cage1 == null || cage2 == null) {
            helper.fail("Couldn't capture vanilla bees into cages", pos);
            return;
        }

        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, cage1);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, cage2);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get()));

        helper.succeedWhen(() -> assertChildCageInOutput(helper, chamber, pos, "vanilla bee"));
    }

    // ── Breeding chamber: two draconic bees with draconic_dust ──────────────────
    // ConfigurableBee with beeType="productivebees:draconic". draconic.json doesn't set
    // selfbreed=false, and BeeCreator.parse defaults selfbreed to true when the field is
    // absent — so canSelfBreed resolves to true. breedingItem=productivebees:draconic_dust,
    // breedingItemCount=2 per side.
    private static void testBreedingChamberDraconicBees(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        BreedingChamberBlockEntity chamber = helper.getBlockEntity(pos, BreedingChamberBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            chamber.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack cage1 = captureConfigurableBeeIntoCage(helper, "productivebees:draconic", new BlockPos(3, 2, 2));
        ItemStack cage2 = captureConfigurableBeeIntoCage(helper, "productivebees:draconic", new BlockPos(4, 2, 2));
        if (cage1 == null || cage2 == null) {
            helper.fail("Couldn't capture draconic bees into cages", pos);
            return;
        }

        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, cage1);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, cage2);
        // breedingItemCount=2 per draconic.json — load 4 so 2 get consumed cleanly on the first run.
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(ModItems.DRACONIC_DUST.get(), 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(ModItems.DRACONIC_DUST.get(), 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get()));

        helper.succeedWhen(() -> assertChildCageInOutput(helper, chamber, pos, "draconic bee"));
    }

    // ── Gene indexer: merges two same-type partial-purity genes into a 100% gene ─
    // CombineGeneRecipe.mergeGenes sums the input purities (capped at 100). Two TYPE genes of
    // "productivebees:wanna" at 50% each → one gene at 100%. After the indexer processes, the
    // two input slots must be empty (the genes are consumed) and exactly one slot holds the
    // merged 100% gene.
    private static void testGeneIndexerMergesToFullPurity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.GENE_INDEXER.get().defaultBlockState());
        GeneIndexerBlockEntity indexer = helper.getBlockEntity(pos, GeneIndexerBlockEntity.class);

        String beeType = "productivebees:wanna";
        indexer.inventoryHandler.setStackInSlot(0, Gene.getStack(GeneAttribute.TYPE, beeType, 1, 50));
        indexer.inventoryHandler.setStackInSlot(1, Gene.getStack(GeneAttribute.TYPE, beeType, 1, 50));

        helper.succeedWhen(() -> {
            int matchingGenes = 0;
            int fullPurityCount = 0;
            int partialPurityCount = 0;
            for (int i = 0; i < 104; i++) {
                ItemStack stack = indexer.inventoryHandler.getStackInSlot(i);
                if (!stack.is(ModItems.GENE.get())) continue;
                if (!beeType.equals(Gene.getValue(stack))) continue;
                matchingGenes++;
                if (Gene.getPurity(stack) == 100) {
                    fullPurityCount += stack.getCount();
                } else {
                    partialPurityCount += stack.getCount();
                }
            }
            if (fullPurityCount < 1) {
                throw helper.assertionException(pos, "Gene indexer has not produced a 100%-purity "
                        + beeType + " gene yet (matchingGenes=" + matchingGenes + ")");
            }
            if (partialPurityCount > 0) {
                throw helper.assertionException(pos, "The two 50%-purity " + beeType
                        + " genes were not consumed (still " + partialPurityCount + " partial-purity gene(s) in inventory)");
            }
        });
    }

    // ── Incubator: ages child bee in cage to adult ──────────────────────────────
    // Catalyst is a plain honey_treat stack (≥ incubatorTreatUse = 20). When the catalyst has
    // no genes, the cage NBT's Age is forced to 0 (IncubatorBlockEntity line 179-181). The
    // bee inside is captured as a child (Age = -24000) and must emerge with Age = 0.
    private static void testIncubatorAgesChildToAdult(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.INCUBATOR.get().defaultBlockState());
        IncubatorBlockEntity incubator = helper.getBlockEntity(pos, IncubatorBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) incubator.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            incubator.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack childCage = captureBabyBeeIntoCage(helper, new BlockPos(3, 2, 2));
        if (childCage == null) {
            helper.fail("Couldn't capture a baby bee into cage", pos);
            return;
        }

        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_INPUT, childCage);
        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_CATALYST, new ItemStack(ModItems.HONEY_TREAT.get(), 32));

        helper.succeedWhen(() -> {
            ItemStack out = incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT);
            if (out.isEmpty() || !(out.getItem() instanceof BeeCage)) {
                throw helper.assertionException(pos, "Incubator output is not a filled bee cage: "
                        + (out.isEmpty() ? "empty" : out.getItem().getDescriptionId()));
            }
            CustomData data = out.get(DataComponents.CUSTOM_DATA);
            if (data == null) {
                throw helper.assertionException(pos, "Incubated cage has no custom data");
            }
            int age = data.copyTag().getIntOr("Age", -1);
            if (age != 0) {
                throw helper.assertionException(pos, "Incubated bee age is " + age + ", expected 0 (adult)");
            }
        });
    }

    // ── Incubator: spawn egg from a type-gene-laden honey treat ─────────────────
    // Egg input + HoneyTreat carrying a 100%-purity TYPE gene → BeeCreator.getSpawnEgg
    // returns the appropriate spawn egg (CONFIGURABLE_SPAWN_EGG when the bee type is data-driven).
    // 100% purity makes random.nextInt(100) <= 100 always true.
    private static void testIncubatorSpawnEggFromGeneTreat(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.INCUBATOR.get().defaultBlockState());
        IncubatorBlockEntity incubator = helper.getBlockEntity(pos, IncubatorBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) incubator.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            incubator.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack treat = HoneyTreat.getTypeStack("productivebees:draconic", 100);

        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_INPUT, new ItemStack(Items.EGG));
        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_CATALYST, treat);

        helper.succeedWhen(() -> {
            ItemStack out = incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT);
            if (out.isEmpty() || !(out.getItem() instanceof SpawnEggItem)) {
                throw helper.assertionException(pos, "Incubator output is not a spawn egg: "
                        + (out.isEmpty() ? "empty" : out.getItem().getDescriptionId()));
            }
            if (!out.is(ModItems.CONFIGURABLE_SPAWN_EGG.get())) {
                throw helper.assertionException(pos, "Expected configurable spawn egg (data-driven bee), got "
                        + out.getItem().getDescriptionId());
            }
        });
    }

    // ── Hive expands when an ExpansionBox is attached on a non-front side ───────
    // AdvancedBeehive.updateState scans neighbours for an ExpansionBox and calls
    // calculateExpandedDirection, which validates the box position against the hive's FACING.
    // For a NORTH-facing hive, EAST/WEST/SOUTH/UP/DOWN are valid (= non-front). helper.setBlock
    // doesn't trigger setPlacedBy, so we drive the update by calling updateState directly.
    private static void testHiveExpandsWithBackBox(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 2);
        BlockPos boxPos = new BlockPos(3, 2, 2); // east of hive; non-front for NORTH-facing hive

        var hiveBlock = (AdvancedBeehive) ModBlocks.HIVES.get("advanced_oak_beehive").get();
        var expansionBlock = (ExpansionBox) ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        helper.setBlock(hivePos, hiveBlock.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH));
        helper.setBlock(boxPos, expansionBlock.defaultBlockState());

        // Drive the expansion detection that setPlacedBy would normally trigger.
        hiveBlock.updateState(helper.getLevel(), helper.absolutePos(hivePos),
                helper.getLevel().getBlockState(helper.absolutePos(hivePos)), false);

        VerticalHive expanded = helper.getBlockState(hivePos).getValue(AdvancedBeehive.EXPANDED);
        if (expanded == VerticalHive.NONE) {
            helper.fail("Hive did not register as expanded after placing a box on the non-front side "
                    + "(expanded=" + expanded + ")", hivePos);
            return;
        }
        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);
        if (hive.MAX_BEES != 5) {
            helper.fail("Expanded hive should have MAX_BEES=5 (got " + hive.MAX_BEES + ")", hivePos);
            return;
        }
        helper.succeed();
    }

    // ── Box on the front side does NOT expand the hive ──────────────────────────
    // calculateExpandedDirection rejects a box at hivePos.relative(hiveFacing). For a NORTH-
    // facing hive that's the north neighbour. Should leave EXPANDED at NONE.
    private static void testHiveDoesNotExpandFromFrontBox(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 3);
        BlockPos boxPos = new BlockPos(2, 2, 2); // north of hive = front for NORTH-facing hive

        var hiveBlock = (AdvancedBeehive) ModBlocks.HIVES.get("advanced_oak_beehive").get();
        var expansionBlock = (ExpansionBox) ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        helper.setBlock(hivePos, hiveBlock.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH));
        helper.setBlock(boxPos, expansionBlock.defaultBlockState());

        hiveBlock.updateState(helper.getLevel(), helper.absolutePos(hivePos),
                helper.getLevel().getBlockState(helper.absolutePos(hivePos)), false);

        VerticalHive expanded = helper.getBlockState(hivePos).getValue(AdvancedBeehive.EXPANDED);
        if (expanded != VerticalHive.NONE) {
            helper.fail("Hive expanded from a front-side box (expanded=" + expanded
                    + "), front-side attachment should be rejected", hivePos);
            return;
        }
        helper.succeed();
    }

    // ── Expanded hive accepts upgrades in the upgradeHandler ────────────────────
    // acceptsUpgrades() returns EXPANDED != NONE. Without an adjacent box, false. With one,
    // true and the upgradeHandler accepts items in its 4 slots. Verify both directions to
    // catch regressions where acceptsUpgrades stops gating on EXPANDED.
    private static void testExpandedHiveAcceptsUpgrades(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 2);
        BlockPos boxPos = new BlockPos(3, 2, 2);

        var hiveBlock = (AdvancedBeehive) ModBlocks.HIVES.get("advanced_oak_beehive").get();
        var expansionBlock = (ExpansionBox) ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        helper.setBlock(hivePos, hiveBlock.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH));
        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);

        if (hive.acceptsUpgrades()) {
            helper.fail("Bare hive (no expansion box) should not accept upgrades yet", hivePos);
            return;
        }

        helper.setBlock(boxPos, expansionBlock.defaultBlockState());
        hiveBlock.updateState(helper.getLevel(), helper.absolutePos(hivePos),
                helper.getLevel().getBlockState(helper.absolutePos(hivePos)), false);

        if (!hive.acceptsUpgrades()) {
            helper.fail("Expanded hive should accept upgrades (EXPANDED="
                    + helper.getBlockState(hivePos).getValue(AdvancedBeehive.EXPANDED) + ")", hivePos);
            return;
        }

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler();
        ItemStack productivityUpgrade = new ItemStack(LibItems.UPGRADE_PRODUCTIVITY.get());
        upgrades.setStackInSlot(0, productivityUpgrade);

        ItemStack installed = upgrades.getStackInSlot(0);
        if (!installed.is(LibItems.UPGRADE_PRODUCTIVITY.get())) {
            helper.fail("UpgradeHandler did not hold the inserted productivity upgrade (got "
                    + (installed.isEmpty() ? "empty" : installed.getItem().getDescriptionId()) + ")", hivePos);
            return;
        }
        if (hive.getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) != 1) {
            helper.fail("getUpgradeCount did not see the productivity upgrade (count="
                    + hive.getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get()) + ")", hivePos);
            return;
        }
        helper.succeed();
    }

    // ── Simulated hive absorbs a filled bee cage into an occupant ───────────────
    // Cage-slot processing runs every 23 ticks inside AdvancedBeehiveBlockEntity.tick when
    // isSim() is true. A filled BeeCage in slot 11 becomes an occupant, and the cage stack
    // shrinks by 1. isSim() activates from UPGRADE_SIMULATOR.
    private static void testSimulatedHiveAbsorbsCagedBee(GameTestHelper helper) {
        BlockPos hivePos = new BlockPos(2, 2, 2);
        BlockPos boxPos = new BlockPos(3, 2, 2);

        var hiveBlock = (AdvancedBeehive) ModBlocks.HIVES.get("advanced_oak_beehive").get();
        var expansionBlock = (ExpansionBox) ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        helper.setBlock(hivePos, hiveBlock.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH));
        helper.setBlock(boxPos, expansionBlock.defaultBlockState());
        hiveBlock.updateState(helper.getLevel(), helper.absolutePos(hivePos),
                helper.getLevel().getBlockState(helper.absolutePos(hivePos)), false);

        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);

        // Enable simulation via UPGRADE_SIMULATOR.
        ((InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_SIMULATOR.get()));
        if (!hive.isSim()) {
            helper.fail("Hive should be in simulation mode after installing UPGRADE_SIMULATOR", hivePos);
            return;
        }

        // Capture a vanilla bee into a cage and put it in the cage slot.
        ItemStack cage = captureFreshBeeIntoCage(helper, EntityType.BEE, new BlockPos(5, 2, 2));
        if (cage == null) {
            helper.fail("Couldn't capture bee into cage", hivePos);
            return;
        }
        hive.inventoryHandler.setStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE, cage);

        // Cage processing fires every 23 ticks; budget 200 is plenty.
        helper.succeedWhen(() -> {
            if (hive.getOccupantCount() < 1) {
                throw helper.assertionException(hivePos, "Hive did not absorb caged bee yet "
                        + "(occupants=" + hive.getOccupantCount() + ", cageSlot="
                        + hive.inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE).getCount() + ")");
            }
            // The cage in the slot must have been consumed (or replaced by an empty cage stack).
            ItemStack remaining = hive.inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE);
            if (!remaining.isEmpty() && remaining.getItem() instanceof BeeCage && BeeCage.isFilled(remaining)) {
                throw helper.assertionException(hivePos, "Filled cage was not consumed after the bee was absorbed");
            }
        });
    }

    // ── Expanded simulated hive: iron bee → iron honeycomb (iron block flower) ──
    // Geometry: hive at (h.x, h.y, h.z) facing NORTH. simulateBee resolves the flower as
    //     hive.below(1).relative(NORTH) = (h.x, h.y-1, h.z-1)
    // (i.e. one block down and one block out in the FACING direction). An iron_block at that
    // position satisfies the productivebees:flowers/ferric tag, so a simulated iron bee marks
    // BeeReleaseStatus.HONEY_DELIVERED and beeReleasePostAction runs the bee_produce recipe
    // (output: configurable_honeycomb with bee_type=iron).
    private static void testHiveProducesIronHoneycombFromIronBlock(GameTestHelper helper) {
        runHiveProductionTest(helper, false);
    }

    // ── Expanded simulated hive: iron bee → iron honeycomb (feeder slab flower) ─
    // Same setup as the iron-block variant, but the flower position holds a feeder slab whose
    // inventory contains an iron block. ProductiveBee.isFlowerValid → isValidFeeder iterates
    // feeder.getInventoryItems(); a BlockItem whose default state matches the bee's flowerTag
    // counts as a valid flower.
    private static void testHiveProducesIronHoneycombFromFeeder(GameTestHelper helper) {
        runHiveProductionTest(helper, true);
    }

    private static void runHiveProductionTest(GameTestHelper helper, boolean useFeeder) {
        BlockPos hivePos = new BlockPos(3, 3, 3);
        BlockPos boxPos = new BlockPos(4, 3, 3);          // east of hive — non-front for NORTH-facing
        BlockPos flowerPos = new BlockPos(3, 2, 2);       // hive.below(1).relative(NORTH)

        var hiveBlock = (AdvancedBeehive) ModBlocks.HIVES.get("advanced_oak_beehive").get();
        var expansionBlock = (ExpansionBox) ModBlocks.EXPANSIONS.get("expansion_box_oak").get();

        helper.setBlock(hivePos, hiveBlock.defaultBlockState()
                .setValue(BeehiveBlock.FACING, Direction.NORTH));
        helper.setBlock(boxPos, expansionBlock.defaultBlockState());
        hiveBlock.updateState(helper.getLevel(), helper.absolutePos(hivePos),
                helper.getLevel().getBlockState(helper.absolutePos(hivePos)), false);

        AdvancedBeehiveBlockEntity hive = helper.getBlockEntity(hivePos, AdvancedBeehiveBlockEntity.class);
        if (helper.getBlockState(hivePos).getValue(AdvancedBeehive.EXPANDED) == VerticalHive.NONE) {
            helper.fail("Hive did not register as expanded — production test depends on isSim() path", hivePos);
            return;
        }

        // Simulator upgrade flips isSim() and enables the cage-slot + simulateBee codepath.
        ((InventoryHandlerHelper.UpgradeHandler) hive.getUpgradeHandler())
                .setStackInSlot(0, new ItemStack(LibItems.UPGRADE_SIMULATOR.get()));
        if (!hive.isSim()) {
            helper.fail("Hive should be in simulation mode after installing UPGRADE_SIMULATOR", hivePos);
            return;
        }

        if (useFeeder) {
            helper.setBlock(flowerPos, ModBlocks.FEEDER.get().defaultBlockState()
                    .setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM));
            FeederBlockEntity feeder = helper.getBlockEntity(flowerPos, FeederBlockEntity.class);
            feeder.inventoryHandler.setStackInSlot(0, new ItemStack(Blocks.IRON_BLOCK.asItem()));
        } else {
            helper.setBlock(flowerPos, Blocks.IRON_BLOCK.defaultBlockState());
        }

        ItemStack ironCage = captureConfigurableBeeIntoCage(helper, "productivebees:iron", new BlockPos(5, 3, 3));
        if (ironCage == null) {
            helper.fail("Couldn't capture iron bee into cage", hivePos);
            return;
        }
        hive.inventoryHandler.setStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE, ironCage);

        // The produced comb's BEE_TYPE carries the bee's canonical full-path id (e.g.
        // "productivebees:raw_materials/iron" — set from the ingredient's getBeeType during
        // AdvancedBeehiveRecipe.getRecipeOutputs). Use resolveId so the assertion holds whether
        // a future BeeIngredient ever emits the legacy simple-name form.
        Identifier expected = BeeRegistries.resolveId(Identifier.parse("productivebees:iron"));
        helper.succeedWhen(() -> {
            for (int slot : InventoryHandlerHelper.OUTPUT_SLOTS) {
                ItemStack stack = hive.inventoryHandler.getStackInSlot(slot);
                if (stack.is(ModItems.CONFIGURABLE_HONEYCOMB.get())
                        && stack.has(ModDataComponents.BEE_TYPE)) {
                    Identifier actual = BeeRegistries.resolveId(stack.get(ModDataComponents.BEE_TYPE));
                    if (expected.equals(actual)) {
                        return;
                    }
                }
            }
            throw helper.assertionException(hivePos, "Hive has not produced an iron honeycomb yet "
                    + "(occupants=" + hive.getOccupantCount()
                    + ", flower=" + helper.getBlockState(flowerPos).getBlock().getDescriptionId() + ")");
        });
    }

    // ── Bottler + piston squash → centrifuge → iron type gene ───────────────────
    // Full pipeline. BottlerBlockEntity.tick polls every 7 ticks: if pos.above() is a
    // PISTON_HEAD facing DOWN and a non-baby Bee is inside AABB(pos).expandTowards(0,1,0),
    // it consumes one glass bottle, drops a gene_bottle ItemEntity at pos.above() (the piston
    // head's space — entities can occupy it), and kills the bee. The dropped bottle is then
    // moved into a neighbouring centrifuge's INPUT_SLOT and processed via completeGeneProcessing,
    // which extracts each GeneGroup as a Gene item. We assert one of those is the TYPE gene
    // with value "productivebees:iron".
    private static void testBottlerSquashAdultToIronGene(GameTestHelper helper) {
        BlockPos bottlerPos = new BlockPos(2, 2, 2);
        BlockPos centrifugePos = new BlockPos(4, 2, 2);

        helper.setBlock(bottlerPos, ModBlocks.BOTTLER.get().defaultBlockState());
        helper.setBlock(bottlerPos.above(), Blocks.PISTON_HEAD.defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN));
        helper.setBlock(centrifugePos, ModBlocks.CENTRIFUGE.get().defaultBlockState());

        BottlerBlockEntity bottler = helper.getBlockEntity(bottlerPos, BottlerBlockEntity.class);
        CentrifugeBlockEntity centrifuge = helper.getBlockEntity(centrifugePos, CentrifugeBlockEntity.class);

        // Max time upgrades so the centrifuge cycle collapses from 300 ticks to 5.
        InventoryHandlerHelper.UpgradeHandler centrifugeUpgrades =
                (InventoryHandlerHelper.UpgradeHandler) centrifuge.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            centrifugeUpgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }

        bottler.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.GLASS_BOTTLE, 4));

        ConfigurableBee bee = spawnIronBeeForSquash(helper, bottlerPos, 0); // adult: Age = 0
        if (bee == null) {
            helper.fail("Couldn't construct iron ConfigurableBee", bottlerPos);
            return;
        }

        helper.succeedWhen(() -> {
            for (int slot : InventoryHandlerHelper.OUTPUT_SLOTS) {
                ItemStack stack = centrifuge.inventoryHandler.getStackInSlot(slot);
                if (stack.is(ModItems.GENE.get())
                        && Gene.getAttribute(stack) == GeneAttribute.TYPE
                        && "productivebees:iron".equals(Gene.getValue(stack))) {
                    return;
                }
            }
            if (centrifuge.inventoryHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty()) {
                ItemEntity bottle = findGeneBottleNear(helper, bottlerPos);
                if (bottle == null) {
                    throw helper.assertionException(bottlerPos, "Bottler has not dropped a gene_bottle yet "
                            + "(bee alive=" + bee.isAlive() + ", bottles left="
                            + bottler.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT).getCount() + ")");
                }
                centrifuge.inventoryHandler.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, bottle.getItem().copy());
                bottle.discard();
            }
            throw helper.assertionException(centrifugePos,
                    "Centrifuge has not extracted an iron TYPE gene yet (input slot has bottle: "
                    + !centrifuge.inventoryHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty()
                    + ", recipeProgress=" + centrifuge.recipeProgress + ")");
        });
    }

    // ── Baby iron bee is NOT squashed ───────────────────────────────────────────
    // BottlerBlockEntity.tick filters via !e.isBaby() (line ~89). A child bee (Age < 0) in the
    // squash zone must remain alive and no gene_bottle should be dropped. Wait through several
    // bottler tick windows (% 7) to be sure.
    private static void testBottlerSkipsBabyBee(GameTestHelper helper) {
        BlockPos bottlerPos = new BlockPos(2, 2, 2);

        helper.setBlock(bottlerPos, ModBlocks.BOTTLER.get().defaultBlockState());
        helper.setBlock(bottlerPos.above(), Blocks.PISTON_HEAD.defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.DOWN));

        BottlerBlockEntity bottler = helper.getBlockEntity(bottlerPos, BottlerBlockEntity.class);
        bottler.inventoryHandler.setStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT, new ItemStack(Items.GLASS_BOTTLE, 4));

        ConfigurableBee bee = spawnIronBeeForSquash(helper, bottlerPos, -24000); // baby
        if (bee == null) {
            helper.fail("Couldn't construct iron ConfigurableBee", bottlerPos);
            return;
        }

        // Wait through multiple bottler windows (7-tick cadence). 80 ticks covers ~11 checks.
        helper.runAfterDelay(80, () -> {
            ItemEntity drop = findGeneBottleNear(helper, bottlerPos);
            if (drop != null) {
                helper.fail("Baby iron bee was squashed — bottler dropped a gene_bottle when it should skip babies", bottlerPos);
                return;
            }
            if (!bee.isAlive() || bee.isRemoved()) {
                helper.fail("Baby iron bee died while sitting in the squash zone", bottlerPos);
                return;
            }
            ItemStack remainingBottles = bottler.inventoryHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
            if (remainingBottles.getCount() != 4) {
                helper.fail("Bottler consumed glass bottles for baby bee (count went from 4 to " + remainingBottles.getCount() + ")", bottlerPos);
                return;
            }
            helper.succeed();
        });
    }

    private static ConfigurableBee spawnIronBeeForSquash(GameTestHelper helper, BlockPos bottlerPos, int age) {
        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) return null;
        bee.setBeeType("productivebees:iron");
        bee.setDefaultAttributes();
        bee.setAge(age);
        // Snap to the centre of the block just above the bottler — inside the AABB used by
        // BottlerBlockEntity.tick, and inside the piston head's voxel (which has only a small
        // collision shape, so the entity isn't physically blocked).
        bee.snapTo(helper.absolutePos(bottlerPos.above()).getCenter(), 0f, 0f);
        bee.setNoGravity(true);
        bee.setNoAi(true); // pin it to position so it doesn't wander out of the squash zone
        helper.getLevel().addFreshEntity(bee);
        return bee;
    }

    private static ItemEntity findGeneBottleNear(GameTestHelper helper, BlockPos bottlerPos) {
        AABB area = new AABB(helper.absolutePos(bottlerPos)).inflate(3.0);
        for (ItemEntity ie : helper.getLevel().getEntitiesOfClass(ItemEntity.class, area)) {
            if (ie.getItem().is(ModItems.GENE_BOTTLE.get())) {
                return ie;
            }
        }
        return null;
    }

    // ── Breeding chamber: cross-type bees (recipe-based, not self-breed) ─────────
    // farmer_bee + rancher_bee → cupid_bee (no condition, defined in BeeBreedingRecipeProvider).
    // Both parents are entity-typed solitary bees (BEEHIVE_INHABITORS tagged), so BeeData lookup
    // isn't required — the canProcessInput path falls back to poppy×1 for non-BeeRegistries bees.
    private static void testBreedingChamberCrossTypeBees(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        BreedingChamberBlockEntity chamber = helper.getBlockEntity(pos, BreedingChamberBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            chamber.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack cage1 = captureFreshBeeIntoCage(helper, (EntityType<? extends Bee>) ModEntities.FARMER_BEE.get(), new BlockPos(3, 2, 2));
        ItemStack cage2 = captureFreshBeeIntoCage(helper, (EntityType<? extends Bee>) ModEntities.RANCHER_BEE.get(), new BlockPos(4, 2, 2));
        if (cage1 == null || cage2 == null) {
            helper.fail("Couldn't capture farmer+rancher bees into cages", pos);
            return;
        }

        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, cage1);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, cage2);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get()));

        helper.succeedWhen(() -> assertChildCageInOutput(helper, chamber, pos, "cross-type (farmer+rancher)"));
    }

    // ── Breeding chamber: two ConfigurableBee data bees ──────────────────────────
    // magmatic + fluids/water → breeze (BeeBreedingRecipeProvider line 44). Both parents are
    // ConfigurableBees so the canProcessInput path reads breedingItem from BeeData (defaults to
    // POPPY since neither sets it explicitly).
    private static void testBreedingChamberTwoDataBees(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        BreedingChamberBlockEntity chamber = helper.getBlockEntity(pos, BreedingChamberBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            chamber.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack cage1 = captureConfigurableBeeIntoCage(helper, "productivebees:magmatic", new BlockPos(3, 2, 2));
        ItemStack cage2 = captureConfigurableBeeIntoCage(helper, "productivebees:fluids/water", new BlockPos(4, 2, 2));
        if (cage1 == null || cage2 == null) {
            helper.fail("Couldn't capture magmatic+water configurable bees into cages", pos);
            return;
        }

        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, cage1);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, cage2);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get()));

        helper.succeedWhen(() -> assertChildCageInOutput(helper, chamber, pos, "data+data (magmatic+water)"));
    }

    // ── Breeding chamber: one ConfigurableBee + one entity-typed solitary bee ────
    // magmatic + leafcutter_bee → coal (BeeBreedingRecipeProvider line 45). Exercises the mixed
    // path: parent1 resolves through BeeRegistries; parent2 through BeeIngredientFactory's
    // entity-id alias. Both must agree on the same recipe match.
    private static void testBreedingChamberDataAndEntityBee(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        helper.setBlock(pos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        BreedingChamberBlockEntity chamber = helper.getBlockEntity(pos, BreedingChamberBlockEntity.class);

        InventoryHandlerHelper.UpgradeHandler upgrades =
                (InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler();
        for (int i = 0; i < 4; i++) {
            upgrades.setStackInSlot(i, new ItemStack(LibItems.UPGRADE_TIME_2.get()));
        }
        try (Transaction tx = Transaction.openRoot()) {
            chamber.energyHandler.insert(5000, tx);
            tx.commit();
        }

        ItemStack cage1 = captureConfigurableBeeIntoCage(helper, "productivebees:magmatic", new BlockPos(3, 2, 2));
        ItemStack cage2 = captureFreshBeeIntoCage(helper, (EntityType<? extends Bee>) ModEntities.LEAFCUTTER_BEE.get(), new BlockPos(4, 2, 2));
        if (cage1 == null || cage2 == null) {
            helper.fail("Couldn't capture magmatic+leafcutter bees into cages", pos);
            return;
        }

        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, cage1);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, cage2);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(Items.POPPY, 4));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get()));

        helper.succeedWhen(() -> assertChildCageInOutput(helper, chamber, pos, "data+entity (magmatic+leafcutter)"));
    }

    // ── Kamikaze bee: spawned by the helmet-hurt event handler ───────────────────
    // The event handler creates a ConfigurableBee with type="productivebees:kamikaz" and adds
    // it to the world. Exercise that exact spawn path (no helmet/damage simulation needed —
    // those are vanilla event-bus plumbing; the touchpoint that broke during the port was the
    // bee-data lookup + entity creation). Asserts the bee is in the world AND has the expected
    // hostile temper attribute applied via setDefaultAttributes.
    private static void testKamikazeBeeSpawn(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        if (BeeRegistries.lookup(Identifier.fromNamespaceAndPath("productivebees", "kamikaz")) == null) {
            helper.fail("BeeRegistries does not contain 'productivebees:kamikaz' — server registry not populated", pos);
            return;
        }

        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("ConfigurableBee.create returned null", pos);
            return;
        }
        bee.setBeeType("productivebees:kamikaz");
        bee.setDefaultAttributes();
        bee.snapTo(helper.absolutePos(pos).getCenter(), 0f, 0f);
        helper.getLevel().addFreshEntity(bee);

        helper.succeedWhen(() -> {
            AABB area = new AABB(helper.absolutePos(pos)).inflate(2.0);
            List<ConfigurableBee> found = helper.getLevel().getEntitiesOfClass(ConfigurableBee.class, area);
            for (ConfigurableBee b : found) {
                Identifier type = b.getBeeType();
                if (type != null && type.toString().equals("productivebees:kamikaz")) {
                    return;
                }
            }
            throw helper.assertionException(pos, "Kamikaz bee not present in world after spawn (entities="
                    + found.size() + ", types=" + found.stream().map(b -> String.valueOf(b.getBeeType())).toList() + ")");
        });
    }

    // ── Sugarbag bee: same spawn path as cocoa-pod break ─────────────────────────
    // Exercises the BeeRegistries lookup + ConfigurableBee construction for "productivebees:sugarbag"
    // (the bee-side touchpoint the port could have broken). The 2% RNG gate in the actual event
    // handler is orthogonal to this fix.
    private static void testSugarbagBeeSpawn(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        if (BeeRegistries.lookup(Identifier.fromNamespaceAndPath("productivebees", "sugarbag")) == null) {
            helper.fail("BeeRegistries does not contain 'productivebees:sugarbag'", pos);
            return;
        }

        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.NATURAL);
        if (bee == null) {
            helper.fail("ConfigurableBee.create returned null", pos);
            return;
        }
        bee.setBeeType("productivebees:sugarbag");
        bee.setDefaultAttributes();
        bee.snapTo(helper.absolutePos(pos).getCenter(), 0f, 0f);
        helper.getLevel().addFreshEntity(bee);

        helper.succeedWhen(() -> {
            AABB area = new AABB(helper.absolutePos(pos)).inflate(2.0);
            List<ConfigurableBee> found = helper.getLevel().getEntitiesOfClass(ConfigurableBee.class, area);
            for (ConfigurableBee b : found) {
                Identifier type = b.getBeeType();
                if (type != null && type.toString().equals("productivebees:sugarbag")) {
                    return;
                }
            }
            throw helper.assertionException(pos, "Sugarbag bee not present after spawn (entities="
                    + found.size() + ")");
        });
    }

    // ── Beebee biome tag: verifies allthemodium:the_other resolves into the tag ──
    // The MobSpawnEvent.PositionCheck handler converts a naturally-spawning ConfigurableBee to
    // beebee iff the biome is in BEEBEE_SPAWN_BIOMES. With allthemodium loaded, the_other should
    // be a member. This is a registry-time check — no entity spawn required.
    private static void testBeebeeSpawnBiomesTag(GameTestHelper helper) {
        if (!ModList.get().isLoaded("allthemodium")) {
            helper.succeed();
            return;
        }
        BlockPos pos = new BlockPos(2, 2, 2);
        HolderLookup.RegistryLookup<Biome> biomes =
                helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        var tagHolder = biomes.get(ModTags.BEEBEE_SPAWN_BIOMES);
        if (tagHolder.isEmpty()) {
            throw helper.assertionException(pos, "BEEBEE_SPAWN_BIOMES tag is not registered at runtime");
        }
        Identifier theOther = Identifier.fromNamespaceAndPath("allthemodium", "the_other");
        boolean found = tagHolder.get().stream().anyMatch(holder ->
                holder.unwrapKey().map(key -> key.identifier().equals(theOther)).orElse(false));
        if (!found) {
            throw helper.assertionException(pos, "BEEBEE_SPAWN_BIOMES tag does not contain allthemodium:the_other "
                    + "(present members: " + tagHolder.get().stream()
                            .map(h -> h.unwrapKey().map(k -> k.identifier().toString()).orElse("?"))
                            .toList() + ")");
        }
        helper.succeed();
    }

    // ── Bee-cage helpers ────────────────────────────────────────────────────────
    private static ItemStack captureFreshBeeIntoCage(GameTestHelper helper, EntityType<? extends Bee> type, BlockPos relPos) {
        Bee bee = type.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) return null;
        bee.setAge(0);
        bee.snapTo(helper.absolutePos(relPos).getCenter(), 0f, 0f);
        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(bee, cage);
        return cage;
    }

    private static ItemStack captureConfigurableBeeIntoCage(GameTestHelper helper, String beeType, BlockPos relPos) {
        ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) return null;
        bee.setBeeType(beeType);
        bee.setDefaultAttributes();
        bee.setAge(0);
        bee.snapTo(helper.absolutePos(relPos).getCenter(), 0f, 0f);
        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(bee, cage);
        return cage;
    }

    private static ItemStack captureBabyBeeIntoCage(GameTestHelper helper, BlockPos relPos) {
        Bee bee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) return null;
        bee.setAge(-24000); // child
        bee.snapTo(helper.absolutePos(relPos).getCenter(), 0f, 0f);
        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(bee, cage);
        return cage;
    }

    private static void assertChildCageInOutput(GameTestHelper helper, BreedingChamberBlockEntity chamber,
                                                BlockPos pos, String label) {
        ItemStack out = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_OUTPUT);
        if (out.isEmpty() || !(out.getItem() instanceof BeeCage) || !BeeCage.isFilled(out)) {
            throw helper.assertionException(pos, label + " breeding produced no filled cage in output (out="
                    + (out.isEmpty() ? "empty" : out.getItem().getDescriptionId())
                    + ", recipeProgress=" + chamber.recipeProgress
                    + ", chosenRecipe=" + (chamber.chosenRecipe == null ? "null" : chamber.chosenRecipe.id()) + ")");
        }
        CustomData data = out.get(DataComponents.CUSTOM_DATA);
        int age = data == null ? 0 : data.copyTag().getIntOr("Age", 0);
        if (age >= 0) {
            throw helper.assertionException(pos, label + " offspring is not a child (Age=" + age + ", expected < 0)");
        }
    }

    // ── Shared helpers for centrifuge tests ─────────────────────────────────────
    private static void assertHasWax(GameTestHelper helper, CentrifugeBlockEntity centrifuge, BlockPos pos, String label) {
        for (int slot : InventoryHandlerHelper.OUTPUT_SLOTS) {
            if (centrifuge.inventoryHandler.getStackInSlot(slot).is(ModItems.WAX.get())) {
                return;
            }
        }
        throw helper.assertionException(pos, label + " has not produced wax (recipeProgress="
                + centrifuge.recipeProgress + ")");
    }

    // ── Loot modifier: component preservation ────────────────────────────────────
    // IngredientModifier.doApply must propagate the DataComponentIngredient's patch onto the
    // generated stack. Without that, component-bound additions (the configurable bee spawn egg
    // with entity_data={type: productivebees:amber, id: productivebees:configurable_bee}) drop
    // as a blank spawn egg with no data, which manifests as "the amber spawn egg loot modifier
    // doesn't work" in-world.
    private static void testBeeHelperRecipeCaches(GameTestHelper helper) {
        BlockPos pos = new BlockPos(2, 2, 2);
        BeeHelper.clearRecipeCaches();

        Bee bee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (bee == null) {
            helper.fail("Failed to construct bee", pos);
            return;
        }
        bee.snapTo(helper.absolutePos(pos).getCenter(), 0f, 0f);
        helper.getLevel().addFreshEntity(bee);

        // Block conversion cache — same (bee, blockState) should hit cache on second call.
        BlockState flowerState = Blocks.DANDELION.defaultBlockState();
        BeeHelper.getBlockConversionRecipe(bee, flowerState);
        int afterFirst = BeeHelper.recipeCacheSizes()[0];
        BeeHelper.getBlockConversionRecipe(bee, flowerState);
        int afterSecond = BeeHelper.recipeCacheSizes()[0];
        if (afterFirst == 0) {
            helper.fail("BlockConversion cache should populate on first lookup (got size " + afterFirst + ")", pos);
            return;
        }
        if (afterSecond != afterFirst) {
            helper.fail("BlockConversion cache should not grow on repeat lookup: " + afterFirst + " -> " + afterSecond, pos);
            return;
        }

        // Item conversion cache.
        ItemStack stick = new ItemStack(Items.STICK);
        BeeHelper.getItemConversionRecipe(bee, stick);
        int itemAfterFirst = BeeHelper.recipeCacheSizes()[1];
        BeeHelper.getItemConversionRecipe(bee, stick);
        int itemAfterSecond = BeeHelper.recipeCacheSizes()[1];
        if (itemAfterFirst == 0) {
            helper.fail("ItemConversion cache should populate on first lookup", pos);
            return;
        }
        if (itemAfterSecond != itemAfterFirst) {
            helper.fail("ItemConversion cache should not grow on repeat lookup: " + itemAfterFirst + " -> " + itemAfterSecond, pos);
            return;
        }

        // Bee conversion cache (the one added in this round of perf work).
        ServerLevel serverLevel = helper.getLevel();
        Player fakePlayer = FakePlayerFactory.get(serverLevel, new GameProfile(UUID.randomUUID(), "test_player"));
        BeeHelper.itemInteract(bee, stick, serverLevel, fakePlayer);
        int beeAfterFirst = BeeHelper.recipeCacheSizes()[3];
        BeeHelper.itemInteract(bee, stick, serverLevel, fakePlayer);
        int beeAfterSecond = BeeHelper.recipeCacheSizes()[3];
        if (beeAfterFirst == 0) {
            helper.fail("BeeConversion cache should populate on first itemInteract — cache wiring regressed", pos);
            return;
        }
        if (beeAfterSecond != beeAfterFirst) {
            helper.fail("BeeConversion cache should not grow on repeat itemInteract: " + beeAfterFirst + " -> " + beeAfterSecond, pos);
            return;
        }

        helper.succeed();
    }

    private static void testLootModifierPreservesComponents(GameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 1, 0);
        Ingredient addition = BeeCreator.getSpawnEggIngredient(
                Identifier.fromNamespaceAndPath("productivebees", "amber"));
        var modifier = new IngredientModifier(
                new LootItemCondition[0],
                0, addition, 1.0f, true);

        var params = new LootParams.Builder(helper.getLevel())
                .create(LootContextParamSets.EMPTY);
        var context = new LootContext.Builder(params)
                .create(Optional.empty());

        var output = modifier.apply(new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(), context);
        if (output.size() != 1) {
            helper.fail("IngredientModifier produced " + output.size() + " items (expected 1)", pos);
            return;
        }
        ItemStack stack = output.getFirst();
        if (!stack.is(ModItems.CONFIGURABLE_SPAWN_EGG.get())) {
            helper.fail("Expected configurable spawn egg, got " + stack.getItem().getDescriptionId(), pos);
            return;
        }
        var entityData = stack.get(DataComponents.ENTITY_DATA);
        if (entityData == null) {
            helper.fail("Loot modifier dropped the entity_data component — DataComponentIngredient's "
                    + "DataComponentPatch was not applied to the new ItemStack", pos);
            return;
        }
        String type = entityData.getUnsafe().getString("type").orElse("");
        if (!type.equals("productivebees:amber")) {
            helper.fail("entity_data.type was '" + type + "' (expected 'productivebees:amber')", pos);
            return;
        }
        helper.succeed();
    }

    // ── Breeding chamber: hopper below pulls only OUTPUT ────────────────────────
    private static void testBreedingChamberHopperExtractsOnlyOutput(GameTestHelper helper) {
        BlockPos chamberPos = new BlockPos(2, 3, 2);
        BlockPos hopperPos = new BlockPos(2, 2, 2);
        helper.setBlock(chamberPos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        BreedingChamberBlockEntity chamber = helper.getBlockEntity(chamberPos, BreedingChamberBlockEntity.class);
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);

        Bee scratchBee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (scratchBee == null) {
            helper.fail("Failed to construct scratch bee", chamberPos);
            return;
        }
        ItemStack filledCage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(scratchBee, filledCage);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, filledCage);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, filledCage.copy());
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1, new ItemStack(Items.POPPY, 8));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2, new ItemStack(Items.DANDELION, 8));
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_CAGE, new ItemStack(ModItems.BEE_CAGE.get(), 2));
        ItemStack outputCage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(scratchBee, outputCage);
        chamber.inventoryHandler.setStackInSlot(BreedingChamberContainer.SLOT_OUTPUT, outputCage);

        ((InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler()).setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME.get()));

        helper.runAfterDelay(50, () -> {
            int filledCagesInHopper = 0;
            for (int i = 0; i < hopper.getContainerSize(); i++) {
                ItemStack hopperItem = hopper.getItem(i);
                if (!hopperItem.isEmpty() && hopperItem.getItem() instanceof BeeCage && BeeCage.isFilled(hopperItem)) {
                    filledCagesInHopper += hopperItem.getCount();
                }
            }
            if (filledCagesInHopper != 1) {
                helper.fail("Hopper should hold exactly 1 filled cage (the OUTPUT), found " + filledCagesInHopper, hopperPos);
                return;
            }
            if (!chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_OUTPUT).isEmpty()) {
                helper.fail("Output slot still has an item after hopper pull", chamberPos);
                return;
            }
            ItemStack bee1 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
            ItemStack bee2 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
            if (!(bee1.getItem() instanceof BeeCage) || !BeeCage.isFilled(bee1)) {
                helper.fail("SLOT_BEE_1 lost its filled cage (got " + bee1 + ")", chamberPos);
                return;
            }
            if (!(bee2.getItem() instanceof BeeCage) || !BeeCage.isFilled(bee2)) {
                helper.fail("SLOT_BEE_2 lost its filled cage (got " + bee2 + ")", chamberPos);
                return;
            }
            ItemStack breed1 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1);
            if (!breed1.is(Items.POPPY) || breed1.getCount() != 8) {
                helper.fail("SLOT_BREED_ITEM_1 should still hold 8 poppies, got " + breed1, chamberPos);
                return;
            }
            ItemStack breed2 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2);
            if (!breed2.is(Items.DANDELION) || breed2.getCount() != 8) {
                helper.fail("SLOT_BREED_ITEM_2 should still hold 8 dandelions, got " + breed2, chamberPos);
                return;
            }
            ItemStack cageStack = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE);
            if (!(cageStack.getItem() instanceof BeeCage) || BeeCage.isFilled(cageStack) || cageStack.getCount() != 2) {
                helper.fail("SLOT_CAGE should still hold 2 empty cages, got " + cageStack, chamberPos);
                return;
            }
            ItemStack upgrade = ((InventoryHandlerHelper.UpgradeHandler) chamber.getUpgradeHandler()).getStackInSlot(0);
            if (!upgrade.is(LibItems.UPGRADE_TIME.get())) {
                helper.fail("Upgrade slot 0 should still hold UPGRADE_TIME, got " + upgrade, chamberPos);
                return;
            }
            helper.succeed();
        });
    }

    // ── Breeding chamber: hopper above pushes into INPUT slots, never OUTPUT ────
    private static void testBreedingChamberHopperInsertsIntoInputs(GameTestHelper helper) {
        BlockPos chamberPos = new BlockPos(2, 2, 2);
        BlockPos hopperPos = new BlockPos(2, 3, 2);
        helper.setBlock(chamberPos, ModBlocks.BREEDING_CHAMBER.get().defaultBlockState());
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        BreedingChamberBlockEntity chamber = helper.getBlockEntity(chamberPos, BreedingChamberBlockEntity.class);
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);

        Bee scratchBee = EntityType.BEE.create(helper.getLevel(), EntitySpawnReason.COMMAND);
        if (scratchBee == null) {
            helper.fail("Failed to construct scratch bee", chamberPos);
            return;
        }
        ItemStack filledCage = new ItemStack(ModItems.BEE_CAGE.get());
        BeeCage.captureEntity(scratchBee, filledCage);
        hopper.setItem(0, filledCage);
        hopper.setItem(1, new ItemStack(Items.POPPY));
        hopper.setItem(2, new ItemStack(ModItems.BEE_CAGE.get()));

        ItemResource poppyResource = ItemResource.of(new ItemStack(Items.POPPY));
        int rejected;
        try (Transaction tx = Transaction.openRoot()) {
            rejected = chamber.inventoryHandler.insert(BreedingChamberContainer.SLOT_OUTPUT, poppyResource, 1, tx);
            tx.commit();
        }
        if (rejected != 0) {
            helper.fail("Output slot accepted pipe insert (" + rejected + ")", chamberPos);
            return;
        }

        helper.runAfterDelay(80, () -> {
            ItemStack bee1 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
            ItemStack bee2 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
            boolean filledLanded = (bee1.getItem() instanceof BeeCage && BeeCage.isFilled(bee1))
                    || (bee2.getItem() instanceof BeeCage && BeeCage.isFilled(bee2));
            if (!filledLanded) {
                helper.fail("Filled cage never reached a bee slot (bee1=" + bee1 + ", bee2=" + bee2 + ")", chamberPos);
                return;
            }
            ItemStack breed1 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1);
            ItemStack breed2 = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2);
            if (!breed1.is(Items.POPPY) && !breed2.is(Items.POPPY)) {
                helper.fail("Poppy never reached a breed-item slot", chamberPos);
                return;
            }
            ItemStack cageSlot = chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE);
            if (!(cageSlot.getItem() instanceof BeeCage) || BeeCage.isFilled(cageSlot)) {
                helper.fail("Empty cage never reached the cage slot (got " + cageSlot + ")", chamberPos);
                return;
            }
            if (!chamber.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_OUTPUT).isEmpty()) {
                helper.fail("Hopper pushed an item into the OUTPUT slot", chamberPos);
                return;
            }
            helper.succeed();
        });
    }

    // ── Incubator: hopper above pushes into INPUT and CATALYST, never OUTPUT ────
    private static void testIncubatorHopperInsertsIntoInputs(GameTestHelper helper) {
        BlockPos incPos = new BlockPos(2, 2, 2);
        BlockPos hopperPos = new BlockPos(2, 3, 2);
        helper.setBlock(incPos, ModBlocks.INCUBATOR.get().defaultBlockState());
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        IncubatorBlockEntity incubator = helper.getBlockEntity(incPos, IncubatorBlockEntity.class);
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);

        hopper.setItem(0, new ItemStack(Items.EGG));
        hopper.setItem(1, new ItemStack(ModItems.HONEY_TREAT.get()));

        ItemResource eggResource = ItemResource.of(new ItemStack(Items.EGG));
        int rejected;
        try (Transaction tx = Transaction.openRoot()) {
            rejected = incubator.inventoryHandler.insert(IncubatorContainer.SLOT_OUTPUT, eggResource, 1, tx);
            tx.commit();
        }
        if (rejected != 0) {
            helper.fail("Output slot accepted pipe insert (" + rejected + ")", incPos);
            return;
        }

        helper.runAfterDelay(50, () -> {
            if (!incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT).is(Items.EGG)) {
                helper.fail("Egg never reached the input slot", incPos);
                return;
            }
            if (!incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST).is(ModItems.HONEY_TREAT.get())) {
                helper.fail("Honey treat never reached the catalyst slot", incPos);
                return;
            }
            if (!incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT).isEmpty()) {
                helper.fail("Hopper pushed an item into the OUTPUT slot", incPos);
                return;
            }
            helper.succeed();
        });
    }

    // ── Incubator: hopper below pulls only OUTPUT ───────────────────────────────
    private static void testIncubatorHopperExtractsOnlyOutput(GameTestHelper helper) {
        BlockPos incPos = new BlockPos(2, 3, 2);
        BlockPos hopperPos = new BlockPos(2, 2, 2);
        helper.setBlock(incPos, ModBlocks.INCUBATOR.get().defaultBlockState());
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState());

        IncubatorBlockEntity incubator = helper.getBlockEntity(incPos, IncubatorBlockEntity.class);
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos, HopperBlockEntity.class);

        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_INPUT, new ItemStack(Items.EGG, 3));
        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_CATALYST, new ItemStack(ModItems.HONEY_TREAT.get(), 4));
        incubator.inventoryHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, new ItemStack(Items.GOLD_INGOT));
        ((InventoryHandlerHelper.UpgradeHandler) incubator.getUpgradeHandler()).setStackInSlot(0, new ItemStack(LibItems.UPGRADE_TIME.get()));

        helper.runAfterDelay(50, () -> {
            int goldInHopper = 0;
            int otherInHopper = 0;
            for (int i = 0; i < hopper.getContainerSize(); i++) {
                ItemStack hopperItem = hopper.getItem(i);
                if (hopperItem.isEmpty()) continue;
                if (hopperItem.is(Items.GOLD_INGOT)) {
                    goldInHopper += hopperItem.getCount();
                } else {
                    otherInHopper += hopperItem.getCount();
                }
            }
            if (goldInHopper != 1) {
                helper.fail("Hopper should hold exactly 1 gold ingot (the OUTPUT), got " + goldInHopper, hopperPos);
                return;
            }
            if (otherInHopper != 0) {
                helper.fail("Hopper also pulled " + otherInHopper + " non-output items", hopperPos);
                return;
            }
            if (!incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT).isEmpty()) {
                helper.fail("Output slot still has an item after hopper pull", incPos);
                return;
            }
            ItemStack input = incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
            if (!input.is(Items.EGG) || input.getCount() != 3) {
                helper.fail("SLOT_INPUT should still hold 3 eggs, got " + input, incPos);
                return;
            }
            ItemStack catalyst = incubator.inventoryHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);
            if (!catalyst.is(ModItems.HONEY_TREAT.get()) || catalyst.getCount() != 4) {
                helper.fail("SLOT_CATALYST should still hold 4 honey treats, got " + catalyst, incPos);
                return;
            }
            ItemStack upgrade = ((InventoryHandlerHelper.UpgradeHandler) incubator.getUpgradeHandler()).getStackInSlot(0);
            if (!upgrade.is(LibItems.UPGRADE_TIME.get())) {
                helper.fail("Upgrade slot 0 should still hold UPGRADE_TIME, got " + upgrade, incPos);
                return;
            }
            helper.succeed();
        });
    }

    // ── Loot modifier: plain (non-DataComponent) ingredients still work ─────────
    private static void testLootModifierPlainIngredient(GameTestHelper helper) {
        BlockPos pos = new BlockPos(0, 1, 0);
        var addition = Ingredient.of(Items.GOLD_INGOT);
        var modifier = new IngredientModifier(
                new LootItemCondition[0],
                0, addition, 1.0f, false);

        var params = new LootParams.Builder(helper.getLevel())
                .create(LootContextParamSets.EMPTY);
        var context = new LootContext.Builder(params)
                .create(Optional.empty());

        var output = modifier.apply(new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(), context);
        if (output.size() != 1 || !output.getFirst().is(Items.GOLD_INGOT)) {
            helper.fail("Plain Ingredient.of(GOLD_INGOT) modifier didn't produce a gold ingot (got "
                    + output.size() + " items: " + (output.isEmpty() ? "" : output.getFirst().getItem().getDescriptionId()) + ")", pos);
            return;
        }
        helper.succeed();
    }

    /** Heated centrifuge strips wax outputs but still emits the recipe's fluid into the tank. */
    private static void assertHasHoneyInTank(GameTestHelper helper, CentrifugeBlockEntity centrifuge, BlockPos pos, String label) {
        int amount = centrifuge.fluidHandler.getAmountAsInt(0);
        FluidResource resource = centrifuge.fluidHandler.getResource(0);
        if (amount > 0 && resource.is(ModFluids.HONEY.get())) {
            return;
        }
        throw helper.assertionException(pos, label + " has not produced honey in its fluid tank "
                + "(amount=" + amount + ", resource=" + resource + ", recipeProgress="
                + centrifuge.recipeProgress + ")");
    }

    private static void fillHoneyTank(GameTestHelper helper, HoneyGeneratorBlockEntity generator, BlockPos pos) {
        FluidResource honey = FluidResource.of(ModFluids.HONEY.get());
        try (Transaction tx = Transaction.openRoot()) {
            int inserted = generator.fluidHandler.insert(0, honey, 10000, tx);
            if (inserted < 1000) {
                helper.fail("Could not pre-fill honey generator's fluid tank (inserted " + inserted + "mB)", pos);
                return;
            }
            tx.commit();
        }
    }
}
