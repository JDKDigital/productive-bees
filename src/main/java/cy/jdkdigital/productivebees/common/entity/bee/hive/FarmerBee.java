package cy.jdkdigital.productivebees.common.entity.bee.hive;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FarmerBee extends ProductiveBee
{
    public static final UUID FARMER_BEE_UUID = UUID.nameUUIDFromBytes("pb_farmer_bee".getBytes(StandardCharsets.UTF_8));
    private BlockPos targetHarvestPos = null;

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
            if (!(state.getBlock() instanceof CropBlock) || ((CropBlock) state.getBlock()).isValidBonemealTarget(level, blockPos, state, false)) {
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
            if (FarmerBee.this.targetHarvestPos != null && !positionIsHarvestable(FarmerBee.this.targetHarvestPos)) {
                FarmerBee.this.targetHarvestPos = null;
            }

            return
                    FarmerBee.this.targetHarvestPos != null &&
                    !FarmerBee.this.isAngry() &&
                    !FarmerBee.this.closerThan(FarmerBee.this.targetHarvestPos, 2);
        }

        public void start() {
            this.ticks = 0;
        }

        public void tick(Level level, BlockState state) {
            if (FarmerBee.this.targetHarvestPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
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

        @Override
        public boolean canUse() {
            if (!FarmerBee.this.isAngry()) {
                List<BlockPos> harvestablesNearby = FarmerBee.this.findHarvestablesNearby(10);

                Collections.shuffle(harvestablesNearby);

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

                    FarmerBee.this.targetHarvestPos = nearest;

                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return FarmerBee.this.targetHarvestPos != null && FarmerBee.this.hasHive() && !FarmerBee.this.isAngry();
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

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            FarmerBee.this.targetHarvestPos = null;
                        } else {
                            List<BlockPos> harvestablesNearby = FarmerBee.this.findHarvestablesNearby(0);
                            if (!harvestablesNearby.isEmpty() && level instanceof ServerLevel) {
                                BlockPos pos = harvestablesNearby.iterator().next();

                                // right click if certain mods are installed
                                if ((ModList.get().isLoaded("quark") || ModList.get().isLoaded("pamhc2crops") || ModList.get().isLoaded("simplefarming") || ModList.get().isLoaded("reap"))) {
                                    Player fakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(FARMER_BEE_UUID, "farmer_bee"));
                                    ForgeHooks.onRightClickBlock(fakePlayer, InteractionHand.MAIN_HAND, pos, new BlockHitResult(FarmerBee.this.getEyePosition(), FarmerBee.this.getMotionDirection(), pos, true));
                                } else {
                                    level.destroyBlock(pos, true);
                                }

                                FarmerBee.this.targetHarvestPos = null;

                                FarmerBee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3 nextTarget) {
            FarmerBee.this.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }
}
