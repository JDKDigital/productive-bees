package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.PoweredCentrifugeTileEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PoweredCentrifuge extends Centrifuge
{
    public PoweredCentrifuge(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.POWERED_CENTRIFUGE.get().create();
    }

    @Nullable
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new PoweredCentrifugeTileEntity();
    }
}