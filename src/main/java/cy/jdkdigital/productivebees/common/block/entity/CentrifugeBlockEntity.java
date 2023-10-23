package cy.jdkdigital.productivebees.common.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CentrifugeBlockEntity extends FluidTankBlockEntity implements UpgradeableBlockEntity, IRecipeProcessingBlockEntity
{
    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;
    public int fluidId = 0;
    public int transferCooldown = -1;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(11, this)
    {
        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            if (fromAutomation) {
                // Skip recipe lookup if the item is different
                ItemStack existing = this.stacks.get(slot);
                if (!existing.isEmpty() && !ItemHandlerHelper.canItemStacksStack(stack, existing)) {
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
                    ItemStack.isSameItemSameTags(currentStack, item) ||
                    item.getItem().equals(ModItems.GENE_BOTTLE.get()) ||
                    item.getItem().equals(ModItems.HONEY_TREAT.get()) ||
                    CentrifugeBlockEntity.this.canProcessItemStack(item);

            return !isProcessableItem || slot == InventoryHandlerHelper.INPUT_SLOT;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == InventoryHandlerHelper.INPUT_SLOT && this.getStackInSlot(slot).isEmpty()) {
                CentrifugeBlockEntity.this.recipeProgress = 0;
            }
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CentrifugeBlockEntity.this.fluidId = BuiltInRegistries.FLUID.getId(getFluid().getFluid());
            CentrifugeBlockEntity.this.setChanged();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CENTRIFUGE.get(), pos, state);
    }

    public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public TimedRecipeInterface getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public int getRecipeProgress() {
        return recipeProgress;
    }

    @Override
    public int getProcessingTime(TimedRecipeInterface recipe) {
        return (int) (
                (recipe != null ? recipe.getProcessingTime() : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get()) * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(invHandler -> {
            if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty() && blockEntity.canOperate()) {
                // Process gene bottles
                ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeGeneProcessing(invHandler, level.random);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime(null);

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeTreatProcessing(invHandler);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else {
                    CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler);
                    if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                        level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime(recipe);

                        if (++blockEntity.recipeProgress >= totalTime) {
                            blockEntity.completeRecipeProcessing(recipe, invHandler, level.random);
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
                blockEntity.suckInItems(invHandler);
            }
        });
        FluidTankBlockEntity.tick(level, pos, state, blockEntity);
    }

    @Override
    public void tickFluidTank(Level level, BlockPos pos, BlockState state, FluidTankBlockEntity blockEntity) {
        this.getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() > 0) {
                Direction[] directions = Direction.values();
                for (Direction direction : directions) {
                    BlockEntity te = level.getBlockEntity(worldPosition.relative(direction));
                    if (te != null && fluidStack.getAmount() > 0) {
                        te.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(h -> {
                            int amount = h.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                            if (amount > 0) {
                                amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                                fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                            }
                        });
                    }
                }
            }
        });
    }

    private void suckInItems(IItemHandlerModifiable invHandler) {
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
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    public boolean canProcessItemStack(ItemStack stack) {
        IItemHandlerModifiable inv = new InventoryHandlerHelper.ItemHandler(2, null);
        inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, stack);

        boolean isAllowedByFilter = true;
        List<ItemStack> filterUpgrades = this.getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
        if (filterUpgrades.size() > 0) {
            isAllowedByFilter = false;
            for (ItemStack filter : filterUpgrades) {
                List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
                for (Supplier<BeeIngredient> allowedBee : allowedBees) {
                    List<ItemStack> produceList = BeeHelper.getBeeProduce(level, (Bee) allowedBee.get().getCachedEntity(level), false);
                    for (ItemStack pStack: produceList) {
                        if (pStack.getItem().equals(stack.getItem())) {
                            isAllowedByFilter = true;
                            break;
                        }
                    }
                }
            }
        }

        CentrifugeRecipe recipe = getRecipe(inv);

        return isAllowedByFilter && recipe != null;
    }

    protected CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
            return null;
        }

        if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), level)) {
            return currentRecipe;
        }

        currentRecipe = BeeHelper.getCentrifugeRecipe(level, inputHandler);

        Map<ResourceLocation, CentrifugeRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        Container inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, CentrifugeRecipe> entry : allRecipes.entrySet()) {
            CentrifugeRecipe recipe = entry.getValue();
            if (recipe.matches(inv, level)) {
                currentRecipe = recipe;
                break;
            }
        }

        return currentRecipe;
    }

    protected boolean canProcessRecipe(@Nullable CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.getRecipeOutputs().forEach((stack, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(stack.getItem(), value.get(1).getAsInt());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
            Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
            boolean fluidFlag = true;
            if (fluidOutput != null) {
                fluidFlag = fluidInventory.map(h -> h.getFluidInTank(0).isEmpty() || h.getFluidInTank(0).getFluid().equals(fluidOutput.getFirst())).orElse(false);
            }

            return fluidFlag && ((InventoryHandlerHelper.ItemHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    protected void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler, RandomSource random) {
        recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
            if (random.nextInt(100) <= recipeValues.get(2).getAsInt()) {
                int count = Mth.nextInt(random, Mth.floor(recipeValues.get(0).getAsInt()), Mth.floor(recipeValues.get(1).getAsInt()));
                ItemStack output = itemStack.copy();
                output.setCount(count);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(output);
            }
        });

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);

        Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
        if (fluidOutput != null) {
            fluidInventory.ifPresent(fluidHandler -> {
                fluidHandler.fill(new FluidStack(fluidOutput.getFirst(), fluidOutput.getSecond()), IFluidHandler.FluidAction.EXECUTE);
            });
        }
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler, RandomSource random) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        CompoundTag entityData = GeneBottle.getGenes(geneBottle);
        if (entityData == null) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (String attributeName : BeeAttributes.attributeList()) {
            if (random.nextDouble() <= chance) {
                int value = entityData.getInt("bee_" + attributeName);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.getAttributeByName(attributeName), value));
            }
        }

        // Chance to get a type gene
        if (random.nextDouble() <= chance) {
            int typePurity = ProductiveBeesConfig.BEE_ATTRIBUTES.typeGenePurity.get();
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(entityData.getString("type"), random.nextInt(Math.max(0, typePurity - 5)) + 10));
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    private void completeTreatProcessing(IItemHandlerModifiable invHandler) {
        ItemStack honeyTreat = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        ListTag genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (Tag inbt : genes) {
                ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
                if (((CompoundTag) inbt).contains("purity")) {
                    int purity = ((CompoundTag) inbt).getInt("purity");
                    Gene.setPurity(insertedGene, purity);
                }
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        recipeProgress = tag.getInt("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = BuiltInRegistries.FLUID.getId(fluid);
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        tag.putInt("RecipeProgress", recipeProgress);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new CentrifugeContainer(windowId, playerInventory, this);
    }
}
