package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.GhostlyBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class GhostlyBeeRenderer extends ProductiveBeeRenderer
{
    public GhostlyBeeRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new GhostlyBeeModel<>());
    }

    @Nullable
    @Override
    protected RenderType func_230042_a_(ProductiveBeeEntity entity, boolean b1, boolean b2) {
        return RenderType.getEntityTranslucent(this.getEntityTexture(entity));
    }
}
