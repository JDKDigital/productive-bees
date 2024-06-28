package cy.jdkdigital.productivebees.container;

import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModContainerTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import cy.jdkdigital.productivelib.container.ManualSlotItemHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class CentrifugeContainer extends AbstractContainer
{
    public final CentrifugeBlockEntity blockEntity;

    public final ContainerLevelAccess canInteractWithCallable;

    public CentrifugeContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, data));
    }

    public CentrifugeContainer(final int windowId, final Inventory playerInventory, final CentrifugeBlockEntity blockEntity) {
        this(ModContainerTypes.CENTRIFUGE.get(), windowId, playerInventory, blockEntity);
    }

    public CentrifugeContainer(@Nullable MenuType<?> type, final int windowId, final Inventory playerInventory, final CentrifugeBlockEntity blockEntity) {
        super(type, windowId);

        this.blockEntity = blockEntity;
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addDataSlots(new ContainerData()
        {
            @Override
            public int get(int i) {
                return i == 0 ?
                        blockEntity.fluidId :
                        blockEntity.fluidHandler.getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0:
                        blockEntity.fluidId = value;
                    case 1:
                        FluidStack fluid = blockEntity.fluidHandler.getFluidInTank(0);
                        if (fluid.isEmpty()) {
                            blockEntity.fluidHandler.fill(new FluidStack(BuiltInRegistries.FLUID.byId(blockEntity.fluidId), value), IFluidHandler.FluidAction.EXECUTE);
                        }
                        else {
                            fluid.setAmount(value);
                        }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        addDataSlot(new DataSlot()
        {
            @Override
            public int get() {
                return blockEntity.recipeProgress;
            }

            @Override
            public void set(int value) {
                blockEntity.recipeProgress = value;
            }
        });

        // Comb slot
        addSlot(new ManualSlotItemHandler((InventoryHandlerHelper.BlockEntityItemStackHandler) this.blockEntity.inventoryHandler, InventoryHandlerHelper.INPUT_SLOT, 13, 35));

        // Inventory slots
        addSlotBox(this.blockEntity.inventoryHandler, InventoryHandlerHelper.OUTPUT_SLOTS[0], 67, 17, 3, 18, 3, 18);

        addSlotBox(this.blockEntity.getUpgradeHandler(), 0, 165, 8, 1, 18, 4, 18);

        layoutPlayerInventorySlots(playerInventory, 0, -5, 84);
    }

    private static CentrifugeBlockEntity getBlockEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CentrifugeBlockEntity) {
            return (CentrifugeBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Block entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(@Nonnull final Player player) {
        return canInteractWithCallable.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof Centrifuge && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    protected BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
