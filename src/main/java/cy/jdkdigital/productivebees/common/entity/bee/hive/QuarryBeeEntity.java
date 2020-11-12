package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class QuarryBeeEntity extends ProductiveBeeEntity
{
    public QuarryBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public Tag<Block> getFlowerTag() {
        return ModTags.QUARRY;
    }
}
