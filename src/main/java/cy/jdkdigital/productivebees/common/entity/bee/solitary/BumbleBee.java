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
import net.minecraft.tags.TagKey;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BumbleBee extends SolitaryBee implements ItemSteerable, Saddleable
{
    private static final EntityDataAccessor<Boolean> SADDLED = SynchedEntityData.defineId(BumbleBee.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BOOST_TIME = SynchedEntityData.defineId(BumbleBee.class, EntityDataSerializers.INT);
    private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, BOOST_TIME, SADDLED);

    public BumbleBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        beehiveInterests = (poi) -> poi.value() == ModPointOfInterestTypes.BUMBLE_BEE_NEST.get();
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
    public TagKey<Block> getNestingTag() {
        return ModTags.BUMBLE_BEE_NESTS;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player) {
                Player player = (Player)entity;
                if (player.getMainHandItem().is(ModItems.TREAT_ON_A_STICK.get()) || player.getOffhandItem().is(ModItems.TREAT_ON_A_STICK.get())) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (BOOST_TIME.equals(key) && this.level().isClientSide) {
            this.steering.onSynced();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SADDLED, false);
        pBuilder.define(BOOST_TIME, 0);
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
    protected void dropEquipment() {
        super.dropEquipment();
        if (this.isSaddled()) {
            this.spawnAtLocation(Items.SADDLE);
        }
    }

    @Override
    public void equipSaddle(@Nullable SoundSource soundCategory) {
        this.steering.setSaddle(true);
        if (soundCategory != null) {
            level().playSound(null, this, SoundEvents.PIG_SADDLE, soundCategory, 0.5F, 1.0F);
        }
    }

    @Override
    protected void tickRidden(Player rider, Vec3 direction) {
        super.tickRidden(rider, direction);
        this.setRot(rider.getYRot(), rider.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        this.steering.tickBoost();
    }

    @Override
    public @NotNull Vec3 getRiddenInput(Player rider, Vec3 travelVec) {
        return new Vec3(0.0D, 0.0D, 1.0D);
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.steering.boostFactor();
    }

    @Override
    public boolean boost() {
        return this.steering.boost(this.getRandom());
    }

    @Override
    public float getSizeModifier() {
        return 1.25F + (hasCustomName() && getCustomName().getString().equals("Bleh") ? 1.0f : 0);
    }

    @Override
    @Nonnull
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean flag = this.isFood(player.getItemInHand(hand));
        if (!flag && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide) {
                if (player instanceof ServerPlayer) {
                    ModAdvancements.SADDLE_BEE.get().trigger((ServerPlayer) player, this);
                }

                player.startRiding(this);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void travel(Vec3 vec3) {
        super.travel(vec3);

        if (this.hasPlayerPassenger()) {
            var dy = !level().isEmptyBlock(blockPosition().below(3)) ? .1D : level().isEmptyBlock(blockPosition().below(1)) ? -0.05D : 0.0D;
            if (dy != 0f) {
                Vec3 vec = this.getDeltaMovement();
                this.setDeltaMovement(vec.x(), vec.y() + dy, vec.z());
            }
        }
    }

    private boolean hasPlayerPassenger() {
        if (this.isSaddled()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player) {
                return true;
            }
        }
        return false;
    }
}
