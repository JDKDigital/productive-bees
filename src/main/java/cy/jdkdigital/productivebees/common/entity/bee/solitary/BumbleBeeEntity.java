package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BumbleBeeEntity extends SolitaryBeeEntity
{
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(BumbleBeeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(BumbleBeeEntity.class, DataSerializers.VARINT);
    private boolean boosting;
    private int boostTime;
    private int totalBoostTime;

    public BumbleBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.fromItems(ModItems.TREAT_ON_A_STICK.get()), false));
    }

    @Override
    public String getRenderer() {
        return "thicc";
    }

    @Override
    public Tag<Block> getNestingTag() {
        return ModTags.BUMBLE_BEE_NESTS;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> dataParameter) {
        if (BOOST_TIME.equals(dataParameter) && this.world.isRemote) {
            this.boosting = true;
            this.boostTime = 0;
            this.totalBoostTime = this.dataManager.get(BOOST_TIME);
        }

        super.notifyDataManagerChange(dataParameter);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SADDLED, false);
        this.dataManager.register(BOOST_TIME, 0);
    }

    public void setSaddled(boolean saddled) {
        if (saddled) {
            this.dataManager.set(SADDLED, true);
        } else {
            this.dataManager.set(SADDLED, false);
        }
    }

    public boolean getSaddled() {
        return this.dataManager.get(SADDLED);
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        if (super.processInteract(player, hand) || player.getHeldItemMainhand().getItem().equals(ModItems.HONEY_TREAT.get())) {
            return true;
        }

        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.getItem().equals(Items.NAME_TAG)) {
            return itemstack.interactWithEntity(player, this, hand);
        } else if (this.getSaddled() && !this.isBeingRidden()) {
            if (!this.world.isRemote) {
                player.startRiding(this);
            }
            return true;
        } else if (itemstack.getItem() == Items.SADDLE && isAlive() && !getSaddled() && !isChild()) {
            setSaddled(true);
            world.playSound(player, getPosX(), getPosY(), getPosZ(), SoundEvents.ENTITY_PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            itemstack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.getSaddled()) {
            this.entityDropItem(Items.SADDLE);
        }
    }

    @Override
    public boolean canBeSteered() {
        Entity entity = this.getControllingPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        } else {
            PlayerEntity playerentity = (PlayerEntity)entity;
            return playerentity.getHeldItemMainhand().getItem() == ModItems.TREAT_ON_A_STICK.get() || playerentity.getHeldItemOffhand().getItem() == ModItems.TREAT_ON_A_STICK.get();
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putBoolean("Saddle", this.getSaddled());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setSaddled(compound.getBoolean("Saddle"));
    }

    @Override
    public float getSizeModifier() {
        return 1.25F;
    }

    @Override
    public void travel(Vec3d vec3d) {
        if (this.isAlive()) {
            Entity rider = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
            if (this.isBeingRidden() && this.canBeSteered() && rider != null) {
                this.rotationYaw = rider.rotationYaw;
                this.prevRotationYaw = this.rotationYaw;
                this.rotationPitch = rider.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.renderYawOffset = this.rotationYaw;
                this.rotationYawHead = this.rotationYaw;
                this.stepHeight = 2.0F;
                this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;
                if (this.boosting && this.boostTime++ > this.totalBoostTime) {
                    this.boosting = false;
                }

                if (this.canPassengerSteer()) {
                    float speed = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
                    if (this.boosting) {
                        speed += speed * 6.15F * MathHelper.sin((float)this.boostTime / (float)this.totalBoostTime * 3.1415927F);
                    }

                    this.setAIMoveSpeed(speed);

                    super.travel(new Vec3d(0.0D, !world.isAirBlock(getPosition().down(3)) ? 1.0D : world.isAirBlock(getPosition().down(1)) ? -1.0D : 0.0D, 1.0D));
                    this.newPosRotationIncrements = 0;
                } else {
                    this.setMotion(Vec3d.ZERO);
                }

                this.prevLimbSwingAmount = this.limbSwingAmount;
                double lvt_3_2_ = this.getPosX() - this.prevPosX;
                double lvt_5_1_ = this.getPosZ() - this.prevPosZ;
                float lvt_7_1_ = MathHelper.sqrt(lvt_3_2_ * lvt_3_2_ + lvt_5_1_ * lvt_5_1_) * 4.0F;
                if (lvt_7_1_ > 1.0F) {
                    lvt_7_1_ = 1.0F;
                }

                this.limbSwingAmount += (lvt_7_1_ - this.limbSwingAmount) * 0.4F;
                this.limbSwing += this.limbSwingAmount;
            } else {
                this.stepHeight = 0.5F;
                this.jumpMovementFactor = 0.02F;
                super.travel(vec3d);
            }
        }
    }

    public boolean boost() {
        if (this.boosting) {
            return false;
        } else {
            this.boosting = true;
            this.boostTime = 0;
            this.totalBoostTime = this.getRNG().nextInt(841) + 140;
            this.getDataManager().set(BOOST_TIME, this.totalBoostTime);
            return true;
        }
    }
}
