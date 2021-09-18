package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.BlockConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class BlockConversionRecipeCategory implements IRecipeCategory<BlockConversionRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BlockConversionRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/block_conversion.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 90, 52);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.COBBLESTONE));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BLOCK_CONVERSION_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BlockConversionRecipe> getRecipeClass() {
        return BlockConversionRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.get("jei.productivebees.block_conversion");
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
    public void setIngredients(BlockConversionRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, Collections.singletonList(recipe.bee.get()));

        if (recipe.stateFrom.getFluidState().getType().equals(Fluids.EMPTY)) {
            ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.fromDisplay.getItems()));
        } else {
            ingredients.setInputs(VanillaTypes.FLUID, Collections.singletonList(new FluidStack(recipe.stateFrom.getFluidState().getType(), 1000)));
        }
        if (recipe.stateTo.getFluidState().getType().equals(Fluids.EMPTY)) {
            ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.toDisplay.getItems()));
        } else {
            ingredients.setOutputs(VanillaTypes.FLUID, Collections.singletonList(new FluidStack(recipe.stateTo.getFluidState().getType(), 1000)));
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BlockConversionRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> beeIngredients = recipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);

        beeIngredients.init(0, true, 38, 5);
        beeIngredients.set(ingredients);

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        if (recipe.stateFrom.getFluidState().getType().equals(Fluids.EMPTY)) {
            itemStacks.init(1, true, 5, 25);
        } else {
            fluidStacks.init(1, true, 5, 26);
        }
        if (recipe.stateTo.getFluidState().getType().equals(Fluids.EMPTY)) {
            itemStacks.init(2, false, 65, 25);
        } else {
            fluidStacks.init(2, false, 65, 26);
        }
        itemStacks.set(ingredients);
        fluidStacks.set(ingredients);
    }
}
