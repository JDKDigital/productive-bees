package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Bottler;
import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BottlerContainer extends AbstractContainer
{
    public final BottlerBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public BottlerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BottlerContainer(final int windowId, final Inventory playerInventory, final BottlerBlockEntity blockEntity) {
        super(ModContainerTypes.BOTTLER.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ? blockEntity.fluidId : blockEntity.fluidHandler.getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        blockEntity.fluidId = value;
                    case 1:
                        FluidStack fluid = blockEntity.fluidHandler.getFluidInTank(0);
                        if (fluid.isEmpty()) {
                            blockEntity.fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(blockEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
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

            // Bottle slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, InventoryHandlerHelper.BOTTLE_SLOT, 152, 17));

        // Output slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, 152, 53));

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static BottlerBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BottlerBlockEntity) {
            return (BottlerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Bottler && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
