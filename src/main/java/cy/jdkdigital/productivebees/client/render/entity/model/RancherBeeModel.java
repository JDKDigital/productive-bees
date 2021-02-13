package cy.jdkdigital.productivebees.client.render.entity.model;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class RancherBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    protected final ModelRenderer hat;

    public RancherBeeModel() {
        super();

        addBodyParts(true);

        hat = new ModelRenderer(this);
        hat.setRotationPoint(2.5F, -4.5F, -5.0F);
        hat.setTextureOffset(25, 8).addBox(-7.0F, 0.0F, -1.0F, 9.0F, 1.0F, 9.0F, 0.0F, false);
        hat.setTextureOffset(29, 12).addBox(-5.0F, -2.0F, 1.0F, 5.0F, 2.0F, 5.0F, 0.0F, false);
        torso.addChild(hat);
    }
}
