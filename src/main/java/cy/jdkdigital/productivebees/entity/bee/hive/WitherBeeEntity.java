package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class WitherBeeEntity extends EffectHiveBeeEntity
{
    public WitherBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.TEMPER, 2);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.WITHER_FLOWERS);
        beeAttributes.put(BeeAttributes.EFFECTS, new BeeEffect(new HashMap<Effect, Integer>() {{
            put(Effects.WITHER, 150);
        }}));
    }

    @Override
    public Map<Effect, Integer> getAggressiveEffects() {
        return new HashMap<Effect, Integer>()
        {{
            put(Effects.WITHER, 350);
        }};
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        return source.equals(DamageSource.WITHER) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance effect) {
        return effect.getPotion() != Effects.WITHER && super.isPotionApplicable(effect);
    }
}
