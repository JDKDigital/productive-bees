package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Feeder;
import cy.jdkdigital.productivebees.common.tileentity.FeederTileEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class FeederContainer extends AbstractContainer
{
    public final FeederTileEntity tileEntity;

    private final IWorldPosCallable canInteractWithCallable;

    public FeederContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public FeederContainer(final int windowId, final PlayerInventory playerInventory, final FeederTileEntity tileEntity) {
        super(ModContainerTypes.FEEDER.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addSlotBox(inv, 0, 61, 34, 3, 18, 1, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static FeederTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof FeederTileEntity) {
            return (FeederTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Feeder && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
