package cy.jdkdigital.productivebees.common.entity.bee;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public interface IEffectBeeEntity
{
    default int getEffectCooldown(int temper) {
        return temper > 0 ? 400 / temper : 400;
    }

    default void attackTarget(LivingEntity target) {
        if (getAggressiveEffects() != null) {
            for (Map.Entry<MobEffect, Integer> entry : getAggressiveEffects().entrySet()) {
                target.addEffect(new MobEffectInstance(entry.getKey(), entry.getValue(), 1));
            }
        }
    }

    default Map<MobEffect, Integer> getAggressiveEffects() {
        return null;
    }
}
