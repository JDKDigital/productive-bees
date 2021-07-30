package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.entity.bee.hive.RancherBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntityAbstract;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductiveBeeEntity extends BeeEntity
{
    protected Map<BeeAttribute<?>, Object> beeAttributes = new HashMap<>();

    protected Predicate<PointOfInterestType> beehiveInterests = (poiType) -> {
        PointOfInterestType rbTiered = ForgeRegistries.POI_TYPES.getValue(new ResourceLocation("resourcefulbees", "tiered_beehive_poi"));
        return poiType == PointOfInterestType.BEEHIVE || poiType == rbTiered;
    };
    private Color primaryColor = null;
    private Color secondaryColor = null;
    private boolean renderStatic;

    protected FollowParentGoal followParentGoal;
    protected BreedGoal breedGoal;
    protected EnterBeehiveGoal enterHiveGoal;

    public ProductiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        setAttributeValue(BeeAttributes.PRODUCTIVITY, level.random.nextInt(3));
        setAttributeValue(BeeAttributes.TEMPER, 1);
        setAttributeValue(BeeAttributes.ENDURANCE, level.random.nextInt(4));
        setAttributeValue(BeeAttributes.BEHAVIOR, 0);
        setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, 0);
        setAttributeValue(BeeAttributes.TYPE, "hive");
        setAttributeValue(BeeAttributes.APHRODISIACS, ItemTags.FLOWERS);

        // Goal to make entity follow player, must be registered after init to use bee attributes
        this.goalSelector.addGoal(3, new ProductiveTemptGoal(this, 1.25D));
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.beePollinateGoal = new ProductiveBeeEntity.PollinateGoal();
        this.goalSelector.addGoal(4, this.beePollinateGoal);

        this.goToKnownFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);

        this.goalSelector.addGoal(7, new BeeEntity.FindPollinationTargetGoal());
    }

    protected void registerBaseGoals() {
        this.goalSelector.addGoal(0, new BeeEntity.StingGoal(this, 1.4D, true));

        this.enterHiveGoal = new BeeEntity.EnterBeehiveGoal();
        this.goalSelector.addGoal(1, this.enterHiveGoal);

        this.breedGoal = new BreedGoal(this, 1.0D, ProductiveBeeEntity.class);
        this.goalSelector.addGoal(2, this.breedGoal);

        this.followParentGoal = new FollowParentGoal(this, 1.25D);
        this.goalSelector.addGoal(5, this.followParentGoal);

        this.goalSelector.addGoal(5, new ProductiveBeeEntity.UpdateNestGoal());
        this.goToHiveGoal = new ProductiveBeeEntity.FindNestGoal();
        this.goalSelector.addGoal(5, this.goToHiveGoal);

        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));

        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));

        // Empty default goals
        this.beePollinateGoal = new EmptyPollinateGoal();
        this.goToKnownFlowerGoal = new EmptyFindFlowerGoal();
    }

    @Override
    public void tick() {
        super.tick();

        // "Positive" effect to nearby players
        if (!level.isClientSide && tickCount % ProductiveBeesConfig.BEE_ATTRIBUTES.effectTicks.get() == 0) {
            BeeEffect effect = getBeeEffect();
            if (effect != null && effect.getEffects().size() > 0) {
                List<PlayerEntity> players = level.getEntitiesOfClass(PlayerEntity.class, (new AxisAlignedBB(new BlockPos(ProductiveBeeEntity.this.blockPosition()))).inflate(8.0D, 6.0D, 8.0D));
                if (players.size() > 0) {
                    players.forEach(playerEntity -> {
                        effect.getEffects().forEach((potionEffect, duration) -> {
                            playerEntity.addEffect(new EffectInstance(potionEffect, duration));
                        });
                    });
                }
            }
        }

        // Attribute improvement while leashed
        if (!level.isClientSide && isLeashed() && tickCount % ProductiveBeesConfig.BEE_ATTRIBUTES.leashedTicks.get() == 0) {
            // Rain tolerance improvements
            int tolerance = getAttributeValue(BeeAttributes.WEATHER_TOLERANCE);
            if (tolerance < 2 && level.random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.toleranceChance.get()) {
                if ((tolerance < 1 && level.isRaining()) || level.isThundering()) {
                    beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, tolerance + 1);
                }
            }
            // Behavior improvement
            int behavior = getAttributeValue(BeeAttributes.BEHAVIOR);
            if (behavior < 2 && level.random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.behaviorChance.get()) {
                // If diurnal, it can change to nocturnal
                if (behavior < 1 && level.isNight()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, level.random.nextFloat() < 0.85F ? 1 : 2);
                }
                // If nocturnal, it can become metaturnal or back to diurnal
                else if (behavior == 1 && !level.isNight()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, level.random.nextFloat() < 0.85F ? 2 : 0);
                }
            }

            // It might die when leashed outside
            boolean isInDanger = (tolerance < 1 && level.isRaining()) || (behavior < 1 && level.isNight());
            if (isInDanger && level.random.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.damageChance.get()) {
                setHealth(getHealth() - (getMaxHealth() / 3) - 1);
            }
        }

        // Kill below Y level 0
        if (this.getY() < -0.0D) {
            this.outOfWorld();
        }
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return BeeEntity.createAttributes();
    }

    @Nonnull
    @Override
    public EntitySize getDimensions(Pose poseIn) {
        return super.getDimensions(poseIn).scale(getSizeModifier());
    }

    public float getSizeModifier() {
        return 1.0f;
    }

    @Override
    public boolean isAngry() { // isAngry
        return super.isAngry() && getAttributeValue(BeeAttributes.TEMPER) > 0;
    }

    @Override
    public boolean isFlowerValid(BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }

        Block flowerBlock = level.getBlockState(pos).getBlock();

        return (
            isFlowerBlock(flowerBlock) ||
            (flowerBlock instanceof Feeder && isValidFeeder(level.getBlockEntity(pos), ProductiveBeeEntity.this::isFlowerBlock))
        );
    }

    public boolean doesHiveAcceptBee(BlockPos pos) {
        TileEntity tileentity = level.getBlockEntity(pos);
        if (tileentity instanceof AdvancedBeehiveTileEntityAbstract) {
            return ((AdvancedBeehiveTileEntityAbstract) tileentity).acceptsBee(this);
        }
        return true;
    }

    public static boolean isValidFeeder(TileEntity tile, Predicate<Block> validator) {
        AtomicBoolean hasValidBlock = new AtomicBoolean(false);
        if (tile instanceof FeederTileEntity) {
            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int slot = 0; slot < handler.getSlots(); ++slot) {
                    Item slotItem = handler.getStackInSlot(slot).getItem();
                    if (slotItem instanceof BlockItem && validator.test(((BlockItem) slotItem).getBlock())) {
                        hasValidBlock.set(true);
                    }
                }
            });
        }
        return hasValidBlock.get();
    }

    @Override
    public boolean wantsToEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
            boolean shouldReturnToHive =
                this.isTiredOfLookingForNectar() ||
                this.hasNectar() ||
                (level.isNight() && !canOperateDuringNight()) ||
                (level.isRaining() && !canOperateDuringRain());

            return shouldReturnToHive && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    @Override
    public void setHasStung(boolean hasStung) {
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 2) {
            // 50% chance to not loose stinger
            hasStung = level.random.nextBoolean();
        }
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 3) {
            // 80% chance to not loose stinger
            hasStung = level.random.nextFloat() < .2;
        }
        super.setHasStung(hasStung);
    }

