package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.RancherBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class RancherBeeRenderer extends ProductiveBeeRenderer
{
    public RancherBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new RancherBeeModel<>(context.bakeLayer(PB_RANCHER_LAYER)));
    }
}
