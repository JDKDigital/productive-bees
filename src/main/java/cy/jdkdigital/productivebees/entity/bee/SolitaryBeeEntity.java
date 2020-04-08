package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

abstract public class SolitaryBeeEntity extends ProductiveBeeEntity implements ISolitaryBeeEntity
{
	public SolitaryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.GROUND_NESTS);
		beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
		beeAttributes.remove(BeeAttributes.TYPE);
		beeAttributes.put(BeeAttributes.TYPE, "solitary");
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return super.isBreedingItem(itemStack);
	}

	public boolean canMateWith(AnimalEntity nearbyEntity) {
		if (nearbyEntity == this) {
			return false;
		}

		// Check solitary<->solitary breeding rules
//		BeeEntity possibleOffspring = calculatePossibleOffspring(this, (BeeEntity) nearbyEntity);

		// Check hive<->solitary breeding rules
		return nearbyEntity instanceof HiveBeeEntity && nearbyEntity.canMateWith(this);
	}

//	private BeeEntity calculatePossibleOffspring(BeeEntity solitaryBeeEntity, BeeEntity nearbyEntity) {
//
//	}
}
