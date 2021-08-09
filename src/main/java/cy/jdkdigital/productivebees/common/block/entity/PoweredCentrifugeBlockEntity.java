package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.container.PoweredCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoweredCentrifugeBlockEntity extends CentrifugeBlockEntity
{
    public LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(10000));

    public PoweredCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.POWERED_CENTRIFUGE.get(), pos, state);
    }

    public void tick(Level level, BlockState state) {
        super.tick(level, state);
        if (getBlockState().getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            energyHandler.ifPresent(handler -> {
                handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * getEnergyConsumptionModifier()), false);
            });
        }
    }

    protected double getEnergyConsumptionModifier() {
        double combBlockUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) * ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
        double timeUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get();

        return Math.max(1, timeUpgradeModifier + combBlockUpgradeModifier);
    }

    public int getProcessingTime() {
        return (int) (
                ProductiveBeesConfig.GENERAL.centrifugePoweredProcessingTime.get() * getProcessingTimeModifier()
        );
    }

    protected boolean canOperate() {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
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
    public Component getName() {
        return new TranslatableComponent(ModBlocks.POWERED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new PoweredCentrifugeContainer(windowId, playerInventory, this);
    }
}
