package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.FarmerBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBee;
import cy.jdkdigital.productivebees.compat.harvest.HarvestCompatHandler;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class AdvancedBeehiveBlockEntityAbstract extends BeehiveBlockEntity
{
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList( // TODO use vanilla list
            "AbsorptionAmount", "Attributes", "CitadelData", "KubeJSPersistentData",
            "Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "InLove",
            "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded",
            "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks",
            "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID"
    );
    private static final List<String> OPTIONAL_IGNORED_BEE_TAGS = Arrays.asList(
            "ForgeCaps", "ForgeData"
    );
    public int MAX_BEES = 3;
    private BlockEntityType<?> tileEntityType;

    protected int tickCounter = 0;

    public AdvancedBeehiveBlockEntityAbstract(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(pos, state);
        this.tileEntityType = tileEntityType;
    }

    @Nonnull
    @Override
    public BlockEntityType<?> getType() {
        return this.tileEntityType == null ? super.getType() : this.tileEntityType;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedBeehiveBlockEntityAbstract blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            tickBees(serverLevel, pos, state, blockEntity);
            blockEntity.tickCounter = 0;
        }

        // Play hive buzz sound
        if (level.getRandom().nextDouble() < 0.005D) {
            if (!blockEntity.isEmpty()) {
                double x = (double) pos.getX() + 0.5D;
                double y = (double) pos.getY();
                double z = (double) pos.getZ() + 0.5D;
                level.playSound(null, x, y, z, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    private static void tickBees(ServerLevel pLevel, BlockPos pPos, BlockState pState, AdvancedBeehiveBlockEntityAbstract blockEntity) {
        List<ProductiveBeeData> beesToKeep = new ArrayList<>();

        List<BeehiveBlockEntity.BeeData> pData = blockEntity.stored;
        boolean hasReleased = false;
        ListIterator<BeeData> iterator = pData.listIterator();

        while (iterator.hasNext()) {
            BeehiveBlockEntity.BeeData beedata = iterator.next();
            if (beedata.tick()) {
                BeehiveBlockEntity.BeeReleaseStatus beeReleaseStatus = BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                var hasConverted = beedata.occupant.entityData().getUnsafe().getBoolean("HasConverted");
                if (!hasConverted && beedata.hasNectar()) {
                    beeReleaseStatus = BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED;
                }

                if (blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity && advancedBeehiveBlockEntity.isSim()) {
                    // Sim hive
                    var inhabitant = beedata.toOccupant();
                    var entityData = inhabitant.entityData();
                    int ticksInHive = inhabitant.ticksInHive();
                    int minOccupationTicks = inhabitant.minTicksInHive();
                    // for simulated hives, count all the way up to timeInHive + pollinationTime
                    if (ticksInHive > (minOccupationTicks + 450)) {
                        Entity simulatedBee = simulateBee(pLevel, pPos, pState, blockEntity, inhabitant);
                        hasReleased = true;
                        ticksInHive = 0;

                        minOccupationTicks = blockEntity.getTimeInHive(beeReleaseStatus.equals(BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED), inhabitant);

                        // update bee data
                        CompoundTag compoundNBT = new CompoundTag();
                        simulatedBee.save(compoundNBT);
                        entityData = CustomData.of(compoundNBT);
                    } else if (willLeaveHive(pLevel, inhabitant, beeReleaseStatus)){
                        // only add count if outside is favourable
                        ticksInHive += blockEntity.tickCounter;
                    }
                    var newInhabitant = new BeehiveBlockEntity.Occupant(entityData, ticksInHive, minOccupationTicks);
                    iterator.set(new ProductiveBeeData(newInhabitant));
                } else if (releaseOccupant(pLevel, pPos, pState, beedata.toOccupant(), blockEntity, beeReleaseStatus)) {
                    hasReleased = true;
                    iterator.remove();
                }
            }
        }

        if (hasReleased) {
            setChanged(pLevel, pPos, pState);
        }

        // old code
//        final var currentInhabitants = new CopyOnWriteArrayList<>(blockEntity.beeHandler.getInhabitants());
//        // worst-case size
//        final var inhabitantsToRemove = new ArrayList<BeehiveBlockEntity.Occupant>(currentInhabitants.size());
//        boolean hasReleased = false;
//        for (BeehiveBlockEntity.BeeData beeData : this.stored) {
//            BeehiveBlockEntity.Occupant inhabitant = beeData.toOccupant();
//            if (inhabitant.ticksInHive() > inhabitant.minTicksInHive()) {
//                BeehiveBlockEntity.BeeReleaseStatus beeState = inhabitant.nbt.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
//                if (inhabitant.nbt.contains("HasConverted") && inhabitant.nbt.getBoolean("HasConverted")) {
//                    beeState = BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
//                }
//                if (blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity && advancedBeehiveBlockEntity.isSim()) {
//                    // for simulated hives, count all the way up to timeInHive + pollinationTime
//                    if (inhabitant.ticksInHive() > (inhabitant.minTicksInHive() + 450)) {
//                        simulateBee(pLevel, pPos, pState, blockEntity, inhabitant);
//                        hasReleased = true;
//                    } else if (willLeaveHive(pLevel, inhabitant.nbt, beeState)){
//                        // only add count if outside is favourable
//                        inhabitant.ticksInHive() += blockEntity.tickCounter;
//                    }
//                } else if (releaseBee(pLevel, pPos, pState, blockEntity, inhabitant.nbt, null, beeState)) {
//                    hasReleased = true;
//                    inhabitantsToRemove.add(inhabitant);
//                }
//            } else {
//                inhabitant.ticksInHive += blockEntity.tickCounter;
//            }
//        }
//        if (hasReleased) {
//            currentInhabitants.removeAll(inhabitantsToRemove);
//            h.setInhabitants(new ArrayList<>(currentInhabitants));
//            blockEntity.setNonSuperChanged();
//        }
    }

    protected int getTimeInHive(boolean hasNectar, @Nullable Occupant occupant) {
        if (occupant != null) {
            var data = occupant.entityData().getUnsafe();
            if (data.getString("id").equals("productivebees:hoarder_bee") || data.getString("id").equals("productivebees:collector_bee")) {
                return 100;
            }
        }
        return hasNectar ? ProductiveBeesConfig.GENERAL.timeInHive.get() : ProductiveBeesConfig.GENERAL.timeInHive.get() / 2;
    }

    @Override
    public void emptyAllLivingFromHive(@Nullable Player pPlayer, BlockState pState, BeehiveBlockEntity.BeeReleaseStatus pReleaseStatus) {
        List<Entity> list = this.releaseAllOccupants(pState, pReleaseStatus);
        if (pPlayer != null && getLevel() != null) {
            for (Entity entity : list) {
                if (entity instanceof Bee bee && pPlayer.position().distanceToSqr(entity.position()) <= 16.0) {
                    if (!this.isSedated()) {
                        if (bee instanceof ProductiveBee pBee) {
                            GeneValue temper = pBee.getAttributeValue(GeneAttribute.TEMPER);
                            if (temper.equals(GeneValue.TEMPER_PASSIVE) || (temper.equals(GeneValue.TEMPER_NORMAL) && getLevel().random.nextFloat() < .5)) {
                                bee.setStayOutOfHiveCountdown(400);
                                continue;
                            }
                        }
                        bee.setTarget(pPlayer);
                    } else {
                        bee.setStayOutOfHiveCountdown(400);
                    }
                }
            }
        }
    }

    @Override
    public boolean isFull() {
        return this.getOccupantCount() == MAX_BEES;
    }

    public boolean acceptsBee(Bee bee) {
        return true;
    }

    public void addOccupantFromTag(CompoundTag compoundtag, int ticksInHive, int timeInHive) {
        BeehiveBlockEntity.IGNORED_BEE_TAGS.forEach(compoundtag::remove);
        this.storeBee(new BeehiveBlockEntity.Occupant(CustomData.of(compoundtag), ticksInHive, timeInHive));
        this.setNonSuperChanged();
    }

    public void addOccupant(Entity pOccupant) {
        if (!this.isFull()) {
            pOccupant.stopRiding();
            pOccupant.ejectPassengers();
            pOccupant.getData(ProductiveBees.ATTRIBUTE_HANDLER); // Initialize attributes
            this.storeBee(BeehiveBlockEntity.Occupant.of(pOccupant));
            if (this.level != null) {
                if (pOccupant instanceof Bee bee && bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                    this.savedFlowerPos = bee.getSavedFlowerPos();
                }

                BlockPos blockpos = this.getBlockPos();
                this.level.playSound(null, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(pOccupant, this.getBlockState()));
            }

            pOccupant.discard();
            super.setChanged();
        }
    }

    public static Entity simulateBee(ServerLevel pLevel, BlockPos pPos, BlockState state, AdvancedBeehiveBlockEntityAbstract blockEntity, Occupant inhabitant) {
        Entity entity = inhabitant.createEntity(pLevel, pPos);
        if (entity instanceof Bee beeEntity && blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
            var data = inhabitant.entityData().getUnsafe();
            beeEntity.setHivePos(pPos);
            BeehiveBlockEntity.BeeReleaseStatus beeState = BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (data.getString("id").equals("productivebees:farmer_bee")) {
                List<BlockPos> harvestablesNearby = FarmerBee.findHarvestablesNearby(pLevel, pPos, 5 + advancedBeehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get()));
                harvestablesNearby.forEach(pos -> {
                    if (pos != null && pLevel.isLoaded(pos) && HarvestCompatHandler.isCropValid(pLevel, pos)) {
                        HarvestCompatHandler.harvestBlock(pLevel, pos);
                    }
                });
            } else if (data.getString("id").equals("productivebees:hoarder_bee") || data.getString("id").equals("productivebees:collector_bee")) {
                int distance = 5 + advancedBeehiveBlockEntity.getUpgradeCount(ModItems.UPGRADE_RANGE.get());
                List<ItemEntity> items = pLevel.getEntitiesOfClass(ItemEntity.class, (new AABB(pPos).inflate(distance, distance, distance)));
                for (ItemEntity item: items) {
                    if (advancedBeehiveBlockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler inv) {
                        var leftOver = inv.addOutput(item.getItem().copy());
                        if (leftOver.isEmpty()) {
                            item.kill();
                        } else {
                            item.setItem(leftOver);
                        }
                    }
                }
            } else {
                // state depends on whether the outside is having a valid flower block
                Direction direction = state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : state.getValue(BeehiveBlock.FACING);
                BlockPos flowerPos = pPos.below(state.getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.DOWN) ? 2 : 1).relative(direction);
                beeEntity.setSavedFlowerPos(flowerPos);
                if (pLevel.isLoaded(flowerPos)) {
                    if (beeEntity instanceof ProductiveBee pBee && pBee.isFlowerValid(flowerPos)) {
                        beeState = BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED;
                        pBee.internalSetHasNectar(true);
                        pBee.postPollinate();
                    } else if (!(beeEntity instanceof ProductiveBee)) {
                        BlockState flowerBlock = pLevel.getBlockState(flowerPos);
                        if (beeEntity.isFlowerValid(flowerPos) || flowerBlock.getBlock() instanceof Feeder && ProductiveBee.isValidFeeder(beeEntity, pLevel.getBlockEntity(flowerPos), blockState -> blockState.is(BlockTags.FLOWERS), null)) {
                            beeState = BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED;
                        }
                    }
                }
            }
            blockEntity.beeReleasePostAction(pLevel, beeEntity, state, beeState);
        }
        return entity;
    }

    public static boolean releaseOccupant(ServerLevel pLevel, BlockPos pPos, BlockState pState, BeehiveBlockEntity.Occupant pOccupant, AdvancedBeehiveBlockEntityAbstract blockEntity, BeehiveBlockEntity.BeeReleaseStatus pReleaseStatus) {
        if (pState.getBlock().equals(Blocks.AIR) || pLevel == null) {
            return false;
        }

        Direction direction = pState.hasProperty(BlockStateProperties.FACING) ? pState.getValue(BlockStateProperties.FACING) : pState.getValue(BeehiveBlock.FACING);
        BlockPos frontPos = pPos.relative(direction);

        if (willLeaveHive(pLevel, pOccupant, pReleaseStatus)) {
            boolean isPositionBlocked = !pLevel.getBlockState(frontPos).getCollisionShape(pLevel, frontPos).isEmpty();
            if (!isPositionBlocked || pReleaseStatus == BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
                Entity entity = pOccupant.createEntity(pLevel, pPos);
                if (entity != null) {
                    boolean spawned;
                    if (entity instanceof Bee bee) {
                        if (blockEntity.savedFlowerPos != null && !bee.hasSavedFlowerPos() && (bee.getEncodeId().contains("dye_bee") || pLevel.random.nextFloat() <= 0.9F)) {
                            bee.setSavedFlowerPos(blockEntity.savedFlowerPos);
                        }

                        if (pReleaseStatus == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                            bee.dropOffNectar();
                            if (pState.is(BlockTags.BEEHIVES, p_202037_ -> p_202037_.hasProperty(BeehiveBlock.HONEY_LEVEL))) {
                                int i = getHoneyLevel(pState);
                                if (i < 5) {
                                    int j = pLevel.random.nextInt(100) == 0 ? 2 : 1;
                                    if (i + j > 5) {
                                        j--;
                                    }

                                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BeehiveBlock.HONEY_LEVEL, Integer.valueOf(i + j)));
                                }
                            }
                        }

                        float f = entity.getBbWidth();
                        double d3 = isPositionBlocked ? 0.0 : 0.55 + (double)(f / 2.0F);
                        double d0 = (double)pPos.getX() + 0.5 + d3 * (double)direction.getStepX();
                        double d1 = (double)pPos.getY() + 0.5 - (double)(entity.getBbHeight() / 2.0F);
                        double d2 = (double)pPos.getZ() + 0.5 + d3 * (double)direction.getStepZ();
                        entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());
                    }

                    pLevel.playSound(null, pPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                    pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(entity, pLevel.getBlockState(pPos)));
                    spawned = pLevel.addFreshEntity(entity);
                    if (spawned && entity instanceof Bee bee) {
                        blockEntity.beeReleasePostAction(pLevel, bee, pState, pReleaseStatus);
                    }
                    return spawned;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean willLeaveHive(ServerLevel level, BeehiveBlockEntity.Occupant occupant, BeehiveBlockEntity.BeeReleaseStatus beeState) {
        CompoundTag tag = occupant.entityData().getUnsafe();
        boolean willLeaveHive = beeState == BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY || level.dimensionType().hasFixedTime(); // in an emergency or dim without time
        if (!level.dimensionType().hasFixedTime()) { // Weather and day/night cycle only counts in dim with time
            willLeaveHive = willLeaveHive ||
                    ((!level.isNight() && tag.getInt("bee_behavior") != 1) || // it's day and the bee is not nocturnal
                    (level.isNight() && tag.getInt("bee_behavior") != 0)) && // it's night and the bee is not diurnal
                    (!level.isRaining() || tag.getInt("bee_weather_tolerance") > 0); // it's not raining or the bee is tolerant
        }
        return willLeaveHive;
    }

    protected void beeReleasePostAction(Level level, Bee beeEntity, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState) {
        beeEntity.setHealth(beeEntity.getMaxHealth());

        if (NeoForge.EVENT_BUS.post(new BeeReleaseEvent(level, beeEntity, this, state, beeState)).isCanceled()) {
            return;
        }

        beeEntity.resetTicksWithoutNectarSinceExitingHive();

        // TODO this should only be done for sim bees because Occupant.setBeeReleaseData
//        applyHiveTime(getTimeInHive(beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED, beeEntity), beeEntity);
        beeEntity.dropOffNectar();

        if (beeEntity instanceof ProductiveBee pBee && pBee.hasConverted()) {
            pBee.setHasConverted(false);
            pBee.setSavedFlowerPos(null);
            return;
        }

        // Hoarder bees should leave their inventory behind
        if (beeEntity instanceof HoarderBee) {
            if (((HoarderBee) beeEntity).holdsItem()) {
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos(), null);
                if (handler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler inv) {
                    ((HoarderBee) beeEntity).emptyIntoInventory(inv);
                }
            }
        }

        // Deliver honey on the way out
        if (beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
            if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                int honeyLevel = getHoneyLevel(state);
                int maxHoneyLevel = getMaxHoneyLevel(state);
                if (honeyLevel < maxHoneyLevel) {
                    int levelIncrease = level.random.nextInt(100) == 0 ? 2 : 1;
                    if (honeyLevel + levelIncrease > maxHoneyLevel) {
                        --levelIncrease;
                    }
                    level.setBlockAndUpdate(worldPosition, state.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel + levelIncrease));
                }
            }
        }
    }

    private static void applyHiveTime(int ticksInHive, Bee beeEntity) {
        int i = beeEntity.getAge();
        if (i < 0) {
            beeEntity.setAge(Math.min(0, i + ticksInHive));
        } else if (i > 0) {
            beeEntity.setAge(Math.max(0, i - ticksInHive));
        }

        beeEntity.resetLove();
    }

    public static int getMaxHoneyLevel(BlockState state) {
        Block block = state.getBlock();
        return block instanceof AdvancedBeehiveAbstract ? ((AdvancedBeehiveAbstract) block).getMaxHoneyLevel() : 5;
    }

    public static void removeIgnoredTags(CompoundTag tag) {
        for (String s : BeehiveBlockEntity.IGNORED_BEE_TAGS) {
            if (tag.contains(s)) {
                tag.remove(s);
            }
        }
        for (String s : IGNORED_BEE_TAGS) {
            if (tag.contains(s)) {
                tag.remove(s);
            }
        }
    }

    public static boolean spawnBeeInWorldAtPosition(ServerLevel world, Entity entity, BlockPos pos, Direction direction, @Nullable Integer age) {
        BlockPos offset = pos.relative(direction);
        boolean isPositionBlocked = !world.getBlockState(offset).getCollisionShape(world, offset).isEmpty();
        float f = entity.getBbWidth();
        double d3 = isPositionBlocked ? 0.0D : 0.55D + (double) (f / 2.0F);
        double d0 = (double) pos.getX() + 0.5D + d3 * (double) direction.getStepX();
        double d1 = (double) pos.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
        double d2 = (double) pos.getZ() + 0.5D + d3 * (double) direction.getStepZ();
        entity.moveTo(d0, d1, d2, entity.getYRot(), entity.getXRot());

        if (age != null && entity instanceof Bee) {
            ((Bee) entity).setAge(age);
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        return world.addFreshEntity(entity);
    }

    // sets changed just like in BlockEntity::setChanged to skip BeehiveBlockEntity::setChanged
    public void setNonSuperChanged() {
        if (this.level != null) {
            setChanged(this.level, this.worldPosition, this.getBlockState());
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        this.loadPacketNBT(tag, provider);
        super.loadAdditional(tag, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        this.savePacketNBT(tag, provider);
    }

    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt("tickCounter", tickCounter);
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        tickCounter = tag.contains("tickCounter") ? tag.getInt("tickCounter") : 0;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithId(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
        this.loadPacketNBT(pkt.getTag(), provider);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public static class ProductiveBeeData extends BeeData
    {
        private BeeData beeData;

        private ProductiveBeeData(BeehiveBlockEntity.Occupant pOccupant) {
            super(pOccupant);
        }

        public static ProductiveBeeData fromBeeData(BeeData beeData) {
            var data = new ProductiveBeeData(beeData.toOccupant());
            data.beeData = beeData;
            return data;
        }

        public BeeData getBeeData() {
            return beeData;
        }
    }
}
