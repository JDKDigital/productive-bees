package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.tileentity.CentrifugeTileEntity;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.tileentity.PoweredCentrifugeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CentrifugeContainer extends AbstractContainer
{
    public final CentrifugeTileEntity tileEntity;

    public final IWorldPosCallable canInteractWithCallable;

    public CentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final CentrifugeTileEntity tileEntity) {
        this(ModContainerTypes.CENTRIFUGE.get(), windowId, playerInventory, tileEntity);
    }

    public CentrifugeContainer(@Nullable ContainerType<?> type, final int windowId, final PlayerInventory playerInventory, final CentrifugeTileEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        // Honey
        trackInt(new IntReferenceHolder()
        {
            @Override
            public int get() {
                return tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(fluidHandler -> fluidHandler.getFluidInTank(0).getAmount()).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                    FluidStack fluid = fluidHandler.getFluidInTank(0);
                    if (fluid.isEmpty()) {
                        fluidHandler.fill(new FluidStack(ModFluids.HONEY.get(), value), IFluidHandler.FluidAction.EXECUTE);
                    } else {
                        fluid.setAmount(value);
                    }
                });
            }
        });

        trackInt(new IntReferenceHolder()
        {
            @Override
            public int get() {
                return tileEntity.recipeProgress;
            }

            @Override
            public void set(int value) {
                tileEntity.recipeProgress = value;
            }
        });

        IItemHandler inventory = new InvWrapper(playerInventory);

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            // Comb and bottle slots
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 152, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.INPUT_SLOT, 26, 35));

            // Inventory slots
            addSlotBox(inv, InventoryHandlerHelper.OUTPUT_SLOTS[0], 80, 17, 3, 18, 3, 18);
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, 152, 53));
        });

        layoutPlayerInventorySlots(inventory, 0, 8, 84);
    }

    private static CentrifugeTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof PoweredCentrifugeTileEntity) {
            return (PoweredCentrifugeTileEntity) tileAtPos;
        }
        if (tileAtPos instanceof CentrifugeTileEntity) {
            return (CentrifugeTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Centrifuge && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }
}
