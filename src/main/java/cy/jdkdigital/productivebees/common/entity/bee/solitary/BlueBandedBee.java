package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BlueBandedBee extends SolitaryBee
{
    public BlueBandedBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 0);
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
    public boolean isFlowerBlock(Block flowerBlock) {
        return ModTags.RIVER_FLOWERS.contains(flowerBlock);
    }
}
