package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdvancedBeehiveContainer extends AbstractContainer
{
    public final AdvancedBeehiveTileEntity tileEntity;

    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS = new HashMap<Integer, List<Integer>>()
    {{
        put(0, new ArrayList<Integer>()
        {{
            add(37);
            add(25);
        }});
        put(1, new ArrayList<Integer>()
        {{
            add(55);
            add(35);
        }});
        put(2, new ArrayList<Integer>()
        {{
            add(37);
            add(46);
        }});
    }};
    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS_EXPANDED = new HashMap<Integer, List<Integer>>()
    {{
        put(0, new ArrayList<Integer>()
        {{
            add(19);
            add(24);
        }});
        put(1, new ArrayList<Integer>()
        {{
            add(19);
            add(45);
        }});
        put(2, new ArrayList<Integer>()
        {{
            add(37);
            add(35);
        }});
        put(3, new ArrayList<Integer>()
        {{
            add(55);
            add(24);
        }});
        put(4, new ArrayList<Integer>()
        {{
            add(55);
            add(45);
        }});
    }};

    private final IWorldPosCallable canInteractWithCallable;

    public AdvancedBeehiveContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public AdvancedBeehiveContainer(final int windowId, final PlayerInventory playerInventory, final AdvancedBeehiveTileEntity tileEntity) {
        super(ModContainerTypes.ADVANCED_BEEHIVE.get(), windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());
        boolean expanded = this.tileEntity.getBlockState().get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        // Inventory slots
        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            // Bottle slot
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, InventoryHandlerHelper.BOTTLE_SLOT, 86 - (expanded ? 13 : 0), 17));

            addSlotBox(new ItemHandlerWrapper(inv), InventoryHandlerHelper.OUTPUT_SLOTS[0], 116 - (expanded ? 13 : 0), 17, 3, 18, 3, 18);
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(new ItemHandlerWrapper(upgradeHandler), 0, 178 - (expanded ? 13 : 0), 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, 8 - (expanded ? 13 : 0), 84);
    }

    private static AdvancedBeehiveTileEntity getTileEntity(final PlayerInventory playerInventory, final PacketBuffer data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof AdvancedBeehiveTileEntity) {
            return (AdvancedBeehiveTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {
        return canInteractWithCallable.applyOrElse((world, pos) -> world.getBlockState(pos).getBlock() instanceof AdvancedBeehive && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected TileEntity getTileEntity() {
        return tileEntity;
    }
}
