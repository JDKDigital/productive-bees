package cy.jdkdigital.productivebees.capabilities.attributes;

import cy.jdkdigital.productivebees.util.BeeAttribute;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import java.util.Map;

public interface IBeeAttributes
{
    <T> T getAttributeValue(BeeAttribute<T> attribute);

    void setAttributeValue(BeeAttribute<Integer> attribute, int value);

    void setAttributeValue(BeeAttribute<Integer> attribute, String value);

    Map<BeeAttribute<Integer>, Object> getAttributes();
}
