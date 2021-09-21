package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;

import java.util.List;
import java.util.function.Predicate;

public class RancherBeeEntity extends ProductiveBeeEntity
{
    public CreatureEntity target = null;

    public static Predicate<Entity> predicate = (entity -> ModTags.RANCHABLES.contains(entity.getType()));

    public RancherBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
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
        List<Entity> entities = level.getEntities(this, (new AxisAlignedBB(pos).expandTowards(1.0D, 1.0D, 1.0D)), predicate);
        if (!entities.isEmpty()) {
            target = (CreatureEntity) entities.get(0);

            target.addEffect(new EffectInstance(Effects.LUCK, 400));

            return true;
        }

        return false;
    }

    @Override
    public void postPollinate() {
        super.postPollinate();

        if (target instanceof SheepEntity) {
            ((SheepEntity) target).shear(SoundCategory.BLOCKS);
        }
    }
}
