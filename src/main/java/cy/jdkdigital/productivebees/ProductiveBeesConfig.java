package cy.jdkdigital.productivebees;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.ImmutableList;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class ProductiveBeesConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;
    public static final General GENERAL = new General(BUILDER);
    public static final Bees BEES = new Bees(BUILDER);
    public static final BeeAttributes BEE_ATTRIBUTES = new BeeAttributes(BUILDER);
    public static final WorldGen WORLD_GEN = new WorldGen(BUILDER);
    public static final Upgrades UPGRADES = new Upgrades(BUILDER);

    static {
        CONFIG = BUILDER.build();
    }

    public static class General
    {
        public final ForgeConfigSpec.BooleanValue enableItemConverting;
        public final ForgeConfigSpec.IntValue itemTickRate;
        public final ForgeConfigSpec.IntValue centrifugeProcessingTime;
        public final ForgeConfigSpec.IntValue centrifugePoweredProcessingTime;
        public final ForgeConfigSpec.IntValue centrifugePowerUse;
        public final ForgeConfigSpec.BooleanValue enableCombProduce;
        public final ForgeConfigSpec.IntValue nestRepopulationCooldown;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> preferredTagSource;
        public final ForgeConfigSpec.IntValue numberOfBeesPerBomb;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            enableItemConverting = builder
                    .comment("[UNUSED] Use items to change the type of a bee.", "If false, productive bees can only be obtained through breeding. Default false.")
                    .define("enableItemConverting", false);

            itemTickRate = builder
                    .comment("How often should a bee attempt to generate items while in the hive. Default 500.")
                    .defineInRange("itemTickRate", 1500, 20, Integer.MAX_VALUE);

            centrifugeProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the centrifuge. Default 300.")
                    .defineInRange("centrifugeProcessingTime", 300, 20, Integer.MAX_VALUE);

            centrifugePoweredProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the powerd centrifuge. Default 100.")
                    .defineInRange("centrifugePoweredProcessingTime", 100, 20, Integer.MAX_VALUE);

            centrifugePowerUse = builder
                    .comment("How much FE to use per tick for a powered centrifuge when processing an item. Default 10.")
                    .defineInRange("centrifugePowerUse", 10, 1, Integer.MAX_VALUE);

            enableCombProduce = builder
                    .comment("[DEPRECATED] Bees will create combs instead of raw resource. Combs will need to be processed in a centrifuge. Default true.")
                    .define("enableCombProduce", true);

            nestRepopulationCooldown = builder
                    .comment("Deprecated: Moved to spawning recipe.")
                    .defineInRange("nestRepopulationCooldown", 3600, 20, Integer.MAX_VALUE);

            preferredTagSource = builder
                    .comment("A priority list of Mod IDs that results of comb output should stem from, aka which mod you want the copper to come from. Eg: mekanism,silents_mekanism,immersiveengineering")
                    .defineList("preferredOres", ImmutableList.of(ProductiveBees.MODID, "immersiveengineering", "create", "mekanism", "silents_mechanisms"), obj -> obj.toString().length() > 1);

            numberOfBeesPerBomb = builder
                    .comment("How many bees can fit in a bee bomb. Default is 10")
                    .defineInRange("numberOfBeesPerBomb", 10, 1, 50);

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

            spawnUndeadBees = builder.comment("Spawn skeletal and zombie bees as night?").define("spawnUndeadBees", true);
            spawnUndeadBeesChance = builder.defineInRange("spawnUndeadBeesChance", 0.01, 0, 1);

            cupidBeeAnimalsPerPollination = builder.comment("How many animals a CuBee can breed per pollination").defineInRange("cupidBeeAnimalsPerPollination", 5, 0, Integer.MAX_VALUE);
            cupidBeeAnimalDensity = builder.comment("How densely populated should an areas need to be for the CuBee to stop breeding. The value approximates how many animals can be in a 10x10 area around the bee.").defineInRange("cupidBeeAnimalDensity", 15, 0, Integer.MAX_VALUE);

            builder.pop();
        }
    }

    public static class BeeAttributes
    {
        public final ForgeConfigSpec.IntValue leashedTicks;
        public final ForgeConfigSpec.DoubleValue damageChance;
        public final ForgeConfigSpec.DoubleValue toleranceChance;
        public final ForgeConfigSpec.DoubleValue behaviorChance;
        public final ForgeConfigSpec.DoubleValue genExtractChance;
        public final ForgeConfigSpec.IntValue effectTicks;

        public BeeAttributes(ForgeConfigSpec.Builder builder) {
            builder.push("Bee attributes");

            leashedTicks = builder.comment("Number of ticks between attribute improvement attempts").defineInRange("ticks", 1337, 20, Integer.MAX_VALUE);
            damageChance = builder.comment("Chance that a bee will take damage while leashed in a hostile environment").defineInRange("damageChance", 0.1, 0, 1);
            toleranceChance = builder.comment("Chance to increase tolerance (rain or thunder tolerance trait) while leashed in a hostile environment.").defineInRange("toleranceChance", 0.1, 0, 1);
            behaviorChance = builder.comment("Chance to increase behavior (nocturnal trait) while leashed in a hostile environment.").defineInRange("behaviorChance", 0.1, 0, 1);
            genExtractChance = builder.comment("Chance to extract genes from a bottle of bee material.").defineInRange("genExtractChance", 0.85, 0, 1);

            effectTicks = builder.comment("Number of ticks between effects on nearby entities").defineInRange("ticks", 2337, 20, Integer.MAX_VALUE);
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

        public Upgrades(ForgeConfigSpec.Builder builder) {
            builder.push("Hive Upgrades");

            timeBonus = builder.comment("Time bonus gained from time upgrade. 0.2 means 20% reduction of a bee's time inside the hive.").defineInRange("timeBonus", 0.2, 0, 1);
            combBlockTimeModifier = builder.comment("Time penalty from installing the comb block upgrade. .4 means 40% increase of a bee's time inside the hive.").defineInRange("combBlockTimeModifier", .4, 0, 1);
            productivityMultiplier = builder.comment("Multiplier per productivity upgrade installed in the hive.").defineInRange("productivityMultiplier", 1.8, 1, Integer.MAX_VALUE);
            breedingChance = builder.comment("Chance for a bee to produce an offspring after a hive visit.").defineInRange("breedingChance", 0.05, 0, 1);

            builder.pop();
        }
    }

    public static void loadConfig(ForgeConfigSpec config, String path) {
        final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
        file.load();
        config.setConfig(file);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }
}