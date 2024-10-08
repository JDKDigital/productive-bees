package cy.jdkdigital.productivebees.compat.geckolib.client.render.model;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.GeckoBee;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

public class GeckoBeeModel extends DefaultedEntityGeoModel<GeckoBee>
{
    Map<ResourceLocation, ResourceLocation> modelCache = new HashMap<>();
    Map<ResourceLocation, ResourceLocation> animationCache = new HashMap<>();

    public GeckoBeeModel() {
        super(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee"));
    }

    @Override
    public @Nullable RenderType getRenderType(GeckoBee animatable, ResourceLocation texture) {
        return animatable.isTranslucent() ? RenderType.entityTranslucent(texture) : super.getRenderType(animatable, texture);
    }

    @Override
    public ResourceLocation getModelResource(GeckoBee animatable) {
        return modelCache.computeIfAbsent(animatable.getBeeType(), resourceLocation -> animatable.getModelLocation());
    }

    @Override
    public ResourceLocation getTextureResource(GeckoBee animatable) {
        return animatable.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoBee animatable) {
        return animationCache.computeIfAbsent(animatable.getBeeType(), resourceLocation -> animatable.getAnimationLocation());
    }
}
