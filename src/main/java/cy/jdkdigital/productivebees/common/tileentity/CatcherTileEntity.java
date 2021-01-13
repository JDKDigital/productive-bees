package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.container.CatcherContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CatcherTileEntity extends FluidTankTileEntity implements INamedContainerProvider, ITickableTileEntity, UpgradeableTileEntity
{
    public int recipeProgress = 0;
    public int fluidId = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isBottleItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET;
        }

        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            return super.isInputSlotItem(slot, item);
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            CatcherTileEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            CatcherTileEntity.this.markDirty();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(100000));

    public CatcherTileEntity() {
        super(ModTileEntityTypes.CATCHER.get());
    }

    public int getProcessingTime() {
        return (int) (
            ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get() * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double combBlockUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) * ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier + combBlockUpgradeModifier);
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty()) {
                    // Process gene bottles
                    ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                } else {
                    this.recipeProgress = 0;
                }
            });
        }
        super.tick();
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
        return new CatcherContainer(windowId, playerInventory, this);
    }
}
