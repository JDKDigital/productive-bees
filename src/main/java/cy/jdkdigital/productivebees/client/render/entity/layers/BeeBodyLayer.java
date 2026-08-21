package cy.jdkdigital.productivebees.client.render.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.ProductiveBeeRenderer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.state.ProductiveBeeRenderState;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BeeBodyLayer extends RenderLayer<ProductiveBeeRenderState, ProductiveBeeModel<ProductiveBeeRenderState>>
{
    private final String modelType;
    private Map<String, Identifier> textures;
    private final ProductiveBeeModel<ProductiveBeeRenderState> model;
    private final boolean isChristmas;
    private final ProductiveBeeRenderer parent;

    public static Map<String, Map<String, Identifier>> baseTextures = new HashMap<>() {{
        put("default", new HashMap<>() {{
            put("primary", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default/primary.png"));
            put("abdomen", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default/abdomen.png"));
            put("glowlayer", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default/primary.png"));
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default/santa_hat.png"));
        }});
        put("default_crystal", new HashMap<>() {{
            put("primary", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/primary.png"));
            put("abdomen", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/abdomen.png"));
            put("glowlayer", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/crystals.png"));
            put("crystals", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/crystals.png"));
            put("crystals_clear", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/crystals_clear.png"));
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_crystal/santa_hat.png"));
        }});
        put("default_foliage", new HashMap<>() {{
            put("primary", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/primary.png"));
            put("abdomen", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/abdomen.png"));
            put("glowlayer", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/crystals.png"));
            put("crystals", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/crystals.png"));
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_foliage/santa_hat.png"));
        }});
        put("default_shell", new HashMap<>() {{
            put("primary", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/primary.png"));
            put("abdomen", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/abdomen.png"));
            put("glowlayer", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/crystals.png"));
            put("crystals", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/crystals.png"));
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/default_shell/santa_hat.png"));
        }});
        put("thicc", new HashMap<>() {{
            put("primary", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/thicc/primary.png"));
            put("abdomen", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/thicc/abdomen.png"));
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/thicc/santa_hat.png"));
        }});
        put("translucent_with_center", new HashMap<>() {{
            put("santahat", Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/base/translucent_with_center/santa_hat.png"));
        }});
    }};

    public BeeBodyLayer(ProductiveBeeRenderer rendererIn, ModelPart layer, String modelType, boolean isChristmas) {
        super(rendererIn);

        this.modelType = modelType;
        this.model = new ProductiveBeeModel<>(layer, modelType);
        this.isChristmas = isChristmas;
        this.parent = rendererIn;
    }

    /** Texture set for this layer's model, falling back to the default set for an unknown renderer name. */
    private Map<String, Identifier> textures() {
        if (this.textures == null) {
            this.textures = baseTextures.getOrDefault(this.modelType, baseTextures.get("default"));
        }
        return this.textures;
    }

    @Override
    public void submit(@Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector collector, int packedLight, @Nonnull ProductiveBeeRenderState state, float yRot, float xRot) {
        if (state.renderType.equals(this.modelType) && !state.isInvisible) {
            this.model.setupAnim(state);
            this.model.hideSantaHat(!isChristmas || state.renderStatic);

            int order = 0;
            Identifier baseTexture = this.parent.getTextureLocation(state);
            if (state.isTranslucent) {
                collector.order(order++).submitModel(this.model, state, poseStack, RenderTypes.entityTranslucent(baseTexture), packedLight, OverlayTexture.NO_OVERLAY, -1, null, state.outlineColor, null);
            } else {
                renderColoredCutoutModel(this.model, baseTexture, poseStack, collector, packedLight, state, -1, order++);
            }

            if (state.isColored) {
                order = renderColoredLayers(poseStack, collector, packedLight, state, order);
            } else if (state.isConfigurable && this.modelType.equals("default_crystal") && state.useGlowLayer) {
                order = renderCrystalLayer(poseStack, collector, packedLight, state, order);
            }

            if (state.hasNectar && !state.hasConverted) {
                order = renderNectarLayer(poseStack, collector, packedLight, state, order);
            }

            order = renderChristmasHat(poseStack, collector, packedLight, state, order);

            renderSaddle(poseStack, collector, packedLight, state, order);
        }
    }

    private int renderColoredLayers(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ProductiveBeeRenderState state, int order) {
        Identifier location = textures().get("primary");
        renderColoredCutoutModel(this.model, location, poseStack, collector, packedLight, state, state.primaryColor, order++);

        Identifier abdomenLocation = textures().get("abdomen");
        renderColoredCutoutModel(this.model, abdomenLocation, poseStack, collector, packedLight, state, state.secondaryColor, order++);

        if (this.modelType.equals("default_crystal")) {
            return renderCrystalLayer(poseStack, collector, packedLight, state, order);
        } else if (this.modelType.equals("default_foliage") || this.modelType.equals("default_shell")) {
            int color = state.isConfigurable ? state.tertiaryColor : state.primaryColor;
            Identifier foliageLocation = textures().get("crystals");
            renderColoredCutoutModel(this.model, foliageLocation, poseStack, collector, packedLight, state, color, order++);
        }
        return order;
    }

    private int renderCrystalLayer(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ProductiveBeeRenderState state, int order) {
        int color = state.isConfigurable ? state.tertiaryColor : state.primaryColor;
        boolean useGlowLayer = !state.renderStatic && (!state.isConfigurable || state.useGlowLayer);
        Identifier crystalsLocation = textures().get("crystals_clear");
        renderColoredCutoutModel(this.model, crystalsLocation, poseStack, collector, packedLight, state, color, order++);
        if (useGlowLayer) {
            Identifier crystalsOverlayLocation = textures().get("crystals");
            collector.order(order++).submitModel(this.model, state, poseStack, RenderTypes.eyes(crystalsOverlayLocation), packedLight, OverlayTexture.NO_OVERLAY, color, null, state.outlineColor, null);
        }
        return order;
    }

    private int renderNectarLayer(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ProductiveBeeRenderState state, int order) {
        if (state.isColored) {
            if (state.hasBeeTexture) {
                return order;
            }
            int colors = state.hasParticleColor ? state.particleColor : -1;

            Identifier location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/base/" + this.modelType + "/pollen.png");
            renderColoredCutoutModel(this.model, location, poseStack, collector, packedLight, state, colors, order++);
        }
        return order;
    }

    private int renderChristmasHat(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ProductiveBeeRenderState state, int order) {
        if (isChristmas && !state.renderStatic && this.modelType.contains("default")) {
            Identifier location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/base/" + this.modelType + "/santa_hat.png");
            renderColoredCutoutModel(this.model, location, poseStack, collector, packedLight, state, -1, order++);
        }
        return order;
    }

    private int renderSaddle(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ProductiveBeeRenderState state, int order) {
        if (state.isBumbleBee && state.isSaddled) {
            Identifier location = ProductiveBeeRenderer.resLoc(ProductiveBees.MODID + ":textures/entity/bee/bumble" + (state.isBlehBumbleBee ? "_bleh" : "") + "/saddle.png");
            renderColoredCutoutModel(this.model, location, poseStack, collector, packedLight, state, -1, order++);
        }
        return order;
    }
}
