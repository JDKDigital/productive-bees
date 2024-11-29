package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
import cy.jdkdigital.productivebees.compat.emi.BeeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class BlockConversionRecipeCategory implements IRecipeCategory<BlockConversionRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BlockConversionRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/block_conversion.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 90, 52);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.COBBLESTONE));
    }

    @Override
    public RecipeType<BlockConversionRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BLOCK_CONVERSION_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.block_conversion");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BlockConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 38, 5)
                .addIngredients(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.getBees())
                .setSlotName("source");


        if (!recipe.input.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 26)
                    .addItemStacks(Arrays.asList(recipe.input.getItems()))
                    .setSlotName("sourceBlocks");
        } else if (recipe.stateFrom.getFluidState().getType().equals(Fluids.EMPTY)) {
            if (recipe.fromDisplay.isPresent() && !recipe.fromDisplay.get().isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 5, 25)
                        .addItemStacks(Arrays.asList(recipe.fromDisplay.get().getItems()))
                        .setSlotName("sourceBlock");
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, 5, 25)
                        .addItemStack(new ItemStack(recipe.stateFrom.getBlock().asItem()))
                        .setSlotName("sourceBlock");
            }
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 26)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(new FluidStack(recipe.stateFrom.getFluidState().getType(), 1000)))
                    .setSlotName("sourceFluid");
        }

        if (recipe.stateTo.getFluidState().getType().equals(Fluids.EMPTY)) {
            if (recipe.toDisplay.isPresent() && recipe.toDisplay.get().getItems().length > 0) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 25)
                        .addItemStacks(Arrays.asList(recipe.toDisplay.get().getItems()))
                        .setSlotName("resultBlock");
            } else {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 25)
                        .addItemStack(new ItemStack(recipe.stateTo.getBlock().asItem()))
                        .setSlotName("resultBlock");
            }
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 26)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(new FluidStack(recipe.stateTo.getFluidState().getType(), 1000)))
                    .setSlotName("resultFluid");
        }
    }

    @Override
    public void draw(BlockConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.productivebees.block_conversion.chance", recipe.chance * 100)), 0, 45, 0xFF000000, false);
    }
}
