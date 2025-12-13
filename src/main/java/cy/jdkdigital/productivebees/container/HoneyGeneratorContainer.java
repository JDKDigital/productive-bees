package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.HoneyGeneratorBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Objects;

public class HoneyGeneratorContainer extends AbstractContainer<HoneyGeneratorBlockEntity>
{
    public HoneyGeneratorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public HoneyGeneratorContainer(final int windowId, final Inventory playerInventory, final HoneyGeneratorBlockEntity tileEntity) {
        super(ModContainerTypes.HONEY_GENERATOR.get(), tileEntity, windowId);

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.energyHandler.getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (tileEntity.energyHandler.getEnergyStored() > 0) {
                    tileEntity.energyHandler.extractEnergy(tileEntity.energyHandler.getEnergyStored(), false);
                }
                if (value > 0) {
                    tileEntity.energyHandler.receiveEnergy(value, false);
                }
            }
        });

        // Fluid
        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ?
                        tileEntity.fluidId :
                        tileEntity.fluidHandler.getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        tileEntity.fluidId = value;
                    case 1:
                        FluidStack fluid = tileEntity.fluidHandler.getFluidInTank(0);
                        if (fluid.isEmpty()) {
                            tileEntity.fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(tileEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                        } else {
                            fluid.setAmount(value);
                        }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        // Input and output slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), 0, 152, 17));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), 1, 152, 53));

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static HoneyGeneratorBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof HoneyGeneratorBlockEntity) {
            return (HoneyGeneratorBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
