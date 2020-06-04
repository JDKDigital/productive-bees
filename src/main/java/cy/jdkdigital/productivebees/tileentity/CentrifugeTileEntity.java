package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.recipe.CentrifugeRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CentrifugeTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity
{
    private static final Random rand = new Random();

    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this)
    {
        public boolean isInputItem(Item item) {
            return item == Items.GLASS_BOTTLE || item == Items.BUCKET || ModTags.HONEYCOMBS.contains(item);
        }
        public boolean isInputSlotItem(int slot, Item item) {
            return (slot == InventoryHandlerHelper.BOTTLE_SLOT && item == Items.BUCKET) || (slot == InventoryHandlerHelper.BOTTLE_SLOT && item == Items.GLASS_BOTTLE) || (slot == InventoryHandlerHelper.INPUT_SLOT && ModTags.HONEYCOMBS.contains(item));
        }
    });

    public LazyOptional<IFluidHandler> honeyInventory = LazyOptional.of(() -> new InventoryHandlerHelper.FluidHandler(10000)
    {
        @Override
        protected void onContentsChanged()
        {
            super.onContentsChanged();
            CentrifugeTileEntity.this.tickFluidTank();
            CentrifugeTileEntity.this.markDirty();
        }

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
                else {
                    this.recipeProgress = 0;
                    world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, false));
                }
            });
        }
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

    public void tickFluidTank() {
        honeyInventory.ifPresent(honeyHandler -> {
            FluidStack honeyFluid = honeyHandler.getFluidInTank(0);
            if (honeyFluid.getAmount() >= 250) {
                inventoryHandler.ifPresent(invHandler -> {
                    ItemStack fluidContainer = invHandler.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                    int drainedHoney = 0;
                    ItemStack outputItem = null;
                    if (fluidContainer.getItem() == Items.GLASS_BOTTLE) {
                        drainedHoney = 250;
                        outputItem = new ItemStack(Items.HONEY_BOTTLE);
                    }
                    else if (fluidContainer.getItem() == Items.BUCKET && honeyFluid.getAmount() >= 1000) {
                        drainedHoney = 1000;
                        outputItem = new ItemStack(ModItems.HONEY_BUCKET.get());
                    }

                    if (drainedHoney > 0) {
                        ItemStack existingOutput = invHandler.getStackInSlot(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT);
                        if (existingOutput.isEmpty() || (existingOutput.getItem() == outputItem.getItem() && existingOutput.getCount() < outputItem.getMaxStackSize())) {
                            honeyHandler.drain(drainedHoney, IFluidHandler.FluidAction.EXECUTE);
                            invHandler.insertItem(InventoryHandlerHelper.FLUID_ITEM_OUTPUT_SLOT, outputItem, false);
                            fluidContainer.shrink(1);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        inventoryHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT fluidTag = tag.getCompound("fluid");
        honeyInventory.ifPresent(fluid -> ((INBTSerializable<CompoundNBT>) fluid).deserializeNBT(fluidTag));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        inventoryHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        honeyInventory.ifPresent(fluid -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) fluid).serializeNBT();
            tag.put("fluid", compound);
        });

        return tag;
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
