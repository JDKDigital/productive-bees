package cy.jdkdigital.productivebees.compat.geckolib.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import cy.jdkdigital.productivebees.compat.geckolib.client.render.model.GeckoBeeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Calendar;

public class GeckoBeeRenderer extends GeoEntityRenderer<GeckoBee>
{
    protected boolean isChristmas;
    protected boolean isAprilFool;

    public GeckoBeeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoBeeModel());

        Calendar calendar = Calendar.getInstance();
        if (ProductiveBeesConfig.CLIENT.alwaysChristmas.get() || (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 21 && calendar.get(Calendar.DATE) <= 26)) {
            this.isChristmas = true;
        }
        if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1) {
            this.isAprilFool = true;
        }

        addRenderLayer(new JellyLayer(this));
        addRenderLayer(new ColoredLayer(this));
        addRenderLayer(new GlowingLayer(this));
        if (this.isChristmas) {
            addRenderLayer(new ChristmasLayer(this));
        }
    }

    @Override
    public void preRender(PoseStack poseStack, GeckoBee animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (!isReRender) {
            if ((isAprilFool && !animatable.getRenderTransform().equals("flipped")) || (!isAprilFool && animatable.getRenderTransform().equals("flipped"))) {
                poseStack.translate(0.0D, animatable.getBbHeight() + 0.1F, 0.0D);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
        }
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, GeckoBee animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        float beeSize = animatable.getSizeModifier();
        if (animatable.isBaby()) {
            beeSize /= 2;
        }
        super.scaleModelForRender(widthScale * beeSize, heightScale * beeSize, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void postRender(PoseStack poseStack, GeckoBee animatable, BakedGeoModel model, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.model.getBone("stinger").ifPresent(geoBone -> {
            geoBone.setHidden(animatable.hasStung() || animatable.isStingless());
        });
        if (!this.isChristmas) {
            this.model.getBone("santahat").ifPresent(geoBone -> {
                geoBone.setHidden(true);
            });
        }
    }

    static class JellyLayer extends GeoRenderLayer<GeckoBee>
    {
        public JellyLayer(GeoRenderer<GeckoBee> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, GeckoBee animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (!animatable.isInvisible() && animatable.isTranslucent()) {
                renderType = RenderType.entityTranslucent(getTextureResource(animatable));
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, 15728640, packedOverlay, -1);
            }
        }
    }

    static class ColoredLayer extends GeoRenderLayer<GeckoBee>
    {
        public ColoredLayer(GeoRenderer<GeckoBee> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, GeckoBee animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (!animatable.isInvisible() && animatable.isColored()) {
                renderType = RenderType.entityCutoutNoCull(BeeBodyLayer.baseTextures.get(animatable.getRenderer()).get("primary"));
                int primaryColor = animatable.getColor(0, partialTick);
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, 15728640, packedOverlay, primaryColor);

                renderType = RenderType.entityCutoutNoCull(BeeBodyLayer.baseTextures.get(animatable.getRenderer()).get("abdomen"));
                int secondaryColor = animatable.getColor(1, partialTick);
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, 15728640, packedOverlay, secondaryColor);
            }
        }
    }

    static class GlowingLayer extends GeoRenderLayer<GeckoBee>
    {
        public GlowingLayer(GeoRenderer<GeckoBee> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, GeckoBee animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (!animatable.isInvisible() && !animatable.hasBeeTexture() && animatable.useGlowLayer() && !animatable.getRenderStatic() && BeeBodyLayer.baseTextures.get(animatable.getRenderer()).containsKey("glowlayer")) {
                renderType = RenderType.eyes(BeeBodyLayer.baseTextures.get(animatable.getRenderer()).get("glowlayer"));
                int primaryColor = animatable.getTertiaryColor(partialTick);
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, 15728640, packedOverlay, primaryColor);
            }
        }
    }

    static class ChristmasLayer extends GeoRenderLayer<GeckoBee>
    {
        public ChristmasLayer(GeoRenderer<GeckoBee> entityRendererIn) {
            super(entityRendererIn);
        }

        @Override
        public void render(PoseStack poseStack, GeckoBee animatable, BakedGeoModel bakedModel, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
            if (!animatable.isInvisible() && BeeBodyLayer.baseTextures.get(animatable.getRenderer()).containsKey("santahat")) {
                renderType = RenderType.entityCutoutNoCull(BeeBodyLayer.baseTextures.get(animatable.getRenderer()).get("santahat"));
                getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, renderType,
                        bufferSource.getBuffer(renderType), partialTick, 15728640, packedOverlay, -1);
            }
        }
    }
}
