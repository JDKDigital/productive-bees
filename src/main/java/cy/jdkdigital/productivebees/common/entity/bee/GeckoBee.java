package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GeckoBee extends ConfigurableBee implements GeoEntity
{
    protected static final RawAnimation BEE_FLY = RawAnimation.begin().thenLoop("animation.bee.fly");
    protected static final RawAnimation BEE_ATTACK = RawAnimation.begin().thenPlay("animation.bee.attack");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public GeckoBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "animation", 0, state -> {
            if (swinging && !hasStung()) {
                state.setAndContinue(BEE_ATTACK);
                return PlayState.STOP;
            }
            return state.setAndContinue(BEE_FLY);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    public ResourceLocation getModelLocation() {
        var data  = getNBTData();
        return data.contains("model") ? ResourceLocation.parse(data.getString("model")) :
                ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "geo/entity/" + getRenderer() + ".geo.json");
    }

    public ResourceLocation getTextureLocation() {
        String textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + getBeeName() + "/bee";

        // Colored bees use tinted base texture
        if (isColored()) {
            String modelType = getRenderer();
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/base/" + modelType + "/bee";
        }

        if (hasBeeTexture()) {
            textureLocation = getBeeTexture();
        }

        if (isAngry()) {
            textureLocation = textureLocation + "_angry";
        }

        if (hasNectar()) {
            textureLocation = textureLocation + "_nectar";
        }

        return ResourceLocation.parse(textureLocation + ".png");
    }

    public ResourceLocation getAnimationLocation() {
        var data  = getNBTData();
        return data.contains("animation") ? ResourceLocation.parse(data.getString("animation")) : ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "animations/entity/bee.animation.json");
    }
}
