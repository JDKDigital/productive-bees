package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BlueBandedBeeEntity extends SolitaryBeeEntity {
    public BlueBandedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 0);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.getTag(ModTags.RIVER_FLOWERS));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.75F);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4F);
    }
}
