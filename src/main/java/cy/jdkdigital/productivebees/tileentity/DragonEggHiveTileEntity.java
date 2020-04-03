package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

import java.util.Iterator;
import java.util.List;

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
