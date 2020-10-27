package cy.jdkdigital.productivebees.common.entity.bee.nesting;

import cy.jdkdigital.productivebees.common.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class GlowingBeeEntity extends EffectHiveBeeEntity
{
    public GlowingBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.GLOWSTONE_NESTS);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.GLOWING_FLOWERS);
        beeAttributes.put(BeeAttributes.EFFECTS, new BeeEffect(new HashMap<Effect, Integer>() {{
            put(Effects.GLOWING, 150);
        }}));
    }

    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public Map<Effect, Integer> getAggressiveEffects() {
        return new HashMap<Effect, Integer>()
        {{
            put(Effects.BLINDNESS, 450);
        }};
    }
}
