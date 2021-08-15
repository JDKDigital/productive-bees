package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.IEffectBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

import static net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent;

public class CreeperBee extends ProductiveBee implements IEffectBeeEntity
{
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(CreeperBee.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(CreeperBee.class, EntityDataSerializers.BOOLEAN);

    private int timeSinceIgnited;

    public CreeperBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.TEMPER, 2);
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        return ModTags.GUNPOWDER.contains(flowerBlock);
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
        Explosion.BlockInteraction explosionMode = getMobGriefingEvent(level, this) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
        float f = this.entityData.get(POWERED) ? 2.0F : 1.0F;
        this.dead = true;
        float explosionRadius = 1.6F;
        level.explode(this, this.getX(), this.getY(), this.getZ(), explosionRadius * f, explosionMode);
        this.discard();
        this.spawnLingeringCloud();
    }

    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloudentity = new AreaEffectCloud(level, this.getX(), this.getY(), this.getZ());
            areaeffectcloudentity.setRadius(2.5F);
            areaeffectcloudentity.setRadiusOnUse(-0.5F);
            areaeffectcloudentity.setWaitTime(10);
            areaeffectcloudentity.setDuration(areaeffectcloudentity.getDuration() / 2);
            areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float) areaeffectcloudentity.getDuration());

            for (MobEffectInstance effectinstance : collection) {
                areaeffectcloudentity.addEffect(new MobEffectInstance(effectinstance));
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
    public void thunderHit(ServerLevel world, LightningBolt lightningBolt) {
        super.thunderHit(world, lightningBolt);
        this.entityData.set(POWERED, true);
    }
}
