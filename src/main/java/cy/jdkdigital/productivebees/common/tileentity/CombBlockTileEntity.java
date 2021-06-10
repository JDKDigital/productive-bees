package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class CombBlockTileEntity extends TileEntity
{
    private String type;

    public CombBlockTileEntity() {
        this(null);
    }

    public CombBlockTileEntity(String type) {
        super(ModTileEntityTypes.COMB_BLOCK.get());
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
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(type);
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        this.type = tag.getString("type");
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putString("type", type);
        return super.save(tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.putString("type", type);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.type = tag.getString("type");
    }
}
