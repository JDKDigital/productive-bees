package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

abstract public class EffectHiveBeeEntity extends ProductiveBeeEntity implements IEffectBeeEntity
{
    private int attackCooldown = 0;

    public EffectHiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && isAngry() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0D) {
                attackCooldown = getEffectCooldown(getAttributeValue(BeeAttributes.TEMPER));
                attackTarget(this.getTarget());
            }
        }
    }
}
