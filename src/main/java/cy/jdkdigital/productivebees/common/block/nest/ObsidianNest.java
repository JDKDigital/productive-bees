package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import net.minecraft.world.World;

public class ObsidianNest extends SolitaryNest
{

    public ObsidianNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(World world) {
        return world.getDimensionKey() == World.THE_END;
    }
}
