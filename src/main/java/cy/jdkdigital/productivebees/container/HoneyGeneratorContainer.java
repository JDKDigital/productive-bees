package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.HoneyGenerator;
import cy.jdkdigital.productivebees.common.block.entity.HoneyGeneratorBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class HoneyGeneratorContainer extends AbstractContainer
{
    public final HoneyGeneratorBlockEntity tileEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public HoneyGeneratorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public HoneyGeneratorContainer(final int windowId, final Inventory playerInventory, final HoneyGeneratorBlockEntity tileEntity) {
        this(ModContainerTypes.HONEY_GENERATOR.get(), windowId, playerInventory, tileEntity);
    }

    public HoneyGeneratorContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final HoneyGeneratorBlockEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(ForgeCapabilities.ENERGY).ifPresent(handler -> {
                    if (handler.getEnergyStored() > 0) {
                        handler.extractEnergy(handler.getEnergyStored(), false);
                    }
                    if (value > 0) {
                        handler.receiveEnergy(value, false);
                    }
                });
            }
        });

        // Fluid
        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ?
                        tileEntity.fluidId :
                        tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).map(fluidHandler -> fluidHandler.getFluidInTank(0).getAmount()).orElse(0);
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        tileEntity.fluidId = value;
                    case 1:
                        tileEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
                            FluidStack fluid = fluidHandler.getFluidInTank(0);
                            if (fluid.isEmpty()) {
                                fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(tileEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                            } else {
                                fluid.setAmount(value);
                            }
                        });
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            // Input and output slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) inv, 0, 139, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) inv, 1, 139, 53));
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
    }

    private static HoneyGeneratorBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof HoneyGeneratorBlockEntity) {
            return (HoneyGeneratorBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof HoneyGenerator && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return tileEntity;
    }
}
