package cy.jdkdigital.productivebees.handler.attributes;

import cy.jdkdigital.productivebees.util.BeeAttribute;
import net.minecraft.nbt.INBT;

import javax.annotation.Nonnull;
import java.util.Map;

public interface IBeeAttributes
{
    void setDefaults();

    <T> T getAttributeValue(BeeAttribute<T> attribute);

    @Nonnull
    INBT getAsNBT();

    void readFromNBT(INBT list);

    void setAttributeValue(BeeAttribute<?> attribute, int value);

    void setAttributeValue(BeeAttribute<?> attribute, String value);

    Map<BeeAttribute<?>, Object> getAttributes();
}
