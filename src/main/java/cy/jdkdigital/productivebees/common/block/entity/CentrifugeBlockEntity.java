package cy.jdkdigital.productivebees.common.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CentrifugeBlockEntity extends FluidTankBlockEntity implements UpgradeableBlockEntity
{
    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;
    public int fluidId = 0;
    public int transferCooldown = -1;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        // TOD 1.18 remove bottle and output slot completely
        @Override
        public boolean isContainerItem(Item item) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            boolean isProcessableItem = item.equals(ModItems.GENE_BOTTLE.get()) || item.equals(ModItems.HONEY_TREAT.get()) || CentrifugeBlockEntity.this.canProcessItemStack(new ItemStack(item));

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT) || (!isProcessableItem && super.isInputSlotItem(slot, item));
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CentrifugeBlockEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            CentrifugeBlockEntity.this.setChanged();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public CentrifugeBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.CENTRIFUGE.get(), pos, state);
    }

    public CentrifugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public int getProcessingTime() {
        return (int) (
            ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get() * getProcessingTimeModifier()
        );
    }

    protected double getProcessingTimeModifier() {
        double combBlockUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) * ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());

        return Math.max(0, timeUpgradeModifier + combBlockUpgradeModifier);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(invHandler -> {
            if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty() && blockEntity.canOperate()) {
                // Process gene bottles
                ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime();

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeGeneProcessing(invHandler);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                    level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                    int totalTime = blockEntity.getProcessingTime();

                    if (++blockEntity.recipeProgress >= totalTime) {
                        blockEntity.completeTreatProcessing(invHandler);
                        blockEntity.recipeProgress = 0;
                        blockEntity.setChanged();
                    }
                } else {
                    CentrifugeRecipe recipe = blockEntity.getRecipe(invHandler);
                    if (blockEntity.canProcessRecipe(recipe, invHandler)) {
                        level.setBlockAndUpdate(pos, state.setValue(Centrifuge.RUNNING, true));
                        int totalTime = blockEntity.getProcessingTime();

                        if (++blockEntity.recipeProgress >= totalTime) {
                            blockEntity.completeRecipeProcessing(recipe, invHandler);
                            blockEntity.recipeProgress = 0;
                            blockEntity.setChanged();
                        }
                    }
                }
            } else {
                blockEntity.recipeProgress = 0;
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
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() > 0) {
                Direction[] directions = Direction.values();
                for (Direction direction : directions) {
                    BlockEntity te = level.getBlockEntity(worldPosition.relative(direction));
                    if (te != null && fluidStack.getAmount() > 0) {
                        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(h -> {
                            int amount = h.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                            fluidHandler.drain(amount, IFluidHandler.FluidAction.EXECUTE);
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
        ItemStack insertStack = itemEntity.getItem().copy();
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT, insertStack, false);

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

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    public boolean canProcessItemStack(ItemStack stack) {
        IItemHandlerModifiable inv = new InventoryHandlerHelper.ItemHandler(2, null);
        inv.setStackInSlot(InventoryHandlerHelper.INPUT_SLOT, stack);

        CentrifugeRecipe recipe = getRecipe(inv);

        return recipe != null;
    }

    private CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || input == ItemStack.EMPTY || level == null) {
            return null;
        }

        if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), level)) {
            return currentRecipe;
        }

        currentRecipe = BeeHelper.getCentrifugeRecipe(level.getRecipeManager(), inputHandler);

        Map<ResourceLocation, Recipe<Container>> allRecipes = level.getRecipeManager().byType(CentrifugeRecipe.CENTRIFUGE);
        Container inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, Recipe<Container>> entry : allRecipes.entrySet()) {
            CentrifugeRecipe recipe = (CentrifugeRecipe) entry.getValue();
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

            recipe.getRecipeOutputs().forEach((key, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(key.getItem(), value.get(1).getAsInt());
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

    private void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (canProcessRecipe(recipe, invHandler)) {
            recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
                if (ProductiveBees.rand.nextInt(100) <= recipeValues.get(2).getAsInt()) {
                    int count = Mth.nextInt(ProductiveBees.rand, Mth.floor(recipeValues.get(0).getAsInt()), Mth.floor(recipeValues.get(1).getAsInt()));
                    itemStack.setCount(count);
                    ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(itemStack.copy());
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
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        CompoundTag entityData = GeneBottle.getGenes(geneBottle);
        if (entityData == null) {
            return;
        }

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.geneExtractChance.get();
        for (String attributeName : BeeAttributes.attributeList()) {
            if (ProductiveBees.rand.nextDouble() <= chance) {
                int value = entityData.getInt("bee_" + attributeName);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.getAttributeByName(attributeName), value));
            }
        }

        // Chance to get a type gene
        if (ProductiveBees.rand.nextDouble() <= chance) {
            int typePurity = ProductiveBeesConfig.BEE_ATTRIBUTES.typeGenePurity.get();
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(entityData.getString("type"), ProductiveBees.rand.nextInt(Math.max(0, typePurity - 5)) + 10));
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
    public void load(CompoundTag tag) {
        super.load(tag);

        recipeProgress = tag.getInt("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = Registry.FLUID.getId(fluid);
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
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
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public Component getName() {
        return new TranslatableComponent(ModBlocks.CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new CentrifugeContainer(windowId, playerInventory, this);
    }
}
