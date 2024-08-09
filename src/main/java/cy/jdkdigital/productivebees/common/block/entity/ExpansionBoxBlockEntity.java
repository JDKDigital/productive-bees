package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.ExpansionBox;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

public class ExpansionBoxBlockEntity extends BlockEntity
{
    public ExpansionBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.EXPANSION_BOX.get(), pos, state);
    }

    public ExpansionBoxBlockEntity(BlockEntityType<?> pType, BlockPos pos, BlockState state) {
        super(pType, pos, state);
    }

    public IItemHandlerModifiable getHiveInventoryHandler() {
        if (level != null && !getBlockState().getValue(AdvancedBeehive.EXPANDED).equals(VerticalHive.NONE)) {
            Pair<Pair<BlockPos, Direction>, BlockState> pair = ExpansionBox.getAttachedHive(getBlockState(), level, getBlockPos());
            if (pair != null) {
                Pair<BlockPos, Direction> posAndDirection = pair.getLeft();
                BlockPos hivePos = posAndDirection.getLeft();
                BlockEntity hiveTileEntity = level.getBlockEntity(hivePos);
                if (hiveTileEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity) {
                    return advancedBeehiveBlockEntity.inventoryHandler;
                }
            }
        }
        return null;
    }
}
