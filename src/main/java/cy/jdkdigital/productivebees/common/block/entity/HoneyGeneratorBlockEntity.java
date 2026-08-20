package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.HoneyGenerator;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.ICapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorBlockEntity extends FluidTankBlockEntity implements MenuProvider, IUpgradeableBlockEntity
{
    protected int tickCounter = 0;
    public int fluidId = 0;
    public boolean hasLoaded = false;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(2, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item.equals(Items.HONEY_BOTTLE) || item.builtInRegistryHolder().is(ModTags.Common.HONEY_BUCKETS) || item.equals(Items.HONEY_BLOCK);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == InventoryHandlerHelper.BOTTLE_SLOT;
        }
    };

    public FluidStacksResourceHandler fluidHandler = new FluidStacksResourceHandler(1, 10000)
    {
        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            super.onContentsChanged(index, previousContents);
            HoneyGeneratorBlockEntity.this.setFilled(getAmountAsLong(index) > 0);
            HoneyGeneratorBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getResource(index).getFluid());
            HoneyGeneratorBlockEntity.this.setChanged();
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get(),
            LibItems.UPGRADE_PRODUCTIVITY.get()
    )); // TODO add support for higher tier productivity upgrades

    public SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(100000);

    private List<EnergyHandler> recipients = new ArrayList<>();

    private void setFilled(boolean filled) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.FULL, filled));
        }
    }

    private void setOn(boolean filled) {
        if (level != null && !level.isClientSide()) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.ON, filled));
        }
    }

    public HoneyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.HONEY_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HoneyGeneratorBlockEntity blockEntity) {
        int tickRate = 10;

        if (!blockEntity.hasLoaded) {
            blockEntity.refreshConnectedTileEntityCache();
            blockEntity.hasLoaded = true;
        }

        if (++blockEntity.tickCounter % tickRate == 0) {
            // TODO use higher tier prod. upgrades
            double consumeModifier = 1d + blockEntity.getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get());
            double speedModifier = 1d + (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + blockEntity.getUpgradeCount(LibItems.UPGRADE_TIME.get())));
            int inputPowerAmount = (int) (ProductiveBeesConfig.GENERAL.generatorPowerGen.get() * tickRate * speedModifier);
            int fluidConsumeAmount = (int) (ProductiveBeesConfig.GENERAL.generatorHoneyUse.get() * tickRate * speedModifier / consumeModifier);
            FluidResource currentResource = blockEntity.fluidHandler.getResource(0);
            if (currentResource.isEmpty()) {
                blockEntity.setOn(false);
            } else {
                try (Transaction tx = Transaction.openRoot()) {
                    long fluidDrained = blockEntity.fluidHandler.extract(0, currentResource, fluidConsumeAmount, tx);
                    int energyAccepted = blockEntity.energyHandler.insert(inputPowerAmount, tx);
                    if (fluidDrained >= fluidConsumeAmount && energyAccepted > 0) {
                        tx.commit();
                        blockEntity.setOn(true);
                    } else {
                        blockEntity.setOn(false);
                    }
                }
            }
            blockEntity.sendOutPower(tickRate);
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    public void sendOutPower(int modifier) {
        if (this.level != null) {
            AtomicInteger capacity = new AtomicInteger(energyHandler.getAmountAsInt());
            if (capacity.get() > 0) {
                AtomicBoolean dirty = new AtomicBoolean(false);
                for (EnergyHandler handler : recipients) {
                    try (Transaction tx = Transaction.openRoot()) {
                        int sendable = Math.min(capacity.get(), 100 * modifier);
                        int received = handler.insert(sendable, tx);
                        if (received > 0) {
                            int extracted = energyHandler.extract(received, tx);
                            if (extracted > 0) {
                                tx.commit();
                                capacity.addAndGet(-received);
                                dirty.set(true);
                            }
                        }
                    }
                    if (capacity.get() <= 0) {
                        break;
                    }
                }
                if (dirty.get()) {
                    this.setChanged();
                }
            }
        }
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        FluidResource honey = FluidResource.of(ModFluids.HONEY.get());
        int fluidSpace = fluidHandler.getCapacityAsInt(0, honey) - fluidHandler.getAmountAsInt(0);
        if (fluidSpace <= 0) {
            return;
        }
        ItemStack invItem = inventoryHandler.getStackInSlot(0);
        if (invItem.isEmpty()) {
            return;
        }
        ItemStack outputInvItem = inventoryHandler.getStackInSlot(1);

        ResourceHandler<FluidResource> itemFluidHandler = ItemAccess.forHandlerIndex(inventoryHandler, 0).getCapability(Capabilities.Fluid.ITEM);
        boolean isHoneyBottle = invItem.getItem().equals(Items.HONEY_BOTTLE);
        boolean isHoneyBlock = invItem.getItem().equals(Items.HONEY_BLOCK);
        boolean isHoneyBucket = invItem.is(ModTags.Common.HONEY_BUCKETS);

        int addAmount = 0;
        ItemStack outputItem = ItemStack.EMPTY;
        if (isHoneyBottle) {
            addAmount = 250;
            outputItem = new ItemStack(Items.GLASS_BOTTLE);
        } else if (isHoneyBlock) {
            addAmount = 1000;
        } else if (isHoneyBucket) {
            addAmount = 1000;
            outputItem = new ItemStack(Items.BUCKET);
        } else if (itemFluidHandler != null) {
            addAmount = fluidSpace;
        }

        // Output slot must be empty or stackable with the conversion output.
        if (!outputItem.isEmpty() && !outputInvItem.isEmpty() && (!outputInvItem.getItem().equals(outputItem.getItem()) || outputInvItem.getMaxStackSize() == outputInvItem.getCount())) {
            return;
        }

        // Emptied generic fluid container — move it to the output slot.
        if (itemFluidHandler != null && itemFluidHandler.getAmountAsInt(0) == 0 && !isHoneyBucket && !isHoneyBottle && !isHoneyBlock) {
            if (outputInvItem.isEmpty()) {
                if (!inventoryHandler.insertItem(1, invItem, false).isEmpty()) {
                    inventoryHandler.setStackInSlot(0, ItemStack.EMPTY);
                }
            }
            return;
        }

        if (addAmount <= 0 || addAmount > fluidSpace) {
            return;
        }

        if (itemFluidHandler != null && !isHoneyBucket && !isHoneyBottle) {
            // Generic fluid container — extract from the item and insert into the tank.
            try (Transaction tx = Transaction.openRoot()) {
                FluidResource itemResource = itemFluidHandler.getResource(0);
                int extracted = itemFluidHandler.extract(0, itemResource, addAmount, tx);
                if (extracted > 0) {
                    int inserted = fluidHandler.insert(honey, extracted, tx);
                    if (inserted > 0) {
                        tx.commit();
                    }
                }
            }
        } else {
            // Honey bottle/block/bucket — fill the tank, shrink the input, place the conversion output.
            try (Transaction tx = Transaction.openRoot()) {
                int inserted = fluidHandler.insert(honey, addAmount, tx);
                if (inserted > 0) {
                    tx.commit();
                    inventoryHandler.extractItem(0, 1, false, false);
                    if (!outputItem.isEmpty()) {
                        if (outputInvItem.isEmpty()) {
                            inventoryHandler.setStackInSlot(1, outputItem);
                        } else {
                            ItemStack combined = outputInvItem.copy();
                            combined.grow(1);
                            inventoryHandler.setStackInSlot(1, combined);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        // set fluid ID for screens
        fluidId = BuiltInRegistries.FLUID.getId(fluidHandler.getResource(0).getFluid());
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.HONEY_GENERATOR.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new HoneyGeneratorContainer(pContainerId, pPlayerInventory, this);
    }

    public void refreshConnectedTileEntityCache() {
        this.recipients = new ArrayList<>();
        if (level instanceof ServerLevel serverLevel) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbourPos = worldPosition.relative(direction);
                EnergyHandler handler = serverLevel.getCapability(Capabilities.Energy.BLOCK, neighbourPos, direction.getOpposite());
                if (handler != null) {
                    this.recipients.add(handler);
                }
            }
        }
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
    }
}
