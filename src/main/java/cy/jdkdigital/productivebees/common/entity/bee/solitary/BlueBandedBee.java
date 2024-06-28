package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlueBandedBee extends SolitaryBee
{
    public BlueBandedBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        setAttributeValue(GeneAttribute.TEMPER, GeneValue.TEMPER_PASSIVE);
    }

    public static AttributeSupplier.Builder getDefaultAttributes() {
        return Bee.createAttributes()
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
