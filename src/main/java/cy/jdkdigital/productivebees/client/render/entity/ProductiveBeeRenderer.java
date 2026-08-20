package cy.jdkdigital.productivebees.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.client.render.entity.state.ProductiveBeeRenderState;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BumbleBee;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProductiveBeeRenderer extends MobRenderer<ProductiveBee, ProductiveBeeRenderState, ProductiveBeeModel<ProductiveBeeRenderState>>
{
    private static Map<String, Identifier> resLocCache = new HashMap<>();

    public static final ModelLayerLocation PB_MAIN_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "main"), "main");
    public static final ModelLayerLocation PB_HOARDER_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "hoarder"), "main");
    public static final ModelLayerLocation PB_RANCHER_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "rancher"), "main");
    public static final ModelLayerLocation PB_THICC_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "thicc"), "main");
    public static final ModelLayerLocation PB_DEFAULT_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default"), "main");
    public static final ModelLayerLocation PB_DEFAULT_CRYSTAL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default_crystal"), "main");
    public static final ModelLayerLocation PB_DEFAULT_SHELL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default_shell"), "main");
    public static final ModelLayerLocation PB_DEFAULT_FOLIAGE_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default_foliage"), "main");
    public static final ModelLayerLocation PB_ELVIS_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "elvis"), "main");
    public static final ModelLayerLocation PB_SMALL_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "small"), "main");
    public static final ModelLayerLocation PB_SLIM_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "slim"), "main");
    public static final ModelLayerLocation PB_TINY_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "tiny"), "main");
    public static final ModelLayerLocation PB_SLIMY_LAYER = new ModelLayerLocation(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "translucent_with_center"), "main");

    protected boolean isChristmas;
    protected boolean isAprilFool;

    public ProductiveBeeRenderer(EntityRendererProvider.Context context) {
        this(context, new ProductiveBeeModel<>(context.bakeLayer(PB_MAIN_LAYER)));

        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_THICC_LAYER), "thicc", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_DEFAULT_LAYER), "default", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_DEFAULT_CRYSTAL_LAYER), "default_crystal", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_DEFAULT_SHELL_LAYER), "default_shell", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_DEFAULT_FOLIAGE_LAYER), "default_foliage", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_ELVIS_LAYER), "elvis", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_SMALL_LAYER), "small", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_SLIM_LAYER), "slim", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_TINY_LAYER), "tiny", isChristmas));
        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_SLIMY_LAYER), "translucent_with_center", isChristmas));
    }

    public ProductiveBeeRenderer(EntityRendererProvider.Context context, ProductiveBeeModel<ProductiveBeeRenderState> model) {
        super(context, model, 0.4F);

        Calendar calendar = Calendar.getInstance();
        if (ProductiveBeesConfig.CLIENT.alwaysChristmas.get() || (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 21 && calendar.get(Calendar.DATE) <= 26)) {
            this.isChristmas = !ProductiveBeesConfig.CLIENT.neverChristmas.get();
        }
        if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1) {
            this.isAprilFool = ProductiveBeesConfig.CLIENT.enableJokes.get();
        }
    }

    @Override
    public ProductiveBeeRenderState createRenderState() {
        return new ProductiveBeeRenderState();
    }

    @Override
    public void extractRenderState(ProductiveBee bee, ProductiveBeeRenderState state, float partialTick) {
        super.extractRenderState(bee, state, partialTick);
        state.rollAmount = bee.getRollAmount(partialTick);
        state.hasStinger = !bee.hasStung();
        state.isOnGround = bee.onGround();
        state.isAngry = bee.isAngry();
        state.hasNectar = bee.hasNectar();
        state.deltaMovementSqr = bee.getDeltaMovement().lengthSqr();
        state.beeName = bee.getBeeName();
        state.renderType = bee.getRenderer();
        state.isColored = bee.isColored();
        state.sizeModifier = bee.getSizeModifier();
        state.renderStatic = bee.getRenderStatic();
        state.hasConverted = bee.hasConverted();
        state.primaryColor = bee.getColor(0, partialTick);
        state.secondaryColor = bee.getColor(1, partialTick);
        state.isInvisible = bee.isInvisible();
        state.christmasMode = isChristmas;
        state.aprilFool = isAprilFool;

        if (bee instanceof ConfigurableBee cBee) {
            state.isConfigurable = true;
            state.isStingless = cBee.isStingless();
            state.isTranslucent = cBee.isTranslucent();
            state.hasBeeTexture = cBee.hasBeeTexture();
            state.beeTexture = cBee.hasBeeTexture() ? cBee.getBeeTexture() : "";
            state.renderTransform = cBee.getRenderTransform();
            state.useGlowLayer = cBee.useGlowLayer();
            state.hasParticleColor = cBee.hasParticleColor();
            state.particleColor = cBee.hasParticleColor() ? cBee.getParticleColor() : -1;
            state.tertiaryColor = cBee.getTertiaryColor(partialTick);
        } else {
            state.tertiaryColor = state.primaryColor;
        }

        if (bee instanceof BumbleBee bumbleBee) {
            state.isBumbleBee = true;
            state.isSaddled = bumbleBee.isSaddled();
            state.isBlehBumbleBee = bumbleBee.hasCustomName() && bumbleBee.getCustomName().getString().equals("Bleh");
        }
    }

    @Override
    protected void setupRotations(ProductiveBeeRenderState state, PoseStack poseStack, float yRot, float xRot) {
        super.setupRotations(state, poseStack, yRot, xRot);

        if (state.aprilFool || state.renderTransform.equals("flipped")) {
            poseStack.translate(0.0D, 0.6F + 0.1F, 0.0D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }

    @Nullable
    @Override
    protected RenderType getRenderType(ProductiveBeeRenderState state, boolean b1, boolean b2, boolean b3) {
        if (state.isTranslucent) {
            return RenderTypes.entityTranslucent(this.getTextureLocation(state));
        }
        return super.getRenderType(state, b1, b2, b3);
    }

    @Nonnull
    @Override
    public Identifier getTextureLocation(ProductiveBeeRenderState state) {
        String textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + state.beeName + "/bee";

        if (state.isBlehBumbleBee) {
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + state.beeName + "_bleh/bee";
        }

        if (state.isColored) {
            String modelType = state.renderType.isEmpty() ? "default" : state.renderType;
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/base/" + modelType + "/bee";
        }

        if (state.hasBeeTexture) {
            textureLocation = state.beeTexture;
        }

        if (state.isAngry) {
            textureLocation = textureLocation + "_angry";
        }

        if (state.hasNectar) {
            textureLocation = textureLocation + "_nectar";
        }

        return resLoc(textureLocation + ".png");
    }

    public static Identifier resLoc(String key) {
        if (!resLocCache.containsKey(key)) {
            resLocCache.put(key, Identifier.parse(key));
        }
        return resLocCache.get(key);
    }
}
