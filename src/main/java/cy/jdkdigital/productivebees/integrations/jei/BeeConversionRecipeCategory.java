package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BeeConversionRecipeCategory implements IRecipeCategory<BeeConversionRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeConversionRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_conversion_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BEE_CONVERSION_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BeeConversionRecipe> getRecipeClass() {
        return BeeConversionRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bee_conversion");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeConversionRecipe recipe, List<? extends IFocus<?>> focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 27)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.source.get())
                .setSlotName("source");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 28)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.result.get())
                .setSlotName("result");

        builder.addSlot(RecipeIngredientRole.INPUT, 10, 26)
                .addItemStacks(Arrays.stream(recipe.item.getItems()).toList())
                .setSlotName("conversionItems");
    }
}
