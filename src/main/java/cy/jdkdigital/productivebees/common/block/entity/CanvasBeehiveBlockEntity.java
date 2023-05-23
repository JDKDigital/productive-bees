package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.CanvasBeehive;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CanvasBeehiveBlockEntity extends AdvancedBeehiveBlockEntity implements CanvasBlockEntityInterface
{
    private final CanvasBeehive hiveBlock;
    private int color = 16777215;

    public CanvasBeehiveBlockEntity(CanvasBeehive hiveBlock, BlockPos pos, BlockState state) {
        super(hiveBlock.getBlockEntitySupplier().get(), pos, state);
        this.hiveBlock = hiveBlock;
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() {
        return hiveBlock.getBlockEntitySupplier().get();
    }

    public void setColor(int color) {
        this.color = color;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public int getColor(int tintIndex) {
        return color;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("color", color);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        }
    }
}
