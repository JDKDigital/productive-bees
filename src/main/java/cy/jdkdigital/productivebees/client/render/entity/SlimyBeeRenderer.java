package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.layers.SlimyGelLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.SlimyBeeModel;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimyBeeRenderer extends ProductiveBeeRenderer
{
    public SlimyBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new SlimyBeeModel<>(false));
        this.addLayer(new SlimyGelLayer(this));
    }
}
