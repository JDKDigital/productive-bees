package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.IncubatorContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
                (slot == IncubatorContainer.SLOT_INPUT && item.is(ModTags.Forge.EGGS)) ||
                (slot == IncubatorContainer.SLOT_CATALYST && item.getItem() instanceof HoneyTreat);
        }
    };

    private void setRunning(boolean running) {
        isRunning = running;
    }

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this);

    public IEnergyStorage energyHandler = new EnergyStorage(10000);

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.INCUBATOR.get(), pos, state);
    }

    @Override
    public TimedRecipeInterface getCurrentRecipe() {
        return null;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    public int getProcessingTime(TimedRecipeInterface recipe) {
        return (int) (
                (recipe != null ? recipe.getProcessingTime() : ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get()) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

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

                if (++blockEntity.recipeProgress >= totalTime) {
                    blockEntity.completeIncubation(blockEntity.inventoryHandler, level.random);
                    blockEntity.recipeProgress = 0;
                    blockEntity.setChanged();
                }
            }
        } else {
            blockEntity.recipeProgress = 0;
            blockEntity.setRunning(false);
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get();

        return Math.max(1, timeUpgradeModifier);
    }

    /**
     * Three things can be processed here, babees to adults, eggs to spawn eggs and applying genes
     */
    private boolean canProcessInput(IItemHandlerModifiable invHandler) {
        int energy = energyHandler.getEnergyStored();
        ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT);
        ItemStack treatItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

        boolean eggProcessing = inItem.is(ModTags.Forge.EGGS);
        boolean cageProcessing = inItem.getItem() instanceof BeeCage && BeeCage.isFilled(inItem);

        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() // has enough power
                && (eggProcessing || cageProcessing) // valid processing
                && invHandler.getStackInSlot(IncubatorContainer.SLOT_OUTPUT).isEmpty() // output has room
                && treatItem.getItem().equals(ModItems.HONEY_TREAT.get())
                && (
                    (cageProcessing && (treatItem.getCount() >= ProductiveBeesConfig.GENERAL.incubatorTreatUse.get() || (HoneyTreat.hasGene(treatItem) && !HoneyTreat.hasBeeType(treatItem)))) ||
                    (eggProcessing && !treatItem.isEmpty() && HoneyTreat.hasBeeType(treatItem))
                );
    }

    private void completeIncubation(IItemHandlerModifiable invHandler, RandomSource random) {
        if (canProcessInput(invHandler)) {
            ItemStack inItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT).copy();

            boolean eggProcessing = inItem.is(ModTags.Forge.EGGS);
            boolean cageProcessing = inItem.getItem() instanceof BeeCage;

            if (cageProcessing) {
                ItemStack treat = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);
                if (HoneyTreat.hasGene(treat)) {
                    // Apply gene to the bee inside cage
                    var entity = BeeCage.getEntityFromStack(inItem, level, true);
                    if (entity instanceof ProductiveBee pBee) {
                        HoneyTreat.applyGenesToBee(level, treat, pBee);
                        var newBeeStack = new ItemStack(inItem.getItem());
                        BeeCage.captureEntity(pBee, newBeeStack);
                        invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, newBeeStack);
                        invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT).shrink(1);
                        treat.shrink(1);
                    }
                } else {
                    CompoundTag nbt = inItem.getTag();
                    if (nbt != null && nbt.contains("Age")) {
                        nbt.putInt("Age", 0);
                    }
                    inItem.setCount(1);
                    invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, inItem);
                    treat.shrink(ProductiveBeesConfig.GENERAL.incubatorTreatUse.get());
                    invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT).shrink(1);
                }
            } else if (eggProcessing) {
                ItemStack treatItem = invHandler.getStackInSlot(IncubatorContainer.SLOT_CATALYST);

                ListTag genes = HoneyTreat.getGenes(treatItem);
                for (Tag inbt : genes) {
                    ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
                    String beeName = Gene.getAttributeName(insertedGene);
                    if (!beeName.isEmpty()) {
                        int purity = Gene.getPurity(insertedGene);
                        if (((CompoundTag) inbt).contains("purity")) {
                            purity = ((CompoundTag) inbt).getInt("purity");
                        }
                        if (random.nextInt(100) <= purity) {
                            ItemStack egg = BeeCreator.getSpawnEgg(beeName);
                            if (egg.getItem() instanceof SpawnEggItem) {
                                invHandler.setStackInSlot(IncubatorContainer.SLOT_OUTPUT, egg);
                            }
                        }
                    }
                }
                invHandler.getStackInSlot(IncubatorContainer.SLOT_INPUT).shrink(1);
                treatItem.shrink(1);
            }
        }
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
}
