package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
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
            return
                (slot == 0 && item instanceof BeeCage) ||
                (slot == 0 && item.is(ModTags.EGGS)) ||
                (slot == 1 && item instanceof HoneyTreat);
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
        if (level != null && !level.isClientSide) {
            if (isRunning && level instanceof ServerWorld) {
                energyHandler.ifPresent(handler -> {
                    handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() * getEnergyConsumptionModifier()), false);
                });
            }
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(0).isEmpty()) {
                    // Process incubation
                    if (isRunning || canProcessInput(invHandler)) {
                        setRunning(true);
                        int totalTime = getProcessingTime();

                        if (++this.recipeProgress >= totalTime) {
                            this.completeIncubation(invHandler);
                            recipeProgress = 0;
                            this.setChanged();
                        }
                    }
                }
                else {
                    this.recipeProgress = 0;
                    setRunning(false);
                }
            });
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get();

        return Math.max(1, timeUpgradeModifier);
    }

    /**
     * Two recipes can be processed here, babees to adults and eggs to spawn eggs
     */
    private boolean canProcessInput(IItemHandlerModifiable invHandler) {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);
        ItemStack inItem = invHandler.getStackInSlot(0);
        ItemStack treatItem = invHandler.getStackInSlot(1);

        boolean eggProcessing = inItem.getItem().is(ModTags.EGGS);
        boolean cageProcessing = inItem.getItem() instanceof BeeCage && BeeCage.isFilled(inItem);

        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() // has enough power
                && (eggProcessing || cageProcessing) // valid processing
                && invHandler.getStackInSlot(2).isEmpty() // output has room
                && treatItem.getItem().equals(ModItems.HONEY_TREAT.get())
                && (
                    (cageProcessing && treatItem.getCount() >= ProductiveBeesConfig.GENERAL.incubatorTreatUse.get()) ||
                    (eggProcessing && !treatItem.isEmpty() && HoneyTreat.hasBeeType(treatItem))
                );
    }

    private void completeIncubation(IItemHandlerModifiable invHandler) {
        if (canProcessInput(invHandler)) {
            ItemStack inItem = invHandler.getStackInSlot(0);

            boolean eggProcessing = inItem.getItem().is(ModTags.EGGS);
            boolean cageProcessing = inItem.getItem() instanceof BeeCage;

            if (canProcessInput(invHandler)) {
                if (cageProcessing) {
                    CompoundNBT nbt = inItem.getTag();
                    if (nbt != null && nbt.contains("Age")) {
                        nbt.putInt("Age", 0);
                    }
                    invHandler.setStackInSlot(2, inItem);
                    invHandler.getStackInSlot(1).shrink(ProductiveBeesConfig.GENERAL.incubatorTreatUse.get());
                    invHandler.setStackInSlot(0, ItemStack.EMPTY);
                } else if (eggProcessing) {
                    ItemStack treatItem = invHandler.getStackInSlot(1);

                    ListNBT genes = HoneyTreat.getGenes(treatItem);
                    for (INBT inbt : genes) {
                        ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
                        String beeName = Gene.getAttributeName(insertedGene);
                        if (!beeName.isEmpty()) {
                            int purity = ((CompoundNBT) inbt).getInt("purity");
                            if (ProductiveBees.rand.nextInt(100) <= purity) {
                                ItemStack egg = BeeCreator.getSpawnEgg(beeName);
                                if (egg.getItem() instanceof SpawnEggItem) {
                                    invHandler.setStackInSlot(2, egg);
                                }
                            }
                        }
                    }

                    inItem.shrink(1);
                    invHandler.getStackInSlot(1).shrink(1);
                }
            }
        }
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        setRunning(false);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        recipeProgress = tag.getInt("RecipeProgress");
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag = super.save(tag);

        tag.putInt("RecipeProgress", recipeProgress);

        return tag;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        else if (cap == CapabilityEnergy.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.INCUBATOR.get().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new IncubatorContainer(windowId, playerInventory, this);
    }
}
