//package cy.jdkdigital.productivebees.datagen.recipe.builder;
//
//import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import net.minecraft.data.recipes.FinishedRecipe;
//import net.minecraft.resources.ResourceLocation;
//import net.neoforged.neoforge.common.crafting.conditions.ICondition;
//
//import java.util.List;
//import java.util.function.Consumer;
//
//public class CreateMixingRecipeBuilder extends ProcessingRecipeBuilder<MixingRecipe>
//{
//    public CreateMixingRecipeBuilder(ProcessingRecipeFactory<MixingRecipe> factory, ResourceLocation recipeId) {
//        super(factory, recipeId);
//    }
//
//    @Override
//    public void build(Consumer<FinishedRecipe> consumer) {
//        consumer.accept(new Result(this.build(), this.recipeConditions));
//    }
//
//    public static class Result extends DataGenResult<MixingRecipe> {
//        private ResourceLocation id;
//
//        public Result(MixingRecipe recipe, List<ICondition> recipeConditions) {
//            super(recipe, recipeConditions);
//            this.id = recipe.getId();
//        }
//
//        @Override
//        public ResourceLocation getId() {
//            return this.id;
//        }
//    }
//}
