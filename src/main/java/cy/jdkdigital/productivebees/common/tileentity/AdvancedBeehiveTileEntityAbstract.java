package cy.jdkdigital.productivebees.common.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.hive.HoarderBeeEntity;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.handler.bee.InhabitantStorage;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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

    protected int tickCounter = 0;

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
        if (level != null && !level.isClientSide) {

            if (tickCounter++ % 100 == 0) {
                this.tickBees();
                tickCounter = 0;
            }

            // Play hive buzz sound
            beeHandler.ifPresent(h -> {
                if (h.getInhabitants().size() > 0 && level.getRandom().nextDouble() < 0.005D) {
                    BlockPos pos = this.getBlockPos();
                    double x = (double) pos.getX() + 0.5D;
                    double y = (double) pos.getY();
                    double z = (double) pos.getZ() + 0.5D;
                    level.playSound(null, x, y, z, SoundEvents.BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            });
        }
    }

    private void tickBees() {
        beeHandler.ifPresent(h -> {
            Iterator<AdvancedBeehiveTileEntityAbstract.Inhabitant> inhabitantIterator = h.getInhabitants().iterator();
            while (inhabitantIterator.hasNext()) {
                AdvancedBeehiveTileEntityAbstract.Inhabitant inhabitant = inhabitantIterator.next();
                if (inhabitant.ticksInHive > inhabitant.minOccupationTicks) {
                    BeehiveTileEntity.State beeState = inhabitant.nbt.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
                    if (this.releaseBee(this.getBlockState(), inhabitant.nbt.copy(), null, beeState)) {
                        inhabitantIterator.remove();
                    }
                } else {
                    inhabitant.ticksInHive += tickCounter;
                }
            }
        });
    }

    protected int getTimeInHive(boolean hasNectar, @Nullable BeeEntity beeEntity) {
        if (beeEntity instanceof ProductiveBeeEntity) {
            return ((ProductiveBeeEntity) beeEntity).getTimeInHive(hasNectar);
        }
        return hasNectar ? ProductiveBeesConfig.GENERAL.timeInHive.get() : ProductiveBeesConfig.GENERAL.timeInHive.get() / 2;
    }

    public void setChanged() {
        if (this.level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }

        super.setChanged();
    }

    @Override
    public void emptyAllLivingFromHive(@Nullable PlayerEntity player, BlockState blockState, BeehiveTileEntity.State beeState) {
        List<Entity> releasedBees = Lists.newArrayList();
        beeHandler.ifPresent(h -> {
            h.getInhabitants().removeIf((tag) -> this.releaseBee(blockState, tag.nbt.copy(), releasedBees, beeState));
        });
        if (player != null) {
            for (Entity entity : releasedBees) {
                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    if (player.blockPosition().distSqr(entity.blockPosition()) <= 16.0D) {
                        if (!this.isSedated()) {
                            // Check temper
                            if (beeEntity instanceof ProductiveBeeEntity) {
                                int temper = ((ProductiveBeeEntity) beeEntity).getAttributeValue(BeeAttributes.TEMPER);
                                if (temper == 0 || (temper == 1 && ProductiveBees.rand.nextFloat() < .5)) {
                                    beeEntity.setStayOutOfHiveCountdown(400);
                                    break;
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

    public boolean acceptsBee(BeeEntity bee) {
        return true;
    }

    @Override
    public void addOccupantWithPresetTicks(Entity entity, boolean hasNectar, int ticksInHive) {
        if (entity instanceof BeeEntity && acceptsBee((BeeEntity) entity)) {
            beeHandler.ifPresent(h -> {
                if (h.getInhabitants().size() < MAX_BEES) {
                    entity.stopRiding();
                    entity.ejectPassengers();
                    CompoundNBT compoundNBT = new CompoundNBT();
                    entity.save(compoundNBT);

                    BeeEntity beeEntity = (BeeEntity) entity;

                    h.addInhabitant(new Inhabitant(compoundNBT, ticksInHive, this.getTimeInHive(hasNectar, beeEntity), ((BeeEntity) entity).getSavedFlowerPos(), entity.getName().getString()));
                    if (beeEntity.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || (this.level != null && level.random.nextBoolean()))) {
                        this.savedFlowerPos = beeEntity.getSavedFlowerPos();
                    }

                    if (this.level != null) {
                        BlockPos pos = this.getBlockPos();
                        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }

                    entity.remove();
                }
            });
        }
    }

    @Override
    public void addOccupant(@Nonnull Entity beeEntity, boolean hasNectar) {
        this.addOccupantWithPresetTicks(beeEntity, hasNectar, 0);
    }

    public boolean releaseBee(BlockState blockState, CompoundNBT tag, @Nullable List<Entity> releasedBees, BeehiveTileEntity.State beeState) {
        if (blockState.getBlock().equals(Blocks.AIR) || level == null) {
            return false;
        }

        boolean stayInside =
                beeState != BeehiveTileEntity.State.EMERGENCY &&
                level.dimension() == World.OVERWORLD &&
                (
                    (level.isNight() && tag.getInt("bee_behavior") == 0) || // it's night and the bee is diurnal
                    (level.isRaining() && tag.getInt("bee_weather_tolerance") == 0) // it's raining and the bees is not tolerant
                );

        if (!stayInside & this.level instanceof ServerWorld) {
            BlockPos pos = this.getBlockPos();
            tag.remove("Passengers");
            tag.remove("Leash");
            tag.remove("UUID");
            Direction direction = blockState.hasProperty(BlockStateProperties.FACING) ? blockState.getValue(BlockStateProperties.FACING) : blockState.getValue(BeehiveBlock.FACING);
            BlockPos offset = pos.relative(direction);
            boolean isPositionBlocked = !level.getBlockState(offset).getCollisionShape(level, offset).isEmpty();
            if (!isPositionBlocked || beeState == BeehiveTileEntity.State.EMERGENCY) {
                // Spawn entity
                boolean spawned = false;
                BeeEntity beeEntity = (BeeEntity) EntityType.loadEntityRecursive(tag, level, (spawnedEntity) -> spawnedEntity);
                if (beeEntity != null) {

                    // Hoarder bees should leave their item behind
                    AtomicBoolean hasOffloaded = new AtomicBoolean(true);
                    if (beeEntity instanceof HoarderBeeEntity) {
                        if (((HoarderBeeEntity) beeEntity).holdsItem()) {
                            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                                ((HoarderBeeEntity) beeEntity).emptyIntoInventory(((InventoryHandlerHelper.ItemHandler) inv));

                                if (((HoarderBeeEntity) beeEntity).isInventoryEmpty()) {
                                    hasOffloaded.set(false);
                                }
                            });
                        }
                    }

                    spawned = spawnBeeInWorldAtPosition((ServerWorld) level, beeEntity, pos, direction, null);
                    if (spawned && hasOffloaded.get()) {
                        if (this.hasSavedFlowerPos() && !beeEntity.hasSavedFlowerPos() && (beeEntity.getEncodeId().contains("dye_bee") || level.random.nextFloat() <= 0.9F)) {
                            beeEntity.setSavedFlowerPos(this.savedFlowerPos);
                        }
                        beeReleasePostAction(beeEntity, blockState, beeState);

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
        beeEntity.resetTicksWithoutNectarSinceExitingHive();
        beeEntity.heal(2);

        applyHiveTime(getTimeInHive(beeState == BeehiveTileEntity.State.HONEY_DELIVERED, beeEntity), beeEntity);
        beeEntity.dropOffNectar();

        if (beeEntity instanceof ProductiveBeeEntity && ((ProductiveBeeEntity) beeEntity).hasConverted()) {
            return;
        }

        // Deliver honey on the way out
        if (beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
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

    private static void applyHiveTime(int ticksInHive, BeeEntity beeEntity) {
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

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        super.load(blockState, tag);

        CompoundNBT beeTag = tag.getCompound("Bees");
        beeHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(beeTag));
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);

        beeHandler.ifPresent(h -> {
            tag.remove("Bees");
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("Bees", compound);
        });

        return tag;
    }

    @Nonnull
    public ListNBT getBeeListAsNBTList() {
        return getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitantListAsListNBT).orElse(new ListNBT());
    }

    public static boolean spawnBeeInWorldAtPosition(ServerWorld world, Entity entity, BlockPos pos, Direction direction, @Nullable Integer age) {
        BlockPos offset = pos.relative(direction);
        boolean isPositionBlocked = !world.getBlockState(offset).getCollisionShape(world, offset).isEmpty();
        float width = entity.getBbWidth();
        double spawnOffset = isPositionBlocked ? 0.0D : 0.55D + (double) (width / 2.0F);
        double x = (double) pos.getX() + 0.5D + spawnOffset * (double) direction.getStepX();
        double y = (double) pos.getY() + 0.5D - (double) (entity.getBbHeight() / 2.0F);
        double z = (double) pos.getZ() + 0.5D + spawnOffset * (double) direction.getStepZ();
        entity.moveTo(x, y, z, entity.yRot, entity.xRot);
        if (age != null && entity instanceof BeeEntity) {
            ((BeeEntity) entity).setAge(age);
        }
        // Check if the entity is in beehive_inhabitors tag
        if (entity.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            CompoundNBT tag = new CompoundNBT();
            if (entity instanceof ConfigurableBeeEntity) {
                tag.putString("type", ((ConfigurableBeeEntity) entity).getBeeType());
            }
            return world.addFreshEntity(entity);
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
            nbt.remove("UUID");
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
                AdvancedBeehiveTileEntityAbstract.this.setChanged();
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

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(null, pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        deserializeNBT(tag);
    }
}