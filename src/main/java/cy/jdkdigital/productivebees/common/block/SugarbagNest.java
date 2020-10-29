package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.SugarbagNestTileEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SugarbagNest extends BeehiveBlock
{
    public SugarbagNest(Properties properties) {
        super(properties);
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new SugarbagNestTileEntity();
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return ModTileEntityTypes.SUGARBAG_NEST.get().create();
    }
}
