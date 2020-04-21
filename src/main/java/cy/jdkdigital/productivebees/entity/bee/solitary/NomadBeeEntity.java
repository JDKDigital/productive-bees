package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class NomadBeeEntity extends SolitaryBeeEntity {
    public NomadBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.getTag(ModTags.ARID_FLOWERS));
    }
}
