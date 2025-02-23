package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class CombBlockBlockEntity extends AbstractBlockEntity
{
    private ResourceLocation combType;

    public CombBlockBlockEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    public CombBlockBlockEntity(ResourceLocation combType, BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COMB_BLOCK.get(), pos, state);
        this.combType = combType;
    }

    public void setCombType(ResourceLocation combType) {
        this.combType = combType;
    }

    public ResourceLocation getCombType() {
        return combType;
    }

    public int getColor() {
        if (combType != null) {
            CompoundTag nbt = BeeReloadListener.INSTANCE.getData(combType);
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        if (combType != null) {
            tag.putString("type", combType.toString());
        }
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("type")) {
            setCombType(ResourceLocation.parse(tag.getString("type")));
        }
    }
}
