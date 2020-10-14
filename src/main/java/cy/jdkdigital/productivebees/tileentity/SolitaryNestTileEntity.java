package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.BeeSpawningRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SolitaryNestTileEntity extends AdvancedBeehiveTileEntityAbstract
{
    // Used for calculating if a new bee should move in (initial value, will be overriden by recipe value)
    public int nestTickTimer = 24000;

    public SolitaryNestTileEntity() {
        super(ModTileEntityTypes.SOLITARY_NEST.get());
        MAX_BEES = 1;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote) {
            // Check if the nest has been abandoned and spawn a bee if it has
            Block block = this.getBlockState().getBlock();
            if (--nestTickTimer <= 0) {
                nestTickTimer = this.getRepopulationCooldown(block);
                if (this.canRepopulate()) {
                    if (block instanceof SolitaryNest) {
                        BeeEntity newBee = ((SolitaryNest) block).getNestingBeeType(world);
                        if (newBee != null) {
                            newBee.setHealth(newBee.getMaxHealth());
                            Direction direction = this.getBlockState().get(BlockStateProperties.FACING);
                            spawnBeeInWorldAPosition(this.world, newBee, pos, direction, null);
                        }
                    }
                }
            }
        }
        super.tick();
    }

    protected boolean canRepopulate() {
        SolitaryNest nest = ((SolitaryNest) this.getBlockState().getBlock());
        boolean blockConditionsMet = nest.canRepopulateIn(world.getDimension(), world.getBiome(this.getPos()));
        return hasNoBees() && blockConditionsMet;
    }

    public int getRepopulationCooldown(Block block) {
        IRecipe<?> recipe = world.getRecipeManager().getRecipe(new ResourceLocation(ProductiveBees.MODID, "bee_spawning/" + block.getRegistryName().getPath())).orElse(null);
        if (recipe instanceof BeeSpawningRecipe) {
            return ((BeeSpawningRecipe) recipe).repopulationCooldown;
        }
        return ProductiveBeesConfig.GENERAL.nestRepopulationCooldown.get();
    }

    protected int getTimeInHive(boolean hasNectar, @Nullable BeeEntity beeEntity) {
        return hasNectar && beeEntity != null && !beeEntity.isChild() ? 12000 : 6000;
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
        nestTickTimer = this.getRepopulationCooldown(state.getBlock());
    }
}
