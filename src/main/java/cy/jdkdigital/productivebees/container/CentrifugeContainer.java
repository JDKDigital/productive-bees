package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.tileentity.CentrifugeTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.common.tileentity.PoweredCentrifugeTileEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModFluids;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

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

        trackIntArray(new IIntArray() {
            @Override
            public int get(int i) {
                return i == 0 ?
                        tileEntity.fluidId :
                        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).map(fluidHandler -> fluidHandler.getFluidInTank(0).getAmount()).orElse(0);
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        tileEntity.fluidId = value;
                    case 1:
                        tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
                            FluidStack fluid = fluidHandler.getFluidInTank(0);
                            if (fluid.isEmpty()) {
                                fluidHandler.fill(new FluidStack(Registry.FLUID.getByValue(tileEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                            } else {
                                fluid.setAmount(value);
                            }
                        });
                }
            }

            @Override
            public int size() {
                return 2;
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
