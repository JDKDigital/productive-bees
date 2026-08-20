package cy.jdkdigital.productivebees.compat.geckolib.client.render;

import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import cy.jdkdigital.productivebees.compat.geckolib.client.render.model.GeckoBeeModel;
import cy.jdkdigital.productivebees.compat.geckolib.client.render.state.GeckoBeeRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.Calendar;
import java.util.Map;

/**
 * Geckolib 5.x renderer for {@link GeckoBee}. The pre-26.1 renderer hooked
 * {@code preRender/postRender/scaleModelForRender} and emitted re-renders via {@code reRender(...)}
 * inside {@code GeoRenderLayer.render}. None of that API exists in 5.x — the render path is now
 * state-driven (extract once, build a {@link RenderPassInfo}, submit nodes).
 *
 * <p>The state extraction in {@link #extractRenderState} captures every per-bee thing the model
 * and layers need (model id, texture id, base layer textures, colours, flags). Bones are hidden in
 * {@link #postRenderPass} matching the 4.x semantics — the model is shared, so we toggle hidden
 * state per-render-pass.
 */
public class GeckoBeeRenderer extends GeoEntityRenderer<GeckoBee, GeckoBeeRenderState>
{
    private final boolean isChristmas;
    private final boolean isAprilFool;

    public GeckoBeeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeckoBeeModel());

        Calendar calendar = Calendar.getInstance();
        boolean christmasWindow = ProductiveBeesConfig.CLIENT.alwaysChristmas.get()
                || (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 21 && calendar.get(Calendar.DATE) <= 26);
        this.isChristmas = christmasWindow && !ProductiveBeesConfig.CLIENT.neverChristmas.get();
        this.isAprilFool = (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1)
                && ProductiveBeesConfig.GENERAL.enableJokes.get();

        withRenderLayer(new JellyLayer(this));
        withRenderLayer(new ColoredLayer(this));
        withRenderLayer(new GlowingLayer(this));
        if (this.isChristmas) {
            withRenderLayer(new ChristmasLayer(this));
        }
    }

    @Override
    public GeckoBeeRenderState createRenderState(GeckoBee bee, Void instance) {
        return new GeckoBeeRenderState();
    }

    @Override
    public void extractRenderState(GeckoBee bee, GeckoBeeRenderState state, float partialTick) {
        super.extractRenderState(bee, state, partialTick);

        state.isInvisible = bee.isInvisible();
        state.isBaby = bee.isBaby();
        state.hasStung = bee.hasStung();
        state.isStingless = bee.isStingless();
        state.isTranslucent = bee.isTranslucent();
        state.isColored = bee.isColored();
        state.hasBeeTexture = bee.hasBeeTexture();
        state.useGlowLayer = bee.useGlowLayer();
        state.renderStatic = bee.getRenderStatic();
        state.renderType = bee.getRenderer();
        state.renderTransform = bee.getRenderTransform();
        state.sizeModifier = bee.getSizeModifier();
        state.primaryColor = bee.getColor(0, partialTick);
        state.secondaryColor = bee.getColor(1, partialTick);
        state.tertiaryColor = bee.getTertiaryColor(partialTick);
        state.aprilFool = isAprilFool;
        state.christmasMode = isChristmas;

        state.modelLocation = bee.getModelLocation();
        state.textureLocation = bee.getTextureLocation();

        // Snapshot the per-layer base textures so layers don't have to peek into the live map on
        // the render thread.
        Map<String, Identifier> textures = BeeBodyLayer.baseTextures.get(state.renderType);
        if (textures != null) {
            state.primaryTexture = textures.get("primary");
            state.abdomenTexture = textures.get("abdomen");
            state.glowTexture = textures.get("glowlayer");
            state.santahatTexture = textures.get("santahat");
        }
    }

    @Override
    public int getRenderColor(GeckoBee bee, Void instance, float partialTick) {
        // Base render-pass colour. Layers override via DataTickets.RENDER_COLOR when they need a
        // different tint.
        return -1;
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<GeckoBeeRenderState> passInfo, float widthScale, float heightScale) {
        GeckoBeeRenderState state = passInfo.renderState();
        float beeSize = state.sizeModifier;
        if (state.isBaby) {
            beeSize /= 2;
        }
        super.scaleModelForRender(passInfo, widthScale * beeSize, heightScale * beeSize);
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeckoBeeRenderState> passInfo) {
        super.adjustRenderPose(passInfo);

        GeckoBeeRenderState state = passInfo.renderState();
        boolean flipped = state.renderTransform != null && state.renderTransform.equals("flipped");
        if (state.aprilFool != flipped) {
            PoseStack pose = passInfo.poseStack();
            pose.translate(0.0D, 0.7D, 0.0D);
            pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Override
    public void adjustModelBonesForRender(RenderPassInfo<GeckoBeeRenderState> passInfo, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(passInfo, snapshots);

        GeckoBeeRenderState state = passInfo.renderState();
        snapshots.ifPresent("stinger", s -> s.skipRender(state.hasStung || state.isStingless));
        snapshots.ifPresent("santahat", s -> s.skipRender(!state.christmasMode));
    }

    private static final class JellyLayer extends GeoRenderLayer<GeckoBee, Void, GeckoBeeRenderState>
    {
        JellyLayer(GeoEntityRenderer<GeckoBee, GeckoBeeRenderState> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<GeckoBeeRenderState> passInfo, SubmitNodeCollector collector) {
            GeckoBeeRenderState state = passInfo.renderState();
            if (state.isInvisible || !state.isTranslucent) {
                return;
            }
            RenderType rt = RenderTypes.entityTranslucent(state.textureLocation);
            getRenderer().submitRenderTasks(passInfo, collector.order(1), rt);
        }
    }

    /** Tints the base + abdomen textures with the bee's primary / secondary colours. */
    private static final class ColoredLayer extends GeoRenderLayer<GeckoBee, Void, GeckoBeeRenderState>
    {
        ColoredLayer(GeoEntityRenderer<GeckoBee, GeckoBeeRenderState> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<GeckoBeeRenderState> passInfo, SubmitNodeCollector collector) {
            GeckoBeeRenderState state = passInfo.renderState();
            if (state.isInvisible || !state.isColored || state.primaryTexture == null || state.abdomenTexture == null) {
                return;
            }
            // RENDER_COLOR is read fresh from state inside submitRenderTasks, so toggle it around
            // each sub-pass. Restore after so unrelated layers see the renderer-supplied default.
            int saved = passInfo.renderColor();

            state.addGeckolibData(DataTickets.RENDER_COLOR, state.primaryColor);
            getRenderer().submitRenderTasks(passInfo, collector.order(1), RenderTypes.entityCutout(state.primaryTexture));

            state.addGeckolibData(DataTickets.RENDER_COLOR, state.secondaryColor);
            getRenderer().submitRenderTasks(passInfo, collector.order(2), RenderTypes.entityCutout(state.abdomenTexture));

            state.addGeckolibData(DataTickets.RENDER_COLOR, saved);
        }
    }

    /** Eyes-layer glow tinted by the tertiary colour, gated by the same flags as the 4.x impl. */
    private static final class GlowingLayer extends GeoRenderLayer<GeckoBee, Void, GeckoBeeRenderState>
    {
        GlowingLayer(GeoEntityRenderer<GeckoBee, GeckoBeeRenderState> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<GeckoBeeRenderState> passInfo, SubmitNodeCollector collector) {
            GeckoBeeRenderState state = passInfo.renderState();
            if (state.isInvisible || state.hasBeeTexture || !state.useGlowLayer || state.renderStatic || state.glowTexture == null) {
                return;
            }
            int saved = passInfo.renderColor();
            state.addGeckolibData(DataTickets.RENDER_COLOR, state.tertiaryColor);
            getRenderer().submitRenderTasks(passInfo, collector.order(3), RenderTypes.eyes(state.glowTexture));
            state.addGeckolibData(DataTickets.RENDER_COLOR, saved);
        }
    }

    /** Renders the santahat overlay when the christmas window is active. */
    private static final class ChristmasLayer extends GeoRenderLayer<GeckoBee, Void, GeckoBeeRenderState>
    {
        ChristmasLayer(GeoEntityRenderer<GeckoBee, GeckoBeeRenderState> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<GeckoBeeRenderState> passInfo, SubmitNodeCollector collector) {
            GeckoBeeRenderState state = passInfo.renderState();
            if (state.isInvisible || state.santahatTexture == null) {
                return;
            }
            getRenderer().submitRenderTasks(passInfo, collector.order(4), RenderTypes.entityCutout(state.santahatTexture));
        }
    }

}
