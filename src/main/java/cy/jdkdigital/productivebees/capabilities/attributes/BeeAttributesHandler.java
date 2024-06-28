package cy.jdkdigital.productivebees.capabilities.attributes;

import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BeeAttributesHandler implements IBeeAttributes, INBTSerializable<CompoundTag>
{
    protected Map<GeneAttribute, GeneValue> beeAttributes = new HashMap<>();

    public BeeAttributesHandler() {
        Random rand = new Random();
        setAttributeValue(GeneAttribute.PRODUCTIVITY, GeneValue.getRandomProductivity(rand));
        setAttributeValue(GeneAttribute.TEMPER, GeneValue.TEMPER_NORMAL);
        setAttributeValue(GeneAttribute.ENDURANCE, GeneValue.getRandomEndurance(rand));
        setAttributeValue(GeneAttribute.BEHAVIOR, GeneValue.BEHAVIOR_DIURNAL);
        setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_NONE);
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

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.putString("bee_productivity", this.getAttributeValue(GeneAttribute.PRODUCTIVITY).getSerializedName());
        tag.putString("bee_endurance", this.getAttributeValue(GeneAttribute.ENDURANCE).getSerializedName());
        tag.putString("bee_temper", this.getAttributeValue(GeneAttribute.TEMPER).getSerializedName());
        tag.putString("bee_behavior", this.getAttributeValue(GeneAttribute.BEHAVIOR).getSerializedName());
        tag.putString("bee_weather_tolerance", this.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE).getSerializedName());
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        setAttributeValue(GeneAttribute.PRODUCTIVITY, GeneValue.byName(tag.getString("bee_productivity")));
        setAttributeValue(GeneAttribute.ENDURANCE, GeneValue.byName(tag.getString("bee_endurance")));
        setAttributeValue(GeneAttribute.TEMPER, GeneValue.byName(tag.getString("bee_temper")));
        setAttributeValue(GeneAttribute.BEHAVIOR, GeneValue.byName(tag.getString("bee_behavior")));
        setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.byName(tag.getString("bee_weather_tolerance")));
    }
}
