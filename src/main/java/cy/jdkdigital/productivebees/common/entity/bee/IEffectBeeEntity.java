package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public interface IEffectBeeEntity
{
    default int getEffectCooldown(GeneValue temper) {
        return !temper.equals(GeneValue.TEMPER_PASSIVE) ? 400 / temper.getValue() : 400;
    }

    default void attackTarget(LivingEntity target) {
        if (getAggressiveEffects() != null) {
            for (Map.Entry<Holder<MobEffect>, Integer> entry : getAggressiveEffects().entrySet()) {
                target.addEffect(new MobEffectInstance(entry.getKey(), entry.getValue(), 1));
            }
        }
    }

    default Map<Holder<MobEffect>, Integer> getAggressiveEffects() {
        return null;
    }
}
