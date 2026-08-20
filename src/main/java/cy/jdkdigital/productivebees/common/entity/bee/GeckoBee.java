package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;

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
        controllers.add(new AnimationController<GeckoBee>("animation", 0, state -> {
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

    public Identifier getModelLocation() {
        var data = getBeeData();
        return data != null && data.model().isPresent()
                ? Identifier.parse(data.model().get())
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "entity/" + (getRenderer().isEmpty() ? "default" : getRenderer()));
    }

    public Identifier getTextureLocation() {
        String textureLocation = ProductiveBees.MODID + ":textures/entity/bee/" + getBeeName() + "/bee";

        // Colored bees use tinted base texture
        if (isColored()) {
            String modelType = getRenderer();
            textureLocation = ProductiveBees.MODID + ":textures/entity/bee/base/" + (!modelType.isEmpty() ? modelType : "default") + "/bee";
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

        return Identifier.parse(textureLocation + ".png");
    }

    public Identifier getAnimationLocation() {
        var data = getBeeData();
        return data != null && data.animation().isPresent()
                ? Identifier.parse(data.animation().get())
                : Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "entity/bee");
    }
}
