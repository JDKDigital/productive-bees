package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.block.SolitaryNest;
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
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import java.util.Iterator;
import java.util.List;

public class SolitaryNestTileEntity extends AdvancedBeehiveTileEntityAbstract {

    public List<SolitaryNestTileEntity.Egg> eggs = Lists.newArrayList();
    protected boolean isSealed = false;

    // Used for calculating if a new bee should move in
    private int nestTickTimer = 0;

    protected int MAX_BEES = 1;
    protected int MAX_EGGS = 3;

	public SolitaryNestTileEntity() {
	    super(ModTileEntityTypes.SOLITARY_NEST.get());
	}

    @Override
    public int getMaxBees() {
        return MAX_BEES;
    }

    public int getEggCapacity() {
        return MAX_EGGS;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            // Check if the nest has been abandoned and spawn a bee if it has
            if (++nestTickTimer % 47 == 0) { // Does not need to check every tick
                if (!this.canRepopulate()) {
                    nestTickTimer = 0;
                } else if (this.canRepopulate() && nestTickTimer > this.getRepopulationCooldown()) {
                    nestTickTimer = 0;
                    BlockPos pos = this.getPos();
                    Block block = this.getBlockState().getBlock();
                    if (block instanceof SolitaryNest) {
                        EntityType<BeeEntity> beeType = getProducibleBeeType(world, pos, (SolitaryNest) block);
                        BeeEntity newBee = beeType.create(this.world);
                        if (newBee != null) {
                            Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                            spawnBeeInWorldAPosition(this.world, newBee, this.pos, direction, null);
                        }
                    }
                }
            }

            // Once nest is empty of eggs, unseal it
            if (this.eggs.isEmpty()) {
                this.isSealed = false;
            }
            // Check if eggs need to hatch from sealed hive
            if (this.isSealed && nestTickTimer % 41 == 0) {
                Iterator<SolitaryNestTileEntity.Egg> eggIterator = this.eggs.iterator();
                while(eggIterator.hasNext()) {
                    SolitaryNestTileEntity.Egg egg = eggIterator.next();
                    if (egg.ticksInHive > egg.incubationTime) {
                        CompoundNBT tag = egg.nbt;
                        Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                        BeeEntity beeEntity = (BeeEntity) EntityType.func_220335_a(tag, this.world, (spawnedEntity) -> spawnedEntity);
                        if (beeEntity != null && spawnBeeInWorldAPosition(this.world, beeEntity, this.getPos(), direction, 0)) {
                            eggIterator.remove();
                        }
                    } else {
                        egg.ticksInHive++;
                    }
                }
            }
        }
        super.tick();
    }

    public static EntityType<BeeEntity> getProducibleBeeType(World world, BlockPos pos, SolitaryNest nest) {
        Dimension dimension = world.getDimension();
        Biome biome = world.getBiome(pos);

        return nest.getNestingBeeType(world);
    }

    protected boolean canRepopulate() {
        SolitaryNest block = ((SolitaryNest)this.getBlockState().getBlock());
	    boolean blockConditionsMet = block.canRepopulateIn(world.getDimension(), world.getBiome(this.getPos()));
	    return isHiveEmpty() && blockConditionsMet;
    }

    protected int getRepopulationCooldown() {
	    return ((SolitaryNest)this.getBlockState().getBlock()).getRepopulationCooldown();
    }

    protected int getTimeInHive(boolean hasNectar) {
        // When the bee returns with nectar, it will produce an egg cell and will stay a while
        return hasNectar ? 4000 : 600;
    }

    public void read(CompoundNBT compoundNBT) {
        super.read(compoundNBT);
        this.eggs.clear();
        ListNBT eggList = compoundNBT.getList("Eggs", 10);

        for(int i = 0; i < eggList.size(); ++i) {
            CompoundNBT eggNBT = eggList.getCompound(i);
            SolitaryNestTileEntity.Egg egg = new SolitaryNestTileEntity.Egg(eggNBT.getCompound("EntityData"), eggNBT.getInt("TicksInHive"), eggNBT.getInt("IncubationTime"));
            this.eggs.add(egg);
        }
    }

    public CompoundNBT write(CompoundNBT compoundNBT) {
        super.write(compoundNBT);
        compoundNBT.put("Eggs", this.getEggList());

        return compoundNBT;
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        beeEntity.func_226426_eu_();

        // Lay egg
        if (beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            beeEntity.func_226413_eG_();
            if (this.eggs.size() < this.getEggCapacity()) {
                CompoundNBT compoundNBT = new CompoundNBT();
                beeEntity.writeUnlessPassenger(compoundNBT);
                this.eggs.add(new SolitaryNestTileEntity.Egg(compoundNBT, 0, this.getRepopulationCooldown() * this.eggs.size() / 3));

                // Once nest is full of eggs, seal it and set the "mother" bee to die
                if (this.eggs.size() == this.getEggCapacity()) {
                    this.isSealed = true;
                    beeEntity.hivePos = null;
                    beeEntity.func_226449_s_(true); // loose stinger
                }
            }
        }
    }

    public ListNBT getEggList() {
        ListNBT listNBT = new ListNBT();

        for (Egg egg : this.eggs) {
            egg.nbt.removeUniqueId("UUID");
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.put("EntityData", egg.nbt);
            compoundNBT.putInt("TicksInHive", egg.ticksInHive);
            compoundNBT.putInt("IncubationTime", egg.incubationTime);
            listNBT.add(compoundNBT);
        }

        return listNBT;
    }

    public static class Egg {
        public final CompoundNBT nbt;
        public int ticksInHive;
        public final int incubationTime;

        public Egg(CompoundNBT nbt, int ticksInHive, int incubationTime) {
            nbt.removeUniqueId("UUID");
            this.nbt = nbt;
            this.ticksInHive = ticksInHive;
            this.incubationTime = incubationTime;
        }
    }
}
