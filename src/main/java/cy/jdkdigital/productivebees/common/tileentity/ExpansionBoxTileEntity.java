package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExpansionBoxTileEntity extends TileEntity
{
    public ExpansionBoxTileEntity() {
        super(ModTileEntityTypes.EXPANSION_BOX.get());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!getBlockState().get(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
            Pair<Pair<BlockPos, Direction>, BlockState> pair = ExpansionBox.getAdjacentHive(world, pos);
            if (pair != null) {
                Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
                BlockPos hivePos = posAndDirection.getLeft();
                return world.getTileEntity(hivePos).getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }
}
