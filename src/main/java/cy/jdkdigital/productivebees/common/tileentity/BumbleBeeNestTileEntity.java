package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;

public class BumbleBeeNestTileEntity extends SolitaryNestTileEntity
{
    public BumbleBeeNestTileEntity() {
        super(ModTileEntityTypes.BUMBLE_BEE_NEST.get());
        MAX_BEES = 3;
    }
}