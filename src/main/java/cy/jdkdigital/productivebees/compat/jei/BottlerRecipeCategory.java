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
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BottlerRecipeCategory implements IRecipeCategory<BottlerRecipe>
{
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bottler");
    private final IDrawable icon;
    private final IDrawable background;

    public BottlerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bottler_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
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
                .addIngredients(NeoForgeTypes.FLUID_STACK, List.of(recipe.fluidInput.getFluids()))
                .setSlotName("inputFluid");
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 27)
                .addItemStacks(Arrays.stream(recipe.itemInput.getItems()).toList())
                .setSlotName("inputItem");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 27)
                .addItemStack(recipe.result)
                .setSlotName("result");
    }
}
