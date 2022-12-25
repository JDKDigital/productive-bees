package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class CombBlockBlockEntity extends AbstractBlockEntity
{
    private String type;

    public CombBlockBlockEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    public CombBlockBlockEntity(String type, BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COMB_BLOCK.get(), pos, state);
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
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        if (type != null) {
            tag.putString("type", type);
        }
    }

    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
        if (tag.contains("type")) {
            this.type = tag.getString("type");
        }
    }
}
