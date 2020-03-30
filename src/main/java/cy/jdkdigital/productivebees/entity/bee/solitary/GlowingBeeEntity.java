package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class GlowingBeeEntity extends EffectHiveBeeEntity implements IBeeEntity {

	public GlowingBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.GLOWSTONE_NESTS);
	}

	public float getBrightness() {
		return 1.0F;
	}

	@Override
    public Effect getEffect() {
		return Effects.BLINDNESS;
	}
}
