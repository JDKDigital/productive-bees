package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HoarderBeeEntity extends ProductiveBeeEntity
{
    protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.createKey(HoarderBeeEntity.class, DataSerializers.BYTE);
    private float prevPeekAmount;
    private float peekAmount = 1.0F;
    @Nullable
    public BlockPos targetItemPos = null;
    private final Inventory inventory = new Inventory(1);

    public HoarderBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4D, true));
        // Resting goal!
        this.goalSelector.addGoal(1, new BeeEntity.EnterBeehiveGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, HoarderBeeEntity.class));

        this.pollinateGoal = new PollinateGoal();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        // Pickup item goal
        this.goalSelector.addGoal(4, new PickupItemGoal());

        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));

        this.goalSelector.addGoal(5, new ProductiveBeeEntity.UpdateNestGoal());
        this.findBeehiveGoal = new ProductiveBeeEntity.FindNestGoal();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);

        this.findFlowerGoal = new FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
        // Move to item goal and pick it up
        this.goalSelector.addGoal(6, new LocateItemGoal());

        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));

        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PEEK_TICK, (byte)100);
    }

    @Override
    public void tick() {
        super.tick();

        float f1 = (float)this.getPeekTick() * 0.01F;
        prevPeekAmount = peekAmount;
        if (peekAmount > f1) {
            peekAmount = MathHelper.clamp(peekAmount - 0.05F, f1, 1.0F);
        } else if (peekAmount < f1) {
            peekAmount = MathHelper.clamp(peekAmount + 0.05F, 0.0F, f1);
        }
    }

    public int getPeekTick() {
        return this.dataManager.get(PEEK_TICK);
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientPeekAmount(float p_184688_1_) {
        return MathHelper.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.dataManager.set(PEEK_TICK, tag.getByte("Peek"));

        if (tag.contains("targetItemPos")) {
            targetItemPos = NBTUtil.readBlockPos(tag.getCompound("targetItemPos"));
        }

        if (tag.contains("inventory")) {
            ListNBT listnbt = tag.getList("inventory", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < listnbt.size(); ++i) {
                ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
                if (!itemstack.isEmpty()) {
                    inventory.addItem(itemstack);
                }
            }
            tag.remove("inventory");
        }
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putByte("Peek", this.dataManager.get(PEEK_TICK));

        if (targetItemPos != null) {
            tag.put("targetItemPos", NBTUtil.writeBlockPos(targetItemPos));
        }

        if (!inventory.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for(int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    listnbt.add(itemstack.write(new CompoundNBT()));
                }
            }

            tag.put("inventory", listnbt);
        }
    }

    public void openAbdomen() {
        this.dataManager.set(PEEK_TICK, (byte)0);
    }

    public void closeAbdomen() {
        this.dataManager.set(PEEK_TICK, (byte)100);
    }

    public boolean holdsItem() {
        return !inventory.isEmpty();
    }

    public ItemStack getItem() {
        return inventory.getStackInSlot(0);
    }

    public void clearInventory() {
        inventory.clear();
    }

    @Override
    public boolean canEnterHive() {
        return holdsItem() || super.canEnterHive();
    }

    @Override
    public void onDeath(@Nonnull DamageSource damageSource) {
        super.onDeath(damageSource);
        if (holdsItem()) {
            InventoryHelper.dropInventoryItems(world, getPosition(), inventory);
        }
    }

    public List<ItemEntity> getItemsNearby(double distance) {
        return getItemsNearby(this.getPosition(), distance);
    }

    public List<ItemEntity> getItemsNearby(BlockPos pos, double distance) {
        return world.getEntitiesWithinAABB(ItemEntity.class, (new AxisAlignedBB(pos).grow(distance, distance, distance)));
    }

    public class PickupItemGoal extends Goal {
        private int ticks = 0;

        @Override
        public boolean shouldExecute() {
            if (HoarderBeeEntity.this.targetItemPos != null && !positionHasItemEntity(HoarderBeeEntity.this.targetItemPos)) {
                HoarderBeeEntity.this.targetItemPos = null;
            }
            return
                HoarderBeeEntity.this.targetItemPos != null &&
//                HoarderBeeEntity.this.hasHive() &&
                !HoarderBeeEntity.this.holdsItem() && !HoarderBeeEntity.this.isAngry() &&
                !HoarderBeeEntity.this.isWithinDistance(HoarderBeeEntity.this.targetItemPos, 2);
        }

        @Override
        public boolean shouldContinueExecuting() {
            return shouldExecute();
        }

        public void startExecuting() {
            this.ticks = 0;
            super.startExecuting();
        }

        public void tick() {
            if (HoarderBeeEntity.this.targetItemPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    HoarderBeeEntity.this.targetItemPos = null;
                } else if (!HoarderBeeEntity.this.navigator.func_226337_n_()) {
                    BlockPos itemPos = HoarderBeeEntity.this.targetItemPos;
                    HoarderBeeEntity.this.navigator.tryMoveToXYZ(itemPos.getX(), itemPos.getY(), itemPos.getZ(), 1.0D);
                }
            }
        }

        private boolean positionHasItemEntity(BlockPos pos) {
            return !HoarderBeeEntity.this.getItemsNearby(pos, 0).isEmpty();
        }
    }

    public class LocateItemGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean shouldExecute() {
            boolean canStart =
//                    HoarderBeeEntity.this.hasHive() &&
                !HoarderBeeEntity.this.holdsItem() &&
                !HoarderBeeEntity.this.isAngry();

            if (canStart) {
                List<ItemEntity> items = HoarderBeeEntity.this.getItemsNearby(10);

                if (!items.isEmpty()) {
                    ItemEntity nearestItem = null;
                    double nearestItemDistance = 0;
                    int i = 0;
                    for (ItemEntity item: items) {
                        double distance = item.getPosition().distanceSq(HoarderBeeEntity.this.getPosition());
                        if (nearestItemDistance == 0 || distance < nearestItemDistance) {
                            nearestItemDistance = distance;
                            nearestItem = item;
                        }

                        // Don't look at more than 10 items
                        if (++i > 10) {
                            break;
                        }
                    }

                    HoarderBeeEntity.this.targetItemPos = nearestItem.getPosition();

                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return HoarderBeeEntity.this.targetItemPos != null && HoarderBeeEntity.this.hasHive() && !HoarderBeeEntity.this.holdsItem() && !HoarderBeeEntity.this.isAngry();
        }

        public void startExecuting() {
            ticks = 0;
        }

        @Override
        public void resetTask() {
            ticks = 0;
            HoarderBeeEntity.this.closeAbdomen();
        }

        @Override
        public void tick() {
            ++ticks;
            if (HoarderBeeEntity.this.targetItemPos != null) {
                if (ticks > 600) {
                    HoarderBeeEntity.this.targetItemPos = null;
                }
                else {
                    Vec3d vec3d = (new Vec3d(HoarderBeeEntity.this.targetItemPos)).add(0.5D, (double) 0.6F, 0.5D);
                    double distanceToTarget = vec3d.distanceTo(HoarderBeeEntity.this.getPositionVec());

                    if (distanceToTarget < 2.0D && distanceToTarget > 0.2D) {
                        HoarderBeeEntity.this.openAbdomen();
                    }

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    }
                    else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            HoarderBeeEntity.this.targetItemPos = null;
                        }
                        else {
                            // Pick up item
                            List<ItemEntity> items = HoarderBeeEntity.this.getItemsNearby(0);
                            if (!items.isEmpty()) {
                                ItemEntity item = items.iterator().next();
                                ItemStack itemstack = item.getItem().copy();

                                ItemStack remaining = HoarderBeeEntity.this.inventory.addItem(itemstack);
                                if (remaining.isEmpty()) {
                                    item.remove();
                                } else {
                                    item.setItem(remaining);
                                }

                                HoarderBeeEntity.this.closeAbdomen();

                                HoarderBeeEntity.this.playSound(SoundEvents.ENTITY_BEE_POLLINATE, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3d nextTarget) {
            HoarderBeeEntity.this.getMoveHelper().setMoveTo(nextTarget.getX(), nextTarget.getY(), nextTarget.getZ(), 1.0F);
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
