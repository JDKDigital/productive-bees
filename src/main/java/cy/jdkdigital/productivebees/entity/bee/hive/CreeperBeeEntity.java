package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Collection;

import static net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent;

public class CreeperBeeEntity extends EffectHiveBeeEntity {
	private static final DataParameter<Integer> STATE = EntityDataManager.createKey(CreeperBeeEntity.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> POWERED = EntityDataManager.createKey(CreeperBeeEntity.class, DataSerializers.BOOLEAN);

	private int lastActiveTime;
	private int timeSinceIgnited;
	private int fuseTime = 30;
	private float explosionRadius = 1.6F;

	public CreeperBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
		super(entityType, world);
	}

	protected void registerData() {
		super.registerData();
		this.dataManager.register(STATE, -1);
		this.dataManager.register(POWERED, false);
	}

	@Override
	public void tick() {
		if (this.isAlive() && !this.world.isRemote) {
			this.lastActiveTime = this.timeSinceIgnited;

			int i = this.getCreeperState();
			if (i > 0 && this.timeSinceIgnited == 0) {
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
			}

			this.timeSinceIgnited += i;
			if (this.timeSinceIgnited < 0) {
				this.timeSinceIgnited = 0;
			}

			if (this.timeSinceIgnited >= this.fuseTime) {
				this.timeSinceIgnited = this.fuseTime;
				this.explode();
			}
		}
		super.tick();
	}

	public void attackTarget(LivingEntity target) {
		if (this.isAlive()) {
			this.setCreeperState(1);
		}
	}

	private void explode() {
		Explosion.Mode explosionMode = getMobGriefingEvent(this.world, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
		float f = this.dataManager.get(POWERED) ? 2.0F : 1.0F;
		this.dead = true;
		this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), this.explosionRadius * f, explosionMode);
		this.remove();
		this.spawnLingeringCloud();
	}

	private void spawnLingeringCloud() {
		Collection<EffectInstance> collection = this.getActivePotionEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
			areaeffectcloudentity.setRadius(2.5F);
			areaeffectcloudentity.setRadiusOnUse(-0.5F);
			areaeffectcloudentity.setWaitTime(10);
			areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
			areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());

			for(EffectInstance effectinstance : collection) {
				areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
			}

			this.world.addEntity(areaeffectcloudentity);
		}
	}

	/**
	 * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
	 */
	public int getCreeperState() {
		return this.dataManager.get(STATE);
	}

	/**
	 * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
	 */
	public void setCreeperState(int state) {
		this.dataManager.set(STATE, state);
	}

	/**
	 * Called when a lightning bolt hits the entity.
	 */
	public void onStruckByLightning(LightningBoltEntity lightningBolt) {
		super.onStruckByLightning(lightningBolt);
		this.dataManager.set(POWERED, true);
	}
}
