package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.compat.harvest.HarvestCompatHandler;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FarmerBee extends ProductiveBee
{
    public static final UUID FARMER_BEE_UUID = UUID.nameUUIDFromBytes("pb_farmer_bee".getBytes(StandardCharsets.UTF_8)); // 4b9dd067-5433-3648-90a3-0d48ac6041f7
    private BlockPos targetHarvestPos = null;
    private LocateCropGoal locateCropGoal;

    public FarmerBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        // Harvest crop goal
        this.goalSelector.addGoal(4, new HarvestCropGoal());

        // Locate crop goal
        this.locateCropGoal = new LocateCropGoal();
        this.goalSelector.addGoal(6, this.locateCropGoal);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(this.level().damageSources().cactus()) || super.isInvulnerableTo(source);
    }

    public List<BlockPos> findHarvestablesNearby(BlockPos pos, int distance) {
        List<BlockPos> list = BlockPos.betweenClosedStream(pos.offset(-distance, -distance + 2, -distance), pos.offset(distance, distance - 2, distance)).map(BlockPos::immutable).collect(Collectors.toList());
        list.removeIf(blockPos -> this.level().getBlockState(blockPos).isAir());
        list.removeIf(blockPos -> !isCropValid(blockPos));
        return list;
    }

    public boolean isCropValid(BlockPos blockPos) {
        return HarvestCompatHandler.isCropValid(this, blockPos);
    }

    public class HarvestCropGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            if (FarmerBee.this.targetHarvestPos != null && !positionIsHarvestable(FarmerBee.this.targetHarvestPos)) {
                FarmerBee.this.targetHarvestPos = null;
            }

            return FarmerBee.this.targetHarvestPos != null &&
                            !FarmerBee.this.isAngry() &&
                            !FarmerBee.this.closerThan(FarmerBee.this.targetHarvestPos, 2);
        }

        @Override
        public void start() {
            this.ticks = 0;
        }

        @Override
        public void tick() {
            if (FarmerBee.this.targetHarvestPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    FarmerBee.this.locateCropGoal.cooldown = 120;
                    FarmerBee.this.targetHarvestPos = null;
                } else if (!FarmerBee.this.navigation.isDone()) {
                    BlockPos blockPos = FarmerBee.this.targetHarvestPos;
                    FarmerBee.this.navigation.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0D);
                }
            }
        }

        private boolean positionIsHarvestable(BlockPos pos) {
            return !FarmerBee.this.findHarvestablesNearby(pos, 0).isEmpty();
        }
    }

    public class LocateCropGoal extends Goal
    {
        private int ticks = 0;
        private int cooldown = 0;

        @Override
        public boolean canUse() {
            if (--cooldown <= 0 && !FarmerBee.this.isAngry()) {
                FarmerBee.this.targetHarvestPos = findNearestHarvestableTarget();
                if (FarmerBee.this.targetHarvestPos == null) {
                    cooldown = 70;
                }
            }
            return FarmerBee.this.targetHarvestPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (FarmerBee.this.tickCount % 20 == 0 && !FarmerBee.this.isCropValid(FarmerBee.this.targetHarvestPos)) {
                FarmerBee.this.targetHarvestPos = null;
            }
            return FarmerBee.this.targetHarvestPos != null && !FarmerBee.this.isAngry();
        }

        private BlockPos findNearestHarvestableTarget() {
            if (FarmerBee.this.hivePos != null) {
                BlockEntity hive = FarmerBee.this.level().getBlockEntity(FarmerBee.this.hivePos);
                if (hive instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                    int radius = 5 + beehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get());
                    List<BlockPos> harvestablesNearby = FarmerBee.this.findHarvestablesNearby(FarmerBee.this.hivePos, radius);

                    if (!harvestablesNearby.isEmpty()) {
                        BlockPos nearest = null;
                        double nearestDistance = 0;
                        for (BlockPos pos : harvestablesNearby) {
                            double distance = pos.distSqr(FarmerBee.this.blockPosition());
                            if (nearestDistance == 0 || distance <= nearestDistance) {
                                nearestDistance = distance;
                                nearest = pos;
                            }
                        }
                        return nearest;
                    }
                }
            }
            return null;
        }

        public void start() {
            ticks = 0;
        }

        @Override
        public void tick() {
            ++ticks;
            if (FarmerBee.this.targetHarvestPos != null) {
                if (ticks > 600) {
                    FarmerBee.this.targetHarvestPos = null;
                } else {
                    Vec3 vec3d = (Vec3.atCenterOf(FarmerBee.this.targetHarvestPos)).add(0.5D, 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(FarmerBee.this.position());

                    if (distanceToTarget > 1.5D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            // reset when unable to harvest
                            FarmerBee.this.locateCropGoal.cooldown = 120;
                            FarmerBee.this.targetHarvestPos = null;
                        } else {
                            BlockPos pos = FarmerBee.this.targetHarvestPos;
                            if (FarmerBee.this.isCropValid(pos)) {
                                FarmerBee.this.harvestBlock(pos);
                            }

                            FarmerBee.this.targetHarvestPos = null;
                            FarmerBee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3 nextTarget) {
            FarmerBee.this.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }

    public void harvestBlock(BlockPos pos) {
        HarvestCompatHandler.harvestBlock(this, pos);
    }
}
