package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.entity.FeederBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class FeederContainer extends AbstractContainer<FeederBlockEntity>
{
    public FeederContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public FeederContainer(final int windowId, final Inventory playerInventory, final FeederBlockEntity blockEntity) {
        super(ModContainerTypes.FEEDER.get(), blockEntity, windowId);

        addSlotBox(this.getBlockEntity().getItemHandler(), 0, 62, blockEntity.isDouble() ? 26 : 35, 3, 18, blockEntity.isDouble() ? 2 : 1, 18);

        layoutPlayerInventorySlots(playerInventory, 0, 8, 84);
    }

    private static FeederBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof FeederBlockEntity) {
            return (FeederBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }
}
