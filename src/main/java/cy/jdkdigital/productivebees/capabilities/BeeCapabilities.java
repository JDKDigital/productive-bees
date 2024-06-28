package cy.jdkdigital.productivebees.capabilities;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.capabilities.attributes.IBeeAttributes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class BeeCapabilities
{
    public static final class AttributeHandler
    {
        public static final EntityCapability<IBeeAttributes, Void> ENTITY = EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "attribute_handler"), IBeeAttributes.class);

        private AttributeHandler() {}
    }
}
