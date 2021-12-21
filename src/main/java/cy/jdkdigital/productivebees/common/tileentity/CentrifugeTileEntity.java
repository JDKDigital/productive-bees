package cy.jdkdigital.productivebees.common.tileentity;

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
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
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

public class CentrifugeTileEntity extends FluidTankTileEntity implements INamedContainerProvider, ITickableTileEntity, UpgradeableTileEntity
{
    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;
    public int fluidId = 0;
    private int transferCooldown = -1;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isBottleItem(Item item) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            boolean isProcessableItem = item.equals(ModItems.GENE_BOTTLE.get()) || item.equals(ModItems.HONEY_TREAT.get()) || CentrifugeTileEntity.this.canProcessItemStack(new ItemStack(item));

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT) || (!isProcessableItem && super.isInputSlotItem(slot, item));
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == InventoryHandlerHelper.INPUT_SLOT && this.getStackInSlot(slot).isEmpty()) {
                CentrifugeTileEntity.this.recipeProgress = 0;
            }
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            CentrifugeTileEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            CentrifugeTileEntity.this.setChanged();
        }
    });

    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public CentrifugeTileEntity() {
        super(ModTileEntityTypes.CENTRIFUGE.get());
    }

    public CentrifugeTileEntity(TileEntityType<?> type) {
        super(type);
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

    @Override
    public void tick() {
        if (level instanceof ServerWorld) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty() && canOperate()) {
                    // Process gene bottles
                    ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(Centrifuge.RUNNING, true));
                        int totalTime = getProcessingTime();

                        if (++this.recipeProgress >= totalTime) {
                            this.completeGeneProcessing(invHandler);
                            recipeProgress = 0;
                            this.setChanged();
                        }
                    } else if (invItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(Centrifuge.RUNNING, true));
                        int totalTime = getProcessingTime();

                        if (++this.recipeProgress >= totalTime) {
                            this.completeTreatProcessing(invHandler);
                            recipeProgress = 0;
                            this.setChanged();
                        }
                    } else {
                        CentrifugeRecipe recipe = getRecipe(invHandler);
                        if (canProcessRecipe(recipe, invHandler)) {
                            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(Centrifuge.RUNNING, true));
                            int totalTime = getProcessingTime();

                            if (++this.recipeProgress >= totalTime) {
                                this.completeRecipeProcessing(recipe, invHandler);
                                recipeProgress = 0;
                                this.setChanged();
                            }
                        }
                    }
                } else {
                    level.setBlockAndUpdate(worldPosition, getBlockState().setValue(Centrifuge.RUNNING, false));
                }

                // Pull items dropped on top
                if (ProductiveBeesConfig.GENERAL.centrifugeHopperMode.get() && --transferCooldown <= 0) {
                    transferCooldown = 22;
                    suckInItems(invHandler);
                }
            });
        }
        super.tick();
    }

    @Override
    public void tickFluidTank() {
        this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(fluidHandler -> {
            FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            if (fluidStack.getAmount() > 0 && level instanceof ServerWorld) {
                Direction[] directions = Direction.values();
                for (Direction direction : directions) {
                    TileEntity te = level.getBlockEntity(worldPosition.relative(direction));
                    if (te != null && fluidStack.getAmount() > 0) {
                        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).ifPresent(h -> {
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
        return Centrifuge.COLLECTION_AREA_SHAPE.toAabbs().stream().flatMap((blockPos) -> level.getEntitiesOfClass(ItemEntity.class, blockPos.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntityPredicates.ENTITY_STILL_ALIVE).stream()).collect(Collectors.toList());
    }

    private static void captureItem(IItemHandlerModifiable invHandler, ItemEntity itemEntity) {
        ItemStack insertStack = itemEntity.getItem().copy();
        ItemStack leftoverStack = invHandler.insertItem(InventoryHandlerHelper.INPUT_SLOT, insertStack, false);

        if (leftoverStack.isEmpty()) {
            itemEntity.remove();
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

        Map<ResourceLocation, IRecipe<IInventory>> allRecipes = level.getRecipeManager().byType(CentrifugeRecipe.CENTRIFUGE);
        IInventory inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
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
            boolean fluidFlag = false;
            Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
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
                    int count = MathHelper.nextInt(ProductiveBees.rand, MathHelper.floor(recipeValues.get(0).getAsInt()), MathHelper.floor(recipeValues.get(1).getAsInt()));
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

        CompoundNBT entityData = GeneBottle.getGenes(geneBottle);
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

        ListNBT genes = HoneyTreat.getGenes(honeyTreat);
        if (!genes.isEmpty()) {
            for (INBT inbt : genes) {
                ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
                if (((CompoundNBT) inbt).contains("purity")) {
                    int purity = ((CompoundNBT) inbt).getInt("purity");
                    Gene.setPurity(insertedGene, purity);
                }
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(insertedGene);
            }
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        recipeProgress = tag.getInt("RecipeProgress");

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = Registry.FLUID.getId(fluid);
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
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidInventory.cast();
        }
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.CENTRIFUGE.get().getDescriptionId());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, @Nonnull final PlayerInventory playerInventory, @Nonnull final PlayerEntity player) {
        return new CentrifugeContainer(windowId, playerInventory, this);
    }
}
