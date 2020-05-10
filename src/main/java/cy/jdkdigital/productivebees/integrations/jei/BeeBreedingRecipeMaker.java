package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BeeBreedingRecipeMaker {

    public static List<Object> getRecipes() {
        List<Object> recipes = new ArrayList<>();

        for(Map.Entry<String, Map<String, List<String>>> entry: BeeHelper.breedingMap.entrySet()) {
            BeeIngredient mainInput = BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":" + entry.getKey() + "_bee");
            if (mainInput != null) {
                ResourceLocation regName = mainInput.getBeeType().getRegistryName();

                for (Map.Entry<String, List<String>> beePartner : entry.getValue().entrySet()) {
                    BeeIngredient secondInput = BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":" + beePartner.getKey() + "_bee");
                    BeeIngredient output = BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":" + beePartner.getValue().get(0) + "_bee");
                    if (secondInput != null && output != null) {
                        BeeBreedingRecipe recipe = new BeeBreedingRecipe(regName, Arrays.asList(mainInput, secondInput), output);
                        recipes.add(recipe);
                    }
                }
            } else {
                ProductiveBees.LOGGER.info("No ingredient for " + entry.getKey());
            }
        }

        return recipes;
    }
}
