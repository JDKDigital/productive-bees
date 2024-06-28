package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.GeneIndexer;
import cy.jdkdigital.productivebees.common.block.entity.GeneIndexerBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GeneIndexerContainer extends AbstractContainer
{
    public final GeneIndexerBlockEntity blockEntity;

    private final ContainerLevelAccess canInteractWithCallable;

    public GeneIndexerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public GeneIndexerContainer(final int windowId, final Inventory playerInventory, final GeneIndexerBlockEntity blockEntity) {
        super(ModContainerTypes.GENE_INDEXER.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlotBox(this.blockEntity.inventoryHandler, 0, 12, 16, 13, 18, 8, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 48, 174);
    }

    private static GeneIndexerBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof GeneIndexerBlockEntity) {
            return (GeneIndexerBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof GeneIndexer && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
