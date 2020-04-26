package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BlazingBeeEntity extends EffectHiveBeeEntity implements IBeeEntity {

	public BlazingBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

	public void attackTarget(LivingEntity target) {
		if (this.isAlive()) {
			target.setFire(200);
		}
	}
}
