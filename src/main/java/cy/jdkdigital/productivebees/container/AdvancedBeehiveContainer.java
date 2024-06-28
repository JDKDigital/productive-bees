package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdvancedBeehiveContainer extends AbstractContainer
{
    public final AdvancedBeehiveBlockEntity blockEntity;

    public static final int SLOT_BOTTLE = 0;
    public static final int SLOT_CAGE = 11;

    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS = new HashMap<>()
    {{
        put(0, new ArrayList<>()
        {{
            add(35);
            add(24);
        }});
        put(1, new ArrayList<>()
        {{
            add(53);
            add(34);
        }});
        put(2, new ArrayList<>()
        {{
            add(35);
            add(45);
        }});
    }};
    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS_EXPANDED = new HashMap<>()
    {{
        put(0, new ArrayList<>()
        {{
            add(17);
            add(23);
        }});
        put(1, new ArrayList<>()
        {{
            add(17);
            add(44);
        }});
        put(2, new ArrayList<>()
        {{
            add(35);
            add(34);
        }});
        put(3, new ArrayList<>()
        {{
            add(53);
            add(23);
        }});
        put(4, new ArrayList<>()
        {{
            add(53);
            add(44);
        }});
    }};

    private final ContainerLevelAccess canInteractWithCallable;

    public AdvancedBeehiveContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public AdvancedBeehiveContainer(final int windowId, final Inventory playerInventory, final AdvancedBeehiveBlockEntity blockEntity) {
        super(ModContainerTypes.ADVANCED_BEEHIVE.get(), windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        boolean expanded = this.blockEntity.getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;

        // Inventory slots
        // Bottle slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SLOT_BOTTLE, 86 - (expanded ? 13 : 0), 17));
        // Cage slot for simulated hives
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, SLOT_CAGE, 86 - (expanded ? 13 : 0), 53));

        addSlotBox(this.blockEntity.inventoryHandler, InventoryHandlerHelper.OUTPUT_SLOTS[0], 116 - (expanded ? 13 : 0), 17, 3, 18, 3, 18);

        if (this.blockEntity.acceptsUpgrades()) {
            addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 178 - (expanded ? 13 : 0), 8, 1, 18, 4, 18);
        }

        layoutPlayerInventorySlots(playerInventory, 0, 8 - (expanded ? 13 : 0), 84);
    }

    private static AdvancedBeehiveBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof AdvancedBeehiveBlockEntity) {
            return (AdvancedBeehiveBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof AdvancedBeehive && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
