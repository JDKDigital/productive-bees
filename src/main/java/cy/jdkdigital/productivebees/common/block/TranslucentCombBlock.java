package cy.jdkdigital.productivebees.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class TranslucentCombBlock extends Block
{
    public TranslucentCombBlock(Properties properties) {
        super(properties);
    }

    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this || super.isSideInvisible(state, adjacentBlockState, side);
    }
}
