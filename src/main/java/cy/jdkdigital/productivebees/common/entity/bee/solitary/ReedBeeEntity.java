package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class ReedBeeEntity extends SolitaryBeeEntity
{
    public ReedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Tag<Block> getFlowerTag() {
        return ModTags.SWAMP_FLOWERS;
    }

    @Override
    public Tag<Block> getNestingTag() {
        return ModTags.REED_NESTS;
    }
}
