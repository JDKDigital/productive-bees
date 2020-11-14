package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.client.render.entity.model.HatModel;
import cy.jdkdigital.productivebees.client.render.entity.model.IHasBeeHat;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SantaHatLayer<T extends ProductiveBeeEntity, M extends ProductiveBeeModel<T> & IHasBeeHat> extends LayerRenderer<T, M>
{
    public SantaHatLayer(IEntityRenderer<T, M> rendererIn) {
        super(rendererIn);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int p_225628_3_, T beeEntity, float limbSwing, float limbSwingAmount, float partialTicks, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        matrixStack.push();
        if (this.getEntityModel().isChild) {
            matrixStack.translate(0.0D, 0.75D, 0.0D);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
        }


        matrixStack.pop();

        if (!beeEntity.isInvisible()) {
            this.getEntityModel().getModelHat().translateRotate(matrixStack);

//            this.getEntityModel().copyModelAttributesTo(this.model);
//            this.model.setLivingAnimations(beeEntity, limbSwing, limbSwingAmount, partialTicks);
//            this.model.setRotationAngles(beeEntity, limbSwing, limbSwingAmount, p_225628_8_, p_225628_9_, p_225628_10_);
//            IVertexBuilder vertexBuilder = renderBuffer.getBuffer(RenderType.getEntityTranslucent(this.getEntityTexture(beeEntity)));
//            this.model.render(matrixStack, vertexBuilder, p_225628_3_, LivingRenderer.getPackedOverlay(beeEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
