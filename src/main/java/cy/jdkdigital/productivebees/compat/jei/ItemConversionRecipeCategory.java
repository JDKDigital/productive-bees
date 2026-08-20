package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.ItemConversionRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemConversionRecipeCategory implements IRecipeCategory<ItemConversionRecipe>
{
    private static final int BACKGROUND_WIDTH = 90;
    private static final int BACKGROUND_HEIGHT = 52;
    private final IDrawable background;
    private final IDrawable icon;

    public ItemConversionRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/block_conversion.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FEEDER.get()));
    }

    @Override
    public RecipeType<ItemConversionRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.ITEM_CONVERSION_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.item_conversion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ItemConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 38, 5)
                .addIngredients(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.getBees())
                .setSlotName("source");

        builder.addSlot(RecipeIngredientRole.INPUT, 5, 25)
                .addItemStacks(recipe.ingredient.items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                .setSlotName("sourceitem");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 25)
                .addItemStacks(List.of(recipe.getResult()))
                .setSlotName("resultItem");
    }

    @Override
    public void draw(ItemConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.text(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.productivebees.block_conversion.chance", recipe.chance * 100)), 0, 45, 0xFF000000, false);
    }
}
