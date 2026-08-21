package cy.jdkdigital.productivebees.mixin;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ai.BeeAggressiveGoal;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Bee.class)
public abstract class BeeMixin extends Animal
{
    protected BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    // The Feeder-as-flower path lives in BeePollinateGoalMixin#findNearbyFlower.

    @Inject(at = {@At(value = "RETURN")}, method = {"registerGoals"})
    protected void registerGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(0, new BeeAggressiveGoal((Bee) (Object) this));
    }

    @Inject(at = {@At(value = "RETURN")}, method = {"wantsToEnterHive"}, cancellable = true)
    public void wantsToEnterHive(CallbackInfoReturnable<Boolean> ci) {
        Bee bee = (Bee) (Object) this;
        if (bee.stayOutOfHiveCountdown <= 0 && !bee.beePollinateGoal.isPollinating() && !bee.hasStung() && this.getTarget() == null) {
            var attributes = bee.getData(ProductiveBees.ATTRIBUTE_HANDLER);
            boolean shouldReturnToHive = bee.isTiredOfLookingForNectar() || bee.hasNectar();

            if (!shouldReturnToHive && !level().dimensionType().hasFixedTime()) { // in overworld, return to hive if raining or when night
                shouldReturnToHive =
                    (this.level().getOverworldClockTime() % 24000L >= 12000L && attributes.getAttributeValue(GeneAttribute.BEHAVIOR).equals(GeneValue.BEHAVIOR_DIURNAL)) ||
                    (this.level().isRaining() && attributes.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE).equals(GeneValue.WEATHER_TOLERANCE_NONE)) ||
                    (this.level().isThundering() && !attributes.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE).equals(GeneValue.WEATHER_TOLERANCE_ANY));
            }
            ci.setReturnValue(shouldReturnToHive && !bee.isHiveNearFire());
        } else {
            ci.setReturnValue(false);
        }
    }
}
