package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.BreedingChamber;
import cy.jdkdigital.productivebees.common.block.entity.BreedingChamberBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class BreedingChamberContainer extends AbstractContainer
{
    public final BreedingChamberBlockEntity tileEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public final static int SLOT_CAGE = 0;
    public final static int SLOT_BEE_1 = 1;
    public final static int SLOT_BEE_2 = 2;
    public final static int SLOT_BREED_ITEM_1 = 3;
    public final static int SLOT_BREED_ITEM_2 = 4;
    public static final int SLOT_OUTPUT = 5;

    public BreedingChamberContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public BreedingChamberContainer(final int windowId, final Inventory playerInventory, final BreedingChamberBlockEntity tileEntity) {
        this(ModContainerTypes.BREEDING_CHAMBER.get(), windowId, playerInventory, tileEntity);
    }

    public BreedingChamberContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final BreedingChamberBlockEntity tileEntity) {
        super(type, windowId);

        this.tileEntity = tileEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        // Energy
        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
                    if (handler.getEnergyStored() > 0) {
                        handler.extractEnergy(handler.getEnergyStored(), false);
                    }
                    if (value > 0) {
                        handler.receiveEnergy(value, false);
                    }
                });
            }
        });

        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return tileEntity.recipeProgress;
            }

            @Override
            public void set(int value) {
                tileEntity.recipeProgress = value;
            }
        });

        this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_CAGE, 134 - 13, 41));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_BEE_1, 26 - 13, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_BEE_2, 62 - 13, 17));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_BREED_ITEM_1, 26 - 13, 37));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_BREED_ITEM_2, 62 - 13, 37));
            addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.ItemHandler) inv, SLOT_OUTPUT, 152 - 13, 41));
        });

        this.tileEntity.getUpgradeHandler().ifPresent(upgradeHandler -> {
            addSlotBox(upgradeHandler, 0, 165, 8, 1, 18, 4, 18);
        });

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
    }

    private static BreedingChamberBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof BreedingChamberBlockEntity) {
            return (BreedingChamberBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof BreedingChamber && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getTileEntity() {
        return tileEntity;
    }
}
