package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BumbleBeeNestBlockEntity extends SolitaryNestBlockEntity
{
    public BumbleBeeNestBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        MAX_BEES = 3;
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntityTypes.BUMBLE_BEE_NEST.get();
    }
}