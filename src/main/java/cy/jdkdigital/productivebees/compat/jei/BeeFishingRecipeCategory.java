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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class BeeFishingRecipeCategory implements IRecipeCategory<BeeFishingRecipe>
{
    private final IDrawable background;
    private final IDrawable icon;

    public BeeFishingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_fishing_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 126, 110);
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
    public void draw(BeeFishingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int xPos = 0;
        AtomicInteger yPos = new AtomicInteger(45);

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            var biomeRegistry = minecraft.level.registryAccess().registryOrThrow(Registries.BIOME);
            for (Biome biome : BeeFishingRecipe.getBiomeList(recipe, minecraft.level)) {
                var key = biomeRegistry.getKey(biome);
                if (key != null) {
                    guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("biome.minecraft." + key.getPath())), xPos, yPos.get(), 0xFF000000, false);
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

        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addIngredients(VanillaTypes.ITEM_STACK, Arrays.asList(Ingredient.of(ModTags.Common.FISHING_RODS).getItems()));
    }
}
