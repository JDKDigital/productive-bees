package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.HoneyGenerator;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorBlockEntity extends FluidTankBlockEntity implements UpgradeableBlockEntity
{
    protected int tickCounter = 0;
    public int fluidId = 0;
    public boolean hasLoaded = false;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(2, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item.equals(Items.HONEY_BOTTLE) || item.builtInRegistryHolder().is(ModTags.Forge.HONEY_BUCKETS) || item.equals(Items.HONEY_BLOCK);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == InventoryHandlerHelper.BOTTLE_SLOT;
        }
    };

    public IFluidHandler fluidInventory = new InventoryHandlerHelper.FluidHandler(10000, fluidStack -> fluidStack.getFluid().is(ModTags.HONEY))
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (fluid.getAmount() > 0) {
                HoneyGeneratorBlockEntity.this.setFilled(true);
            } else {
                HoneyGeneratorBlockEntity.this.setFilled(false);
            }
            HoneyGeneratorBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
            HoneyGeneratorBlockEntity.this.setChanged();
        }
    };
    private List<IEnergyStorage> recipients = new ArrayList<>();

    private void setFilled(boolean filled) {
        if (level != null && !level.isClientSide) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.FULL, filled));
        }
    }

    private void setOn(boolean filled) {
        if (level != null && !level.isClientSide) {
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(HoneyGenerator.ON, filled));
        }
    }

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this);

    public IEnergyStorage energyHandler = new EnergyStorage(100000);

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
            double consumeModifier = 1d + blockEntity.getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY.get());
            double speedModifier = 1d + (blockEntity.getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
            int inputPowerAmount = (int) (ProductiveBeesConfig.GENERAL.generatorPowerGen.get() * tickRate * speedModifier);
            int fluidConsumeAmount = (int) (ProductiveBeesConfig.GENERAL.generatorHoneyUse.get() * tickRate * speedModifier / consumeModifier);
            if (blockEntity.fluidInventory.getFluidInTank(0).getAmount() >= fluidConsumeAmount && blockEntity.energyHandler.receiveEnergy(inputPowerAmount, true) > 0) {
                blockEntity.energyHandler.receiveEnergy(inputPowerAmount, false);
                blockEntity.fluidInventory.drain(fluidConsumeAmount, IFluidHandler.FluidAction.EXECUTE);
                blockEntity.setOn(true);
            } else {
                blockEntity.setOn(false);
            }
            blockEntity.sendOutPower(tickRate);
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    public void sendOutPower(int modifier) {
        if (this.level != null) {
            AtomicInteger capacity = new AtomicInteger(energyHandler.getEnergyStored());
            if (capacity.get() > 0) {
                AtomicBoolean dirty = new AtomicBoolean(false);
                for (IEnergyStorage handler : recipients) {
                    boolean doContinue = true;
                    if (handler.canReceive()) {
                        int received = handler.receiveEnergy(Math.min(capacity.get(), 100 * modifier), false);
                        capacity.addAndGet(-received);
                        energyHandler.extractEnergy(received, false);
                        dirty.set(true);
                        doContinue = capacity.get() > 0;
                    }

                    if (!doContinue) {
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
        int fluidSpace = this.fluidInventory.getTankCapacity(0) - this.fluidInventory.getFluidInTank(0).getAmount();
        if (!inventoryHandler.getStackInSlot(0).isEmpty()) {
            ItemStack invItem = inventoryHandler.getStackInSlot(0);
            ItemStack outputInvItem = inventoryHandler.getStackInSlot(1);
            ItemStack outputItem = ItemStack.EMPTY;

            IFluidHandler itemFluidHandler = invItem.getCapability(Capabilities.FluidHandler.ITEM);
            boolean isHoneyBottle = invItem.getItem().equals(Items.HONEY_BOTTLE);
            boolean isHoneyBlock = invItem.getItem().equals(Items.HONEY_BLOCK);
            boolean isHoneyBucket = invItem.is(ModTags.Forge.HONEY_BUCKETS);

            int addAmount = 0;
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

            // Check if output has room
            if (!outputItem.equals(ItemStack.EMPTY) && !outputInvItem.isEmpty() && (!outputInvItem.getItem().equals(outputItem.getItem()) || outputInvItem.getMaxStackSize() == outputInvItem.getCount())) {
                return;
            }

            // Move empty containers to output
            if (itemFluidHandler != null && itemFluidHandler.getFluidInTank(0).isEmpty()) {
                if (outputInvItem.isEmpty()) {
                    if (!inventoryHandler.insertItem(1, invItem, false).isEmpty()) {
                        inventoryHandler.setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
                return;
            }

            if (addAmount > 0 && addAmount <= fluidSpace) {
                int fillAmount = fluidInventory.fill(new FluidStack(ModFluids.HONEY.get(), addAmount), IFluidHandler.FluidAction.EXECUTE);
                if (itemFluidHandler != null) {
                    FluidUtil.tryEmptyContainer(invItem, fluidInventory, fillAmount, null, true);
                } else {
                    invItem.shrink(1);
                    if (!outputItem.equals(ItemStack.EMPTY)) {
                        if (outputInvItem.isEmpty()) {
                            inventoryHandler.setStackInSlot(1, outputItem);
                        } else {
                            outputInvItem.grow(1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        // set fluid ID for screens
        Fluid fluid = fluidInventory.getFluidInTank(0).getFluid();
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.HONEY_GENERATOR.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory) {
        return new HoneyGeneratorContainer(windowId, playerInventory, this);
    }

    public void refreshConnectedTileEntityCache() {
        if (level instanceof ServerLevel) {
            List<IEnergyStorage> recipients = new ArrayList<>();
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockEntity te = level.getBlockEntity(worldPosition.relative(direction));
                if (te != null) {
                    IEnergyStorage energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK, worldPosition.relative(direction), direction.getOpposite());
                    if (energyCap != null) {
                        recipients.add(energyCap);
                    }
                }
            }
            this.recipients = recipients;
        }
    }
}
