package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NetherBeeNestBlockEntity extends AdvancedBeehiveBlockEntityAbstract
{
    public NetherBeeNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.NETHER_BEE_NEST.get(), pos, state);
    }
}
