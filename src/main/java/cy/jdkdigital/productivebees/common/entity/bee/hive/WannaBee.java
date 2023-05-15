package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class WannaBee extends ProductiveBee
{
    public Animal targetEntity = null;
    private int animalsBredSincePollination;

    private SetLoveModeGoal loveGoal;

    public static Predicate<Entity> predicate = (entity -> {
        if (entity instanceof Animal) {
            return !((Animal) entity).isInLove() && !((Animal) entity).isBaby();
        }
        return false;
    });

    public WannaBee(EntityType<? extends Bee> entityType, Level world) {
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

        this.beePollinateGoal = new PollinateGoal();
        this.goalSelector.addGoal(4, this.beePollinateGoal);

        this.goToKnownFlowerGoal = new BeeGoToKnownFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.goalSelector.addGoal(2, new GoToBreedableGoal());
        this.loveGoal = new SetLoveModeGoal();
        this.goalSelector.addGoal(3, this.loveGoal);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData livingEntityData, @Nullable CompoundTag tag) {
        if (level.getRandom().nextFloat() < 0.05f) {
            this.setCustomName(Component.literal("Lena CuBee"));
        }

        return super.finalizeSpawn(level, difficulty, spawnReason, livingEntityData, tag);
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

        return level.getEntities(this, (new AABB(pos).inflate(distance, distance, distance)), predicate);
    }

    public class GoToBreedableGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            return !WannaBee.this.isAngry() && WannaBee.this.hasNectar() && WannaBee.this.targetEntity != null;
        }

        @Override
        public boolean canContinueToUse() {
            return WannaBee.this.targetEntity != null && WannaBee.this.targetEntity.position().distanceTo(WannaBee.this.targetEntity.position()) > 2;
        }

        public void start() {
            this.ticks = 0;
        }

        @Override
        public void tick() {
            if (WannaBee.this.targetEntity != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    WannaBee.this.targetEntity = null;
                } else if (!WannaBee.this.navigation.isDone() || this.ticks % 100 == 0) {
                    BlockPos blockPos = WannaBee.this.targetEntity.blockPosition();
                    WannaBee.this.navigation.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0D);
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
            if (!WannaBee.this.isAngry() && WannaBee.this.hasNectar() && WannaBee.this.getAnimalsBredSincePollination() <= ProductiveBeesConfig.BEES.cupidBeeAnimalsPerPollination.get()) {
                List<Entity> breedablesNearby = WannaBee.this.findNearbyBreedables(5);

                if (!breedablesNearby.isEmpty() && breedablesNearby.size() < ProductiveBeesConfig.BEES.cupidBeeAnimalDensity.get()) {
                    BlockPos beePos = WannaBee.this.blockPosition();
                    Animal nearest = null;
                    double nearestDistance = 0;
                    for (Entity entity : breedablesNearby) {
                        if (entity instanceof Animal) {
                            BlockPos pos = entity.blockPosition();
                            double distance = pos.distSqr(beePos);
                            if (nearestDistance == 0 || distance < nearestDistance) {
                                nearestDistance = distance;
                                nearest = (Animal) entity;
                            }
                        }
                    }

                    WannaBee.this.targetEntity = nearest;
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
            return WannaBee.this.targetEntity != null && WannaBee.this.hasNectar() && !WannaBee.this.isAngry();
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
            if (WannaBee.this.targetEntity != null) {
                if (ticks > 600) {
                    WannaBee.this.targetEntity = null;
                } else {
                    Vec3 vec3d = WannaBee.this.targetEntity.position().add(0.5D, 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(WannaBee.this.position());

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            WannaBee.this.targetEntity = null;
                        } else {
                            List<Entity> breedablesNearby = WannaBee.this.findNearbyBreedables(1);
                            if (!breedablesNearby.isEmpty()) {
                                Entity target = breedablesNearby.iterator().next();

                                if (target instanceof Animal) {
                                    if (!((Animal) target).isBaby() && ((Animal) target).canFallInLove()) {
                                        ((Animal) target).setInLove(null);
                                        WannaBee.this.addBreedCounter();
                                    }

                                    WannaBee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3 target) {
            WannaBee.this.getMoveControl().setWantedPosition(target.x, target.y, target.z, 1.0F);
        }
    }
}
