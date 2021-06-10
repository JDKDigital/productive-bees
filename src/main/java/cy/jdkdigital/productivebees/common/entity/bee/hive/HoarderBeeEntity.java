package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;

public class HoarderBeeEntity extends ProductiveBeeEntity
{
    protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.defineId(HoarderBeeEntity.class, DataSerializers.BYTE);
    private float prevPeekAmount;
    private float peekAmount = 1.0F;
    public BlockPos targetItemPos = null;
    private final Inventory inventory;
    private int outOfHiveCounter = 0;

    public HoarderBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        inventory = new Inventory(getBeeName().equals("hoarder") ? 3 : 1);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.goalSelector.removeGoal(this.breedGoal);

        // Pickup item goal
        this.goalSelector.addGoal(4, new PickupItemGoal());

        // Move to item goal and pick it up
        this.goalSelector.addGoal(6, new LocateItemGoal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PEEK_TICK, (byte) 100);
    }

    @Override
    public void tick() {
        super.tick();

        float f1 = (float) this.getPeekTick() * 0.01F;
        prevPeekAmount = peekAmount;
        if (peekAmount > f1) {
            peekAmount = MathHelper.clamp(peekAmount - 0.05F, f1, 1.0F);
        } else if (peekAmount < f1) {
            peekAmount = MathHelper.clamp(peekAmount + 0.05F, 0.0F, f1);
        }
    }

    public int getTimeInHive(boolean hasNectar) {
        return 100;
    }

    public int getPeekTick() {
        return this.entityData.get(PEEK_TICK);
    }

    public float getClientPeekAmount(float p_184688_1_) {
        return MathHelper.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(PEEK_TICK, tag.getByte("Peek"));

        if (tag.contains("targetItemPos")) {
            targetItemPos = NBTUtil.readBlockPos(tag.getCompound("targetItemPos"));
        }

        if (tag.contains("inventory")) {
            ListNBT listnbt = tag.getList("inventory", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < listnbt.size(); ++i) {
                ItemStack itemstack = ItemStack.of(listnbt.getCompound(i));
                if (!itemstack.isEmpty()) {
                    inventory.addItem(itemstack);
                }
            }
            tag.remove("inventory");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Peek", this.entityData.get(PEEK_TICK));

        if (targetItemPos != null) {
            tag.put("targetItemPos", NBTUtil.writeBlockPos(targetItemPos));
        }

        if (!inventory.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack itemstack = inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    listnbt.add(itemstack.save(new CompoundNBT()));
                }
            }

            tag.put("inventory", listnbt);
        }
    }

    @Override
    public void resetTicksWithoutNectarSinceExitingHive() {
        super.resetTicksWithoutNectarSinceExitingHive();
        outOfHiveCounter = 0;
    }

    public void openAbdomen() {
        this.entityData.set(PEEK_TICK, (byte) 0);
    }

    public void closeAbdomen() {
        this.entityData.set(PEEK_TICK, (byte) 100);
    }

    public boolean holdsItem() {
        return !inventory.isEmpty();
    }

    public void emptyIntoInventory(InventoryHandlerHelper.ItemHandler inv) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (inv.addOutput(itemstack.copy())) {
                inventory.removeItemNoUpdate(i);
            }
        }
        inventory.setChanged();
    }

    @Override
    public boolean wantsToEnterHive() {
        return outOfHiveCounter > 600 || !inventoryHasSpace() || super.wantsToEnterHive();
    }

    @Override
    public void die(@Nonnull DamageSource damageSource) {
        super.die(damageSource);
        if (!isInventoryEmpty()) {
            InventoryHelper.dropContents(level, this, inventory);
        }
    }

    public List<ItemEntity> getItemsNearby(double distance) {
        return getItemsNearby(blockPosition(), distance);
    }

    public List<ItemEntity> getItemsNearby(BlockPos pos, double distance) {
        return level.getEntitiesOfClass(ItemEntity.class, (new AxisAlignedBB(pos).expandTowards(distance, distance, distance)));
    }

    public boolean isInventoryEmpty() {
        return inventory.isEmpty();
    }

    private boolean inventoryHasSpace() {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (itemstack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public class PickupItemGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            if (HoarderBeeEntity.this.targetItemPos != null && !positionHasItemEntity(HoarderBeeEntity.this.targetItemPos)) {
                HoarderBeeEntity.this.targetItemPos = null;
            }
            return
                    HoarderBeeEntity.this.targetItemPos != null &&
                    HoarderBeeEntity.this.inventoryHasSpace() &&
                    !HoarderBeeEntity.this.isAngry() &&
                    !HoarderBeeEntity.this.closerThan(HoarderBeeEntity.this.targetItemPos, 2);
        }

        public void start() {
            this.ticks = 0;
            super.start();
        }

        public void tick() {
            HoarderBeeEntity.this.outOfHiveCounter++;
            if (HoarderBeeEntity.this.targetItemPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    HoarderBeeEntity.this.targetItemPos = null;
                } else if (!HoarderBeeEntity.this.navigation.isStuck()) {
                    BlockPos itemPos = HoarderBeeEntity.this.targetItemPos;
                    HoarderBeeEntity.this.navigation.moveTo(itemPos.getX(), itemPos.getY(), itemPos.getZ(), 1.0D);
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
        public boolean canUse() {
            boolean canStart =
                    HoarderBeeEntity.this.inventoryHasSpace() &&
                    !HoarderBeeEntity.this.isAngry();

            if (canStart) {
                List<ItemEntity> items = HoarderBeeEntity.this.getItemsNearby(10);

                if (!items.isEmpty()) {
                    BlockPos nearestItemLocation = null;
                    double nearestItemDistance = 0;
                    BlockPos beeLocation = HoarderBeeEntity.this.blockPosition();
                    int i = 0;
                    for (ItemEntity item : items) {
                        BlockPos itemLocation = new BlockPos(item.getX(), item.getY(), item.getZ());
                        double distance = itemLocation.distSqr(beeLocation);
                        if (nearestItemDistance == 0 || distance < nearestItemDistance) {
                            nearestItemDistance = distance;
                            nearestItemLocation = itemLocation;
                        }

                        // Don't look at more than 10 items
                        if (++i > 10) {
                            break;
                        }
                    }

                    HoarderBeeEntity.this.targetItemPos = nearestItemLocation;

                    return true;
                }

            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return HoarderBeeEntity.this.targetItemPos != null && HoarderBeeEntity.this.inventoryHasSpace() && !HoarderBeeEntity.this.isAngry();
        }

        public void start() {
            ticks = 0;
        }

        @Override
        public void stop() {
            ticks = 0;
            HoarderBeeEntity.this.closeAbdomen();
        }

        @Override
        public void tick() {
            ++ticks;
            if (HoarderBeeEntity.this.targetItemPos != null) {
                if (ticks > 600) {
                    HoarderBeeEntity.this.targetItemPos = null;
                } else {
                    Vector3d vec3d = Vector3d.atCenterOf(HoarderBeeEntity.this.targetItemPos).add(0.0D, 0.6F, 0.0D);
                    double distanceToTarget = vec3d.distanceTo(HoarderBeeEntity.this.position());

                    if (distanceToTarget < 2.0D && distanceToTarget > 0.2D) {
                        HoarderBeeEntity.this.openAbdomen();
                    }

                    if (distanceToTarget > 1.0D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.1D && ticks > 600) {
                            HoarderBeeEntity.this.targetItemPos = null;
                        } else {
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

                                HoarderBeeEntity.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vector3d nextTarget) {
            HoarderBeeEntity.this.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }
}
