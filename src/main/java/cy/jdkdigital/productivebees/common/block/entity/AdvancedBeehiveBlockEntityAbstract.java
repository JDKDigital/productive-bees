package cy.jdkdigital.productivebees.common.block.entity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBee;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.handler.bee.InhabitantStorage;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AdvancedBeehiveBlockEntityAbstract extends BeehiveBlockEntity
{
    private static final List<String> IGNORED_BEE_TAGS = Arrays.asList(
        "ForgeCaps", "ForgeData", "AbsorptionAmount", "Attributes", "CitadelData", "KubeJSPersistentData",
        "Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "InLove",
        "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded",
        "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterHiveTicks",
        "TicksSincePollination", "CropsGrownSincePollination", "HivePos", "Passengers", "Leash", "UUID"
    );
    public int MAX_BEES = 3;
    private LazyOptional<IInhabitantStorage> beeHandler = LazyOptional.of(InhabitantStorage::new);
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
        if (level instanceof ServerLevel serverLevel && blockEntity.tickCounter++ % 100 == 0) {
            tickBees(serverLevel, pos, state, blockEntity);
            blockEntity.tickCounter = 0;
        }

        // Play hive buzz sound
        if (level.getRandom().nextDouble() < 0.005D) {
            blockEntity.beeHandler.ifPresent(h -> {
                if (h.getInhabitants().size() > 0) {
                    double x = (double) pos.getX() + 0.5D;
                    double y = (double) pos.getY();
                    double z = (double) pos.getZ() + 0.5D;
                    level.playSound(null, x, y, z, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            });
        }
    }

    private static void tickBees(ServerLevel level, BlockPos hivePos, BlockState state, AdvancedBeehiveBlockEntityAbstract blockEntity) {
        blockEntity.beeHandler.ifPresent(h -> {
            Iterator<AdvancedBeehiveBlockEntityAbstract.Inhabitant> inhabitantIterator = h.getInhabitants().iterator();
            boolean hasReleased = false;
            while (inhabitantIterator.hasNext()) {
                AdvancedBeehiveBlockEntityAbstract.Inhabitant inhabitant = inhabitantIterator.next();
                if (inhabitant.ticksInHive > inhabitant.minOccupationTicks) {
                    BeehiveBlockEntity.BeeReleaseStatus beeState = inhabitant.nbt.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
                    if (AdvancedBeehiveBlockEntityAbstract.releaseBee(level, hivePos, state, blockEntity, inhabitant.nbt, null, beeState)) {
                        hasReleased = true;
                        inhabitantIterator.remove();
                    }
                } else {
                    inhabitant.ticksInHive += blockEntity.tickCounter;
                }
            }
            if (hasReleased) {
                blockEntity.setNonSuperChanged();
            }
        });
    }

    protected int getTimeInHive(boolean hasNectar, @Nullable Bee beeEntity) {
        if (beeEntity instanceof ProductiveBee) {
            return ((ProductiveBee) beeEntity).getTimeInHive(hasNectar);
        }
        return hasNectar ? ProductiveBeesConfig.GENERAL.timeInHive.get() : ProductiveBeesConfig.GENERAL.timeInHive.get() / 2;
    }

    @Override
    public void emptyAllLivingFromHive(@Nullable Player player, BlockState blockState, BeehiveBlockEntity.BeeReleaseStatus beeState) {
        if (level instanceof ServerLevel serverLevel) {
            List<Entity> releasedBees = Lists.newArrayList();
            beeHandler.ifPresent(h -> {
                h.getInhabitants().removeIf((tag) -> AdvancedBeehiveBlockEntityAbstract.releaseBee(serverLevel, getBlockPos(), blockState, this, tag.nbt, releasedBees, beeState));
            });
            if (player != null) {
                for (Entity entity : releasedBees) {
                    if (entity instanceof Bee beeEntity) {
                        if (player.blockPosition().distSqr(entity.blockPosition()) <= 16.0D) {
                            if (!this.isSedated()) {
                                // Check temper
                                if (beeEntity instanceof ProductiveBee) {
                                    int temper = ((ProductiveBee) beeEntity).getAttributeValue(BeeAttributes.TEMPER);
                                    if (temper == 0 || (temper == 1 && level.random.nextFloat() < .5)) {
                                        beeEntity.setStayOutOfHiveCountdown(400);
                                        continue;
                                    }
                                }
                                beeEntity.setTarget(player);
                            } else {
                                beeEntity.setStayOutOfHiveCountdown(400);
                            }
                        }
                    }
                }
            }
            if (!releasedBees.isEmpty()) {
                setNonSuperChanged();
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return this.getBeeList().isEmpty();
    }

    @Override
    public int getOccupantCount() {
        return this.getBeeList().size();
    }

    @Override
    public boolean isFull() {
        return this.getOccupantCount() == MAX_BEES;
    }

    public boolean acceptsBee(Bee bee) {
        return true;
    }

    @Override
    public void addOccupant(@Nonnull Entity beeEntity, boolean hasNectar) {
        this.addOccupantWithPresetTicks(beeEntity, hasNectar, 0);
    }

    @Override
    public void addOccupantWithPresetTicks(Entity entity, boolean hasNectar, int ticksInHive) {
        if (entity instanceof Bee && acceptsBee((Bee) entity)) {
            beeHandler.ifPresent(h -> {
                if (h.getInhabitants().size() < MAX_BEES) {
                    entity.stopRiding();
                    entity.ejectPassengers();
                    CompoundTag compoundNBT = new CompoundTag();
                    entity.save(compoundNBT);

                    Bee beeEntity = (Bee) entity;

                    addBee(compoundNBT, ticksInHive, this.getTimeInHive(hasNectar, beeEntity), ((Bee) entity).getSavedFlowerPos(), entity.getName().getString());
                    if (beeEntity.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || (this.level != null && level.random.nextBoolean()))) {
                        this.savedFlowerPos = beeEntity.getSavedFlowerPos();
                    }

                    if (this.level != null) {
                        BlockPos pos = this.getBlockPos();
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    entity.discard();
                }
            });
        }
    }

    public void addBee(CompoundTag tag, int ticksInHive, int timeInHive, BlockPos flowerPos, String name) {
        beeHandler.ifPresent(h -> {
            h.addInhabitant(new Inhabitant(tag, ticksInHive, timeInHive, flowerPos, name));
        });
        this.setNonSuperChanged();
    }

    public static boolean releaseBee(ServerLevel level, BlockPos hivePos, BlockState state, AdvancedBeehiveBlockEntityAbstract blockEntity, CompoundTag tag, @Nullable List<Entity> releasedBees, BeehiveBlockEntity.BeeReleaseStatus beeState) {
        if (state.getBlock().equals(Blocks.AIR) || level == null) {
            return false;
        }

        boolean stayInside =
            beeState != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY &&
            level.canSleepThroughNights() &&
            (
                (level.isNight() && tag.getInt("bee_behavior") == 0) || // it's night and the bee is diurnal
                (!level.isNight() && tag.getInt("bee_behavior") == 1) || // it's day and the bee is nocturnal
                (level.isRainingAt(hivePos) && tag.getInt("bee_weather_tolerance") == 0) // it's raining and the bee is not tolerant
            );

        if (!stayInside) {
            Direction direction = state.hasProperty(BlockStateProperties.FACING) ? state.getValue(BlockStateProperties.FACING) : state.getValue(BeehiveBlock.FACING);
            BlockPos frontPos = hivePos.relative(direction);
            boolean isPositionBlocked = !level.getBlockState(frontPos).getCollisionShape(level, frontPos).isEmpty();
            if (!isPositionBlocked || beeState == BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
                // Spawn entity
                boolean spawned = false;
                Bee beeEntity = (Bee) EntityType.loadEntityRecursive(tag, level, (spawnedEntity) -> spawnedEntity);
                if (beeEntity != null) {
                    // Hoarder bees should leave their inventory behind
                    AtomicBoolean hasOffloaded = new AtomicBoolean(true);
                    if (beeEntity instanceof HoarderBee) {
                        if (((HoarderBee) beeEntity).holdsItem()) {
                            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                                ((HoarderBee) beeEntity).emptyIntoInventory(((InventoryHandlerHelper.ItemHandler) inv));

                                if (!((HoarderBee) beeEntity).isInventoryEmpty()) {
                                    hasOffloaded.set(false);
                                }
                            });
                        }
                    }

                    spawned = spawnBeeInWorldAtPosition(level, beeEntity, hivePos, direction, null);
                    if (spawned && hasOffloaded.get()) {
                        if (blockEntity.savedFlowerPos != null && !beeEntity.hasSavedFlowerPos() && (beeEntity.getEncodeId().contains("dye_bee") || level.random.nextFloat() <= 0.9F)) {
                            beeEntity.setSavedFlowerPos(blockEntity.savedFlowerPos);
                        }
                        beeEntity.hivePos = hivePos;

                        blockEntity.beeReleasePostAction(level, beeEntity, state, beeState);

                        if (releasedBees != null) {
                            releasedBees.add(beeEntity);
                        }
                    }
                }

                return spawned;
            }
            return false;
        }
        return false;
    }

    protected void beeReleasePostAction(Level level, Bee beeEntity, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState) {
        beeEntity.resetTicksWithoutNectarSinceExitingHive();
        beeEntity.heal(2);

        applyHiveTime(getTimeInHive(beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED, beeEntity), beeEntity);
        beeEntity.dropOffNectar();

        if (beeEntity instanceof ProductiveBee && ((ProductiveBee) beeEntity).hasConverted()) {
            return;
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

        beeEntity.resetTicksWithoutNectarSinceExitingHive();
    }

    private boolean hasSavedFlowerPos() {
        return this.savedFlowerPos != null;
    }

    public static int getMaxHoneyLevel(BlockState state) {
        Block block = state.getBlock();
        return block instanceof AdvancedBeehiveAbstract ? ((AdvancedBeehiveAbstract) block).getMaxHoneyLevel() : 5;
    }

    public static void removeIgnoredBeeTags(CompoundTag tag) {
        for(String s : IGNORED_BEE_TAGS) {
            if (tag.contains(s)) {
                tag.remove(s);
            }
        }
    }

    public static boolean spawnBeeInWorldAtPosition(ServerLevel world, Entity entity, BlockPos pos, Direction direction, @Nullable Integer age) {
        BlockPos offset = pos.relative(direction);
        boolean isPositionBlocked = !world.getBlockState(offset).getCollisionShape(world, offset).isEmpty();
        float width = entity.getBbWidth();
        double spawnOffset = isPositionBlocked ? 0.0D : 0.55D + (double) (width / 2.0F);
        double x = (double) pos.getX() + 0.5D + spawnOffset * (double) direction.getStepX();
        double y = (double) pos.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
        double z = (double) pos.getZ() + 0.5D + spawnOffset * (double) direction.getStepZ();
        entity.moveTo(x, y, z, entity.getYRot(), entity.getXRot());
        if (age != null && entity instanceof Bee) {
            ((Bee) entity).setAge(age);
        }

        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
        return world.addFreshEntity(entity);
    }

    public List<Inhabitant> getBeeList() {
        return this.getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitants).orElse(new ArrayList<>());
    }

    public static class Inhabitant
    {
        public final CompoundTag nbt;
        public int ticksInHive;
        public final int minOccupationTicks;
        public final BlockPos flowerPos;
        public final String localizedName;

        public Inhabitant(CompoundTag nbt, int ticksInHive, int minOccupationTicks, BlockPos flowerPos, String localizedName) {
            AdvancedBeehiveBlockEntityAbstract.removeIgnoredBeeTags(nbt);
            this.nbt = nbt;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
            this.flowerPos = flowerPos;
            this.localizedName = localizedName;
        }

        @Override
        public String toString() {
            return "Bee{" +
                    "ticksInHive=" + ticksInHive +
                    "flowerPos=" + flowerPos +
                    ", minOccupationTicks=" + minOccupationTicks +
                    ", nbt=" + nbt +
                    '}';
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityBee.BEE) {
            return beeHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    // sets changed just like in BlockEntity::setChanged to skip BeehiveBlockEntity::setChanged
    public void setNonSuperChanged() {
        if (this.level != null) {
            setChanged(this.level, this.worldPosition, this.getBlockState());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        this.loadPacketNBT(tag);
        tag.remove("Bees"); // remove vanilla bee data
        super.load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.savePacketNBT(tag);
    }

    public void savePacketNBT(CompoundTag tag) {
        beeHandler.ifPresent(h -> {
            tag.remove("Bees"); // remove vanilla bee data
            CompoundTag compound = ((INBTSerializable<CompoundTag>) h).serializeNBT();
            tag.put("BeeList", compound);
        });
    }

    public void loadPacketNBT(CompoundTag tag) {
        beeHandler.ifPresent(h -> ((INBTSerializable<CompoundTag>) h).deserializeNBT(tag.getCompound(tag.contains("BeeList") ? "BeeList" : "Bees")));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithId();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.loadPacketNBT(pkt.getTag());
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }
}