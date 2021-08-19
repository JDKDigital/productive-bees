package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class CupidBeeEntity extends ProductiveBeeEntity
{
    public AnimalEntity targetEntity = null;
    private int animalsBredSincePollination;

    private SetLoveModeGoal loveGoal;

    public static Predicate<Entity> predicate = (entity -> {
        if (entity instanceof AnimalEntity) {
            return !((AnimalEntity) entity).isInLove() && !((AnimalEntity) entity).isBaby() && ((AnimalEntity) entity).canFallInLove();
        }
        return false;
    });

    public CupidBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.goalSelector.removeGoal(this.breedGoal);

        this.beePollinateGoal = new ProductiveBeeEntity.PollinateGoal();
        this.goalSelector.addGoal(4, this.beePollinateGoal);

        this.goToKnownFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.goalSelector.addGoal(2, new GoToBreedableGoal());
        this.loveGoal = new SetLoveModeGoal();
        this.goalSelector.addGoal(3, this.loveGoal);
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData livingEntityData, @Nullable CompoundNBT tag) {
        if (ProductiveBees.rand.nextFloat() < 0.01f) {
            this.setCustomName(new StringTextComponent("Leena CuBee"));
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, livingEntityData, tag);
    }

    public void tick() {
        super.tick();

        if (this.tickCount % 20 == 0 && hasNectar()) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    private int getAnimalsBredSincePollination() {
        return this.animalsBredSincePollination;
    }

    private void resetBreedCounter() {
        this.animalsBredSincePollination = 0;
    }

    private void addBreedCounter() {
        ++this.animalsBredSincePollination;
    }

    public boolean wantsToEnterHive() {
        return super.wantsToEnterHive() && !this.loveGoal.isRunning();
    }

    @Override
    public void dropOffNectar() {
        super.dropOffNectar();
        resetBreedCounter();
    }

    public List<Entity> findNearbyBreedables(float distance) {
        BlockPos pos = blockPosition();

        return level.getEntities(this, (new AxisAlignedBB(pos).expandTowards(distance, distance, distance)), predicate);
    }

    public class GoToBreedableGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            return !CupidBeeEntity.this.isAngry() && CupidBeeEntity.this.hasNectar() && CupidBeeEntity.this.targetEntity != null;
        }

        @Override
        public boolean canContinueToUse() {
            return CupidBeeEntity.this.targetEntity != null && CupidBeeEntity.this.targetEntity.position().distanceTo(CupidBeeEntity.this.targetEntity.position()) > 2;
        }

        public void start() {
            this.ticks = 0;
        }

        @Override
        public void tick() {
            if (CupidBeeEntity.this.targetEntity != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    CupidBeeEntity.this.targetEntity = null;
                } else if (!CupidBeeEntity.this.navigation.isDone() || this.ticks % 100 == 0) {
                    BlockPos blockPos = CupidBeeEntity.this.targetEntity.blockPosition();
                    CupidBeeEntity.this.navigation.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0D);
                }
            }
        }
    }

    public class SetLoveModeGoal extends Goal
    {
        private int ticks = 0;
        private boolean running;

        @Override
        public boolean canUse() {
            if (!CupidBeeEntity.this.isAngry() && CupidBeeEntity.this.hasNectar() && CupidBeeEntity.this.getAnimalsBredSincePollination() <= ProductiveBeesConfig.BEES.cupidBeeAnimalsPerPollination.get()) {
                List<Entity> breedablesNearby = CupidBeeEntity.this.findNearbyBreedables(5);

                if (!breedablesNearby.isEmpty() && breedablesNearby.size() < ProductiveBeesConfig.BEES.cupidBeeAnimalDensity.get()) {
                    BlockPos beePos = CupidBeeEntity.this.blockPosition();
                    AnimalEntity nearest = null;
                    double nearestDistance = 0;
                    for (Entity entity : breedablesNearby) {
                        if (entity instanceof AnimalEntity) {
                            BlockPos pos = entity.blockPosition();
                            double distance = pos.distSqr(beePos);
                            if (nearestDistance == 0 || distance < nearestDistance) {
                                nearestDistance = distance;
                                nearest = (AnimalEntity) entity;
                            }
                        }
                    }

                    CupidBeeEntity.this.targetEntity = nearest;
                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (!this.running) {
                return false;
            }
            return CupidBeeEntity.this.targetEntity != null && CupidBeeEntity.this.hasNectar() && !CupidBeeEntity.this.isAngry();
        }

        public boolean isRunning() {
            return this.running;
        }

        public void start() {
            ticks = 0;
            this.running = true;
        }

        public void stop() {
            this.running = false;
        }

        @Override
        public void tick() {
            ++ticks;
            if (CupidBeeEntity.this.targetEntity != null) {
                if (ticks > 600) {
                    CupidBeeEntity.this.targetEntity = null;
                } else {
                    Vector3d vec3d = CupidBeeEntity.this.targetEntity.position().add(0.5D, 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(CupidBeeEntity.this.position());

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            CupidBeeEntity.this.targetEntity = null;
                        } else {
                            List<Entity> breedablesNearby = CupidBeeEntity.this.findNearbyBreedables(1);
                            if (!breedablesNearby.isEmpty()) {
                                Entity target = breedablesNearby.iterator().next();

                                if (target instanceof AnimalEntity) {
                                    if (!((AnimalEntity) target).isBaby() && ((AnimalEntity) target).canFallInLove()) {
                                        ((AnimalEntity) target).setInLove(null);
                                        CupidBeeEntity.this.addBreedCounter();
                                    }

                                    CupidBeeEntity.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vector3d target) {
            CupidBeeEntity.this.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.0F);
        }
    }
}
