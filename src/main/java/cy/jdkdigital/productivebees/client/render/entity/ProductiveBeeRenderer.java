package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;

public class ProductiveBeeRenderer extends MobRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>>
{
    private boolean isChristmas;

    public ProductiveBeeRenderer(EntityRendererManager renderManagerIn, ProductiveBeeModel<ProductiveBeeEntity> model) {
        super(renderManagerIn, model, 0.7F);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 21 && calendar.get(Calendar.DATE) <= 26) {
            this.isChristmas = true;
        }
    }

    public ProductiveBeeRenderer(EntityRendererManager renderManagerIn) {
        this(renderManagerIn, new ProductiveBeeModel<>());

        addLayer(new BeeBodyLayer(this, "thicc", isChristmas));
        addLayer(new BeeBodyLayer(this, "default", isChristmas));
        addLayer(new BeeBodyLayer(this, "default_crystal", isChristmas));
        addLayer(new BeeBodyLayer(this, "default_shell", isChristmas));
        addLayer(new BeeBodyLayer(this, "default_foliage", isChristmas));
        addLayer(new BeeBodyLayer(this, "small", isChristmas));
        addLayer(new BeeBodyLayer(this, "slim", isChristmas));
        addLayer(new BeeBodyLayer(this, "tiny", isChristmas));
        addLayer(new BeeBodyLayer(this, "translucent_with_center", isChristmas));
    }

    @Nullable
    @Override
    protected RenderType func_230042_a_(ProductiveBeeEntity bee, boolean b1, boolean b2) {
        if (bee instanceof ConfigurableBeeEntity && ((ConfigurableBeeEntity) bee).isTranslucent()) {
            return RenderType.getEntityTranslucent(this.getEntityTexture(bee));
        }
        return super.func_230042_a_(bee, b1, b2);
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(ProductiveBeeEntity bee) {
        String textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + bee.getBeeName() + "/bee";

        // Colored bees use tinted base texture
        if (bee.getColor(0) != null) {
            String modelType = bee.getRenderer();
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/base/" + modelType + "/bee";
        }

        if (bee instanceof ConfigurableBeeEntity) {
            if (((ConfigurableBeeEntity) bee).hasBeeTexture()) {
                textureLocation = ((ConfigurableBeeEntity) bee).getBeeTexture();
            }
        }

        if (bee.isAngry()) {
            textureLocation = textureLocation + "_angry";
        }

        if (bee.hasNectar()) {
            textureLocation = textureLocation + "_nectar";
        }

        return new ResourceLocation(textureLocation + ".png");
    }
}
