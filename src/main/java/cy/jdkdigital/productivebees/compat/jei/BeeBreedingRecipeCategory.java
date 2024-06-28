package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

public class BeeBreedingRecipeCategory implements IRecipeCategory<BeeBreedingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeBreedingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_breeding_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
        this.icon = guiHelper.createDrawableIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
    }

    @Override
    public RecipeType<BeeBreedingRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BEE_BREEDING_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bee_breeding");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BeeBreedingRecipe recipe, IFocusGroup focuses) {
        if (recipe.parent1.get() == null || recipe.parent2.get() == null || recipe.offspring.get() == null) {
            ProductiveBees.LOGGER.warn("Recipe is missing bee");
            return;
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.parent1.get())
                .setSlotName("parent1");
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.parent2.get())
                .setSlotName("parent2");
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.offspring.get())
                .setSlotName("offspring");

        BeeIngredient beeIngredient1 = recipe.parent1.get();
        Entity bee1 = beeIngredient1.getCachedEntity(Minecraft.getInstance().level);
        if (bee1 instanceof ProductiveBee productiveBee) {
            builder.addSlot(RecipeIngredientRole.INPUT, 10, 38)
                    .addItemStacks(productiveBee.getBreedingItems())
                    .setSlotName("breedingItem1");
        }
        BeeIngredient beeIngredient2 = recipe.parent2.get();
        Entity bee2 = beeIngredient2.getCachedEntity(Minecraft.getInstance().level);
        if (bee2 instanceof ProductiveBee productiveBee) {
            builder.addSlot(RecipeIngredientRole.INPUT, 44, 38)
                    .addItemStacks(productiveBee.getBreedingItems())
                    .setSlotName("breedingItem2");
        }
    }
}
