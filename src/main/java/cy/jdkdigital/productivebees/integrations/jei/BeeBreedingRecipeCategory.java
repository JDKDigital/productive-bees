package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.recipe.BeeBreedingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeeBreedingRecipeCategory implements IRecipeCategory<BeeBreedingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeBreedingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_breeding_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return ProductiveBeesJeiPlugin.CATEGORY_BEE_BREEDING_UID;
    }

    @Nonnull
    @Override
    public Class<? extends BeeBreedingRecipe> getRecipeClass() {
        return BeeBreedingRecipe.class;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bee_breeding");
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
    public void setIngredients(BeeBreedingRecipe recipe, IIngredients ingredients) {
        List<BeeIngredient> recipeIngredients = new ArrayList<>();
        List<List<ItemStack>> breedingItems = new ArrayList<>();
        for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
            BeeIngredient beeIngredient = ingredient.get();
            Entity bee = beeIngredient.getCachedEntity(ProductiveBees.proxy.getWorld());
            if (bee instanceof ProductiveBee productiveBee) {
                breedingItems.add(productiveBee.getBreedingItems());
            } else {
                breedingItems.add(List.of(ItemStack.EMPTY));
            }
            recipeIngredients.add(beeIngredient);
        }

        List<BeeIngredient> recipeOutputs = new ArrayList<>();
        recipeOutputs.add(recipe.offspring.get());

        ingredients.setInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipeIngredients);
        ingredients.setInputLists(VanillaTypes.ITEM, breedingItems);
        ingredients.setOutputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipeOutputs);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, BeeBreedingRecipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = recipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);
        ingredientStacks.init(0, true, 12, 17);
        ingredientStacks.init(1, true, 46, 17);
        ingredientStacks.init(2, false, 104, 17);
        ingredientStacks.set(ingredients);

        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(3, true, 10, 38);
        itemStacks.init(4, true, 44, 38);
        itemStacks.set(ingredients);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeBreedingRecipe recipe, List<? extends IFocus<?>> focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredients.get(0).get())
                .setSlotName("parent1");
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredients.get(1).get())
                .setSlotName("parent2");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.offspring.get())
                .setSlotName("offspring");

        List<List<ItemStack>> breedingItems = new ArrayList<>();
        for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
            BeeIngredient beeIngredient = ingredient.get();
            Entity bee = beeIngredient.getCachedEntity(ProductiveBees.proxy.getWorld());
            if (bee instanceof ProductiveBee productiveBee) {
                breedingItems.add(productiveBee.getBreedingItems());
            } else {
                breedingItems.add(List.of(ItemStack.EMPTY));
            }
        }

        if (breedingItems.size() == 2) {
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 38)
                    .addItemStacks(breedingItems.get(0))
                    .setSlotName("breedingItem1");

            builder.addSlot(RecipeIngredientRole.INPUT, 44, 38)
                    .addItemStacks(breedingItems.get(1))
                    .setSlotName("breedingItem2");
        }
    }
}
