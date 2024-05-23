package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        if (type != null) {
            tag.putString("type", type);
        }
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("type")) {
            this.type = tag.getString("type");
        }
    }
}
