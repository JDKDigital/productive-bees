package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class SweatyBeeEntity extends SolitaryBeeEntity
{
    public SweatyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 2);
    }

    @Override
    public String getRenderer() {
        return "slim";
    }

    @Override
    public Tag<Block> getFlowerTag() {
        return ModTags.SNOW_FLOWERS;
    }

    @Override
    public Tag<Block> getNestingTag() {
        return ModTags.COLD_NESTS;
    }
}
