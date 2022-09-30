package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.container.BreedingChamberContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BreedingChamberBlockEntity extends CapabilityBlockEntity implements UpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public int recipeLookupCooldown = 0;
    public boolean isRunning = false;
    private List<BeeBreedingRecipe> currentBreedingRecipes = new ArrayList<>();
    public BeeBreedingRecipe chosenRecipe;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(6, this)
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
                if (this.tileEntity instanceof BreedingChamberBlockEntity breedingChamberBlockEntity) {
                    breedingChamberBlockEntity.reset();
                    breedingChamberBlockEntity.setRecipe(null);
                }
            }
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    protected LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> new EnergyStorage(10000));

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

    private void setRecipe(BeeBreedingRecipe recipe) {
        chosenRecipe = recipe;
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    public int getProcessingTime() {
        return (int) (
                ProductiveBeesConfig.GENERAL.breedingChamberProcessingTime.get() * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BreedingChamberBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            if (blockEntity.isRunning) {
                blockEntity.energyHandler.ifPresent(handler -> {
                    handler.extractEnergy((int) (ProductiveBeesConfig.GENERAL.breedingChamberPowerUse.get() * blockEntity.getEnergyConsumptionModifier()), false);
                });
            }
            blockEntity.inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1).isEmpty() && !invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2).isEmpty()) {
                    Bee bee1 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1), level, false);
                    Bee bee2 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2), level, false);
                    if (blockEntity.currentBreedingRecipes.isEmpty() && ++blockEntity.recipeLookupCooldown > 0 && bee1 != null && bee2 != null) {
                        blockEntity.currentBreedingRecipes = BeeHelper.getBreedingRecipes(bee1, bee2, serverLevel);
                        if (blockEntity.currentBreedingRecipes.size() > 0 && !blockEntity.currentBreedingRecipes.contains(blockEntity.chosenRecipe)) { // Pick a random recipe from the list as active recipe
                            blockEntity.setRecipe(blockEntity.currentBreedingRecipes.get(level.random.nextInt(blockEntity.currentBreedingRecipes.size())));
                        }
                        blockEntity.recipeLookupCooldown = -20; // delay between looking up recipe in case the two bees do not produce a recipe result
                    }

                    // Process breeding
                    if (blockEntity.isRunning || (!blockEntity.currentBreedingRecipes.isEmpty() && blockEntity.canProcessInput(invHandler, true))) {
                        blockEntity.setRunning(true);
                        int totalTime = blockEntity.getProcessingTime();

                        if (blockEntity.recipeProgress == 0) {
                            // Consume breeding items when starting processing
                            invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_1).shrink(bee1 instanceof ProductiveBee pBee1 ? pBee1.getBreedingItemCount() : 1);
                            invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BREED_ITEM_2).shrink(bee2 instanceof ProductiveBee pBee2 ? pBee2.getBreedingItemCount() : 1);
                        }

                        if (++blockEntity.recipeProgress >= totalTime && blockEntity.completeBreeding(invHandler)) {
                            blockEntity.reset();
                        }
                        blockEntity.recipeProgress = Math.min(blockEntity.recipeProgress, totalTime); // clamp progress so the GUI doesn't break
                    }
                } else {
                    blockEntity.reset();
                }
            });
        }
    }

    protected double getEnergyConsumptionModifier() {
        double timeUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get();

        return Math.max(1, timeUpgradeModifier);
    }

    private boolean canProcessInput(IItemHandlerModifiable invHandler, boolean firstRun) {
        int energy = energyHandler.map(IEnergyStorage::getEnergyStored).orElse(0);

        Bee bee1 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1), level, false);
        Bee bee2 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2), level, false);

        if (bee1 == null || bee1.isBaby() || bee2 == null || bee2.isBaby()) {
            return false;
        }

        Ingredient breedingIngredient1 = Ingredient.of(ItemTags.FLOWERS);
        int breedingCount1 = 1;
        Ingredient breedingIngredient2 = Ingredient.of(ItemTags.FLOWERS);
        int breedingCount2 = 1;

        if (bee1 instanceof ProductiveBee pBee) {
            breedingIngredient1 = pBee.getBreedingIngredient();
            breedingCount1 = pBee.getBreedingItemCount();
        }
        if (bee2 instanceof ProductiveBee pBee) {
            breedingIngredient2 = pBee.getBreedingIngredient();
            breedingCount2 = pBee.getBreedingItemCount();
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
            var beeIngredient = chosenRecipe.offspring.get();

            Entity offspring = beeIngredient.getBeeEntity().create(level);
            if (offspring instanceof Bee bee) {
                if (bee instanceof ConfigurableBee) {
                    ((ConfigurableBee) bee).setBeeType(beeIngredient.getBeeType().toString());
                    ((ConfigurableBee) bee).setAttributes();
                }

                Bee bee1 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1), level, true);
                if (bee instanceof ProductiveBee && bee1 instanceof ProductiveBee) {
                    Bee bee2 = BeeCage.getEntityFromStack(invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2), level, true);
                    BeeHelper.setOffspringAttributes((ProductiveBee) bee, (ProductiveBee) bee1, bee2);
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
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        if (tag.contains("ChosenRecipe") && level != null) {
            Optional<? extends Recipe<?>> recipe = level.getRecipeManager().byKey(new ResourceLocation(tag.getString("ChosenRecipe")));
            if (recipe.isPresent() && recipe.get() instanceof BeeBreedingRecipe breedingRecipe) {
                setRecipe(breedingRecipe);
            }
        }

        recipeProgress = tag.getInt("RecipeProgress");
        isRunning = tag.contains("IsRunning") && tag.getBoolean("IsRunning");
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        if (chosenRecipe != null) {
            tag.putString("ChosenRecipe", chosenRecipe.getId().toString());
        }
        tag.putInt("RecipeProgress", recipeProgress);
        tag.putBoolean("IsRunning", isRunning);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        else if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
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
}
