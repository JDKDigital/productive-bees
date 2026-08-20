package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class SolitaryNestBlockEntity extends AdvancedBeehiveBlockEntityAbstract
{
    // Used for calculating if a new bee should move in (initial value, will be overriden by recipe value)
    private int nestTickTimer = -1;

    // Counter for cuckoo bee spawns
    private int spawnCount = 0;

    public SolitaryNestBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        MAX_BEES = 1;
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntityTypes.SOLITARY_NEST.get();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SolitaryNestBlockEntity blockEntity) {
        if (blockEntity.nestTickTimer > 0) {
            // Check if the nest has been activated and spawn a bee if it has
            Block block = state.getBlock();
            if (--blockEntity.nestTickTimer <= 0) {
                if (blockEntity.canRepopulate() && block instanceof SolitaryNest nest) {
                    Entity newBee = SolitaryNest.getNestingBeeType(nest, level, level.getBiome(pos), level.getRandom());
                    if (newBee != null) {
                        if (newBee instanceof ProductiveBee pBee) {
                            pBee.setDefaultAttributes();
                        }
                        Direction direction = state.getValue(BlockStateProperties.FACING);
                        spawnBeeInWorldAtPosition((ServerLevel) level, newBee, pos.relative(direction), direction, null);
                        blockEntity.nestTickTimer = -1;
                        if (newBee instanceof Bee bee) {
                            bee.setHealth(((Bee) newBee).getMaxHealth());
                            bee.setHivePos(pos);
                        }
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
        return canRepopulate(ItemStack.EMPTY);
    }

    public boolean canRepopulate(ItemStack heldItem) {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = false;
        if (level instanceof ServerLevel serverLevel) {
            blockConditionsMet = !SolitaryNest.getSpawningRecipes(nest, serverLevel, serverLevel.getBiome(this.getBlockPos()), heldItem).isEmpty();
        }

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

        if (beeEntity.getEncodeId() != null && getSpawnCount() < ProductiveBeesConfig.BEES.cuckooSpawnCount.get() && !beeEntity.isBaby() && beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED && level.getRandom().nextFloat() <= 0.1f) {
            // Cuckoo behavior
            Bee offspring = switch (beeEntity.getEncodeId()) {
                case "productivebees:blue_banded_bee" -> ModEntities.NEON_CUCKOO_BEE.get().create(level, EntitySpawnReason.NATURAL);
                case "productivebees:ashy_mining_bee" -> ModEntities.NOMAD_BEE.get().create(level, EntitySpawnReason.NATURAL);
                default -> null;
            };

            if (offspring != null) {
                spawnCount++;
                offspring.setAge(-24000);
                offspring.snapTo(beeEntity.getX(), beeEntity.getY(), beeEntity.getZ(), 0.0F, 0.0F);
                if (offspring instanceof ProductiveBee pBee) {
                    pBee.setDefaultAttributes();
                }
                level.addFreshEntity(offspring);
            }
        }
        // reset repopulation cooldown
        nestTickTimer = -1;
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        nestTickTimer = input.getIntOr("nestTickTimer", -1);
        spawnCount = input.getIntOr("spawnCount", 0);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);

        output.putInt("nestTickTimer", nestTickTimer);
        output.putInt("spawnCount", spawnCount);
    }
}
