package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.HoarderBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HoarderBeeRenderer extends ProductiveBeeRenderer
{
    public HoarderBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new HoarderBeeModel<>(context.bakeLayer(PB_HOARDER_LAYER)));
    }
}
