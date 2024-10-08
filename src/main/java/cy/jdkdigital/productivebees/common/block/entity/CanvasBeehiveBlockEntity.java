package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class CanvasBeehiveBlockEntity extends AdvancedBeehiveBlockEntity implements CanvasBlockEntityInterface
{
    private int color = 16777215;

    public CanvasBeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CANVAS_ADVANCED_HIVE.get(), pos, state);
    }

    public void setColor(int color) {
        this.color = color;
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    public int getColor(int tintIndex) {
        return color;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("color", color);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("color")) {
            this.setColor(tag.getInt("color"));
        }
    }
}
