package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.layers.BeeBodyLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class DyeBeeRenderer extends ProductiveBeeRenderer
{
    public DyeBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new ProductiveBeeModel<>(context.bakeLayer(PB_MAIN_LAYER), "default"));

        addLayer(new BeeBodyLayer(this, context.bakeLayer(PB_DEFAULT_LAYER), "default", isChristmas));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(ProductiveBee bee) {
        int num = bee.getRenderStatic() ? 1 : sum(bee.getId(), 3);

        String beeLocation = ProductiveBees.MODID + ":textures/entity/bee/" + bee.getBeeName() + "/" + num + "/bee";

        if (bee.isAngry()) {
            beeLocation = beeLocation + "_angry";
        }

        if (bee.hasNectar()) {
            beeLocation = beeLocation + "_nectar";
        }

        return ProductiveBeeRenderer.resLoc(beeLocation + ".png");
    }

    private int sum(int num, int max) {
        double sum = 0;
        while (num > 0) {
            sum = sum + num % 10;
            num = num / 10;
        }
        return sum > 9 ? sum((int) sum, max) : (int) Math.ceil(sum / max);
    }
}
