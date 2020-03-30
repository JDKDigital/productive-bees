package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.handler.bee.IBeeStorage;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntityAbstract;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdvancedBeehiveContainer extends AbstractContainer {

	public final AdvancedBeehiveTileEntityAbstract tileEntity;

	public static final HashMap<Integer, ArrayList<Integer>> BEE_POSITIONS = new HashMap<Integer, ArrayList<Integer>>() {{
		put(0, new ArrayList<Integer>() {{add(36);add(25);}});
		put(1, new ArrayList<Integer>() {{add(54);add(35);}});
		put(4, new ArrayList<Integer>() {{add(36);add(45);}});
	}};
	public static final HashMap<Integer, ArrayList<Integer>> BEE_POSITIONS_EXPANDED = new HashMap<Integer, ArrayList<Integer>>() {{
		put(0, new ArrayList<Integer>() {{add(18);add(24);}});
		put(1, new ArrayList<Integer>() {{add(18);add(45);}});
		put(2, new ArrayList<Integer>() {{add(36);add(33);}});
		put(3, new ArrayList<Integer>() {{add(54);add(24);}});
		put(4, new ArrayList<Integer>() {{add(54);add(45);}});
	}};
	
	private final IWorldPosCallable canInteractWithCallable;
	
	public AdvancedBeehiveContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	public AdvancedBeehiveContainer(final int windowId, final PlayerInventory playerInventory, final AdvancedBeehiveTileEntity tileEntity) {
		super(ModContainerTypes.ADVANCED_BEEHIVE.get(), windowId);

		this.tileEntity = tileEntity;
		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

		IItemHandler inventory = new InvWrapper(playerInventory);
		ProductiveBees.LOGGER.info("World " + tileEntity.getWorld());

		// Tile inventory slot(s)
		this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
			addSlot(new SlotItemHandler(handler, AdvancedBeehiveTileEntity.BOTTLE_SLOT, 86, 17));
			addSlotBox(handler, AdvancedBeehiveTileEntity.OUTPUT_SLOTS[0], 116, 17, 3, 18, 3, 18);
		});
		this.tileEntity.getCapability(CapabilityBee.BEE).ifPresent((handler -> {
			ProductiveBees.LOGGER.info(handler.getBees());
		}));

		layoutPlayerInventorySlots(inventory, 0, 8, 84);
	}

	private static AdvancedBeehiveTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
		Objects.requireNonNull(data, "data cannot be null!");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof AdvancedBeehiveTileEntity) {
			ProductiveBees.LOGGER.info("Data: " + ((AdvancedBeehiveTileEntity) tileAtPos).getBees());
			return (AdvancedBeehiveTileEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	@Override
	public boolean canInteractWith(@Nonnull final PlayerEntity player) {
		return isWithinUsableDistance(canInteractWithCallable, player, ModBlocks.ADVANCED_BEEHIVE.get());
	}
}
