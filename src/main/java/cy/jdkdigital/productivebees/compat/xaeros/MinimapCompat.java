package cy.jdkdigital.productivebees.compat.xaeros;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

public class MinimapCompat
{
    public static void buildVariantIdString(StringBuilder stringBuilder, EntityRenderer entityRenderer, Entity entity) {
        ProductiveBees.LOGGER.info("buildVariantIdString " + entity);
        if (entity instanceof ConfigurableBee configBee) {
            stringBuilder.append(configBee.getBeeType());
        }
    }
}
