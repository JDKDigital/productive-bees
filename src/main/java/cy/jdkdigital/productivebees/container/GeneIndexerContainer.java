package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.GeneIndexerBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class GeneIndexerContainer extends AbstractContainer<GeneIndexerBlockEntity>
{
    public GeneIndexerContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public GeneIndexerContainer(final int windowId, final Inventory playerInventory, final GeneIndexerBlockEntity blockEntity) {
        super(ModContainerTypes.GENE_INDEXER.get(), blockEntity, windowId);

        addSlotBox(this.getBlockEntity().getItemHandler(), 0, 12, 16, 13, 18, 8, 18);

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
}
