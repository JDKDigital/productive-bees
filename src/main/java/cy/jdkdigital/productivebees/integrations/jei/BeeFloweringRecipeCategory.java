package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
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
    public String getTitle() {
        return I18n.format("jei.productivebees.bee_flowering");
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

        List<ItemStack> stacks = new ArrayList<>();
        try {
            List<Block> blockList = new ArrayList<>();
            if (recipe.blockTag != null) {
                blockList = recipe.blockTag.getAllElements();
            } else if(recipe.block != null) {
                blockList.add(recipe.block);
            }
            for (Block block: blockList) {
                ItemStack item = new ItemStack(block.asItem());
                if (!item.getItem().equals(Items.AIR)) {
                    stacks.add(item);
                }
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("Failed to find flowering requirements for " + recipe.getBee());
        }
        List<List<ItemStack>> items = new ArrayList<>();
        items.add(stacks);
        ingredients.setInputLists(VanillaTypes.ITEM, items);
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, @Nonnull Recipe recipe, IIngredients ingredients) {
        IGuiIngredientGroup<BeeIngredient> ingredientStacks = iRecipeLayout.getIngredientsGroup(ProductiveBeesJeiPlugin.BEE_INGREDIENT);
        ingredientStacks.init(0, true, 29, 12);
        ingredientStacks.set(0, ingredients.getInputs(ProductiveBeesJeiPlugin.BEE_INGREDIENT).get(0));

        IGuiItemStackGroup itemStacks = iRecipeLayout.getItemStacks();
        itemStacks.init(1, true, 26, 51);
        itemStacks.set(1, ingredients.getInputs(VanillaTypes.ITEM).get(0));
    }

    public static List<Recipe> getFlowersRecipes(Map<String, BeeIngredient> beeList) {
        List<Recipe> recipes = new ArrayList<>();

        // Hardcoded for now until bees are moved to config
        Map<String, ITag<Block>> flowering = new HashMap<>();
        flowering.put("productivebees:blue_banded_bee", ModTags.RIVER_FLOWERS);
        flowering.put("productivebees:green_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:nomad_bee", ModTags.ARID_FLOWERS);
        flowering.put("productivebees:reed_bee", ModTags.SWAMP_FLOWERS);
        flowering.put("productivebees:resin_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:sweaty_bee", ModTags.SNOW_FLOWERS);
        flowering.put("productivebees:yellow_black_carpenter_bee", ModTags.FOREST_FLOWERS);
        flowering.put("productivebees:lumber_bee", BlockTags.LOGS);
        flowering.put("productivebees:quarry_bee", ModTags.QUARRY);

        ITag<Block> defaultBlockTag = BlockTags.FLOWERS;

        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()){
            if (entry.getValue().isConfigurable()) {
                CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(entry.getValue().getBeeType().toString());
                if (nbt.contains("flowerTag")) {
                    ITag<Block> flowerTag = ModTags.getTag(new ResourceLocation(nbt.getString("flowerTag")));
                    recipes.add(new Recipe(flowerTag, entry.getValue()));
                } else if (nbt.contains("flowerBlock")) {
                    Block flowerBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("flowerBlock")));
                    recipes.add(new Recipe(flowerBlock, entry.getValue()));
                }
            } else if (flowering.containsKey(entry.getValue().getBeeType().toString())) {
                ITag<Block> blockTag = flowering.get(entry.getValue().getBeeType().toString());
                recipes.add(new Recipe(blockTag, entry.getValue()));
            }
            recipes.add(new Recipe(defaultBlockTag, entry.getValue()));

        }
        return recipes;
    }

    public static class Recipe {

        private final BeeIngredient bee;

        private final ITag<Block> blockTag;

        private final Block block;

        public Recipe(ITag<Block> blockTag, BeeIngredient bee) {
            this.blockTag = blockTag;
            this.block = null;
            this.bee = bee;
        }

        public Recipe(Block block, BeeIngredient bee) {
            this.blockTag = null;
            this.block = block;
            this.bee = bee;
        }

        public BeeIngredient getBee() {
            return this.bee;
        }
    }
}
