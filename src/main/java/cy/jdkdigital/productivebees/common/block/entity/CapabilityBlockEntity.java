package cy.jdkdigital.productivebees.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class CapabilityBlockEntity extends AbstractBlockEntity implements MenuProvider, Nameable
{
    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
            tag.putInt("energy", handler.getEnergyStored());
        });

        getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluid -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) fluid).serializeNBT();
            tag.put("fluid", compound);
        });

        if (this instanceof UpgradeableBlockEntity) {
            ((UpgradeableBlockEntity) this).getUpgradeHandler().ifPresent(inv -> {
                CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
                tag.put("upgrades", compound);
            });
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
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
}
