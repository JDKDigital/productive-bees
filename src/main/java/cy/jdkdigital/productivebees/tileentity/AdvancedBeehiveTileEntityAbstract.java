package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.hive.HoarderBeeEntity;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.handler.bee.InhabitantStorage;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AdvancedBeehiveTileEntityAbstract extends BeehiveTileEntity
{
    public int MAX_BEES = 3;
    private LazyOptional<IInhabitantStorage> beeHandler = LazyOptional.of(this::createBeeHandler);
    private TileEntityType<?> tileEntityType;

    private int tickCounter = 0;

    public AdvancedBeehiveTileEntityAbstract(TileEntityType<?> tileEntityType) {
        super();
        this.tileEntityType = tileEntityType;
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return this.tileEntityType == null ? super.getType() : this.tileEntityType;
    }

    public void tick() {
        if (!this.world.isRemote) {

            if (tickCounter++ % 100 == 0) {
                this.tickBees();
                tickCounter = 0;
            }

            // Play hive buzz sound
            beeHandler.ifPresent(h -> {
                if (h.getInhabitants().size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
                    BlockPos pos = this.getPos();
                    double x = (double) pos.getX() + 0.5D;
                    double y = (double) pos.getY();
                    double z = (double) pos.getZ() + 0.5D;
                    this.world.playSound(null, x, y, z, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            });
        }
    }

    private void tickBees() {
        beeHandler.ifPresent(h -> {
            h.getInhabitants().removeIf((inhabitant) -> {
                if (inhabitant.ticksInHive > inhabitant.minOccupationTicks) {
                    BeehiveTileEntity.State beeState = inhabitant.nbt.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
                    return this.releaseBee(this.getBlockState(), inhabitant.nbt, null, beeState);
                }
                else {
                    inhabitant.ticksInHive += tickCounter;
                }
                return false;
            });
        });
    }

    protected int getTimeInHive(boolean hasNectar, @Nullable BeeEntity beeEntity) {
        if (beeEntity instanceof HoarderBeeEntity) {
            return 100;
        }
        return hasNectar ? 2400 : 600;
    }

    public void markDirty() {
        if (this.world != null) {
            if (this.isNearFire()) {
                this.angerBees(null, this.world.getBlockState(this.getPos()), BeehiveTileEntity.State.EMERGENCY);
            }

            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
        }

        super.markDirty();
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState blockState, BeehiveTileEntity.State beeState) {
        List<Entity> releasedBees = Lists.newArrayList();
        beeHandler.ifPresent(h -> {
            h.getInhabitants().removeIf((tag) -> this.releaseBee(blockState, tag.nbt, releasedBees, beeState));
        });
        if (player != null) {
            Iterator entityIterator = releasedBees.iterator();

            while (entityIterator.hasNext()) {
                Entity entity = (Entity) entityIterator.next();
                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    if (player.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 16.0D) {
                        if (!this.isSmoked()) {
                            beeEntity.setBeeAttacker(player);
                        }
                        else {
                            beeEntity.setStayOutOfHiveCountdown(400);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasNoBees() {
        return this.getBeeList().isEmpty();
    }

    @Override
    public int getBeeCount() {
        return this.getBeeList().size();
    }

    @Override
    public boolean isFullOfBees() {
        return this.getBeeCount() == MAX_BEES;
    }

    @Override
    public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
        beeHandler.ifPresent(h -> {
            if (h.getInhabitants().size() < MAX_BEES) {
                entity.stopRiding();
                entity.removePassengers();
                CompoundNBT compoundNBT = new CompoundNBT();
                entity.writeUnlessPassenger(compoundNBT);

                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    if (beeEntity instanceof SolitaryBeeEntity) {
                        ((SolitaryBeeEntity) beeEntity).hasHadNest = true;
                    }

                    h.addInhabitant(new Inhabitant(compoundNBT, ticksInHive, this.getTimeInHive(hasNectar, beeEntity), ((BeeEntity) entity).getFlowerPos(), entity.getName().getFormattedText()));
                    if (beeEntity.hasFlower() && (!this.hasFlowerPos() || (this.world != null && this.world.rand.nextBoolean()))) {
                        this.flowerPos = beeEntity.getFlowerPos();
                    }
                }

                if (this.world != null) {
                    BlockPos pos = this.getPos();
                    this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                entity.remove();
            }
        });
    }

    public void tryEnterHive(Entity beeEntity, boolean hasNectar) {
        this.tryEnterHive(beeEntity, hasNectar, 0);
    }

    public boolean releaseBee(BlockState state, CompoundNBT tag, @Nullable List<Entity> releasedBees, BeehiveTileEntity.State beeState) {
        boolean stayInside =
                this.world.dimension.isSurfaceWorld() &&
                        (this.world.isNightTime() && tag.getInt("bee_behavior") == 0) || // it's night and the bee is diurnal
                        (this.world.isRaining() && (beeState != BeehiveTileEntity.State.EMERGENCY || tag.getInt("bee_weather_tolerance") == 0)); // it's raining and the bees is not tolerant

        if (!this.world.isNightTime() && !this.world.isRaining() && stayInside) {
            ProductiveBees.LOGGER.debug("Bee is staying inside during the day: " + tag);
        }

        if (!stayInside) {
            BlockPos pos = this.getPos();
            tag.remove("Passengers");
            tag.remove("Leash");
            tag.removeUniqueId("UUID");
            Direction direction = state.has(BlockStateProperties.FACING) ? state.get(BlockStateProperties.FACING) : state.get(BeehiveBlock.FACING);
            BlockPos offset = pos.offset(direction);
            boolean isPositionBlocked = !this.world.getBlockState(offset).getCollisionShape(this.world, offset).isEmpty();
            if (!isPositionBlocked || beeState == BeehiveTileEntity.State.EMERGENCY) {
                // Spawn entity
                boolean spawned = false;
                BeeEntity beeEntity = (BeeEntity) EntityType.loadEntityAndExecute(tag, this.world, (spawnedEntity) -> spawnedEntity);
                if (beeEntity != null) {
                    // Hoarder bees should leave their item behind
                    AtomicBoolean hasOffloaded = new AtomicBoolean(true);
                    if (beeEntity instanceof HoarderBeeEntity) {
                        if (((HoarderBeeEntity) beeEntity).holdsItem()) {
                            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                                if (((InventoryHandlerHelper.ItemHandler) inv).addOutput(((HoarderBeeEntity) beeEntity).getItem())) {
                                    ((HoarderBeeEntity) beeEntity).clearInventory();
                                } else {
                                    hasOffloaded.set(false);
                                }
                            });
                        }
                    }

                    spawned = spawnBeeInWorldAPosition(this.world, beeEntity, pos, direction, null);
                    if (spawned && hasOffloaded.get()) {
                        if (this.hasFlowerPos() && !beeEntity.hasFlower() && this.world.rand.nextFloat() <= 0.9F) {
                            beeEntity.setFlowerPos(this.flowerPos);
                        }
                        beeReleasePostAction(beeEntity, state, beeState);

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

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        beeEntity.resetTicksWithoutNectar();

        // Deliver honey on the way out
        if (beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            beeEntity.onHoneyDelivered();
            if (state.has(BeehiveBlock.HONEY_LEVEL)) {
                int honeyLevel = getHoneyLevel(state);
                int maxHoneyLevel = getMaxHoneyLevel(state);
                if (honeyLevel < maxHoneyLevel) {
                    int levelIncrease = this.world.rand.nextInt(100) == 0 ? 2 : 1;
                    if (honeyLevel + levelIncrease > maxHoneyLevel) {
                        --levelIncrease;
                    }

                    this.world.setBlockState(pos, state.with(BeehiveBlock.HONEY_LEVEL, honeyLevel + levelIncrease));
                }
            }
        }
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    public static int getMaxHoneyLevel(BlockState state) {
        Block block = state.getBlock();
        return block instanceof AdvancedBeehiveAbstract ? ((AdvancedBeehiveAbstract) block).getMaxHoneyLevel() : 5;
    }

    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT beeTag = tag.getCompound("Bees");
        beeHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(beeTag));
    }

    @Nonnull
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        beeHandler.ifPresent(h -> {
            tag.remove("Bees");
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("Bees", compound);
        });

        return tag;
    }

    @Nonnull
    public ListNBT getBeeListAsNBTList() {
        return this.getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitantListAsListNBT).orElse(new ListNBT());
    }

    public static boolean spawnBeeInWorldAPosition(World world, BeeEntity entity, BlockPos pos, Direction direction, @Nullable Integer age) {
        BlockPos offset = pos.offset(direction);
        boolean isPositionBlocked = !world.getBlockState(offset).getCollisionShape(world, offset).isEmpty();
        float width = entity.getWidth();
        double spawnOffset = isPositionBlocked ? 0.0D : 0.55D + (double) (width / 2.0F);
        double x = (double) pos.getX() + 0.5D + spawnOffset * (double) direction.getXOffset();
        double y = (double) pos.getY() + 0.5D - (double) (entity.getHeight() / 2.0F);
        double z = (double) pos.getZ() + 0.5D + spawnOffset * (double) direction.getZOffset();
        entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
        if (age != null) {
            entity.setGrowingAge(age);
        }
        // Check if the entity is in beehive_inhabitors tag
        if (entity.getType().isContained(EntityTypeTags.BEEHIVE_INHABITORS)) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return world.addEntity(entity);
        }
        return false;
    }

    public List<Inhabitant> getBeeList() {
        return this.getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitants).orElse(new ArrayList<>());
    }

    public static class Inhabitant
    {
        public final CompoundNBT nbt;
        public int ticksInHive;
        public final int minOccupationTicks;
        public final BlockPos flowerPos;
        public final String localizedName;

        public Inhabitant(CompoundNBT nbt, int ticksInHive, int minOccupationTicks, BlockPos flowerPos, String localizedName) {
            nbt.removeUniqueId("UUID");
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

    private IInhabitantStorage createBeeHandler() {
        return new InhabitantStorage()
        {
            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                AdvancedBeehiveTileEntityAbstract.this.markDirty();
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityBee.BEE) {
            return beeHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}