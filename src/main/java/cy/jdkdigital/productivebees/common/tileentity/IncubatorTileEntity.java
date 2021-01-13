package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IncubatorTileEntity extends CapabilityTileEntity implements INamedContainerProvider, ITickableTileEntity, UpgradeableTileEntity
{
    public int recipeProgress = 0;
    public boolean isRunning = false;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(3, this)
    {
        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            return (slot == 0 && item.equals(ModItems.BEE_CAGE.get())) || (slot == 1 && item.equals(ModItems.HONEY_TREAT.get()));
        }
    });

    private void setRunning(boolean running) {
        isRunning = running;
    }

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(10000));

    public IncubatorTileEntity() {
        super(ModTileEntityTypes.INCUBATOR.get());
    }

    public int getProcessingTime() {
        return (int) (
            ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get() * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier);
    }

    @Override
    public void tick() {
        if (world != null && !world.isRemote) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    // Process incubation
                    if (isRunning || canProcessRecipe(invHandler)) {
                        setRunning(true);
                        int totalTime = getProcessingTime();

                        if (++this.recipeProgress >= totalTime) {
                            this.completeIncubation(invHandler);
                            recipeProgress = 0;
                            this.markDirty();
                        }
                    }
                } else {
                    this.recipeProgress = 0;
                    setRunning(false);
                }
            });
        }
    }

    private boolean canProcessRecipe(IItemHandlerModifiable invHandler) {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        ItemStack cageItem = invHandler.getStackInSlot(0);
        ItemStack treatItem = invHandler.getStackInSlot(1);
        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get()
                && BeeCage.isFilled(cageItem)
                && invHandler.getStackInSlot(2).isEmpty()
                && treatItem.getItem().equals(ModItems.HONEY_TREAT.get())
                && treatItem.getCount() >= ProductiveBeesConfig.GENERAL.incubatorTreatUse.get();
    }

    private void completeIncubation(IItemHandlerModifiable invHandler) {
        if (canProcessRecipe(invHandler)) {
            ItemStack cage = invHandler.getStackInSlot(0);

            CompoundNBT nbt = cage.getTag();
            if (nbt != null && nbt.contains("Age")) {
                nbt.putInt("Age", 0);
            }

            invHandler.setStackInSlot(2, cage);
            invHandler.setStackInSlot(0, ItemStack.EMPTY);
            invHandler.getStackInSlot(1).shrink(ProductiveBeesConfig.GENERAL.incubatorTreatUse.get());
        }
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        setRunning(false);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        recipeProgress = tag.getInt("RecipeProgress");
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);

        tag.putInt("RecipeProgress", recipeProgress);

        return tag;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        } else if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.INCUBATOR.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new IncubatorContainer(windowId, playerInventory, this);
    }
}
