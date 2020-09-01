package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipeCategory implements IRecipeCategory<AdvancedBeehiveRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public AdvancedBeehiveRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_produce_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.ADVANCED_OAK_BEEHIVE.get()));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_ADVANCED_BEEHIVE_UID;
    }

    @Nonnull
    @Override
    public Class<? extends AdvancedBeehiveRecipe> getRecipeClass() {
        return AdvancedBeehiveRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return I18n.format("jei.productivebees.advanced_beehive");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(@Nonnull AdvancedBeehiveRecipe recipe, @Nonnull IIngredients ingredients) {
        ingredients.setInput(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredient.get());

        List<List<ItemStack>> outputList = new ArrayList<>();
        recipe.getRecipeOutputs().forEach((stack, value) -> {
            List<ItemStack> innerList = new ArrayList<>();
            IntStream.range(value.get(0).getInt(), value.get(1).getInt() + 1).forEach((i) -> {
                ItemStack newStack = stack.copy();
                newStack.setCount(i);
                innerList.add(newStack);
            });
            outputList.add(innerList);
        });

        ingredients.setOutputLists(VanillaTypes.ITEM, outputList);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AdvancedBeehiveRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = recipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);

        ingredientStacks.init(0, true, 6, 28);
        ingredientStacks.set(ingredients);

        int startX = 68;
        int startY = 8;
        if (ingredients.getOutputs(VanillaTypes.ITEM).size() > 0) {
            List<ItemStack> outputs = ingredients.getOutputs(VanillaTypes.ITEM).iterator().next();
            int offset = ingredients.getInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT).size();
            IntStream.range(offset, outputs.size() + offset).forEach((i) -> {
                if (i > 9 + offset) {
                    return;
                }
                itemStacks.init(i, false, startX + ((i - offset) * 18), startY + ((int) Math.floor(((float) i - offset) / 3.0F) * 18));
            });
        }

        itemStacks.set(ingredients);
    }
}
