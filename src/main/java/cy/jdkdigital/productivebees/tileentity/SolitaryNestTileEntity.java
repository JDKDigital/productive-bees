package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.handler.bee.InhabitantStorage;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SolitaryNestTileEntity extends AdvancedBeehiveTileEntityAbstract
{
    private LazyOptional<IInhabitantStorage> eggHandler = LazyOptional.of(this::createEggHandler);
    protected boolean isSealed = false;
    private int tickCounter = 0;

    // Used for calculating if a new bee should move in
    private int nestTickTimer = 0;

    public int MAX_EGGS = 3;

    public SolitaryNestTileEntity() {
        super(ModTileEntityTypes.SOLITARY_NEST.get());
        MAX_BEES = 1;
    }

    public boolean isSealed() {
        return isSealed;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            // Check if the nest has been abandoned and spawn a bee if it has
            if (++nestTickTimer % 47 == 0) { // Does not need to check every tick
                if (!this.canRepopulate()) {
                    nestTickTimer = 0;
                }
                else if (this.canRepopulate() && nestTickTimer > this.getRepopulationCooldown()) {
                    nestTickTimer = 0;
                    Block block = this.getBlockState().getBlock();
                    if (block instanceof SolitaryNest) {
                        EntityType<BeeEntity> beeType = getProducibleBeeType(world, pos, (SolitaryNest) block);
                        if (beeType != null) {
                            BeeEntity newBee = beeType.create(this.world);
                            Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                            spawnBeeInWorldAPosition(this.world, newBee, pos, direction, null);
                        }
                    }
                }
            }

            eggHandler.ifPresent(h -> {
                // Once nest is empty of eggs, unseal it
                if (h.getInhabitants().isEmpty()) {
                    this.isSealed = false;
                }
            });

            if (tickCounter++ % 97 == 0) {
                this.tickEggs();
                tickCounter = 0;
            }
        }
        super.tick();
    }

    private void tickEggs() {
        eggHandler.ifPresent(h -> {
            // Once nest is empty of eggs, unseal it
            if (h.getInhabitants().isEmpty()) {
                this.isSealed = false;
            }
            // Check if eggs need to hatch from sealed hive
            if (this.isSealed) {
                h.getInhabitants().removeIf((egg) -> {
                    if (egg.ticksInHive > egg.minOccupationTicks) {
                        CompoundNBT tag = egg.nbt;
                        Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                        BeeEntity beeEntity = (BeeEntity) EntityType.func_220335_a(tag, this.world, (spawnedEntity) -> spawnedEntity);
                        if (beeEntity != null && spawnBeeInWorldAPosition(this.world, beeEntity, this.getPos(), direction, -24000)) {
                            return true;
                        }
                    }
                    else {
                        egg.ticksInHive += tickCounter;
                    }
                    return false;
                });
            }
        });
    }

    @Nullable
    public static EntityType<BeeEntity> getProducibleBeeType(World world, BlockPos pos, SolitaryNest nest) {
        EntityType<BeeEntity> beeType = nest.getNestingBeeType(world);

        // Cuckoo behavior
        if (beeType != null && world.getRandom().nextInt(10) == 1) {
            switch (beeType.getRegistryName().getPath()) {
                case "blue_banded_bee":
                    beeType = ModEntities.NEON_CUCKOO_BEE.get();
                    break;
                case "ashy_mining_bee":
                    beeType = ModEntities.NOMAD_BEE.get();
                    break;
            }
        }

        return beeType;
    }

    protected boolean canRepopulate() {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = nest.canRepopulateIn(world.getDimension(), world.getBiome(this.getPos()));
        return hasNoBees() && blockConditionsMet;
    }

    protected int getRepopulationCooldown() {
        return ((SolitaryNest) this.getBlockState().getBlock()).getRepopulationCooldown();
    }

    protected int getTimeInHive(boolean hasNectar) {
        // When the bee returns with nectar, it will produce an egg cell and will stay a while
        return hasNectar ? 12000 : 600;
    }

    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT eggTag = tag.getCompound("Eggs");
        eggHandler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(eggTag));
    }

    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        eggHandler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("Eggs", compound);
        });

        return tag;
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        // Lay egg
        if (beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            eggHandler.ifPresent(h -> {
                if (h.getInhabitants().size() < MAX_EGGS) {
                    CompoundNBT compoundNBT = new CompoundNBT();
                    beeEntity.writeUnlessPassenger(compoundNBT);

                    h.addInhabitant(new Egg(compoundNBT, 0, this.getRepopulationCooldown() * this.getEggs().size()));
                    // Once nest is full of eggs, seal it and set the "mother" bee to die
                    if (h.getInhabitants().size() == MAX_EGGS) {
                        this.isSealed = true;
                        // Kill off mother bee after filling the hive with eggs
                        beeEntity.hivePos = null;
                        beeEntity.setHasStung(true);
                    }
                }
            });
        }
    }

    public List<Inhabitant> getEggs() {
        return this.getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitants).orElse(new ArrayList<>());
    }

    @Nonnull
    public ListNBT getEggListAsNBTList() {
        return this.getCapability(CapabilityBee.BEE).map(IInhabitantStorage::getInhabitantListAsListNBT).orElse(new ListNBT());
    }

    public static class Egg extends Inhabitant
    {

        public Egg(CompoundNBT nbt, int ticksInHive, int incubationTime) {
            super(nbt, ticksInHive, incubationTime, "");
        }

        @Override
        public String toString() {
            return "Egg {" +
                    "ticksInHive=" + ticksInHive +
                    ", incubationTime=" + minOccupationTicks +
                    '}';
        }
    }

    private IInhabitantStorage createEggHandler() {
        return new InhabitantStorage()
        {
            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                SolitaryNestTileEntity.this.markDirty();
            }
        };
    }
}
