package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

abstract public class SolitaryBeeEntity extends ProductiveBeeEntity implements ISolitaryBeeEntity
{
	public SolitaryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.GROUND_NESTS);
		isInterestedIn = (poiType) -> poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() || poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
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
