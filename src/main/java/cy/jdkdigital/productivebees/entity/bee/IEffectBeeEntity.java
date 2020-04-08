package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;

import java.util.Map;

public interface IEffectBeeEntity {

    int getEffectCooldown();

    void attackTarget(LivingEntity target);

    abstract Map<Effect, Integer> getEffects();
}
