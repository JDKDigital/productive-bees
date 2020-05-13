package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.SolitaryBeeModel;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SolitaryBeeRenderer extends MobRenderer<SolitaryBeeEntity, SolitaryBeeModel<SolitaryBeeEntity>>
{
    public SolitaryBeeRenderer(EntityRendererManager renderManagerIn, SolitaryBeeModel<SolitaryBeeEntity> model) {
        super(renderManagerIn, model, 0.7F);
    }

    public SolitaryBeeRenderer(EntityRendererManager renderManagerIn) {
        this(renderManagerIn, new SolitaryBeeModel<>());
    }

    @Override
    public ResourceLocation getEntityTexture(SolitaryBeeEntity bee) {
        String beeLocation = "bee/" + bee.getBeeType() + "/bee";

        return new ResourceLocation(ProductiveBees.MODID + ":textures/entity/" + beeLocation + ".png");
    }
}
