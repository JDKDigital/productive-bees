package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;
import net.minecraft.world.item.crafting.RecipeHolder;

public interface IRecipeProcessingBlockEntity
{
    int getRecipeProgress();

    int getProcessingTime(RecipeHolder<? extends TimedRecipeInterface> recipe);

    RecipeHolder<? extends TimedRecipeInterface> getCurrentRecipe();
}
