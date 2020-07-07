package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.item.Gene;
import cy.jdkdigital.productivebees.item.GeneBottle;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
import java.util.Random;

public class CentrifugeTileEntity extends FluidTankTileEntity implements INamedContainerProvider
{
    private static final Random rand = new Random();

    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        @Override
        public boolean isInputItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || ModTags.HONEYCOMBS.contains(item);
        }

        @Override
        public boolean isInputSlotItem(int slot, Item item) {
            return (slot == InventoryHandlerHelper.BOTTLE_SLOT && item == Items.BUCKET) ||
                    (slot == InventoryHandlerHelper.BOTTLE_SLOT && item == Items.GLASS_BOTTLE) ||
                    (slot == InventoryHandlerHelper.INPUT_SLOT && ModTags.HONEYCOMBS.contains(item)) ||
                    (slot == InventoryHandlerHelper.INPUT_SLOT && item.equals(ModItems.GENE_BOTTLE.get()));
        }
    });

    public LazyOptional<IFluidHandler> honeyInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            CentrifugeTileEntity.this.markDirty();
        }

        @Override
        public boolean isFluidValid(FluidStack stack)
        {
            return stack.getFluid().isIn(ModTags.HONEY);
        }
    });

    public CentrifugeTileEntity() {
        super(ModTileEntityTypes.CENTRIFUGE.get());
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            inventoryHandler.ifPresent(invHandler -> {
                if (!invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).isEmpty()) {
                    // Process gene bottles
                    ItemStack invItem = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
                    if (invItem.getItem().equals(ModItems.GENE_BOTTLE.get())) {
                        world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, true));
                        int totalTime = ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get();

                        if (++this.recipeProgress == totalTime) {
                            recipeProgress = 0;
                            this.completeGeneProcessing(invHandler);
                        }
                    } else {
                        CentrifugeRecipe recipe = getRecipe(invHandler);
                        boolean isValidRecipe = this.canProcessRecipe(recipe, invHandler);
                        if (isValidRecipe) {
                            world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, true));
                            int totalTime = ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get();

                            if (++this.recipeProgress == totalTime) {
                                recipeProgress = 0;
                                this.completeRecipeProcessing(recipe, invHandler);
                            }
                        }
                    }
                }
                else {
                    this.recipeProgress = 0;
                    world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, false));
                }
            });
        }
        super.tick();
    }

    private CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || input == ItemStack.EMPTY) {
            return null;
        }

        if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), world)) {
            return currentRecipe;
        }
        currentRecipe = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE, new RecipeWrapper(inputHandler), this.world).orElse(null);

        return currentRecipe;
    }

    private boolean canProcessRecipe(@Nullable CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();

            recipe.output.forEach((key, value) -> {
                // Check for item with max possible output
                ItemStack item = new ItemStack(key.getItem(), value.get(1).getInt());
                outputList.add(item);
            });
            return ((InventoryHandlerHelper.ItemHandler) invHandler).canFitStacks(outputList);
        }
        return false;
    }

    private void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (this.canProcessRecipe(recipe, invHandler)) {

            honeyInventory.ifPresent(honeyHandler -> {
                honeyHandler.fill(new FluidStack(ModFluids.HONEY.get(), 250), IFluidHandler.FluidAction.EXECUTE);
            });

            recipe.output.forEach((itemStack, recipeValues) -> {
                if (rand.nextInt(100) <= recipeValues.get(2).getInt()) {
                    int count = MathHelper.nextInt(rand, MathHelper.floor(recipeValues.get(0).getInt()), MathHelper.floor(recipeValues.get(1).getInt()));
                    itemStack.setCount(count);
                    ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(itemStack);
                }
            });

            invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);
        }
        recipeProgress = 0;
        this.markDirty();
    }

    private void completeGeneProcessing(IItemHandlerModifiable invHandler) {
        ItemStack geneBottle = invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

        CompoundNBT entityData = GeneBottle.getGenesTag(geneBottle);
        ProductiveBees.LOGGER.info("Data: " + entityData);
        int productivity = entityData.getInt("bee_productivity");
        int tolerance = entityData.getInt("bee_weather_tolerance");
        int behavior = entityData.getInt("bee_behavior");
        int endurance = entityData.getInt("bee_endurance");
        int temper = entityData.getInt("bee_temper");

        if (rand.nextFloat() > 0.85F) {
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.PRODUCTIVITY, productivity));
        }
        if (rand.nextFloat() > 0.85F) {
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.WEATHER_TOLERANCE, tolerance));
        }
        if (rand.nextFloat() > 0.85F) {
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.BEHAVIOR, behavior));
        }
        if (rand.nextFloat() > 0.85F) {
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.ENDURANCE, endurance));
        }
        if (rand.nextFloat() > 0.85F) {
            ((InventoryHandlerHelper.ItemHandler) invHandler).addOutput(Gene.getStack(BeeAttributes.TEMPER, temper));
        }

        invHandler.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).shrink(1);

        recipeProgress = 0;
        this.markDirty();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return honeyInventory.cast();
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
