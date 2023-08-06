package cy.jdkdigital.productivebees.handler.attributes;

import cy.jdkdigital.productivebees.util.BeeAttribute;
import net.minecraft.nbt.Tag;

import javax.annotation.Nonnull;
import java.util.Map;

public interface IBeeAttributes
{
    void setDefaults();

    <T> T getAttributeValue(BeeAttribute<T> attribute);

    @Nonnull
    Tag getAsNBT();

    void readFromNBT(Tag list);

    void setAttributeValue(BeeAttribute<Integer> attribute, int value);

    void setAttributeValue(BeeAttribute<Integer> attribute, String value);

    Map<BeeAttribute<Integer>, Object> getAttributes();
}
