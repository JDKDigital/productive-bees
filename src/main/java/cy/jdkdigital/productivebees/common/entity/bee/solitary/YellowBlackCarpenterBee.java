package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class YellowBlackCarpenterBee extends SolitaryBee
{
    public YellowBlackCarpenterBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        return ModTags.FOREST_FLOWERS.contains(flowerBlock);
    }

    @Override
    public String getRenderer() {
        return "thicc";
    }
}
