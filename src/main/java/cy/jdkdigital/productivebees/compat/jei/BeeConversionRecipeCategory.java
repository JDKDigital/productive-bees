//package cy.jdkdigital.productivebees.compat.jei;
//
//import cy.jdkdigital.productivebees.ProductiveBees;
//import cy.jdkdigital.productivebees.common.recipe.BeeConversionRecipe;
//import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
//import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.locale.Language;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//
//import javax.annotation.Nonnull;
//import java.util.Arrays;
//
//public class BeeConversionRecipeCategory implements IRecipeCategory<BeeConversionRecipe>
//{
//    private final IDrawable background;
//    private final IDrawable icon;
//
//    public BeeConversionRecipeCategory(IGuiHelper guiHelper) {
//        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_conversion_recipe.png");
//        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
//        this.icon = guiHelper.createDrawableIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, BeeIngredientFactory.getOrCreateList().get(ProductiveBees.MODID + ":quarry_bee"));
//    }
//
//    @Override
//    public RecipeType<BeeConversionRecipe> getRecipeType() {
//        return ProductiveBeesJeiPlugin.BEE_CONVERSION_TYPE;
//    }
//
//    @Nonnull
//    @Override
//    public Component getTitle() {
//        return Component.translatable("jei.productivebees.bee_conversion");
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
//    public void setRecipe(IRecipeLayoutBuilder builder, BeeConversionRecipe recipe, IFocusGroup focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 42, 27)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.source.get())
//                .setSlotName("source");
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 28)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.result.get())
//                .setSlotName("result");
//
//        builder.addSlot(RecipeIngredientRole.INPUT, 10, 26)
//                .addItemStacks(Arrays.stream(recipe.item.getItems()).toList())
//                .setSlotName("conversionItems");
//    }
//
//    @Override
//    public void draw(BeeConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//        if (recipe.chance < 100) {
//            Minecraft minecraft = Minecraft.getInstance();
//            guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.productivebees.block_conversion.chance", recipe.chance)), 0, 60, 0xFF000000, false);
//        }
//    }
//}
