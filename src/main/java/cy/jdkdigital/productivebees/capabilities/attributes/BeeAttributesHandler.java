package cy.jdkdigital.productivebees.capabilities.attributes;

import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeAttributesHandler implements IBeeAttributes
{
    protected Map<BeeAttribute<Integer>, Object> beeAttributes = new HashMap<>();

    public <T> T getAttributeValue(BeeAttribute<T> attribute) {
        return (T) this.beeAttributes.get(attribute);
    }

    @Override
    public void setAttributeValue(BeeAttribute<Integer> attribute, int value) {
        beeAttributes.put(attribute, value);
    }

    @Override
    public void setAttributeValue(BeeAttribute<Integer> attribute, String value) {
        beeAttributes.put(attribute, value);
    }

    @Override
    public Map<BeeAttribute<Integer>, Object> getAttributes() {
        return beeAttributes;
    }
}
