package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ZombieBeeEntity extends EffectHiveBeeEntity
{
    public ZombieBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.BEHAVIOR, 1);
    }

    @Override
    public Map<Effect, Integer> getEffects() {
        return new HashMap<Effect, Integer>()
        {{
            put(Effects.HUNGER, 220);
        }};
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return ProductiveBeeEntity.getDefaultAttributes()
                .createMutableAttribute(Attributes.FLYING_SPEED, 0.4D)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
    }
}
