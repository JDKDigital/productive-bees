package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.IncubationRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class IncubationEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/incubator.png");

    public IncubationEmiRecipe(RecipeHolder<IncubationRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.INCUBATION_CATEGORY, recipe.id(), 126, 70);

        this.inputs.add(EmiIngredient.of(recipe.value().input));
        this.inputs.add(EmiIngredient.of(recipe.value().catalyst));
        this.outputs.add(EmiStack.of(recipe.value().result));

    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 70, 0, 0);

        widgets.addSlot(this.inputs.get(0), 8, 8);
        widgets.addSlot(this.inputs.get(1), 36, 26);
        widgets.addSlot(this.outputs.get(0), 64, 8).recipeContext(this);
    }
}
