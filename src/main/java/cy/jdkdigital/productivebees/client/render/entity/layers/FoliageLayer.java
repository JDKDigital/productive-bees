package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class FoliageLayer<T extends ProductiveBeeEntity> extends LayerRenderer<T, ProductiveBeeModel<T>>
{
    public FoliageLayer(IEntityRenderer<T, ProductiveBeeModel<T>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T bee, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!bee.isChild() && !bee.isInvisible()) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState blockstate = Blocks.BROWN_MUSHROOM.getDefaultState();
            int i = LivingRenderer.getPackedOverlay(bee, 0.0F);

            this.getEntityModel().getBody().translateRotate(matrixStackIn);

            matrixStackIn.push();
            matrixStackIn.translate(0.2F, -0.25F, 0.0D);
//            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStackIn.scale(-0.35F, -0.35F, 0.35F);
//            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            blockrendererdispatcher.renderBlock(Blocks.BROWN_MUSHROOM.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, i);
            matrixStackIn.pop();

            matrixStackIn.push();
            matrixStackIn.translate(0.1F, -0.25F, -0.25F);
//            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(42.0F));
//            matrixStackIn.translate(0.1F, 0.0D, -0.6F);
//            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStackIn.scale(-0.45F, -0.45F, 0.45F);
//            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            blockrendererdispatcher.renderBlock(Blocks.RED_MUSHROOM.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, i);
            matrixStackIn.pop();
//
            matrixStackIn.push();
            matrixStackIn.translate(0.0D, -0.25F, -0.4F);
//            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-78.0F));
            matrixStackIn.scale(-0.35F, -0.35F, 0.35F);
//            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            blockrendererdispatcher.renderBlock(Blocks.BAMBOO_SAPLING.getDefaultState(), matrixStackIn, bufferIn, packedLightIn, i);
            matrixStackIn.pop();
        }
    }
}
