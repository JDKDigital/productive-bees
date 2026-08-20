package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.BlockConversionRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;

public class BlockConversionRecipeCategory implements IRecipeCategory<BlockConversionRecipe>
{
    private static final int BACKGROUND_WIDTH = 90;
    private static final int BACKGROUND_HEIGHT = 52;
    private final IDrawable background;
    private final IDrawable icon;

    public BlockConversionRecipeCategory(IGuiHelper guiHelper) {
        Identifier location = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/block_conversion.png");
        this.background = guiHelper.createDrawable(location, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.COBBLESTONE));
    }

    @Override
    public RecipeType<BlockConversionRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BLOCK_CONVERSION_TYPE;
    }

    @Nonnull
    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.block_conversion");
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
    public void setRecipe(IRecipeLayoutBuilder builder, BlockConversionRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 38, 5)
                .addIngredients(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.getBees())
                .setSlotName("source");


        if (recipe.input.isPresent()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 26)
                    .addItemStacks(recipe.input.get().items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                    .setSlotName("sourceBlocks");
        } else if (recipe.stateFrom.getFluidState().getType().equals(Fluids.EMPTY)) {
            if (recipe.fromDisplay.isPresent() && !recipe.fromDisplay.get().isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 5, 25)
                        .addItemStacks(recipe.fromDisplay.get().items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                        .setSlotName("sourceBlock");
            } else {
                builder.addSlot(RecipeIngredientRole.INPUT, 5, 25)
                        .addItemStack(new ItemStack(recipe.stateFrom.getBlock().asItem()))
                        .setSlotName("sourceBlock");
            }
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 26)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(new FluidStack(recipe.stateFrom.getFluidState().getType(), 1000)))
                    .setSlotName("sourceFluid");
        }

        if (recipe.stateTo.getFluidState().getType().equals(Fluids.EMPTY)) {
            if (recipe.toDisplay.isPresent() && !recipe.toDisplay.get().isEmpty()) {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 25)
                        .addItemStacks(recipe.toDisplay.get().items().<ItemStack>map(h -> new ItemStack(h.value())).toList())
                        .setSlotName("resultBlock");
            } else {
                builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 25)
                        .addItemStack(new ItemStack(recipe.stateTo.getBlock().asItem()))
                        .setSlotName("resultBlock");
            }
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 26)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, Collections.singletonList(new FluidStack(recipe.stateTo.getFluidState().getType(), 1000)))
                    .setSlotName("resultFluid");
        }
    }

    @Override
    public void draw(BlockConversionRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.text(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.productivebees.block_conversion.chance", recipe.chance * 100)), 0, 45, 0xFF000000, false);
    }
}
