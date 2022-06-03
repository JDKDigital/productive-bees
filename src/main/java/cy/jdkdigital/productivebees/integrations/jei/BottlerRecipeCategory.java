package cy.jdkdigital.productivebees.integrations.jei;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.common.recipe.TagOutputRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottlerRecipeCategory implements IRecipeCategory<BottlerRecipe>
{
    public static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "bottler");
    private final IDrawable icon;
    private final IDrawable background;

    public BottlerRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bottler_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.BOTTLER.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends BottlerRecipe> getRecipeClass() {
        return BottlerRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bottler");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BottlerRecipe recipe, List<? extends IFocus<?>> focuses) {
        List<Fluid> fluids = TagOutputRecipe.getAllFluidsFromName(recipe.fluidInput.getFirst());
        List<FluidStack> fluidStacks = new ArrayList<>();
        for (Fluid fluid: fluids) {
            fluidStacks.add(new FluidStack(fluid, recipe.fluidInput.getSecond()));
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 27)
                .addIngredients(VanillaTypes.FLUID, fluidStacks)
                .setSlotName("inputFluid");
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 27)
                .addItemStacks(Arrays.stream(recipe.itemInput.getItems()).toList())
                .setSlotName("inputItem");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 27)
                .addItemStack(recipe.result)
                .setSlotName("result");
    }
}
