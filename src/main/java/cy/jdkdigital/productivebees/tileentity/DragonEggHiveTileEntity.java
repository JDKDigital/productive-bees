package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;

public class DragonEggHiveTileEntity extends AdvancedBeehiveTileEntityAbstract {

	public DragonEggHiveTileEntity() {
	    super(ModTileEntityTypes.DRACONIC_BEEHIVE.get());
        MAX_BEES = 3;
	}

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);
        // Generate item?
    }
}
