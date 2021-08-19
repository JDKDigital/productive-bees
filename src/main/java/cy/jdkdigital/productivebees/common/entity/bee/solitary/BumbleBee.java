package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BumbleBee extends SolitaryBee implements ItemSteerable, Saddleable
{
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(BumbleBee.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(BumbleBee.class, EntityDataSerializers.INT);
    private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, BOOST_TIME, SADDLED);

    public BumbleBee(EntityType<? extends Bee> entityType, Level world) {
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
    public Tag<Block> getNestingTag() {
        return ModTags.BUMBLE_BEE_NESTS;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
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
        if (!(entity instanceof Player playerEntity)) {
            return false;
        } else {
            return playerEntity.getMainHandItem().getItem() == ModItems.TREAT_ON_A_STICK.get() || playerEntity.getOffhandItem().getItem() == ModItems.TREAT_ON_A_STICK.get();
        }
    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.steering.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.steering.readAdditionalSaveData(compound);
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
    public void equipSaddle(@Nullable SoundSource soundCategory) {
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
    public void travel(Vec3 travelVector) {
        this.travel(this, this.steering, travelVector);
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    public void travelWithInput(Vec3 travelVec) {
        super.travel(travelVec);
    }

    @Override
    public float getSteeringSpeed() {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean flag = this.isFood(player.getItemInHand(hand));
        if (!flag && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide) {
                if (player instanceof ServerPlayer) {
                    ModAdvancements.SADDLE_BEE.trigger((ServerPlayer) player, this);
                }

                player.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean travel(Mob entity, ItemBasedSteering boostHelper, Vec3 vec3d) {
        if (!entity.isAlive()) {
            return false;
        } else {
            Entity rider = entity.getPassengers().isEmpty() ? null : entity.getPassengers().get(0);
            if (entity.isVehicle() && entity.canBeControlledByRider() && rider instanceof Player) {
                entity.yRotO = rider.getYRot();
                entity.setYRot(rider.getYRot() % 360.0F);
                entity.setXRot((rider.getXRot() * 0.5F) % 360.0F);
                entity.yBodyRot = entity.getYRot();
                entity.yHeadRot = entity.getYRot();
                entity.maxUpStep = 1.0F;
                entity.flyingSpeed = entity.getSpeed() * 0.1F;
                if (boostHelper.boosting && boostHelper.boostTime++ > boostHelper.boostTimeTotal) {
                    boostHelper.boosting = false;
                }

                if (entity.isControlledByLocalInstance()) {
                    float speed = this.getSteeringSpeed();
                    if (boostHelper.boosting) {
                        speed += speed * 2.15F * Mth.sin((float)boostHelper.boostTime / (float)boostHelper.boostTimeTotal * 3.1415927F);
                    }

                    entity.setSpeed(speed);
                    this.travelWithInput(new Vec3(0.0D, !level.isEmptyBlock(blockPosition().below(3)) ? 1.0D : level.isEmptyBlock(blockPosition().below(1)) ? -1.0D : 0.0D, 1.0D));
                    if (entity instanceof BumbleBee) {
                        setNewPosRotationIncrements(0);
                    }
                } else {
                    entity.calculateEntityAnimation(entity, false);
                    entity.setDeltaMovement(Vec3.ZERO);
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
