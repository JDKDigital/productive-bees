package cy.jdkdigital.productivebees.capabilities;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.capabilities.attributes.IBeeAttributes;
import cy.jdkdigital.productivebees.capabilities.bee.IInhabitantStorage;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;

public final class BeeCapabilities
{
    public static final class AttributeHandler
    {
        public static final EntityCapability<IBeeAttributes, Void> ENTITY = EntityCapability.createVoid(new ResourceLocation(ProductiveBees.MODID, "attribute_handler"), IBeeAttributes.class);

        private AttributeHandler() {}
    }

    public static final class InhabitantHandler
    {
        public static final BlockCapability<IInhabitantStorage, Void> BLOCK = BlockCapability.createVoid(new ResourceLocation(ProductiveBees.MODID, "inhabitant_handler"), IInhabitantStorage.class);

        private InhabitantHandler() {}
    }
}
