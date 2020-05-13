package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;

public class DraconicBeeEntity extends ProductiveBeeEntity
{
    public DraconicBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE || poiType == ModPointOfInterestTypes.DRACONIC_NEST.get();
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.DRACONIC_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.DRACONIC_NESTS);
    }
}
