package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeeBreedingRecipeCategory implements IRecipeCategory<BeeBreedingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeBreedingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_breeding_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BEE_BREEDING_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BeeBreedingRecipe> getRecipeClass() {
        return BeeBreedingRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.get("jei.productivebees.bee_breeding");
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
    public void setIngredients(BeeBreedingRecipe recipe, IIngredients ingredients) {
        List<BeeIngredient> recipeIngredients = new ArrayList<>();
        for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
            recipeIngredients.add(ingredient.get());
        }

        List<BeeIngredient> recipeOutputs = new ArrayList<>();
        for (Map.Entry<Lazy<BeeIngredient>, Integer> ingredient : recipe.offspring.entrySet()) {
            recipeOutputs.add(ingredient.getKey().get());
        }

        ingredients.setInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipeIngredients);
        ingredients.setOutputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipeOutputs);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BeeBreedingRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = recipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);

        ingredientStacks.init(0, true, 8, 27);
        ingredientStacks.init(1, true, 42, 27);
        ingredientStacks.init(2, false, 100, 27);
        ingredientStacks.set(ingredients);
    }
}
