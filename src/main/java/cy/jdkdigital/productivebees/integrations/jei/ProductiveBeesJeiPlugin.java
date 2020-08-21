package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.recipe.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class ProductiveBeesJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveBees.MODID, ProductiveBees.MODID);
    public static final ResourceLocation CATEGORY_ADVANCED_BEEHIVE_UID = new ResourceLocation(ProductiveBees.MODID, "advanced_beehive");
    public static final ResourceLocation CATEGORY_BEE_BREEDING_UID = new ResourceLocation(ProductiveBees.MODID, "bee_breeding");
    public static final ResourceLocation CATEGORY_BEE_CONVERSION_UID = new ResourceLocation(ProductiveBees.MODID, "bee_conversion");
    public static final ResourceLocation CATEGORY_BEE_SPAWNING_UID = new ResourceLocation(ProductiveBees.MODID, "bee_spawning");
    public static final ResourceLocation CATEGORY_BEE_SPAWNING_BIG_UID = new ResourceLocation(ProductiveBees.MODID, "bee_spawning_big");
    public static final ResourceLocation CATEGORY_CENTRIFUGE_UID = new ResourceLocation(ProductiveBees.MODID, "centrifuge");

    public static final IIngredientType<BeeIngredient> BEE_INGREDIENT = () -> BeeIngredient.class;

    public ProductiveBeesJeiPlugin() {
        BeeIngredientFactory.createList();
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_OAK_BEEHIVE.get()), CATEGORY_ADVANCED_BEEHIVE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.POWERED_CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COARSE_DIRT_NEST.get()), CATEGORY_BEE_SPAWNING_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OAK_WOOD_NEST.get()), CATEGORY_BEE_SPAWNING_BIG_UID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new AdvancedBeehiveRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeBreedingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeSpawningRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeSpawningRecipeBigCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(BEE_INGREDIENT, new ArrayList<>(BeeIngredientFactory.getOrCreateList().values()), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.WOOD_CHIP.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        // Beehive bee produce recipes
        Map<ResourceLocation, IRecipe<IInventory>> advancedBeehiveRecipesMap = recipeManager.getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE);
        registration.addRecipes(advancedBeehiveRecipesMap.values(), CATEGORY_ADVANCED_BEEHIVE_UID);
        // Centrifuge recipes
        Map<ResourceLocation, IRecipe<IInventory>> centrifugeRecipesMap = recipeManager.getRecipes(CentrifugeRecipe.CENTRIFUGE);
        registration.addRecipes(centrifugeRecipesMap.values(), CATEGORY_CENTRIFUGE_UID);
        // Spawning recipes
        Map<ResourceLocation, IRecipe<IInventory>> beeSpawningRecipesMap = recipeManager.getRecipes(BeeSpawningRecipe.BEE_SPAWNING);
        registration.addRecipes(beeSpawningRecipesMap.values(), CATEGORY_BEE_SPAWNING_UID);
        Map<ResourceLocation, IRecipe<IInventory>> beeSpawningRecipesBigMap = recipeManager.getRecipes(BeeSpawningBigRecipe.BEE_SPAWNING);
        registration.addRecipes(beeSpawningRecipesBigMap.values(), CATEGORY_BEE_SPAWNING_BIG_UID);
        // Breeding recipes
        Map<ResourceLocation, IRecipe<IInventory>> beeBreedingRecipeMap = recipeManager.getRecipes(BeeBreedingRecipe.BEE_BREEDING);
        registration.addRecipes(beeBreedingRecipeMap.values(), CATEGORY_BEE_BREEDING_UID);
        // Bee conversion recipes
        Map<ResourceLocation, IRecipe<IInventory>> beeConversionRecipeMap = recipeManager.getRecipes(BeeConversionRecipe.BEE_CONVERSION);
        registration.addRecipes(beeConversionRecipeMap.values(), CATEGORY_BEE_CONVERSION_UID);

        // Ingredient descriptions
        List<String> notInfoBees = Arrays.asList("minecraft:bee", "aluminium_bee", "brass_bee", "bronze_bee", "copper_bee", "invar_bee", "lead_bee", "nickel_bee", "osmium_bee", "platinum_bee", "radioactive_bee", "silver_bee", "steel_bee", "tin_bee", "titanium_bee", "tungsten_bee", "zinc_bee", "amber_bee");
        for (Map.Entry<String, BeeIngredient> entry : BeeIngredientFactory.getOrCreateList().entrySet()) {
            String beeId = entry.getKey().replace("productivebees:", "");
            if (!notInfoBees.contains(beeId)) {
                registration.addIngredientInfo(entry.getValue(), BEE_INGREDIENT, "productivebees.ingredient.description." + (beeId));
            }
        }

        List<String> itemInfos = Arrays.asList("inactive_dragon_egg", "sand_nest", "snow_nest", "gravel_nest", "coarse_dirt_nest", "oak_wood_nest", "spruce_wood_nest", "acacia_wood_nest", "dark_oak_wood_nest", "jungle_wood_nest", "birch_wood_nest", "end_stone_nest", "obsidian_nest", "glowstone_nest", "nether_brick_nest", "nether_quartz_nest");
        for (String itemName: itemInfos) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, itemName));
            registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM, "productivebees.ingredient.description." + itemName);
        }
    }
}
