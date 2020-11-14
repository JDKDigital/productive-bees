package cy.jdkdigital.productivebees.client.render.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IHasBeeHat
{
    ModelRenderer getModelHat();
}
