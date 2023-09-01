package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BeeSpawningRecipeCategory implements IRecipeCategory<BeeSpawningRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS = new HashMap<Integer, List<Integer>>()
    {{
        put(0, new ArrayList<>() {{
            add(79);
            add(18);
        }});
        put(1, new ArrayList<>() {{
            add(97);
            add(28);
        }});
        put(2, new ArrayList<>() {{
            add(79);
            add(38);
        }});
    }};

    public BeeSpawningRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_spawning_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.COARSE_DIRT_NEST.get()));
    }

    @Override
    public RecipeType<BeeSpawningRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BEE_SPAWNING_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bee_spawning");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BeeSpawningRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 36, 27)
                .addItemStacks(Arrays.asList(recipe.ingredient.getItems()))
                .setSlotName("nestBlock");
        builder.addSlot(RecipeIngredientRole.INPUT, 11, 27)
                .addItemStacks(Arrays.asList(recipe.spawnItem.getItems()))
                .setSlotName("spawnItem");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 27)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.output.get(0).get())
                .setSlotName("spawn");
    }
}
