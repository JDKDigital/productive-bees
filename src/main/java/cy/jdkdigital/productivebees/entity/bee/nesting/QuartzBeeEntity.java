package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class QuartzBeeEntity extends ProductiveBeeEntity
{
    public QuartzBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.NETHER_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.NETHER_QUARTZ_NESTS);
    }
}
