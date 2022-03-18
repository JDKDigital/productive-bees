package cy.jdkdigital.productivebees.datagen.recipe.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class AbstractRecipeBuilder implements RecipeBuilder {
    public static class IngredientOutput {
        private Ingredient ingredient;
        private int chance;

        public IngredientOutput(Ingredient ingredient, int chance) {
            this.ingredient = ingredient;
            this.chance = chance;
        }

        public IngredientOutput(Ingredient ingredient) {
            this(ingredient, 100);
        }

        public JsonElement toJson() {
            JsonObject output = new JsonObject();
            output.add("item", ingredient.toJson());
            if (chance != 100) {
                output.addProperty("chance", chance);
            }
            return output;
        }
    }
    public static class FluidOutput {
        private String fluidString;
        private int amount;

        public FluidOutput(String fluidString, int amount) {
            this.fluidString = fluidString;
            this.amount = amount;
        }

        public FluidOutput(String fluidString) {
            this(fluidString, 100);
        }

        public JsonElement toJson() {
            JsonObject output = new JsonObject();
            JsonObject fluid = new JsonObject();
            if (fluidString.startsWith("#")) {
                fluid.addProperty("tag", fluidString.substring(1));
            } else {
                fluid.addProperty("fluid", fluidString);
            }

            output.add("fluid", fluid);
            output.addProperty("amount", amount);
            return output;
        }
    }
}