//    @Override
//    public boolean isBreedingItem(ItemStack itemStack) {
//        return itemStack.getItem().is(getAttributeValue(BeeAttributes.APHRODISIACS));
//    }

    public String getBeeType() {
        return getEncodeId();
    }

    public String getBeeName() {
        return getBeeName(true);
    }

    public String getBeeName(boolean stripName) {
        String[] types = getBeeType().split("[:]");
        String type = types[0];
        if (types.length > 1) {
            type = types[1];
        }
        return stripName ? type.replace("_bee", "") : type;
    }

    public String getRenderer() {
        return "default";
    }

    public <T> T getAttributeValue(BeeAttribute<T> parameter) {
        return (T) this.beeAttributes.get(parameter);
    }

    public void setAttributeValue(BeeAttribute<?> parameter, Integer value) {
        // Give health boost based on endurance
        if (parameter.equals(BeeAttributes.ENDURANCE)) {
            ModifiableAttributeInstance healthMod = this.getAttribute(Attributes.MAX_HEALTH);
            if (healthMod != null && value != 1) {
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_WEAK);
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_MEDIUM);
                healthMod.removeModifier(BeeAttributes.HEALTH_MOD_ID_STRONG);
                healthMod.addPermanentModifier(BeeAttributes.HEALTH_MODS.get(value));
            }
        }

        this.beeAttributes.put(parameter, value);
    }

    public void setAttributeValue(BeeAttribute<?> parameter, Object value) {
        this.beeAttributes.put(parameter, value);
    }

    public Map<BeeAttribute<?>, Object> getBeeAttributes() {
        return beeAttributes;
    }

    public boolean canOperateDuringNight() {
        return getAttributeValue(BeeAttributes.BEHAVIOR) > 0;
    }

    boolean canOperateDuringRain() {
        return getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) == 1;
    }

    boolean canOperateDuringThunder() {
        return getAttributeValue(BeeAttributes.WEATHER_TOLERANCE) == 2;
    }

    public int getTimeInHive(boolean hasNectar) {
        return hasNectar ? 2400 : 600;
    }

    public void setRenderStatic() {
        renderStatic = true;
    }

    public boolean getRenderStatic() {
        return renderStatic;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (getBeeName().equals("dye") && source.equals(DamageSource.WITHER)) {
            return true;
        }
        return source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.SWEET_BERRY_BUSH) || super.isInvulnerableTo(source);
    }

    @Nonnull
    @Override
    protected PathNavigator createNavigation(@Nonnull World worldIn) {
        PathNavigator navigator = super.createNavigation(worldIn);

        if (navigator instanceof FlyingPathNavigator) {
            navigator.setCanFloat(false);
            ((FlyingPathNavigator) navigator).setCanPassDoors(false);
        }
        return navigator;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);

        tag.putInt("bee_productivity", this.getAttributeValue(BeeAttributes.PRODUCTIVITY));
        tag.putInt("bee_endurance", this.getAttributeValue(BeeAttributes.ENDURANCE));
        tag.putInt("bee_temper", this.getAttributeValue(BeeAttributes.TEMPER));
        tag.putInt("bee_behavior", this.getAttributeValue(BeeAttributes.BEHAVIOR));
        tag.putInt("bee_weather_tolerance", this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE));
        tag.putString("bee_type", this.getAttributeValue(BeeAttributes.TYPE));
