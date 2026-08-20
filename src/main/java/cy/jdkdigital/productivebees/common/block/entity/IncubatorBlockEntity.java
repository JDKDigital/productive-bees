package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.ICapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class IncubatorBlockEntity extends CapabilityBlockEntity implements MenuProvider, IUpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public boolean isRunning = false;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(3, this)
    {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return
                (slot == IncubatorContainer.SLOT_INPUT && item.getItem() instanceof BeeCage) ||
                (slot == IncubatorContainer.SLOT_INPUT && item.is(Tags.Items.EGGS)) ||
                (slot == IncubatorContainer.SLOT_CATALYST && item.getItem() instanceof HoneyTreat);
        }
    };

    private void setRunning(boolean running) {
        isRunning = running;
    }

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get()
    ));

    public SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(10000);

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
        return Math.max((int) (
                (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get()) * getProcessingTimeModifier()
        ), 5);
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IncubatorBlockEntity blockEntity) {
        if (blockEntity.isRunning && level instanceof ServerLevel) {
            try (Transaction tx = Transaction.openRoot()) {
                blockEntity.energyHandler.extract((int) (ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() * blockEntity.getEnergyConsumptionModifier()), tx);
                tx.commit();
            }
        }
        if (!blockEntity.inventoryHandler.getStackInSlot(0).isEmpty()) {
            // Process incubation
            if (blockEntity.isRunning || blockEntity.canProcessInput(blockEntity.inventoryHandler)) {
                blockEntity.setRunning(true);
                int totalTime = blockEntity.getProcessingTime(null);

                if (blockEntity.recipeProgress >= totalTime && blockEntity.completeIncubation(blockEntity.inventoryHandler, level.getRandom())) {
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
        double timeUpgradeModifier = ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get()));

        return Math.max(1, timeUpgradeModifier);
    }

    /**
     * Three things can be processed here, babees to adults, eggs to spawn eggs and applying genes
     */
    private boolean canProcessInput(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        int energy = energyHandler.getAmountAsInt();
        ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
        ItemStack treatItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

        boolean eggProcessing = inItem.is(Tags.Items.EGGS);
        boolean cageProcessing = inItem.getItem() instanceof BeeCage && BeeCage.isFilled(inItem);

        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() // has enough power
                && (eggProcessing || cageProcessing) // valid processing
                && treatItem.getItem().equals(ModItems.HONEY_TREAT.get())
                && (
                    (cageProcessing && (treatItem.getCount() >= ProductiveBeesConfig.GENERAL.incubatorTreatUse.get() || (HoneyTreat.hasGene(treatItem) && !HoneyTreat.hasBeeType(treatItem)))) ||
                    (eggProcessing && !treatItem.isEmpty() && HoneyTreat.hasBeeType(treatItem))
                );
    }

    private boolean completeIncubation(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, RandomSource random) {
        if (canProcessInput(invHandler)) {
            ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
            ItemStack catalystItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

            boolean eggProcessing = inItem.is(Tags.Items.EGGS);
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
                    shrinkCatalyst = ProductiveBeesConfig.GENERAL.incubatorTreatUse.get().intValue();
                }
            } else if (eggProcessing) {
                try {
                    List<GeneGroup> genes = HoneyTreat.getGenes(catalystItem);
                    for (GeneGroup geneGroup : genes) {
                        GeneAttribute geneAttribute = geneGroup.attribute();
                        if (geneAttribute.equals(GeneAttribute.TYPE)) {
                            if (random.nextInt(100) <= geneGroup.purity()) {
                                ItemStack egg = BeeCreator.getSpawnEgg(Identifier.parse(geneGroup.value()));
                                if (egg.getItem() instanceof SpawnEggItem) {
                                    resultItem = egg;
                                }
                            } else {
                                invHandler.extractItem(IncubatorContainer.SLOT_INPUT, shrinkInput, false, false);
                                invHandler.extractItem(IncubatorContainer.SLOT_CATALYST, 1, false, false);
                            }
                        }
                    }
                } catch (Exception e) {
                    ProductiveBees.LOGGER.debug("Failed to create bee spawn egg " + e.getMessage());
                }
            }
            ItemStack outItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT);
            if (!resultItem.isEmpty() && (outItem.isEmpty() || ItemStack.isSameItemSameComponents(outItem, resultItem)) && (outItem.isEmpty() || (outItem.getCount() + resultItem.getCount()) <= outItem.getMaxStackSize())) {
                if (outItem.isEmpty()) {
                    invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, resultItem);
                } else {
                    ItemStack combined = outItem.copy();
                    combined.grow(resultItem.getCount());
                    invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, combined);
                }
                invHandler.extractItem(IncubatorContainer.SLOT_INPUT, shrinkInput, false, false);
                invHandler.extractItem(IncubatorContainer.SLOT_CATALYST, shrinkCatalyst, false, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        setRunning(false);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        recipeProgress = input.getIntOr("RecipeProgress", 0);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);

        output.putInt("RecipeProgress", recipeProgress);
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
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }
}
