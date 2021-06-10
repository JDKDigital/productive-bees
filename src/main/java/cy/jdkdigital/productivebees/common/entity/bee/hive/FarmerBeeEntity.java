package cy.jdkdigital.productivebees.common.entity.bee.hive;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FarmerBeeEntity extends ProductiveBeeEntity
{
    public static final UUID FARMER_BEE_UUID = UUID.nameUUIDFromBytes("pb_farmer_bee".getBytes(StandardCharsets.UTF_8));
    private BlockPos targetHarvestPos = null;

    public FarmerBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
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
        this.goalSelector.addGoal(6, new LocateCropGoal());
    }

    public List<BlockPos> findHarvestablesNearby(double distance) {
        return findHarvestablesNearby(this.blockPosition(), distance);
    }

    public List<BlockPos> findHarvestablesNearby(BlockPos pos, double distance) {
        List<BlockPos> list = BlockPos.betweenClosedStream(pos.offset(-distance, -distance + 2, -distance), pos.offset(distance, distance - 2, distance)).map(BlockPos::immutable).collect(Collectors.toList());
        Iterator<BlockPos> iterator = list.iterator();
        while (iterator.hasNext()) {
            BlockPos blockPos = iterator.next();
            BlockState state = level.getBlockState(blockPos);
            if (!(state.getBlock() instanceof CropsBlock) || ((CropsBlock) state.getBlock()).isValidBonemealTarget(level, blockPos, state, false)) {
                iterator.remove();
            }
        }
        return list;
    }

    public class HarvestCropGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            if (FarmerBeeEntity.this.targetHarvestPos != null && !positionIsHarvestable(FarmerBeeEntity.this.targetHarvestPos)) {
                FarmerBeeEntity.this.targetHarvestPos = null;
            }

            return
                    FarmerBeeEntity.this.targetHarvestPos != null &&
                    !FarmerBeeEntity.this.isAngry() &&
                    !FarmerBeeEntity.this.closerThan(FarmerBeeEntity.this.targetHarvestPos, 2);
        }

        public void start() {
            this.ticks = 0;
        }

        public void tick() {
            if (FarmerBeeEntity.this.targetHarvestPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    FarmerBeeEntity.this.targetHarvestPos = null;
                } else if (!FarmerBeeEntity.this.navigation.isDone()) {
                    BlockPos blockPos = FarmerBeeEntity.this.targetHarvestPos;
                    FarmerBeeEntity.this.navigation.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0D);
                }
            }
        }

        private boolean positionIsHarvestable(BlockPos pos) {
            return !FarmerBeeEntity.this.findHarvestablesNearby(pos, 0).isEmpty();
        }
    }

    public class LocateCropGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            if (!FarmerBeeEntity.this.isAngry()) {
                List<BlockPos> harvestablesNearby = FarmerBeeEntity.this.findHarvestablesNearby(10);

                Collections.shuffle(harvestablesNearby);

                if (!harvestablesNearby.isEmpty()) {
                    BlockPos nearest = null;
                    double nearestDistance = 0;
                    for (BlockPos pos : harvestablesNearby) {
                        double distance = pos.distSqr(FarmerBeeEntity.this.blockPosition());
                        if (nearestDistance == 0 || distance <= nearestDistance) {
                            nearestDistance = distance;
                            nearest = pos;
                        }
                    }

                    FarmerBeeEntity.this.targetHarvestPos = nearest;

                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return FarmerBeeEntity.this.targetHarvestPos != null && FarmerBeeEntity.this.hasHive() && !FarmerBeeEntity.this.isAngry();
        }

        public void start() {
            ticks = 0;
        }

        @Override
        public void tick() {
            ++ticks;
            if (FarmerBeeEntity.this.targetHarvestPos != null) {
                if (ticks > 600) {
                    FarmerBeeEntity.this.targetHarvestPos = null;
                } else {
                    Vector3d vec3d = (Vector3d.atCenterOf(FarmerBeeEntity.this.targetHarvestPos)).add(0.5D, 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(FarmerBeeEntity.this.position());

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            FarmerBeeEntity.this.targetHarvestPos = null;
                        } else {
                            List<BlockPos> harvestablesNearby = FarmerBeeEntity.this.findHarvestablesNearby(0);
                            if (!harvestablesNearby.isEmpty() && level instanceof ServerWorld) {
                                BlockPos pos = harvestablesNearby.iterator().next();

                                // right click if certain mods are installed
                                if ((ModList.get().isLoaded("quark") || ModList.get().isLoaded("pamhc2crops") || ModList.get().isLoaded("simplefarming") || ModList.get().isLoaded("reap"))) {
                                    PlayerEntity fakePlayer = FakePlayerFactory.get((ServerWorld) level, new GameProfile(FARMER_BEE_UUID, "farmer_bee"));
                                    ForgeHooks.onRightClickBlock(fakePlayer, Hand.MAIN_HAND, pos, FarmerBeeEntity.this.getMotionDirection());
                                } else {
                                    level.destroyBlock(pos, true);
                                }

                                FarmerBeeEntity.this.targetHarvestPos = null;

                                FarmerBeeEntity.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vector3d nextTarget) {
            FarmerBeeEntity.this.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }
}
