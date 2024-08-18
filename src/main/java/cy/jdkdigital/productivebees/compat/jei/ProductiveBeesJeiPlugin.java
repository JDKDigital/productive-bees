package cy.jdkdigital.productivebees.compat.jei;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.helper.RecipeHelper;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.container.gui.BreedingChamberScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.container.gui.IncubatorScreen;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
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
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

@JeiPlugin
public class ProductiveBeesJeiPlugin implements IModPlugin
{
    private static final ResourceLocation pluginId = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, ProductiveBees.MODID);

    public static final RecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE_TYPE = RecipeType.create(ProductiveBees.MODID, "advanced_beehive", AdvancedBeehiveRecipe.class);
    public static final RecipeType<BeeBreedingRecipe> BEE_BREEDING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_breeding", BeeBreedingRecipe.class);
    public static final RecipeType<BeeConversionRecipe> BEE_CONVERSION_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_conversion", BeeConversionRecipe.class);
    public static final RecipeType<BeeFishingRecipe> BEE_FISHING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_fishing", BeeFishingRecipe.class);
    public static final RecipeType<BeeSpawningRecipe> BEE_SPAWNING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_spawning", BeeSpawningRecipe.class);
    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE_TYPE = RecipeType.create(ProductiveBees.MODID, "centrifuge", CentrifugeRecipe.class);
    public static final RecipeType<CentrifugeRecipe> BLOCK_CENTRIFUGE_TYPE = RecipeType.create(ProductiveBees.MODID, "block_centrifuge", CentrifugeRecipe.class);
    public static final RecipeType<BeeFloweringRecipe> BEE_FLOWERING_TYPE = RecipeType.create(ProductiveBees.MODID, "bee_flowering", BeeFloweringRecipe.class);
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
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_HONEYCOMB.get(), (itemStack, uidContext) -> {
            if (!itemStack.has(ModDataComponents.BEE_TYPE)) {
                return IIngredientSubtypeInterpreter.NONE;
            }
            return itemStack.get(ModDataComponents.BEE_TYPE).toString();
        });
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_SPAWN_EGG.get(), (itemStack, uidContext) -> {
            if (!itemStack.has(DataComponents.ENTITY_DATA)) {
                return IIngredientSubtypeInterpreter.NONE;
            }
            return itemStack.get(DataComponents.ENTITY_DATA).getUnsafe().get("type").toString();
        });
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_COMB_BLOCK.get(), (itemStack, uidContext) -> {
            if (!itemStack.has(ModDataComponents.BEE_TYPE)) {
                return IIngredientSubtypeInterpreter.NONE;
            }
            return itemStack.get(ModDataComponents.BEE_TYPE).toString();
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Beehive bee produce recipes
        List<RecipeHolder<AdvancedBeehiveRecipe>> advancedBeehiveRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, advancedBeehiveRecipesMap.stream().map(RecipeHolder::value).toList());
        // Centrifuge recipes
        List<RecipeHolder<CentrifugeRecipe>> centrifugeRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        registration.addRecipes(CENTRIFUGE_TYPE, centrifugeRecipesMap.stream().map(RecipeHolder::value).toList());

        List<CentrifugeRecipe> blockCentrifugeRecipesMap = centrifugeRecipesMap.stream().map(recipe -> {
            var item = recipe.value().ingredient.getItems()[0];
            if (item.getItem() instanceof HoneycombItem) {
                List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
                recipe.value().itemOutput.forEach((chanceOutput) -> {
                    outputs.add(new TagOutputRecipe.ChancedOutput(chanceOutput.ingredient(), chanceOutput.min() * 4, chanceOutput.max() * 4, chanceOutput.chance()));
                });
                var fluid = new SizedFluidIngredient(recipe.value().fluidOutput.ingredient(), recipe.value().fluidOutput.amount() * 4);
                return new CentrifugeRecipe(Ingredient.of(BeeHelper.getCombBlockFromHoneyComb(item)), outputs, fluid, recipe.value().getProcessingTime());
            }
            return null;
        }).filter(Objects::nonNull).toList();
        registration.addRecipes(BLOCK_CENTRIFUGE_TYPE, blockCentrifugeRecipesMap);

        // Fishing recipes
        List<RecipeHolder<BeeFishingRecipe>> fishingRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_FISHING_TYPE.get());
        registration.addRecipes(BEE_FISHING_TYPE, fishingRecipesMap.stream().map(RecipeHolder::value).toList());
        // Spawning recipes
        List<RecipeHolder<BeeSpawningRecipe>> beeSpawningRecipesMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
        registration.addRecipes(BEE_SPAWNING_TYPE, beeSpawningRecipesMap.stream().map(RecipeHolder::value).toList());
        // Breeding recipes
        List<RecipeHolder<BeeBreedingRecipe>> beeBreedingRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        registration.addRecipes(BEE_BREEDING_TYPE,beeBreedingRecipeMap.stream().map(RecipeHolder::value).toList());
        // Bee conversion recipes
        List<RecipeHolder<BeeConversionRecipe>> beeConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
        registration.addRecipes(BEE_CONVERSION_TYPE, beeConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Block conversion recipes
        List<RecipeHolder<BlockConversionRecipe>> blockConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
        registration.addRecipes(BLOCK_CONVERSION_TYPE, blockConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Item conversion recipes
        List<RecipeHolder<ItemConversionRecipe>> itemConversionRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.ITEM_CONVERSION_TYPE.get());
        registration.addRecipes(ITEM_CONVERSION_TYPE, itemConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Bottler recipes
        List<RecipeHolder<BottlerRecipe>> bottlerRecipeMap = recipeManager.getAllRecipesFor(ModRecipeTypes.BOTTLER_TYPE.get());
        registration.addRecipes(BOTTLER_TYPE, bottlerRecipeMap.stream().map(RecipeHolder::value).toList());

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
        registration.addRecipes(BEE_FLOWERING_TYPE, RecipeHelper.getFlowersRecipes(beeList));

        // Incubation recipes
        registration.addRecipes(INCUBATION_TYPE, RecipeHelper.getRecipes(beeList).stream().map(RecipeHolder::value).toList());

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
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, itemName));
            registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, Component.translatable("productivebees.ingredient.description." + itemName));
        }

        // Quarry and lumber bee recipes
        Collection<AdvancedBeehiveRecipe> chipHiveRecipes = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
                blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
                chipHiveRecipes.add(new AdvancedBeehiveRecipe(Lazy.of(() -> beeList.get("productivebees:quarry_bee")), blockItemOutput));
            }
        });
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.LUMBER).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (!b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) {
                List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
                blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
                chipHiveRecipes.add(new AdvancedBeehiveRecipe(Lazy.of(() -> beeList.get("productivebees:lumber_bee")), blockItemOutput));
            }
        });
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, chipHiveRecipes.stream().toList());

        // Configurable combs
        Optional<RecipeHolder<?>> honeycombRecipe = recipeManager.byKey(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "comb_block/configurable_honeycomb"));
        int count = 4;
        if (honeycombRecipe.isPresent()) {
            count = ((ConfigurableHoneycombRecipe) honeycombRecipe.get().value()).count;
        }
        List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
        for (Map.Entry<ResourceLocation, CompoundTag> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
            ResourceLocation beeType = entry.getKey();
            ResourceLocation idComb = beeType.withPath(p -> p + "_honeycomb");
            ResourceLocation idCombBlock = beeType.withPath(p -> p + "_comb");

            // Add comb item
            ItemStack comb = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
            BeeCreator.setType(beeType, comb);
            NonNullList<Ingredient> combInput = NonNullList.create();
            for (int i = 0; i < count; i++) {
                combInput.add(Ingredient.of(comb));
            }

            // Add comb block
            ItemStack combBlock = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
            BeeCreator.setType(beeType, combBlock);
            NonNullList<Ingredient> combBlockInput = NonNullList.create();
            combBlockInput.add(Ingredient.of(combBlock));

            recipes.add(new RecipeHolder<>(idComb, new ShapelessRecipe("", CraftingBookCategory.BUILDING, combBlock, combInput)));
            ItemStack combOutput = comb.copy();
            combOutput.setCount(count);
            recipes.add(new RecipeHolder<>(idCombBlock, new ShapelessRecipe("", CraftingBookCategory.MISC, combOutput, combBlockInput)));
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
}
