package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class ResinBeeEntity extends SolitaryBeeEntity {
    public ResinBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }
}
