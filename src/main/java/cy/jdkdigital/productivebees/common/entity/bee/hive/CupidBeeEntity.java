package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class CupidBeeEntity extends ProductiveBeeEntity
{
    public AnimalEntity target = null;

    public static Predicate<Entity> predicate = (entity -> entity instanceof AnimalEntity);

    public CupidBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, 1);
    }

    public void livingTick() {
        if (this.ticksExisted % 20 == 0) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(1.0D), this.getPosYRandom() + 0.5D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }

    @Override
    public boolean isFlowers(BlockPos pos) {
        List<Entity> entities = world.getEntitiesInAABBexcluding(this, (new AxisAlignedBB(pos).grow(1.0D, 1.0D, 1.0D)), predicate);
        if (!entities.isEmpty()) {
            target = (AnimalEntity) entities.get(0);

            if (target.getGrowingAge() == 0 && target.canBreed()) {
                target.setInLove(600);
            }

            return true;
        }

        return false;
    }
}