//        tag.putString("bee_aphrodisiac", this.getAttributeValue(BeeAttributes.APHRODISIACS).toString());
        tag.putFloat("MaxHealth", getMaxHealth());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("bee_productivity")) {
            beeAttributes.clear();
            setAttributeValue(BeeAttributes.PRODUCTIVITY, tag.getInt("bee_productivity"));
            setAttributeValue(BeeAttributes.ENDURANCE, tag.contains("bee_endurance") ? tag.getInt("bee_endurance") : 1);
            setAttributeValue(BeeAttributes.TEMPER, tag.getInt("bee_temper"));
            setAttributeValue(BeeAttributes.BEHAVIOR, tag.getInt("bee_behavior"));
            setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, tag.getInt("bee_weather_tolerance"));
            setAttributeValue(BeeAttributes.TYPE, tag.getString("bee_type"));
//            setAttributeValue(BeeAttributes.APHRODISIACS, ItemTags.createOptional(new ResourceLocation(tag.getString("bee_aphrodisiac"))));
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return BeeCreator.getSpawnEgg(this.getBeeType());
    }


    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();

        if (!this.isBaby()) {
            BlockPos pos = blockPosition();
            if (level.isEmptyBlock(pos)) {
                this.setPos(pos.getX(), pos.getY(), pos.getZ());
            } else if (level.isEmptyBlock(pos.below())) {
                pos = pos.below();
                this.setPos(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Override
    public BeeEntity getBreedOffspring(@Nonnull ServerWorld world, AgeableEntity targetEntity) {
        Entity newBee = BeeHelper.getBreedingResult(this, targetEntity, world);

        if (!(newBee instanceof BeeEntity)) {
            return EntityType.BEE.create(world);
        }

        if (newBee instanceof ProductiveBeeEntity) {
            BeeHelper.setOffspringAttributes((ProductiveBeeEntity) newBee, this, targetEntity);
        }

        return (BeeEntity) newBee;
    }

    @Override
    public boolean canMate(@Nonnull AnimalEntity otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!(otherAnimal instanceof BeeEntity)) {
            return false;
        } else {
            return (
                this.isInLove() &&
                otherAnimal.isInLove()
            ) &&
                (
                    (level instanceof ServerWorld && BeeHelper.getRandomBreedingRecipe(this, otherAnimal, (ServerWorld) level) != null) || // check if there's an offspring recipe
                    canSelfBreed() || // allows self breeding
                    !(otherAnimal instanceof ProductiveBeeEntity) // or not a productive bee
                );
        }
    }

    public boolean canSelfBreed() {
        return true;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isBaby() ? sizeIn.height * 0.25F : sizeIn.height * 0.5F;
    }

    public void setColor(Color primary, Color secondary) {
        this.primaryColor = primary;
        this.secondaryColor = secondary;
    }

    public Color getColor(int tintIndex) {
        return tintIndex == 0 ? primaryColor : secondaryColor;
    }

    public boolean isFlowerBlock(Block flowerBlock) {
        return flowerBlock.is(BlockTags.FLOWERS);
    }

    public ITag<Block> getNestingTag() {
        return BlockTags.BEEHIVES;
    }

    public BeeEffect getBeeEffect() {
        return null;
    }

    public class PollinateGoal extends BeeEntity.PollinateGoal
    {
        public Predicate<BlockPos> flowerPredicate = (blockPos) -> {
            BlockState blockState = ProductiveBeeEntity.this.level.getBlockState(blockPos);
            boolean isInterested = false;
            try {
                if (blockState.getBlock() instanceof Feeder) {
                    isInterested = isValidFeeder(level.getBlockEntity(blockPos), ProductiveBeeEntity.this::isFlowerBlock);
                } else {
                    isInterested = ProductiveBeeEntity.this.isFlowerBlock(blockState.getBlock());
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
            if (ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            } else if (ProductiveBeeEntity.this.hasNectar()) {
                return false;
            } else if (ProductiveBeeEntity.this.level.isRaining() && !ProductiveBeeEntity.this.canOperateDuringRain()) {
                return false;
            } else if (ProductiveBeeEntity.this.level.isThundering() && !ProductiveBeeEntity.this.canOperateDuringThunder()) {
                return false;
            } else if (ProductiveBeeEntity.this.random.nextFloat() <= 0.7F) {
                return false;
            } else {
                Optional<BlockPos> optional = this.findNearbyFlower();
                if (optional.isPresent()) {
                    ProductiveBeeEntity.this.savedFlowerPos = optional.get();
                    ProductiveBeeEntity.this.navigation.moveTo((double) ProductiveBeeEntity.this.savedFlowerPos.getX() + 0.5D, (double) ProductiveBeeEntity.this.savedFlowerPos.getY() + 0.5D, (double) ProductiveBeeEntity.this.savedFlowerPos.getZ() + 0.5D, 1.2F);
                    return true;
                }
                // Failing to find a target will set a cooldown before next attempt
                ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower = 70 + level.random.nextInt(50);
                return false;
            }
        }

        @Nonnull
        @Override
        public Optional<BlockPos> findNearbyFlower() {
            if (ProductiveBeeEntity.this instanceof RancherBeeEntity) {
                return findEntities(RancherBeeEntity.predicate, 5D);
            }
            return this.findFlower(this.flowerPredicate, 5);
        }

        private MutableBoundingBox box = null;

        private Optional<BlockPos> findFlower(Predicate<BlockPos> predicate, int distance) {
            BlockPos blockpos = ProductiveBeeEntity.this.blockPosition();
            BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(0, 0, 0);

            if (box == null) {
                box = MutableBoundingBox.createProper(blockpos.getX() + distance, blockpos.getY() + distance, blockpos.getZ() + distance, blockpos.getX() - distance, blockpos.getY() - distance, blockpos.getZ() - distance);
            } else {
                box.x1 = blockpos.getX() + distance;
                box.y1 = blockpos.getY() + distance;
                box.z1 = blockpos.getZ() + distance;
                box.x0 = blockpos.getX() - distance;
                box.y0 = blockpos.getY() - distance;
                box.z0 = blockpos.getZ() - distance;
            }
            AtomicReference<Double> lastDistance = new AtomicReference<>(100.0D);
            BlockPos.betweenClosedStream(box).filter(predicate).forEach(blockPos -> {
                double currDistance = blockpos.distSqr(blockPos);
                if (currDistance < lastDistance.get()) {
                    lastDistance.set(currDistance);
                    mutableBlockPos.set(blockPos);
                }
            });
            if (lastDistance.get() < 100) {
                return Optional.of(mutableBlockPos);
            }

            return Optional.empty();
        }

        private Optional<BlockPos> findEntities(Predicate<Entity> predicate, double distance) {
            BlockPos blockpos = ProductiveBeeEntity.this.blockPosition();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            List<Entity> ranchables = level.getEntities(ProductiveBeeEntity.this, (new AxisAlignedBB(blockpos).expandTowards(distance, distance, distance)), predicate);
            if (ranchables.size() > 0) {
                CreatureEntity entity = (CreatureEntity) ranchables.get(0);
                entity.getNavigation().setSpeedModifier(0);
                blockpos$mutable.set(entity.getX(), entity.getY(), entity.getZ());
                return Optional.of(blockpos$mutable);
            }

            return Optional.empty();
        }
    }

    public class FindNestGoal extends BeeEntity.FindBeehiveGoal
    {
        public FindNestGoal() {
            super();
        }

        @Override
        public boolean canBeeUse() {
            if (!ProductiveBeeEntity.this.hasHive()) {
                return false;
            }

            ITag<Block> nestTag = ProductiveBeeEntity.this.getNestingTag();
            try {
                if (nestTag == null || nestTag.getValues().size() == 0) {
                    return false;
                }
            } catch (Exception e) {
                String bee = ProductiveBeeEntity.this.getEncodeId();
                if (ProductiveBeeEntity.this instanceof ConfigurableBeeEntity) {
                    bee = ProductiveBeeEntity.this.getBeeType();
                }
                ProductiveBees.LOGGER.debug("Nesting tag for " + bee + " not found. Looking for " + nestTag.toString());
            }

            return !ProductiveBeeEntity.this.hasRestriction() &&
                    ProductiveBeeEntity.this.wantsToEnterHive() &&
                    !this.isCloseEnough(ProductiveBeeEntity.this.hivePos) &&
                    ProductiveBeeEntity.this.level.getBlockState(ProductiveBeeEntity.this.hivePos).getBlock().is(nestTag);
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (ProductiveBeeEntity.this.closerThan(pos, 2)) {
                return true;
            } else {
                Path path = ProductiveBeeEntity.this.navigation.getPath();
                return path != null && path.getTarget().equals(pos) && path.canReach() && path.isDone();
            }
        }

        @Override
        protected void blacklistTarget(BlockPos pos) {
            TileEntity tileEntity = ProductiveBeeEntity.this.level.getBlockEntity(pos);
            ITag<Block> nestTag = ProductiveBeeEntity.this.getNestingTag();
            if (tileEntity != null && tileEntity.getBlockState().is(nestTag)) {
                this.blacklistedTargets.add(pos);

                while (this.blacklistedTargets.size() > 3) {
                    this.blacklistedTargets.remove(0);
                }
            }
        }
    }

    public class UpdateNestGoal extends BeeEntity.UpdateBeehiveGoal
    {
        public UpdateNestGoal() {
            super();
        }

        @Override
        public void start() {
            ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPos> nearbyNests = this.getNearbyFreeNests();
            if (!nearbyNests.isEmpty()) {
                Iterator<BlockPos> iterator = nearbyNests.iterator();
                BlockPos blockPos;
                do {
                    if (!iterator.hasNext()) {
                        ProductiveBeeEntity.this.goToHiveGoal.clearBlacklist();
                        ProductiveBeeEntity.this.hivePos = nearbyNests.get(0);
                        return;
                    }

                    blockPos = iterator.next();
                } while (ProductiveBeeEntity.this.goToHiveGoal.isTargetBlacklisted(blockPos));

                ProductiveBeeEntity.this.hivePos = blockPos;
            }
        }

        private List<BlockPos> getNearbyFreeNests() {
            BlockPos pos = ProductiveBeeEntity.this.blockPosition();

            PointOfInterestManager poiManager = ((ServerWorld) ProductiveBeeEntity.this.level).getPoiManager();

            Stream<PointOfInterest> stream = poiManager.getInRange(ProductiveBeeEntity.this.beehiveInterests, pos, 30, PointOfInterestManager.Status.ANY);

            return stream
                    .map(PointOfInterest::getPos)
                    .filter(ProductiveBeeEntity.this::doesHiveHaveSpace)
                    .filter(ProductiveBeeEntity.this::doesHiveAcceptBee)
                    .sorted(Comparator.comparingDouble((vec) -> vec.distSqr(pos)))
                    .collect(Collectors.toList());
        }
    }

    public class ProductiveTemptGoal extends TemptGoal
    {
        public ProductiveTemptGoal(CreatureEntity entity, double speed) {
            super(entity, speed, false, Ingredient.of(ItemTags.FLOWERS));
        }
    }

    public class EmptyPollinateGoal extends PollinateGoal
    {
        @Override
        public boolean canBeeUse() {
            return false;
        }
    }

    public class EmptyFindFlowerGoal extends FindFlowerGoal
    {
        @Override
        public boolean canBeeUse() {
            return false;
        }
    }
}
