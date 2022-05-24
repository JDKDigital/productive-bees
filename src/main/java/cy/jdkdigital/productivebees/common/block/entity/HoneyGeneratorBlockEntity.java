package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.HoneyGenerator;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivebees.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorBlockEntity extends FluidTankBlockEntity implements UpgradeableBlockEntity
{
    protected int tickCounter = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(2, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return item.equals(Items.HONEY_BOTTLE) || item.builtInRegistryHolder().is(ModTags.Forge.HONEY_BUCKETS) || item.equals(Items.HONEY_BLOCK);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == InventoryHandlerHelper.BOTTLE_SLOT;
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isSame(ModFluids.HONEY.get());
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            if (fluid.getAmount() > 0) {
                HoneyGeneratorBlockEntity.this.setFilled(true);
            } else {
                HoneyGeneratorBlockEntity.this.setFilled(false);
            }
            HoneyGeneratorBlockEntity.this.setChanged();
        }
    });
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

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(100000));

    public HoneyGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.HONEY_GENERATOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HoneyGeneratorBlockEntity blockEntity) {
        int tickRate = 10;
        if (++blockEntity.tickCounter % tickRate == 0) {
            double consumeModifier = 1d + blockEntity.getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY.get());
            double speedModifier = 1d + (blockEntity.getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
            int inputPowerAmount = (int) (ProductiveBeesConfig.GENERAL.generatorPowerGen.get() * tickRate * speedModifier);
            int fluidConsumeAmount = (int) (ProductiveBeesConfig.GENERAL.generatorHoneyUse.get() * tickRate * speedModifier / consumeModifier);
            blockEntity.fluidInventory.ifPresent(fluidHandler -> blockEntity.energyHandler.ifPresent(handler -> {
                if (fluidHandler.getFluidInTank(0).getAmount() >= fluidConsumeAmount && handler.receiveEnergy(inputPowerAmount, true) > 0) {
                    handler.receiveEnergy(inputPowerAmount, false);
                    fluidHandler.drain(fluidConsumeAmount, IFluidHandler.FluidAction.EXECUTE);
                    blockEntity.setOn(true);
                } else {
                    blockEntity.setOn(false);
                }
            }));
        }
        blockEntity.sendOutPower(tickRate);
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    public void sendOutPower(int modifier) {
        if (this.level != null) {
            energyHandler.ifPresent(energyHandler -> {
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
            });
        }
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        fluidInventory.ifPresent(fluidHandler -> {
            inventoryHandler.ifPresent(invHandler -> {
                int fluidSpace = fluidInventory.map(h -> h.getTankCapacity(0) - h.getFluidInTank(0).getAmount()).orElse(0);
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    ItemStack invItem = invHandler.getStackInSlot(0);
                    ItemStack outputInvItem = invHandler.getStackInSlot(1);
                    ItemStack outputItem = ItemStack.EMPTY;

                    LazyOptional<IFluidHandler> itemFluidHandler = invItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
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
                    } else if (itemFluidHandler.isPresent()) {
                        addAmount = fluidSpace;
                    }

                    // Check if output has room
                    if (!outputItem.equals(ItemStack.EMPTY) && !outputInvItem.isEmpty() && (!outputInvItem.getItem().equals(outputItem.getItem()) || outputInvItem.getMaxStackSize() == outputInvItem.getCount())) {
                        return;
                    }

                    // Move empty containers to output
                    if (itemFluidHandler.isPresent() && itemFluidHandler.map(h -> h.getFluidInTank(0).isEmpty()).orElse(false)) {
                        if (outputInvItem.isEmpty()) {
                            if (!invHandler.insertItem(1, invItem, false).isEmpty()) {
                                invHandler.setStackInSlot(0, ItemStack.EMPTY);
                            }
                        }
                        return;
                    }

                    if (addAmount > 0 && addAmount <= fluidSpace) {
                        int fillAmount = fluidHandler.fill(new FluidStack(ModFluids.HONEY.get(), addAmount), IFluidHandler.FluidAction.EXECUTE);
                        if (itemFluidHandler.isPresent()) {
                            FluidUtil.tryEmptyContainer(invItem, fluidHandler, fillAmount, null, true);
                        } else {
                            invItem.shrink(1);
                            if (!outputItem.equals(ItemStack.EMPTY)) {
                                if (outputInvItem.isEmpty()) {
                                    invHandler.setStackInSlot(1, outputItem);
                                } else {
                                    outputInvItem.grow(1);
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        // Rebuild cached attached TEs
        refreshConnectedTileEntityCache();
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        } else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidInventory.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public Component getName() {
        return new TranslatableComponent(ModBlocks.HONEY_GENERATOR.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new HoneyGeneratorContainer(windowId, playerInventory, this);
    }

    public void refreshConnectedTileEntityCache() {
        if (level instanceof ServerLevel) {
            List<IEnergyStorage> recipients = new ArrayList<>();
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                BlockEntity te = level.getBlockEntity(worldPosition.relative(direction));
                if (te != null) {
                    te.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).ifPresent(recipients::add);
                }
            }
            this.recipients = recipients;
        }
    }
}
