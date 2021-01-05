package cy.jdkdigital.productivebees.common.tileentity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.GeneBottle;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.util.BeeAttributes;
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
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET;
        }

        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            boolean isProcessableItem = item.equals(ModItems.GENE_BOTTLE.get()) || CentrifugeTileEntity.this.canProcessItemStack(new ItemStack(item));

            return (isProcessableItem && slot == InventoryHandlerHelper.INPUT_SLOT) || (!isProcessableItem && super.isInputSlotItem(slot, item));
        }
    });

    protected LazyOptional<IFluidHandler> fluidInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            CentrifugeTileEntity.this.fluidId = Registry.FLUID.getId(getFluid().getFluid());
            CentrifugeTileEntity.this.markDirty();
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
        if (world != null && !world.isRemote) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty() && canOperate()) {
                    // Process gene bottles
                    ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, true));
                        int totalTime = getProcessingTime();

                        if (++this.recipeProgress >= totalTime) {
                            this.completeGeneProcessing(invHandler);
                            recipeProgress = 0;
                            this.markDirty();
                        }
                    } else {
                        CentrifugeRecipe recipe = getRecipe(invHandler);
                        if (canProcessRecipe(recipe, invHandler)) {
                            world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, true));
                            int totalTime = getProcessingTime();

                            if (++this.recipeProgress >= totalTime) {
                                this.completeRecipeProcessing(recipe, invHandler);
                                recipeProgress = 0;
                                this.markDirty();
                            }
                        }
                    }
                } else {
                    this.recipeProgress = 0;
                    world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, false));
                }

                // Pull items dropped ontop
                if (--transferCooldown <= 0) {
                    transferCooldown = 20;
                    pullItems(invHandler);
                }
            });
        }
        super.tick();
    }

    private void pullItems(IItemHandlerModifiable invHandler) {
        for(ItemEntity itementity : getCaptureItems()) {
            if (canProcessItemStack(itementity.getItem())) {
                captureItem(invHandler, itementity);
            }
        }
    }

    private List<ItemEntity> getCaptureItems() {
        assert world != null;

        return Centrifuge.COLLECTION_AREA_SHAPE.toBoundingBoxList().stream().flatMap((blockPos) -> world.getEntitiesWithinAABB(ItemEntity.class, blockPos.offset(pos.getX(), pos.getY(), pos.getZ()), EntityPredicates.IS_ALIVE).stream()).collect(Collectors.toList());
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
    public void markDirty() {
        super.markDirty();
        if (this.world != null) {
            world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
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
        if (input.isEmpty() || input == ItemStack.EMPTY || world == null) {
            return null;
        }

        if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), world)) {
            return currentRecipe;
        }
        currentRecipe = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE, new RecipeWrapper(inputHandler), this.world).orElse(null);

        Map<ResourceLocation, IRecipe<IInventory>> allRecipes = world.getRecipeManager().getRecipes(CentrifugeRecipe.CENTRIFUGE);
        IInventory inv = new RecipeWrapper(inputHandler);
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
            CentrifugeRecipe recipe = (CentrifugeRecipe) entry.getValue();
            if (recipe.matches(inv, world)) {
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
                ItemStack item = new ItemStack(key.getItem(), value.get(1).getInt());
                outputList.add(item);
            });

            // Allow overfilling of fluid but don't process if the tank has a different fluid
            boolean fluidFlag = false;
            Pair<Fluid, Integer> fluidOutput = recipe.getFluidOutputs();
            if (fluidOutput != null) {
                fluidFlag = fluidInventory.map(h -> h.getFluidInTank(0).isEmpty() || h.getFluidInTank(0).getFluid().isEquivalentTo(fluidOutput.getFirst())).orElse(false);
            }

            return fluidFlag && ((InventoryHandlerHelper.ItemHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    private void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (canProcessRecipe(recipe, invHandler)) {

            recipe.getRecipeOutputs().forEach((itemStack, recipeValues) -> {
                if (ProductiveBees.rand.nextInt(100) <= recipeValues.get(2).getInt()) {
                    int count = MathHelper.nextInt(ProductiveBees.rand, MathHelper.floor(recipeValues.get(0).getInt()), MathHelper.floor(recipeValues.get(1).getInt()));
                    itemStack.setCount(count);
                    ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(itemStack);
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

        List<String> attributes = new ArrayList<String>() {{
            add("productivity");
            add("weather_tolerance");
            add("behavior");
            add("endurance");
            add("temper");
        }};

        double chance = ProductiveBeesConfig.BEE_ATTRIBUTES.genExtractChance.get();
        for (String attributeName: attributes) {
            if (ProductiveBees.rand.nextDouble() <= chance) {
                int value = entityData.getInt("bee_" + attributeName);
                ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.getAttributeByName(attributeName), value));
            }
        }

//        // Chance to get a type gene
//        if (rand.nextDouble() <= chance) {
//            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(entityData.getString("type")));
//        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT upgradeTag = tag.getCompound("upgrades");
        getUpgradeHandler().ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(upgradeTag));

        // set fluid ID for screens
        Fluid fluid = fluidInventory.map(fluidHandler -> fluidHandler.getFluidInTank(0).getFluid()).orElse(Fluids.EMPTY);
        fluidId = Registry.FLUID.getId(fluid);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag = super.write(tag);

        CompoundNBT finalTag = tag;
        getUpgradeHandler().ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            finalTag.put("upgrades", compound);
        });

        return finalTag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), -1, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        deserializeNBT(tag);
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

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.CENTRIFUGE.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new CentrifugeContainer(windowId, playerInventory, this);
    }
}
