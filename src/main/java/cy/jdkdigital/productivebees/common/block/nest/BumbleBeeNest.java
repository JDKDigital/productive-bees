package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.tileentity.BumbleBeeNestTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class BumbleBeeNest extends SolitaryNest
{
    public BumbleBeeNest(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BumbleBeeNestTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
    }
}
