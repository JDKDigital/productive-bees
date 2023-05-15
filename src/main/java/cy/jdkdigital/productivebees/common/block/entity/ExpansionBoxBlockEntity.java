package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExpansionBoxBlockEntity extends BlockEntity
{
    public ExpansionBoxBlockEntity(ExpansionBox box, BlockPos pos, BlockState state) {
        super(box.getBlockEntitySupplier().get(), pos, state);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!getBlockState().getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
            Pair<Pair<BlockPos, Direction>, BlockState> pair = ExpansionBox.getAdjacentHive(level, worldPosition);
            if (pair != null) {
                Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
                BlockPos hivePos = posAndDirection.getLeft();
                return level.getBlockEntity(hivePos).getCapability(cap, side);
            }
        }
        return super.getCapability(cap, side);
    }
}
