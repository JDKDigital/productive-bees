package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DyeBeeRenderer extends ProductiveBeeRenderer
{
    public DyeBeeRenderer(EntityRendererManager renderManagerIn, ProductiveBeeModel<ProductiveBeeEntity> model) {
        super(renderManagerIn, model);
    }

    public DyeBeeRenderer(EntityRendererManager renderManagerIn) {
        this(renderManagerIn, new ProductiveBeeModel<>());
    }

    @Override
    public ResourceLocation getEntityTexture(ProductiveBeeEntity bee) {
        int num = sum(bee.getEntityId());

        String beeLocation = "bee/" + bee.getBeeType() + "/" + num + "/bee";

        if (bee.func_233678_J__()) {
            beeLocation = beeLocation + "_angry";
        }

        if (bee.hasNectar()) {
            beeLocation = beeLocation + "_nectar";
        }

        return getResLocation(beeLocation);
    }

    private int sum(int num) {
        double sum = 0;
        while (num > 0) {
            sum = sum + num % 10;
            num = num / 10;
        }
        return sum > 9 ? sum((int) sum) : (int) Math.ceil(sum / 3);
    }
}
