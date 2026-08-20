package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.HoarderBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.state.ProductiveBeeRenderState;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBee;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HoarderBeeRenderer extends ProductiveBeeRenderer
{
    public HoarderBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new HoarderBeeModel<>(context.bakeLayer(PB_HOARDER_LAYER)));
    }

    @Override
    public void extractRenderState(ProductiveBee bee, ProductiveBeeRenderState state, float partialTick) {
        super.extractRenderState(bee, state, partialTick);
        if (bee instanceof HoarderBee hoarderBee) {
            float time = state.ageInTicks - (float) hoarderBee.tickCount;
            state.hoarderPeekAmount = hoarderBee.getClientPeekAmount(time);
        }
    }
}
