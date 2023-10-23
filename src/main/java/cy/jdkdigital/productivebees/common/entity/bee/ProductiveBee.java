package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.block.entity.*;
import cy.jdkdigital.productivebees.common.entity.bee.hive.RancherBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.ResinBee;
import cy.jdkdigital.productivebees.common.recipe.BeeNBTChangerRecipe;
import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
import cy.jdkdigital.productivebees.common.recipe.ItemConversionRecipe;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductiveBee extends Bee
{
    protected Map<BeeAttribute<Integer>, Object> beeAttributes = new HashMap<>();

    protected Predicate<Holder<PoiType>> beehiveInterests = (poi) -> poi.is(PoiTypeTags.BEE_HOME);

    private boolean renderStatic = false;

    protected FollowParentGoal followParentGoal;
    protected BreedGoal breedGoal;
    protected EnterHiveGoal enterHiveGoal;
    private int breedItemCount;

    public ProductiveBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        // Goal to make entity follow player, must be registered after init to use bee attributes
        this.goalSelector.addGoal(3, new ProductiveTemptGoal(this, 1.25D));

        this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.beePollinateGoal = new ProductiveBee.PollinateGoal();
        this.goalSelector.addGoal(4, this.beePollinateGoal);

        this.goToKnownFlowerGoal = new Bee.BeeGoToKnownFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.goalSelector.addGoal(7, new Bee.BeeGrowCropGoal());
    }

    protected void registerBaseGoals() {
        this.goalSelector.addGoal(0, new BeeAttackGoal(this, 1.4D, true));

        this.enterHiveGoal = new EnterHiveGoal();
        this.goalSelector.addGoal(1, this.enterHiveGoal);

        this.breedGoal = new BreedGoal(this, 1.0D, ProductiveBee.class);
        this.goalSelector.addGoal(2, this.breedGoal);

        this.followParentGoal = new FollowParentGoal(this, 1.25D);
        this.goalSelector.addGoal(5, this.followParentGoal);

        this.goalSelector.addGoal(5, new ProductiveBee.UpdateNestGoal());
        this.goToHiveGoal = new ProductiveBee.FindNestGoal();
        this.goalSelector.addGoal(5, this.goToHiveGoal);

        if (!ProductiveBeesConfig.BEES.disableWanderGoal.get()) {
            this.goalSelector.addGoal(8, new BetterBeeWanderGoal());
        }
        this.goalSelector.addGoal(9, new FloatGoal(this));

        if (!getBeeName().equals("kamikaz")) { // TODO generalize to disable for more bees based on config
            this.targetSelector.addGoal(1, (new Bee.BeeHurtByOtherGoal(this)).setAlertOthers());
        }
        this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));

        // Empty default goals
        this.beePollinateGoal = new EmptyPollinateGoal();
        this.goToKnownFlowerGoal = new EmptyFindFlowerGoal();
    }

    @Override
    public void tick() {
        super.tick();

        // "Positive" effect to nearby entities
        if (!level().isClientSide && tickCount % ProductiveBeesConfig.BEE_ATTRIBUTES.effectTicks.get() == 0) {
            BeeEffect effect = getBeeEffect();
            if (effect != null && effect.getEffects().size() > 0) {
                List<LivingEntity> entities;
                if (getBeeType().equals("productivebees:pepto_bismol")) {
                    entities = level().getEntitiesOfClass(LivingEntity.class, (new AABB(new BlockPos(ProductiveBee.this.blockPosition()))).inflate(8.0D, 6.0D, 8.0D));
                } else {
                    entities = level().getEntitiesOfClass(Player.class, (new AABB(new BlockPos(ProductiveBee.this.blockPosition()))).inflate(8.0D, 6.0D, 8.0D)).stream().map(player -> (LivingEntity) player).collect(Collectors.toList());
                }
                if (entities.size() > 0) {
                    entities.forEach(entity -> {
                        for (Map.Entry<MobEffect, Integer> entry : effect.getEffects().entrySet()) {
                            MobEffect potionEffect = entry.getKey();
                            Integer duration = entry.getValue();
                            entity.addEffect(new MobEffectInstance(potionEffect, duration));
                        }
                    });
                }
            }
        }

        // Attribute improvement while leashed
        if (!level().isClientSide && isLeashed() && tickCount % ProductiveBeesConfig.BEE_ATTRIBUTES.leashedTicks.get() == 0) {
            // Rain tolerance improvements
            int tolerance = getAttributeValue(BeeAttributes.WEATHER_TOLERANCE);
            if (tolerance < 2 && level().random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.toleranceChance.get()) {
                if (tolerance < 1 && (level().isRaining() || level().isThundering())) {
                    beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
                } else if (tolerance == 1 && level().isThundering()) {
                    beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 2);
                }
            }
            // Behavior improvement
            int behavior = getAttributeValue(BeeAttributes.BEHAVIOR);
            if (behavior < 2 && level().random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.behaviorChance.get()) {
                // If diurnal, it can change to nocturnal
                if (behavior < 1 && level().isNight()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, level().random.nextFloat() < 0.85F ? 1 : 2);
                }
                // If nocturnal, it can become metaturnal or back to diurnal
                else if (behavior == 1 && !level().isNight()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, level().random.nextFloat() < 0.9F ? 2 : 0);
                }
            }

            // It might die when leashed outside
            boolean isInDangerFromRain = tolerance < 1 && level().isRaining();
            boolean isInDayCycleDanger = (behavior < 1 && level().isNight()) || (behavior == 1 && level().isDay());
            if ((isInDangerFromRain || isInDayCycleDanger) && level().random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.damageChance.get()) {
                hurt(isInDangerFromRain ? this.level().damageSources().drown() : this.level().damageSources().generic(), (getMaxHealth() / 3) - 1);
            }
        }

        // Kill below world border
        this.checkBelowWorld();
    }

    @Override
    public void setTarget(@Nullable LivingEntity livingEntity) {
        boolean isWearingBeeHelmet = false;

        if (livingEntity != null) {
            ItemStack itemstack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty() && itemstack.getItem().equals(ModItems.BEE_NEST_DIAMOND_HELMET.get())) {
                isWearingBeeHelmet = true;
            }
        }

        if (!isWearingBeeHelmet) {
            super.setTarget(livingEntity);
        }
    }

    @Nonnull
    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return super.getDimensions(poseIn).scale(getSizeModifier());
    }

    public float getSizeModifier() {
        return 1.0f;
    }

    @Override
    public boolean isAngry() {
        return super.isAngry() && getAttributeValue(BeeAttributes.TEMPER) > 0;
    }

    @Override
    public void setHasNectar(boolean hasNectar) {
        // Only allow removing nectar state or setting on an allowed list of bees.
        // Use internal method to prevent other mods from setting nectar state
        if (!hasNectar || this.getType().is(ModTags.EXTERNAL_CAN_POLLINATE)) {
            internalSetHasNectar(false);
        }
    }

    public void internalSetHasNectar(boolean hasNectar) {
        super.setHasNectar(hasNectar);
    }

    @Override
    public boolean isFlowerValid(@Nullable BlockPos pos) {
        return isFlowerValid(pos, ProductiveBee.this::isFlowerBlock, ProductiveBee.this::isFlowerItem);
    }

    public boolean isFlowerValid(@Nullable BlockPos pos, Predicate<BlockState> validator, Predicate<ItemStack> itemValidator) {
        if (pos == null || !level().isLoaded(pos)) {
            return false;
        }

        BlockState flowerBlock = level().getBlockState(pos);

        return validator.test(flowerBlock) || (flowerBlock.getBlock() instanceof Feeder && (isValidFeeder(this, level().getBlockEntity(pos), validator, itemValidator)));
    }

    public List<ItemStack> getBreedingItems() {
        int count = getBreedingItemCount();
        List<ItemStack> list = Arrays.stream(getBreedingIngredient().getItems()).toList();
        list.forEach(e -> e.setCount(count));
        return list;
    }

    public Ingredient getBreedingIngredient() {
        return Ingredient.of(ItemTags.FLOWERS);
    }

    public Integer getBreedingItemCount() {
        return 1;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return getBreedingIngredient().test(stack);
    }

    public Predicate<Holder<PoiType>> getBeehiveInterests() {
        return beehiveInterests;
    }

    public boolean doesHiveAcceptBee(BlockPos pos) {
        BlockEntity blockEntity = level().getBlockEntity(pos);
        if (blockEntity instanceof AdvancedBeehiveBlockEntityAbstract) {
            return ((AdvancedBeehiveBlockEntityAbstract) blockEntity).acceptsBee(this);
        }
        return true;
    }

    public static boolean isValidFeeder(Bee bee, BlockEntity tile, Predicate<BlockState> validator, Predicate<ItemStack> itemValidator) {
        AtomicBoolean hasValidBlock = new AtomicBoolean(false);
        if (tile instanceof FeederBlockEntity feederBlockEntity) {
            for (ItemStack stack: feederBlockEntity.getInventoryItems()) {
                Item slotItem = stack.getItem();
                if (slotItem instanceof BlockItem && validator.test(((BlockItem) slotItem).getBlock().defaultBlockState())) {
                    hasValidBlock.set(true);
                } else if (itemValidator != null && itemValidator.test(stack)) {
                    hasValidBlock.set(true);
                } else if (BeeHelper.hasItemConversionRecipe(bee, stack)) {
                    hasValidBlock.set(true);
                } else if (BeeHelper.hasNBTChangerRecipe(bee, stack)) {
                    hasValidBlock.set(true);
                }
            }
            if (!hasValidBlock.get() && feederBlockEntity.getBlockState().getValue(BlockStateProperties.WATERLOGGED) && !feederBlockEntity.getBlockState().getValue(Feeder.HONEYLOGGED)) {
                hasValidBlock.set(validator.test(Blocks.WATER.defaultBlockState()));
            }
        }
        return hasValidBlock.get();
    }

    @Override
    protected void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
        super.usePlayerItem(player, hand, stack);

        this.level().broadcastEntityEvent(this, (byte)13);
        this.breedItemCount++;
    }

    @Override
    public void setInLove(@Nullable Player player) {
        if (this.breedItemCount >= getBreedingItemCount()) {
            super.setInLove(player);
            this.breedItemCount = 0;
        }
    }

    @Override
    public boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean shouldReturnToHive = this.isTiredOfLookingForNectar() || this.hasNectar();

            if (!shouldReturnToHive && !level().dimensionType().hasFixedTime()) { // in overworld, return to hive if raining or when night
                shouldReturnToHive =
                    (level().isNight() && !canOperateDuringNight()) ||
                    (level().isRaining() && !canOperateDuringRain()) ||
                    (level().isThundering() && !canOperateDuringThunder());
            }

            return shouldReturnToHive && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    @Override
    public void setHasStung(boolean hasStung) {
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 2) {
            // 50% chance to not lose stinger
            hasStung = level().random.nextBoolean();
        }
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 3) {
            // 80% chance to not lose stinger
            hasStung = level().random.nextFloat() < .2;
        }
        super.setHasStung(hasStung);

        if (hasStung && getBeeName().equals("kamikaz")) {
            this.hurt(this.level().damageSources().generic(), this.getHealth());
        }
    }

    public String getBeeType() {
        return getEncodeId();
    }

    public String getBeeName() {
        return getBeeName(getBeeType());
    }

    public static String getBeeName(String beeType) {
        String[] types = beeType.split("[:]");
        String type = types[0];
        if (types.length > 1) {
            type = types[1];
        }
        return type.replace("_bee", "");
    }

    public String getRenderer() {
        return "default";
    }

    public Integer getAttributeValue(BeeAttribute<Integer> parameter) {
        if (this.beeAttributes.get(parameter) != null) {
            return (Integer) this.beeAttributes.get(parameter);
        }
        return 0;
    }

    public void setAttributeValue(BeeAttribute<Integer> parameter, Integer value) {
        // Give health boost based on endurance
        if (parameter.equals(BeeAttributes.ENDURANCE)) {
            AttributeInstance healthMod = this.getAttribute(Attributes.MAX_HEALTH);
            if (healthMod != null && value != 1) {
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_WEAK);
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_MEDIUM);
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_STRONG);
                healthMod.addPermanentModifier(BeeAttributes.HEALTH_MODS.get(value));
            }
        }

        this.beeAttributes.put(parameter, value);
    }

    public Map<BeeAttribute<Integer>, Object> getBeeAttributes() {
        return beeAttributes;
    }

    public boolean hasBeeAttributes() {
        return beeAttributes.containsKey(BeeAttributes.PRODUCTIVITY);
    }

    public void setDefaultAttributes() {
        if (!hasBeeAttributes()) {
            Random rand = new Random();
            setAttributeValue(BeeAttributes.PRODUCTIVITY, rand.nextInt(3));
            setAttributeValue(BeeAttributes.TEMPER, 1);
            setAttributeValue(BeeAttributes.ENDURANCE, rand.nextInt(3));
            setAttributeValue(BeeAttributes.BEHAVIOR, 0);
            setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, 0);
            setHealth(getMaxHealth());
        }
    }

    public boolean canOperateDuringNight() {
        return getAttributeValue(BeeAttributes.BEHAVIOR) > 0;
    }

    public boolean canOperateDuringRain() {
        return getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) > 0;
    }

    public boolean canOperateDuringThunder() {
        return getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) == 2;
    }

    public int getTimeInHive(boolean hasNectar) {
        return hasNectar ? ProductiveBeesConfig.GENERAL.timeInHive.get() : ProductiveBeesConfig.GENERAL.timeInHive.get() / 2;
    }

    public void setRenderStatic() {
        renderStatic = true;
    }

    public boolean getRenderStatic() {
        return renderStatic;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(this.level().damageSources().inWall()) || source.equals(this.level().damageSources().sweetBerryBush()) || (source.equals(this.level().damageSources().wither()) && getBeeType().contains("dye_bee")) || super.isInvulnerableTo(source);
    }

    @Nonnull
    @Override
    protected PathNavigation createNavigation(@Nonnull Level worldIn) {
        PathNavigation navigator = super.createNavigation(worldIn);

        if (navigator instanceof FlyingPathNavigation) {
            navigator.setCanFloat(false);
            ((FlyingPathNavigation) navigator).setCanPassDoors(false);
        }
        return navigator;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("bee_productivity", this.getAttributeValue(BeeAttributes.PRODUCTIVITY));
        tag.putInt("bee_endurance", this.getAttributeValue(BeeAttributes.ENDURANCE));
        tag.putInt("bee_temper", this.getAttributeValue(BeeAttributes.TEMPER));
        tag.putInt("bee_behavior", this.getAttributeValue(BeeAttributes.BEHAVIOR));
        tag.putInt("bee_weather_tolerance", this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE));

        tag.putString("bee_type", this instanceof SolitaryBee ? "solitary" : "hive");
        tag.putFloat("MaxHealth", getMaxHealth());
        tag.putBoolean("HasConverted", hasConverted());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.internalSetHasNectar(tag.getBoolean("HasNectar"));

        if (tag.contains("bee_productivity")) {
            beeAttributes.clear();
            setAttributeValue(BeeAttributes.PRODUCTIVITY, tag.getInt("bee_productivity"));
            setAttributeValue(BeeAttributes.ENDURANCE, tag.contains("bee_endurance") ? tag.getInt("bee_endurance") : 1);
            setAttributeValue(BeeAttributes.TEMPER, tag.getInt("bee_temper"));
            setAttributeValue(BeeAttributes.BEHAVIOR, tag.getInt("bee_behavior"));
            setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, tag.getInt("bee_weather_tolerance"));
        } else {
            setDefaultAttributes();
        }
        setHasConverted(tag.contains("HasConverted") && tag.getBoolean("HasConverted"));
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return BeeCreator.getSpawnEgg(this.getBeeType());
    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();

        if (!this.isBaby()) {
            BlockPos pos = blockPosition();
            if (level().isEmptyBlock(pos)) {
                this.setPos(pos.getX(), pos.getY(), pos.getZ());
            } else if (level().isEmptyBlock(pos.below())) {
                pos = pos.below();
                this.setPos(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Nullable
    @Override
    public Bee getBreedOffspring(@Nonnull ServerLevel world, AgeableMob targetEntity) {
        Entity newBee = BeeHelper.getBreedingResult(this, targetEntity, world);

        if (!(newBee instanceof Bee)) {
            return null;
        }

        if (newBee instanceof ProductiveBee) {
            BeeHelper.setOffspringAttributes((ProductiveBee) newBee, this, targetEntity);
        }

        return (Bee) newBee;
    }

    @Override
    public boolean canMate(@Nonnull Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!(otherAnimal instanceof Bee)) {
            return false;
        } else {
            return (
                    this.isInLove() &&
                            otherAnimal.isInLove()
            ) &&
                    (
                            (level() instanceof ServerLevel serverLevel && BeeHelper.getRandomBreedingRecipe(this, otherAnimal, serverLevel) != null) || // check if there's an offspring recipe
                                    canSelfBreed() || // allows self breeding
                                    !(otherAnimal instanceof ProductiveBee) // or not a productive bee
                    );
        }
    }

    public boolean canSelfBreed() {
        return true;
    }

    public void postPollinate() {
        if (hasNectar() && savedFlowerPos != null) {
            BlockState flowerBlockState = level().getBlockState(savedFlowerPos);
            if (BeeHelper.hasBlockConversionRecipe(this, flowerBlockState)) {
                BlockConversionRecipe recipe = BeeHelper.getBlockConversionRecipe(this, flowerBlockState);
                if (recipe != null && level().random.nextInt(100) <= recipe.chance) {
                    level().setBlock(savedFlowerPos, recipe.stateTo, 3);
                    level().levelEvent(2005, savedFlowerPos, 0);
                }
                // Set flag to prevent produce when trying to convert blocks
                setHasConverted(!recipe.pollinates);
            } else {
                BlockEntity blockEntity = level().getBlockEntity(savedFlowerPos);
                if (blockEntity instanceof FeederBlockEntity feederBlockEntity) {
                    BlockEntity hiveBlockEntity = hivePos != null ? level().getBlockEntity(hivePos) : null;
                    for (ItemStack stack : feederBlockEntity.getInventoryItems()) {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            BlockConversionRecipe blockRecipe = BeeHelper.getBlockConversionRecipe(this, blockItem.getBlock().defaultBlockState());
                            if (blockRecipe != null && hiveBlockEntity instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                                if (level().random.nextInt(100) <= blockRecipe.chance) {
                                    ItemStack output = new ItemStack(blockRecipe.stateTo.getBlock().asItem());
                                    if (beehiveBlockEntity.isSim()) {
                                        beehiveBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
                                            if (!output.equals(ItemStack.EMPTY) &&
                                                    h instanceof InventoryHandlerHelper.ItemHandler itemHandler
                                                    && itemHandler.addOutput(output).getCount() == 0) {
                                                stack.shrink(1);
                                            }
                                        });
                                    } else {
                                        Block.popResource(level(), feederBlockEntity.getBlockPos().relative(Direction.UP), output);
                                        stack.shrink(1);
                                    }
                                }
                                setHasConverted(!blockRecipe.pollinates);
                                return;
                            }
                        }
                        ItemConversionRecipe itemRecipe = BeeHelper.getItemConversionRecipe(this, stack);
                        if (itemRecipe != null && hiveBlockEntity instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                            if (level().random.nextInt(100) <= itemRecipe.chance) {
                                if (beehiveBlockEntity.isSim()) {
                                    beehiveBlockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> {
                                        if (h instanceof InventoryHandlerHelper.ItemHandler itemHandler
                                                && itemHandler.addOutput(itemRecipe.output.copy()).getCount() == 0) {
                                            stack.shrink(1);
                                        }
                                    });
                                } else {
                                    Block.popResource(level(), feederBlockEntity.getBlockPos().relative(Direction.UP), itemRecipe.output.copy());
                                    stack.shrink(1);
                                }
                            }
                            setHasConverted(!itemRecipe.pollinates);
                            return;
                        }
                        BeeNBTChangerRecipe nbtRecipe = BeeHelper.getNBTChangerRecipe(this, stack);
                        CompoundTag tag = stack.getTag();
                        if (nbtRecipe != null && tag != null) {
                            // TODO support long
                            switch (nbtRecipe.method) {
                                case "decrement" -> tag.putInt(nbtRecipe.attribute, Math.max(nbtRecipe.min, tag.getInt(nbtRecipe.attribute) - nbtRecipe.value));
                                case "increment" -> tag.putInt(nbtRecipe.attribute, Math.min(nbtRecipe.max, tag.getInt(nbtRecipe.attribute) + nbtRecipe.value));
                                case "set" -> tag.putInt(nbtRecipe.attribute, nbtRecipe.value);
                                case "unset" -> tag.remove(nbtRecipe.attribute);
                            }
                            stack.setTag(tag);
                            // Set flag to prevent produce
                            setHasConverted(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void insertConversionResult() {

    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.25F : sizeIn.height * 0.5F;
    }

    public float[] getColor(int tintIndex, float tickCount) {
        return ColorUtil.getCacheColor(-1);
    }

    public boolean isColored() {
        return false;
    }

    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(BlockTags.FLOWERS) || BeeHelper.hasBlockConversionRecipe(this, flowerBlock);
    }

    public boolean isFlowerItem(ItemStack flowerItem) {
        if (flowerItem.getItem() instanceof BlockItem blockItem && isFlowerBlock(blockItem.getBlock().defaultBlockState())) {
            return true;
        }
        return BeeHelper.hasItemConversionRecipe(this, flowerItem);
    }

    public TagKey<Block> getNestingTag() {
        return BlockTags.BEEHIVES;
    }

    public BeeEffect getBeeEffect() {
        return null;
    }

    public boolean hasConverted() {
        return getFlag(16);
    }

    public void setHasConverted(boolean hasConverted) {
        setFlag(16, hasConverted);
    }

    public class EnterHiveGoal extends Bee.BeeEnterHiveGoal
    {
        public EnterHiveGoal() {
            super();
        }

        public boolean canBeeUse() {
            if (ProductiveBee.this.hivePos != null && ProductiveBee.this.wantsToEnterHive() && ProductiveBee.this.hivePos.closerToCenterThan(ProductiveBee.this.position(), 2.0D)) {
                BlockEntity blockEntity = ProductiveBee.this.level().getBlockEntity(ProductiveBee.this.hivePos);
                if (blockEntity instanceof BeehiveBlockEntity beehiveblockentity) {
                    if (!beehiveblockentity.isFull()) {
                        return true;
                    }

                    ProductiveBee.this.hivePos = null;
                }
            }
            return false;
        }
    }

    public class PollinateGoal extends Bee.BeePollinateGoal
    {
        public Predicate<BlockPos> flowerPredicate = (blockPos) -> {
            BlockState blockState = ProductiveBee.this.level().getBlockState(blockPos);
            boolean isInterested = false;
            try {
                if (blockState.getBlock() instanceof Feeder) {
                    isInterested = isValidFeeder(ProductiveBee.this, level().getBlockEntity(blockPos), ProductiveBee.this::isFlowerBlock, ProductiveBee.this::isFlowerItem);
                } else {
                    isInterested = ProductiveBee.this.isFlowerBlock(blockState);
                    if (isInterested && blockState.is(BlockTags.TALL_FLOWERS)) {
                        if (blockState.getBlock() == Blocks.SUNFLOWER) {
                            isInterested = blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
                        }
                    }
                }
            } catch (Exception e) {
                // early tag access
            }

            return isInterested;
        };

        public PollinateGoal() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (ProductiveBee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            } else if (ProductiveBee.this.hasNectar()) {
                return false;
            } else if (ProductiveBee.this.level().isRaining() && !ProductiveBee.this.canOperateDuringRain()) {
                return false;
            } else if (ProductiveBee.this.level().isThundering() && !ProductiveBee.this.canOperateDuringThunder()) {
                return false;
            } else {
                Optional<BlockPos> optional = this.findNearbyFlower();
                if (optional.isPresent()) {
                    ProductiveBee.this.savedFlowerPos = optional.get();
                    ProductiveBee.this.navigation.moveTo((double) ProductiveBee.this.savedFlowerPos.getX() + 0.5D, (double) ProductiveBee.this.savedFlowerPos.getY() + 0.5D, (double) ProductiveBee.this.savedFlowerPos.getZ() + 0.5D, 1.2F);
                    return true;
                }
                // Failing to find a target will set a cooldown before next attempt
                ProductiveBee.this.remainingCooldownBeforeLocatingNewFlower = 70 + level().random.nextInt(50);
                return false;
            }
        }

        @Override
        public boolean canBeeContinueToUse() {
            if (!this.isPollinating()) {
                return false;
            } else if (!ProductiveBee.this.hasSavedFlowerPos()) {
                return false;
            } else if (ProductiveBee.this.level().isRaining() && !ProductiveBee.this.canOperateDuringRain()) {
                return false;
            } else if (ProductiveBee.this.level().isThundering() && !ProductiveBee.this.canOperateDuringThunder()) {
                return false;
            } else if (this.hasPollinatedLongEnough()) {
                return ProductiveBee.this.random.nextFloat() < 0.2F;
            } else if (ProductiveBee.this.tickCount % 20 == 0 && !ProductiveBee.this.isFlowerValid(ProductiveBee.this.savedFlowerPos)) {
                ProductiveBee.this.savedFlowerPos = null;
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void stop() {
            super.stop();
            if (this.hasPollinatedLongEnough()) {
                ProductiveBee.this.internalSetHasNectar(true);
            }
            ProductiveBee.this.postPollinate();
        }

        @Nonnull
        @Override
        public Optional<BlockPos> findNearbyFlower() {
            if (ProductiveBee.this instanceof RancherBee) {
                return findEntities(RancherBee.predicate, 5D);
            }
            if (ProductiveBee.this instanceof ResinBee) {
                return findEntities(ResinBee.predicate, 5D);
            }
            if (ProductiveBee.this instanceof ConfigurableBee && ((ConfigurableBee) ProductiveBee.this).getFlowerType().equals("entity_types")) {
                CompoundTag nbt = ((ConfigurableBee) ProductiveBee.this).getNBTData();
                if (nbt != null) {
                    if (nbt.contains("flowerTag")) {
                        var flowerTag = ModTags.getEntityTag(new ResourceLocation(nbt.getString("flowerTag")));
                        var amberBlocks = this.findNearestBlock(pos -> {
                            if (ProductiveBee.this.level().getBlockEntity(pos) instanceof AmberBlockEntity amberBlockEntity) {
                                return amberBlockEntity.getCachedEntity().getType().is(flowerTag);
                            }
                            return false;
                        }, 5);
                        if (amberBlocks.isPresent()) {
                            return amberBlocks;
                        }
                        return findEntities(entity -> entity instanceof Mob && nbt.getBoolean("inverseFlower") != entity.getType().is(flowerTag), 5D);
                    }
                }
            }
            return this.findNearestBlock(this.flowerPredicate, 5);
        }

        private Optional<BlockPos> findNearestBlock(Predicate<BlockPos> predicate, double distance) {
            BlockPos blockpos = ProductiveBee.this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int i = 0; (double) i <= distance; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < distance; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
                            if (blockpos.closerThan(blockpos$mutableblockpos, distance) && predicate.test(blockpos$mutableblockpos)) {
                                return Optional.of(blockpos$mutableblockpos);
                            }
                        }
                    }
                }
            }

            return Optional.empty();
        }

        private Optional<BlockPos> findEntities(Predicate<Entity> predicate, double distance) {
            BlockPos blockpos = ProductiveBee.this.blockPosition();
            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

            List<Entity> entities = level().getEntities(ProductiveBee.this, (new AABB(blockpos).inflate(distance, distance, distance)), predicate);
            if (entities.size() > 0) {
                Entity target = entities.get(0);
                if (target instanceof PathfinderMob pathfinderMob) {
                    pathfinderMob.getNavigation().setSpeedModifier(0);
                }
                blockpos$mutable.set(target.getX(), target.getY(), target.getZ());
                return Optional.of(blockpos$mutable);
            }

            return Optional.empty();
        }
    }

    public class FindNestGoal extends Bee.BeeGoToHiveGoal
    {
        public FindNestGoal() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (!ProductiveBee.this.hasHive()) {
                return false;
            }

            HolderSet.Named<Block> nestTag = BuiltInRegistries.BLOCK.getOrCreateTag(ProductiveBee.this.getNestingTag());
            try {
                if (nestTag.size() == 0) {
                    return false;
                }
            } catch (Exception e) {
                String bee = ProductiveBee.this.getEncodeId();
                if (ProductiveBee.this instanceof ConfigurableBee) {
                    bee = ProductiveBee.this.getBeeType();
                }
                ProductiveBees.LOGGER.debug("Nesting tag for " + bee + " not found. Looking for " + nestTag);
            }

            return !ProductiveBee.this.hasRestriction() &&
                    ProductiveBee.this.wantsToEnterHive() &&
                    !this.isCloseEnough(ProductiveBee.this.hivePos) &&
                    ProductiveBee.this.level().getBlockState(ProductiveBee.this.hivePos).is(nestTag);
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (ProductiveBee.this.closerThan(pos, 2)) {
                return true;
            } else {
                Path path = ProductiveBee.this.navigation.getPath();
                return path != null && path.getTarget().equals(pos) && path.canReach() && path.isDone();
            }
        }

        @Override
        protected void blacklistTarget(BlockPos pos) {
            BlockEntity tileEntity = ProductiveBee.this.level().getBlockEntity(pos);
            TagKey<Block> nestTag = ProductiveBee.this.getNestingTag();
            if (tileEntity != null && tileEntity.getBlockState().is(nestTag)) {
                this.blacklistedTargets.add(pos);

                while (this.blacklistedTargets.size() > 3) {
                    this.blacklistedTargets.remove(0);
                }
            }
        }
    }

    public class UpdateNestGoal extends Bee.BeeLocateHiveGoal
    {
        public UpdateNestGoal() {
            super();
        }

        @Override
        public void start() {
            ProductiveBee.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPos> nearbyNests = this.getNearbyFreeNests();
            if (!nearbyNests.isEmpty()) {
                Iterator<BlockPos> iterator = nearbyNests.iterator();
                BlockPos blockPos;
                do {
                    if (!iterator.hasNext()) {
                        ProductiveBee.this.goToHiveGoal.clearBlacklist();
                        ProductiveBee.this.hivePos = nearbyNests.get(0);
                        return;
                    }

                    blockPos = iterator.next();
                } while (ProductiveBee.this.goToHiveGoal.isTargetBlacklisted(blockPos));

                ProductiveBee.this.hivePos = blockPos;
            }
        }

        private List<BlockPos> getNearbyFreeNests() {
            BlockPos pos = ProductiveBee.this.blockPosition();

            PoiManager poiManager = ((ServerLevel) ProductiveBee.this.level()).getPoiManager();

            Stream<PoiRecord> stream = poiManager.getInRange(ProductiveBee.this.beehiveInterests, pos, 30, PoiManager.Occupancy.ANY);

            return stream
                    .map(PoiRecord::getPos)
                    .filter(ProductiveBee.this::doesHiveHaveSpace)
                    .filter(ProductiveBee.this::doesHiveAcceptBee)
                    .sorted(Comparator.comparingDouble((vec) -> vec.distSqr(pos)))
                    .collect(Collectors.toList());
        }
    }

    public class BeeAttackGoal extends MeleeAttackGoal
    {
        BeeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }

        public boolean canUse() {
            return super.canUse() && ProductiveBee.this.isAngry() && !ProductiveBee.this.hasStung();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && ProductiveBee.this.isAngry() && !ProductiveBee.this.hasStung();
        }
    }

    public class ProductiveTemptGoal extends TemptGoal
    {
        public ProductiveTemptGoal(PathfinderMob entity, double speed) {
            super(entity, speed, Ingredient.EMPTY, false);
            List<ItemStack> listOfStuff = Arrays.asList(Ingredient.of(ItemTags.FLOWERS).getItems());
//            listOfStuff.addAll(Arrays.asList(ProductiveBee.this.getBreedingItem().getItems())); TODO
            items = Ingredient.of(listOfStuff.stream());
        }
    }

    public class BetterBeeWanderGoal extends Bee.BeeWanderGoal {
        public BetterBeeWanderGoal() {
            super();
        }

        public boolean canUse() {
            // Trigger if the bee gets too far from its hive, it will engage it to return
            return super.canUse() || (ProductiveBee.this.hivePos != null && !ProductiveBee.this.closerThan(ProductiveBee.this.hivePos, 22));
        }
    }

    public class EmptyPollinateGoal extends PollinateGoal
    {
        @Override
        public boolean canBeeUse() {
            return false;
        }
    }

    public class EmptyFindFlowerGoal extends BeeGoToKnownFlowerGoal
    {
        @Override
        public boolean canBeeUse() {
            return false;
        }
    }
}
