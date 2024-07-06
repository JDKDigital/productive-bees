package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

public class BlockConversionEmiRecipe extends BasicEmiRecipe
{
    private final float chance;
    ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/block_conversion.png");

    public BlockConversionEmiRecipe(RecipeHolder<BlockConversionRecipe> recipe) {
        super(ProductiveBeesEmiPlugin.BLOCK_CONVERSION_CATEGORY, recipe.id(), 90, 52);

        this.inputs.add(BeeEmiStack.of(recipe.value().getBees().get(0)));
        if (!recipe.value().input.isEmpty()) {
            this.inputs.add(EmiIngredient.of(recipe.value().input));
        } else if (recipe.value().stateFrom.getFluidState().getType().equals(Fluids.EMPTY) && recipe.value().fromDisplay.isPresent()) {
            this.inputs.add(EmiIngredient.of(recipe.value().fromDisplay.get()));
        } else {
            this.inputs.add(EmiStack.of(recipe.value().stateFrom.getFluidState().getType(), 1000));
        }

        if (recipe.value().stateTo.getFluidState().getType().equals(Fluids.EMPTY) && recipe.value().toDisplay.isPresent() && recipe.value().toDisplay.get().getItems().length > 0) {
            this.outputs.add(EmiStack.of(recipe.value().toDisplay.get().getItems()[0]).setChance(recipe.value().chance));
        } else {
            this.outputs.add(EmiStack.of(recipe.value().stateTo.getFluidState().getType()).setChance(recipe.value().chance));
        }

        this.chance = recipe.value().chance;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(location, 0, 0, 90, 52, 0, 0);

        widgets.addSlot(this.inputs.get(0), 37, 4).drawBack(false);
        widgets.addSlot(this.inputs.get(1), 4, 25);

        widgets.addSlot(this.outputs.get(0), 65, 25).recipeContext(this);

        if (chance < 1f) {
            widgets.addText(Component.translatable("jei.productivebees.block_conversion.chance", (int)(chance * 100f)), 0, 45, 0xFF000000, false);
        }
    }
}
