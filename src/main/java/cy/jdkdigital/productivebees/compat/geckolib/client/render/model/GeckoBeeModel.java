package cy.jdkdigital.productivebees.compat.geckolib.client.render.model;

import com.geckolib.model.DefaultedEntityGeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import cy.jdkdigital.productivebees.compat.geckolib.client.render.state.GeckoBeeRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class GeckoBeeModel extends DefaultedEntityGeoModel<GeckoBee>
{
    private static final Identifier FALLBACK_MODEL = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "entity/default");
    private static final Identifier FALLBACK_TEXTURE = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/entity/bee/default/bee.png");

    public GeckoBeeModel() {
        super(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee"));
    }

    @Override
    public Identifier getModelResource(GeoRenderState state) {
        if (state instanceof GeckoBeeRenderState gecko && gecko.modelLocation != null) {
            return gecko.modelLocation;
        }
        return FALLBACK_MODEL;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState state) {
        if (state instanceof GeckoBeeRenderState gecko && gecko.textureLocation != null) {
            return gecko.textureLocation;
        }
        return FALLBACK_TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GeckoBee animatable) {
        return animatable.getAnimationLocation();
    }

    public RenderType getRenderType(GeoRenderState state, Identifier texture) {
        if (state instanceof GeckoBeeRenderState gecko && gecko.isTranslucent) {
            return RenderTypes.entityTranslucent(texture);
        }
        return RenderTypes.entityCutout(texture);
    }
}
