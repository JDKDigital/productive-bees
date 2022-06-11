package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BumbleBeeNestBlockEntity extends SolitaryNestBlockEntity
{
    public BumbleBeeNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BUMBLE_BEE_NEST.get(), pos, state);
        MAX_BEES = 3;
    }
}