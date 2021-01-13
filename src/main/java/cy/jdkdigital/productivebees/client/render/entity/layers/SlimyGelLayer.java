package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.client.render.entity.model.SlimyBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;

public class SlimyGelLayer <T extends ProductiveBeeEntity> extends LayerRenderer<T, SlimyBeeModel<T>>
{
    private final EntityModel<T> slimyBeeModel = new SlimyBeeModel<T>(true);

    public SlimyGelLayer(IEntityRenderer<T, SlimyBeeModel<T>> model) {
        super(model);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int p_225628_3_, T beeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (!beeEntity.isInvisible()) {
            this.getEntityModel().copyModelAttributesTo(this.slimyBeeModel);
            this.slimyBeeModel.setLivingAnimations(beeEntity, limbSwing, limbSwingAmount, partialTicks);
            this.slimyBeeModel.setRotationAngles(beeEntity, limbSwing, limbSwingAmount, p_225628_8_, p_225628_9_, p_225628_10_);
            IVertexBuilder vertexBuilder = renderBuffer.getBuffer(RenderType.getEntityTranslucent(this.getEntityTexture(beeEntity)));
            this.slimyBeeModel.render(matrixStack, vertexBuilder, p_225628_3_, LivingRenderer.getPackedOverlay(beeEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
