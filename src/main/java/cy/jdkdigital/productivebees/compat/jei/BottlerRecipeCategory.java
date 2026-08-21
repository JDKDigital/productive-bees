package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class BottlerRecipeCategory implements IRecipeCategory<BottlerRecipe>
{
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bottler");
    private static final int BACKGROUND_WIDTH = 126;
    private static final int BACKGROUND_HEIGHT = 70;
    private final IDrawable icon;
    private final IDrawable background;

    public BottlerRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bottler_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.BOTTLER.get()));
    }

    @Override
    public RecipeType<BottlerRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BOTTLER_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bottler");
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
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BottlerRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 27)
                .addIngredients(NeoForgeTypes.FLUID_STACK, recipe.fluidInput.ingredient().fluids().stream().map(h -> new FluidStack(h, recipe.fluidInput.amount())).toList())
                .setSlotName("inputFluid");
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 27)
                .addItemStacks(recipe.itemInput.items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                .setSlotName("inputItem");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 27)
                .addItemStack(recipe.getResult())
                .setSlotName("result");
    }
    @Override
    public void draw(BottlerRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

}
