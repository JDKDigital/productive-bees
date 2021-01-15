package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class GreenCarpenterBeeEntity extends SolitaryBeeEntity
{
    public GreenCarpenterBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        return flowerBlock.isIn(ModTags.FOREST_FLOWERS);
    }
}
