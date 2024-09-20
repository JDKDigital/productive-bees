package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModTags;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

public class CentrifugeEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/centrifuge_recipe.png");

    public CentrifugeEmiRecipe(RecipeHolder<CentrifugeRecipe> recipe) {
        this(ProductiveBeesEmiPlugin.CENTRIFUGE_CATEGORY, recipe, false);
    }

    public CentrifugeEmiRecipe(EmiRecipeCategory category, RecipeHolder<CentrifugeRecipe> recipe, boolean stripWax) {
        super(category, recipe.id(), 126, 70);

        this.inputs.add(EmiIngredient.of(recipe.value().ingredient));

        recipe.value().getRecipeOutputs().forEach((itemStack, chancedOutput) -> {
            if (!stripWax || !itemStack.is(ModTags.Common.WAXES)) {
                this.outputs.add(EmiStack.of(itemStack).setAmount(chancedOutput.max()).setChance(chancedOutput.chance()));
            }
        });

        FluidStack fluid = recipe.value().getFluidOutputs();
        if (!fluid.isEmpty()) {
            this.outputs.add(EmiStack.of(fluid.getFluid(), fluid.getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 70, 0, 0);

        widgets.addSlot(this.inputs.get(0), 4, 26);

        int startX = 67;
        int startY = 25;
        int i = 0;
        for (EmiStack stack : this.outputs) {
            widgets.addSlot(stack, startX + ((3-i)%3 * 18) + 1, startY + ((int) Math.floor(i / 3.0F) * 18) + 1).recipeContext(this);
            i++;
        }
    }
}
