package cy.jdkdigital.productivebees.tileentity;

public class SolitaryHiveTileEntity extends SolitaryNestTileEntity {

    protected int MAX_BEES = 1;

    protected int MAX_EGGS = 9;

	public SolitaryHiveTileEntity() {
	    super();
	}

    @Override
    public int getMaxBees() {
        return MAX_BEES;
    }

    @Override
    public int getEggCapacity() {
        return MAX_EGGS;
    }
    protected boolean canRepopulate() {
	    return false;
    }
}
