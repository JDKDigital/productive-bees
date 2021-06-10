package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
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
import net.minecraft.world.server.ServerWorld;

import java.util.Collection;

import static net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent;

public class CreeperBeeEntity extends EffectHiveBeeEntity
{
    private static final DataParameter<Integer> STATE = EntityDataManager.defineId(CreeperBeeEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> POWERED = EntityDataManager.defineId(CreeperBeeEntity.class, DataSerializers.BOOLEAN);

    private int timeSinceIgnited;

    public CreeperBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.TEMPER, 2);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, -1);
        this.entityData.define(POWERED, false);
    }

    @Override
    public void tick() {
        if (this.isAlive() && !this.level.isClientSide) {
            int i = this.getCreeperState();
            if (i > 0 && this.timeSinceIgnited == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
            }

            this.timeSinceIgnited += i;
            if (this.timeSinceIgnited < 0) {
                this.timeSinceIgnited = 0;
            }

            int fuseTime = 30;
            if (this.timeSinceIgnited >= fuseTime) {
                this.timeSinceIgnited = fuseTime;
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
        Explosion.Mode explosionMode = getMobGriefingEvent(level, this) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
        float f = this.entityData.get(POWERED) ? 2.0F : 1.0F;
        this.dead = true;
        float explosionRadius = 1.6F;
        level.explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius * f, explosionMode);
        this.remove();
        this.spawnLingeringCloud();
    }

    private void spawnLingeringCloud() {
        Collection<EffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float) areaeffectcloudentity.getDuration());

            for (EffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
            }

            level.addFreshEntity(areaeffectcloudentity);
        }
    }

    /**
     * Returns the current state of creeper, -1 is idle, 1 is 'in fuse'
     */
    public int getCreeperState() {
        return this.entityData.get(STATE);
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    public void setCreeperState(int state) {
        this.entityData.set(STATE, state);
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    public void thunderHit(ServerWorld world, LightningBoltEntity lightningBolt) {
        super.thunderHit(world, lightningBolt);
        this.entityData.set(POWERED, true);
    }
}
