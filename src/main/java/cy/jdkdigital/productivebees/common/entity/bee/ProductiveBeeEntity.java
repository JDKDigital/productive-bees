package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.entity.bee.hive.RancherBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.entity.*;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
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
import javax.annotation.Nullable;
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
        return poiType == PointOfInterestType.BEEHIVE ||
                poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
                poiType == ModPointOfInterestTypes.SOLITARY_NEST.get();
    };
    private Color primaryColor = null;
    private Color secondaryColor = null;
    private boolean renderStatic;

    protected FollowParentGoal followParentGoal;
    protected BreedGoal breedGoal;
    protected EnterBeehiveGoal enterHiveGoal;

    public ProductiveBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.PRODUCTIVITY, world.rand.nextInt(2));
        beeAttributes.put(BeeAttributes.TEMPER, 1);
        beeAttributes.put(BeeAttributes.ENDURANCE, world.rand.nextInt(3));
        beeAttributes.put(BeeAttributes.BEHAVIOR, 0);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 0);
        beeAttributes.put(BeeAttributes.TYPE, "hive");
        beeAttributes.put(BeeAttributes.APHRODISIACS, ItemTags.FLOWERS);

        // Goal to make entity follow player, must be registered after init to use bee attributes
        this.goalSelector.addGoal(3, new ProductiveTemptGoal(this, 1.25D));

        // Give health boost based on endurance
        if (getAttributeValue(BeeAttributes.ENDURANCE) != 1) {
            this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(BeeAttributes.HEALTH_MODS.get(getAttributeValue(BeeAttributes.ENDURANCE)));
            this.heal(this.getMaxHealth());
        }
    }

    @Override
    protected void registerGoals() {
        registerBaseGoals();

        this.pollinateGoal = new ProductiveBeeEntity.PollinateGoal();
        this.goalSelector.addGoal(4, this.pollinateGoal);

        this.findFlowerGoal = new BeeEntity.FindFlowerGoal();
        this.goalSelector.addGoal(6, this.findFlowerGoal);

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
        this.findBeehiveGoal = new ProductiveBeeEntity.FindNestGoal();
        this.goalSelector.addGoal(5, this.findBeehiveGoal);

        this.goalSelector.addGoal(8, new BeeEntity.WanderGoal());
        this.goalSelector.addGoal(9, new SwimGoal(this));

        this.targetSelector.addGoal(1, (new BeeEntity.AngerGoal(this)).setCallsForHelp());
        this.targetSelector.addGoal(2, new BeeEntity.AttackPlayerGoal(this));

        // Empty default goals
        this.pollinateGoal = new EmptyPollinateGoal();
        this.findFlowerGoal = new EmptyFindFlowerGoal();
    }

    @Override
    public void livingTick() {
        super.livingTick();

        // "Positive" effect to nearby players
        if (!world.isRemote && ticksExisted % ProductiveBeesConfig.BEE_ATTRIBUTES.effectTicks.get() == 0) {
            BeeEffect effect = getBeeEffect();
            if (effect != null && effect.getEffects().size() > 0) {
                List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, (new AxisAlignedBB(new BlockPos(ProductiveBeeEntity.this))).grow(8.0D, 6.0D, 8.0D));
                if (players.size() > 0) {
                    players.forEach(playerEntity -> {
                        effect.getEffects().forEach((potionEffect, duration) -> {
                            playerEntity.addPotionEffect(new EffectInstance(potionEffect, duration));
                        });
                    });
                }
            }
        }

        // Attribute improvement while leashed
        if (!world.isRemote && getLeashed() && ticksExisted % ProductiveBeesConfig.BEE_ATTRIBUTES.leashedTicks.get() == 0) {
            // Rain tolerance improvements
            int tolerance = getAttributeValue(BeeAttributes.WEATHER_TOLERANCE);
            if (tolerance < 2 && world.rand.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.toleranceChance.get()) {
                if ((tolerance < 1 && world.isRaining()) || world.isThundering()) {
                    beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, tolerance + 1);
                }
            }
            // Behavior improvement
            int behavior = getAttributeValue(BeeAttributes.BEHAVIOR);
            if (behavior < 2 && world.rand.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.behaviorChance.get()) {
                // If diurnal, it can change to nocturnal
                if (behavior < 1 && world.isNightTime()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, world.rand.nextFloat() < 0.85F ? 1 : 2);
                }
                // If nocturnal, it can become metaturnal or back to diurnal
                else if (behavior == 1 && !world.isNightTime()) {
                    beeAttributes.put(BeeAttributes.BEHAVIOR, world.rand.nextFloat() < 0.85F ? 2 : 0);
                }
            }

            // It might die when leashed outside
            boolean isInDanger = (tolerance < 1 && world.isRaining()) || (behavior < 1 && world.isNightTime());
            if (isInDanger && world.rand.nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.damageChance.get()) {
                setHealth(getHealth() - (getMaxHealth() / 3) - 1);
            }
        }
    }

    @Override
    public boolean isAngry() {
        return super.isAngry() && getAttributeValue(BeeAttributes.TEMPER) > 0;
    }

    @Override
    public boolean isFlowers(BlockPos pos) {
        if (!world.isBlockPresent(pos)) {
            return false;
        }

        Block flowerBlock = this.world.getBlockState(pos).getBlock();

        return (
                flowerBlock.isIn(getFlowerTag()) ||
                (
                    flowerBlock instanceof Feeder && isValidFeeder(world.getTileEntity(pos), getFlowerTag())
                )
            );
    }

    public static boolean isValidFeeder(TileEntity tile, Tag<Block> flowerTag) {
        AtomicBoolean hasValidBlock = new AtomicBoolean(false);
        if (tile instanceof FeederTileEntity) {
            tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                for (int slot = 0; slot < handler.getSlots(); ++slot) {
                    Item slotItem = handler.getStackInSlot(slot).getItem();
                    if (slotItem instanceof BlockItem && ((BlockItem) slotItem).getBlock().isIn(flowerTag)) {
                        hasValidBlock.set(true);
                    }
                }
            });
        }
        return hasValidBlock.get();
    }

    @Override
    public boolean canEnterHive() {
        if (this.stayOutOfHiveCountdown <= 0 && !this.pollinateGoal.isRunning() && !this.hasStung()) {
            boolean shouldReturnToHive =
                    this.failedPollinatingTooLong() ||
                            this.hasNectar() ||
                            (this.world.isNightTime() && !canOperateDuringNight()) ||
                            (this.world.isRaining() && !canOperateDuringRain());

            return shouldReturnToHive && !this.isHiveNearFire();
        } else {
            return false;
        }
    }

    @Override
    public void setHasStung(boolean hasStung) {
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 2) {
            // 50% chance to not loose stinger
            hasStung = world.rand.nextBoolean();
        }
        if (hasStung && getAttributeValue(BeeAttributes.ENDURANCE) == 3) {
            // 80% chance to not loose stinger
            hasStung = world.rand.nextFloat() < .2;
        }
        super.setHasStung(hasStung);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack.getItem().isIn(getAttributeValue(BeeAttributes.APHRODISIACS));
    }

    public String getBeeName() {
        return getBeeName(true);
    }

    public String getBeeName(boolean stripName) {
        String[] types = this.getEntityString().split("[:]");
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

    public void setRenderStatic() {
        renderStatic = true;
    }

    public boolean getRenderStatic() {
        return renderStatic;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.SWEET_BERRY_BUSH) || super.isInvulnerableTo(source);
    }

    @Nonnull
    @Override
    protected PathNavigator createNavigator(@Nonnull World worldIn) {
        PathNavigator navigator = super.createNavigator(world);

        if (navigator instanceof FlyingPathNavigator) {
            navigator.setCanSwim(false);
            ((FlyingPathNavigator) navigator).setCanEnterDoors(false);
        }
        return navigator;
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);

        tag.putInt("bee_productivity", this.getAttributeValue(BeeAttributes.PRODUCTIVITY));
        tag.putInt("bee_endurance", this.getAttributeValue(BeeAttributes.ENDURANCE));
        tag.putInt("bee_temper", this.getAttributeValue(BeeAttributes.TEMPER));
        tag.putInt("bee_behavior", this.getAttributeValue(BeeAttributes.BEHAVIOR));
        tag.putInt("bee_weather_tolerance", this.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE));
        tag.putString("bee_type", this.getAttributeValue(BeeAttributes.TYPE));
        tag.putString("bee_aphrodisiac", this.getAttributeValue(BeeAttributes.APHRODISIACS).getId().toString());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);

        if (tag.contains("bee_productivity")) {
            beeAttributes.clear();
            beeAttributes.put(BeeAttributes.PRODUCTIVITY, tag.getInt("bee_productivity"));
            beeAttributes.put(BeeAttributes.ENDURANCE, tag.contains("bee_endurance") ? tag.getInt("bee_endurance") : 1);
            beeAttributes.put(BeeAttributes.TEMPER, tag.getInt("bee_temper"));
            beeAttributes.put(BeeAttributes.BEHAVIOR, tag.getInt("bee_behavior"));
            beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, tag.getInt("bee_weather_tolerance"));
            beeAttributes.put(BeeAttributes.TYPE, tag.getString("bee_type"));
            beeAttributes.put(BeeAttributes.APHRODISIACS, new ItemTags.Wrapper(new ResourceLocation(tag.getString("bee_aphrodisiac"))));
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_" + this.getBeeName(false)));
        return new ItemStack(item);
    }

    @Override
    protected void onGrowingAdult() {
        super.onGrowingAdult();

        if (!this.isChild()) {
            BlockPos pos = this.getPosition();
            if (world.isAirBlock(pos)) {
                this.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
            } else if (world.isAirBlock(pos.down())) {
                pos = pos.down();
                this.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    @Override
    public BeeEntity createChild(AgeableEntity targetEntity) {
        BeeEntity newBee = BeeHelper.getBreedingResult(this, targetEntity, world);

        if (newBee instanceof ProductiveBeeEntity) {
            BeeHelper.setOffspringAttributes((ProductiveBeeEntity) newBee, this, targetEntity);
        }

        return newBee;
    }

    @Override
    public boolean canMateWith(@Nonnull AnimalEntity otherAnimal) {
        if (otherAnimal == this) {
            return false;
        }
        else if (!(otherAnimal instanceof BeeEntity)) {
            return false;
        }
        else {
            return this.isInLove() && otherAnimal.isInLove();
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.isChild() ? sizeIn.height * 0.25F : sizeIn.height * 0.5F;
    }

    public void setColor(Color primary, Color secondary) {
        this.primaryColor = primary;
        this.secondaryColor = secondary;
    }

    public Color getColor(int tintIndex) {
        return tintIndex == 0 ? primaryColor : secondaryColor;
    }

    public Tag<Block> getFlowerTag() {
        return BlockTags.FLOWERS;
    }

    public Tag<Block> getNestingTag() {
        return BlockTags.BEEHIVES;
    }

    public BeeEffect getBeeEffect() {
        return null;
    }

    public class PollinateGoal extends BeeEntity.PollinateGoal
    {
        public Predicate<BlockPos> flowerPredicate = (blockPos) -> {
            BlockState blockState = ProductiveBeeEntity.this.world.getBlockState(blockPos);
            Tag<Block> interests = ProductiveBeeEntity.this.getFlowerTag();
            boolean isInterested;
            if (blockState.getBlock() instanceof Feeder) {
                isInterested = isValidFeeder(world.getTileEntity(blockPos), ProductiveBeeEntity.this.getFlowerTag());
            } else {
                isInterested = blockState.isIn(interests);
                if (isInterested && blockState.isIn(BlockTags.TALL_FLOWERS)) {
                    if (blockState.getBlock() == Blocks.SUNFLOWER) {
                        isInterested = blockState.get(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
                    }
                }
            }
            return isInterested;
        };

        public PollinateGoal() {
            super();
        }

        public boolean canBeeStart() {
            if (ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower > 0) {
                return false;
            }
            else if (ProductiveBeeEntity.this.hasNectar()) {
                return false;
            }
            else if (ProductiveBeeEntity.this.world.isRaining() && !ProductiveBeeEntity.this.canOperateDuringRain()) {
                return false;
            }
            else if (ProductiveBeeEntity.this.world.isThundering() && !ProductiveBeeEntity.this.canOperateDuringThunder()) {
                return false;
            }
            else if (ProductiveBeeEntity.this.rand.nextFloat() <= 0.7F) {
                return false;
            }
            else {
                Optional<BlockPos> optional = this.getFlower();
                if (optional.isPresent()) {
                    ProductiveBeeEntity.this.savedFlowerPos = optional.get();
                    ProductiveBeeEntity.this.navigator.tryMoveToXYZ((double) ProductiveBeeEntity.this.savedFlowerPos.getX() + 0.5D, (double) ProductiveBeeEntity.this.savedFlowerPos.getY() + 0.5D, (double) ProductiveBeeEntity.this.savedFlowerPos.getZ() + 0.5D, 1.2F);
                    return true;
                }
                // Failing to find a target will set a cooldown before next attempt
                ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewFlower = 70 + world.rand.nextInt(50);
                return false;
            }
        }

        @Nonnull
        @Override
        public Optional<BlockPos> getFlower() {
            if (ProductiveBeeEntity.this instanceof RancherBeeEntity) {
                return findEntities(RancherBeeEntity.predicate, 5D);
            }
            return this.findFlower(this.flowerPredicate, 5);
        }

        private MutableBoundingBox box = null;
        private Optional<BlockPos> findFlower(Predicate<BlockPos> predicate, int distance) {
            BlockPos blockpos = new BlockPos(ProductiveBeeEntity.this);
            BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable(0,0,0);

            if (box == null)
                box = MutableBoundingBox.createProper(blockpos.getX() + distance , blockpos.getY() + distance , blockpos.getZ() + distance, blockpos.getX() - distance, blockpos.getY() - distance, blockpos.getZ() - distance);
            else {
                box.maxX = blockpos.getX() + distance;
                box.maxY = blockpos.getY() + distance;
                box.maxZ = blockpos.getZ() + distance;
                box.minX = blockpos.getX() - distance;
                box.minY = blockpos.getY() - distance;
                box.minZ = blockpos.getZ() - distance;
            }
            AtomicReference<Double> lastDistance = new AtomicReference<>(100.0D);
            BlockPos.getAllInBox(box).filter(predicate).forEach(blockPos -> {
                double currDistance = blockpos.distanceSq(blockPos);
                if (currDistance < lastDistance.get()){
                    lastDistance.set(currDistance);
                    mutableBlockPos.setPos(blockPos);
                }
            });
            if (lastDistance.get() < 100) {
                return Optional.of(mutableBlockPos);
            }

            return Optional.empty();
        }

        private Optional<BlockPos> findEntities(Predicate<Entity> predicate, double distance) {
            BlockPos blockpos = new BlockPos(ProductiveBeeEntity.this);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            List<Entity> ranchables = world.getEntitiesInAABBexcluding(ProductiveBeeEntity.this, (new AxisAlignedBB(blockpos).grow(distance, distance, distance)), predicate);
            if (ranchables.size() > 0) {
                CreatureEntity entity = (CreatureEntity) ranchables.get(0);
                entity.getNavigator().setSpeed(0);
                blockpos$mutable.setPos(entity);
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

        public boolean canBeeStart() {
            if (!ProductiveBeeEntity.this.hasHive()) {
                return false;
            }

            Tag<Block> nestTag = ProductiveBeeEntity.this.getNestingTag();
            if (nestTag == null || nestTag.getAllElements().size() == 0) {
                return false;
            }

            return !ProductiveBeeEntity.this.detachHome() &&
                    ProductiveBeeEntity.this.canEnterHive() &&
                    !this.isCloseEnough(ProductiveBeeEntity.this.hivePos) &&
                    ProductiveBeeEntity.this.world.getBlockState(ProductiveBeeEntity.this.hivePos).isIn(nestTag);
        }

        private boolean isCloseEnough(BlockPos pos) {
            if (ProductiveBeeEntity.this.isWithinDistance(pos, 2)) {
                return true;
            }
            else {
                Path path = ProductiveBeeEntity.this.navigator.getPath();
                return path != null && path.getTarget().equals(pos) && path.reachesTarget() && path.isFinished();
            }
        }

        protected void addPossibleHives(BlockPos pos) {
            TileEntity tileEntity = ProductiveBeeEntity.this.world.getTileEntity(pos);
            Tag<Block> nestTag = ProductiveBeeEntity.this.getNestingTag();
            if (tileEntity != null && tileEntity.getBlockState().isIn(nestTag)) {
                this.possibleHives.add(pos);

                while (this.possibleHives.size() > 3) {
                    this.possibleHives.remove(0);
                }
            }
        }
    }

    public class UpdateNestGoal extends BeeEntity.UpdateBeehiveGoal
    {
        public UpdateNestGoal() {
            super();
        }

        public void startExecuting() {
            ProductiveBeeEntity.this.remainingCooldownBeforeLocatingNewHive = 200;
            List<BlockPos> nearbyNests = this.getNearbyFreeNests();
            if (!nearbyNests.isEmpty()) {
                Iterator<BlockPos> iterator = nearbyNests.iterator();
                BlockPos blockPos;
                do {
                    if (!iterator.hasNext()) {
                        ProductiveBeeEntity.this.findBeehiveGoal.clearPossibleHives();
                        ProductiveBeeEntity.this.hivePos = nearbyNests.get(0);
                        return;
                    }

                    blockPos = iterator.next();
                } while (ProductiveBeeEntity.this.findBeehiveGoal.isPossibleHive(blockPos));

                ProductiveBeeEntity.this.hivePos = blockPos;
            }
        }

        private List<BlockPos> getNearbyFreeNests() {
            BlockPos pos = new BlockPos(ProductiveBeeEntity.this);

            PointOfInterestManager poiManager = ((ServerWorld) ProductiveBeeEntity.this.world).getPointOfInterestManager();

            Stream<PointOfInterest> stream = poiManager.func_219146_b(ProductiveBeeEntity.this.beehiveInterests, pos, 30, PointOfInterestManager.Status.ANY);

            return stream
                    .map(PointOfInterest::getPos)
                    .filter(ProductiveBeeEntity.this::doesHiveHaveSpace)
                    .sorted(Comparator.comparingDouble((vec) -> vec.distanceSq(pos)))
                    .collect(Collectors.toList());
        }
    }

    public class ProductiveTemptGoal extends TemptGoal
    {
        public ProductiveTemptGoal(CreatureEntity entity, double speed) {
            super(entity, speed, false, Ingredient.fromTag(getAttributeValue(BeeAttributes.APHRODISIACS)));
        }
    }

    public class EmptyPollinateGoal extends PollinateGoal {
        @Override
        public boolean shouldExecute() {
            return false;
        }
    }
    public class EmptyFindFlowerGoal extends FindFlowerGoal {
        @Override
        public boolean shouldExecute() {
            return false;
        }
    }
}
