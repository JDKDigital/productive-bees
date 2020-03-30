package cy.jdkdigital.productivebees.entity.bee;

import net.minecraft.block.Block;
import net.minecraft.tags.Tag;

public interface IBeeEntity {

    static Tag<Block> getNestBlockTag() {
        return null;
    }
}
