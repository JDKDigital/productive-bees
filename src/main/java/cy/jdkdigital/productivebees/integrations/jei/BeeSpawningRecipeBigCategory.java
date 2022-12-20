//package cy.jdkdigital.productivebees.integrations.jei;
//
//import cy.jdkdigital.productivebees.ProductiveBees;
//import cy.jdkdigital.productivebees.common.recipe.BeeSpawningBigRecipe;
//import cy.jdkdigital.productivebees.init.ModBlocks;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//
//import javax.annotation.Nonnull;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.IntStream;
//
//public class BeeSpawningRecipeBigCategory implements IRecipeCategory<BeeSpawningBigRecipe>
//{
//    private final IDrawable background;
//    private final IDrawable icon;
//
//    public static final HashMap<Integer, List<Integer>> BEE_POSITIONS = new HashMap<>() {{
//        put(0, new ArrayList<>() {{
//            add(66);
//            add(18);
//        }});
//        put(1, new ArrayList<>() {{
//            add(66);
//            add(38);
//        }});
//        put(2, new ArrayList<>() {{
//            add(84);
//            add(28);
//        }});
//        put(3, new ArrayList<>() {{
//            add(102);
//            add(18);
//        }});
//        put(4, new ArrayList<>() {{
//            add(102);
//            add(38);
//        }});
//    }};
//
//    public BeeSpawningRecipeBigCategory(IGuiHelper guiHelper) {
//        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_spawning_recipe_big.png");
//        background = guiHelper.createDrawable(location, 0, 0, 126, 70);
//        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.OAK_WOOD_NEST.get()));
//    }
//
//    @Override
//    public RecipeType<BeeSpawningBigRecipe> getRecipeType() {
//        return ProductiveBeesJeiPlugin.BEE_SPAWNING_BIG_TYPE;
//    }
//
//    @Nonnull
//    @Override
//    public Component getTitle() {
//        return Component.translatable("jei.productivebees.bee_spawning");
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
//    public void setRecipe(IRecipeLayoutBuilder builder, BeeSpawningBigRecipe recipe, IFocusGroup focuses) {
//        builder.addSlot(RecipeIngredientRole.INPUT, 5, 27)
//                .addItemStacks(Arrays.asList(recipe.ingredient.getItems()))
//                .setSlotName("nestBlock");
//
//        IntStream.range(0, recipe.output.size()).forEach((i) -> {
//            List<Integer> pos = BEE_POSITIONS.get(i);
//            builder.addSlot(RecipeIngredientRole.INPUT, pos.get(0), pos.get(1))
//                    .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.output.get(i).get())
//                    .setSlotName("spawn" + i);
//        });
//    }
//}
