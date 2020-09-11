package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class DeprecatedBeeEntity extends ProductiveBeeEntity
{
    public DeprecatedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }
}
