package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.container.PoweredCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoweredCentrifugeTileEntity extends CentrifugeTileEntity
{
    public LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(1000));

    public PoweredCentrifugeTileEntity() {
        super(ModTileEntityTypes.POWERED_CENTRIFUGE.get());
    }

    @Override
    public void tick() {
        super.tick();
        if (getBlockState().get(Centrifuge.RUNNING)) {
            energyHandler.ifPresent(handler -> {
                handler.extractEnergy(ProductiveBeesConfig.GENERAL.centrifugePowerUse.get(), false);
            });
        }
    }

    public int getProcessingTime() {
        return ProductiveBeesConfig.GENERAL.centrifugePoweredProcessingTime.get();
    }

    protected boolean canOperate() {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        ProductiveBees.LOGGER.info("canProcessRecipe: " + energy);
        return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket(){
        CompoundNBT tag = new CompoundNBT();

        energyHandler.ifPresent(handler -> {
            tag.putInt("energy", handler.getEnergyStored());
        });

        return new SUpdateTileEntityPacket(getPos(), -1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        CompoundNBT tag = pkt.getNbtCompound();

        if (tag.contains("energy")) {
            energyHandler.ifPresent(handler -> {
                handler.receiveEnergy(tag.getInt("input"), false);
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.POWERED_CENTRIFUGE.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new PoweredCentrifugeContainer(windowId, playerInventory, this);
    }
}
