package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ISolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;

public class EnderBeeEntity extends ProductiveBeeEntity implements IBeeEntity, ISolitaryBeeEntity {

	public EnderBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.END_NESTS);
		beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.getTag(ModTags.END_FLOWERS));
	}

	@Override
	protected void updateAITasks() {
		// Teleport to active path
		if (null != this.navigator.getPath()) {
			BlockPos pos = this.navigator.getPath().getTarget();
			teleportTo(pos.getX(), pos.getY(), pos.getZ());
		}

		super.updateAITasks();
	}

	private boolean teleportTo(double x, double y, double z) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

		while(blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
			blockpos$mutable.move(Direction.DOWN);
		}

		BlockState blockstate = this.world.getBlockState(blockpos$mutable);
		if (blockstate.getMaterial().blocksMovement()) {
			net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(this, x, y, z, 0);
//			if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) return false;
			boolean flag2 = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
			if (flag2) {
				this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 0.3F, 1.0F);
				this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.3F, 1.0F);
			}

			return flag2;
		} else {
			return false;
		}
	}
}
