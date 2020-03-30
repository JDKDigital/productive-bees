package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagmaticBeeEntity extends EffectHiveBeeEntity implements IBeeEntity {

	private int lavaDuration = 0;
	private BlockPos lavaPosition = null;

	public MagmaticBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.NETHER_BRICK_NESTS);
	}

	@Override
	public void tick() {
		super.tick();
		// Remove lava source after a few ticks
		if (this.lavaPosition != null && --this.lavaDuration <= 0) {
			this.world.setBlockState(this.lavaPosition, Blocks.AIR.getDefaultState(), 11);
			lavaPosition = null;
		}
	}

	public float getBrightness() {
		return 1.0F;
	}

	public boolean isBurning() {
		return this.isAngry();
	}

	@Override
	public Effect getEffect() {
		return null;
	}

	public void attackTarget(LivingEntity target) {
		if (this.isAlive()) {
			// Place flowing lava on the targets location
			this.lavaPosition = target.getPosition();
			this.lavaDuration = 100;
			this.world.setBlockState(lavaPosition, Blocks.LAVA.getDefaultState().getBlockState(), 11);
		}
	}
}
