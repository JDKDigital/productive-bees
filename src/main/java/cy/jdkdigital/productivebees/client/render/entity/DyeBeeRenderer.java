package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DyeBeeRenderer extends ProductiveBeeRenderer {

	public DyeBeeRenderer(EntityRendererManager renderManagerIn, ProductiveBeeModel<ProductiveBeeEntity> model) {
		super(renderManagerIn, model);
	}

	public DyeBeeRenderer(EntityRendererManager renderManagerIn) {
		this(renderManagerIn, new ProductiveBeeModel<>());
	}

	@Override
	public ResourceLocation getEntityTexture(ProductiveBeeEntity bee) {
		String beeLocation = "bee/" + bee.getBeeType() + "/bee";
		
		if (bee.isAngry()) {
			beeLocation = beeLocation + "_angry";
		}
		
		if (bee.hasNectar()) {
			beeLocation = beeLocation + "_nectar";
		}
		
		return getResLocation(beeLocation);
	}
}
