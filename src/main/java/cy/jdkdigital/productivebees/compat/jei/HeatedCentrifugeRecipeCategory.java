package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class HeatedCentrifugeRecipeCategory extends CentrifugeRecipeCategory
{
    private final IDrawable icon;

    public HeatedCentrifugeRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.HEATED_CENTRIFUGE.get()));
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.heated_centrifuge");
    }

    @Override
    public RecipeType<CentrifugeRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BLOCK_CENTRIFUGE_TYPE;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
        setRecipe(builder, recipe, focuses, true);
    }
}
