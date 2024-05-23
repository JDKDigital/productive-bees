package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Incubator;
import cy.jdkdigital.productivebees.common.block.entity.IncubatorBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class IncubatorContainer extends AbstractContainer
{
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_CATALYST = 1;
    public static final int SLOT_OUTPUT = 2;
    public final IncubatorBlockEntity blockEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public IncubatorContainer(final int windowId, final Inventory playerInventory, final IncubatorBlockEntity blockEntity) {
        this(ModContainerTypes.INCUBATOR.get(), windowId, playerInventory, blockEntity);
    }

    public IncubatorContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final IncubatorBlockEntity blockEntity) {
        super(type, windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.energyHandler.getEnergyStored();
            }

            @Override
            public void set(int value) {
                if (blockEntity.energyHandler.getEnergyStored() > 0) {
                    blockEntity.energyHandler.extractEnergy(blockEntity.energyHandler.getEnergyStored(), false);
                }
                if (value > 0) {
                    blockEntity.energyHandler.receiveEnergy(value, false);
                }
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

        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SLOT_INPUT, 52 - 13, 35));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SLOT_CATALYST, 80 - 13, 53));
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SLOT_OUTPUT, 108 - 13, 35));

        addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 165, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
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

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Incubator && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
