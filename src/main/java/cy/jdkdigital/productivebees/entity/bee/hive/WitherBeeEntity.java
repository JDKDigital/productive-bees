package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class WitherBeeEntity extends EffectHiveBeeEntity {

	public WitherBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
    public Effect getEffect() {
		return Effects.WITHER;
	}
}
