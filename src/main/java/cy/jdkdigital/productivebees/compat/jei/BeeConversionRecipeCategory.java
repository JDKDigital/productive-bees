package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class BeeConversionRecipeCategory implements IRecipeCategory<BeeConversionRecipe>
{
    private static final int BACKGROUND_WIDTH = 126;
    private static final int BACKGROUND_HEIGHT = 70;
    private final IDrawable background;
    private final IDrawable icon;

    public BeeConversionRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_conversion_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Override
    public RecipeType<BeeConversionRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BEE_CONVERSION_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bee_conversion");
    }

    @Override
    public int getWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int getHeight() {
        return BACKGROUND_HEIGHT;
    }

    @SuppressWarnings("unused")
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 42, 27)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.source.get())
                .setSlotName("source");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 28)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.result.get())
                .setSlotName("result");

        builder.addSlot(RecipeIngredientRole.INPUT, 10, 26)
                .addItemStacks(recipe.item.items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                .setSlotName("conversionItems");
    }

    @Override
    public void draw(BeeConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        if (recipe.chance < 1f) {
            Minecraft minecraft = Minecraft.getInstance();
            guiGraphics.text(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.productivebees.block_conversion.chance", recipe.chance * 100)), 0, 60, 0xFF000000, false);
        }
    }
}
