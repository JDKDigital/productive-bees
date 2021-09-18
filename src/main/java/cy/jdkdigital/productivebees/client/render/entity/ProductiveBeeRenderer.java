package cy.jdkdigital.productivebees.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProductiveBeeRenderer extends MobRenderer<ProductiveBee, ProductiveBeeModel<ProductiveBee>>
{
    private static Map<String, ResourceLocation> resLocCache = new HashMap<>();

    public static final ModelLayerLocation PB_MAIN_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "main"), "main");
    public static final ModelLayerLocation PB_HOARDER_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "hoarder"), "main");
    public static final ModelLayerLocation PB_RANCHER_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "rancher"), "main");
    public static final ModelLayerLocation PB_THICC_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "thicc"), "main");
    public static final ModelLayerLocation PB_DEFAULT_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "default"), "main");
    public static final ModelLayerLocation PB_DEFAULT_CRYSTAL_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "default_crystal"), "main");
    public static final ModelLayerLocation PB_DEFAULT_SHELL_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "default_shell"), "main");
    public static final ModelLayerLocation PB_DEFAULT_FOLIAGE_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "default_foliage"), "main");
    public static final ModelLayerLocation PB_ELVIS_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "elvis"), "main");
    public static final ModelLayerLocation PB_SMALL_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "small"), "main");
    public static final ModelLayerLocation PB_SLIM_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "slim"), "main");
    public static final ModelLayerLocation PB_TINY_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "tiny"), "main");
    public static final ModelLayerLocation PB_SLIMY_LAYER = new ModelLayerLocation(new ResourceLocation(ProductiveBees.MODID, "translucent_with_center"), "main");

    protected boolean isChristmas;

    public ProductiveBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new ProductiveBeeModel<>(context.bakeLayer(PB_MAIN_LAYER)), 0.4F);

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

    public ProductiveBeeRenderer(EntityRendererProvider.Context context, ProductiveBeeModel<ProductiveBee> model) {
        super(context, model, 0.4F);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 21 && calendar.get(Calendar.DATE) <= 26) {
            this.isChristmas = true;
        }
    }

    @Override
    protected void setupRotations(ProductiveBee entity, PoseStack matrixStack, float f1, float f2, float f3) {
        super.setupRotations(entity, matrixStack, f1, f2, f3);

        if (entity instanceof ConfigurableBee configurableBee && configurableBee.getRenderTransform().equals("flipped")) {
            matrixStack.translate(0.0D, entity.getBbHeight() + 0.1F, 0.0D);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        }
    }

    @Nullable
    @Override
    protected RenderType getRenderType(ProductiveBee bee, boolean b1, boolean b2, boolean b3) {
        if (bee instanceof ConfigurableBee && ((ConfigurableBee) bee).isTranslucent()) {
            return RenderType.entityTranslucent(this.getTextureLocation(bee));
        }
        return super.getRenderType(bee, b1, b2, b3);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(ProductiveBee bee) {
        String textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + bee.getBeeName() + "/bee";

        // Colored bees use tinted base texture
        if (bee.getColor(0)  > 0) {
            String modelType = bee.getRenderer();
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/base/" + modelType + "/bee";
        }

        if (bee instanceof ConfigurableBee) {
            if (((ConfigurableBee) bee).hasBeeTexture()) {
                textureLocation = ((ConfigurableBee) bee).getBeeTexture();
            }
        }

        if (bee.isAngry()) {
            textureLocation = textureLocation + "_angry";
        }

        if (bee.hasNectar()) {
            textureLocation = textureLocation + "_nectar";
        }

        return resLoc(textureLocation + ".png");
    }

    public static ResourceLocation resLoc(String key) {
        if (!resLocCache.containsKey(key)) {
            resLocCache.put(key, new ResourceLocation(key));
        }
        return resLocCache.get(key);
    }
}
