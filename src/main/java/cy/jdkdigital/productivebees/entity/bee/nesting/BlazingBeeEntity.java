package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

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
