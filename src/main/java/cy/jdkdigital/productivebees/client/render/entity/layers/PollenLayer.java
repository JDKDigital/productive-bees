package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.hive.CupidBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class PollenLayer extends LayerRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>>
{
    public PollenLayer(IEntityRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>> rendererIn) {
        super(rendererIn);
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, ProductiveBeeEntity bee, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (bee.hasNectar()) {
            if (bee.getColor(0) != null) {
                float[] colors = new float[]{1.0F, 1.0F, 1.0F};
                if (bee instanceof ConfigurableBeeEntity) {
                    if (((ConfigurableBeeEntity) bee).hasBeeTexture()) {
                        return;
                    }
                    if (((ConfigurableBeeEntity) bee).hasParticleColor()) {
                        colors = ((ConfigurableBeeEntity) bee).getParticleColor();
                    }
                }

                ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/pollen.png");
                renderCutoutModel(this.getEntityModel(), location, matrixStackIn, bufferIn, packedLightIn, bee, colors[0], colors[1], colors[2]);
            }
        }
    }
}
