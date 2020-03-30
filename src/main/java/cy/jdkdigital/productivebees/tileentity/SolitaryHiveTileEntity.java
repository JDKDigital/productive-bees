package cy.jdkdigital.productivebees.tileentity;

public class SolitaryHiveTileEntity extends SolitaryNestTileEntity {

    protected int MAX_BEES = 1;

	public SolitaryHiveTileEntity() {
	    super();
        MAX_EGGS = 9;
	}

    protected boolean canRepopulate() {
	    return false;
    }
}
