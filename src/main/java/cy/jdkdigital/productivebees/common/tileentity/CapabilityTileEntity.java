package cy.jdkdigital.productivebees.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CapabilityTileEntity extends TileEntity
{
    public CapabilityTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        if (tag.contains("inv")) {
            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(tag.getCompound("inv")));
        }

        if (tag.contains("energy")) {
            getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                handler.extractEnergy(handler.getEnergyStored(), false);
                handler.receiveEnergy(tag.getInt("energy"), false);
            });
        }

        if (tag.contains("fluid")) {
            getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> ((INBTSerializable<CompoundNBT>) fluid).deserializeNBT(tag.getCompound("fluid")));
        }

        if (tag.contains("upgrades") && this instanceof UpgradeableTileEntity) {
            ((UpgradeableTileEntity) this).getUpgradeHandler().ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(tag.getCompound("upgrades")));
        }
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag = super.save(tag);

        CompoundNBT finalTag = tag;
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            finalTag.put("inv", compound);
        });

        getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            finalTag.putInt("energy", handler.getEnergyStored());
        });

        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) fluid).serializeNBT();
            finalTag.put("fluid", compound);
        });

        if (this instanceof UpgradeableTileEntity) {
            ((UpgradeableTileEntity) this).getUpgradeHandler().ifPresent(inv -> {
                CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
                finalTag.put("upgrades", compound);
            });
        }

        return finalTag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(null, pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        deserializeNBT(tag);
    }
}
