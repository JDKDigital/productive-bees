package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HoarderBee extends ProductiveBee
{
    protected static final EntityDataAccessor<Byte> PEEK_TICK = SynchedEntityData.defineId(HoarderBee.class, EntityDataSerializers.BYTE);
    private float prevPeekAmount;
    private float peekAmount = 1.0F;
    public BlockPos targetItemPos = null;
    private final SimpleContainer inventory;
    private int outOfHiveCounter = 0;

    public HoarderBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        inventory = new SimpleContainer(getBeeName().equals("hoarder") ? 3 : 1);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    public int getInventorySize() {
        return inventory.getContainerSize();
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.goalSelector.removeGoal(this.breedGoal);

        // Pickup item goal
        this.goalSelector.addGoal(3, new PickupItemGoal());

        // Move to item goal and pick it up
        this.goalSelector.addGoal(3, new LocateItemGoal());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(PEEK_TICK, (byte) 100);
    }

    @Override
    public void tick() {
        super.tick();

        float f1 = (float) this.getPeekTick() * 0.01F;
        prevPeekAmount = peekAmount;
        if (peekAmount > f1) {
            peekAmount = Mth.clamp(peekAmount - 0.05F, f1, 1.0F);
        } else if (peekAmount < f1) {
            peekAmount = Mth.clamp(peekAmount + 0.05F, 0.0F, f1);
        }
    }

    public int getTimeInHive(boolean hasNectar) {
        return 100;
    }

    public int getPeekTick() {
        return this.entityData.get(PEEK_TICK);
    }

    public float getClientPeekAmount(float p_184688_1_) {
        return Mth.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(PEEK_TICK, tag.getByte("Peek"));

        if (tag.contains("targetItemPos")) {
            targetItemPos = NbtUtils.readBlockPos(tag.getCompound("targetItemPos"));
        }

        if (tag.contains("inventory")) {
            ListTag listnbt = tag.getList("inventory", 10);

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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("Peek", this.entityData.get(PEEK_TICK));

        if (targetItemPos != null) {
            tag.put("targetItemPos", NbtUtils.writeBlockPos(targetItemPos));
        }

        if (!inventory.isEmpty()) {
            ListTag listnbt = new ListTag();

            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                ItemStack itemstack = inventory.getItem(i);
                if (!itemstack.isEmpty()) {
                    listnbt.add(itemstack.save(new CompoundTag()));
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

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.spawnAtLocation(Items.SHULKER_SHELL);
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

    public void emptyIntoInventory(InventoryHandlerHelper.BlockEntityItemStackHandler inv) {
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemstack = inventory.getItem(i);
            if (inv.addOutput(itemstack.copy()).getCount() == 0) {
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
            Containers.dropContents(level(), this, inventory);
        }
    }

    public List<ItemEntity> getItemsNearby(double distance) {
        return getItemsNearby(blockPosition(), distance);
    }

    public List<ItemEntity> getItemsNearby(BlockPos pos, double distance) {
        return level().getEntitiesOfClass(ItemEntity.class, (new AABB(pos).inflate(distance, distance, distance)));
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
            if (HoarderBee.this.targetItemPos != null && !positionHasItemEntity(HoarderBee.this.targetItemPos)) {
                HoarderBee.this.targetItemPos = null;
            }
            return
                    HoarderBee.this.targetItemPos != null &&
                    HoarderBee.this.inventoryHasSpace() &&
                    !HoarderBee.this.isAngry() &&
                    !HoarderBee.this.closerThan(HoarderBee.this.targetItemPos, 2);
        }

        @Override
        public void start() {
            this.ticks = 0;
            super.start();
        }

        @Override
        public void tick() {
            HoarderBee.this.outOfHiveCounter++;
            if (HoarderBee.this.targetItemPos != null) {
                ++this.ticks;
                if (this.ticks > 600) {
                    HoarderBee.this.targetItemPos = null;
                } else if (!HoarderBee.this.navigation.isStuck()) {
                    BlockPos itemPos = HoarderBee.this.targetItemPos;
                    HoarderBee.this.navigation.moveTo(itemPos.getX(), itemPos.getY(), itemPos.getZ(), 1.0D);
                }
            }
        }

        private boolean positionHasItemEntity(BlockPos pos) {
            return !HoarderBee.this.getItemsNearby(pos, 1).isEmpty();
        }
    }

    public class LocateItemGoal extends Goal
    {
        private int ticks = 0;

        @Override
        public boolean canUse() {
            boolean canStart =
                    HoarderBee.this.inventoryHasSpace() &&
                    !HoarderBee.this.isAngry();

            if (canStart && HoarderBee.this.hivePos != null) {
                List<ItemEntity> items = new ArrayList<>();
                BlockEntity hive = level().getBlockEntity(HoarderBee.this.hivePos);
                if (hive instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                    int radius = 5 + beehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get());
                    items = HoarderBee.this.getItemsNearby(HoarderBee.this.hivePos, radius);
                }

                if (!items.isEmpty()) {
                    BlockPos nearestItemLocation = null;
                    double nearestItemDistance = -1;
                    BlockPos beeLocation = HoarderBee.this.blockPosition();
                    int i = 0;
                    for (ItemEntity item : items) {
                        BlockPos itemLocation = new BlockPos((int) item.getX(), (int) item.getY(), (int) item.getZ());
                        double distance = itemLocation.distSqr(beeLocation);
                        if (nearestItemDistance == -1 || distance < nearestItemDistance) {
                            nearestItemDistance = distance;
                            nearestItemLocation = itemLocation;
                        }

                        // Don't look at more than 10 items
                        if (++i > 10) {
                            break;
                        }
                    }

                    HoarderBee.this.targetItemPos = nearestItemLocation;

                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return HoarderBee.this.targetItemPos != null && HoarderBee.this.inventoryHasSpace() && !HoarderBee.this.isAngry();
        }

        @Override
        public void start() {
            ticks = 0;
        }

        @Override
        public void stop() {
            ticks = 0;
            HoarderBee.this.closeAbdomen();
        }

        @Override
        public void tick() {
            HoarderBee.this.outOfHiveCounter++;
            ++ticks;
            if (HoarderBee.this.targetItemPos != null) {
                if (ticks > 600) {
                    HoarderBee.this.targetItemPos = null;
                } else {
                    Vec3 vec3d = Vec3.atCenterOf(HoarderBee.this.targetItemPos).add(0.0D, 0.6F, 0.0D);
                    double distanceToTarget = vec3d.distanceTo(HoarderBee.this.position());

                    if (distanceToTarget < 2.0D && distanceToTarget > 0.2D) {
                        HoarderBee.this.openAbdomen();
                    }

                    if (distanceToTarget > 1.2D) {
                        this.moveToNextTarget(vec3d);
                    } else {
                        if (distanceToTarget > 0.5D && ticks > 600) {
                            HoarderBee.this.targetItemPos = null;
                        } else {
                            // Pick up item
                            List<ItemEntity> items = HoarderBee.this.getItemsNearby(1);
                            if (!items.isEmpty()) {
                                ItemEntity item = items.iterator().next();

                                HoarderBee.this.pickupItem(item);

                                HoarderBee.this.closeAbdomen();

                                HoarderBee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                            }
                        }
                    }
                }
            }
        }

        private void moveToNextTarget(Vec3 nextTarget) {
            HoarderBee.this.getMoveControl().setWantedPosition(nextTarget.x, nextTarget.y, nextTarget.z, 1.0F);
        }
    }

    public void pickupItem(ItemEntity item) {
        ItemStack itemstack = item.getItem().copy();

        ItemStack remaining = this.inventory.addItem(itemstack);
        if (remaining.isEmpty()) {
            item.discard();
        } else {
            item.setItem(remaining);
        }
    }
}
