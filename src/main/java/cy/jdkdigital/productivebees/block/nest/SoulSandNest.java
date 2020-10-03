package cy.jdkdigital.productivebees.block.nest;

import cy.jdkdigital.productivebees.block.SolitaryNest;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

public class SoulSandNest extends SolitaryNest
{

    public SoulSandNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return dimension.isNether();
    }
}
