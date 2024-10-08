package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.hive.RancherBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.ResinBee;
import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
import cy.jdkdigital.productivebees.common.recipe.ItemConversionRecipe;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.*;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductiveBee extends Bee implements IProductiveBee
{
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

        this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
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
            if (effect != null && !effect.getEffects().isEmpty()) {
                List<LivingEntity> entities;
                if (getBeeType().equals(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "pepto_bismol"))) {
                    entities = level().getEntitiesOfClass(LivingEntity.class, (new AABB(new BlockPos(ProductiveBee.this.blockPosition()))).inflate(8.0D, 6.0D, 8.0D));
                } else {
                    entities = level().getEntitiesOfClass(Player.class, (new AABB(new BlockPos(ProductiveBee.this.blockPosition()))).inflate(8.0D, 6.0D, 8.0D)).stream().map(player -> (LivingEntity) player).collect(Collectors.toList());
                }
                if (!entities.isEmpty()) {
                    entities.forEach(entity -> {
                        for (Map.Entry<Holder<MobEffect>, Integer> entry : effect.getEffects().entrySet()) {
                            entity.addEffect(new MobEffectInstance(entry.getKey(), entry.getValue()));
                        }
                    });
                }
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
    public EntityDimensions getDefaultDimensions(Pose poseIn) {
        return super.getDefaultDimensions(poseIn).scale(getSizeModifier());
    }

    @Override
    public boolean isAngry() {
        return super.isAngry() && !getAttributeValue(GeneAttribute.TEMPER).equals(GeneValue.TEMPER_PASSIVE);
    }

    @Override
    public void setHasNectar(boolean hasNectar) {
        // Only allow removing nectar state or setting on an allowed list of bees.
        // Use internal method to prevent other mods from setting nectar state
        if (!hasNectar || this.getType().is(ModTags.EXTERNAL_CAN_POLLINATE)) {
            internalSetHasNectar(hasNectar);
        }
    }

    public void internalSetHasNectar(boolean hasNectar) {
        super.setHasNectar(hasNectar);
    }

    @Override
    public boolean isFlowerValid(BlockPos pos) {
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
        if (hasStung && getAttributeValue(GeneAttribute.ENDURANCE).equals(GeneValue.ENDURANCE_MEDIUM)) {
            // 50% chance to not lose stinger
            hasStung = level().random.nextBoolean();
        }
        if (hasStung && getAttributeValue(GeneAttribute.ENDURANCE).equals(GeneValue.ENDURANCE_STRONG)) {
            // 80% chance to not lose stinger
            hasStung = level().random.nextFloat() < .2;
        }
        super.setHasStung(hasStung);

        if (hasStung && getBeeName().equals("kamikaz")) {
            this.hurt(this.level().damageSources().generic(), this.getHealth());
        }
    }

    public ResourceLocation getBeeType() {
        return EntityType.getKey(this.getType());
    }

    public String getBeeName() {
        return getBeeName(getBeeType());
    }

    public static String getBeeName(ResourceLocation beeType) {
        String[] types = beeType.toString().split("[:]");
        String type = types[0];
        if (types.length > 1) {
            type = types[1];
        }
        return type.replace("_bee", "");
    }

    public GeneValue getAttributeValue(GeneAttribute parameter) {
        var attributes = this.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        return attributes.getAttributeValue(parameter);
    }

    public void setAttributeValue(GeneAttribute parameter, GeneValue geneValue) {
        // Give health boost based on endurance
        if (parameter.equals(GeneAttribute.ENDURANCE)) {
//            AttributeInstance healthMod = this.getAttribute(Attributes.MAX_HEALTH);
//            if (healthMod != null && geneValue.getValue() != 1) {
//                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_WEAK);
//                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_MEDIUM);
//                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_STRONG);
//                healthMod.addPermanentModifier(BeeAttributes.HEALTH_MODS.get(geneValue.getValue()));
//            }
        }

        var data = this.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        data.setAttributeValue(parameter, geneValue);
        this.setData(ProductiveBees.ATTRIBUTE_HANDLER, data);
    }

    public Map<GeneAttribute, GeneValue> getBeeAttributes() {
        return this.getData(ProductiveBees.ATTRIBUTE_HANDLER).getAttributes();
    }

    public boolean hasBeeAttributes() {
        return this.hasData(ProductiveBees.ATTRIBUTE_HANDLER);
    }

    public void setDefaultAttributes() {
        this.getData(ProductiveBees.ATTRIBUTE_HANDLER);
    }

    public boolean canOperateDuringNight() {
        return !getAttributeValue(GeneAttribute.BEHAVIOR).equals(GeneValue.BEHAVIOR_DIURNAL);
    }

    public boolean canOperateDuringRain() {
        return !getAttributeValue(GeneAttribute.WEATHER_TOLERANCE).equals(GeneValue.WEATHER_TOLERANCE_NONE);
    }

    public boolean canOperateDuringThunder() {
        return getAttributeValue(GeneAttribute.WEATHER_TOLERANCE).equals(GeneValue.WEATHER_TOLERANCE_ANY);
    }

    public void setRenderStatic() {
        renderStatic = true;
    }

    public boolean getRenderStatic() {
        return renderStatic;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(this.level().damageSources().inWall()) || source.equals(this.level().damageSources().sweetBerryBush()) || (source.equals(this.level().damageSources().wither()) && getBeeType().getPath().contains("dye_bee")) || super.isInvulnerableTo(source);
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

        tag.putString("bee_type", this instanceof SolitaryBee ? "solitary" : "hive");
        tag.putFloat("MaxHealth", getMaxHealth());
        tag.putBoolean("HasConverted", hasConverted());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        this.internalSetHasNectar(tag.getBoolean("HasNectar"));

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

        BeeHelper.setOffspringAttributes((Bee) newBee, this, targetEntity);

        return (Bee) newBee;
    }

    @Override
    public boolean canMate(@Nonnull Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!(otherAnimal instanceof Bee)) {
            return false;
        } else {
            return (this.isInLove() && otherAnimal.isInLove()) &&
                    (
                            (level() instanceof ServerLevel serverLevel && BeeHelper.getRandomBreedingRecipe(this, otherAnimal, serverLevel) != null) || // check if there's an offspring recipe
                                    canSelfBreed() || // allows self breeding
                                    !(otherAnimal instanceof ProductiveBee) // or not a productive bee
                    );
        }
    }

    public void postPollinate() {
        if (hasNectar() && savedFlowerPos != null) {
            BlockState flowerBlockState = level().getBlockState(savedFlowerPos);
            if (BeeHelper.hasBlockConversionRecipe(this, flowerBlockState)) {
                RecipeHolder<BlockConversionRecipe> recipe = BeeHelper.getBlockConversionRecipe(this, flowerBlockState);
                if (recipe != null) {
                    if (level().random.nextFloat() <= recipe.value().chance) {
                        level().setBlock(savedFlowerPos, recipe.value().stateTo, 3);
                        level().levelEvent(2005, savedFlowerPos, 0);
                    }
                    // Set flag to prevent produce when trying to convert blocks
                    setHasConverted(!recipe.value().pollinates);
                }
            } else {
                BlockEntity blockEntity = level().getBlockEntity(savedFlowerPos);
                if (blockEntity instanceof FeederBlockEntity feederBlockEntity) {
                    BlockEntity hiveBlockEntity = hivePos != null ? level().getBlockEntity(hivePos) : null;
                    for (ItemStack stack : feederBlockEntity.getInventoryItems()) {
                        if (stack.getItem() instanceof BlockItem blockItem) {
                            RecipeHolder<BlockConversionRecipe> blockRecipe = BeeHelper.getBlockConversionRecipe(this, blockItem.getBlock().defaultBlockState());
                            if (blockRecipe != null && hiveBlockEntity instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                                if (level().random.nextFloat() <= blockRecipe.value().chance) {
                                    ItemStack output = new ItemStack(blockRecipe.value().stateTo.getBlock().asItem());
                                    if (beehiveBlockEntity.isSim()) {
                                        if (!output.equals(ItemStack.EMPTY) &&
                                                beehiveBlockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemHandler
                                                && itemHandler.addOutput(output).getCount() == 0) {
                                            stack.shrink(1);
                                        }
                                    } else {
                                        Block.popResourceFromFace(level(), feederBlockEntity.getBlockPos(), Direction.UP, output);
                                        stack.shrink(1);
                                    }
                                }
                                setHasConverted(!blockRecipe.value().pollinates);
                                return;
                            }
                        }
                        RecipeHolder<ItemConversionRecipe> itemRecipe = BeeHelper.getItemConversionRecipe(this, stack);
                        if (itemRecipe != null && hiveBlockEntity instanceof AdvancedBeehiveBlockEntity beehiveBlockEntity) {
                            if (level().random.nextFloat() <= itemRecipe.value().chance) {
                                if (beehiveBlockEntity.isSim()) {
                                    if (beehiveBlockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemHandler
                                            && itemHandler.addOutput(itemRecipe.value().output.copy()).getCount() == 0) {
                                        stack.shrink(1);
                                    }
                                } else {
                                    Block.popResourceFromFace(level(), feederBlockEntity.getBlockPos(), Direction.UP, itemRecipe.value().output.copy());
                                    stack.shrink(1);
                                }
                            }
                            setHasConverted(!itemRecipe.value().pollinates);
                            return;
                        }
                        // TODO 1.21 BeeComponentChangerRecipe
//                        BeeNBTChangerRecipe nbtRecipe = BeeHelper.getNBTChangerRecipe(this, stack);
//                        CompoundTag tag = stack.getTag();
//                        if (nbtRecipe != null && tag != null) {
//                            switch (nbtRecipe.method) {
//                                case "decrement" -> tag.putInt(nbtRecipe.attribute, Math.max(nbtRecipe.min, tag.getInt(nbtRecipe.attribute) - nbtRecipe.value));
//                                case "increment" -> tag.putInt(nbtRecipe.attribute, Math.min(nbtRecipe.max, tag.getInt(nbtRecipe.attribute) + nbtRecipe.value));
//                                case "set" -> tag.putInt(nbtRecipe.attribute, nbtRecipe.value);
//                                case "unset" -> tag.remove(nbtRecipe.attribute);
//                            }
//                            stack.setTag(tag);
//                            // Set flag to prevent produce
//                            setHasConverted(true);
//                            return;
//                        }
                    }
                }
            }
        }
    }

    public String getFlowerType() {
        return "block";
    }

    public int getColor(int tintIndex, float tickCount) {
        return -1;
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
            if (
                    ProductiveBee.this.hasHive() &&
                    ProductiveBee.this.wantsToEnterHive() &&
                    ProductiveBee.this.hivePos.closerToCenterThan(ProductiveBee.this.position(), 2.0D) &&
                    ProductiveBee.this.level().getBlockEntity(ProductiveBee.this.hivePos) instanceof BeehiveBlockEntity beehiveblockentity
            ) {
                if (!beehiveblockentity.isFull()) {
                    return true;
                }

                ProductiveBee.this.hivePos = null;
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
                } else if (!getFlowerType().equals("entity_type")) {
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
        public void tick() {
            if (ProductiveBee.this.hasSavedFlowerPos()) {
                super.tick();
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
                var entities = findEntities(RancherBee.predicate, 5D);
                if (entities.isPresent()) {
                    return entities;
                }
            }
            if (ProductiveBee.this instanceof ResinBee) {
                var entities = findEntities(ResinBee.predicate, 5D);
                if (entities.isPresent()) {
                    return entities;
                }
            }
            if (ProductiveBee.this instanceof ConfigurableBee && ProductiveBee.this.getFlowerType().equals("entity_types")) {
                CompoundTag nbt = ((ConfigurableBee) ProductiveBee.this).getNBTData();
                if (nbt != null) {
                    if (nbt.contains("flowerTag")) {
                        var flowerTag = ModTags.getEntityTag(ResourceLocation.parse(nbt.getString("flowerTag")));
                        var amberBlocks = this.findNearestBlock(pos -> {
                            if (ProductiveBee.this.level().getBlockEntity(pos) instanceof AmberBlockEntity amberBlockEntity) {
                                var entity = amberBlockEntity.getCachedEntity();
                                return entity != null && entity.getType().is(flowerTag);
                            }
                            return false;
                        }, 5);
                        if (amberBlocks.isPresent()) {
                            return amberBlocks;
                        }
                        var entityPositions = findEntities(entity -> entity instanceof Mob && nbt.getBoolean("inverseFlower") != entity.getType().is(flowerTag), 5D);
                        if (entityPositions.isPresent()) {
                            return entityPositions;
                        }
                    }
                }
            }
            return this.findNearestBlock(this.flowerPredicate, 5);
        }

        private Optional<BlockPos> findNearestBlock(Predicate<BlockPos> predicate, double distance) {
            BlockPos blockpos = ProductiveBee.this.blockPosition();
            BlockPos.MutableBlockPos mutableblockpos = new BlockPos.MutableBlockPos();

            for (int i = 0; (double) i <= distance; i = i > 0 ? -i : 1 - i) {
                for (int j = 0; (double) j < distance; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
                            if (blockpos.closerThan(mutableblockpos, distance) && predicate.test(mutableblockpos)) {
                                return Optional.of(mutableblockpos.immutable());
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
                return Optional.of(blockpos$mutable.immutable());
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
                    bee = ProductiveBee.this.getBeeType().toString();
                }
                ProductiveBees.LOGGER.debug("Nesting tag for " + bee + " not found. Looking for " + nestTag);
            }

            return !ProductiveBee.this.hasRestriction() &&
                    ProductiveBee.this.wantsToEnterHive() &&
                    !this.isCloseEnough(ProductiveBee.this.hivePos) &&
                    ProductiveBee.this.level().getBlockState(ProductiveBee.this.hivePos).is(nestTag);
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (ProductiveBee.this.closerThan(pos, 3)) {
                return true;
            } else {
                Path path = ProductiveBee.this.navigation.getPath();
                return path != null && path.getTarget().equals(pos) && path.canReach() && path.isDone();
            }
        }

        @Override
        public void blacklistTarget(BlockPos pos) {
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
            super(entity, speed, Ingredient.of(ItemTags.FLOWERS), false);
//            List<ItemStack> listOfStuff = Arrays.asList(Ingredient.of(ItemTags.FLOWERS).getItems());
////            listOfStuff.addAll(Arrays.asList(ProductiveBee.this.getBreedingItem().getItems())); TODO
//            items = Ingredient.of(listOfStuff.stream()); // public net.minecraft.world.entity.ai.goal.TemptGoal items
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
