package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import javax.annotation.Nullable;
import java.util.Objects;

public class CentrifugeContainer<T extends CentrifugeBlockEntity> extends AbstractContainer<T>
{
    public CentrifugeContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, (T)getBlockEntity(playerInventory, data));
    }

    public CentrifugeContainer(final int windowId, final Inventory playerInventory, final T blockEntity) {
        this(ModContainerTypes.CENTRIFUGE.get(), windowId, playerInventory, blockEntity);
    }

    public CentrifugeContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final T blockEntity) {
        super(type, blockEntity, windowId);

        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ?
                        blockEntity.fluidId :
                        blockEntity.fluidHandler.getAmountAsInt(0);
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
                        }
                        else {
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

        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.recipeProgress;
            }

            @Override
            public void set(int value) {
                blockEntity.recipeProgress = value;
            }
        });

        // Comb slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.INPUT_SLOT, 26, 35));

        // Inventory slots
        addSlotBox(this.getBlockEntity().getItemHandler(), InventoryHandlerHelper.OUTPUT_SLOTS[0], 80, 17, 3, 18, 3, 18);

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static CentrifugeBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CentrifugeBlockEntity) {
            return (CentrifugeBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
