package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MiningBee extends SolitaryBee
{
    public MiningBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source.equals(this.level.damageSources().cactus()) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.ARID_FLOWERS);
    }
}
