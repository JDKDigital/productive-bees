package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.datagen.recipe.builder.AbstractRecipeBuilder;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import java.util.function.Consumer;

public class CentrifugeRecipeProvider extends RecipeProvider {
    public CentrifugeRecipeProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
//        CentrifugeRecipeBuilder.configurable("productivebees:skeletal")
//            .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.BONE_MEAL), 70))
////            .withCondition(new NotCondition(new TagEmptyCondition("forge:wax")))
//            .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_bone"));
    }
}
