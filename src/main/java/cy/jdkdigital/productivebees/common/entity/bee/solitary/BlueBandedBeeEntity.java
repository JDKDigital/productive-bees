package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;

public class BlueBandedBeeEntity extends SolitaryBeeEntity
{
    public BlueBandedBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 0);
    }

    public static AttributeModifierMap.MutableAttribute getDefaultAttributes() {
        return ProductiveBeeEntity.getDefaultAttributes()
                .add(Attributes.FLYING_SPEED, 0.75D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    public String getRenderer() {
        return "small";
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.RIVER_FLOWERS);
    }
}
