package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;

public class BeeFloweringRecipeCategory implements IRecipeCategory<BeeFloweringRecipeCategory.Recipe>
{
    public static final ResourceLocation ID = new ResourceLocation(ProductiveBees.MODID, "bee_flowering");
    private final IDrawable icon;
    private final IDrawable background;

    public BeeFloweringRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(ProductiveBees.MODID, "textures/gui/jei/bee_flowering_recipe.png");
        this.background = guiHelper.createDrawable(location, 0, 0, 70, 82);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(Items.POPPY));
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return Recipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.productivebees.bee_flowering");
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
    public void setIngredients(Recipe recipe, IIngredients ingredients) {
        ingredients.setInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT, Collections.singletonList(recipe.getBee()));

        List<ItemStack> itemStacks = new ArrayList<>();
        List<FluidStack> fluidStacks = new ArrayList<>();
        try {
            List<Block> blockList = new ArrayList<>();
            if (recipe.blockTag != null) {
                blockList = recipe.blockTag.getValues();
            } else if (recipe.block != null) {
                blockList.add(recipe.block);
            } else if (recipe.fluid != null) {
                fluidStacks.add(new FluidStack(recipe.fluid, 1000));
            }

            for (Block block : blockList) {
                ItemStack item = new ItemStack(block.asItem());
                if (!item.getItem().equals(Items.AIR)) {
                    itemStacks.add(item);
                } else {
                    if (block instanceof CocoaBlock) {
                        itemStacks.add(new ItemStack(Items.COCOA_BEANS));
                    }
                }
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("Failed to find flowering requirements for " + recipe.getBee());
        }
        List<List<ItemStack>> items = new ArrayList<>();
        items.add(itemStacks);
        ingredients.setInputLists(VanillaTypes.ITEM, items);

        ingredients.setInputs(VanillaTypes.FLUID, fluidStacks);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);
        ingredientStacks.init(0, true, 29, 12);
        ingredientStacks.set(0, ingredients.getInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT).get(0));

        if (!ingredients.getInputs(VanillaTypes.ITEM).isEmpty()) {
            IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
            itemStacks.init(1, true, 26, 51);
            itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        }

        if (!ingredients.getInputs(VanillaTypes.FLUID).isEmpty()) {
            IGuiFluidStackGroup fluidStacks = iRecipeLayout.getFluidStacks();
            fluidStacks.init(1, true, 26, 51);
            fluidStacks.set(1, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        }
    }

    public static List<Recipe> getFlowersRecipes(Map<String, BeeIngredient> beeList) {
        List<Recipe> recipes = new ArrayList<>();

        // Hardcoded for now until bees are moved to config
        Map<String, Tag<Block>> flowering = new HashMap<>();
        flowering.put("productivebees:blue_banded_bee", ModTags.RIVER_FLOWERS);
        flowering.put("productivebees:green_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:nomad_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:chocolate_mining_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:ashy_mining_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:reed_bee", ModTags.SWAMP_FLOWERS);
        flowering.put("productivebees:resin_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:sweat_bee", ModTags.SNOW_FLOWERS);
        flowering.put("productivebees:yellow_black_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:lumber_bee", BlockTags.LOGS);
        flowering.put("productivebees:quarry_bee", ModTags.QUARRY);
        flowering.put("productivebees:creeper_bee", ModTags.POWDERY);

        Tag<Block> defaultBlockTag = BlockTags.FLOWERS;

        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (entry.getValue().isConfigurable()) {
                CompoundTag nbt = BeeReloadListener.INSTANCE.getData(entry.getValue().getBeeType().toString());
                if (nbt.contains("flowerTag")) {
                    Tag<Block> flowerTag = ModTags.getBlockTag(new ResourceLocation(nbt.getString("flowerTag")));
                    recipes.add(new Recipe(flowerTag, entry.getValue()));
                } else if (nbt.contains("flowerBlock")) {
                    Block flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flowerBlock")));
                    recipes.add(new Recipe(flowerBlock, entry.getValue()));
                } else if (nbt.contains("flowerFluid")) {
                    Fluid flowerFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("flowerFluid")));
                    recipes.add(new Recipe(flowerFluid, entry.getValue()));
                } else {
                    recipes.add(new Recipe(defaultBlockTag, entry.getValue()));
                }
            }
            else if (flowering.containsKey(entry.getValue().getBeeType().toString())) {
                Tag<Block> blockTag = flowering.get(entry.getValue().getBeeType().toString());
                recipes.add(new Recipe(blockTag, entry.getValue()));
            }
            else {
                recipes.add(new Recipe(defaultBlockTag, entry.getValue()));
            }
        }
        return recipes;
    }

    public static class Recipe
    {
        private final BeeIngredient bee;

        private final Tag<Block> blockTag;

        private final Block block;

        private final Fluid fluid;

        public Recipe(Tag<Block> blockTag, BeeIngredient bee) {
            this.blockTag = blockTag;
            this.block = null;
            this.fluid = null;
            this.bee = bee;
        }

        public Recipe(Block block, BeeIngredient bee) {
            this.blockTag = null;
            this.block = block;
            this.fluid = null;
            this.bee = bee;
        }

        public Recipe(Fluid fluid, BeeIngredient bee) {
            this.blockTag = null;
            this.block = null;
            this.fluid = fluid;
            this.bee = bee;
        }

        public BeeIngredient getBee() {
            return this.bee;
        }
    }
}
