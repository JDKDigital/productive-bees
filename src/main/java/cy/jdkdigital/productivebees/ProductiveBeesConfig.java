package cy.jdkdigital.productivebees;

import com.google.common.collect.ImmutableList;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class ProductiveBeesConfig
{
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_CONFIG;
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final ForgeConfigSpec SERVER_CONFIG;
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
        public final ForgeConfigSpec.BooleanValue renderCombsInCentrifuge;
        public final ForgeConfigSpec.BooleanValue renderBeesInJars;
        public final ForgeConfigSpec.BooleanValue renderBeeIngredientAsEntity;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("Client");

            renderCombsInCentrifuge = builder
                    .comment("Render centrifuge comb inventory on the block.")
                    .define("renderCombsInCentrifuge", true);

            renderBeesInJars = builder
                    .comment("Render bees inside bee jars.")
                    .define("renderBeesInJars", true);

            renderBeeIngredientAsEntity = builder
                    .comment("Render bees as entities in JEI and in hive GUI. Set to false to show an image of the bee instead.")
                    .define("renderBeeIngredientAsEntity", true);

            builder.pop();
        }
    }

    public static class General
    {
        public final ForgeConfigSpec.IntValue hiveTickRate;
        public final ForgeConfigSpec.IntValue timeInHive;
        public final ForgeConfigSpec.IntValue centrifugeProcessingTime;
        public final ForgeConfigSpec.IntValue centrifugePoweredProcessingTime;
        public final ForgeConfigSpec.IntValue centrifugePowerUse;
        public final ForgeConfigSpec.IntValue incubatorProcessingTime;
        public final ForgeConfigSpec.IntValue incubatorPowerUse;
        public final ForgeConfigSpec.IntValue incubatorTreatUse;
        public final ForgeConfigSpec.IntValue generatorPowerGen;
        public final ForgeConfigSpec.IntValue generatorHoneyUse;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> preferredTagSource;
        public final ForgeConfigSpec.IntValue numberOfBeesPerBomb;
        public final ForgeConfigSpec.IntValue beeSyncDelay;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            hiveTickRate = builder
                    .comment("How often a hive should attempt special events like spawning undead bees. Default 500.")
                    .defineInRange("hiveTickRate", 1500, 20, Integer.MAX_VALUE);

            timeInHive = builder
                    .comment("How long time a bee should stay in the hive when having delivered honey. Default 4800.")
                    .defineInRange("timeInHive", 4800, 20, Integer.MAX_VALUE);

            centrifugeProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the centrifuge. Default 200.")
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

            generatorPowerGen = builder
                    .comment("How much FE to generate per tick. Default 60.")
                    .defineInRange("generatorPowerGen", 60, 1, Integer.MAX_VALUE);

            generatorHoneyUse = builder
                    .comment("How much honey to consume per tick. Default 5.")
                    .defineInRange("generatorHoneyUse", 5, 1, Integer.MAX_VALUE);

            preferredTagSource = builder
                    .comment("A priority list of Mod IDs that results of comb output should stem from, aka which mod you want the copper to come from.")
                    .defineList("preferredTagSource", ImmutableList.of(ProductiveBees.MODID, "thermal", "immersiveengineering", "create", "mekanism", "silents_mechanisms"), obj -> true);

            numberOfBeesPerBomb = builder
                    .comment("How many bees can fit in a bee bomb. Default is 10")
                    .defineInRange("numberOfBeesPerBomb", 10, 1, 50);

            beeSyncDelay = builder
                    .comment("Delay in seconds between a user logging in and the bee data being synced to the client. A delay is needed to allow JEI to index the bees properly.\n If the clients are having issues getting the bees, try increasing the delay.")
                    .defineInRange("beeSyncDelay", 5, 1, 300);

            builder.pop();
        }
    }

    public static class Bees
    {
        public final ForgeConfigSpec.BooleanValue spawnUndeadBees;
        public final ForgeConfigSpec.DoubleValue spawnUndeadBeesChance;
        public final ForgeConfigSpec.IntValue cupidBeeAnimalsPerPollination;
        public final ForgeConfigSpec.IntValue cupidBeeAnimalDensity;

        public Bees(ForgeConfigSpec.Builder builder) {
            builder.push("Bees");

            spawnUndeadBees = builder
                    .comment("Spawn skeletal and zombie bees as night?")
                    .define("spawnUndeadBees", true);
            spawnUndeadBeesChance = builder
                    .defineInRange("spawnUndeadBeesChance", 0.05, 0, 1);

            cupidBeeAnimalsPerPollination = builder
                    .comment("How many animals a CuBee can breed per pollination")
                    .defineInRange("cupidBeeAnimalsPerPollination", 5, 0, Integer.MAX_VALUE);
            cupidBeeAnimalDensity = builder
                    .comment("How densely populated should an areas need to be for the CuBee to stop breeding. The value approximates how many animals can be in a 10x10 area around the bee.")
                    .defineInRange("cupidBeeAnimalDensity", 20, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class BeeAttributes
    {
        public final ForgeConfigSpec.IntValue leashedTicks;
        public final ForgeConfigSpec.DoubleValue damageChance;
        public final ForgeConfigSpec.DoubleValue toleranceChance;
        public final ForgeConfigSpec.DoubleValue behaviorChance;
        public final ForgeConfigSpec.DoubleValue geneExtractChance;
        public final ForgeConfigSpec.IntValue effectTicks;

        public BeeAttributes(ForgeConfigSpec.Builder builder) {
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
                    .defineInRange("geneExtractChance", 0.85, 0, 1);
            effectTicks = builder
                    .comment("Number of ticks between effects on nearby entities")
                    .defineInRange("ticks", 2337, 20, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class WorldGen
    {
        public final Map<String, ForgeConfigSpec.BooleanValue> nestConfigs = new HashMap<>();

        public WorldGen(ForgeConfigSpec.Builder builder) {
            builder.push("Worldgen");
            builder.comment("Which nests should generate in the world. Nest will still be craftable and attract bees when placed in the world.");

            for (RegistryObject<Block> blockReg : ModBlocks.BLOCKS.getEntries()) {
                ResourceLocation resName = blockReg.getId();
                if (resName.toString().contains("_nest")) {
                    nestConfigs.put("enable_" + resName, builder.define("enable_" + resName, true));
                }
            }

            builder.pop();
        }
    }

    public static class Upgrades
    {
        public final ForgeConfigSpec.DoubleValue timeBonus;
        public final ForgeConfigSpec.DoubleValue combBlockTimeModifier;
        public final ForgeConfigSpec.DoubleValue productivityMultiplier;
        public final ForgeConfigSpec.DoubleValue breedingChance;
        public final ForgeConfigSpec.IntValue breedingMaxNearbyEntities;

        public Upgrades(ForgeConfigSpec.Builder builder) {
            builder.push("Hive Upgrades");

            timeBonus = builder
                    .comment("Time bonus gained from time upgrade. 0.2 means 20% reduction of a bee's time inside the hive.")
                    .defineInRange("timeBonus", 0.2, 0, 1);
            combBlockTimeModifier = builder
                    .comment("Time penalty from installing the comb block upgrade. .4 means 40% increase of a bee's time inside the hive.")
                    .defineInRange("combBlockTimeModifier", 1.0, 0, Integer.MAX_VALUE);
            productivityMultiplier = builder
                    .comment("Multiplier per productivity upgrade installed in the hive.")
                    .defineInRange("productivityMultiplier", 1.4, 1, Integer.MAX_VALUE);
            breedingChance = builder
                    .comment("Chance for a bee to produce an offspring after a hive visit.")
                    .defineInRange("breedingChance", 0.05, 0, 1);
            breedingMaxNearbyEntities = builder
                    .comment("Chance for a bee to produce an offspring after a hive visit.")
                    .defineInRange("breedingMaxNearbyEntities", 10, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}