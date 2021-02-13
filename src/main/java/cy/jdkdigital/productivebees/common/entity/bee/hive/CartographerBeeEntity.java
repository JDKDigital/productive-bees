package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CartographerBeeEntity extends ProductiveBeeEntity
{
    @Nullable
    public BlockPos targetItemPos = null;
    private final Inventory inventory = new Inventory(1);

    public CartographerBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new StingGoal(this, 1.4D, true));
        // Resting goal!
        this.goalSelector.addGoal(1, new EnterBeehiveGoal());
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D, CartographerBeeEntity.class));

        this.pollinateGoal = new PollinateGoal();
        this.goalSelector.addGoal(4, this.pollinateGoal);
        // Pickup item goal
//        this.goalSelector.addGoal(4, new PickupItemGoal());

        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));

        this.goalSelector.addGoal(5, new UpdateNestGoal());
        this.findBeehiveGoal = new FindNestGoal();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);

        this.findFlowerGoal = new FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);
        // Move to item goal and pick it up
//        this.goalSelector.addGoal(6, new LocateItemGoal());

        this.goalSelector.addGoal(8, new WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));

        this.targetSelector.addGoal(1, (new AngerGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new AttackPlayerGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

    }


    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

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

        if (targetItemPos != null) {
            tag.put("targetItemPos", NBTUtil.writeBlockPos(targetItemPos));
        }

        if (!inventory.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for (int i = 0; i < inventory.getSizeInventory(); ++i) {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (!itemstack.isEmpty()) {
                    listnbt.add(itemstack.write(new CompoundNBT()));
                }
            }

            tag.put("inventory", listnbt);
        }
    }

    public boolean holdsMap() {
        return !inventory.isEmpty();
    }

    public ItemStack getMap() {
        return inventory.getStackInSlot(0);
    }

    @Override
    public boolean canEnterHive() {
        return !holdsMap() || super.canEnterHive();
    }

    @Override
    public void onDeath(@Nonnull DamageSource damageSource) {
        super.onDeath(damageSource);
        if (holdsMap()) {
            InventoryHelper.dropInventoryItems(world, getPosition(), inventory);
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
