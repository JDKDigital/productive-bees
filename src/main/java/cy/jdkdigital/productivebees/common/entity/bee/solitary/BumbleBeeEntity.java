package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ITag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BumbleBeeEntity extends SolitaryBeeEntity implements IRideable, IEquipable
{
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.createKey(BumbleBeeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.createKey(BumbleBeeEntity.class, DataSerializers.VARINT);
    private final BoostHelper boostHelper = new BoostHelper(this.dataManager, BOOST_TIME, SADDLED);

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
    public ITag<Block> getNestingTag() {
        return ModTags.BUMBLE_BEE_NESTS;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (BOOST_TIME.equals(key) && this.world.isRemote) {
            this.boostHelper.updateData();
        }
        super.notifyDataManagerChange(key);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(SADDLED, false);
        this.dataManager.register(BOOST_TIME, 0);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.isHorseSaddled()) {
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
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.boostHelper.setSaddledToNBT(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.boostHelper.setSaddledFromNBT(compound);
    }

    @Override
    public boolean isHorseSaddled() {
        return this.boostHelper.getSaddled();
    }

    @Override
    public boolean func_230264_L__() {
        return this.isAlive() && !this.isChild();
    }

    @Override
    public void func_230266_a_(@Nullable SoundCategory soundCategory) {
        this.boostHelper.setSaddledFromBoolean(true);
        if (soundCategory != null) {
            this.world.playMovingSound(null, this, SoundEvents.ENTITY_PIG_SADDLE, soundCategory, 0.5F, 1.0F);
        }
    }

    @Override
    public float getSizeModifier() {
        return 1.25F;
    }

    @Override
    public void travel(Vector3d travelVector) {
        this.ride(this, this.boostHelper, travelVector);
    }

    @Override
    public boolean boost() {
        return this.boostHelper.boost(this.getRNG());
    }

    @Override
    public void travelTowards(Vector3d travelVec) {
        super.travel(travelVec);
    }

    @Override
    public float getMountedSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        boolean flag = this.isBreedingItem(player.getHeldItem(hand));
        if (!flag && this.isHorseSaddled() && !this.isBeingRidden() && !player.isSecondaryUseActive()) {
            if (!this.world.isRemote) {
                if (player instanceof ServerPlayerEntity) {
                    ModAdvancements.SADDLE_BEE.trigger((ServerPlayerEntity) player, this);
                }

                player.startRiding(this);
            }

            return ActionResultType.func_233537_a_(this.world.isRemote);
        }
        return super.func_230254_b_(player, hand);
    }

    @Override
    public boolean ride(MobEntity entity, BoostHelper boostHelper, Vector3d vec3d) {
        if (!entity.isAlive()) {
            return false;
        } else {
            Entity rider = entity.getPassengers().isEmpty() ? null : entity.getPassengers().get(0);
            if (entity.isBeingRidden() && entity.canBeSteered() && rider instanceof PlayerEntity) {
                entity.prevRotationYaw = rider.rotationYaw;
                entity.rotationYaw = rider.rotationYaw % 360.0F;
                entity.rotationPitch = (rider.rotationPitch * 0.5F) % 360.0F;
                entity.renderYawOffset = entity.rotationYaw;
                entity.rotationYawHead = entity.rotationYaw;
                entity.stepHeight = 1.0F;
                entity.jumpMovementFactor = entity.getAIMoveSpeed() * 0.1F;
                if (boostHelper.saddledRaw && boostHelper.field_233611_b_++ > boostHelper.boostTimeRaw) {
                    boostHelper.saddledRaw = false;
                }

                if (entity.canPassengerSteer()) {
                    float speed = this.getMountedSpeed();
                    if (boostHelper.saddledRaw) {
                        speed += speed * 2.15F * MathHelper.sin((float)boostHelper.field_233611_b_ / (float)boostHelper.boostTimeRaw * 3.1415927F);
                    }

                    entity.setAIMoveSpeed(speed);
                    this.travelTowards(new Vector3d(0.0D, !world.isAirBlock(getPosition().down(3)) ? 1.0D : world.isAirBlock(getPosition().down(1)) ? -1.0D : 0.0D, 1.0D));
                    if (entity instanceof BumbleBeeEntity) {
                        setNewPosRotationIncrements(0);
                    }
                } else {
                    entity.func_233629_a_(entity, false);
                    entity.setMotion(Vector3d.ZERO);
                }

                return true;
            } else {
                entity.stepHeight = 0.5F;
                entity.jumpMovementFactor = 0.02F;
                this.travelTowards(vec3d);
                return false;
            }
        }
    }

    @Override
    public void updatePassenger(Entity passenger) {
        double d0 = this.getPosY() + this.getMountedYOffset() + passenger.getYOffset();
        float xDirection = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
        float zDirection = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
        passenger.setPosition(this.getPosX() + (double)(0.2F * xDirection), d0, this.getPosZ() - (double)(0.2F * zDirection));
    }

    public void setNewPosRotationIncrements(int value) {
        this.newPosRotationIncrements = value;
    }
}
