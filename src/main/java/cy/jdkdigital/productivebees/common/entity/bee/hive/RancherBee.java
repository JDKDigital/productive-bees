package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class RancherBee extends ProductiveBee
{
    public PathfinderMob target = null;

    public static Predicate<Entity> predicate = (entity -> entity.getType().is(ModTags.RANCHABLES));

    public RancherBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
        setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_RAIN);
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
        if (!level().isLoaded(pos)) {
            return false;
        }

        if (level().getBlockEntity(pos) instanceof AmberBlockEntity amberBlockEntity) {
            var entity = amberBlockEntity.getCachedEntity();
            return entity != null && entity.getType().is(ModTags.RANCHABLES);
        } else {
            List<Entity> entities = level().getEntities(this, (new AABB(pos).inflate(1.0D, 1.0D, 1.0D)), predicate);
            if (!entities.isEmpty()) {
                target = (PathfinderMob) entities.get(0);

                target.addEffect(new MobEffectInstance(MobEffects.LUCK, 400));

                return true;
            }
        }

        return isValidFeeder(this, level().getBlockEntity(pos), this::isFlowerBlock, this::isFlowerItem);
    }

    @Override
    public void postPollinate() {
        super.postPollinate();

        if (target instanceof Shearable sheep && sheep.readyForShearing()) {
            sheep.shear(SoundSource.BLOCKS);
        }
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return false;
    }

    @Override
    public String getFlowerType() {
        return "entity_type";
    }
}
