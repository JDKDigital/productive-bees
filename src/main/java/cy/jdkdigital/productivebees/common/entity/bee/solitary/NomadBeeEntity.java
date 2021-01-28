package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class NomadBeeEntity extends SolitaryBeeEntity
{
    public NomadBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.CACTUS || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        return flowerBlock.isIn(ModTags.ARID_FLOWERS);
    }

    @Override
    public String getRenderer() {
        return "slim";
    }
}
