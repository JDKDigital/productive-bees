package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;

public class SweatyBeeEntity extends SolitaryBeeEntity
{
    public SweatyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 2);
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        return flowerBlock.isIn(ModTags.SNOW_FLOWERS);
    }

    @Override
    public ITag<Block> getNestingTag() {
        return ModTags.COLD_NESTS;
    }

    @Override
    public String getRenderer() {
        return "slim";
    }
}
