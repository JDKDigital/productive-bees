package cy.jdkdigital.productivebees.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TranslucentCombBlock extends Block
{
    public TranslucentCombBlock(Properties properties) {
        super(properties);
    }

    @Deprecated
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
    }
}
