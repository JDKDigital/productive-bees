package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SweatBee extends SolitaryBee
{
    public SweatBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        setAttributeValue(GeneAttribute.TEMPER, GeneValue.TEMPER_AGGRESSIVE);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.SNOW_FLOWERS);
    }

    @Override
    public TagKey<Block> getNestingTag() {
        return ModTags.COLD_NESTS;
    }

    @Override
    public String getRenderer() {
        return "slim";
    }
}
