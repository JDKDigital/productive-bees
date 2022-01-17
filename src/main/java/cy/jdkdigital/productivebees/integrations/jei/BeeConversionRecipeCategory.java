package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.recipe.BeeConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

public class BeeConversionRecipeCategory implements IRecipeCategory<BeeConversionRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeConversionRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_conversion_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BEE_CONVERSION_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BeeConversionRecipe> getRecipeClass() {
        return BeeConversionRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bee_conversion");
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
    public void setIngredients(BeeConversionRecipe recipe, IIngredients ingredients) {
        ingredients.setInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, Collections.singletonList(recipe.source.get()));
        ingredients.setOutputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, Collections.singletonList(recipe.result.get()));
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.item.getItems()));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BeeConversionRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = recipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);

        ingredientStacks.init(0, true, 42, 27);
        ingredientStacks.init(1, false, 100, 28);
        ingredientStacks.set(ingredients);

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(2, true, 10, 26);
        itemStacks.set(2, ingredients.getInputs(VanillaTypes.ITEM).get(0));
    }
}
