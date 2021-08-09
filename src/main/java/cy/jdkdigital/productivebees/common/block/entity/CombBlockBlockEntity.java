package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class CombBlockBlockEntity extends BlockEntity
{
    private String type;

    public CombBlockBlockEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    public CombBlockBlockEntity(String type, BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.COMB_BLOCK.get(), pos, state);
        this.type = type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCombType() {
        return type;
    }

    public int getColor() {
        if (type != null) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(type);
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.type = tag.getString("type");
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("type", type);
        return super.save(tag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putString("type", type);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.type = tag.getString("type");
    }
}
