package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ZombieBeeEntity extends EffectHiveBeeEntity {

	public ZombieBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
    public Map<Effect, Integer> getEffects() {
		return new HashMap<Effect, Integer>() {{put(Effects.HUNGER, 220);}};
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.4000000238418579D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000001192092896D);
	}
}
