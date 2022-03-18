package cy.jdkdigital.productivebees.integrations.jei;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

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
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/centrifuge_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.CENTRIFUGE.get()));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_CENTRIFUGE_UID;
    }

    @Nonnull
    @Override
    public Class<? extends CentrifugeRecipe> getRecipeClass() {
        return CentrifugeRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.centrifuge");
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
    public void setIngredients(@Nonnull CentrifugeRecipe recipe, @Nonnull IIngredients ingredients) {
        ingredients.setInputIngredients(Lists.newArrayList(recipe.ingredient));

        List<List<ItemStack>> outputList = new ArrayList<>();

        recipe.getRecipeOutputs().forEach((stack, value) -> {
            List<ItemStack> innerList = new ArrayList<>();
            IntStream.range(value.get(0).getAsInt(), value.get(1).getAsInt() + 1).forEach((i) -> {
                ItemStack newStack = stack.copy();
                newStack.setCount(i);
                innerList.add(newStack);
            });
            outputList.add(innerList);
        });

        ingredients.setOutputLists(VanillaTypes.ITEM, outputList);

        Pair<Fluid, Integer> fluid = recipe.getFluidOutputs();
        if (fluid != null && fluid.getSecond() > 0) {
            int fluidAmount = fluid.getSecond() < 250 ? fluid.getSecond() * 4 : fluid.getSecond();
            ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(fluid.getFirst(), fluidAmount));
        }
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CentrifugeRecipe recipe, @Nonnull IIngredients ingredients) {
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

        // Input item
        itemStacks.init(0, true, 4, 26);

        // Output items
        int startX = 68;
        int startY = 26;
        int offset = ingredients.getInputs(VanillaTypes.ITEM).size();
        List<List<ItemStack>> itemOutputs = ingredients.getOutputs(VanillaTypes.ITEM);
        if (itemOutputs.size() > 0) {
            IntStream.range(0, itemOutputs.size()).forEach((i) -> {
                itemStacks.init(i + offset, false, startX + (i * 18), startY + ((int) Math.floor(((float) i) / 3.0F) * 18));
            });
        }
        itemStacks.set(ingredients);

        // Output fluids
        List<List<FluidStack>> fluidOutputs = ingredients.getOutputs(VanillaTypes.FLUID);
        if (fluidOutputs.size() > 0) {
            IntStream.range(itemOutputs.size(), itemOutputs.size() + fluidOutputs.size()).forEach((i) -> {
                fluidStacks.init(i + offset, false, startX + (i * 18) + 1, startY + ((int) Math.floor(((float) i) / 3.0F) * 18) + 1);
            });
        }
        fluidStacks.set(ingredients);

        List<Component> chances = new ArrayList<>();
        List<Component> amounts = new ArrayList<>();
        recipe.getRecipeOutputs().forEach((stack, value) -> {
            int chance = value.get(2).getAsInt();
            if (chance < 100) {
                chances.add(new TranslatableComponent("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));
            } else {
                chances.add(new TextComponent(""));
            }
            if (value.get(0) != value.get(1)) {
                amounts.add(new TranslatableComponent("productivebees.centrifuge.tooltip.amount", value.get(0).getAsInt() + " - " + value.get(1).getAsInt()));
            } else {
                amounts.add(new TextComponent(""));
            }
        });
        Pair<Fluid, Integer> fluid = recipe.getFluidOutputs();
        if (fluid != null) {
            chances.add(new TextComponent(""));
            amounts.add(new TranslatableComponent("productivebees.centrifuge.tooltip.amount", fluid.getSecond() + "mB"));
        }

        itemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (!input) {
                if (!chances.isEmpty() && chances.size() >= slotIndex && !chances.get(slotIndex - 1).getString().isEmpty()) {
                    tooltip.add(chances.get(slotIndex - 1));
                }
                if (!amounts.isEmpty() && amounts.size() >= slotIndex && !amounts.get(slotIndex - 1).getString().isEmpty()) {
                    tooltip.add(amounts.get(slotIndex - 1));
                }
            }
        });
        fluidStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (!chances.toString().isEmpty() && chances.size() >= slotIndex && !chances.get(slotIndex - 1).getString().isEmpty()) {
                tooltip.add(chances.get(slotIndex - 1));
            }
            if (!amounts.toString().isEmpty() && amounts.size() >= slotIndex && !amounts.get(slotIndex - 1).getString().isEmpty()) {
                tooltip.add(amounts.get(slotIndex - 1));
            }
        });
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CentrifugeRecipe recipe, List<? extends IFocus<?>> focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 27)
                .addItemStacks(Arrays.stream(recipe.ingredient.getItems()).toList())
                .setSlotName("ingredient");

        int startX = 68;
        int startY = 26;
        final int[] i = {0};
        if (recipe.getRecipeOutputs().size() > 0) {
            recipe.getRecipeOutputs().forEach((stack, value) -> {
                // Add a stack per possible output amount
                List<ItemStack> innerList = new ArrayList<>();
                IntStream.range(value.get(0).getAsInt(), value.get(1).getAsInt() + 1).forEach((u) -> {
                    ItemStack newStack = stack.copy();
                    newStack.setCount(u);
                    innerList.add(newStack);
                });

                builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                        .addItemStacks(innerList)
                        .addTooltipCallback((recipeSlotView, tooltip) -> {
                            int chance = value.get(2).getAsInt();
                            if (chance < 100) {
                                tooltip.add(new TranslatableComponent("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));
                            } else {
                                tooltip.add(new TextComponent(""));
                            }
                            if (value.get(0) != value.get(1)) {
                                tooltip.add(new TranslatableComponent("productivebees.centrifuge.tooltip.amount", value.get(0).getAsInt() + " - " + value.get(1).getAsInt()));
                            } else {
                                tooltip.add(new TextComponent(""));
                            }
                        })
                        .setSlotName("output" + i[0]);
                i[0]++;
            });
        }
        Pair<Fluid, Integer> fluid = recipe.getFluidOutputs();
        if (fluid != null && fluid.getSecond() > 0) {
            int fluidAmount = fluid.getSecond() < 250 ? fluid.getSecond() * 4 : fluid.getSecond();
            builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18) + 1, startY + ((int) Math.floor(i[0] / 3.0F) * 18) + 1)
                    .addIngredient(VanillaTypes.FLUID, new FluidStack(fluid.getFirst(), fluidAmount))
                    .addTooltipCallback((recipeSlotView, tooltip) -> {
                        tooltip.add(new TranslatableComponent("productivebees.centrifuge.tooltip.amount", fluid.getSecond() + "mB"));
                    })
                    .setSlotName("output" + i[0]);
        }
    }
}
