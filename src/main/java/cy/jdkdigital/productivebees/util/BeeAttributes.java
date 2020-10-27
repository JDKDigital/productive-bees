package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BeeAttributes
{
    private static Map<String, BeeAttribute<?>> map = new HashMap<>();
    public static Map<BeeAttribute<?>, Map<Integer, String>> keyMap = new HashMap<>();

    public static final BeeAttribute<String> TYPE = register("type");
    public static final BeeAttribute<Integer> PRODUCTIVITY = register("productivity");
    public static final BeeAttribute<Integer> ENDURANCE = register("endurance");
    public static final BeeAttribute<Integer> TEMPER = register("temper");
    public static final BeeAttribute<Integer> BEHAVIOR = register("behavior");
    public static final BeeAttribute<Integer> WEATHER_TOLERANCE = register("weather_tolerance");
    public static final BeeAttribute<Tag<Block>> FOOD_SOURCE = register("food_source");
    public static final BeeAttribute<Tag<Item>> APHRODISIACS = register("aphrodisiacs");
    public static final BeeAttribute<Tag<Block>> NESTING_PREFERENCE = register("nesting_preference");
    public static final BeeAttribute<BeeEffect> EFFECTS = register("effect");

    private static final UUID HEALTH_MOD_ID_WEAK = UUID.nameUUIDFromBytes("productivebees:health_modifier_weak".getBytes());
    private static final UUID HEALTH_MOD_ID_MEDIUM = UUID.nameUUIDFromBytes("productivebees:health_modifier_medium".getBytes());
    private static final UUID HEALTH_MOD_ID_STRONG = UUID.nameUUIDFromBytes("productivebees:health_modifier_strong".getBytes());
    public static final Map<Integer, AttributeModifier> HEALTH_MODS = new HashMap<Integer, AttributeModifier>() {{
        put(0, (new AttributeModifier(HEALTH_MOD_ID_WEAK, "Health mod weak", 0.30F, AttributeModifier.Operation.MULTIPLY_BASE)).setSaved(false));
        put(2, (new AttributeModifier(HEALTH_MOD_ID_MEDIUM, "Health mod medium", 0.6F, AttributeModifier.Operation.MULTIPLY_BASE)).setSaved(false));
        put(3, (new AttributeModifier(HEALTH_MOD_ID_STRONG, "Health mod strong", 1.0F, AttributeModifier.Operation.MULTIPLY_BASE)).setSaved(false));
    }};

    private static <T> BeeAttribute<T> register(String name) {
        BeeAttribute<T> attribute = new BeeAttribute<T>(new ResourceLocation(ProductiveBees.MODID, name));

        map.put(name, attribute);

        return attribute;
    }

    static {
        keyMap.put(PRODUCTIVITY, new HashMap<Integer, String>()
        {{
            put(0, "productivebees.information.attribute.productivity.normal");
            put(1, "productivebees.information.attribute.productivity.medium");
            put(2, "productivebees.information.attribute.productivity.high");
            put(3, "productivebees.information.attribute.productivity.very_high");
        }});
        keyMap.put(ENDURANCE, new HashMap<Integer, String>()
        {{
            put(0, "productivebees.information.attribute.endurance.weak");
            put(1, "productivebees.information.attribute.endurance.normal");
            put(2, "productivebees.information.attribute.endurance.medium");
            put(3, "productivebees.information.attribute.endurance.strong");
        }});
        keyMap.put(TEMPER, new HashMap<Integer, String>()
        {{
            put(0, "productivebees.information.attribute.temper.passive");
            put(1, "productivebees.information.attribute.temper.normal");
            put(2, "productivebees.information.attribute.temper.aggressive");
            put(3, "productivebees.information.attribute.temper.hostile");
        }});
        keyMap.put(BEHAVIOR, new HashMap<Integer, String>()
        {{
            put(0, "productivebees.information.attribute.behavior.diurnal");
            put(1, "productivebees.information.attribute.behavior.nocturnal");
            put(2, "productivebees.information.attribute.behavior.metaturnal");
        }});
        keyMap.put(WEATHER_TOLERANCE, new HashMap<Integer, String>()
        {{
            put(0, "productivebees.information.attribute.weather_tolerance.none");
            put(1, "productivebees.information.attribute.weather_tolerance.rain");
            put(2, "productivebees.information.attribute.weather_tolerance.any");
        }});
    }

    public static BeeAttribute<?> getAttributeByName(String name) {
        return map.get(name);
    }
}
