package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBee;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeBodyLayer extends RenderLayer<ProductiveBee, ProductiveBeeModel<ProductiveBee>>
{
    private final String modelType;
    private final EntityModel<ProductiveBee> model;
    private final boolean isChristmas;

    Map<String, Map<String, ResourceLocation>> baseTextures = new HashMap<>() {{
        put("default", new HashMap<>() {{
            put("primary", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default/primary.png"));
            put("abdomen", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default/abdomen.png"));
        }});
        put("default_crystal", new HashMap<>() {{
            put("primary", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/primary.png"));
            put("abdomen", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/abdomen.png"));
            put("crystals", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/crystals.png"));
            put("crystals_clear", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/crystals_clear.png"));
        }});
        put("default_foliage", new HashMap<>() {{
            put("primary", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/primary.png"));
            put("abdomen", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/abdomen.png"));
            put("crystals", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/crystals.png"));
        }});
        put("default_shell", new HashMap<>() {{
            put("primary", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/primary.png"));
            put("abdomen", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/abdomen.png"));
            put("crystals", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/crystals.png"));
        }});
        put("thicc", new HashMap<>() {{
            put("primary", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/thicc/primary.png"));
            put("abdomen", new ResourceLocation(ProductiveBees.MODID, "textures/entity/bee/base/thicc/abdomen.png"));
        }});
    }};

    public BeeBodyLayer(RenderLayerParent<ProductiveBee, ProductiveBeeModel<ProductiveBee>> rendererIn, ModelPart layer, String modelType, boolean isChristmas) {
        super(rendererIn);

        this.modelType = modelType;
        model = new ProductiveBeeModel<>(layer, modelType);
        this.isChristmas = isChristmas;
    }

    public void render(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull ProductiveBee entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getRenderer().equals(this.modelType) && !entity.isInvisible()) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            if (entity instanceof ConfigurableBee && ((ConfigurableBee) entity).isTranslucent()) {
                VertexConsumer vertexBuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
                this.model.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                renderColoredCutoutModel(this.model, this.getTextureLocation(entity), matrixStackIn, bufferIn, packedLightIn, entity, 1.0F, 1.0F, 1.0F);
            }

            if (entity.isColored()) {
                if (entity instanceof ConfigurableBee && ((ConfigurableBee) entity).hasBeeTexture()) {
                    if (this.modelType.equals("default_crystal") && ((ConfigurableBee) entity).useGlowLayer()) {
                        renderCrystalLayer(matrixStackIn, bufferIn, packedLightIn, entity, partialTicks);
                    }
                    return;
                }
                renderColoredLayers(matrixStackIn, bufferIn, packedLightIn, entity, partialTicks);
            }

            if (entity.hasNectar() && !entity.hasConverted()) {
                renderNectarLayer(matrixStackIn, bufferIn, packedLightIn, entity);
            }

            renderChristmasHat(matrixStackIn, bufferIn, packedLightIn, entity);

            renderSaddle(matrixStackIn, bufferIn, packedLightIn, entity);
        }
    }

    private void renderColoredLayers(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull ProductiveBee entity, float partialTicks) {
        float[] primaryColor = entity.getColor(0, partialTicks);
        ResourceLocation location = baseTextures.get(this.modelType).get("primary");
        renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, primaryColor[0], primaryColor[1], primaryColor[2]);

        float[] secondaryColor = entity.getColor(1, partialTicks);
        ResourceLocation abdomenLocation = baseTextures.get(this.modelType).get("abdomen");
        renderColoredCutoutModel(this.model, abdomenLocation, matrixStackIn, bufferIn, packedLightIn, entity, secondaryColor[0], secondaryColor[1], secondaryColor[2]);

        if (this.modelType.equals("default_crystal")) {
            renderCrystalLayer(matrixStackIn, bufferIn, packedLightIn, entity, partialTicks);
        } else if (this.modelType.equals("default_foliage") || this.modelType.equals("default_shell")) {
            float[] color = primaryColor;
            if (entity instanceof ConfigurableBee) {
                color = ((ConfigurableBee) entity).getTertiaryColor(partialTicks);
            }
            ResourceLocation foliageLocation = baseTextures.get(this.modelType).get("crystals");
            renderColoredCutoutModel(this.model, foliageLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
        }
    }

    private void renderCrystalLayer(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, ProductiveBee entity, float partialTicks) {
        float[] color = entity.getColor(0, partialTicks);
        boolean useGlowLayer = !entity.getRenderStatic();
        if (entity instanceof ConfigurableBee) {
            color = ((ConfigurableBee) entity).getTertiaryColor(partialTicks);
            useGlowLayer = useGlowLayer && ((ConfigurableBee) entity).useGlowLayer();
        }
        // render a color version of the crystal layer
        ResourceLocation crystalsLocation = baseTextures.get(this.modelType).get("crystals_clear");
        renderColoredCutoutModel(this.model, crystalsLocation, matrixStackIn, bufferIn, packedLightIn, entity, color[0], color[1], color[2]);
        if (useGlowLayer) {
            // render glowing layer on top
            ResourceLocation crystalsOverlayLocation = baseTextures.get(this.modelType).get("crystals");
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(crystalsOverlayLocation));
            this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, color[0], color[1], color[2], 1.0F);
        }
    }

    private void renderNectarLayer(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull ProductiveBee entity) {
        if (entity.isColored()) {
            float[] colors = new float[]{1.0F, 1.0F, 1.0F};
            if (entity instanceof ConfigurableBee) {
                if (((ConfigurableBee) entity).hasBeeTexture()) {
                    return;
                }
                if (((ConfigurableBee) entity).hasParticleColor()) {
                    colors = ((ConfigurableBee) entity).getParticleColor();
                }
            }

            ResourceLocation location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/base/" + this.modelType + "/pollen.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, colors[0], colors[1], colors[2]);
        }
    }

    private void renderChristmasHat(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull ProductiveBee entity) {
        if (isChristmas && entity.isColored() && !entity.getRenderStatic()) {
            if (entity instanceof ConfigurableBee) {
                if (((ConfigurableBee) entity).hasBeeTexture()) {
                    return;
                }
            }

            ResourceLocation location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/base/" + this.modelType + "/santa_hat.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, 1.0f, 1.0f, 1.0f);
        }
    }

    private void renderSaddle(@Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn, @Nonnull ProductiveBee entity) {
        if (entity instanceof BumbleBee bumbleBee && bumbleBee.isSaddled()) {
            ResourceLocation location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/bumble" + (bumbleBee.hasCustomName() && bumbleBee.getCustomName().getString().equals("Bleh") ? "_bleh" : "") + "/saddle.png");
            renderColoredCutoutModel(this.model, location, matrixStackIn, bufferIn, packedLightIn, entity, 1.0f, 1.0f, 1.0f);
        }
    }
}
