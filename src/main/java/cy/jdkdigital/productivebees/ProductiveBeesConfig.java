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
public class ProductiveBeesConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;
    public static final General GENERAL = new General(BUILDER);
    public static final Bees BEES = new Bees(BUILDER);
    public static final WorldGen WORLD_GEN = new WorldGen(BUILDER);

    static {
        CONFIG = BUILDER.build();
    }

    public static class General {
        public final ForgeConfigSpec.BooleanValue enableItemConverting;
        public final ForgeConfigSpec.IntValue itemTickRate;
        public final ForgeConfigSpec.IntValue centrifugeProcessingTime;

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

            builder.pop();
        }
    }

    public static class Bees {
        public final Map<String, ForgeConfigSpec.ConfigValue<Double>> itemProductionRates = new HashMap<>();
        public final ForgeConfigSpec.ConfigValue<Boolean> spawnUndeadBees;
        public final ForgeConfigSpec.ConfigValue<Double> spawnUndeadBeesChance;

        public Bees(ForgeConfigSpec.Builder builder) {
            builder.push("Bees");

            spawnUndeadBees = builder.comment("Spawn skeletal and zombie bees as night?").define("spawnUndeadBees", true);
            spawnUndeadBeesChance = builder.defineInRange("spawnUndeadBeesChance", 0.01, 0, 1);

            itemProductionRates.put("minecraft:bee", builder.defineInRange("minecraft:bee",  0.25D, 0, 1));

            for(RegistryObject<EntityType<?>> registryObject: ModEntities.HIVE_BEES.getEntries()) {
                ResourceLocation resourceLocation = registryObject.getId();
                itemProductionRates.put(resourceLocation + "", builder.defineInRange(resourceLocation + "",  0.25D, 0, 1));
            }

            builder.pop();
        }
    }

    public static class WorldGen {
        public final Map<String, ForgeConfigSpec.BooleanValue> nestConfigs = new HashMap<>();

        public WorldGen(ForgeConfigSpec.Builder builder) {
            builder.push("Worldgen");
//            builder.comment("Which nests should generate in the world. Nest will still be craftable and attract bees.");

            for (RegistryObject<Block> blockReg: ModBlocks.BLOCKS.getEntries()) {
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