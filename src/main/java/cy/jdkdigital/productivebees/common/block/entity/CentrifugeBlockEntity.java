package cy.jdkdigital.productivebees.common.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivelib.common.block.entity.FluidTankBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CentrifugeBlockEntity extends FluidTankBlockEntity implements MenuProvider, UpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    private RecipeHolder<? extends CentrifugeRecipe> currentRecipe = null;
    public int recipeProgress = 0;
    public int fluidId = 0;
    public int transferCooldown = -1;

    public IItemHandlerModifiable inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(11, this)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == InventoryHandlerHelper.BOTTLE_SLOT) return false;

            return super.isItemValid(slot, stack);
        }

        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            if (fromAutomation) {
                // Skip lookup if the item is different
                ItemStack existing = this.stacks.get(slot);
                if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(stack, existing)) {
                    return stack;
                }
            }
            return super.insertItem(slot, stack, simulate, fromAutomation);
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            var currentStack = getStackInSlot(slot);

            if (currentStack.getCount() == currentStack.getMaxStackSize()) {
                return false;
            }

            boolean isProcessableItem =
                    ItemStack.isSameItemSameComponents(currentStack, item) ||
                    item.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                    item.getItem().equals(ModItems.HONEY_TREAT.get()) ||
                    CentrifugeBlockEntity.this.canProcessItemStack(item);

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT) || (!isProcessableItem && !super.isInputSlot(slot));
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == InventoryHandlerHelper.INPUT_SLOT && this.getStackInSlot(slot).isEmpty()) {
                CentrifugeBlockEntity.this.recipeProgress = 0;
            }
        }
    };

    public FluidTank fluidHandler = new FluidTank(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CentrifugeBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
            CentrifugeBlockEntity.this.setChanged();
        }
    };

    protected IItemHandlerModifiable upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this);

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE.get(), pos, state);
    }

    public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public RecipeHolder<? extends TimedRecipeInterface> getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe) {
        return (int) (
            (recipe != null ? recipe.value().getProcessingTime() : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get()) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity blockEntity) {
        if (blockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemStackHandler) {
            if (!itemStackHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty() && blockEntity.canOperate()) {
                // Process gene bottles
                ItemStack invItem = itemStackHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeGeneProcessing(itemStackHandler, level.random);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeTreatProcessing(itemStackHandler);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else {
                    RecipeHolder<CentrifugeRecipe> recipe = blockEntity.getRecipe(itemStackHandler);
                    if (blockEntity.canProcessRecipe(recipe, itemStackHandler)) {
                        level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime(recipe);

                        if (++blockEntity.recipeProgress >= totalTime) {
                            blockEntity.completeRecipeProcessing(recipe, itemStackHandler, level.random);
                            blockEntity.recipeProgress = 0;
                            blockEntity.setChanged();
                        }
                    }
                }
            } else {
                level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, false));
            }

            // Pull items dropped on top
            if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --blockEntity.transferCooldown <= 0) {
                blockEntity.transferCooldown = 22;
                blockEntity.suckInItems(itemStackHandler);
            }
        }
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        IFluidHandler fluidHandler = blockEntity.getFluidHandler();
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        if (fluidStack.getAmount() > 0) {
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                if (fluidStack.getAmount() > 0) {
                    IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(direction.getOpposite()), null);
                    if (h != null) {
                        int amount = h.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                        if (amount > 0) {
                            amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                            fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }
    }

    private void suckInItems(ItemStackHandler invHandler) {
        for (ItemEntity itemEntity : getCaptureItems()) {
            ItemStack itemStack = itemEntity.getItem();
            if (
                    canProcessItemStack(itemStack) ||
                    itemStack.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                    itemStack.getItem().equals(ModItems.HONEY_TREAT.get()) && HoneyTreat.hasGene(itemStack)
            ) {
                captureItem(invHandler, itemEntity);
            }
        }
    }

    private List<ItemEntity> getCaptureItems() {
        assert level != null;
        return Centrifuge.COLLECTION_AREA_SHAPE.toAabbs().stream().flatMap((blockPos) -> level.getEntitiesOfClass(ItemEntity.class, blockPos.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    private static void captureItem(IItemHandlerModifiable invHandler, ItemEntity itemEntity) {
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT, itemEntity.getItem(), false);
        if (leftoverStack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(leftoverStack);
        }
    }

    protected boolean canOperate() {
        return true;
    }

    @Override
    public IItemHandlerModifiable getUpgradeHandler() {
        return upgradeHandler;
    }

    public boolean canProcessItemStack(ItemStack stack) {
        var inv = new InventoryHandlerHelper.BlockEntityItemStackHandler(2, null);
        inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, stack);

        boolean isAllowedByFilter = true;
        List<ItemStack> filterUpgrades = this.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
        if (filterUpgrades.size() > 0) {
            isAllowedByFilter = false;
            for (ItemStack filter : filterUpgrades) {
                List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
                for (Supplier<BeeIngredient> allowedBee : allowedBees) {
                    List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false, 1.0);
                    for (ItemStack pStack: produceList) {
                        if (pStack.getItem().equals(stack.getItem())) {
                            isAllowedByFilter = true;
                            break;
                        }
                    }
                }
            }
        }

        RecipeHolder<CentrifugeRecipe> recipe = this.getRecipe(inv);

        return isAllowedByFilter && recipe != null;
    }

    static Map<String, RecipeHolder<CentrifugeRecipe>> recipeMap = new HashMap<>();
    protected RecipeHolder<CentrifugeRecipe> getRecipe(InventoryHandlerHelper.BlockEntityItemStackHandler inputHandler) {
        if (recipeMap.size() > 5000) {
            recipeMap.clear();
        }
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || level == null) {
            return null;
        }

        String cacheKey = BuiltInRegistries.ITEM.getKey(input.getItem()).toString() + (!input.getComponents().isEmpty() ? input.getComponents().stream().map(TypedDataComponent::toString).reduce((s, s2) -> s + s2) : "");
        if (!recipeMap.containsKey(cacheKey)) {
            recipeMap.put(cacheKey, BeeHelper.getCentrifugeRecipe(level, inputHandler));
        }

        return recipeMap.getOrDefault(cacheKey, null);
    }

    protected boolean canProcessRecipe(@Nullable RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.value().getRecipeOutputs().forEach((stack, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(stack.getItem(), value.max());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
            FluidStack fluidOutput = recipe.value().getFluidOutputs();
            boolean fluidFlag = true;
            if (!fluidOutput.isEmpty()) {
                fluidFlag = fluidHandler.getFluidInTank(0).isEmpty() || fluidHandler.getFluidInTank(0).getFluid().equals(fluidOutput.getFluid());
            }

            return fluidFlag && ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler, RandomSource random) {
        this.completeRecipeProcessing(recipe, invHandler, random, false);
    }

    protected void completeRecipeProcessing(RecipeHolder<CentrifugeRecipe> recipe, IItemHandlerModifiable invHandler, RandomSource random, boolean stripWax) {
        recipe.value().getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if ((!stripWax || !itemStack.is(ModTags.Common.WAX)) && random.nextFloat() <= recipeValues.chance()) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.min()), Mth.floor(recipeValues.max()));
                ItemStack output = itemStack.copy();
                output.setCount(count);
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(output);
            }
        });

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);

        FluidStack fluidOutput = recipe.value().getFluidOutputs();
        if (!fluidOutput.isEmpty()) {
            fluidHandler.fill(fluidOutput.copy(), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, RandomSource random) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        List<GeneGroup> entityData = GeneBottle.getGenes(geneBottle);
        if (entityData.isEmpty()) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (GeneGroup geneGroup : entityData) {
            if (random.nextDouble() <= chance) {
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(Gene.getStack(geneGroup, 1));
            }
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    private void completeTreatProcessing(IItemHandlerModifiable invHandler) {
        ItemStack honeyTreat = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        List<GeneGroup> genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (GeneGroup geneGroup : genes) {
                ItemStack insertedGene = Gene.getStack(geneGroup, 1);
                ((InventoryHandlerHelper.BlockEntityItemStackHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        recipeProgress = tag.getInt("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidHandler.getFluidInTank(0).getFluid();
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        tag.putInt("RecipeProgress", recipeProgress);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new CentrifugeContainer(pContainerId, pPlayerInventory, this);
    }

    @Override
    public IItemHandler getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public FluidTank getFluidHandler() {
        return fluidHandler;
    }
}
