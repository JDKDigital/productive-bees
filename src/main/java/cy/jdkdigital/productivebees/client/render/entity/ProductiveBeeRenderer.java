package cy.jdkdigital.productivebees.client.render.entity;

import com.google.common.collect.Maps;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.model.ProductiveBeeModel;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ProductiveBeeRenderer extends MobRenderer<ProductiveBeeEntity, ProductiveBeeModel<ProductiveBeeEntity>> {

	Map<String, ResourceLocation> cachedResourceLocations = Maps.newHashMap();

	public ProductiveBeeRenderer(EntityRendererManager renderManagerIn, ProductiveBeeModel<ProductiveBeeEntity> model) {
		super(renderManagerIn, model, 0.7F);
	}

	public ProductiveBeeRenderer(EntityRendererManager renderManagerIn) {
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

	protected ResourceLocation getResLocation(String beeLocation) {
		if (!cachedResourceLocations.containsKey(beeLocation)) {
			cachedResourceLocations.put(beeLocation, new ResourceLocation(ProductiveBees.MODID + ":textures/entity/" + beeLocation + ".png"));
		}
		return cachedResourceLocations.get(beeLocation);
	}
}
