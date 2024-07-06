package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BlockCentrifugeEmiRecipe extends CentrifugeEmiRecipe
{
    public BlockCentrifugeEmiRecipe(RecipeHolder<CentrifugeRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BLOCK_CENTRIFUGE_CATEGORY, recipe, true);
    }
}
