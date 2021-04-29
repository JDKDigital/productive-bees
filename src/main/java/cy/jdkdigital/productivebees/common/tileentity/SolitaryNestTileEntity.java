package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.BeeSpawningRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

public class SolitaryNestTileEntity extends AdvancedBeehiveTileEntityAbstract
{
    // Used for calculating if a new bee should move in (initial value, will be overriden by recipe value)
    private int nestTickTimer = -1;

    public SolitaryNestTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
        MAX_BEES = 1;
    }

    public SolitaryNestTileEntity() {
        this(ModTileEntityTypes.SOLITARY_NEST.get());
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote && this.world instanceof ServerWorld && nestTickTimer > 0) {
            // Check if the nest has been activated and spawn a bee if it has
            Block block = this.getBlockState().getBlock();
            if (--nestTickTimer <= 0) {
                if (this.canRepopulate()) {
                    if (block instanceof SolitaryNest) {
                        Entity newBee = ((SolitaryNest) block).getNestingBeeType(world, world.getBiome(pos));
                        if (newBee instanceof BeeEntity) {
                            ((BeeEntity) newBee).setHealth(((BeeEntity) newBee).getMaxHealth());
                            ((BeeEntity) newBee).hivePos = pos;
                        }
                        Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                        spawnBeeInWorldAtPosition((ServerWorld) this.world, newBee, pos.offset(direction), direction, null);
                        nestTickTimer = -1;
                    }
                }
            }

            this.markDirty();
        }
        super.tick();
    }

    public boolean canRepopulate() {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = nest.canRepopulateIn(world, world.getBiome(this.getPos()));
        return hasNoBees() && blockConditionsMet;
    }

    public int getRepopulationCooldown(Block block) {
        IRecipe<?> recipe = world.getRecipeManager().getRecipe(new ResourceLocation(ProductiveBees.MODID, "bee_spawning/" + block.getRegistryName().getPath())).orElse(null);
        if (recipe instanceof BeeSpawningRecipe) {
            return ((BeeSpawningRecipe) recipe).repopulationCooldown;
        }
        return 24000;
    }

    public void setNestCooldown(int cooldown) {
        nestTickTimer = cooldown;
    }

    public int getNestTickCooldown() {
        return nestTickTimer;
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, BeehiveTileEntity.State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        if (!beeEntity.isChild() && beeState == BeehiveTileEntity.State.HONEY_DELIVERED && world.rand.nextFloat() <= 0.1f) {
            // Cuckoo behavior
            BeeEntity offspring = null;
            switch (beeEntity.getEntityString()) {
                case "productivebees:blue_banded_bee":
                    offspring = ModEntities.NEON_CUCKOO_BEE.get().create(world);
                    break;
                case "productivebees:ashy_mining_bee":
                    offspring = ModEntities.NOMAD_BEE.get().create(world);
                    break;
            }

            if (offspring != null) {
                offspring.setGrowingAge(-24000);
                offspring.setLocationAndAngles(beeEntity.getPosX(), beeEntity.getPosY(), beeEntity.getPosZ(), 0.0F, 0.0F);
                world.addEntity(offspring);
            }
        }
        // reset repopulation cooldown
        nestTickTimer = -1;
    }

    @Override
    public void read(BlockState blockState, CompoundNBT tag) {
        super.read(blockState, tag);

        if (tag.contains("nestTickTimer")) {
            setNestCooldown(tag.getInt("nestTickTimer"));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putInt("nestTickTimer", nestTickTimer);

        return tag;
    }
}
