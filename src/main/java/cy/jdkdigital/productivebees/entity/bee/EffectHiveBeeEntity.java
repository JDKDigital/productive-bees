package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

abstract public class EffectHiveBeeEntity extends HiveBeeEntity implements IEffectBeeEntity {

	private int effectDuration = 0;

	public EffectHiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void livingTick() {
		super.livingTick();
		if (!this.world.isRemote) {
			if (--effectDuration < 0) {
				effectDuration = 0;
			}
			if (effectDuration == 0 && isAngry() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.0D) {
				effectDuration = getEffectCooldown();
				attackTarget(this.getAttackTarget());
			}
		}
	}

	public int getEffectCooldown() {
		return 400;
	}

	public void attackTarget(LivingEntity target) {
		target.addPotionEffect(new EffectInstance(getEffect(), 200));
	}

	public abstract Effect getEffect();
}
