package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class SkeletalBeeEntity extends ProductiveBeeEntity {

	public SkeletalBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		beeAttributes.put(BeeAttributes.BEHAVIOR, 1);
	}
}
