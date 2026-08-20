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
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipeCategory implements IRecipeCategory<CentrifugeRecipe>
{
    protected static final int BACKGROUND_WIDTH = 126;
    protected static final int BACKGROUND_HEIGHT = 70;
    private final IDrawable background;
    private final IDrawable icon;

    public CentrifugeRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/centrifuge_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
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
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses) {
        setRecipe(builder, recipe, focuses, false);
    }

    protected void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, IFocusGroup focuses, boolean stripWax) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 27)
                .addItemStacks(stacksWithComponents(recipe.ingredient))
                .setSlotName("ingredient");

        int startX = 68;
        int startY = 26;
        final int[] i = {0};
        if (recipe.getRecipeOutputs().size() > 0) {
            recipe.getRecipeOutputs().forEach((stack, value) -> {
                if (!stripWax || !stack.is(ModTags.Common.WAXES)) {
                    // Add a stack per possible output amount
                    List<ItemStack> innerList = new ArrayList<>();
                    IntStream.range(value.min(), value.max() + 1).forEach((u) -> {
                        ItemStack newStack = stack.copy();
                        newStack.setCount(u);
                        innerList.add(newStack);
                    });

                    builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                            .addItemStacks(innerList)
                            .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                                float chance = value.chance() * 100f;
                                if (chance < 100) {
                                    tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));
                                }
                                if (value.min() != value.max()) {
                                    tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", value.min() + " - " + value.max()));
                                }
                            })
                            .setSlotName("output" + i[0]);
                    if (i[0] == 2) {
                        i[0] = 0;
                    } else {
                        i[0]++;
                    }
                }
            });
        }
        FluidStack fluid = recipe.getFluidOutputs();
        if (!fluid.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                    .addIngredient(NeoForgeTypes.FLUID_STACK, fluid)
                    .addRichTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", fluid.getAmount() + "mB"));
                    })
                    .setSlotName("output" + i[0]);
        }
    }
    @Override
    public void draw(CentrifugeRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    static List<ItemStack> stacksWithComponents(Ingredient ingredient) {
        DataComponentIngredient componentBound = ingredient.getCustomIngredient() instanceof DataComponentIngredient dci ? dci : null;
        List<ItemStack> stacks = new ArrayList<>();
        ingredient.items().forEach(holder -> {
            ItemStack stack = new ItemStack(holder);
            if (componentBound != null) {
                stack.applyComponents(componentBound.components());
            }
            stacks.add(stack);
        });
        return stacks;
    }
}
