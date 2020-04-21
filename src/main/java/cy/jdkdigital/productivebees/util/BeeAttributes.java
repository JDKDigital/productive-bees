package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BeeAttributes {
    public static final BeeAttribute<String> TYPE = register("type");
    public static final BeeAttribute<Integer> PRODUCTIVITY = register("productivity");
    public static final BeeAttribute<Integer> TEMPER = register("temper");
    public static final BeeAttribute<Integer> BEHAVIOR = register("behavior");
    public static final BeeAttribute<Integer> WEATHER_TOLERANCE = register("weather_tolerance");
    public static final BeeAttribute<Tag<Block>> FOOD_SOURCE = register("food_source");
    public static final BeeAttribute<Tag<Item>> APHRODISIACS = register("aphrodisiacs");

    public static Map<BeeAttribute<?>, Map<Integer, String>> keyMap = new HashMap<>();

    private static <T> BeeAttribute<T> register(String resourceLocation) {
        return new BeeAttribute<T>(new ResourceLocation(ProductiveBees.MODID, resourceLocation));
    }

    static {
        keyMap.put(PRODUCTIVITY, new HashMap<Integer, String>() {{
            put(0, "productivebees.information.attribute.productivity.normal");
            put(1, "productivebees.information.attribute.productivity.medium");
            put(2, "productivebees.information.attribute.productivity.high");
            put(3, "productivebees.information.attribute.productivity.very_high");
        }});
        keyMap.put(TEMPER, new HashMap<Integer, String>() {{
            put(0, "productivebees.information.attribute.temper.passive");
            put(1, "productivebees.information.attribute.temper.normal");
            put(2, "productivebees.information.attribute.temper.short");
            put(3, "productivebees.information.attribute.temper.hostile");
        }});
        keyMap.put(BEHAVIOR, new HashMap<Integer, String>() {{
            put(0, "productivebees.information.attribute.behavior.diurnal");
            put(1, "productivebees.information.attribute.behavior.nocturnal");
            put(2, "productivebees.information.attribute.behavior.metaturnal");
        }});
        keyMap.put(WEATHER_TOLERANCE, new HashMap<Integer, String>() {{
            put(0, "productivebees.information.attribute.weather_tolerance.none");
            put(1, "productivebees.information.attribute.weather_tolerance.rain");
            put(2, "productivebees.information.attribute.weather_tolerance.any");
        }});
    }
}
