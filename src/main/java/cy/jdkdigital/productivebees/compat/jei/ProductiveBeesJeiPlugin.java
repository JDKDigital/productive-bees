package cy.jdkdigital.productivebees.compat.jei;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.container.gui.BreedingChamberScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.container.gui.IncubatorScreen;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoneycombItem;
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
    public static final RecipeType<BeeFishingRecipe> BEE_FISHING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_fishing", BeeFishingRecipe.class);
    public static final RecipeType<BeeSpawningRecipe> BEE_SPAWNING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_spawning", BeeSpawningRecipe.class);
    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_TYPE = RecipeType.create(ProductiveBees.MODID, "centrifuge", CentrifugeRecipe.class);
    public static final RecipeType<CentrifugeRecipe> BLOCK_CENTRIFUGE_TYPE = RecipeType.create(ProductiveBees.MODID, "block_centrifuge", CentrifugeRecipe.class);
    public static final RecipeType<BeeFloweringRecipeCategory.Recipe> BEE_FLOWERING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_flowering", BeeFloweringRecipeCategory.Recipe.class);
    public static final RecipeType<IncubationRecipe> INCUBATION_TYPE = RecipeType.create(ProductiveBees.MODID, "incubation", IncubationRecipe.class);
    public static final RecipeType<BlockConversionRecipe> BLOCK_CONVERSION_TYPE = RecipeType.create(ProductiveBees.MODID, "block_conversion", BlockConversionRecipe.class);
    public static final RecipeType<ItemConversionRecipe> ITEM_CONVERSION_TYPE = RecipeType.create(ProductiveBees.MODID, "item_conversion", ItemConversionRecipe.class);
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
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HIVES.get("advanced_oak_beehive").get()), ADVANCED_BEEHIVE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.POWERED_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HEATED_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HEATED_CENTRIFUGE.get()), BLOCK_CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COARSE_DIRT_NEST.get()), BEE_SPAWNING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INCUBATOR.get()), INCUBATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BOTTLER.get()), BOTTLER_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BREEDING_CHAMBER.get()), BEE_BREEDING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FEEDER.get()), ITEM_CONVERSION_TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        registration.addRecipeCategories(new AdvancedBeehiveRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeBreedingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CentrifugeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new HeatedCentrifugeRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeFishingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeSpawningRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BeeFloweringRecipeCategory(guiHelper));
        registration.addRecipeCategories(new IncubationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BlockConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ItemConversionRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BottlerRecipeCategory(guiHelper));
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registration) {
        Collection<BeeIngredient> ingredients = BeeIngredientFactory.getOrCreateList(true).values();
        registration.register(BEE_INGREDIENT, new ArrayList<>(ingredients), new BeeIngredientHelper(), new BeeIngredientRenderer());
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(ModBlocks.AMBER.get().asItem());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_HONEYCOMB.get());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_SPAWN_EGG.get());
        registration.useNbtForSubtypes(ModItems.CONFIGURABLE_COMB_BLOCK.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Beehive bee produce recipes
        List<AdvancedBeehiveRecipe> advancedBeehiveRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, advancedBeehiveRecipesMap);
        // Centrifuge recipes
        List<CentrifugeRecipe> centrifugeRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        registration.addRecipes(CENTRIFUGE_TYPE, centrifugeRecipesMap);

        List<CentrifugeRecipe> blockCentrifugeRecipesMap = centrifugeRecipesMap.stream().map(recipe -> {
            var item = recipe.ingredient.getItems()[0];
            if (item.getItem() instanceof HoneycombItem) {
                Map<Ingredient, IntArrayTag> outputs = new HashMap<>();
                recipe.itemOutput.forEach((ingredient, intTags) -> {
                    outputs.put(ingredient, new IntArrayTag(new int[]{intTags.get(0).getAsInt() * 4, intTags.get(1).getAsInt() * 4, intTags.get(2).getAsInt()}));
                });
                return new CentrifugeRecipe(recipe.id.withPath(p -> p + "_block"), Ingredient.of(BeeHelper.getCombBlockFromHoneyComb(item)), outputs, recipe.fluidOutput != null ? Pair.of(recipe.fluidOutput.getFirst(), recipe.fluidOutput.getSecond() * 4) : null, recipe.getProcessingTime());
            }
            return null;
        }).filter(Objects::nonNull).toList();
        registration.addRecipes(BLOCK_CENTRIFUGE_TYPE, blockCentrifugeRecipesMap);

                // Fishing recipes
        List<BeeFishingRecipe> fishingRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_FISHING_TYPE.get());
        registration.addRecipes(BEE_FISHING_TYPE, fishingRecipesMap);
        // Spawning recipes
        List<BeeSpawningRecipe> beeSpawningRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
        registration.addRecipes(BEE_SPAWNING_TYPE, beeSpawningRecipesMap);
        // Breeding recipes
        List<BeeBreedingRecipe> beeBreedingRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        registration.addRecipes(BEE_BREEDING_TYPE,beeBreedingRecipeMap);
        // Bee conversion recipes
        List<BeeConversionRecipe> beeConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
        registration.addRecipes(BEE_CONVERSION_TYPE, beeConversionRecipeMap);
        // Block conversion recipes
        List<BlockConversionRecipe> blockConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
        registration.addRecipes(BLOCK_CONVERSION_TYPE, blockConversionRecipeMap);
        // Item conversion recipes
        List<ItemConversionRecipe> itemConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.ITEM_CONVERSION_TYPE.get());
        registration.addRecipes(ITEM_CONVERSION_TYPE, itemConversionRecipeMap);
        // Bottler recipes
        List<BottlerRecipe> bottlerRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BOTTLER_TYPE.get());
        registration.addRecipes(BOTTLER_TYPE, bottlerRecipeMap);

        // Bee ingredient descriptions
        Map<String, BeeIngredient> beeList = BeeIngredientFactory.getOrCreateList();
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (entry.getKey().contains(ProductiveBees.MODID)) {
                String beeId = entry.getKey().replace("productivebees:", "");
                Component description;
                if (entry.getValue().isConfigurable()) {
                    CompoundTag nbt = BeeReloadListener.INSTANCE.getData(entry.getKey());
                    if (nbt.contains("description")) {
                        description = Component.translatable(nbt.getString("description"));
                    } else {
                        description = Component.translatable("productivebees.ingredient.description." + beeId + "_bee");
                        if (description.getString().equals("productivebees.ingredient.description." + beeId + "_bee")) {
                            description = Component.literal("");
                        }
                    }
                    if (!nbt.getBoolean("selfbreed")) {
                        description = Component.translatable("productivebees.ingredient.description.selfbreed", description.getString());
                    }
                } else {
                    description = Component.translatable("productivebees.ingredient.description." + beeId);
                    if (beeId.equals("lumber_bee") || beeId.equals("quarry_bee") || beeId.equals("rancher_bee") || beeId.equals("collector_bee") || beeId.equals("hoarder_bee") || beeId.equals("farmer_bee") || beeId.equals("cupid_bee")) {
                        description = Component.translatable("productivebees.ingredient.description.selfbreed", description.getString());
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

        // Quarry and lumber bee recipes
        Collection<AdvancedBeehiveRecipe> chipHiveRecipes = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                String id = ForgeRegistries.BLOCKS.getKey(b).getPath();
                Map<Ingredient, IntArrayTag> blockItemOutput = new HashMap<>();
                blockItemOutput.put(Ingredient.of(b.asItem()), new IntArrayTag(new int[]{1, 1, 100}));
                chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "quarry_bee_block_hive_" + id), Lazy.of(() -> beeList.get("productivebees:quarry_bee")), blockItemOutput));
            }
        });
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.LUMBER).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                String id = ForgeRegistries.BLOCKS.getKey(b).getPath();
                Map<Ingredient, IntArrayTag> blockItemOutput = new HashMap<>();
                blockItemOutput.put(Ingredient.of(b.asItem()), new IntArrayTag(new int[]{1, 1, 100}));
                chipHiveRecipes.add(new AdvancedBeehiveRecipe(new ResourceLocation(ProductiveBees.MODID, "lumber_bee_block_hive_" + id), Lazy.of(() -> beeList.get("productivebees:lumber_bee")), blockItemOutput));
            }
        });
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

            recipes.add(new ShapelessRecipe(idComb, "", CraftingBookCategory.BUILDING, combBlock, combInput));
            ItemStack combOutput = comb.copy();
            combOutput.setCount(count);
            recipes.add(new ShapelessRecipe(idCombBlock, "", CraftingBookCategory.MISC, combOutput, combBlockInput));
        }
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CentrifugeScreen.class, 35, 35, 24, 16, CENTRIFUGE_TYPE);
        registration.addRecipeClickArea(BottlerScreen.class, 142, 37, 14, 14, BOTTLER_TYPE);
        registration.addRecipeClickArea(BreedingChamberScreen.class, 72, 14, 45, 22, BEE_BREEDING_TYPE);
        registration.addRecipeClickArea(IncubatorScreen.class, 64, 35, 14, 16, INCUBATION_TYPE);
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
