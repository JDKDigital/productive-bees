package cy.jdkdigital.productivebees.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CapabilityBlockEntity extends BlockEntity implements MenuProvider, Nameable
{
    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("inv")) {
            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(tag.getCompound("inv")));
        }

        if (tag.contains("energy")) {
            getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                handler.extractEnergy(handler.getEnergyStored(), false);
                handler.receiveEnergy(tag.getInt("energy"), false);
            });
        }

        if (tag.contains("fluid")) {
            getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> ((INBTSerializable<CompoundTag>) fluid).deserializeNBT(tag.getCompound("fluid")));
        }

        if (tag.contains("upgrades") && this instanceof UpgradeableBlockEntity) {
            ((UpgradeableBlockEntity) this).getUpgradeHandler().ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(tag.getCompound("upgrades")));
        }
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag = super.save(tag);

        CompoundTag finalTag = tag;
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            finalTag.put("inv", compound);
        });

        getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            finalTag.putInt("energy", handler.getEnergyStored());
        });

        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) fluid).serializeNBT();
            finalTag.put("fluid", compound);
        });

        if (this instanceof UpgradeableBlockEntity) {
            ((UpgradeableBlockEntity) this).getUpgradeHandler().ifPresent(inv -> {
                CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
                finalTag.put("upgrades", compound);
            });
        }

        return finalTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.getBlockPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        deserializeNBT(tag);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }
}
