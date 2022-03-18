package cy.jdkdigital.productivebees.common.entity.ai;

public class BeeAi
{
//    abstract static class BaseBeeGoal extends Goal
//    {
//        protected ProductiveBee bee;
//
//        protected BaseBeeGoal(ProductiveBee bee) {
//            this.bee = bee;
//        }
//
//        public abstract boolean canBeeUse();
//
//        public abstract boolean canBeeContinueToUse();
//
//        public boolean canUse() {
//            return this.canBeeUse() && !this.bee.isAngry();
//        }
//
//        public boolean canContinueToUse() {
//            return this.canBeeContinueToUse() && !this.bee.isAngry();
//        }
//    }
//
//    public static class BeeAttackGoal extends MeleeAttackGoal
//    {
//        public BeeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
//            super(mob, speedModifier, followingTargetEvenIfNotSeen);
//        }
//
//        public boolean canUse() {
//            return super.canUse() && (this.mob instanceof Bee && ((Bee) this.mob).isAngry() && !((Bee) this.mob).hasStung());
//        }
//
//        public boolean canContinueToUse() {
//            return super.canContinueToUse() && (this.mob instanceof Bee && ((Bee) this.mob).isAngry() && !((Bee) this.mob).hasStung());
//        }
//    }
//
//    public static class BeeBecomeAngryTargetGoal extends NearestAttackableTargetGoal<Player>
//    {
//        public BeeBecomeAngryTargetGoal(Bee bee) {
//            super(bee, Player.class, 10, true, false, bee::isAngryAt);
//        }
//
//        public boolean canUse() {
//            return this.beeCanTarget() && super.canUse();
//        }
//
//        public boolean canContinueToUse() {
//            boolean flag = this.beeCanTarget();
//            if (flag && this.mob.getTarget() != null) {
//                return super.canContinueToUse();
//            } else {
//                this.targetMob = null;
//                return false;
//            }
//        }
//
//        private boolean beeCanTarget() {
//            Bee bee = (Bee)this.mob;
//            return bee.isAngry() && !bee.hasStung();
//        }
//    }
//
//    public static class BeeEnterHiveGoal extends BaseBeeGoal {
//        public boolean canBeeUse() {
//            if (this.bee.hasHive() && this.bee.wantsToEnterHive() && this.bee.hivePos.closerThan(this.bee.position(), 2.0D)) {
//                BlockEntity blockentity = this.bee.level.getBlockEntity(this.bee.hivePos);
//                if (blockentity instanceof BeehiveBlockEntity beehiveblockentity) {
//                    if (!beehiveblockentity.isFull()) {
//                        return true;
//                    }
//
//                    this.bee.hivePos = null;
//                }
//            }
//
//            return false;
//        }
//
//        public boolean canBeeContinueToUse() {
//            return false;
//        }
//
//        public void start() {
//            BlockEntity blockentity = this.bee.level.getBlockEntity(this.bee.hivePos);
//            if (blockentity instanceof BeehiveBlockEntity beehiveblockentity) {
//                beehiveblockentity.addOccupant(Bee.this, this.bee.hasNectar());
//            }
//
//        }
//    }
//
//    public static class BeeGoToHiveGoal extends BaseBeeGoal {
//        public static final int MAX_TRAVELLING_TICKS = 600;
//        int travellingTicks = this.bee.level.random.nextInt(10);
//        private static final int MAX_BLACKLISTED_TARGETS = 3;
//        public final List<BlockPos> blacklistedTargets = Lists.newArrayList();
//        private Path lastPath;
//        private static final int TICKS_BEFORE_HIVE_DROP = 60;
//        private int ticksStuck;
//
//        public BeeGoToHiveGoal() {
//            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//        }
//
//        @Override
//        public boolean canBeeUse() {
//            if (!this.bee.hasHive()) {
//                return false;
//            }
//
//            TagKey<Block> nestTag = this.bee.getNestingTag();
//            try {
//                if (nestTag == null || nestTag.getValues().size() == 0) {
//                    return false;
//                }
//            } catch (Exception e) {
//                String bee = this.bee.getEncodeId();
//                if (ProductiveBee.this instanceof ConfigurableBee) {
//                    bee = this.bee.getBeeType();
//                }
//                ProductiveBees.LOGGER.debug("Nesting tag for " + bee + " not found. Looking for " + nestTag);
//            }
//
//            return !this.bee.hasRestriction() &&
//                    this.bee.wantsToEnterHive() &&
//                    !this.isCloseEnough(this.bee.hivePos) &&
//                    nestTag.contains(this.bee.level.getBlockState(this.bee.hivePos).getBlock());
//        }
//
//        public boolean canBeeContinueToUse() {
//            return this.canBeeUse();
//        }
//
//        public void start() {
//            this.travellingTicks = 0;
//            this.ticksStuck = 0;
//            super.start();
//        }
//
//        public void stop() {
//            this.travellingTicks = 0;
//            this.ticksStuck = 0;
//            this.bee.navigation.stop();
//            this.bee.navigation.resetMaxVisitedNodesMultiplier();
//        }
//
//        public void tick() {
//            if (this.bee.hivePos != null) {
//                ++this.travellingTicks;
//                if (this.travellingTicks > 600) {
//                    this.dropAndBlacklistHive();
//                } else if (!this.bee.navigation.isInProgress()) {
//                    if (!this.bee.closerThan(this.bee.hivePos, 16)) {
//                        if (this.bee.isTooFarAway(this.bee.hivePos)) {
//                            this.dropHive();
//                        } else {
//                            this.bee.pathfindRandomlyTowards(this.bee.hivePos);
//                        }
//                    } else {
//                        boolean flag = this.pathfindDirectlyTowards(this.bee.hivePos);
//                        if (!flag) {
//                            this.dropAndBlacklistHive();
//                        } else if (this.lastPath != null && this.bee.navigation.getPath().sameAs(this.lastPath)) {
//                            ++this.ticksStuck;
//                            if (this.ticksStuck > 60) {
//                                this.dropHive();
//                                this.ticksStuck = 0;
//                            }
//                        } else {
//                            this.lastPath = this.bee.navigation.getPath();
//                        }
//
//                    }
//                }
//            }
//        }
//
//        private boolean pathfindDirectlyTowards(BlockPos p_27991_) {
//            this.bee.navigation.setMaxVisitedNodesMultiplier(10.0F);
//            this.bee.navigation.moveTo((double)p_27991_.getX(), (double)p_27991_.getY(), (double)p_27991_.getZ(), 1.0D);
//            return this.bee.navigation.getPath() != null && this.bee.navigation.getPath().canReach();
//        }
//
//        public boolean isTargetBlacklisted(BlockPos p_27994_) {
//            return this.blacklistedTargets.contains(p_27994_);
//        }
//
//        protected void blacklistTarget(BlockPos p_27999_) {
//            this.blacklistedTargets.add(p_27999_);
//
//            while(this.blacklistedTargets.size() > 3) {
//                this.blacklistedTargets.remove(0);
//            }
//        }
//
//        public void clearBlacklist() {
//            this.blacklistedTargets.clear();
//        }
//
//        private void dropAndBlacklistHive() {
//            if (this.bee.hivePos != null) {
//                this.blacklistTarget(this.bee.hivePos);
//            }
//            this.dropHive();
//        }
//
//        private void dropHive() {
//            this.bee.hivePos = null;
//            this.bee.remainingCooldownBeforeLocatingNewHive = 200;
//        }
//
//        private boolean hasReachedTarget(BlockPos p_28002_) {
//            if (this.bee.closerThan(p_28002_, 2)) {
//                return true;
//            } else {
//                Path path = this.bee.navigation.getPath();
//                return path != null && path.getTarget().equals(p_28002_) && path.canReach() && path.isDone();
//            }
//        }
//
//        private boolean isCloseEnough(BlockPos pos) {
//            if (this.bee.closerThan(pos, 2)) {
//                return true;
//            } else {
//                Path path = this.bee.navigation.getPath();
//                return path != null && path.getTarget().equals(pos) && path.canReach() && path.isDone();
//            }
//        }
//
//        @Override
//        protected void blacklistTarget(BlockPos pos) {
//            BlockEntity tileEntity = this.bee.level.getBlockEntity(pos);
//            TagKey<Block> nestTag = this.bee.getNestingTag();
//            if (tileEntity != null && tileEntity.getBlockState().is(nestTag)) {
//                this.blacklistedTargets.add(pos);
//
//                while (this.blacklistedTargets.size() > 3) {
//                    this.blacklistedTargets.remove(0);
//                }
//            }
//        }
//    }
//
//    public static class BeeGoToKnownFlowerGoal extends BaseBeeGoal {
//        private static final int MAX_TRAVELLING_TICKS = 600;
//        int travellingTicks = this.bee.level.random.nextInt(10);
//
//        public BeeGoToKnownFlowerGoal() {
//            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//        }
//
//        public boolean canBeeUse() {
//            return this.bee.savedFlowerPos != null && !this.bee.hasRestriction() && this.wantsToGoToKnownFlower() && this.bee.isFlowerValid(this.bee.savedFlowerPos) && !this.bee.closerThan(this.bee.savedFlowerPos, 2);
//        }
//
//        public boolean canBeeContinueToUse() {
//            return this.canBeeUse();
//        }
//
//        public void start() {
//            this.travellingTicks = 0;
//            super.start();
//        }
//
//        public void stop() {
//            this.travellingTicks = 0;
//            this.bee.navigation.stop();
//            this.bee.navigation.resetMaxVisitedNodesMultiplier();
//        }
//
//        public void tick() {
//            if (this.bee.savedFlowerPos != null) {
//                ++this.travellingTicks;
//                if (this.travellingTicks > 600) {
//                    this.bee.savedFlowerPos = null;
//                } else if (!this.bee.navigation.isInProgress()) {
//                    if (this.bee.isTooFarAway(this.bee.savedFlowerPos)) {
//                        this.bee.savedFlowerPos = null;
//                    } else {
//                        this.bee.pathfindRandomlyTowards(this.bee.savedFlowerPos);
//                    }
//                }
//            }
//        }
//
//        private boolean wantsToGoToKnownFlower() {
//            return this.bee.ticksWithoutNectarSinceExitingHive > 2400;
//        }
//    }
//
//    public static class BeeGrowCropGoal extends BaseBeeGoal {
//        static final int GROW_CHANCE = 30;
//
//        public boolean canBeeUse() {
//            if (this.bee.getCropsGrownSincePollination() >= 10) {
//                return false;
//            } else if (this.bee.random.nextFloat() < 0.3F) {
//                return false;
//            } else {
//                return this.bee.hasNectar() && this.bee.isHiveValid();
//            }
//        }
//
//        public boolean canBeeContinueToUse() {
//            return this.canBeeUse();
//        }
//
//        public void tick() {
//            if (this.bee.random.nextInt(30) == 0) {
//                for(int i = 1; i <= 2; ++i) {
//                    BlockPos blockpos = this.bee.blockPosition().below(i);
//                    BlockState blockstate = this.bee.level.getBlockState(blockpos);
//                    Block block = blockstate.getBlock();
//                    boolean flag = false;
//                    IntegerProperty integerproperty = null;
//                    if (blockstate.is(BlockTags.BEE_GROWABLES)) {
//                        if (block instanceof CropBlock) {
//                            CropBlock cropblock = (CropBlock)block;
//                            if (!cropblock.isMaxAge(blockstate)) {
//                                flag = true;
//                                integerproperty = cropblock.getAgeProperty();
//                            }
//                        } else if (block instanceof StemBlock) {
//                            int j = blockstate.getValue(StemBlock.AGE);
//                            if (j < 7) {
//                                flag = true;
//                                integerproperty = StemBlock.AGE;
//                            }
//                        } else if (blockstate.is(Blocks.SWEET_BERRY_BUSH)) {
//                            int k = blockstate.getValue(SweetBerryBushBlock.AGE);
//                            if (k < 3) {
//                                flag = true;
//                                integerproperty = SweetBerryBushBlock.AGE;
//                            }
//                        } else if (blockstate.is(Blocks.CAVE_VINES) || blockstate.is(Blocks.CAVE_VINES_PLANT)) {
//                            ((BonemealableBlock)blockstate.getBlock()).performBonemeal((ServerLevel)this.bee.level, this.bee.random, blockpos, blockstate);
//                        }
//
//                        if (flag) {
//                            this.bee.level.levelEvent(2005, blockpos, 0);
//                            this.bee.level.setBlockAndUpdate(blockpos, blockstate.setValue(integerproperty, Integer.valueOf(blockstate.getValue(integerproperty) + 1)));
//                            this.bee.incrementNumCropsGrownSincePollination();
//                        }
//                    }
//                }
//
//            }
//        }
//    }
//
//    public static class BeeHurtByOtherGoal extends HurtByTargetGoal
//    {
//        public BeeHurtByOtherGoal(Bee p_28033_) {
//            super(p_28033_);
//        }
//
//        public boolean canContinueToUse() {
//            return this.bee.isAngry() && super.canContinueToUse();
//        }
//
//        protected void alertOther(Mob p_28035_, LivingEntity p_28036_) {
//            if (p_28035_ instanceof Bee && this.mob.hasLineOfSight(p_28036_)) {
//                p_28035_.setTarget(p_28036_);
//            }
//
//        }
//    }
//
//    public static class BeeLocateHiveGoal extends BaseBeeGoal {
//        public boolean canBeeUse() {
//            return this.bee.remainingCooldownBeforeLocatingNewHive == 0 && !this.bee.hasHive() && this.bee.wantsToEnterHive();
//        }
//
//        public boolean canBeeContinueToUse() {
//            return false;
//        }
//
//        @Override
//        public void start() {
//            this.bee.remainingCooldownBeforeLocatingNewHive = 200;
//            List<BlockPos> nearbyNests = this.findNearbyHivesWithSpace();
//            if (!nearbyNests.isEmpty()) {
//                Iterator<BlockPos> iterator = nearbyNests.iterator();
//                BlockPos blockPos;
//                do {
//                    if (!iterator.hasNext()) {
//                        this.bee.goToHiveGoal.clearBlacklist();
//                        this.bee.hivePos = nearbyNests.get(0);
//                        return;
//                    }
//
//                    blockPos = iterator.next();
//                } while (this.bee.goToHiveGoal.isTargetBlacklisted(blockPos));
//
//                this.bee.hivePos = blockPos;
//            }
//        }
//
//        private List<BlockPos> findNearbyHivesWithSpace() {
//            BlockPos pos = this.bee.blockPosition();
//
//            PoiManager poiManager = ((ServerLevel) this.bee.level).getPoiManager();
//
//            Stream<PoiRecord> stream = poiManager.getInRange(this.bee.beehiveInterests, pos, 30, PoiManager.Occupancy.ANY);
//
//            return stream
//                    .map(PoiRecord::getPos)
//                    .filter(ProductiveBee.this::doesHiveHaveSpace)
//                    .filter(ProductiveBee.this::doesHiveAcceptBee)
//                    .sorted(Comparator.comparingDouble((vec) -> vec.distSqr(pos)))
//                    .collect(Collectors.toList());
//        }
//    }
//
//    public static class BeePollinateGoal extends BaseBeeGoal {
//        private static final int MIN_POLLINATION_TICKS = 400;
//        private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
//        private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
//        private final Predicate<BlockState> VALID_POLLINATION_BLOCKS = (p_28074_) -> {
//            if (p_28074_.is(BlockTags.FLOWERS)) {
//                if (p_28074_.is(Blocks.SUNFLOWER)) {
//                    return p_28074_.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
//                } else {
//                    return true;
//                }
//            } else {
//                return false;
//            }
//        };
//        public Predicate<BlockPos> flowerPredicate = (blockPos) -> {
//            BlockState blockState = this.bee.level.getBlockState(blockPos);
//            boolean isInterested = false;
//            try {
//                if (blockState.getBlock() instanceof Feeder) {
//                    isInterested = isValidFeeder(level.getBlockEntity(blockPos), ProductiveBee.this::isFlowerBlock);
//                } else {
//                    isInterested = this.bee.isFlowerBlock(blockState);
//                    if (isInterested && blockState.is(BlockTags.TALL_FLOWERS)) {
//                        if (blockState.getBlock() == Blocks.SUNFLOWER) {
//                            isInterested = blockState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                // early tag access
//            }
//
//            return isInterested;
//        };
//        private static final double ARRIVAL_THRESHOLD = 0.1D;
//        private static final int POSITION_CHANGE_CHANCE = 25;
//        private static final float SPEED_MODIFIER = 0.35F;
//        private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6F;
//        private static final float HOVER_POS_OFFSET = 0.33333334F;
//        private int successfulPollinatingTicks;
//        private int lastSoundPlayedTick;
//        private boolean pollinating;
//        private Vec3 hoverPos;
//        private int pollinatingTicks;
//        private static final int MAX_POLLINATING_TICKS = 600;
//
//        public BeePollinateGoal() {
//            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//        }
//
//        @Override
//        public boolean canBeeUse() {
//            if (this.bee.remainingCooldownBeforeLocatingNewFlower > 0) {
//                return false;
//            } else if (this.bee.hasNectar()) {
//                return false;
//            } else if (this.bee.level.isRaining() && !this.bee.canOperateDuringRain()) {
//                return false;
//            } else if (this.bee.level.isThundering() && !this.bee.canOperateDuringThunder()) {
//                return false;
//            } else if (this.bee.random.nextFloat() <= 0.7F) {
//                return false;
//            } else {
//                Optional<BlockPos> optional = this.findNearbyFlower();
//                if (optional.isPresent()) {
//                    this.bee.savedFlowerPos = optional.get();
//                    this.bee.navigation.moveTo((double) this.bee.savedFlowerPos.getX() + 0.5D, (double) this.bee.savedFlowerPos.getY() + 0.5D, (double) this.bee.savedFlowerPos.getZ() + 0.5D, 1.2F);
//                    return true;
//                }
//                // Failing to find a target will set a cooldown before next attempt
//                this.bee.remainingCooldownBeforeLocatingNewFlower = 70 + level.random.nextInt(50);
//                return false;
//            }
//        }
//
//        public boolean canBeeContinueToUse() {
//            if (!this.pollinating) {
//                return false;
//            } else if (!this.bee.hasSavedFlowerPos()) {
//                return false;
//            } else if (this.bee.level.isRaining()) {
//                return false;
//            } else if (this.hasPollinatedLongEnough()) {
//                return this.bee.random.nextFloat() < 0.2F;
//            } else if (this.bee.tickCount % 20 == 0 && !this.bee.isFlowerValid(this.bee.savedFlowerPos)) {
//                this.bee.savedFlowerPos = null;
//                return false;
//            } else {
//                return true;
//            }
//        }
//
//        private boolean hasPollinatedLongEnough() {
//            return this.successfulPollinatingTicks > 400;
//        }
//
//        public boolean isPollinating() {
//            return this.pollinating;
//        }
//
//        void stopPollinating() {
//            this.pollinating = false;
//        }
//
//        public void start() {
//            this.successfulPollinatingTicks = 0;
//            this.pollinatingTicks = 0;
//            this.lastSoundPlayedTick = 0;
//            this.pollinating = true;
//            this.bee.resetTicksWithoutNectarSinceExitingHive();
//        }
//
//        public void stop() {
//            if (this.hasPollinatedLongEnough()) {
//                this.bee.setHasNectar(true);
//            }
//
//            this.pollinating = false;
//            this.bee.navigation.stop();
//            this.bee.remainingCooldownBeforeLocatingNewFlower = 200;
//            this.bee.postPollinate();
//        }
//
//        public void tick() {
//            ++this.pollinatingTicks;
//            if (this.pollinatingTicks > 600) {
//                this.bee.savedFlowerPos = null;
//            } else {
//                Vec3 vec3 = Vec3.atBottomCenterOf(this.bee.savedFlowerPos).add(0.0D, (double)0.6F, 0.0D);
//                if (vec3.distanceTo(this.bee.position()) > 1.0D) {
//                    this.hoverPos = vec3;
//                    this.setWantedPos();
//                } else {
//                    if (this.hoverPos == null) {
//                        this.hoverPos = vec3;
//                    }
//
//                    boolean flag = this.bee.position().distanceTo(this.hoverPos) <= 0.1D;
//                    boolean flag1 = true;
//                    if (!flag && this.pollinatingTicks > 600) {
//                        this.bee.savedFlowerPos = null;
//                    } else {
//                        if (flag) {
//                            boolean flag2 = this.bee.random.nextInt(25) == 0;
//                            if (flag2) {
//                                this.hoverPos = new Vec3(vec3.x() + (double)this.getOffset(), vec3.y(), vec3.z() + (double)this.getOffset());
//                                this.bee.navigation.stop();
//                            } else {
//                                flag1 = false;
//                            }
//
//                            this.bee.getLookControl().setLookAt(vec3.x(), vec3.y(), vec3.z());
//                        }
//
//                        if (flag1) {
//                            this.setWantedPos();
//                        }
//
//                        ++this.successfulPollinatingTicks;
//                        if (this.bee.random.nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
//                            this.lastSoundPlayedTick = this.successfulPollinatingTicks;
//                            this.bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
//                        }
//
//                    }
//                }
//            }
//        }
//
//        private void setWantedPos() {
//            this.bee.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), (double)0.35F);
//        }
//
//        private float getOffset() {
//            return (this.bee.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
//        }
//
//        public Optional<BlockPos> findNearbyFlower() {
//            if (ProductiveBee.this instanceof RancherBee) {
//                return findEntities(RancherBee.predicate, 5D);
//            }
//            return this.findNearestBlock(this.flowerPredicate, 5);
//        }
//
//        private Optional<BlockPos> findNearestBlock(Predicate<BlockPos> predicate, double distance) {
//            BlockPos blockpos = this.bee.blockPosition();
//            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
//
//            for(int i = 0; (double)i <= distance; i = i > 0 ? -i : 1 - i) {
//                for(int j = 0; (double)j < distance; ++j) {
//                    for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
//                        for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
//                            blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
//                            if (blockpos.closerThan(blockpos$mutableblockpos, distance) && predicate.test(blockpos$mutableblockpos)) {
//                                return Optional.of(blockpos$mutableblockpos);
//                            }
//                        }
//                    }
//                }
//            }
//
//            return Optional.empty();
//        }
//
//        private Optional<BlockPos> findEntities(Predicate<Entity> predicate, double distance) {
//            BlockPos blockpos = this.bee.blockPosition();
//            BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
//
//            List<Entity> ranchables = level.getEntities(ProductiveBee.this, (new AABB(blockpos).expandTowards(distance, distance, distance)), predicate);
//            if (ranchables.size() > 0) {
//                PathfinderMob entity = (PathfinderMob) ranchables.get(0);
//                entity.getNavigation().setSpeedModifier(0);
//                blockpos$mutable.set(entity.getX(), entity.getY(), entity.getZ());
//                return Optional.of(blockpos$mutable);
//            }
//
//            return Optional.empty();
//        }
//    }
//
//    public static class BeeWanderGoal extends Goal {
//        private static final int WANDER_THRESHOLD = 22;
//
//        public BeeWanderGoal() {
//            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
//        }
//
//        public boolean canUse() {
//            return this.bee.navigation.isDone() && this.bee.random.nextInt(10) == 0;
//        }
//
//        public boolean canContinueToUse() {
//            return this.bee.navigation.isInProgress();
//        }
//
//        public void start() {
//            Vec3 vec3 = this.findPos();
//            if (vec3 != null) {
//                this.bee.navigation.moveTo(this.bee.navigation.createPath(new BlockPos(vec3), 1), 1.0D);
//            }
//
//        }
//
//        @Nullable
//        private Vec3 findPos() {
//            Vec3 vec3;
//            if (this.bee.isHiveValid() && !this.bee.closerThan(this.bee.hivePos, 22)) {
//                Vec3 vec31 = Vec3.atCenterOf(this.bee.hivePos);
//                vec3 = vec31.subtract(this.bee.position()).normalize();
//            } else {
//                vec3 = this.bee.getViewVector(0.0F);
//            }
//
//            int i = 8;
//            Vec3 vec32 = HoverRandomPos.getPos(Bee.this, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
//            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(Bee.this, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
//        }
//    }
//
//    public static class PollinateGoal extends BeePollinateGoal
//    {
//
//    }
//
//    public static class ProductiveTemptGoal extends TemptGoal
//    {
//        public ProductiveTemptGoal(PathfinderMob entity, double speed) {
//            super(entity, speed, Ingredient.fromValues(Stream.concat(Stream.of(new Ingredient.TagValue(ItemTags.FLOWERS)), Stream.of(new Ingredient.ItemValue(new ItemStack(ModItems.HONEY_TREAT.get()))))), false);
//        }
//    }
//
//    public static class EmptyPollinateGoal extends BeePollinateGoal
//    {
//        @Override
//        public boolean canBeeUse() {
//            return false;
//        }
//    }
//
//    public static class EmptyFindFlowerGoal extends BeeGoToKnownFlowerGoal
//    {
//        @Override
//        public boolean canBeeUse() {
//            return false;
//        }
//    }
}
