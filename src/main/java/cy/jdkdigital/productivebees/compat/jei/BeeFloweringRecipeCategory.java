package cy.jdkdigital.productivebees.compat.jei;

import com.google.common.collect.Streams;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.AmberItem;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.recipe.BeeFloweringRecipe;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeeFloweringRecipeCategory implements IRecipeCategory<BeeFloweringRecipe>
{
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_flowering");
    private final IDrawable icon;
    private final IDrawable background;

    public BeeFloweringRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "textures/gui/jei/bee_flowering_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 70, 82);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.POPPY));
    }

    @Override
    public RecipeType<BeeFloweringRecipe> getRecipeType() {
        return ProductiveBeesJeiPlugin.BEE_FLOWERING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.productivebees.bee_flowering");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, BeeFloweringRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 12)
                .addIngredient(ProductiveBeesJeiPlugin.BEE_INGREDIENT, recipe.bee())
                .setSlotName("source");

        List<ItemStack> itemStacks = new ArrayList<>();
        List<FluidStack> fluidStacks = new ArrayList<>();
        try {
            List<Block> blockList = new ArrayList<>();
            if (recipe.blockTag() != null) {
                blockList = Streams.stream(BuiltInRegistries.BLOCK.getTagOrEmpty(recipe.blockTag())).map(Holder::value).toList();
                if (blockList.isEmpty() && recipe.itemTag() != null) {
                    itemStacks.addAll(Streams.stream(BuiltInRegistries.ITEM.getTagOrEmpty(recipe.itemTag())).map(itemHolder -> new ItemStack(itemHolder.value())).toList());
                }
            } else if (recipe.fluidTag() != null) {
                fluidStacks = Streams.stream(BuiltInRegistries.FLUID.getTagOrEmpty(recipe.fluidTag())).map(fluidHolder -> new FluidStack(fluidHolder.value(), 1000)).toList();
            } else if (recipe.block() != null) {
                blockList.add(recipe.block());
            } else if (recipe.fluid() != null) {
                fluidStacks.add(new FluidStack(recipe.fluid(), 1000));
            } else if (recipe.item() != null) {
                itemStacks.add(recipe.item());
            }

            for (Block block : blockList) {
                ItemStack item = new ItemStack(block.asItem());
                if (!item.getItem().equals(Items.AIR) && !itemStacks.contains(item)) {
                    itemStacks.add(item);
                } else if (block instanceof CocoaBlock) {
                    itemStacks.add(new ItemStack(Items.COCOA_BEANS));
                }
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("Failed to find flowering requirements for " + recipe);
        }

        if (!fluidStacks.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 26, 51)
                    .addIngredients(NeoForgeTypes.FLUID_STACK, fluidStacks)
                    .setSlotName("inputFluid");
        }
        if (!itemStacks.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 26, 51)
                    .addItemStacks(itemStacks)
                    .setSlotName("inputItem");
        }
    }
}
