package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.IBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ISolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class SlimyBeeEntity extends EffectHiveBeeEntity implements IBeeEntity, ISolitaryBeeEntity {

	public SlimyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
		this.nestBlockTag = ModTags.getTag(ModTags.SLIMY_NESTS);

		beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.getTag(ModTags.SWAMP_FLOWERS));
	}

	@Override
    public Map<Effect, Integer> getEffects() {
		return new HashMap<Effect, Integer>() {{put(Effects.SLOWNESS, 200);}};
	}

	@Override
	public void livingTick() {
		super.livingTick();
		if (this.world.isRemote && ticksExisted%100 == 0) {
			int i = 1;
			for(int j = 0; j < i * 8; ++j) {
				float f = this.rand.nextFloat() * ((float)Math.PI * 2F);
				float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
				float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
				float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
				this.world.addParticle(ParticleTypes.ITEM_SLIME, this.getPosX() + (double)f2, this.getPosY(), this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Nonnull
	@Override
	protected ResourceLocation getLootTable() {
		return EntityType.SLIME.getLootTable();
	}
}
