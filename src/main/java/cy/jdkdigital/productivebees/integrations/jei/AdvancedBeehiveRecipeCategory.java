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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
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
        return ProduciveBeesJeiPlugin.CATEGORY_ADVANCED_BEEHIVE_UID;
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
        ingredients.setInput(ProduciveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredient);

        List<ItemStack> outputList = new ArrayList<>();
        recipe.outputs.forEach((key, value) -> outputList.add(key));

        ingredients.setOutputs(VanillaTypes.ITEM, outputList);
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AdvancedBeehiveRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = recipeLayout.getIngredientsGroup(ProduciveBeesJeiPlugin.BEE_INGREDIENT);
        List<ItemStack> outputs = ingredients.getOutputs(VanillaTypes.ITEM).get(0);

        ingredientStacks.init(0, true, 6, 28);
        ingredientStacks.set(ingredients);

        int startX = 68;
        int startY = 8;
        int offset = ingredients.getInputs(ProduciveBeesJeiPlugin.BEE_INGREDIENT).size();
        IntStream.range(offset, outputs.size() + offset).forEach((i) -> {
            if (i > 9 + offset) {
                return;
            }
            itemStacks.init(i, false, startX + ((i - offset) * 18), startY + ((int) Math.floor(((float) i - offset) / 3.0F) * 18));
        });
        itemStacks.set(ingredients);
    }

    @Override
    public void draw(AdvancedBeehiveRecipe recipe, double mouseX, double mouseY) {
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        DecimalFormat decimalFormat = new DecimalFormat("##%");
        String productionChanceString = decimalFormat.format(recipe.chance);

        fontRenderer.drawString(productionChanceString, 38, 46, 0xff808080);
    }
}
