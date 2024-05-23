package cy.jdkdigital.productivebees;

import com.google.common.collect.ImmutableList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ProductiveBeesConfig
{
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec CLIENT_CONFIG;
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final ModConfigSpec SERVER_CONFIG;
    public static final General GENERAL = new General(SERVER_BUILDER);
    public static final Bees BEES = new Bees(SERVER_BUILDER);
    public static final BeeAttributes BEE_ATTRIBUTES = new BeeAttributes(SERVER_BUILDER);
    public static final WorldGen WORLD_GEN = new WorldGen(SERVER_BUILDER);
    public static final Upgrades UPGRADES = new Upgrades(SERVER_BUILDER);

    static {
        CLIENT_CONFIG = CLIENT_BUILDER.build();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static class Client
    {
        public final ModConfigSpec.BooleanValue renderCombsInCentrifuge;
        public final ModConfigSpec.BooleanValue renderBeesInJars;
        public final ModConfigSpec.BooleanValue mutedBeeNestHelmet;
        public final ModConfigSpec.BooleanValue alwaysChristmas;
        public final ModConfigSpec.BooleanValue renderEntitiesInAmber;

        public Client(ModConfigSpec.Builder builder) {
            builder.push("Client");

            renderCombsInCentrifuge = builder
                    .comment("Render centrifuge comb inventory on the block.")
                    .define("renderCombsInCentrifuge", true);

            renderBeesInJars = builder
                    .comment("Render bees inside bee jars.")
                    .define("renderBeesInJars", true);

            mutedBeeNestHelmet = builder
                    .comment("Stop bee nest helmets from making sounds.")
                    .define("mutedBeeNestHelmet", false);

            alwaysChristmas = builder
                    .comment("Bees wear santa hats.")
                    .define("alwaysChristmas", false);

            renderEntitiesInAmber = builder
                    .comment("Render entities inside amber blocks.")
                    .define("renderEntitiesInAmber", true);

            builder.pop();
        }
    }

    public static class General
    {
        public final ModConfigSpec.IntValue hiveTickRate;
        public final ModConfigSpec.IntValue timeInHive;
        public final ModConfigSpec.IntValue centrifugeProcessingTime;
        public final ModConfigSpec.IntValue centrifugePoweredProcessingTime;
        public final ModConfigSpec.IntValue centrifugePowerUse;
        public final ModConfigSpec.IntValue incubatorProcessingTime;
        public final ModConfigSpec.IntValue incubatorPowerUse;
        public final ModConfigSpec.IntValue incubatorTreatUse;
        public final ModConfigSpec.IntValue breedingChamberProcessingTime;
        public final ModConfigSpec.IntValue breedingChamberPowerUse;
        public final ModConfigSpec.IntValue generatorPowerGen;
        public final ModConfigSpec.IntValue generatorHoneyUse;
        public final ModConfigSpec.ConfigValue<List<? extends String>> preferredTagSource;
        public final ModConfigSpec.IntValue numberOfBeesPerBomb;
        public final ModConfigSpec.IntValue nestLocatorDistance;
        public final ModConfigSpec.IntValue nestSpawnCooldown;
        public final ModConfigSpec.BooleanValue centrifugeHopperMode;
        public final ModConfigSpec.BooleanValue stripForgeCaps;
        public final ModConfigSpec.BooleanValue forceEnableFarmerBeeRightClickHarvest;

        public General(ModConfigSpec.Builder builder) {
            builder.push("General");

            hiveTickRate = builder
                    .comment("How often a hive should attempt special events like spawning undead bees. Default 500.")
                    .defineInRange("hiveTickRate", 1500, 20, Integer.MAX_VALUE);

            timeInHive = builder
                    .comment("How long time a bee should stay in the hive when having delivered honey. Default 4800.")
                    .defineInRange("timeInHive", 4800, 20, Integer.MAX_VALUE);

            centrifugeProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the centrifuge. Default 300.")
                    .defineInRange("centrifugeProcessingTime", 300, 20, Integer.MAX_VALUE);

            centrifugePoweredProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the powered centrifuge. Default 100.")
                    .defineInRange("centrifugePoweredProcessingTime", 100, 20, Integer.MAX_VALUE);

            centrifugePowerUse = builder
                    .comment("How much FE to use per tick for a powered centrifuge when processing an item. Default 10.")
                    .defineInRange("centrifugePowerUse", 10, 1, Integer.MAX_VALUE);

            incubatorProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the incubator. Default 3600.")
                    .defineInRange("incubatorProcessingTime", 3600, 20, Integer.MAX_VALUE);

            incubatorPowerUse = builder
                    .comment("How much FE to use per tick for an incubator when processing an item. Default 10.")
                    .defineInRange("incubatorPowerUse", 10, 1, Integer.MAX_VALUE);

            incubatorTreatUse = builder
                    .comment("How many treats to use when incubating a bee. Default 20.")
                    .defineInRange("incubatorTreatUse", 20, 1, 64);

            breedingChamberProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the breeding chamber. Default 6000.")
                    .defineInRange("breedingChamberProcessingTime", 6000, 20, Integer.MAX_VALUE);

            breedingChamberPowerUse = builder
                    .comment("How much FE to use per tick for an incubator when processing an item. Default 10.")
                    .defineInRange("breedingChamberPowerUse", 50, 1, Integer.MAX_VALUE);

            generatorPowerGen = builder
                    .comment("How much FE to generate per tick. Default 60.")
                    .defineInRange("generatorPowerGen", 60, 1, Integer.MAX_VALUE);

            generatorHoneyUse = builder
                    .comment("How much honey to consume per tick. Default 5.")
                    .defineInRange("generatorHoneyUse", 2, 1, Integer.MAX_VALUE);

            preferredTagSource = builder
                    .comment("A priority list of Mod IDs that results of comb output should stem from, aka which mod you want the copper to come from.")
                    .defineList("preferredTagSource", ImmutableList.of("minecraft", ProductiveBees.MODID, "alltheores", "ato", "thermal", "tconstruct", "create", "immersiveengineering", "mekanism", "enderio", "silents_mechanisms"), obj -> true);

            numberOfBeesPerBomb = builder
                    .comment("How many bees can fit in a bee bomb. Default is 10")
                    .defineInRange("numberOfBeesPerBomb", 10, 1, 50);

            nestLocatorDistance = builder
                    .comment("The distance a nest locator can search for nests.")
                    .defineInRange("nestLocatorDistance", 100, 0, 1000);

            nestSpawnCooldown = builder
                    .comment("Initial tick cooldown when repopulating a nest.")
                    .defineInRange("nestSpawnCooldown", 24000, 0, Integer.MAX_VALUE);

            centrifugeHopperMode = builder
                    .comment("Centrifuges will pick up items thrown on it")
                    .define("centrifugeHopperMode", true);

            stripForgeCaps = builder
                    .comment("Having a lot of bees (or bee cages in an inventory) in a single chunk can overload the chunk with data. A lot of data is already stripped from the bees as they are saved, but this will also remove all Forge capabilities, which is data added to the bees by other mods. Turn off to keep the data.")
                    .define("stripForgeCaps", false);

            forceEnableFarmerBeeRightClickHarvest = builder
                    .comment("Enable this if you have a right click harvest handler but none of the following mods: right_click_get_crops, croptopia, quark, harvest, simplefarming, reap")
                    .define("forceEnableFarmerBeeRightClickHarvest", false);

            builder.pop();
        }
    }

    public static class Bees
    {
        public final ModConfigSpec.BooleanValue allowBeeSimulation;
        public final ModConfigSpec.DoubleValue spawnUndeadBeesChance;
        public final ModConfigSpec.DoubleValue deadBeeConvertChance;
        public final ModConfigSpec.DoubleValue sugarbagBeeChance;
        public final ModConfigSpec.IntValue cupidBeeAnimalsPerPollination;
        public final ModConfigSpec.IntValue cupidBeeAnimalDensity;
        public final ModConfigSpec.IntValue cuckooSpawnCount;
        public final ModConfigSpec.DoubleValue kamikazBeeChance;
        public final ModConfigSpec.BooleanValue disableWanderGoal;
        public final ModConfigSpec.BooleanValue enableResinBeeEncasing;

        public Bees(ModConfigSpec.Builder builder) {
            builder.push("Bees");

            allowBeeSimulation = builder
                    .comment("Allow for bee simulation in hives. This will stop bees from exiting the hive and instead simulate a trip to flower blocks saving on performance.")
                    .define("allowBeeSimulation", true);

            spawnUndeadBeesChance = builder
                    .defineInRange("spawnUndeadBeesChance", 0.05, 0, 1);

            deadBeeConvertChance = builder
                    .defineInRange("deadBeeConvertChance", 0.03, 0, 1);

            sugarbagBeeChance = builder
                    .defineInRange("sugarbagBeeChance", 0.02, 0, 1);

            cupidBeeAnimalsPerPollination = builder
                    .comment("How many animals a CuBee can breed per pollination")
                    .defineInRange("cupidBeeAnimalsPerPollination", 5, 0, Integer.MAX_VALUE);

            cupidBeeAnimalDensity = builder
                    .comment("How densely populated should an areas need to be for the CuBee to stop breeding. The value approximates how many animals can be in a 10x10 area around the bee.")
                    .defineInRange("cupidBeeAnimalDensity", 20, 0, Integer.MAX_VALUE);

            cuckooSpawnCount = builder
                    .comment("How many cuckoo bees can spawn from a nest before it shuts off")
                    .defineInRange("cuckooSpawnCount", 2, 0, Integer.MAX_VALUE);

            kamikazBeeChance = builder
                    .comment("Chance to spawn a KamikazBee when hit while wearing bee nest armor")
                    .defineInRange("kamikazBeeChance", 0.3, 0, 1);

            disableWanderGoal = builder
                    .comment("Disable the wander goal in bees to increase performance")
                    .define("disableWanderGoal", false);

            enableResinBeeEncasing = builder
                    .comment("Allow resin bees to encase mobs in amber. With this disabled it's only possible with an amber bee and it's also not as fun.")
                    .define("enableResinBeeEncasing", true);

            builder.pop();
        }
    }

    public static class BeeAttributes
    {
        public final ModConfigSpec.IntValue leashedTicks;
        public final ModConfigSpec.DoubleValue damageChance;
        public final ModConfigSpec.DoubleValue toleranceChance;
        public final ModConfigSpec.DoubleValue behaviorChance;
        public final ModConfigSpec.DoubleValue geneExtractChance;
        public final ModConfigSpec.IntValue typeGenePurity;
        public final ModConfigSpec.IntValue effectTicks;

        public BeeAttributes(ModConfigSpec.Builder builder) {
            builder.push("Bee attributes");

            leashedTicks = builder
                    .comment("Number of ticks between attribute improvement attempts while leashed")
                    .defineInRange("ticks", 1337, 20, Integer.MAX_VALUE);
            damageChance = builder
                    .comment("Chance that a bee will take damage while leashed in a hostile environment")
                    .defineInRange("damageChance", 0.1, 0, 1);
            toleranceChance = builder
                    .comment("Chance to increase tolerance (rain or thunder tolerance trait) while leashed in a hostile environment.")
                    .defineInRange("toleranceChance", 0.1, 0, 1);
            behaviorChance = builder
                    .comment("Chance to increase behavior (nocturnal trait) while leashed in a hostile environment.")
                    .defineInRange("behaviorChance", 0.1, 0, 1);
            geneExtractChance = builder
                    .comment("Chance to extract genes from a bottle of bee material.")
                    .defineInRange("geneExtractChance", 1.0, 0, 1);
            typeGenePurity = builder
                    .comment("Average purity of type genes (does not apply to attribute genes)")
                    .defineInRange("typeGenePurity", 33, 1, 100);
            effectTicks = builder
                    .comment("Number of ticks between effects on nearby entities")
                    .defineInRange("ticks", 2337, 20, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class WorldGen
    {
        public final Map<String, ModConfigSpec.DoubleValue> nestConfigs = new HashMap<>();
        public final ModConfigSpec.DoubleValue treeGrowNestChance;

        public WorldGen(ModConfigSpec.Builder builder) {
            builder.push("Worldgen");
            builder.comment("Probability for a nest to generate in the world given it's conditions. Nest will still be craftable and attract bees when placed in the world.");

            nestConfigs.put("stone_nest", builder.defineInRange("stone_nest", 0.1D, 0.0D, 1.0D));
            nestConfigs.put("coarse_dirt_nest", builder.defineInRange("coarse_dirt_nest", 0.60D, 0.0D, 1.0D));
            nestConfigs.put("sand_nest", builder.defineInRange("sand_nest", 0.1D, 0.0D, 1.0D));
            nestConfigs.put("snow_nest", builder.defineInRange("snow_nest", 0.1D, 0.0D, 1.0D));
            nestConfigs.put("gravel_nest", builder.defineInRange("gravel_nest", 0.15D, 0.0D, 1.0D));
            nestConfigs.put("sugar_cane_nest", builder.defineInRange("sugar_cane_nest", 0.40D, 0.0D, 1.0D));
            nestConfigs.put("slimy_nest", builder.defineInRange("slimy_nest", 0.10D, 0.0D, 1.0D));
            nestConfigs.put("glowstone_nest", builder.defineInRange("glowstone_nest", 0.90D, 0.0D, 1.0D));
            nestConfigs.put("soul_sand_nest", builder.defineInRange("soul_sand_nest", 0.10D, 0.0D, 1.0D));
            nestConfigs.put("nether_quartz_nest", builder.defineInRange("nether_quartz_nest", 0.20D, 0.0D, 1.0D));
            nestConfigs.put("nether_brick_nest", builder.defineInRange("nether_brick_nest", 0.90D, 0.0D, 1.0D));
            nestConfigs.put("end_stone_nest", builder.defineInRange("end_stone_nest", 0.15D, 0.0D, 1.0D));
            nestConfigs.put("obsidian_nest", builder.defineInRange("obsidian_nest", 1.00D, 0.0D, 1.0D));
            nestConfigs.put("bumble_bee_nest", builder.defineInRange("bumble_bee_nest", 0.02D, 0.0D, 1.0D));
            nestConfigs.put("oak_wood_nest", builder.defineInRange("oak_wood_nest", 0.15D, 0.0D, 1.0D));
            nestConfigs.put("spruce_wood_nest", builder.defineInRange("spruce_wood_nest", 0.2D, 0.0D, 1.0D));
            nestConfigs.put("dark_oak_wood_nest", builder.defineInRange("dark_oak_wood_nest", 0.2D, 0.0D, 1.0D));
            nestConfigs.put("birch_wood_nest", builder.defineInRange("birch_wood_nest", 0.2D, 0.0D, 1.0D));
            nestConfigs.put("jungle_wood_nest", builder.defineInRange("jungle_wood_nest", 0.10D, 0.0D, 1.0D));
            nestConfigs.put("acacia_wood_nest", builder.defineInRange("acacia_wood_nest", 0.2D, 0.0D, 1.0D));
            nestConfigs.put("nether_bee_nest", builder.defineInRange("nether_bee_nest", 0.02D, 0.0D, 1.0D));
            nestConfigs.put("sugarbag_nest", builder.defineInRange("sugarbag_nest", 0.02D, 0.0D, 1.0D));

            treeGrowNestChance = builder
                    .comment("Chance for a nest to spawn when growing a tree or fungus.")
                    .defineInRange("treeGrowNestChance", 0.02, 0, 1);

            builder.pop();
        }
    }

    public static class Upgrades
    {
        public final ModConfigSpec.DoubleValue timeBonus;
        public final ModConfigSpec.DoubleValue productivityMultiplier;
        public final ModConfigSpec.DoubleValue productivityMultiplier2;
        public final ModConfigSpec.DoubleValue productivityMultiplier3;
        public final ModConfigSpec.DoubleValue productivityMultiplier4;
        public final ModConfigSpec.DoubleValue breedingChance;
        public final ModConfigSpec.IntValue breedingMaxNearbyEntities;
        public final ModConfigSpec.DoubleValue samplerChance;

        public Upgrades(ModConfigSpec.Builder builder) {
            builder.push("Hive Upgrades");

            timeBonus = builder
                    .comment("Time bonus gained from time upgrade. 0.2 means 20% reduction of a bee's time inside the hive or centrifuge processing time.")
                    .defineInRange("timeBonus", 0.2, 0, 1);
            productivityMultiplier = builder
                    .comment("Multiplier per productivity alpha upgrade installed in the hive.")
                    .defineInRange("productivityMultiplier", 1.2, 1, Integer.MAX_VALUE);
            productivityMultiplier2 = builder
                    .comment("Multiplier per productivity beta upgrade installed in the hive.")
                    .defineInRange("productivityMultiplier2", 1.5, 1, Integer.MAX_VALUE);
            productivityMultiplier3 = builder
                    .comment("Multiplier per productivity gamma upgrade installed in the hive.")
                    .defineInRange("productivityMultiplier3", 2.0, 1, Integer.MAX_VALUE);
            productivityMultiplier4 = builder
                    .comment("Multiplier per productivity omega upgrade installed in the hive.")
                    .defineInRange("productivityMultiplier4", 2.6, 1, Integer.MAX_VALUE);
            breedingChance = builder
                    .comment("Chance for a bee to produce an offspring after a hive visit.")
                    .defineInRange("breedingChance", 0.05, 0, 1);
            breedingMaxNearbyEntities = builder
                    .comment("How many bees can be around a hive before a babee upgrade stops working.")
                    .defineInRange("breedingMaxNearbyEntities", 10, 0, Integer.MAX_VALUE);
            samplerChance = builder
                    .comment("Chance for a gene sample to be taken from a bee after a hive visit.")
                    .defineInRange("samplerChance", 0.05, 0, 1);

            builder.pop();
        }
    }
}