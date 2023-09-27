package cy.jdkdigital.productivebees.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class FluidTankBlockEntity extends CapabilityBlockEntity
{
    private int tankTick = 0;

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        if (++blockEntity.tankTick > 21) {
            blockEntity.tankTick = 0;
            blockEntity.tickFluidTank(level, pos, state, blockEntity);
        }
    }

    abstract void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity);
}
