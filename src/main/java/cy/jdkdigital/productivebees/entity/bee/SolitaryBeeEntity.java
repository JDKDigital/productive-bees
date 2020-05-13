package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

abstract public class SolitaryBeeEntity extends ProductiveBeeEntity implements ISolitaryBeeEntity
{
    public SolitaryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
        beeAttributes.put(BeeAttributes.TYPE, "solitary");
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.SOLITARY_OVERWORLD_NESTS);
    }
}
