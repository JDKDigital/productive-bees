package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LumberBee extends ProductiveBee
{
    public LumberBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.LUMBER);
    }
}
