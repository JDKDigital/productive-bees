package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class IncubatorBlockEntity extends CapabilityBlockEntity implements MenuProvider, UpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public boolean isRunning = false;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(3, this)
    {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return
                (slot == IncubatorContainer.SLOT_INPUT && item.getItem() instanceof BeeCage) ||
                (slot == IncubatorContainer.SLOT_INPUT && item.is(ModTags.Common.EGGS)) ||
                (slot == IncubatorContainer.SLOT_CATALYST && item.getItem() instanceof HoneyTreat);
        }
    };

    private void setRunning(boolean running) {
        isRunning = running;
    }

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get()
    ));

    public EnergyStorage energyHandler = new EnergyStorage(10000);

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.INCUBATOR.get(), pos, state);
    }

    @Override
    public RecipeHolder<? extends TimedRecipeInterface> getCurrentRecipe() {
        return null;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return (int) (
                (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get()) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity blockEntity) {
        if (blockEntity.isRunning && level instanceof ServerLevel) {
            blockEntity.energyHandler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
        }
        if (!blockEntity.inventoryHandler.getStackInSlot(0).isEmpty()) {
            // Process incubation
            if (blockEntity.isRunning || blockEntity.canProcessInput(blockEntity.inventoryHandler)) {
                blockEntity.setRunning(true);
                int totalTime = blockEntity.getProcessingTime(null);

                if (blockEntity.recipeProgress >= totalTime && blockEntity.completeIncubation(blockEntity.inventoryHandler, level.random)) {
                    blockEntity.recipeProgress = 0;
                    blockEntity.setChanged();
                } else {
                    blockEntity.recipeProgress = Math.min(totalTime, blockEntity.recipeProgress + 1);
                }
            }
        } else {
            blockEntity.recipeProgress = 0;
            blockEntity.setRunning(false);
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get()));

        return Math.max(1, timeUpgradeModifier);
    }

    /**
     * Three things can be processed here, babees to adults, eggs to spawn eggs and applying genes
     */
    private boolean canProcessInput(IItemHandlerModifiable invHandler) {
        int energy = energyHandler.getEnergyStored();
        ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
        ItemStack treatItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

        boolean eggProcessing = inItem.is(ModTags.Common.EGGS);
        boolean cageProcessing = inItem.getItem() instanceof BeeCage && BeeCage.isFilled(inItem);

        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() // has enough power
                && (eggProcessing || cageProcessing) // valid processing
//                && invHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT).isEmpty() // output has room
                && treatItem.getItem().equals(ModItems.HONEY_TREAT.get())
                && (
                    (cageProcessing && (treatItem.getCount() >= ProductiveBeesConfig.GENERAL.incubatorTreatUse.get() || (HoneyTreat.hasGene(treatItem) && !HoneyTreat.hasBeeType(treatItem)))) ||
                    (eggProcessing && !treatItem.isEmpty() && HoneyTreat.hasBeeType(treatItem))
                );
    }

    private boolean completeIncubation(IItemHandlerModifiable invHandler, RandomSource random) {
        if (canProcessInput(invHandler)) {
            ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
            ItemStack catalystItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

            boolean eggProcessing = inItem.is(ModTags.Common.EGGS);
            boolean cageProcessing = inItem.getItem() instanceof BeeCage;

            ItemStack resultItem = ItemStack.EMPTY;
            int shrinkCatalyst = 1;
            int shrinkInput = 1;
            if (cageProcessing) {
                if (HoneyTreat.hasGene(catalystItem)) {
                    // Apply gene to the bee inside cage
                    var entity = BeeCage.getEntityFromStack(inItem, level, true);
                    if (entity instanceof Bee bee) {
                        HoneyTreat.applyGenesToBee(level, catalystItem, bee);
                        resultItem = new ItemStack(inItem.getItem());
                        BeeCage.captureEntity(bee, resultItem);
                    }
                } else if (BeeCage.isFilled(inItem)) {
                    CompoundTag nbt = inItem.get(DataComponents.CUSTOM_DATA).copyTag();
                    if (nbt.contains("Age")) {
                        nbt.putInt("Age", 0);
                    }
                    resultItem = inItem.copy();
                    resultItem.setCount(1);
                    resultItem.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                    shrinkCatalyst = ProductiveBeesConfig.GENERAL.incubatorTreatUse.get();
                }
            } else if (eggProcessing) {
                try {
                    List<GeneGroup> genes = HoneyTreat.getGenes(catalystItem);
                    for (GeneGroup geneGroup : genes) {
                        GeneAttribute geneAttribute = geneGroup.attribute();
                        if (geneAttribute.equals(GeneAttribute.TYPE)) {
                            int purity = geneGroup.purity();
                            if (random.nextInt(100) <= purity) {
                                ItemStack egg = BeeCreator.getSpawnEgg(ResourceLocation.parse(geneGroup.value()));
                                if (egg.getItem() instanceof SpawnEggItem) {
                                    resultItem = egg;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    ProductiveBees.LOGGER.warn("Failed to create bee spawn egg " + e.getMessage());
                }
            }
            ItemStack outItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT);
            if (!resultItem.isEmpty() && (outItem.isEmpty() ||ItemStack.isSameItemSameComponents(outItem, resultItem)) && (outItem.isEmpty() || (outItem.getCount() + resultItem.getCount()) <= outItem.getMaxStackSize())) {
                if (outItem.isEmpty()) {
                    invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, resultItem);
                } else {
                    outItem.grow(resultItem.getCount());
                }
                inItem.shrink(shrinkInput);
                catalystItem.shrink(shrinkCatalyst);
                return true;
            }
        }
        return false;
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        setRunning(false);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        recipeProgress = tag.getInt("RecipeProgress");
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        tag.putInt("RecipeProgress", recipeProgress);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.INCUBATOR.get().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new IncubatorContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyStorage getEnergyHandler() {
        return energyHandler;
    }
}
