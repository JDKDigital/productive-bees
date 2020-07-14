package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.RancherBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RancherBeeRenderer extends ProductiveBeeRenderer
{
    public RancherBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new RancherBeeModel<>());
    }
}
