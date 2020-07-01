package cy.jdkdigital.productivebees.block.nest;

import cy.jdkdigital.productivebees.block.SolitaryNest;

public class GravelNest extends SolitaryNest
{
    public GravelNest(Properties properties) {
        super(properties);
    }

//    @Override
//    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
//        return (biome.getCategory().equals(Biome.Category.RIVER) || biome.getCategory().equals(Biome.Category.BEACH)) && biome.getTempCategory() != Biome.TempCategory.COLD;
//    }
}
