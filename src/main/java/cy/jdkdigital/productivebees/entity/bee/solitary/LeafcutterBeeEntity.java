package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class LeafcutterBeeEntity extends SolitaryBeeEntity {
    public LeafcutterBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }
}
