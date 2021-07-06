package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

public class SolitaryNestTileEntity extends AdvancedBeehiveTileEntityAbstract
{
    // Used for calculating if a new bee should move in (initial value, will be overriden by recipe value)
    private int nestTickTimer = -1;
    // Counter for cuckoo bee spawns
    private int spawnCount = 0;

    public SolitaryNestTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        MAX_BEES = 1;
    }

    public SolitaryNestTileEntity() {
        this(ModTileEntityTypes.SOLITARY_NEST.get());
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide && this.level instanceof ServerWorld && nestTickTimer > 0) {
            // Check if the nest has been activated and spawn a bee if it has
            Block block = this.getBlockState().getBlock();
            if (--nestTickTimer <= 0) {
                if (this.canRepopulate()) {
                    if (block instanceof SolitaryNest) {
                        Entity newBee = ((SolitaryNest) block).getNestingBeeType(level, level.getBiome(worldPosition));
                        if (newBee instanceof BeeEntity) {
                            ((BeeEntity) newBee).setHealth(((BeeEntity) newBee).getMaxHealth());
                            ((BeeEntity) newBee).hivePos = worldPosition;
                        }
                        Direction direction = this.getBlockState().getValue(BlockStateProperties.FACING);
                        spawnBeeInWorldAtPosition((ServerWorld) level, newBee, worldPosition.relative(direction), direction, null);
                        nestTickTimer = -1;
                    }
                }
            }

            this.setChanged();
        }
        super.tick();
    }

    public boolean canRepopulate() {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = nest.canRepopulateIn(level, level.getBiome(this.getBlockPos()));
        return isEmpty() && blockConditionsMet;
    }

    public void setNestCooldown(int cooldown) {
        nestTickTimer = cooldown;
    }

    public int getNestTickCooldown() {
        return nestTickTimer;
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        if (level != null && spawnCount < ProductiveBeesConfig.BEES.cuckooSpawnCount.get() && !beeEntity.isBaby() && beeState == BeehiveTileEntity.State.HONEY_DELIVERED && level.random.nextFloat() <= 0.1f) {
            // Cuckoo behavior
            BeeEntity offspring = null;
            switch (beeEntity.getEncodeId()) {
                case "productivebees:blue_banded_bee":
                    offspring = ModEntities.NEON_CUCKOO_BEE.get().create(level);
                    break;
                case "productivebees:ashy_mining_bee":
                    offspring = ModEntities.NOMAD_BEE.get().create(level);
                    break;
            }

            if (offspring != null) {
                spawnCount++;
                offspring.setAge(-24000);
                offspring.moveTo(beeEntity.getX(), beeEntity.getY(), beeEntity.getZ(), 0.0F, 0.0F);
                level.addFreshEntity(offspring);
            }
        }
        // reset repopulation cooldown
        nestTickTimer = -1;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        super.load(blockState, tag);

        if (tag.contains("nestTickTimer")) {
            nestTickTimer = tag.getInt("nestTickTimer");
        }
        if (tag.contains("spawnCount")) {
            spawnCount = tag.getInt("spawnCount");
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);

        tag.putInt("nestTickTimer", nestTickTimer);
        tag.putInt("spawnCount", spawnCount);

        return tag;
    }
}
