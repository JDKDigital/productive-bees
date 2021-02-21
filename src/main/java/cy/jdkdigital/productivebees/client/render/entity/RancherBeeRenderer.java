package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.RancherBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public class RancherBeeRenderer extends ProductiveBeeRenderer
{
    public RancherBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new RancherBeeModel<>());
    }
}
