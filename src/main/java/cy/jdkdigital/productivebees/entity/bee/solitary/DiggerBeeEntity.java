package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;

public class DiggerBeeEntity extends SolitaryBeeEntity {
    public DiggerBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        this.nestBlockTag = ModTags.getTag(ModTags.ARID_NESTS);
    }
    // Desert/arid biomes https://en.wikipedia.org/wiki/Centris_pallida https://en.wikipedia.org/wiki/List_of_Perdita_species
}
