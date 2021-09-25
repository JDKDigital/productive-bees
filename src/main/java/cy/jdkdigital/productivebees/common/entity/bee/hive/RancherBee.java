package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class RancherBee extends ProductiveBee
{
    public PathfinderMob target = null;

    public static Predicate<Entity> predicate = (entity -> ModTags.RANCHABLES.contains(entity.getType()));

    public RancherBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
    }

    @Override
    public void tick() {
        super.tick();

        if (target != null) {
            if (!hasNectar()) {
                target.getNavigation().setSpeedModifier(0);
            } else {
                target.setTarget(this);
                target = null;
            }
        }
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    public boolean isFlowerValid(BlockPos pos) {
        List<Entity> entities = level.getEntities(this, (new AABB(pos).expandTowards(1.0D, 1.0D, 1.0D)), predicate);
        if (!entities.isEmpty()) {
            target = (PathfinderMob) entities.get(0);

            target.addEffect(new MobEffectInstance(MobEffects.LUCK, 400));

            return true;
        }

        return false;
    }

    @Override
    public void postPollinate() {
        super.postPollinate();

        if (target instanceof Sheep sheep) {
            sheep.shear(SoundSource.BLOCKS);
        }
    }
}
