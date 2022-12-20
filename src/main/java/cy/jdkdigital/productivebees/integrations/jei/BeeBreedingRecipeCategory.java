//package cy.jdkdigital.productivebees.integrations.jei;
//
//import cy.jdkdigital.productivebees.ProductiveBees;
//import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
//import cy.jdkdigital.productivebees.common.recipe.BeeBreedingRecipe;
//import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
//import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.common.util.Lazy;
//
//import javax.annotation.Nonnull;
//import java.util.ArrayList;
//import java.util.List;
//
//public class BeeBreedingRecipeCategory implements IRecipeCategory<BeeBreedingRecipe>
//{
//    private final IDrawable background;
//    private final IDrawable icon;
//
//    public BeeBreedingRecipeCategory(IGuiHelper guiHelper) {
//        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_breeding_recipe.png");
//        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
//        this.icon = guiHelper.createDrawableIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
//    }
//
//    @Override
//    public RecipeType<BeeBreedingRecipe> getRecipeType() {
//        return ProductiveBeesJeiPlugin.BEE_BREEDING_TYPE;
//    }
//
//    @Nonnull
//    @Override
//    public Component getTitle() {
//        return Component.translatable("jei.productivebees.bee_breeding");
//    }
//
//    @Nonnull
//    @Override
//    public IDrawable getBackground() {
//        return this.background;
//    }
//
//    @Nonnull
//    @Override
//    public IDrawable getIcon() {
//        return this.icon;
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, BeeBreedingRecipe recipe, IFocusGroup focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 12, 17)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredients.get(0).get())
//                .setSlotName("parent1");
//        builder.addSlot(RecipeIngredientRole.INPUT, 46, 17)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredients.get(1).get())
//                .setSlotName("parent2");
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 17)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.offspring.get())
//                .setSlotName("offspring");
//
//        List<List<ItemStack>> breedingItems = new ArrayList<>();
//        for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
//            BeeIngredient beeIngredient = ingredient.get();
//            Entity bee = beeIngredient.getCachedEntity(ProductiveBees.proxy.getWorld());
//            if (bee instanceof ProductiveBee productiveBee) {
//                breedingItems.add(productiveBee.getBreedingItems());
//            } else {
//                breedingItems.add(List.of(ItemStack.EMPTY));
//            }
//        }
//
//        if (breedingItems.size() == 2) {
//            builder.addSlot(RecipeIngredientRole.INPUT, 10, 38)
//                    .addItemStacks(breedingItems.get(0))
//                    .setSlotName("breedingItem1");
//
//            builder.addSlot(RecipeIngredientRole.INPUT, 44, 38)
//                    .addItemStacks(breedingItems.get(1))
//                    .setSlotName("breedingItem2");
//        }
//    }
//}
