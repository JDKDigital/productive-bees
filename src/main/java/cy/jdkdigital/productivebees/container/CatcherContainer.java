package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Catcher;
import cy.jdkdigital.productivebees.common.block.entity.CatcherBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CatcherContainer extends AbstractContainer
{
    public final CatcherBlockEntity tileEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public CatcherContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CatcherContainer(final int windowId, final Inventory playerInventory, final CatcherBlockEntity tileEntity) {
        this(ModContainerTypes.CATCHER.get(), windowId, playerInventory, tileEntity);
    }

    public CatcherContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final CatcherBlockEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        this.tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 13, 35));

            // Inventory slots
            addSlotBox(inv, InventoryHandlerHelper.OUTPUT_SLOTS[0], 67, 17, 3, 18, 3, 18);
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
    }

    private static CatcherBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CatcherBlockEntity) {
            return (CatcherBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Catcher && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return tileEntity;
    }
}
