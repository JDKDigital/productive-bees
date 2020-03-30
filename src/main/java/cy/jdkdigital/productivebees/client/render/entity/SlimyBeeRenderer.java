package cy.jdkdigital.productivebees.client.render.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.entity.layers.SlimyGelLayer;
import cy.jdkdigital.productivebees.client.render.entity.model.SlimyBeeModel;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.solitary.SlimyBeeEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeGelLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimyBeeRenderer extends ProductiveBeeRenderer {

	public SlimyBeeRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn, new SlimyBeeModel<>(false));
		this.addLayer(new SlimyGelLayer(this));
	}
}
