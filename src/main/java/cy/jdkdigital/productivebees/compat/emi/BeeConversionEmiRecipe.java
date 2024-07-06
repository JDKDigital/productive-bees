package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class BeeConversionEmiRecipe extends BasicEmiRecipe
{
    private final float chance;
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_conversion_recipe.png");

    public BeeConversionEmiRecipe(RecipeHolder<BeeConversionRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BEE_CONVERSION_CATEGORY, recipe.id(), 126, 70);

        this.inputs.add(BeeEmiStack.of(recipe.value().source.get()));
        this.inputs.add(EmiIngredient.of(recipe.value().item));
        this.outputs.add(BeeEmiStack.of(recipe.value().result.get()).setChance(recipe.value().chance));
        this.chance = recipe.value().chance;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 126, 70, 0, 0);

        widgets.addSlot(this.inputs.get(0), 41, 26).drawBack(false);
        widgets.addSlot(this.inputs.get(1), 9, 25);
        widgets.addSlot(this.outputs.get(0), 99, 26).drawBack(false).recipeContext(this);

        if (chance < 1f) {
            widgets.addText(Component.translatable("jei.productivebees.block_conversion.chance", (int)(chance * 100f)), 0, 60, 0xFF000000, false);
        }
    }
}
