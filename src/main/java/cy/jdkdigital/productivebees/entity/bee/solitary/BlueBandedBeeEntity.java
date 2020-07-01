package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
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

    public static AttributeModifierMap.MutableAttribute func_234182_eX_() {
        return MobEntity.func_233666_p_()
                .func_233815_a_(Attributes.field_233822_e_, 0.75D)
                .func_233815_a_(Attributes.field_233821_d_, 0.4D);
    }
}
