package cy.jdkdigital.productivebees.integrations.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import cy.jdkdigital.productivebees.common.item.WoodChip;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;

@JeiPlugin
public class ProductiveBeesJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = new ResourceLocation(ProductiveBees.MODID, ProductiveBees.MODID);

    public static final RecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE_TYPE = RecipeType.create(ProductiveBees.MODID, "advanced_beehive", AdvancedBeehiveRecipe.class);
    public static final RecipeType<BeeBreedingRecipe> BEE_BREEDING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_breeding", BeeBreedingRecipe.class);
    public static final RecipeType<BeeConversionRecipe> BEE_CONVERSION_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_conversion", BeeConversionRecipe.class);
    public static final RecipeType<BeeSpawningRecipe> BEE_SPAWNING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_spawning", BeeSpawningRecipe.class);
    public static final RecipeType<BeeSpawningBigRecipe> BEE_SPAWNING_BIG_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_spawning_big", BeeSpawningBigRecipe.class);
    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_TYPE = RecipeType.create(ProductiveBees.MODID, "centrifuge", CentrifugeRecipe.class);
    public static final RecipeType<BeeFloweringRecipeCategory.Recipe> BEE_FLOWERING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_flowering", BeeFloweringRecipeCategory.Recipe.class);
    public static final RecipeType<IncubationRecipe> INCUBATION_TYPE = RecipeType.create(ProductiveBees.MODID, "incubation", IncubationRecipe.class);
    public static final RecipeType<BlockConversionRecipe> BLOCK_CONVERSION_TYPE = RecipeType.create(ProductiveBees.MODID, "block_conversion", BlockConversionRecipe.class);
    public static final RecipeType<BottlerRecipe> BOTTLER_TYPE = RecipeType.create(ProductiveBees.MODID, "bottler", BottlerRecipe.class);

    public static final IIngredientType<BeeIngredient> BEE_INGREDIENT = () -> BeeIngredient.class;

    public ProductiveBeesJeiPlugin() {
        BeeIngredientFactory.getOrCreateList();
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return pluginId;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ADVANCED_OAK_BEEHIVE.get()), ADVANCED_BEEHIVE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.POWERED_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COARSE_DIRT_NEST.get()), BEE_SPAWNING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OAK_WOOD_NEST.get()), BEE_SPAWNING_BIG_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INCUBATOR.get()), INCUBATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BOTTLER.get()), BOTTLER_TYPE);
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
        registration.addRecipeCategories(new BeeFloweringRecipeCategory(guiHelper));
        registration.addRecipeCategories(new IncubationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BlockConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BottlerRecipeCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<BeeIngredient> ingredients = BeeIngredientFactory.getOrCreateList(true).values();
        registration.register(BEE_INGREDIENT, new ArrayList<>(ingredients), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModItems.WOOD_CHIP.get());
        registration.useNbtForSubtypes(ModItems.STONE_CHIP.get());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_HONEYCOMB.get());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_SPAWN_EGG.get());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_COMB_BLOCK.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Beehive bee produce recipes
        Map<ResourceLocation, AdvancedBeehiveRecipe> advancedBeehiveRecipesMap = recipeManager.byType(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, advancedBeehiveRecipesMap.values().stream().toList());
        // Centrifuge recipes
        Map<ResourceLocation, CentrifugeRecipe> centrifugeRecipesMap = recipeManager.byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        registration.addRecipes(CENTRIFUGE_TYPE, centrifugeRecipesMap.values().stream().toList());
        // Spawning recipes
        Map<ResourceLocation, BeeSpawningRecipe> beeSpawningRecipesMap = recipeManager.byType(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
        registration.addRecipes(BEE_SPAWNING_TYPE, beeSpawningRecipesMap.values().stream().toList());
        Map<ResourceLocation, BeeSpawningBigRecipe> beeSpawningRecipesBigMap = recipeManager.byType(ModRecipeTypes.BEE_SPAWNING_BIG_TYPE.get());
        registration.addRecipes(BEE_SPAWNING_BIG_TYPE, beeSpawningRecipesBigMap.values().stream().toList());
        // Breeding recipes
        Map<ResourceLocation, BeeBreedingRecipe> beeBreedingRecipeMap = recipeManager.byType(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        registration.addRecipes(BEE_BREEDING_TYPE,beeBreedingRecipeMap.values().stream().toList());
        // Bee conversion recipes
        Map<ResourceLocation, BeeConversionRecipe> beeConversionRecipeMap = recipeManager.byType(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
        registration.addRecipes(BEE_CONVERSION_TYPE, beeConversionRecipeMap.values().stream().toList());
        // Block conversion recipes
        Map<ResourceLocation, BlockConversionRecipe> blockConversionRecipeMap = recipeManager.byType(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
        registration.addRecipes(BLOCK_CONVERSION_TYPE, blockConversionRecipeMap.values().stream().toList());
        // Bottler recipes
        Map<ResourceLocation, BottlerRecipe> bottlerRecipeMap = recipeManager.byType(ModRecipeTypes.BOTTLER_TYPE.get());
        registration.addRecipes(BOTTLER_TYPE, bottlerRecipeMap.values().stream().toList());

        // Bee ingredient descriptions
        List<String> notInfoBees = Arrays.asList("minecraft:bee", "configurable_bee");
        Map<String, BeeIngredient> beeList = BeeIngredientFactory.getOrCreateList();
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            String beeId = entry.getKey().replace("productivebees:", "");
            if (!notInfoBees.contains(beeId)) {
                Component description = Component.literal("");
                if (entry.getValue().isConfigurable()) {
                    CompoundTag nbt = BeeReloadListener.INSTANCE.getData(entry.getKey());
                    if (nbt.contains("description")) {
                        description = Component.translatable(nbt.getString("description"));
                    }
                    if (!nbt.getBoolean("selfbreed")) {
                        description = Component.translatable("productivebees.ingredient.description.selfbreed", description);
                    }
                } else {
                    description = Component.translatable("productivebees.ingredient.description." + (beeId));
                    if (beeId.equals("lumber_bee") || beeId.equals("quarry_bee") || beeId.equals("rancher_bee") || beeId.equals("collector_bee") || beeId.equals("hoarder_bee") || beeId.equals("farmer_bee") || beeId.equals("cupid_bee")) {
                        description = Component.translatable("productivebees.ingredient.description.selfbreed", description);
                    }
                }

                if (!description.getString().isEmpty()) {
                    registration.addIngredientInfo(entry.getValue(), BEE_INGREDIENT, description);
                }
            }

        }
        // Bee flowering requirements
        registration.addRecipes(BEE_FLOWERING_TYPE, BeeFloweringRecipeCategory.getFlowersRecipes(beeList));

        // Incubation recipes
        registration.addRecipes(INCUBATION_TYPE, IncubationRecipeCategory.getRecipes(beeList));

        // Bee nest descriptions
        List<String> itemInfos = Arrays.asList(
                "inactive_dragon_egg",
                "dragon_egg_hive",
                "bumble_bee_nest",
                "sugar_cane_nest",
                "slimy_nest",
                "stone_nest",
                "sand_nest",
                "snow_nest",
                "gravel_nest",
                "coarse_dirt_nest",
                "oak_wood_nest",
                "spruce_wood_nest",
                "acacia_wood_nest",
                "dark_oak_wood_nest",
                "jungle_wood_nest",
                "birch_wood_nest",
                "end_stone_nest",
                "obsidian_nest",
                "glowstone_nest",
                "soul_sand_nest",
                "nether_brick_nest",
                "nether_quartz_nest"
        );
        for (String itemName : itemInfos) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, itemName));
            registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, Component.translatable("productivebees.ingredient.description." + itemName));
        }

        // Chip recipes
        Collection<CraftingRecipe> chipRecipes = new ArrayList<>();
        Collection<AdvancedBeehiveRecipe> chipHiveRecipes = new ArrayList<>();
        Registry.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
            Block b = blockHolder.value();
            chipRecipes.add(StoneChip.getRecipe(b));

            String id = ForgeRegistries.BLOCKS.getKey(b).getPath();
            Map<Ingredient, IntArrayTag> blockItemOutput = new HashMap<>();
            blockItemOutput.put(Ingredient.of(b.asItem()), new IntArrayTag(new int[]{1, 7, 100}));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "stone_chip_block_hive_" + id), Lazy.of(() -> beeList.get("productivebees:quarry_bee")), blockItemOutput));
            Map<Ingredient, IntArrayTag> chipItemOutput = new HashMap<>();
            chipItemOutput.put(Ingredient.of(StoneChip.getStack(b)), new IntArrayTag(new int[]{1, 7, 100}));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "stone_chip_hive_" + id), Lazy.of(() -> beeList.get("productivebees:quarry_bee")), chipItemOutput));
        });
        Registry.BLOCK.getTagOrEmpty(ModTags.LUMBER).forEach(blockHolder -> {
            Block b = blockHolder.value();
            chipRecipes.add(WoodChip.getRecipe(b));

            String id = ForgeRegistries.BLOCKS.getKey(b).getPath();
            Map<Ingredient, IntArrayTag> blockItemOutput = new HashMap<>();
            blockItemOutput.put(Ingredient.of(b.asItem()), new IntArrayTag(new int[]{1, 7, 100}));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "wood_chip_block_hive_" + id), Lazy.of(() -> beeList.get("productivebees:lumber_bee")), blockItemOutput));
            Map<Ingredient, IntArrayTag> chipItemOutput = new HashMap<>();
            chipItemOutput.put(Ingredient.of(WoodChip.getStack(b)), new IntArrayTag(new int[]{1, 7, 100}));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "wood_chip_hive_" + id), Lazy.of(() -> beeList.get("productivebees:lumber_bee")), chipItemOutput));
        });
        registration.addRecipes(RecipeTypes.CRAFTING, chipRecipes.stream().toList());
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, chipHiveRecipes.stream().toList());

        // Configurable combs
        Optional<? extends Recipe<?>> honeycombRecipe = recipeManager.byKey(new ResourceLocation(ProductiveBees.MODID, "comb_block/configurable_honeycomb"));
        int count = 4;
        if (honeycombRecipe.isPresent()) {
            count = ((ConfigurableHoneycombRecipe) honeycombRecipe.get()).count;
        }
        List<CraftingRecipe> recipes = new ArrayList<>();
        for (Map.Entry<String, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            String beeType = entry.getKey();
            ResourceLocation idComb = new ResourceLocation(beeType + "_honeycomb");
            ResourceLocation idCombBlock = new ResourceLocation(beeType + "_comb");

            // Add comb item
            ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
            BeeCreator.setTag(beeType, comb);
            NonNullList<Ingredient> combInput = NonNullList.create();
            for (int i = 0; i < count; i++) {
                combInput.add(Ingredient.of(comb));
            }

            // Add comb block
            ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
            BeeCreator.setTag(beeType, combBlock);
            NonNullList<Ingredient> combBlockInput = NonNullList.create();
            combBlockInput.add(Ingredient.of(combBlock));

            recipes.add(new ShapelessRecipe(idComb, "", combBlock, combInput));
            ItemStack combOutput = comb.copy();
            combOutput.setCount(count);
            recipes.add(new ShapelessRecipe(idCombBlock, "", combOutput, combBlockInput));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        // Hide RBees
        Collection<BeeIngredient> ingredients = BeeIngredientFactory.getRBeesIngredients().values();
        if (ingredients.size() > 0) {
            jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(BEE_INGREDIENT, new ArrayList<>(ingredients));
        }
    }
}
