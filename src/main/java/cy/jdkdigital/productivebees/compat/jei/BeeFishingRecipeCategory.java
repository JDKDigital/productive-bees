package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BeeFishingRecipe;
import cy.jdkdigital.productivebees.init.ModTags;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BeeFishingRecipeCategory implements IRecipeCategory<BeeFishingRecipe>
{
    private static final int BACKGROUND_WIDTH = 126;
    private static final int BACKGROUND_HEIGHT = 110;
    private final IDrawable background;
    private final IDrawable icon;

    public BeeFishingRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_fishing_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.FISHING_ROD));
    }

    @Override
    public RecipeType<BeeFishingRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BEE_FISHING_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bee_fishing");
    }

    @Override
    public int getWidth() {
        return BACKGROUND_WIDTH;
    }

    @Override
    public int getHeight() {
        return BACKGROUND_HEIGHT;
    }

    @SuppressWarnings("unused")
    public IDrawable getBackground() {
        return this.background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(BeeFishingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        int xPos = 0;
        AtomicInteger yPos = new AtomicInteger(45);

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            var biomeRegistry = minecraft.level.registryAccess().lookupOrThrow(Registries.BIOME);
            for (Biome biome : BeeFishingRecipe.getBiomeList(recipe, minecraft.level)) {
                Identifier key = biomeRegistry.listElements().filter(h -> h.value() == biome).findFirst().<Identifier>map(h -> h.unwrapKey().orElseThrow().identifier()).orElse(null);
                if (key != null) {
                    guiGraphics.text(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("biome.minecraft." + key.getPath())), xPos, yPos.get(), 0xFF000000, false);
                    yPos.addAndGet(minecraft.font.lineHeight + 2);
                }
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeFishingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 17)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.output.get())
                .setSlotName("source");

        List<ItemStack> fishingRods = new ArrayList<>();
        BuiltInRegistries.ITEM.getTagOrEmpty(ModTags.Common.FISHING_RODS).forEach((Holder<Item> h) -> fishingRods.add(new ItemStack(h.value())));
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(VanillaTypes.ITEM_STACK, fishingRods);
    }
}
