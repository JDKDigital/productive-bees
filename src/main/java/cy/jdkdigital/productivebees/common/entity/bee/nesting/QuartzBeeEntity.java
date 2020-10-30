package cy.jdkdigital.productivebees.common.entity.bee.nesting;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class QuartzBeeEntity extends ProductiveBeeEntity
{
    public QuartzBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }
}
