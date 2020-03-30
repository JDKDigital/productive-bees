package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.NestingBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BumbleBeeEntity extends NestingBeeEntity
{
	public BumbleBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

}
