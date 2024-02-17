package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.CryoStasis;
import cy.jdkdigital.productivebees.common.block.entity.CryoStasisBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
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

public class CryoStasisContainer extends AbstractContainer
{
    public final CryoStasisBlockEntity blockEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public CryoStasisContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CryoStasisContainer(final int windowId, final Inventory playerInventory, final CryoStasisBlockEntity blockEntity) {
        this(ModContainerTypes.CRYO_STASIS.get(), windowId, playerInventory, blockEntity);
    }

    public CryoStasisContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final CryoStasisBlockEntity blockEntity) {
        super(type, windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, CryoStasisBlockEntity.SLOT_INPUT, 108, 18));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, CryoStasisBlockEntity.SLOT_CAGE, 108, 36));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, CryoStasisBlockEntity.SLOT_OUT, 108, 54));
        });

        layoutPlayerInventorySlots(playerInventory, 0, 108, 84);
    }

    private static CryoStasisBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CryoStasisBlockEntity) {
            return (CryoStasisBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof CryoStasis && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
