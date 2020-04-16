package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;

import java.util.List;

public class DraconicBeeEntity extends EffectHiveBeeEntity implements IBeeEntity {

	public DraconicBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.DRACONIC_NESTS);
		beehiveInterests = (poiType) -> poiType == PointOfInterestType.field_226356_s_ || poiType == ModPointOfInterestTypes.DRACONIC_NEST.get();
		beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.getTag(ModTags.DRACONIC_FLOWERS));
	}
}
