package cy.jdkdigital.productivebees.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class BeeBreedingRecipeCategory implements IRecipeCategory<AdvancedBeehiveRecipe> {

    private IGuiHelper guiHelper;

    public BeeBreedingRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(ProductiveBees.MODID, "bee_breeding");
    }

    @Override
    public Class<? extends AdvancedBeehiveRecipe> getRecipeClass() {
        return AdvancedBeehiveRecipe.class;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public IDrawable getBackground() {
        return null;
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    @Override
    public void setIngredients(AdvancedBeehiveRecipe advancedBeehiveRecipe, IIngredients iIngredients) {

    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, AdvancedBeehiveRecipe advancedBeehiveRecipe, IIngredients iIngredients) {

    }
}
