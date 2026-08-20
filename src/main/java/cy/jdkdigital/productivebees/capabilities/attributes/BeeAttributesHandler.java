package cy.jdkdigital.productivebees.capabilities.attributes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BeeAttributesHandler implements IBeeAttributes
{
    public static final MapCodec<BeeAttributesHandler> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GeneValue.CODEC.fieldOf("bee_productivity").forGetter(h -> h.getAttributeValue(GeneAttribute.PRODUCTIVITY)),
            GeneValue.CODEC.fieldOf("bee_endurance").forGetter(h -> h.getAttributeValue(GeneAttribute.ENDURANCE)),
            GeneValue.CODEC.fieldOf("bee_temper").forGetter(h -> h.getAttributeValue(GeneAttribute.TEMPER)),
            GeneValue.CODEC.fieldOf("bee_behavior").forGetter(h -> h.getAttributeValue(GeneAttribute.BEHAVIOR)),
            GeneValue.CODEC.fieldOf("bee_weather_tolerance").forGetter(h -> h.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE))
    ).apply(instance, BeeAttributesHandler::of));

    protected Map<GeneAttribute, GeneValue> beeAttributes = new HashMap<>();

    public BeeAttributesHandler() {
        Random rand = new Random();
        setAttributeValue(GeneAttribute.PRODUCTIVITY, GeneValue.getRandomProductivity(rand));
        setAttributeValue(GeneAttribute.TEMPER, GeneValue.TEMPER_NORMAL);
        setAttributeValue(GeneAttribute.ENDURANCE, GeneValue.getRandomEndurance(rand));
        setAttributeValue(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_DIURNAL);
        setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_NONE);
    }

    public static BeeAttributesHandler of(GeneValue productivity, GeneValue endurance, GeneValue temper, GeneValue behavior, GeneValue weatherTolerance) {
        BeeAttributesHandler h = new BeeAttributesHandler();
        h.setAttributeValue(GeneAttribute.PRODUCTIVITY, productivity);
        h.setAttributeValue(GeneAttribute.ENDURANCE, endurance);
        h.setAttributeValue(GeneAttribute.TEMPER, temper);
        h.setAttributeValue(GeneAttribute.BEHAVIOR, behavior);
        h.setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, weatherTolerance);
        return h;
    }

    @Override
    public GeneValue getAttributeValue(GeneAttribute attribute) {
        return this.beeAttributes.get(attribute);
    }

    @Override
    public void setAttributeValue(GeneAttribute attribute, GeneValue value) {
        beeAttributes.put(attribute, value);
    }

    @Override
    public Map<GeneAttribute, GeneValue> getAttributes() {
        return beeAttributes;
    }
}
