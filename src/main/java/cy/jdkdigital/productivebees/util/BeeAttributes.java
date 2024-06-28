package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import javax.annotation.Nullable;
import java.util.*;

public class BeeAttributes
{
//    public static Map<String, GeneAttribute<Integer>> map = new HashMap<>();
//    public static Map<GeneAttribute<Integer>, Map<Integer, String>> keyMap = new HashMap<>();

//    public static final GeneAttribute<Integer> PRODUCTIVITY = register("productivity");
//    public static final GeneAttribute<Integer> ENDURANCE = register("endurance");
//    public static final GeneAttribute<Integer> TEMPER = register("temper");
//    public static final GeneAttribute<Integer> BEHAVIOR = register("behavior");
//    public static final GeneAttribute<Integer> WEATHER_TOLERANCE = register("weather_tolerance");

    public static final ResourceLocation HEALTH_MOD_ID_WEAK = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_weak");
    public static final ResourceLocation HEALTH_MOD_ID_MEDIUM = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_medium");
    public static final ResourceLocation HEALTH_MOD_ID_STRONG = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_strong");
    public static final Map<Integer, AttributeModifier> HEALTH_MODS = new HashMap<>();

//    private static GeneAttribute<Integer> register(String name) {
////        GeneAttribute<Integer> attribute = new GeneAttribute(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, name));
//
//        map.put(name, attribute);
//
//        return attribute;
//    }

//    public static List<String> attributeList() {
//        List<String> attributes = new ArrayList<>();
//        attributes.add("productivity");
//        attributes.add("weather_tolerance");
//        attributes.add("behavior");
//        attributes.add("endurance");
//        attributes.add("temper");
//
//        return attributes;
//    }

//    static {
//        keyMap.put(PRODUCTIVITY, new HashMap<>()
//        {{
//            put(0, "productivebees.information.attribute.productivity.normal");
//            put(1, "productivebees.information.attribute.productivity.medium");
//            put(2, "productivebees.information.attribute.productivity.high");
//            put(3, "productivebees.information.attribute.productivity.very_high");
//        }});
//        keyMap.put(ENDURANCE, new HashMap<>()
//        {{
//            put(0, "productivebees.information.attribute.endurance.weak");
//            put(1, "productivebees.information.attribute.endurance.normal");
//            put(2, "productivebees.information.attribute.endurance.medium");
//            put(3, "productivebees.information.attribute.endurance.strong");
//        }});
//        keyMap.put(TEMPER, new HashMap<>()
//        {{
//            put(0, "productivebees.information.attribute.temper.passive");
//            put(1, "productivebees.information.attribute.temper.normal");
//            put(2, "productivebees.information.attribute.temper.aggressive");
//            put(3, "productivebees.information.attribute.temper.hostile");
//        }});
//        keyMap.put(BEHAVIOR, new HashMap<>()
//        {{
//            put(0, "productivebees.information.attribute.behavior.diurnal");
//            put(1, "productivebees.information.attribute.behavior.nocturnal");
//            put(2, "productivebees.information.attribute.behavior.metaturnal");
//        }});
//        keyMap.put(WEATHER_TOLERANCE, new HashMap<>()
//        {{
//            put(0, "productivebees.information.attribute.weather_tolerance.none");
//            put(1, "productivebees.information.attribute.weather_tolerance.rain");
//            put(2, "productivebees.information.attribute.weather_tolerance.any");
//        }});
//
//        HEALTH_MODS.put(0, (new AttributeModifier(HEALTH_MOD_ID_WEAK, "Health mod weak", -0.30F, AttributeModifier.Operation.MULTIPLY_BASE)));
//        HEALTH_MODS.put(2, (new AttributeModifier(HEALTH_MOD_ID_MEDIUM, "Health mod medium", 0.5F, AttributeModifier.Operation.MULTIPLY_BASE)));
//        HEALTH_MODS.put(3, (new AttributeModifier(HEALTH_MOD_ID_STRONG, "Health mod strong", 1.0F, AttributeModifier.Operation.MULTIPLY_BASE)));
//    }

//    @Nullable
//    public static GeneAttribute<Integer> getAttributeByName(String name) {
//        return map.get(name);
//    }
}
