package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class ResinBee extends SolitaryBee
{
    public PathfinderMob target = null;

    public static Predicate<Entity> predicate = (entity -> entity instanceof PathfinderMob && !entity.getType().is(ModTags.BEE_ENCASE_BLACKLIST));

    public ResinBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.FOREST_FLOWERS);
    }

    @Override
    public TagKey<Block> getNestingTag() {
        return ModTags.WOOD_NESTS;
    }

    @Override
    public String getRenderer() {
        return "small";
    }

    @Override
    public boolean isFlowerValid(BlockPos pos) {
        if (!level.isLoaded(pos)) {
            return false;
        }

        List<Entity> entities = level.getEntities(this, (new AABB(pos).inflate(1.0D, 1.0D, 1.0D)), predicate);
        if (!entities.isEmpty()) {
            target = (PathfinderMob) entities.get(0);

            target.addEffect(new MobEffectInstance(MobEffects.LUCK, 400));

            return true;
        }

        return isFlowerBlock(level.getBlockState(pos));
    }

    @Override
    public void postPollinate() {
        super.postPollinate();
        BeeHelper.encaseMob(target, level, this.getDirection());
    }
}
