package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;

public interface IEffectBeeEntity {

    public int getEffectCooldown();

    public void attackTarget(LivingEntity target);

    abstract Effect getEffect();
}
