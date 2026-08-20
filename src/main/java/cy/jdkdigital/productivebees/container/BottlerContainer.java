package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.BottlerBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Objects;

public class BottlerContainer extends AbstractContainer<BottlerBlockEntity>
{
    public BottlerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BottlerContainer(final int windowId, final Inventory playerInventory, final BottlerBlockEntity blockEntity) {
        super(ModContainerTypes.BOTTLER.get(), blockEntity, windowId);

        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ? blockEntity.fluidId : blockEntity.fluidHandler.getAmountAsInt(0);
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        blockEntity.fluidId = value;
                        break;
                    case 1:
                        if (value <= 0) {
                            blockEntity.fluidHandler.set(0, FluidResource.EMPTY, 0);
                            break;
                        }
                        FluidResource resource = blockEntity.fluidHandler.getResource(0);
                        if (resource.isEmpty()) {
                            if (blockEntity.fluidId <= 0) {
                                break;
                            }
                            blockEntity.fluidHandler.set(0, FluidResource.of(BuiltInRegistries.FLUID.byIdOrThrow(blockEntity.fluidId)), value);
                        } else {
                            blockEntity.fluidHandler.set(0, resource, value);
                        }
                        break;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        // Bottle slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.BOTTLE_SLOT, 152, 17));

        // Output slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, 152, 53));

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
}
