package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import net.minecraft.world.World;

public class SoulSandNest extends SolitaryNest
{

    public SoulSandNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(World world) {
        return world.getDimensionKey() == World.THE_NETHER;
    }
}
