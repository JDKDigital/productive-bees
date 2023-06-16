package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;

public interface IRecipeProcessingBlockEntity
{
    int getRecipeProgress();

    int getProcessingTime(TimedRecipeInterface recipe);

    TimedRecipeInterface getCurrentRecipe();
}
