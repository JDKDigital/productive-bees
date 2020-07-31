package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Map;

abstract public class EffectHiveBeeEntity extends ProductiveBeeEntity implements IEffectBeeEntity
{
    private int attackCooldown = 0;

    public EffectHiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && isAngry() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.0D) {
                attackCooldown = getEffectCooldown();
                attackTarget(this.getAttackTarget());
            }
        }
    }

    public int getEffectCooldown() {
        int temper = getAttributeValue(BeeAttributes.TEMPER);
        return temper > 0 ? 400 / temper : 400;
    }

    public void attackTarget(LivingEntity target) {
        if (getEffects() != null) {
            for (Map.Entry<Effect, Integer> entry : getEffects().entrySet()) {
                target.addPotionEffect(new EffectInstance(entry.getKey(), entry.getValue(), 1));
            }
        }
    }

    public Map<Effect, Integer> getEffects() {
        return null;
    }
}
