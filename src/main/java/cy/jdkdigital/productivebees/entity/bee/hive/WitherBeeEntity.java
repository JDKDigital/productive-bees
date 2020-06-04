package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WitherBeeEntity extends EffectHiveBeeEntity
{
    public WitherBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.TEMPER, 2);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.WITHER_FLOWERS);
    }

    @Override
    public Map<Effect, Integer> getEffects() {
        return new HashMap<Effect, Integer>()
        {{
            put(Effects.WITHER, 150);
        }};
    }
}
