package cy.jdkdigital.productivebees.common.entity.bee.hive;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FarmerBeeEntity extends ProductiveBeeEntity
{
    public static final UUID FARMER_BEE_UUID = UUID.nameUUIDFromBytes("pb_farmer_bee".getBytes(StandardCharsets.UTF_8));
    private BlockPos targetHarvestPos = null;
    private LocateCropGoal locateCropGoal;

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
        this.locateCropGoal = new LocateCropGoal();
        this.goalSelector.addGoal(6, this.locateCropGoal);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(DamageSource.CACTUS) || super.isInvulnerableTo(source);
    }

    public List<BlockPos> findHarvestablesNearby(BlockPos pos, double distance) {
        List<BlockPos> list = BlockPos.betweenClosedStream(pos.offset(-distance, -distance + 2, -distance), pos.offset(distance, distance - 2, distance)).map(BlockPos::immutable).collect(Collectors.toList());
        list.removeIf(blockPos -> !isCropValid(blockPos));
        return list;
    }

    private boolean isCropValid(BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        }
        BlockState state = level.getBlockState(blockPos);
        Block block = state.getBlock();

        if (block instanceof CocoaBlock && state.getValue(CocoaBlock.AGE) == 2) {
            return true;
        }
        if (block instanceof SweetBerryBushBlock && state.getValue(SweetBerryBushBlock.AGE) == 3) {
            return true;
        }
        if (block instanceof StemGrownBlock) {
            return true;
        }

        // Cactus and sugarcane blocks taller than 1 are harvestable
        if (block instanceof CactusBlock || block instanceof SugarCaneBlock) {
            return level.getBlockState(blockPos.below()).getBlock().equals(state.getBlock());
        }

        return block instanceof CropsBlock && !((CropsBlock) block).isValidBonemealTarget(level, blockPos, state, false);
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
                    FarmerBeeEntity.this.locateCropGoal.cooldown = 120;
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
        private int cooldown = 0;

        @Override
        public boolean canUse() {
            if (--cooldown <= 0 && !FarmerBeeEntity.this.isAngry()) {
                FarmerBeeEntity.this.targetHarvestPos = findNearestHarvestableTarget(5);
                if (FarmerBeeEntity.this.targetHarvestPos != null) {
                    FarmerBeeEntity.this.targetHarvestPos = findNearestHarvestableTarget(10);
                }
                if (FarmerBeeEntity.this.targetHarvestPos == null) {
                    cooldown = 70;
                }
            }
            return FarmerBeeEntity.this.targetHarvestPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (FarmerBeeEntity.this.tickCount % 20 == 0 && !FarmerBeeEntity.this.isCropValid(FarmerBeeEntity.this.targetHarvestPos)) {
                FarmerBeeEntity.this.targetHarvestPos = null;
            }
            return FarmerBeeEntity.this.targetHarvestPos != null && !FarmerBeeEntity.this.isAngry();
        }

        private BlockPos findNearestHarvestableTarget(int radius) {
            List<BlockPos> harvestablesNearby = FarmerBeeEntity.this.findHarvestablesNearby(FarmerBeeEntity.this.blockPosition(), radius);

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
                return nearest;
            }
            return null;
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

                    if (distanceToTarget > 1.5D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            // reset when unable to harvest
                            FarmerBeeEntity.this.locateCropGoal.cooldown = 120;
                            FarmerBeeEntity.this.targetHarvestPos = null;
                        } else {
                            BlockPos pos = FarmerBeeEntity.this.targetHarvestPos; //harvestablesNearby.iterator().next();
                            if (FarmerBeeEntity.this.isCropValid(pos)) {
                                BlockState cropBlockState = FarmerBeeEntity.this.level.getBlockState(pos);
                                Block cropBlock = cropBlockState.getBlock();
                                if (cropBlock instanceof AttachedStemBlock) {
                                    BlockState fruitBlock = FarmerBeeEntity.this.level.getBlockState(pos.relative(cropBlockState.getValue(HorizontalBlock.FACING)));
                                    if (fruitBlock.getBlock() instanceof StemGrownBlock) {
                                        FarmerBeeEntity.this.level.destroyBlock(pos.relative(cropBlockState.getValue(HorizontalBlock.FACING)), true);
                                    }
                                } else if (cropBlock instanceof SugarCaneBlock || cropBlock instanceof CactusBlock) {
                                    int i = 0;
                                    while (i++ < 5 && FarmerBeeEntity.this.level.getBlockState(pos.below()).getBlock().equals(cropBlock)) {
                                        pos = pos.below();
                                    }
                                    FarmerBeeEntity.this.level.destroyBlock(pos.above(), true);
                                } else if (cropBlock instanceof SweetBerryBushBlock) {
                                    int i = cropBlockState.getValue(SweetBerryBushBlock.AGE);
                                    if (i > 1) {
                                        int j = 1 + FarmerBeeEntity.this.level.random.nextInt(2);
                                        Block.popResource(FarmerBeeEntity.this.level, pos, new ItemStack(Items.SWEET_BERRIES, j + (i == 3 ? 1 : 0)));
                                        FarmerBeeEntity.this.level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, 0.8F + FarmerBeeEntity.this.level.random.nextFloat() * 0.4F);
                                        FarmerBeeEntity.this.level.setBlock(pos, cropBlockState.setValue(SweetBerryBushBlock.AGE, 1), 2);
                                    }
                                } else {
                                    // right click crop if certain mods are installed
                                    if (
                                            ModList.get().isLoaded("right_click_get_crops") ||
                                            ModList.get().isLoaded("croptopia") ||
                                            ModList.get().isLoaded("quark") ||
                                            ModList.get().isLoaded("harvest") ||
                                            ModList.get().isLoaded("pamhc2crops") ||
                                            ModList.get().isLoaded("simplefarming") ||
                                            ModList.get().isLoaded("reap")
                                    ) {
                                        PlayerEntity fakePlayer = FakePlayerFactory.get((ServerWorld) FarmerBeeEntity.this.level, new GameProfile(FARMER_BEE_UUID, "farmer_bee"));
                                        ForgeHooks.onRightClickBlock(fakePlayer, Hand.MAIN_HAND, pos, FarmerBeeEntity.this.getMotionDirection());
                                    } else {
                                        FarmerBeeEntity.this.level.destroyBlock(pos, true);
                                    }
                                }
                            }

                            FarmerBeeEntity.this.targetHarvestPos = null;
                            FarmerBeeEntity.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
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
