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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PoweredCentrifugeBlockEntity extends CentrifugeBlockEntity
{
    public IEnergyStorage energyHandler = new EnergyStorage(10000);

    public PoweredCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), pos, state);
    }

    public PoweredCentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredCentrifugeBlockEntity blockEntity) {
        CentrifugeBlockEntity.tick(level, pos, state, blockEntity);
        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            blockEntity.energyHandler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
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
        return energyHandler.getEnergyStored() >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get();
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.POWERED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory) {
        return new PoweredCentrifugeContainer(windowId, playerInventory, this);
    }
}
