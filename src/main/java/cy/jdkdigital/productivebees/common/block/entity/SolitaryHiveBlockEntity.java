package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SolitaryHiveBlockEntity extends SolitaryNestBlockEntity
{
    public SolitaryHiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.SOLITARY_HIVE.get(), pos, state);
        MAX_BEES = 9;
    }

    public boolean canRepopulate() {
        return false;
    }
}
