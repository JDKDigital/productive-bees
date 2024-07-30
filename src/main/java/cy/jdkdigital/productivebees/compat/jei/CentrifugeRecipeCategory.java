package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
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
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/centrifuge_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.CENTRIFUGE.get()));
    }

    @Override
    public RecipeType<CentrifugeRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.CENTRIFUGE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.centrifuge");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
        setRecipe(builder, recipe, focuses, false);
    }

    protected void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses, boolean stripWax) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 27)
                .addItemStacks(Arrays.stream(recipe.ingredient.getItems()).toList())
                .setSlotName("ingredient");

        int startX = 68;
        int startY = 26;
        final int[] i = {0};
        if (recipe.getRecipeOutputs().size() > 0) {
            recipe.getRecipeOutputs().forEach((stack, value) -> {
                if (!stripWax || !stack.is(ModTags.Common.WAX)) {
                    // Add a stack per possible output amount
                    List<ItemStack> innerList = new ArrayList<>();
                    IntStream.range(value.min(), value.max() + 1).forEach((u) -> {
                        ItemStack newStack = stack.copy();
                        newStack.setCount(u);
                        innerList.add(newStack);
                    });

                    builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                            .addItemStacks(innerList)
                            .addTooltipCallback((recipeSlotView, tooltip) -> {
                                float chance = value.chance() * 100f;
                                if (chance < 100) {
                                    tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));
                                }
                                if (value.min() != value.max()) {
                                    tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", value.min() + " - " + value.max()));
                                }
                            })
                            .setSlotName("output" + i[0]);
                    i[0]++;
                }
            });
        }
        FluidStack fluid = recipe.getFluidOutputs();
        if (!fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluid)
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", fluid.getAmount() + "mB"));
                    })
                    .setSlotName("output" + i[0]);
        }
    }
}
