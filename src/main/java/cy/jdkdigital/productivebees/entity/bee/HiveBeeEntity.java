package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

abstract public class HiveBeeEntity extends ProductiveBeeEntity implements IHiveBeeEntity {
	public HiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

}
