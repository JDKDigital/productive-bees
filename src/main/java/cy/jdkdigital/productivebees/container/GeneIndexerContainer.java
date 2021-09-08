package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.GeneIndexer;
import cy.jdkdigital.productivebees.common.tileentity.GeneIndexerTileEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GeneIndexerContainer extends AbstractContainer
{
    public final GeneIndexerTileEntity tileEntity;

    private final IWorldPosCallable canInteractWithCallable;

    public GeneIndexerContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public GeneIndexerContainer(final int windowId, final PlayerInventory playerInventory, final GeneIndexerTileEntity tileEntity) {
        super(ModContainerTypes.GENE_INDEXER.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addSlotBox(inv, 0, 12, 16, 13, 18, 8, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, 48, 174);
    }

    private static GeneIndexerTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof GeneIndexerTileEntity) {
            return (GeneIndexerTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof GeneIndexer && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
