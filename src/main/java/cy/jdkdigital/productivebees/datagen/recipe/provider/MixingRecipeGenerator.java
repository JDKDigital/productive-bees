//package cy.jdkdigital.productivebees.datagen.recipe.provider;
//
//import com.simibubi.create.AllRecipeTypes;
//import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
//import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
//import cy.jdkdigital.productivebees.datagen.recipe.builder.CreateMixingRecipeBuilder;
//import net.minecraft.resources.ResourceLocation;
//
//public class MixingRecipeGenerator
//{
//    public ProcessingRecipeBuilder<MixingRecipe> builder(ResourceLocation name) {
//        return new CreateMixingRecipeBuilder(this.getSerializer().getFactory(), name);
//    }
//
//    protected ProcessingRecipeSerializer<MixingRecipe> getSerializer() {
//        return AllRecipeTypes.MIXING.getSerializer();
//    }
//}
