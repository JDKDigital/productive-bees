package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.BreedingChamberBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class BreedingChamberContainer extends AbstractContainer<BreedingChamberBlockEntity>
{
    public final static int SLOT_CAGE = 0;
    public final static int SLOT_BEE_1 = 1;
    public final static int SLOT_BEE_2 = 2;
    public final static int SLOT_BREED_ITEM_1 = 3;
    public final static int SLOT_BREED_ITEM_2 = 4;
    public static final int SLOT_OUTPUT = 5;

    public BreedingChamberContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BreedingChamberContainer(final int windowId, final Inventory playerInventory, final BreedingChamberBlockEntity blockEntity) {
        super(ModContainerTypes.BREEDING_CHAMBER.get(), blockEntity, windowId);

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

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_CAGE, 134, 41));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_BEE_1, 26, 17));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_BEE_2, 62, 17));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_BREED_ITEM_1, 26, 37));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_BREED_ITEM_2, 62, 37));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.getBlockEntity().getItemHandler(), SLOT_OUTPUT, 152, 41));

        addSlotBox(this.getBlockEntity().getUpgradeHandler(), 0, 178, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static BreedingChamberBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BreedingChamberBlockEntity) {
            return (BreedingChamberBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
