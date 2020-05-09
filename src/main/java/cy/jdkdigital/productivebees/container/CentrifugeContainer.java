package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.tileentity.CentrifugeTileEntity;
import cy.jdkdigital.productivebees.tileentity.ItemHandlerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CentrifugeContainer extends AbstractContainer {

	public final CentrifugeTileEntity tileEntity;

	private final IWorldPosCallable canInteractWithCallable;

	public CentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	public CentrifugeContainer(final int windowId, final PlayerInventory playerInventory, final CentrifugeTileEntity tileEntity) {
		super(ModContainerTypes.CENTRIFUGE.get(), windowId);

		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

		trackInt(new IntReferenceHolder() {
			@Override
			public int get() {
				return tileEntity.recipeProgress;
			}

			@Override
			public void set(int value) {
				tileEntity.recipeProgress = value;
			}
		});

		IItemHandler inventory = new InvWrapper(playerInventory);

		// Comb and bottle slots
		this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null, true).ifPresent(input -> {
			addSlot(new SlotItemHandler(input, ItemHandlerHelper.BOTTLE_SLOT, 76, 17));
			addSlot(new SlotItemHandler(input, ItemHandlerHelper.INPUT_SLOT, 38, 35));
		});
		// Inventory slots
		this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
			addSlotBox(inv, ItemHandlerHelper.OUTPUT_SLOTS[0], 116, 17, 3, 18, 3, 18);
		});

		layoutPlayerInventorySlots(inventory, 0, 8, 84);
	}

	private static CentrifugeTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof CentrifugeTileEntity) {
			return (CentrifugeTileEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	@Override
	public boolean canInteractWith(@Nonnull final PlayerEntity player) {
		return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof Centrifuge && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
	}
}
