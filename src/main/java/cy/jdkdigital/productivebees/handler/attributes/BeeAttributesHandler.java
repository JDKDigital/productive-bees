package cy.jdkdigital.productivebees.handler.attributes;

import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeAttributesHandler implements IBeeAttributes
{
    protected Map<BeeAttribute<?>, Object> beeAttributes = new HashMap<>();

    @Override
    public void setDefaults() {
        beeAttributes.put(BeeAttributes.PRODUCTIVITY, 1);
        beeAttributes.put(BeeAttributes.TEMPER, 1);
        beeAttributes.put(BeeAttributes.ENDURANCE, 1);
        beeAttributes.put(BeeAttributes.BEHAVIOR, 0);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        beeAttributes.put(BeeAttributes.TYPE, "hive");
    }

    public <T> T getAttributeValue(BeeAttribute<T> attribute) {
        return (T) this.beeAttributes.get(attribute);
    }

    @Override
    public void setAttributeValue(BeeAttribute<?> attribute, int value) {
        beeAttributes.put(attribute, value);
    }

    @Override
    public void setAttributeValue(BeeAttribute<?> attribute, String value) {
        beeAttributes.put(attribute, value);
    }

    @Override
    public Map<BeeAttribute<?>, Object> getAttributes() {
        return beeAttributes;
    }

    @Nonnull
    @Override
    public Tag getAsNBT() {
        for (Map.Entry<BeeAttribute<?>, Object> entry : getAttributes().entrySet()) {
        }
        CompoundTag tag = new CompoundTag();
        tag.putInt("bee_productivity", this.getAttributeValue(BeeAttributes.PRODUCTIVITY));
        tag.putInt("bee_endurance", this.getAttributeValue(BeeAttributes.ENDURANCE));
        tag.putInt("bee_temper", this.getAttributeValue(BeeAttributes.TEMPER));
        tag.putInt("bee_behavior", this.getAttributeValue(BeeAttributes.BEHAVIOR));
        tag.putInt("bee_weather_tolerance", this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE));
        tag.putString("bee_type", this.getAttributeValue(BeeAttributes.TYPE));
        return tag;
    }

    @Override
    public void readFromNBT(Tag tag) {
//        ProductiveBees.LOGGER.info("readFromNBT " + (tag instanceof CompoundTag));
        if (tag instanceof CompoundTag) {
            CompoundTag nbt = (CompoundTag) tag;

            beeAttributes.put(BeeAttributes.PRODUCTIVITY, nbt.getInt("bee_productivity"));
            beeAttributes.put(BeeAttributes.ENDURANCE, nbt.contains("bee_endurance"));
            beeAttributes.put(BeeAttributes.TEMPER, nbt.getInt("bee_temper"));
            beeAttributes.put(BeeAttributes.BEHAVIOR, nbt.getInt("bee_behavior"));
            beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, nbt.getInt("bee_weather_tolerance"));
            beeAttributes.put(BeeAttributes.TYPE, nbt.getString("bee_type"));

            for (Map.Entry<BeeAttribute<?>, Object> entry : getAttributes().entrySet()) {
//                ProductiveBees.LOGGER.info(entry.getKey() + " - " + entry.getValue());
            }
        }
    }
}
