package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.container.BreedingChamberContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeHelper;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BreedingChamberBlockEntity extends CapabilityBlockEntity implements MenuProvider, UpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public int recipeLookupCooldown = 0;
    public boolean isRunning = false;
    private List<RecipeHolder<BeeBreedingRecipe>> currentBreedingRecipes = new ArrayList<>();
    public RecipeHolder<BeeBreedingRecipe> chosenRecipe;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(6, this)
    {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return slot != BreedingChamberContainer.SLOT_OUTPUT &&
                    ((slot == BreedingChamberContainer.SLOT_BREED_ITEM_1 || slot == BreedingChamberContainer.SLOT_BREED_ITEM_2) && !(item.getItem() instanceof BeeCage)) || // flower item slots accept anything except bee cages
                    (slot == BreedingChamberContainer.SLOT_CAGE && item.getItem() instanceof BeeCage && !BeeCage.isFilled(item)) || // empty bee cages in bee cage slot
                    ((slot == BreedingChamberContainer.SLOT_BEE_1 || slot == BreedingChamberContainer.SLOT_BEE_2) && item.getItem() instanceof BeeCage && BeeCage.isFilled(item)); // filled bee cages in bee slots
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot != BreedingChamberContainer.SLOT_OUTPUT;
        }

        @Override
        public int[] getOutputSlots() {
            return new int[]{BreedingChamberContainer.SLOT_OUTPUT};
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (slot == BreedingChamberContainer.SLOT_BEE_1 || slot == BreedingChamberContainer.SLOT_BEE_2) {
                // Bee input changed, reset processing
                if (this.blockEntity instanceof BreedingChamberBlockEntity breedingChamberBlockEntity) {
                    breedingChamberBlockEntity.reset();
                    breedingChamberBlockEntity.setRecipe(null);
                }
            }
        }
    };

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get()
    ));

    public EnergyStorage energyHandler = new EnergyStorage(10000);

    public BreedingChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.BREEDING_CHAMBER.get(), pos, state);
    }

    private void reset() {
        recipeProgress = 0;
        currentBreedingRecipes = new ArrayList<>(); // reset recipe cache
        setRunning(false);
        setChanged();
    }

    private void setRunning(boolean running) {
        isRunning = running;
    }

    private void setRecipe(RecipeHolder<BeeBreedingRecipe> recipe) {
        chosenRecipe = recipe;
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public RecipeHolder<BeeBreedingRecipe> getCurrentRecipe() {
        return chosenRecipe;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return (int) (
                (recipe != null ? recipe.value().getProcessingTime() : 6000) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BreedingChamberBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            if (blockEntity.isRunning) {
                blockEntity.energyHandler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.breedingChamberPowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
            }
            if (!blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1).isEmpty() && !blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2).isEmpty()) {
                if (blockEntity.currentBreedingRecipes.isEmpty() && ++blockEntity.recipeLookupCooldown > 0) {
                    var cage1 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
                    var cage2 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
                    if (!cage1.isEmpty() && !cage2.isEmpty()) {
                        BeeHelper.IdentifierInventory beeInv = new BeeHelper.IdentifierInventory(BeeCage.getBeeType(cage1), BeeCage.getBeeType(cage2));
                        blockEntity.currentBreedingRecipes = BeeHelper.getBreedingRecipes(beeInv, serverLevel);
                        if (blockEntity.currentBreedingRecipes.size() > 0 && !blockEntity.currentBreedingRecipes.contains(blockEntity.chosenRecipe)) { // Pick a random recipe from the list as active recipe
                            blockEntity.setRecipe(blockEntity.currentBreedingRecipes.get(level.random.nextInt(blockEntity.currentBreedingRecipes.size())));
                        }
                        blockEntity.recipeLookupCooldown = -20; // delay between looking up recipe in case the two bees do not produce a recipe result
                    }
                }

                // Process breeding
                if (blockEntity.isRunning || (!blockEntity.currentBreedingRecipes.isEmpty() && blockEntity.canProcessInput(blockEntity.inventoryHandler, true))) {
                    blockEntity.setRunning(true);
                    int totalTime = blockEntity.getProcessingTime(blockEntity.chosenRecipe);

                    if (blockEntity.recipeProgress == 0) {
                        var cage1 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
                        var cage2 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);

                        var bee1Data = BeeReloadListener.INSTANCE.getData(BeeCage.getBeeType(cage1));
                        var bee2Data = BeeReloadListener.INSTANCE.getData(BeeCage.getBeeType(cage2));

                        // Consume breeding items when starting processing
                        blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1).shrink(bee1Data != null ? bee1Data.getInt("breedingItemCount") : 1);
                        blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2).shrink(bee2Data != null ? bee2Data.getInt("breedingItemCount")  : 1);
                    }

                    if (++blockEntity.recipeProgress >= totalTime && blockEntity.completeBreeding(blockEntity.inventoryHandler)) {
                        blockEntity.reset();
                    }
                    blockEntity.recipeProgress = Math.min(blockEntity.recipeProgress, totalTime); // clamp progress so the GUI doesn't break
                }
            } else {
                blockEntity.reset();
            }
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(ModItems.UPGRADE_TIME.get()) + getUpgradeCount(LibItems.UPGRADE_TIME.get()));

        return Math.max(1, timeUpgradeModifier);
    }

    private boolean canProcessInput(IItemHandlerModifiable invHandler, boolean firstRun) {
        int energy = energyHandler.getEnergyStored();

        var cage1 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
        var cage2 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);

        var bee1Data = BeeReloadListener.INSTANCE.getData(BeeCage.getBeeType(cage1));
        var bee2Data = BeeReloadListener.INSTANCE.getData(BeeCage.getBeeType(cage2));

        var bee1IsBaby = BeeCage.isFilled(cage1) && cage1.get(DataComponents.CUSTOM_DATA).getUnsafe().contains("Age") && cage1.get(DataComponents.CUSTOM_DATA).copyTag().getInt("Age") < 0;
        var bee2IsBaby = BeeCage.isFilled(cage2) && cage2.get(DataComponents.CUSTOM_DATA).getUnsafe().contains("Age") && cage2.get(DataComponents.CUSTOM_DATA).copyTag().getInt("Age") < 0;

        if (bee1IsBaby || bee2IsBaby) {
            return false;
        }

        Ingredient breedingIngredient1 = Ingredient.of(ModTags.DEFAULT_BREEDING);
        int breedingCount1 = 1;
        Ingredient breedingIngredient2 = Ingredient.of(ModTags.DEFAULT_BREEDING);
        int breedingCount2 = 1;

        if (bee1Data != null) {
            breedingIngredient1 = ConfigurableBee.getBreedingIngredientFromString(bee1Data.getString("breedingItem"));
            breedingCount1 = bee1Data.getInt("breedingItemCount");
        }
        if (bee2Data != null) {
            breedingIngredient2 = ConfigurableBee.getBreedingIngredientFromString(bee2Data.getString("breedingItem"));
            breedingCount2 = bee2Data.getInt("breedingItemCount");
        }

        ItemStack breedingItem1 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1);
        ItemStack breedingItem2 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2);

        return energy > ProductiveBeesConfig.GENERAL.incubatorPowerUse.get() && // has enough power
                ( // breeding items match the two bees on firstRun
                    !firstRun ||
                    (
                            breedingIngredient1.test(breedingItem1) &&
                            breedingCount1 <= breedingItem1.getCount() &&
                            breedingIngredient2.test(breedingItem2) &&
                            breedingCount2 <= breedingItem2.getCount()
                    )
                );
    }

    private boolean completeBreeding(IItemHandlerModifiable invHandler) {
        if (level != null && chosenRecipe != null && invHandler.getStackInSlot(BreedingChamberContainer.SLOT_OUTPUT).isEmpty() && invHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE).getItem() instanceof BeeCage && canProcessInput(invHandler, false)) {
            BeeIngredient beeIngredient = chosenRecipe.value().offspring.get();

            Entity offspring = beeIngredient.getBeeEntity().create(level);
            if (offspring instanceof Bee bee) {
                if (bee instanceof ConfigurableBee) {
                    ((ConfigurableBee) bee).setBeeType(beeIngredient.getBeeType().toString());
                    ((ConfigurableBee) bee).setDefaultAttributes();
                }

                Bee bee1 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1), level, true);
                if (bee1 != null) {
                    Bee bee2 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2), level, true);
                    BeeHelper.setOffspringAttributes(bee, bee1, bee2);
                }

                bee.setAge(-24000);

                ItemStack cage = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE);

                ItemStack newCage = new ItemStack(cage.getItem());
                BeeCage.captureEntity(bee, newCage);
                cage.shrink(1);

                invHandler.setStackInSlot(BreedingChamberContainer.SLOT_OUTPUT, newCage);

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
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        if (tag.contains("ChosenRecipe") && level != null) {
            Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(ResourceLocation.parse(tag.getString("ChosenRecipe")));
            if (recipe.isPresent() && recipe.get().value() instanceof BeeBreedingRecipe) {
                setRecipe((RecipeHolder<BeeBreedingRecipe>) recipe.get());
            }
        }

        recipeProgress = tag.getInt("RecipeProgress");
        isRunning = tag.contains("IsRunning") && tag.getBoolean("IsRunning");
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        if (chosenRecipe != null) {
            tag.putString("ChosenRecipe", chosenRecipe.id().toString());
        }
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putBoolean("IsRunning", isRunning);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.BREEDING_CHAMBER.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new BreedingChamberContainer(windowId, playerInventory, this);
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
