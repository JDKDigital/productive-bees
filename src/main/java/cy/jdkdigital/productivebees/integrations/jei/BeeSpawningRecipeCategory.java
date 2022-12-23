package cy.jdkdigital.productivebees.integrations.jei;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningRecipeCategory implements IRecipeCategory<BeeSpawningRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS = new HashMap<Integer, List<Integer>>()
    {{
        put(0, new ArrayList<Integer>()
        {{
            add(79);
            add(18);
        }});
        put(1, new ArrayList<Integer>()
        {{
            add(97);
            add(28);
        }});
        put(2, new ArrayList<Integer>()
        {{
            add(79);
            add(38);
        }});
    }};

    public BeeSpawningRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_spawning_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COARSE_DIRT_NEST.get()));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BEE_SPAWNING_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BeeSpawningRecipe> getRecipeClass() {
        return BeeSpawningRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bee_spawning");
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(BeeSpawningRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(Lists.newArrayList(recipe.ingredient));
        List<BeeIngredient> ingredientList = new ArrayList<>();
        for (Lazy<BeeIngredient> lazyIng : recipe.output) {
            ingredientList.add(lazyIng.get());
        }
        ingredients.setOutputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, ingredientList);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeSpawningRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 26)
                .addItemStacks(Arrays.asList(recipe.ingredient.getItems()))
                .setSlotName("nestBlock");

        IntStream.range(0, recipe.output.size()).forEach((i) -> {
            List<Integer> pos = BEE_POSITIONS.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, pos.get(0), pos.get(1))
                    .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.output.get(i).get())
                    .setSlotName("spawn" + i);
            builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                    .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.output.get(i).get());
        });
    }
}
