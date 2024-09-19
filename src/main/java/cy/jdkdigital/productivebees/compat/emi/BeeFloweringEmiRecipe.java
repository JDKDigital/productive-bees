package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeFloweringRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

public class BeeFloweringEmiRecipe extends BasicEmiRecipe
{
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_flowering_recipe.png");

    public BeeFloweringEmiRecipe(BeeFloweringRecipe recipe) {
        super(ProductiveBeesEmiPlugin.BEE_FLOWERING_CATEGORY, recipe.id(), 70, 82);

        this.inputs.add(BeeEmiStack.of(recipe.bee()));

        if (recipe.itemTag() != null) {
            this.inputs.add(EmiIngredient.of(recipe.itemTag()));
        } else if (recipe.blockTag() != null) {
            this.inputs.add(EmiIngredient.of(recipe.blockTag()));
        } else if (recipe.fluidTag() != null) {
            this.inputs.add(EmiIngredient.of(recipe.fluidTag()));
        } else if (recipe.block() != null) {
            this.inputs.add(EmiStack.of(recipe.block()));
        } else if (recipe.fluid() != null) {
            this.inputs.add(EmiStack.of(recipe.fluid()));
        } else if (recipe.item() != null) {
            this.inputs.add(EmiStack.of(recipe.item()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 70, 82, 0, 0);

        widgets.addSlot(this.inputs.get(0), 28, 11).drawBack(false);
        if (this.inputs.size() > 1) {
            widgets.addSlot(this.inputs.get(1), 25, 50);
        }
    }
}
