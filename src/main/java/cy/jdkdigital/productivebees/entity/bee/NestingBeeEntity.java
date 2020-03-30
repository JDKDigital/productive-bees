package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

abstract public class NestingBeeEntity extends ProductiveBeeEntity
{
	public NestingBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

}
