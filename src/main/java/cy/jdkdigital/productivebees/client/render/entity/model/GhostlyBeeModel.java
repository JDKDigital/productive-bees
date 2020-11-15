package cy.jdkdigital.productivebees.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;

public class GhostlyBeeModel<T extends ProductiveBeeEntity> extends ProductiveBeeModel<T>
{
    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder renderBuffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(matrixStackIn, renderBuffer, packedLightIn, packedOverlayIn, red, green, blue, 1.0F);
    }
}
