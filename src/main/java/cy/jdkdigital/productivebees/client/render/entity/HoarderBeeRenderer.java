package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.HoarderBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoarderBeeRenderer extends ProductiveBeeRenderer
{
    public HoarderBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HoarderBeeModel<>());
    }
}
