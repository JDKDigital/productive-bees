package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class DyeBeeRenderer extends ProductiveBeeRenderer
{
    public DyeBeeRenderer(EntityRendererManager renderManagerIn, ProductiveBeeModel<ProductiveBeeEntity> model) {
        super(renderManagerIn, model);
    }

    public DyeBeeRenderer(EntityRendererManager renderManagerIn) {
        this(renderManagerIn, new ProductiveBeeModel<>());
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(ProductiveBeeEntity bee) {
        int num = bee.getRenderStatic() ? 1 : sum(bee.getEntityId(), 3);

        String beeLocation = "textures/entity/bee/" + bee.getBeeName() + "/" + num + "/bee";

        if (bee.func_233678_J__()) {
            beeLocation = beeLocation + "_angry";
        }

        if (bee.hasNectar()) {
            beeLocation = beeLocation + "_nectar";
        }

        return new ResourceLocation(ProductiveBees.MODID, beeLocation + ".png");
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
