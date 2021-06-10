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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BumbleBeeEntity extends SolitaryBeeEntity implements IRideable, IEquipable
{
    private static final DataParameter<Boolean> SADDLED = EntityDataManager.defineId(BumbleBeeEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BOOST_TIME = EntityDataManager.defineId(BumbleBeeEntity.class, DataSerializers.INT);
    private final BoostHelper steering = new BoostHelper(this.entityData, BOOST_TIME, SADDLED);

    public BumbleBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beehiveInterests = (poiType) -> poiType == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, Ingredient.of(ModItems.TREAT_ON_A_STICK.get()), false));
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
    public void onSyncedDataUpdated(DataParameter<?> key) {
        if (BOOST_TIME.equals(key) && this.level.isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SADDLED, false);
        this.entityData.define(BOOST_TIME, 0);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public boolean canBeControlledByRider() {
        Entity entity = this.getControllingPassenger();
        if (!(entity instanceof PlayerEntity)) {
            return false;
        } else {
            PlayerEntity playerentity = (PlayerEntity)entity;
            return playerentity.getMainHandItem().getItem() == ModItems.TREAT_ON_A_STICK.get() || playerentity.getOffhandItem().getItem() == ModItems.TREAT_ON_A_STICK.get();
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        this.steering.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.steering.addAdditionalSaveData(compound);
    }

    @Override
    public boolean isSaddled() {
        return this.steering.hasSaddle();
    }

    @Override
    public boolean isSaddleable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void equipSaddle(@Nullable SoundCategory soundCategory) {
        this.steering.setSaddle(true);
        if (soundCategory != null) {
            level.playSound(null, this, SoundEvents.PIG_SADDLE, soundCategory, 0.5F, 1.0F);
        }
    }

    @Override
    public float getSizeModifier() {
        return 1.25F;
    }

    @Override
    public void travel(Vector3d travelVector) {
        this.travel(this, this.steering, travelVector);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    public void travelWithInput(Vector3d travelVec) {
        super.travel(travelVec);
    }

    @Override
    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    @Nonnull
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        boolean flag = this.isFood(player.getItemInHand(hand));
        if (!flag && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                if (player instanceof ServerPlayerEntity) {
                    ModAdvancements.SADDLE_BEE.trigger((ServerPlayerEntity) player, this);
                }

                player.startRiding(this);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean travel(MobEntity entity, BoostHelper boostHelper, Vector3d vec3d) {
        if (!entity.isAlive()) {
            return false;
        } else {
            Entity rider = entity.getPassengers().isEmpty() ? null : entity.getPassengers().get(0);
            if (entity.isVehicle() && entity.canBeControlledByRider() && rider instanceof PlayerEntity) {
                entity.yRotO = rider.yRot;
                entity.yRot = rider.yRot % 360.0F;
                entity.xRot = (rider.xRot * 0.5F) % 360.0F;
                entity.yBodyRot = entity.yRot;
                entity.yHeadRot = entity.yRot;
                entity.maxUpStep = 1.0F;
                entity.flyingSpeed = entity.getSpeed() * 0.1F;
                if (boostHelper.boosting && boostHelper.boostTime++ > boostHelper.boostTimeTotal) {
                    boostHelper.boosting = false;
                }

                if (entity.isControlledByLocalInstance()) {
                    float speed = this.getSteeringSpeed();
                    if (boostHelper.boosting) {
                        speed += speed * 2.15F * MathHelper.sin((float)boostHelper.boostTime / (float)boostHelper.boostTimeTotal * 3.1415927F);
                    }

                    entity.setSpeed(speed);
                    this.travelWithInput(new Vector3d(0.0D, !level.isEmptyBlock(blockPosition().below(3)) ? 1.0D : level.isEmptyBlock(blockPosition().below(1)) ? -1.0D : 0.0D, 1.0D));
                    if (entity instanceof BumbleBeeEntity) {
                        setNewPosRotationIncrements(0);
                    }
                } else {
                    entity.calculateEntityAnimation(entity, false);
                    entity.setDeltaMovement(Vector3d.ZERO);
                }

                return true;
            } else {
                entity.maxUpStep = 0.5F;
                entity.flyingSpeed = 0.02F;
                this.travelWithInput(vec3d);
                return false;
            }
        }
    }

    public void setNewPosRotationIncrements(int value) {
        this.lerpSteps = value;
    }
}
