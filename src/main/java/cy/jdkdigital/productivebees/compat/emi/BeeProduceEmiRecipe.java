package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BeeProduceEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_produce_recipe.png");

    public BeeProduceEmiRecipe(RecipeHolder<AdvancedBeehiveRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BEE_PRODUCE_CATEGORY, recipe.id(), 130, 60);

        this.inputs.add(BeeEmiStack.of(recipe.value().ingredient.get()));

        recipe.value().getRecipeOutputs().forEach((itemStack, chancedOutput) -> this.outputs.add(EmiStack.of(itemStack).setAmount(chancedOutput.max()).setChance(chancedOutput.chance())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 130, 60, 0, 0);

        widgets.addSlot(this.inputs.get(0), 6, 26).drawBack(false);

        if (this.outputs.size() > 0) {
            int startX = 68;
            int startY = 26;
            int i = 0;
            for (EmiStack emiStack : this.outputs) {
                widgets.addSlot(emiStack, startX + (i % 3 * 18), startY + ((int) (Math.floor((i / 3.0F)) * 18))).recipeContext(this);
                i++;
            }
        }
    }
}
