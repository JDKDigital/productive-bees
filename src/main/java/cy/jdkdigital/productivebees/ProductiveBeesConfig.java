package cy.jdkdigital.productivebees;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class ProductiveBeesConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;
    public static final General GENERAL = new General(BUILDER);
    public static final Bees BEES = new Bees(BUILDER);

    static {
        CONFIG = BUILDER.build();
    }

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Boolean> enableItemConverting;
        public final ForgeConfigSpec.ConfigValue<Integer> itemTickRate;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            enableItemConverting = builder
                    .comment("Use items to change the type of a bee.", "If false, productive bees can only be obtained through breeding.")
                    .define("enableItemConverting", true);

            itemTickRate = builder
                    .comment("How often should a bee generate items while in the hive.")
                    .define("itemTickRate", 600);

            builder.pop();
        }
    }

    public static class Bees {
        public final ForgeConfigSpec.ConfigValue<Config> itemProductionRules;

        public Bees(ForgeConfigSpec.Builder builder) {
            builder.push("Bees");

            Config productionValues = Config.inMemory();

            productionValues.add("minecraft:bee", itemConfig(new HashMap<String, Double>() {{
                put(ForgeRegistries.ITEMS.getKey(Items.HONEYCOMB).toString(), 0.25D);
            }}));
            productionValues.add("productivebees:iron_bee", itemConfig(new HashMap<String, Double>() {{
                put(ForgeRegistries.ITEMS.getKey(Items.IRON_INGOT).toString(), 0.10D);
                put(Items.IRON_NUGGET.getRegistryName().toString(), 0.40D);
            }}));
            productionValues.add("productivebees:gold_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.GOLD_INGOT.getRegistryName().toString(), 0.10D);
                put(Items.GOLD_NUGGET.getRegistryName().toString(), 0.40D);
            }}));
            productionValues.add("productivebees:redstone_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.REDSTONE.getRegistryName().toString(), 0.25D);
            }}));
            productionValues.add("productivebees:lapis_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.LAPIS_LAZULI.getRegistryName().toString(), 0.20D);
            }}));
            productionValues.add("productivebees:emerald_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.EMERALD.getRegistryName().toString(), 0.05D);
            }}));
            productionValues.add("productivebees:diamond_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.DIAMOND.getRegistryName().toString(), 0.10D);
            }}));
            productionValues.add("productivebees:glowing_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.GLOWSTONE.getRegistryName().toString(), 0.25D);
            }}));
            productionValues.add("productivebees:quartz_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.QUARTZ.getRegistryName().toString(), 0.25D);
            }}));
            productionValues.add("productivebees:creeper_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.GUNPOWDER.getRegistryName().toString(), 0.10D);
            }}));
            productionValues.add("productivebees:zombie_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.ROTTEN_FLESH.getRegistryName().toString(), 0.10D);
            }}));
            productionValues.add("productivebees:ender_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.ENDER_PEARL.getRegistryName().toString(), 0.05D);
            }}));
            productionValues.add("productivebees:skeletal_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.BONE.getRegistryName().toString(), 0.10D);
            }}));
            productionValues.add("productivebees:wither_bee", itemConfig(new HashMap<String, Double>() {{
                put(Items.WITHER_SKELETON_SKULL.getRegistryName().toString(), 0.01D);
            }}));

            itemProductionRules = builder
                    .comment("Bee production rules.")
                    .define("itemProductionRules", productionValues);

            builder.pop();
        }
    }

    private static Config itemConfig(Map<String, Double> itemMap) {
        Config config = Config.inMemory();
        for (Map.Entry<String, Double> entry : itemMap.entrySet()) {
            config.add(entry.getKey(), entry.getValue());
        }
        return config;
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }

}