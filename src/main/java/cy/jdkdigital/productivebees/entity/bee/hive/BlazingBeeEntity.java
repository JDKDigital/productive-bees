package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BlazingBeeEntity extends EffectHiveBeeEntity
{
    public BlazingBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public void attackTarget(LivingEntity target) {
        if (this.isAlive()) {
            target.setFire(200);
        }
    }
}
