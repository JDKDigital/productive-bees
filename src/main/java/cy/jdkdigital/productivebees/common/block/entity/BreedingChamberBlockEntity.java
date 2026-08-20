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
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.compat.jei.RecipeMapCache;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BreedingChamberBlockEntity extends CapabilityBlockEntity implements MenuProvider, IUpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    public int recipeProgress = 0;
    public int recipeLookupCooldown = 0;
    private int fbiCooldown;
    public boolean isRunning = false;
    private List<RecipeHolder<BeeBreedingRecipe>> currentBreedingRecipes = new ArrayList<>();
    public RecipeHolder<BeeBreedingRecipe> chosenRecipe;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(6, this)
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
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            super.onContentsChanged(slot, previousContents);

            if (slot == BreedingChamberContainer.SLOT_BEE_1 || slot == BreedingChamberContainer.SLOT_BEE_2) {
                // Bee input changed, reset processing
                if (this.blockEntity instanceof BreedingChamberBlockEntity breedingChamberBlockEntity) {
                    breedingChamberBlockEntity.reset();
                    breedingChamberBlockEntity.setRecipe(null);
                }
            }
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_TIME_2.get()
    ));

    public SimpleEnergyHandler energyHandler = new SimpleEnergyHandler(10000);

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
        return Math.max((int) (
                (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.breedingChamberProcessingTime.get()) * getProcessingTimeModifier()
        ), 5);
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get())));

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BreedingChamberBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel && ProductiveBeesConfig.BEES.allowBeeBreeding.get()) {
            blockEntity.fbiCooldown = blockEntity.fbiCooldown > 0 ? blockEntity.fbiCooldown-1 : 0;
            if (blockEntity.isRunning) {
                try (Transaction tx = Transaction.openRoot()) {
                    blockEntity.energyHandler.extract((int) (ProductiveBeesConfig.GENERAL.breedingChamberPowerUse.get() * blockEntity.getEnergyConsumptionModifier()), tx);
                    tx.commit();
                }
            }
            if (!blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1).isEmpty() && !blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2).isEmpty()) {
                if (blockEntity.currentBreedingRecipes.isEmpty() && ++blockEntity.recipeLookupCooldown > 0) {
                    var cage1 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
                    var cage2 = blockEntity.inventoryHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);
                    if (!cage1.isEmpty() && !cage2.isEmpty()) {
                        BeeHelper.IdentifierInventory beeInv = new BeeHelper.IdentifierInventory(BeeCage.getBeeType(cage1), BeeCage.getBeeType(cage2));
                        blockEntity.currentBreedingRecipes = BeeHelper.getBreedingRecipes(beeInv, serverLevel);
                        if (!blockEntity.currentBreedingRecipes.isEmpty() && !blockEntity.currentBreedingRecipes.contains(blockEntity.chosenRecipe)) { // Pick a random recipe from the list as active recipe
                            blockEntity.setRecipe(blockEntity.currentBreedingRecipes.get(level.getRandom().nextInt(blockEntity.currentBreedingRecipes.size())));
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

                        BeeData bee1Data = BeeRegistries.lookup(Identifier.parse(BeeCage.getBeeType(cage1)));
                        BeeData bee2Data = BeeRegistries.lookup(Identifier.parse(BeeCage.getBeeType(cage2)));

                        // Consume breeding items when starting processing
                        blockEntity.inventoryHandler.extractItem(BreedingChamberContainer.SLOT_BREED_ITEM_1, bee1Data != null ? bee1Data.breedingItemCount() : 1, false, false);
                        blockEntity.inventoryHandler.extractItem(BreedingChamberContainer.SLOT_BREED_ITEM_2, bee2Data != null ? bee2Data.breedingItemCount() : 1, false, false);
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
        double timeUpgradeModifier = ProductiveBeesConfig.UPGRADES.timeBonus.get() * (getUpgradeCount(LibItems.UPGRADE_TIME_2.get()) * 2 + getUpgradeCount(LibItems.UPGRADE_TIME.get()));

        return Math.max(1, timeUpgradeModifier);
    }

    private boolean canProcessInput(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler, boolean firstRun) {
        int energy = energyHandler.getAmountAsInt();

        var cage1 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1);
        var cage2 = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2);

        BeeData bee1Data = BeeRegistries.lookup(Identifier.parse(BeeCage.getBeeType(cage1)));
        BeeData bee2Data = BeeRegistries.lookup(Identifier.parse(BeeCage.getBeeType(cage2)));

        var bee1IsBaby = BeeCage.isFilled(cage1) && cage1.get(DataComponents.CUSTOM_DATA).contains("Age") && cage1.get(DataComponents.CUSTOM_DATA).copyTag().getIntOr("Age", 0) < 0;
        var bee2IsBaby = BeeCage.isFilled(cage2) && cage2.get(DataComponents.CUSTOM_DATA).contains("Age") && cage2.get(DataComponents.CUSTOM_DATA).copyTag().getIntOr("Age", 0) < 0;

        if (bee1IsBaby || bee2IsBaby) {
            if (this.fbiCooldown <= 0 && this.getLevel() != null) {
                // Spawn FBeeI if there's a player nearby
                List<Player> players = this.getLevel().getEntitiesOfClass(Player.class, new AABB(this.getBlockPos()).inflate(5, 2, 5));
                if (!players.isEmpty()) {
                    Entity entity = ModEntities.CONFIGURABLE_BEE.get().create(this.getLevel(), EntitySpawnReason.NATURAL);
                    if (entity instanceof ConfigurableBee bee) {
                        bee.setBeeType("productivebees:fbi");
                        bee.setDefaultAttributes();
                        bee.snapTo(this.getBlockPos().getX(), this.getBlockPos().getY() + 1, this.getBlockPos().getZ());
                        bee.setTarget(players.getFirst());
                        this.getLevel().addFreshEntity(bee);
                    }
                }
                this.fbiCooldown = 1200;
            }
            return false;
        }

        Ingredient breedingIngredient1 = ConfigurableBee.getBreedingIngredientFromString(bee1Data != null ? bee1Data.breedingItem() : "");
        int breedingCount1 = bee1Data != null ? bee1Data.breedingItemCount() : 1;
        Ingredient breedingIngredient2 = ConfigurableBee.getBreedingIngredientFromString(bee2Data != null ? bee2Data.breedingItem() : "");
        int breedingCount2 = bee2Data != null ? bee2Data.breedingItemCount() : 1;

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

    private boolean completeBreeding(InventoryHandlerHelper.BlockEntityItemStackHandler invHandler) {
        ItemStack cage = invHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE);
        if (cage.isEmpty()) {
            return false;
        }
        if (level != null && chosenRecipe != null && invHandler.getStackInSlot(BreedingChamberContainer.SLOT_OUTPUT).isEmpty() && invHandler.getStackInSlot(BreedingChamberContainer.SLOT_CAGE).getItem() instanceof BeeCage && canProcessInput(invHandler, false)) {
            var recipe = chosenRecipe.value();
            BeeIngredient beeIngredient = recipe.offspring.get();

            Entity offspring = beeIngredient.getBeeEntity().create(level, EntitySpawnReason.NATURAL);
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

                ItemStack newCage = new ItemStack(cage.getItem());
                BeeCage.captureEntity(bee, newCage);
                invHandler.extractItem(BreedingChamberContainer.SLOT_CAGE, 1, false, false);

                invHandler.setStackInSlot(BreedingChamberContainer.SLOT_OUTPUT, newCage);

                // parent death
                if (recipe.parentDeathChance > level.getRandom().nextFloat()) {
                    invHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_1, invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_1).getItem().getDefaultInstance());
                }
                if (recipe.parentDeathChance > level.getRandom().nextFloat()) {
                    invHandler.setStackInSlot(BreedingChamberContainer.SLOT_BEE_2, invHandler.getStackInSlot(BreedingChamberContainer.SLOT_BEE_2).getItem().getDefaultInstance());
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);

        input.getString("ChosenRecipe").ifPresent(id -> {
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(id));
            if (level instanceof ServerLevel sl) {
                Optional<RecipeHolder<?>> recipe = sl.recipeAccess().byKey(key);
                if (recipe.isPresent() && recipe.get().value() instanceof BeeBreedingRecipe) {
                    setRecipe((RecipeHolder<BeeBreedingRecipe>) recipe.get());
                }
            } else if (level != null) {
                for (RecipeHolder<BeeBreedingRecipe> holder : RecipeMapCache.getRecipeMap().byType(ModRecipeTypes.BEE_BREEDING_TYPE.get())) {
                    if (holder.id().equals(key)) {
                        setRecipe(holder);
                        break;
                    }
                }
            }
        });

        recipeProgress = input.getIntOr("RecipeProgress", 0);
        isRunning = input.getBooleanOr("IsRunning", false);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);

        if (chosenRecipe != null) {
            output.putString("ChosenRecipe", chosenRecipe.id().identifier().toString());
        }
        output.putInt("RecipeProgress", recipeProgress);
        output.putBoolean("IsRunning", isRunning);
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
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return energyHandler;
    }
}
