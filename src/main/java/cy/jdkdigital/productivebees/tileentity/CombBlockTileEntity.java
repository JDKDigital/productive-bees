package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

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
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(type));
            if (nbt != null) {
                return nbt.getInt("primaryColor");
            }
        }
        return 0;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.type = tag.getString("type");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putString("type", type);
        return super.write(tag);
    }
}
