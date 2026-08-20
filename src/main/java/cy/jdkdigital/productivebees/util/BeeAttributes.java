package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;

public class BeeAttributes
{
    public static final Identifier HEALTH_MOD_ID_WEAK = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_weak");
    public static final Identifier HEALTH_MOD_ID_MEDIUM = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_medium");
    public static final Identifier HEALTH_MOD_ID_STRONG = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "health_modifier_strong");
    public static final Map<Integer, AttributeModifier> HEALTH_MODS = new HashMap<>();
}
