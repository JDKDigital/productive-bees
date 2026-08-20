package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.container.PoweredCentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;

public class PoweredCentrifugeBlockEntity extends CentrifugeBlockEntity
{
    public SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(10000);

    public PoweredCentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.POWERED_CENTRIFUGE.get(), pos, state);
    }

    public PoweredCentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PoweredCentrifugeBlockEntity blockEntity) {
        CentrifugeBlockEntity.tick(level, pos, state, blockEntity);
        if (state.getValue(Centrifuge.RUNNING) && level instanceof ServerLevel) {
            try (Transaction tx = Transaction.openRoot()) {
                blockEntity.energyHandler.extract((int) (ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * blockEntity.getEnergyConsumptionModifier()), tx);
                tx.commit();
            }
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = 1D + (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(1, timeUpgradeModifier);
    }

    @Override
    protected double getProcessingTimeModifier() {
        return super.getProcessingTimeModifier() / 3;
    }

    protected boolean canOperate() {
        return energyHandler.getAmountAsInt() >= ProductiveBeesConfig.GENERAL.centrifugePowerUse.get() * getEnergyConsumptionModifier();
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.POWERED_CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PoweredCentrifugeContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }
}
