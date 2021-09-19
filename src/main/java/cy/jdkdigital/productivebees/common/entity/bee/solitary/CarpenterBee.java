package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CarpenterBee extends SolitaryBee
{
    public CarpenterBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.FOREST_FLOWERS);
    }

    @Override
    public String getRenderer() {
        return "thicc";
    }
}
