package cy.jdkdigital.productivebees.integrations.jei;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.recipe.BottlerRecipe;
import cy.jdkdigital.productivebees.recipe.TagOutputRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
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
    public void setIngredients(BottlerRecipe recipe, IIngredients ingredients) {
        List<Fluid> fluids = TagOutputRecipe.getAllFluidsFromName(recipe.fluidInput.getFirst());
        List<FluidStack> fluidStacks = new ArrayList<>();
        for (Fluid fluid: fluids) {
            fluidStacks.add(new FluidStack(fluid, recipe.fluidInput.getSecond()));
        }
        ingredients.setInputs(VanillaTypes.FLUID, fluidStacks);
        ingredients.setInputIngredients(Lists.newArrayList(recipe.itemInput));
        ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(recipe.result.getItems()));
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull BottlerRecipe recipe, IIngredients ingredients) {
        IGuiFluidStackGroup fluids = iRecipeLayout.getFluidStacks();
        fluids.init(0, true, 9, 27);
        fluids.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(1, true, 42, 26);
        itemStacks.init(2, false, 98, 26);
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        itemStacks.set(2, ingredients.getOutputs(VanillaTypes.ITEM).get(0));

        fluids.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            int amount = recipe.fluidInput.getSecond();
            tooltip.add(new TranslatableComponent("productivebees.centrifuge.tooltip.amount", amount + "mB"));
        });
    }
}
