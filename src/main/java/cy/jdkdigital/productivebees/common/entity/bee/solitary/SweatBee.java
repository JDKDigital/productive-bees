package cy.jdkdigital.productivebees.common.entity.bee.solitary;

import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SweatBee extends SolitaryBee
{
    public SweatBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        beeAttributes.put(BeeAttributes.TEMPER, 2);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModTags.SNOW_FLOWERS);
    }

    @Override
    public Tag<Block> getNestingTag() {
        return ModTags.COLD_NESTS;
    }

    @Override
    public String getRenderer() {
        return "slim";
    }
}
