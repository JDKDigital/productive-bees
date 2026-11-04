package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.BeeSquisherBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class BeeSquisherContainer extends AbstractContainer<BeeSquisherBlockEntity>
{
    public static final int SLOT_CAGE = 0;
    public static final int SLOT_BOTTLE = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_CAGE_OUTPUT = 3;

    public BeeSquisherContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public BeeSquisherContainer(final int windowId, final Inventory playerInventory, final BeeSquisherBlockEntity blockEntity) {
        super(ModContainerTypes.BEE_SQUISHER.get(), blockEntity, windowId);

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

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_CAGE, 52, 26));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_BOTTLE, 52, 48));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_OUTPUT, 108, 26));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_CAGE_OUTPUT, 108, 48));

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static BeeSquisherBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BeeSquisherBlockEntity) {
            return (BeeSquisherBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
