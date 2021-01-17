package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.container.HoneyGeneratorContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class HoneyGeneratorTileEntity extends FluidTankTileEntity implements INamedContainerProvider, ITickableTileEntity, UpgradeableTileEntity
{
    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(2, this)
    {
        @Override
        public boolean isBottleItem(Item item) {
            return item == Items.HONEY_BOTTLE || item.isIn(ModTags.HONEY_BUCKETS);
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid().isEquivalentTo(ModFluids.HONEY.get());
        }

        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            HoneyGeneratorTileEntity.this.markDirty();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(100000));

    public HoneyGeneratorTileEntity() {
        super(ModTileEntityTypes.HONEY_GENERATOR.get());
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            // 40 rf / tick - 4mb / tick
            int inputPowerAmount = ProductiveBeesConfig.GENERAL.generatorPowerGen.get();
            int fluidConsumeAmount = ProductiveBeesConfig.GENERAL.generatorHoneyUse.get();
            fluidInventory.ifPresent(fluidHandler -> {
                energyHandler.ifPresent(energyHandler -> {
                    if (fluidHandler.getFluidInTank(0).getAmount() >= fluidConsumeAmount && energyHandler.receiveEnergy(inputPowerAmount, true) > 0) {
                        energyHandler.receiveEnergy(inputPowerAmount, false);
                        fluidHandler.drain(fluidConsumeAmount, IFluidHandler.FluidAction.EXECUTE);
                    }
                });
            });
        }
        this.sendOutPower();
        super.tick();
    }

    private void sendOutPower() {
        energyHandler.ifPresent(energyHandler -> {
            AtomicInteger capacity = new AtomicInteger(energyHandler.getEnergyStored());
            if (capacity.get() > 0) {
                Direction[] directions = Direction.values();

                for (Direction direction : directions) {
                    if (this.world != null) {
                        TileEntity te = this.world.getTileEntity(this.pos.offset(direction));
                        if (te != null) {
                            boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map((handler) -> {
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(), 100), false);
                                    capacity.addAndGet(-received);
                                    energyHandler.extractEnergy(received, false);
                                    this.markDirty();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }).orElse(true);

                            if (!doContinue) {
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void tickFluidTank() {
        fluidInventory.ifPresent(fluidHandler -> {
            inventoryHandler.ifPresent(invHandler -> {
                int fluidSpace = fluidInventory.map(h -> h.getTankCapacity(0) - h.getFluidInTank(0).getAmount()).orElse(0);
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    ItemStack invItem = invHandler.getStackInSlot(0);
                    LazyOptional<IFluidHandler> itemFluidHandler = invItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
                    boolean isHoneyBottle = invItem.getItem().equals(Items.HONEY_BOTTLE);

                    // Check if output can hold a bottle
                    if (isHoneyBottle && invHandler.insertItem(1, new ItemStack(Items.GLASS_BOTTLE, 1), true).isEmpty()) {
                        ProductiveBees.LOGGER.info("Generator does not have room for glass bottle");
                        return;
                    }

                    // Move empty containers to output
                    if (itemFluidHandler.isPresent() && itemFluidHandler.map(h -> h.getFluidInTank(0).isEmpty()).orElse(false)) {
                        ProductiveBees.LOGGER.info("Move fluidhandler to output");
                        if (invHandler.getStackInSlot(1).isEmpty()) {
                            if (!invHandler.insertItem(1, invItem, false).isEmpty()) {
                                invHandler.setStackInSlot(0, ItemStack.EMPTY);
                            }
                        }
                        return;
                    }

                    int addAmount = isHoneyBottle ? 250 : itemFluidHandler.isPresent() ? fluidSpace : 0;
                    if (addAmount > 0 && addAmount <= fluidSpace) {
                        int fillAmount = fluidHandler.fill(new FluidStack(ModFluids.HONEY.get(), addAmount), IFluidHandler.FluidAction.EXECUTE);

                        if (itemFluidHandler.isPresent()) {
                            FluidUtil.tryEmptyContainer(invItem, fluidHandler, fillAmount, null, true);
                        } else if (invItem.getItem().equals(Items.HONEY_BOTTLE)) {
                            invItem.shrink(1);
                            invHandler.insertItem(1, new ItemStack(Items.GLASS_BOTTLE), false);
                        }
                    }
                }
            });
        });
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.HONEY_GENERATOR.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new HoneyGeneratorContainer(windowId, playerInventory, this);
    }
}
