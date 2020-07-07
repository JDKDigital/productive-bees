package cy.jdkdigital.productivebees;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.HashMap;
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

    static {
        CONFIG = BUILDER.build();
    }

    public static class General
    {
        public final ForgeConfigSpec.BooleanValue enableItemConverting;
        public final ForgeConfigSpec.IntValue itemTickRate;
        public final ForgeConfigSpec.IntValue centrifugeProcessingTime;
        public final ForgeConfigSpec.BooleanValue enableCombProduce;
        public final ForgeConfigSpec.IntValue nestRepopulationCooldown;
        public final ForgeConfigSpec.ConfigValue<String> woodChipLogsBlacklist;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            enableItemConverting = builder
                    .comment("Use items to change the type of a bee.", "If false, productive bees can only be obtained through breeding. Default false.")
                    .define("enableItemConverting", false);

            itemTickRate = builder
                    .comment("How often should a bee attempt to generate items while in the hive. Default 500.")
                    .defineInRange("itemTickRate", 500, 20, Integer.MAX_VALUE);

            centrifugeProcessingTime = builder
                    .comment("How many ticks it takes for process a recipe in the centrifuge. Default 300.")
                    .defineInRange("centrifugeProcessingTime", 300, 20, Integer.MAX_VALUE);

            enableCombProduce = builder
                    .comment("Bees will create combs instead of raw resource. Combs will need to be processed in a centrifuge. Default true.")
                    .define("enableCombProduce", true);

            nestRepopulationCooldown = builder
                    .comment("Cooldown for when an abandoned nest will spawn a new inhabitant")
                    .defineInRange("nestRepopulationCooldown", 36000, 20, Integer.MAX_VALUE);

            woodChipLogsBlacklist = builder
                    .comment("Comma separated list of block names to exclude from the list of woodchips")
                    .define("woodChipLogsBlacklist", "atmospheric:crustose_log");

            builder.pop();
        }
    }

    public static class Bees
    {
        public final ForgeConfigSpec.BooleanValue spawnUndeadBees;
        public final ForgeConfigSpec.DoubleValue spawnUndeadBeesChance;
        public final Map<String, ForgeConfigSpec.DoubleValue> itemProductionRates = new HashMap<>();

        public Bees(ForgeConfigSpec.Builder builder) {
            builder.push("Bees");

            spawnUndeadBees = builder.comment("Spawn skeletal and zombie bees as night?").define("spawnUndeadBees", true);
            spawnUndeadBeesChance = builder.defineInRange("spawnUndeadBeesChance", 0.01, 0, 1);

            builder.comment("Production chance for all bees who can produce items inside advanced hives.", "The final production rate also depends on General.itemTickRate");
            builder.comment("Production chance for vanilla bees");
            itemProductionRates.put("minecraft:bee", builder.defineInRange("minecraft:bee", 0.65D, 0, 1));
            for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
                ResourceLocation resourceLocation = registryObject.getId();
                builder.comment("Production chance for " + resourceLocation);
                itemProductionRates.put(resourceLocation + "", builder.defineInRange(resourceLocation + "", 0.65D, 0, 1));
            }

            builder.pop();
        }
    }

    public static class BeeAttributes
    {
        public final ForgeConfigSpec.IntValue leashedTicks;
        public final ForgeConfigSpec.DoubleValue damageChance;
        public final ForgeConfigSpec.DoubleValue toleranceChance;
        public final ForgeConfigSpec.DoubleValue behaviorChance;
        public final ForgeConfigSpec.IntValue effectTicks;

        public BeeAttributes(ForgeConfigSpec.Builder builder) {
            builder.push("Bee attributes");

            leashedTicks = builder.comment("Number of ticks between attribute improvement attempts").defineInRange("ticks", 1337, 20, Integer.MAX_VALUE);
            damageChance = builder.comment("Chance that a bee will take damage while leashed in a hostile environment").defineInRange("damageChance", 0.1, 0, 1);
            toleranceChance = builder.comment("Chance to increase tolerance (rain or thunder tolerance trait) while leashed in a hostile environment.").defineInRange("toleranceChance", 0.1, 0, 1);
            behaviorChance = builder.comment("Chance to increase behavior (nocturnal trait) while leashed in a hostile environment.").defineInRange("behaviorChance", 0.1, 0, 1);

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