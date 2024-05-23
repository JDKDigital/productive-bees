package cy.jdkdigital.productivebees.capabilities;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BaseCapability;
import net.neoforged.neoforge.capabilities.CapabilityRegistry;

public final class AttributeCapability<T, C> extends BaseCapability<T, C>
{
    private static final CapabilityRegistry<AttributeCapability<?, ?>> registry = new CapabilityRegistry<AttributeCapability<?, ?>>(AttributeCapability::new);

    private AttributeCapability(ResourceLocation name, Class<T> typeClass, Class<C> contextClass) {
        super(name, typeClass, contextClass);
    }
}
