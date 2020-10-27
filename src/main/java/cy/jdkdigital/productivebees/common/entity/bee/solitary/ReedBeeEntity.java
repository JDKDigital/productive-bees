package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class ReedBeeEntity extends SolitaryBeeEntity
{
    public ReedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.REED_NESTS);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.SWAMP_FLOWERS);
    }
}
