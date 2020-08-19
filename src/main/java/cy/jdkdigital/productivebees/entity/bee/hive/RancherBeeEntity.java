package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class RancherBeeEntity extends ProductiveBeeEntity
{
    public CreatureEntity target = null;

    public static Predicate<Entity> predicate = (entity -> {
        return ModTags.RANCHABLES.contains(entity.getType());
    });

    public RancherBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if (target != null) {
            if (!hasNectar()) {
                target.getNavigator().setSpeed(0);
            }
            else {
                target.setRevengeTarget(this);
                target = null;
            }
        }
    }

    @Override
    public boolean isFlowers(BlockPos pos) {
        List<Entity> entities = world.getEntitiesInAABBexcluding(this, (new AxisAlignedBB(pos).grow(1.0D, 1.0D, 1.0D)), predicate);
        if (!entities.isEmpty()) {
            target = (CreatureEntity) entities.get(0);

            target.addPotionEffect(new EffectInstance(Effects.LUCK, 400));

            return true;
        }

        return false;
    }
}
