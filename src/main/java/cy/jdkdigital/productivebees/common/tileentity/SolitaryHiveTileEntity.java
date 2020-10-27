package cy.jdkdigital.productivebees.common.tileentity;

public class SolitaryHiveTileEntity extends SolitaryNestTileEntity
{
    public SolitaryHiveTileEntity() {
        super();
    }

    protected boolean canRepopulate() {
        return false;
    }
}
