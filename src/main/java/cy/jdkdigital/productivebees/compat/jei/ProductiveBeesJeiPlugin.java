package cy.jdkdigital.productivebees.compat.jei;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.helper.RecipeHelper;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.recipe.*;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientHelper;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientRenderer;
import cy.jdkdigital.productivebees.container.gui.BottlerScreen;
import cy.jdkdigital.productivebees.container.gui.BreedingChamberScreen;
import cy.jdkdigital.productivebees.container.gui.CentrifugeScreen;
import cy.jdkdigital.productivebees.container.gui.IncubatorScreen;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import cy.jdkdigital.productivelib.compat.jei.RecipeMapCache;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;

@JeiPlugin
public class ProductiveBeesJeiPlugin implements IModPlugin
{
    private static final Identifier pluginId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, ProductiveBees.MODID);

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
    public Identifier getPluginUid() {
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
        Codec<BeeIngredient> ingredientCodec = BeeIngredient.CODEC.xmap(Supplier::get, ingredient -> () -> ingredient);
        registration.register(BEE_INGREDIENT, new ArrayList<>(ingredients), new BeeIngredientHelper(), new BeeIngredientRenderer(), ingredientCodec);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_SPAWN_EGG.get(), (ItemStack ingredient, UidContext context) -> ingredient.get(DataComponents.ENTITY_DATA));
        registration.registerSubtypeInterpreter(ModItems.GENE.get(), (ItemStack ingredient, UidContext context) -> ingredient.get(ModDataComponents.GENE_GROUP));
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_HONEYCOMB.get(), (ItemStack ingredient, UidContext context) -> ingredient.get(ModDataComponents.BEE_TYPE));
        registration.registerSubtypeInterpreter(ModItems.CONFIGURABLE_COMB_BLOCK.get(), (ItemStack ingredient, UidContext context) -> ingredient.get(ModDataComponents.BEE_TYPE));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 26.1: client only sees recipes opted in via OnDatapackSyncEvent.sendRecipes (see EventHandler#onDataSync).
        // The lib-side RecipeMapCache subscribes to RecipesReceivedEvent and caches the synced map.
        var recipeMap = RecipeMapCache.getRecipeMap();

        // Beehive bee produce recipes
        Collection<RecipeHolder<AdvancedBeehiveRecipe>> advancedBeehiveRecipesMap = recipeMap.byType(ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get());
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, advancedBeehiveRecipesMap.stream().map(RecipeHolder::value).toList());
        // Centrifuge recipes
        Collection<RecipeHolder<CentrifugeRecipe>> centrifugeRecipesMap = recipeMap.byType(ModRecipeTypes.CENTRIFUGE_TYPE.get());
        registration.addRecipes(CENTRIFUGE_TYPE, centrifugeRecipesMap.stream().map(RecipeHolder::value).toList());

        // Map out all combs that have a direct block centrifuge recipe
        List<Identifier> beesWithCombRecipes = centrifugeRecipesMap.stream().filter(recipe -> {
            ItemStack first = CentrifugeRecipeCategory.stacksWithComponents(recipe.value().ingredient).stream().findFirst().orElse(ItemStack.EMPTY);
            return first.is(ModTags.Common.STORAGE_BLOCK_HONEYCOMBS);
        }).map(r -> {
            ItemStack first = CentrifugeRecipeCategory.stacksWithComponents(r.value().ingredient).stream().findFirst().orElse(ItemStack.EMPTY);
            return first.get(ModDataComponents.BEE_TYPE);
        }).toList();
        List<CentrifugeRecipe> blockCentrifugeRecipesMap = centrifugeRecipesMap.stream().map(recipe -> {
            ItemStack item = CentrifugeRecipeCategory.stacksWithComponents(recipe.value().ingredient).stream().findFirst().orElse(ItemStack.EMPTY);
            if (!beesWithCombRecipes.contains(item.get(ModDataComponents.BEE_TYPE))) {
                if (item.getItem() instanceof HoneycombItem) {
                    List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
                    recipe.value().itemOutput.forEach((chanceOutput) -> {
                        outputs.add(new TagOutputRecipe.ChancedOutput(chanceOutput.ingredient(), chanceOutput.min() * 4, chanceOutput.max() * 4, chanceOutput.chance()));
                    });
                    var fluid = new SizedFluidIngredient(recipe.value().fluidOutput.ingredient(), recipe.value().fluidOutput.amount() * 4);
                    return new CentrifugeRecipe(ComponentIngredient.of(BeeHelper.getCombBlockFromHoneyComb(item)), outputs, fluid, recipe.value().getProcessingTime());
                }
            }
            return null;
        }).filter(Objects::nonNull).toList();
        registration.addRecipes(BLOCK_CENTRIFUGE_TYPE, blockCentrifugeRecipesMap);

        // Fishing recipes
        Collection<RecipeHolder<BeeFishingRecipe>> fishingRecipesMap = recipeMap.byType(ModRecipeTypes.BEE_FISHING_TYPE.get());
        registration.addRecipes(BEE_FISHING_TYPE, fishingRecipesMap.stream().map(RecipeHolder::value).toList());
        // Spawning recipes
        Collection<RecipeHolder<BeeSpawningRecipe>> beeSpawningRecipesMap = recipeMap.byType(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
        registration.addRecipes(BEE_SPAWNING_TYPE, beeSpawningRecipesMap.stream().map(RecipeHolder::value).toList());
        // Breeding recipes
        Collection<RecipeHolder<BeeBreedingRecipe>> beeBreedingRecipeMap = recipeMap.byType(ModRecipeTypes.BEE_BREEDING_TYPE.get());
        registration.addRecipes(BEE_BREEDING_TYPE, beeBreedingRecipeMap.stream().map(RecipeHolder::value).toList());
        // Bee conversion recipes
        Collection<RecipeHolder<BeeConversionRecipe>> beeConversionRecipeMap = recipeMap.byType(ModRecipeTypes.BEE_CONVERSION_TYPE.get());
        registration.addRecipes(BEE_CONVERSION_TYPE, beeConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Block conversion recipes
        Collection<RecipeHolder<BlockConversionRecipe>> blockConversionRecipeMap = recipeMap.byType(ModRecipeTypes.BLOCK_CONVERSION_TYPE.get());
        registration.addRecipes(BLOCK_CONVERSION_TYPE, blockConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Item conversion recipes
        Collection<RecipeHolder<ItemConversionRecipe>> itemConversionRecipeMap = recipeMap.byType(ModRecipeTypes.ITEM_CONVERSION_TYPE.get());
        registration.addRecipes(ITEM_CONVERSION_TYPE, itemConversionRecipeMap.stream().map(RecipeHolder::value).toList());
        // Bottler recipes
        Collection<RecipeHolder<BottlerRecipe>> bottlerRecipeMap = recipeMap.byType(ModRecipeTypes.BOTTLER_TYPE.get());
        registration.addRecipes(BOTTLER_TYPE, bottlerRecipeMap.stream().map(RecipeHolder::value).toList());

        var minecraft = Minecraft.getInstance();
        Map<String, BeeIngredient> beeList = BeeIngredientFactory.getOrCreateList();
        // Self breeding bees
        List<RecipeHolder<BeeBreedingRecipe>> beeSelfBreedingRecipeMap = new ArrayList<>();
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getBeeType().toString())) continue;
            if (entry.getValue().getCachedEntity(minecraft.level) instanceof ConfigurableBee configurableBee && configurableBee.canSelfBreed()) {
                beeSelfBreedingRecipeMap.add(new RecipeHolder<>(
                        ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "self_breeding/" + Identifier.parse(entry.getKey()).getPath())),
                        new BeeBreedingRecipe(() -> entry.getValue(), () -> entry.getValue(), () -> entry.getValue(), 0f)
                ));
            }
        }
        registration.addRecipes(BEE_BREEDING_TYPE, beeSelfBreedingRecipeMap.stream().map(RecipeHolder::value).toList());

        // Bee ingredient descriptions
        for (Map.Entry<String, BeeIngredient> entry : beeList.entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getBeeType().toString())) continue;
            if (entry.getKey().contains(ProductiveBees.MODID)) {
                String beeId = entry.getKey().replace("productivebees:", "");
                Component description;
                if (entry.getValue().isConfigurable()) {
                    BeeData beeData = BeeRegistries.lookup(Identifier.parse(entry.getKey()));
                    if (beeData != null && beeData.description().isPresent()) {
                        description = Component.translatable(beeData.description().get());
                    } else {
                        description = Component.translatable("productivebees.ingredient.description." + beeId + "_bee");
                        if (description.getString().equals("productivebees.ingredient.description." + beeId + "_bee")) {
                            description = Component.literal("");
                        }
                    }
                    if (beeData != null && !beeData.selfbreed()) {
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
            Item item = BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, itemName)).map(Holder::value).orElse(Items.AIR);
            registration.addIngredientInfo(new ItemStack(item), VanillaTypes.ITEM_STACK, Component.translatable("productivebees.ingredient.description." + itemName));
        }

        // Quarry and lumber bee recipes
        Collection<AdvancedBeehiveRecipe> chipHiveRecipes = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.QUARRY).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (b.asItem() == Items.AIR || b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) return;
            List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
            blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(Lazy.of(() -> beeList.get("productivebees:quarry_bee")), blockItemOutput));
        });
        BuiltInRegistries.BLOCK.getTagOrEmpty(ModTags.LUMBER).forEach(blockHolder -> {
            Block b = blockHolder.value();
            if (b.asItem() == Items.AIR || b.builtInRegistryHolder().is(ModTags.DUPE_BLACKLIST)) return;
            List<TagOutputRecipe.ChancedOutput> blockItemOutput = new ArrayList<>();
            blockItemOutput.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(b.asItem()), 1, 1, 1f));
            chipHiveRecipes.add(new AdvancedBeehiveRecipe(Lazy.of(() -> beeList.get("productivebees:lumber_bee")), blockItemOutput));
        });
        registration.addRecipes(ADVANCED_BEEHIVE_TYPE, chipHiveRecipes.stream().toList());

        // Configurable comb / comb-block crafting recipes (one pair per known bee type)
        RecipeHolder<?> honeycombRecipe = recipeMap.byKey(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "comb_block/configurable_honeycomb")));
        int count = 4;
        if (honeycombRecipe != null && honeycombRecipe.value() instanceof ConfigurableHoneycombRecipe configurableHoneycombRecipe) {
            count = configurableHoneycombRecipe.count;
        }
        List<RecipeHolder<CraftingRecipe>> combRecipes = new ArrayList<>();
        for (var holder : BeeRegistries.all().toList()) {
            Identifier beeType = holder.unwrapKey().orElseThrow().identifier();
            Identifier idComb = beeType.withPath(p -> p + "_honeycomb");
            Identifier idCombBlock = beeType.withPath(p -> p + "_comb");

            DataComponentPatch patch = DataComponentPatch.builder()
                    .set(ModDataComponents.BEE_TYPE.get(), beeType)
                    .build();
            Ingredient combIngredient = ComponentIngredient.of(patch, ModItems.CONFIGURABLE_HONEYCOMB.get());
            Ingredient combBlockIngredient = ComponentIngredient.of(patch, ModItems.CONFIGURABLE_COMB_BLOCK.get());

            // comb_block from N combs
            List<Ingredient> combInput = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                combInput.add(combIngredient);
            }
            ItemStackTemplate combBlockResult = new ItemStackTemplate(ModItems.CONFIGURABLE_COMB_BLOCK.get().builtInRegistryHolder(), 1, patch);
            combRecipes.add(new RecipeHolder<>(
                    ResourceKey.create(Registries.RECIPE, idComb),
                    new ShapelessRecipe(
                            new Recipe.CommonInfo(true),
                            new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.BUILDING, ""),
                            combBlockResult,
                            combInput)));

            // N combs from comb_block
            ItemStackTemplate combResult = new ItemStackTemplate(ModItems.CONFIGURABLE_HONEYCOMB.get().builtInRegistryHolder(), count, patch);
            combRecipes.add(new RecipeHolder<>(
                    ResourceKey.create(Registries.RECIPE, idCombBlock),
                    new ShapelessRecipe(
                            new Recipe.CommonInfo(true),
                            new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
                            combResult,
                            List.of(combBlockIngredient))));
        }
        registration.addRecipes(mezz.jei.api.constants.RecipeTypes.CRAFTING, combRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CentrifugeScreen.class, 45, 35, 24, 16, CENTRIFUGE_TYPE);
        registration.addRecipeClickArea(BottlerScreen.class, 152, 37, 14, 14, BOTTLER_TYPE);
        registration.addRecipeClickArea(BreedingChamberScreen.class, 82, 14, 45, 22, BEE_BREEDING_TYPE);
        registration.addRecipeClickArea(IncubatorScreen.class, 74, 35, 14, 16, INCUBATION_TYPE);
    }
}
