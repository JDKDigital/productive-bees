package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipeCategory implements IRecipeCategory<AdvancedBeehiveRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public AdvancedBeehiveRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_produce_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.HIVES.get("advanced_oak_beehive").get()));
    }

    @Override
    public RecipeType<AdvancedBeehiveRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.ADVANCED_BEEHIVE_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.advanced_beehive");
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
    public void setRecipe(IRecipeLayoutBuilder builder, AdvancedBeehiveRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 7, 27)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredient.get())
                .setSlotName("source");

        if (recipe.getRecipeOutputs().size() > 0) {
            int startX = 69;
            int startY = 27;
            int i = 0;
            for (Map.Entry<ItemStack, IntArrayTag> entry : recipe.getRecipeOutputs().entrySet()) {
                IntArrayTag countRange = entry.getValue();

                // Add a stack per possible output amount
                List<ItemStack> innerList = new ArrayList<>();
                IntStream.range(countRange.get(0).getAsInt(), countRange.get(1).getAsInt() + 1).forEach((amount) -> {
                    ItemStack newStack = entry.getKey().copy();
                    newStack.setCount(amount);
                    innerList.add(newStack);
                });

                builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i%3 * 18), startY + ((int) (Math.floor((i / 3.0F)) * 18)))
                        .addItemStacks(innerList)
                        .addTooltipCallback((recipeSlotView, tooltip) -> {
                            int chance = countRange.get(2).getAsInt();
                            tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));

                            if (countRange.get(0) != countRange.get(1)) {
                                tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", countRange.get(0).getAsInt() + " - " + countRange.get(1).getAsInt()));
                            } else {
                                tooltip.add(Component.literal(""));
                            }
                        })
                        .setSlotName("output" + i);
                i++;
            }
        }
    }
}
