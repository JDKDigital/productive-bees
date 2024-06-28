package cy.jdkdigital.productivebees.capabilities.attributes;

import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;

import java.util.Map;

public interface IBeeAttributes
{
    GeneValue getAttributeValue(GeneAttribute attribute);

    void setAttributeValue(GeneAttribute attribute, GeneValue value);

    Map<GeneAttribute, GeneValue> getAttributes();
}
