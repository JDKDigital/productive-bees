package cy.jdkdigital.productivebees.integrations.jei;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
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

        for(Map.Entry<String, Map<String, String>> entry: BeeHelper.breedingMap.entrySet()) {
            ProduciveBeesJeiPlugin.BeeIngredient mainInput = BeeIngredientHelper.ingredientList.get(ProductiveBees.MODID + ":" + entry.getKey() + "_bee");
            if (mainInput != null) {
                ResourceLocation regName = mainInput.getBeeType().getRegistryName();

                for (Map.Entry<String, String> beePartner : entry.getValue().entrySet()) {
                    ProduciveBeesJeiPlugin.BeeIngredient secondInput = BeeIngredientHelper.ingredientList.get(ProductiveBees.MODID + ":" + beePartner.getKey() + "_bee");
                    ProduciveBeesJeiPlugin.BeeIngredient output = BeeIngredientHelper.ingredientList.get(ProductiveBees.MODID + ":" + beePartner.getValue() + "_bee");
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
