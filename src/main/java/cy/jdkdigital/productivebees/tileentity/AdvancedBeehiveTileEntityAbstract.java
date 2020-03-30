package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.handler.bee.BeeStorage;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IBeeStorage;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AdvancedBeehiveTileEntityAbstract extends BeehiveTileEntity {
    public BlockPos flowerPos = null;
    public int MAX_BEES = 3;
    private LazyOptional<IBeeStorage> beeHandler = LazyOptional.of(this::createBeeHandler);
    private TileEntityType<?> tileEntityType;

    private int tickCounter = 0;

    public AdvancedBeehiveTileEntityAbstract(TileEntityType<?> tileEntityType) {
        super();
        this.tileEntityType = tileEntityType;
    }

    @Nonnull
    @Override
    public TileEntityType<?> getType() {
        return this.tileEntityType;
    }

    public void tick() {
        if (!this.world.isRemote) {

            if (tickCounter++ % 100 == 0) {
                this.tickBees();
                tickCounter = 0;
            }

            // Play hive buzz sound
            beeHandler.ifPresent(h -> {
                if (h.getBees().size() > 0 && this.world.getRandom().nextDouble() < 0.005D) {
                    BlockPos pos = this.getPos();
                    double x = (double) pos.getX() + 0.5D;
                    double y = (double) pos.getY();
                    double z = (double) pos.getZ() + 0.5D;
                    this.world.playSound(null, x, y, z, SoundEvents.field_226134_ai_, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            });
        }
    }

    private void tickBees() {
        beeHandler.ifPresent(h -> {
            h.getBees().removeIf((bee) -> {
                if (bee.ticksInHive > bee.minOccupationTicks) {
                    BeehiveTileEntity.State beeState = bee.nbt.getBoolean("HasNectar") ? BeehiveTileEntity.State.HONEY_DELIVERED : BeehiveTileEntity.State.BEE_RELEASED;
                    return this.releaseBee(this.getBlockState(), bee.nbt, null, beeState);
                } else {
                    bee.ticksInHive += tickCounter;
                }
                return false;
            });
        });
    }

    protected int getTimeInHive(boolean hasNectar) {
        return hasNectar ? 2400 : 600;
    }

    public void markDirty() {
        if (this.func_226968_d_()) {
            this.releaseBees(null, this.world.getBlockState(this.getPos()), BeehiveTileEntity.State.EMERGENCY);
        }

        super.markDirty();
    }

    public void releaseBees(@Nullable PlayerEntity player, BlockState blockState, BeehiveTileEntity.State beeState) {
        List<Entity> releasedBees = Lists.newArrayList();
        beeHandler.ifPresent(h -> {
            h.getBees().removeIf((tag) -> this.releaseBee(blockState, tag.nbt, releasedBees, beeState));
        });
        if (player != null) {
            Iterator entityIterator = releasedBees.iterator();

            while (entityIterator.hasNext()) {
                Entity entity = (Entity) entityIterator.next();
                if (entity instanceof BeeEntity) {
                    BeeEntity beeEntity = (BeeEntity) entity;
                    if (player.getPositionVec().squareDistanceTo(entity.getPositionVec()) <= 16.0D) {
                        if (!this.hasCampfire()) {
                            beeEntity.func_226391_a_(player);
                        } else {
                            beeEntity.func_226450_t_(400);
                        }
                    }
                }
            }
        }
    }

    public boolean isHiveEmpty() {
        return this.getBees().size() == 0;
    }

    public boolean isHiveFull() {
        return this.getBees().size() == MAX_BEES;
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(AdvancedBeehiveAbstract.HONEY_LEVEL);
    }

    public static int getMaxHoneyLevel(BlockState state) {
        Block block = state.getBlock();
        return block instanceof AdvancedBeehiveAbstract ? ((AdvancedBeehiveAbstract) block).getMaxHoneyLevel() : 5;
    }

    public boolean hasCampfire() {
        return CampfireBlock.func_226914_b_(this.world, this.getPos(), 5);
    }

    public void insertBee(Entity entity, boolean hasNectar, int ticksInHive) {
        beeHandler.ifPresent(h -> {
            if (h.getBees().size() < MAX_BEES) {
                entity.stopRiding();
                entity.removePassengers();
                CompoundNBT compoundNBT = new CompoundNBT();
                entity.writeUnlessPassenger(compoundNBT);
                h.addBee(new Bee(compoundNBT, ticksInHive, this.getTimeInHive(hasNectar)));
                if (this.world != null) {
                    if (entity instanceof BeeEntity) {
                        BeeEntity beeEntity = (BeeEntity) entity;
                        if (beeEntity.func_226425_er_() && (!this.hasFlowerPos() || this.world.rand.nextBoolean())) {
                            this.flowerPos = beeEntity.func_226424_eq_();
                        }
                    }

                    BlockPos pos = this.getPos();
                    this.world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.field_226131_af_, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                entity.remove();
            }
        });
    }

    public void func_226961_a_(Entity beeEntity, boolean hasNectar) {
        this.insertBee(beeEntity, hasNectar, 0);
    }

    public void func_226962_a_(Entity beeEntity, boolean hasNectar, int ticksInHive) {
        this.insertBee(beeEntity, hasNectar, ticksInHive);
    }

    public boolean releaseBee(BlockState state, CompoundNBT tag, @Nullable List<Entity> releasedBees, BeehiveTileEntity.State beeState) {
        BlockPos pos = this.getPos();
        if ((this.world.isNightTime() || this.world.isRaining()) && beeState != BeehiveTileEntity.State.EMERGENCY) {
            return false;
        } else {
            tag.remove("Passengers");
            tag.remove("Leash");
            tag.removeUniqueId("UUID");
            Direction direction = state.has(BlockStateProperties.FACING) ? state.get(BlockStateProperties.FACING) : state.get(BeehiveBlock.field_226872_b_);
            BlockPos offset = pos.offset(direction);
            boolean isPositionBlocked = !this.world.getBlockState(offset).getCollisionShape(this.world, offset).isEmpty();
            if (isPositionBlocked && beeState != BeehiveTileEntity.State.EMERGENCY) {
                ProductiveBees.LOGGER.info("position blocked");
                return false;
            } else {
                // Spawn entity
                boolean spawned = false;
                BeeEntity beeEntity = (BeeEntity) EntityType.func_220335_a(tag, this.world, (spawnedEntity) -> spawnedEntity);
                if (beeEntity != null) {
                    ProductiveBees.LOGGER.info("release entity: " + beeEntity);
                    spawned = spawnBeeInWorldAPosition(this.world, beeEntity, this.pos, direction, null);
                    if (spawned) {
                        if (this.hasFlowerPos() && !beeEntity.func_226425_er_() && this.world.rand.nextFloat() < 0.9F) {
                            beeEntity.func_226431_g_(this.flowerPos);
                        }

                        beeReleasePostAction(beeEntity, state, beeState);

                        if (releasedBees != null) {
                            releasedBees.add(beeEntity);
                        }
                    }
                }
                return spawned;
            }
        }
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        beeEntity.func_226426_eu_();

        // Deliver honey on the way out
        if (beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            beeEntity.func_226413_eG_();
            Block block = state.getBlock();
            if (block.isIn(BlockTags.BEEHIVES)) {
                int honeyLevel = getHoneyLevel(state);
                int maxHoneyLevel = getMaxHoneyLevel(state);
                if (honeyLevel < maxHoneyLevel) {
                    int levelIncrease = this.world.rand.nextInt(100) == 0 ? 2 : 1;
                    if (honeyLevel + levelIncrease > maxHoneyLevel) {
                        --levelIncrease;
                    }

                    this.world.setBlockState(pos, state.with(AdvancedBeehiveAbstract.HONEY_LEVEL, honeyLevel + levelIncrease));
                }
            }
        }
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT beeTag = tag.getCompound("Bees");
        beeHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(beeTag));
    }

    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        beeHandler.ifPresent(h -> {
            tag.remove("Bees");
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("Bees", compound);
        });

        return tag;
    }

    public ListNBT getBeeListAsNBTList() {
        return this.getCapability(CapabilityBee.BEE).map(IBeeStorage::getBeeListAsListNBT).orElse(new ListNBT());
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
        if (!entity.getType().isContained(EntityTypeTags.field_226155_c_)) {
            return false;
        } else {
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.field_226132_ag_, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return world.addEntity(entity);
        }
    }

    public List<Bee> getBees() {
        return this.getCapability(CapabilityBee.BEE).map(IBeeStorage::getBees).orElse(new ArrayList<>());
    }

    public static class Bee {
        public final CompoundNBT nbt;
        public int ticksInHive;
        public final int minOccupationTicks;

        public Bee(CompoundNBT nbt, int ticksInHive, int minOccupationTicks) {
            nbt.removeUniqueId("UUID");
            this.nbt = nbt;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
        }

        @Override
        public String toString() {
            return "Bee{" +
                    "ticksInHive=" + ticksInHive +
                    ", minOccupationTicks=" + minOccupationTicks +
                    ", nbt=" + nbt +
                    '}';
        }
    }

    private IBeeStorage createBeeHandler() {
        return new BeeStorage() {
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