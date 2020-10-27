package cy.jdkdigital.productivebees.common.block.nest;

import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

public class SnowNest extends SolitaryNest
{
    public SnowNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return biome.getCategory().equals(Biome.Category.EXTREME_HILLS) || biome.getTempCategory() == Biome.TempCategory.COLD;
    }
}
