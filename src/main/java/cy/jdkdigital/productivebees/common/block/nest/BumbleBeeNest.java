package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.tileentity.BumbleBeeNestTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryHiveTileEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class BumbleBeeNest extends SolitaryNest
{
    public BumbleBeeNest(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.FACING, Direction.UP));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.BUMBLE_BEE_NEST.get().create();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new BumbleBeeNestTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return this.getDefaultState().with(BlockStateProperties.FACING, Direction.UP);
    }
}
