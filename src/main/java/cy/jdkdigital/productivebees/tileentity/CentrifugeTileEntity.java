package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.Centrifuge;
import cy.jdkdigital.productivebees.container.CentrifugeContainer;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class CentrifugeTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    private static final Random rand = new Random();

    private CentrifugeRecipe currentRecipe = null;
    public int recipeProgress = 0;

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new ItemHandlerHelper.ItemHandler(12, this) {
        public boolean isInputItem(Item item) {
            return item == Items.GLASS_BOTTLE || ModTags.HONEYCOMBS.contains(item);
        }
    });

    public CentrifugeTileEntity() {
        super(ModTileEntityTypes.CENTRIFUGE.get());
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            this.inventoryHandler.ifPresent(handler -> {
                if (!handler.getStackInSlot(ItemHandlerHelper.INPUT_SLOT).isEmpty() && !handler.getStackInSlot(ItemHandlerHelper.BOTTLE_SLOT).isEmpty()) {
                    CentrifugeRecipe recipe = getRecipe(handler);
                    boolean isValidRecipe = this.canProcessRecipe(recipe, handler);
                    if (isValidRecipe) {
                        world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, true));
                        int totalTime = ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get();

                        if (++this.recipeProgress == totalTime) {
                            recipeProgress = 0;
                            this.completeRecipeProcessing(recipe, handler);
                        }
                    }
                } else {
                    this.recipeProgress = 0;
                    world.setBlockState(pos, getBlockState().with(Centrifuge.RUNNING, false));
                }
            });
        }
    }

    private CentrifugeRecipe getRecipe(IItemHandlerModifiable inputHandler) {
        ItemStack input = inputHandler.getStackInSlot(ItemHandlerHelper.INPUT_SLOT);
        if (input.isEmpty() || input == ItemStack.EMPTY) {
            return null;
        }

        if (currentRecipe != null && currentRecipe.matches(new RecipeWrapper(inputHandler), world)) {
            return currentRecipe;
        }
        CentrifugeRecipe recipe = world.getRecipeManager().getRecipe(CentrifugeRecipe.CENTRIFUGE, new RecipeWrapper(inputHandler), this.world).orElse(null);

        currentRecipe = recipe;

        return recipe;
    }

    private boolean canProcessRecipe(@Nullable CentrifugeRecipe recipe, IItemHandlerModifiable outputHandler) {
        if (recipe != null) {
            // Check if output slots has space for recipe output
            List<ItemStack> outputList = Lists.newArrayList();
            outputList.add(new ItemStack(Items.HONEY_BOTTLE));
            recipe.output.forEach((key, value) -> outputList.add(key));
            return ((ItemHandlerHelper.ItemHandler) outputHandler).canFitStacks(outputList);
        }
        return false;
    }

    private void completeRecipeProcessing(CentrifugeRecipe recipe, IItemHandlerModifiable invHandler) {
        if (this.canProcessRecipe(recipe, invHandler)) {
            ((ItemHandlerHelper.ItemHandler) invHandler).addOutput(new ItemStack(Items.HONEY_BOTTLE));

            recipe.output.forEach((itemStack, bounds) -> {
                int count = MathHelper.nextInt(rand, MathHelper.floor(bounds.getLeft()), MathHelper.floor(bounds.getRight()));
                itemStack.setCount(count);
                ((ItemHandlerHelper.ItemHandler) invHandler).addOutput(itemStack);
            });

            invHandler.getStackInSlot(ItemHandlerHelper.BOTTLE_SLOT).shrink(1);
            invHandler.getStackInSlot(ItemHandlerHelper.INPUT_SLOT).shrink(1);
        }
        recipeProgress = 0;
        this.markDirty();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        inventoryHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        inventoryHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        return tag;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
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
