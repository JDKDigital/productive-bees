package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class CupidBeeEntity extends ProductiveBeeEntity
{
    public AnimalEntity targetEntity = null;

    public static Predicate<Entity> predicate = (entity -> entity instanceof AnimalEntity && !((AnimalEntity) entity).isInLove());

    public CupidBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.goalSelector.removeGoal(this.breedGoal);

        this.goalSelector.addGoal(2, new CupidBeeEntity.SetLoveModeGoal());
        this.goalSelector.addGoal(3, new CupidBeeEntity.LocateBreedableGoal());

        this.pollinateGoal = new CupidBeeEntity.PollinateGoal();
        this.goalSelector.addGoal(4, this.pollinateGoal);

        this.findFlowerGoal = new CupidBeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
    }

    public void livingTick() {
        super.livingTick();

        if (this.ticksExisted % 20 == 0) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }

    public List<Entity> findNearbyBreedables(float distance) {
        BlockPos pos = getPosition();

        return world.getEntitiesInAABBexcluding(this, (new AxisAlignedBB(pos).grow(distance, distance, distance)), predicate);
    }

    public class SetLoveModeGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean shouldExecute() {
            return !CupidBeeEntity.this.isAngry() && CupidBeeEntity.this.hasNectar();
        }

        public void startExecuting() {
            this.ticks = 0;
        }

        @Override
        public void tick() {
            if (CupidBeeEntity.this.targetEntity != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    CupidBeeEntity.this.targetEntity = null;
                } else if (!CupidBeeEntity.this.navigator.func_226337_n_() || this.ticks % 100 == 0) {
                    BlockPos blockPos = CupidBeeEntity.this.targetEntity.getPosition();
                    CupidBeeEntity.this.navigator.tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0D);
                }
            }
        }
    }

    public class LocateBreedableGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean shouldExecute() {
            if (!CupidBeeEntity.this.isAngry() && CupidBeeEntity.this.hasNectar()) {
                List<Entity> breedablesNearby = CupidBeeEntity.this.findNearbyBreedables(10);

                if (!breedablesNearby.isEmpty()) {
                    BlockPos beePos = CupidBeeEntity.this.getPosition();
                    AnimalEntity nearest = null;
                    double nearestDistance = 0;
                    for (Entity entity : breedablesNearby) {
                        if (entity instanceof AnimalEntity) {
                            BlockPos pos = entity.getPosition();
                            double distance = pos.distanceSq(beePos);
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
        public boolean shouldContinueExecuting() {
            return CupidBeeEntity.this.targetEntity != null && CupidBeeEntity.this.hasHive() && !CupidBeeEntity.this.isAngry();
        }

        public void startExecuting() {
            ticks = 0;
        }

        @Override
        public void tick() {
            ++ticks;
            if (CupidBeeEntity.this.targetEntity != null) {
                if (ticks > 600) {
                    CupidBeeEntity.this.targetEntity = null;
                } else {
                    Vec3d vec3d = (new Vec3d(CupidBeeEntity.this.targetEntity.getPosition())).add(0.5D, 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(CupidBeeEntity.this.getPositionVec());

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            CupidBeeEntity.this.targetEntity = null;
                        }
                        else {
                            List<Entity> breedablesNearby = CupidBeeEntity.this.findNearbyBreedables(1);
                            if (!breedablesNearby.isEmpty()) {
                                Entity target = breedablesNearby.iterator().next();

                                if (target instanceof AnimalEntity) {
                                    if (((AnimalEntity)target).getGrowingAge() == 0 && ((AnimalEntity)target).canBreed()) {
                                        ((AnimalEntity)target).setInLove(600);
                                    }

                                    CupidBeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
                                }
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3d target) {
            CupidBeeEntity.this.getMoveHelper().setMoveTo(target.getX(), target.getY(), target.getZ(), 1.0F);
        }
    }

    public class PollinateGoal extends BeeEntity.PollinateGoal
    {
        public boolean canBeeStart() {
            return false;
        }
    }

    public class FindFlowerGoal extends BeeEntity.FindFlowerGoal
    {
        public boolean canBeeStart() {
            return false;
        }
    }
}
