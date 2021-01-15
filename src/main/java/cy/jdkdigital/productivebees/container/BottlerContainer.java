package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.tileentity.BottlerTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.init.ModFluids;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BottlerContainer extends AbstractContainer
{
    public final BottlerTileEntity tileEntity;

    private final IWorldPosCallable canInteractWithCallable;

    public BottlerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BottlerContainer(final int windowId, final PlayerInventory playerInventory, final BottlerTileEntity tileEntity) {
        super(ModContainerTypes.BOTTLER.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

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

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            // Bottle slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 152, 17));

            // Output slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, 152, 53));
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static BottlerTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof BottlerTileEntity) {
            return (BottlerTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Bottler && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
