package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BlueBandedBeeEntity extends SolitaryBeeEntity
{
    public BlueBandedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 0);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.RIVER_FLOWERS);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return ProductiveBeeEntity.getDefaultAttributes()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.75D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4D);
    }
}
