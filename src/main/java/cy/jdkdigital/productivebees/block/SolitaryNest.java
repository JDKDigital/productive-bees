package cy.jdkdigital.productivebees.block;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.tileentity.SolitaryNestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import javax.annotation.Nullable;

public class SolitaryNest extends AdvancedBeehiveAbstract
{
    public SolitaryNest(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.NORTH));
    }

    public int getMaxHoneyLevel() {
        return 0;
    }

    public EntityType<? extends BeeEntity> getNestingBeeType(World world) {
        ResourceLocation id = this.getRegistryName();
        IRecipe<?> recipe = world.getRecipeManager().getRecipe(new ResourceLocation(ProductiveBees.MODID, "bee_spawning/" + id.getPath())).orElse(null);

        if (recipe instanceof BeeSpawningRecipe) {
            BeeSpawningRecipe spawningRecipe = (BeeSpawningRecipe) recipe;
            BeeIngredient bee = spawningRecipe.output.get(world.rand.nextInt(spawningRecipe.output.size())).get();
            return bee.getBeeEntity();
        }
        ProductiveBees.LOGGER.info("No bee spawning recipe found for " + id);
        return null;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return ModTileEntityTypes.SOLITARY_NEST.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new SolitaryNestTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return this.getDefaultState().with(BlockStateProperties.FACING, itemUseContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.with(BlockStateProperties.FACING, rotation.rotate(state.get(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.toRotation(state.get(BlockStateProperties.FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return dimension.isSurfaceWorld();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote()) {
            SolitaryNestTileEntity tileEntity = (SolitaryNestTileEntity) world.getTileEntity(pos);
            ProductiveBees.LOGGER.debug("Nest tilentity: " + tileEntity);
            ProductiveBees.LOGGER.debug("Nest tilentity type: " + tileEntity.getType().getRegistryName());
            ProductiveBees.LOGGER.debug("Bee count: " + tileEntity.getBeeList().size());
            ProductiveBees.LOGGER.debug("Occupants: " + tileEntity.getBeeList());
            ProductiveBees.LOGGER.debug("Egg count: " + tileEntity.getEggs().size());
            ProductiveBees.LOGGER.debug("Eggs: " + tileEntity.getEggListAsNBTList());
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
