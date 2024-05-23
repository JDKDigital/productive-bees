package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExpansionBoxBlockEntity extends BlockEntity
{
    public ExpansionBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.EXPANSION_BOX.get(), pos, state);
    }
}
