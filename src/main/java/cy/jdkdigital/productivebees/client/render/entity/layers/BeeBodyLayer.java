package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBeeEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeBodyLayer extends LayerRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>>
{
    private final String modelType;
    private final EntityModel<ProductiveBeeEntity> model;
    private boolean isChristmas;
    private Map<String, ResourceLocation> resLocCache = new HashMap<>();

    public BeeBodyLayer(IEntityRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>> rendererIn, String modelType, boolean isChristmas) {
        super(rendererIn);

        this.modelType = modelType;
        model = new ProductiveBeeModel<>(modelType);
        this.isChristmas = isChristmas;
    }

    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getRenderer().equals(this.modelType) && !entity.isInvisible()) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            if (entity instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) entity).isTranslucent()) {
                IVertexBuilder vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
                this.model.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, LivingRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                renderColoredCutoutModel(this.model, this.getTextureLocation(entity), matrixStackIn, bufferIn, packedLightIn, entity, 1.0F, 1.0F, 1.0F);
            }

            if (entity.getColor(0) != null) {
                if (entity instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) entity).hasBeeTexture()) {
                    if (this.modelType.equals("default_crystal") && ((ConfigurableBeeEntity) entity).useGlowLayer()) {
                        renderCrystalLayer(matrixStackIn, bufferIn, packedLightIn, entity);
                    }
                    return;
                }
                renderColoredLayers(matrixStackIn, bufferIn, packedLightIn, entity);
            }

            if (entity.hasNectar() && !entity.hasConverted()) {
                renderNectarLayer(matrixStackIn, bufferIn, packedLightIn, entity);
            }

            renderChristmasHat(matrixStackIn, bufferIn, packedLightIn, entity);

            renderSaddle(matrixStackIn, bufferIn, packedLightIn, entity);
        }
    }

    private void renderColoredLayers(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity) {
        float[] primaryColor = entity.getColor(0).getComponents(null);
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/primary.png");
        renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, primaryColor[0], primaryColor[1], primaryColor[2]);

        float[] secondaryColor = entity.getColor(1).getComponents(null);
        ResourceLocation abdomenLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/abdomen.png");
        renderColoredCutoutModel(this.model, abdomenLocation, matrixStackIn, bufferIn, packedLightIn, entity, secondaryColor[0], secondaryColor[1], secondaryColor[2]);

        if (this.modelType.equals("default_crystal")) {
            renderCrystalLayer(matrixStackIn, bufferIn, packedLightIn, entity);
        } else if (this.modelType.equals("default_foliage") || this.modelType.equals("default_shell")) {
            float[] color = primaryColor;
            if (entity instanceof ConfigurableBeeEntity) {
                color = ((ConfigurableBeeEntity) entity).getTertiaryColor();
            }
            ResourceLocation foliageLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals.png");
            renderColoredCutoutModel(this.model, foliageLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
        }
    }

    private void renderCrystalLayer(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, ProductiveBeeEntity entity) {
        float[] color = entity.getColor(0).getComponents(null);
        boolean useGlowLayer = !entity.getRenderStatic();
        if (entity instanceof ConfigurableBeeEntity) {
            color = ((ConfigurableBeeEntity) entity).getTertiaryColor();
            useGlowLayer = useGlowLayer && ((ConfigurableBeeEntity) entity).useGlowLayer();
        }
        // render a color version of the crystal layer
        ResourceLocation crystalsLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals_clear.png");
        renderColoredCutoutModel(this.model, crystalsLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
        if (useGlowLayer) {
            // render glowing layer on top
            ResourceLocation crystalsOverlayLocation = new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/" + this.modelType + "/crystals.png");
            IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(crystalsOverlayLocation));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
        }
    }

    private void renderNectarLayer(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity) {
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

            ResourceLocation location = resLoc("textures/entity/bee/base/" + this.modelType + "/pollen.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, colors[0], colors[1], colors[2]);
        }
    }

    private void renderChristmasHat(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity) {
        if (isChristmas && entity.getColor(0) != null && !entity.getRenderStatic()) {
            if (entity instanceof ConfigurableBeeEntity) {
                if (((ConfigurableBeeEntity) entity).hasBeeTexture()) {
                    return;
                }
            }

            ResourceLocation location = resLoc("textures/entity/bee/base/" + this.modelType + "/santa_hat.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, 1.0f, 1.0f, 1.0f);
        }
    }

    private void renderSaddle(@Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn, @Nonnull ProductiveBeeEntity entity) {
        if (entity instanceof BumbleBeeEntity && ((BumbleBeeEntity) entity).isSaddled()) {
            ResourceLocation location = resLoc("textures/entity/bee/bumble/saddle.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, 1.0f, 1.0f, 1.0f);
        }
    }

    private ResourceLocation resLoc(String key) {
        if (!resLocCache.containsKey(key)) {
            resLocCache.put(key, new ResourceLocation(ProductiveBees.MODID, key));
        }
        return resLocCache.get(key);
    }
}
