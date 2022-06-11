//package cy.jdkdigital.productivebees.integrations.jei;
//
//import cy.jdkdigital.productivebees.ProductiveBees;
//import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
//import cy.jdkdigital.productivebees.init.ModBlocks;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocus;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//
//import javax.annotation.Nonnull;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.IntStream;
//
//public class AdvancedBeehiveRecipeCategory implements IRecipeCategory<AdvancedBeehiveRecipe>
//{
//    private final IDrawable background;
//    private final IDrawable icon;
//
//    public AdvancedBeehiveRecipeCategory(IGuiHelper guiHelper) {
//        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_produce_recipe.png");
//        this.background = guiHelper.createDrawable(location, 0, 0, 126, 70);
//        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.ADVANCED_OAK_BEEHIVE.get()));
//    }
//
//    @Nonnull
//    @Override
//    public ResourceLocation getUid() {
//        return ProductiveBeesJeiPlugin.CATEGORY_ADVANCED_BEEHIVE_UID;
//    }
//
//    @Nonnull
//    @Override
//    public Class<? extends AdvancedBeehiveRecipe> getRecipeClass() {
//        return AdvancedBeehiveRecipe.class;
//    }
//
//    @Nonnull
//    @Override
//    public Component getTitle() {
//        return Component.translatable("jei.productivebees.advanced_beehive");
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
//        return icon;
//    }
//
//    @Override
//    public void setRecipe(IRecipeLayoutBuilder builder, AdvancedBeehiveRecipe recipe, List<? extends IFocus<?>> focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 7, 27)
//                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.ingredient.get())
//                .setSlotName("source");
//
//        if (recipe.getRecipeOutputs().size() > 0) {
//            int startX = 69;
//            int startY = 27;
//            final int[] i = {0};
//            recipe.getRecipeOutputs().forEach((stack, value) -> {
//                // Add a stack per possible output amount
//                List<ItemStack> innerList = new ArrayList<>();
//                IntStream.range(value.get(0).getAsInt(), value.get(1).getAsInt() + 1).forEach((amount) -> {
//                    ItemStack newStack = stack.copy();
//                    newStack.setCount(amount);
//                    innerList.add(newStack);
//                });
//
//                builder.addSlot(RecipeIngredientRole.OUTPUT, startX + (i[0] * 18), startY + ((int) Math.floor((i[0] / 3.0F) * 18)))
//                        .addItemStacks(innerList)
//                        .addTooltipCallback((recipeSlotView, tooltip) -> {
//                            int chance = value.get(2).getAsInt();
//                            if (chance < 100) {
//                                tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.chance", chance < 1 ? "<1%" : chance + "%"));
//                            } else {
//                                tooltip.add(Component.literal(""));
//                            }
//                            if (value.get(0) != value.get(1)) {
//                                tooltip.add(Component.translatable("productivebees.centrifuge.tooltip.amount", value.get(0).getAsInt() + " - " + value.get(1).getAsInt()));
//                            } else {
//                                tooltip.add(Component.literal(""));
//                            }
//                        })
//                        .setSlotName("output" + i[0]);
//                i[0]++;
//            });
//        }
//    }
//}
