package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.HoarderBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class HoarderBeeRenderer extends ProductiveBeeRenderer
{
    public HoarderBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HoarderBeeModel<>());
    }
}
