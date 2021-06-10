package cy.jdkdigital.productivebees.common.entity.bee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import java.util.Map;

public interface IEffectBeeEntity
{
    default int getEffectCooldown(int temper) {
        return temper > 0 ? 400 / temper : 400;
    }

    default void attackTarget(LivingEntity target) {
        if (getAggressiveEffects() != null) {
            for (Map.Entry<Effect, Integer> entry : getAggressiveEffects().entrySet()) {
                target.addEffect(new EffectInstance(entry.getKey(), entry.getValue(), 1));
            }
        }
    }

    default Map<Effect, Integer> getAggressiveEffects() {
        return null;
    }
}
