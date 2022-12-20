package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BottlerContainer extends AbstractContainer
{
    public final BottlerBlockEntity tileEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public BottlerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BottlerContainer(final int windowId, final Inventory playerInventory, final BottlerBlockEntity tileEntity) {
        super(ModContainerTypes.BOTTLER.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

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
            // Bottle slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 152, 17));

            // Output slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, 152, 53));
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static BottlerBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BottlerBlockEntity) {
            return (BottlerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Bottler && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tileEntity;
    }
}
