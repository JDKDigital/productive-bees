package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;

public class DraconicBeeEntity extends EffectHiveBeeEntity implements IBeeEntity {

	public DraconicBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE || poiType == ModPointOfInterestTypes.DRACONIC_NEST.get();
		beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.DRACONIC_FLOWERS);
		beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.DRACONIC_NESTS);
	}
}
