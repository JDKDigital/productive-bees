package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.IncubatorBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class IncubatorContainer extends AbstractContainer<IncubatorBlockEntity>
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_CATALYST = 1;
    public static final int SLOT_OUTPUT = 2;

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final IncubatorBlockEntity blockEntity) {
        super(ModContainerTypes.INCUBATOR.get(), blockEntity, windowId);

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.energyHandler.getAmountAsInt();
            }

            @Override
            public void set(int value) {
                blockEntity.energyHandler.set(value);
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

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_INPUT, 52, 35));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_CATALYST, 80, 53));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_OUTPUT, 108, 35));

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static IncubatorBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof IncubatorBlockEntity) {
            return (IncubatorBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
