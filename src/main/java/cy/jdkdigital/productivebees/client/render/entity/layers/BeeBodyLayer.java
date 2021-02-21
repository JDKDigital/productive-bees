package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BeeBodyLayer extends LayerRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>>
{
    private final String modelType;
    private final EntityModel<ProductiveBeeEntity> model;
    private boolean isChristmas;

    public BeeBodyLayer(IEntityRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>> rendererIn, String modelType, boolean isChristmas) {
        super(rendererIn);

        this.modelType = modelType;
        model = new ProductiveBeeModel<>(modelType);
        this.isChristmas = isChristmas;
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getRenderer().equals(this.modelType) && !entity.isInvisible()) {
            this.getEntityModel().copyModelAttributesTo(this.model);
            this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setRotationAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            if (entity instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) entity).isTranslucent()) {
                IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.getEntityTranslucent(this.getEntityTexture(entity)));
                this.model.render(matrixStackIn, vertexBuilder, packedLightIn, LivingRenderer.getPackedOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                renderCutoutModel(this.model, this.getEntityTexture(entity), matrixStackIn, bufferIn, packedLightIn, entity, 1.0F, 1.0F, 1.0F);
            }

            if (entity.getColor(0) != null) {
                if (entity instanceof ConfigurableBeeEntity) {
                    if (((ConfigurableBeeEntity) entity).hasBeeTexture()) {
                        return;
                    }
                }

                float[] primaryColor = entity.getColor(0).getComponents(null);
                ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/primary.png");
                renderCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, primaryColor[0], primaryColor[1], primaryColor[2]);

                float[] secondaryColor = entity.getColor(1).getComponents(null);
                ResourceLocation abdomenLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/abdomen.png");
                renderCutoutModel(this.model, abdomenLocation, matrixStackIn, bufferIn, packedLightIn, entity, secondaryColor[0], secondaryColor[1], secondaryColor[2]);

                if (this.modelType.equals("default_crystal")) {
                    float[] color = primaryColor;
                    if (entity instanceof ConfigurableBeeEntity) {
                        color = ((ConfigurableBeeEntity) entity).getTertiaryColor();
                    }
                    // render a color version of the crystal layer
                    ResourceLocation crystalsLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals_clear.png");
                    renderCutoutModel(this.model, crystalsLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
                    if (!entity.getRenderStatic()) {
                        // render glowing layer on top
                        ResourceLocation crystalsOverlayLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals.png");
                        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEyes(crystalsOverlayLocation));
                        this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
                    }
                } else if (this.modelType.equals("default_foliage") || this.modelType.equals("default_shell")) {
                    float[] color = primaryColor;
                    if (entity instanceof ConfigurableBeeEntity) {
                        color = ((ConfigurableBeeEntity) entity).getTertiaryColor();
                    }
                    ResourceLocation foliageLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals.png");
                    renderCutoutModel(this.model, foliageLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
                }
            }

            if (entity.hasNectar()) {
                if (entity.getColor(0) != null) {
                    float[] colors = new float[]{1.0F, 1.0F, 1.0F};
                    if (entity instanceof ConfigurableBeeEntity) {
                        if (((ConfigurableBeeEntity) entity).hasBeeTexture()) {
                            return;
                        }
                        if (((ConfigurableBeeEntity) entity).hasParticleColor()) {
                            colors = ((ConfigurableBeeEntity) entity).getParticleColor();
                        }
                    }

                    ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/pollen.png");
                    renderCutoutModel(this.getEntityModel(), location, matrixStackIn, bufferIn, packedLightIn, entity, colors[0], colors[1], colors[2]);
                }
            }

            if (isChristmas && entity.getColor(0) != null && !entity.getRenderStatic()) {
                if (entity instanceof ConfigurableBeeEntity) {
                    if (((ConfigurableBeeEntity) entity).hasBeeTexture()) {
                        return;
                    }
                }

                ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/santa_hat.png");
                renderCutoutModel(this.getEntityModel(), location, matrixStackIn, bufferIn, 15728640, entity, 1.0f, 1.0f, 1.0f);
            }
        }
    }
}
