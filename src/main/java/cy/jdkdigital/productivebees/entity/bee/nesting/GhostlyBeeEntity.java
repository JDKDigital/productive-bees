package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class GhostlyBeeEntity extends ProductiveBeeEntity
{
    public GhostlyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.SOULED_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.SOUL_SAND_NESTS);
        beeAttributes.put(BeeAttributes.BEHAVIOR, 1);
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        return source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.ANVIL) || super.isInvulnerableTo(source);
    }
}
