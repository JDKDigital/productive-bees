package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class GoldBeeEntity extends ProductiveBeeEntity
{
    public GoldBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.GILDED_FLOWERS);
    }
}
