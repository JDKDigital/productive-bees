package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.container.PoweredCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoweredCentrifugeBlockEntity extends CentrifugeBlockEntity
{
    public LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(10000));

    public PoweredCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), pos, state);
    }

    public PoweredCentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredCentrifugeBlockEntity blockEntity) {
        CentrifugeBlockEntity.tick(level, pos, state, blockEntity);
        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            blockEntity.energyHandler.ifPresent(handler -> {
                handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
            });
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = 1D + (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(1, timeUpgradeModifier);
    }

    @Override
    protected double getProcessingTimeModifier() {
        return super.getProcessingTimeModifier() / 3;
    }

    protected boolean canOperate() {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        return energy >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.POWERED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new PoweredCentrifugeContainer(windowId, playerInventory, this);
    }
}
