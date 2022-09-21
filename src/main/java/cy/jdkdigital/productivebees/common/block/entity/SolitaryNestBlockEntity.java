package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SolitaryNestBlockEntity extends AdvancedBeehiveBlockEntityAbstract
{
    // Used for calculating if a new bee should move in (initial value, will be overriden by recipe value)
    private int nestTickTimer = -1;

    // Counter for cuckoo bee spawns
    private int spawnCount = 0;

    public SolitaryNestBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
        MAX_BEES = 1;
    }

    public SolitaryNestBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntityTypes.SOLITARY_NEST.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolitaryNestBlockEntity blockEntity) {
        if (blockEntity.nestTickTimer > 0) {
            // Check if the nest has been activated and spawn a bee if it has
            Block block = state.getBlock();
            if (--blockEntity.nestTickTimer <= 0) {
                if (blockEntity.canRepopulate()) {
                    if (block instanceof SolitaryNest) {
                        Entity newBee = ((SolitaryNest) block).getNestingBeeType(level, level.getBiome(pos).value(), level.random);
                        if (newBee instanceof Bee) {
                            ((Bee) newBee).setHealth(((Bee) newBee).getMaxHealth());
                            ((Bee) newBee).hivePos = pos;
                        }
                        Direction direction = state.getValue(BlockStateProperties.FACING);
                        spawnBeeInWorldAtPosition((ServerLevel) level, newBee, pos.relative(direction), direction, null);
                        blockEntity.nestTickTimer = -1;
                    }
                }
            }

            blockEntity.setChanged();
        }
        AdvancedBeehiveBlockEntityAbstract.tick(level, pos, state, blockEntity);
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public boolean canRepopulate() {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = nest.canRepopulateIn(level, level.getBiome(this.getBlockPos()).value());
        return isEmpty() && blockConditionsMet;
    }

    public void setNestCooldown(int cooldown) {
        nestTickTimer = cooldown;
    }

    public int getNestTickCooldown() {
        return nestTickTimer;
    }

    protected void beeReleasePostAction(Level level, Bee beeEntity, BlockState state, BeeReleaseStatus beeState) {
        super.beeReleasePostAction(level, beeEntity, state, beeState);

        if (beeEntity.getEncodeId() != null && getSpawnCount() < ProductiveBeesConfig.BEES.cuckooSpawnCount.get() && !beeEntity.isBaby() && beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED && level.random.nextFloat() <= 0.1f) {
            // Cuckoo behavior
            Bee offspring = switch (beeEntity.getEncodeId()) {
                case "productivebees:blue_banded_bee" -> ModEntities.NEON_CUCKOO_BEE.get().create(level);
                case "productivebees:ashy_mining_bee" -> ModEntities.NOMAD_BEE.get().create(level);
                default -> null;
            };

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
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        if (tag.contains("nestTickTimer")) {
            nestTickTimer = tag.getInt("nestTickTimer");
        }
        if (tag.contains("spawnCount")) {
            spawnCount = tag.getInt("spawnCount");
        }
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        tag.putInt("nestTickTimer", nestTickTimer);
        tag.putInt("spawnCount", spawnCount);
    }
}
