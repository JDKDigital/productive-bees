package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static cy.jdkdigital.productivebees.datagen.recipe.BeeRecipeHelper.beeResult;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider
{
    private final HolderGetter<Item> items;

    public RecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    protected void buildRecipes() {
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                if (ProductiveBees.includeMod(modid)) {
                    String finalName = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    buildHiveRecipe(modid, finalName, type);
                    buildBoxRecipe(modid, finalName, type);
                    if (modid.equals(ProductiveBees.MODID)) {
                        buildCanvasRecipes(finalName);
                    }
                }
            });
        });

        ModBlocks.hiveStyles.forEach(this::buildCanvasStonecutterRecipes);

        for (DyeColor dyeColor : DyeColor.values()) {
            Block h = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, dyeColor.getSerializedName() + "_petrified_honey"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.BUILDING_BLOCKS, h)
                    .requires(ModBlocks.PETRIFIED_HONEY.get())
                    .requires(dyeColor.getTag())
                    .unlockedBy("has_honey", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.PETRIFIED_HONEY.get()))
                    .save(this.output, recipeKey("petrified_honey/" + dyeColor.getSerializedName()));
        }

        buildBurlyRecipe();
        buildReactorCentrifugeRecipes();
    }

    private void buildBurlyRecipe() {
        Identifier burly = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "burly");
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, beeResult("burly"))
                .pattern("DDD").pattern("DED").pattern("DDD")
                .define('D', Items.GOLDEN_DANDELION)
                .define('E', Items.BEE_SPAWN_EGG)
                .unlockedBy("has_bee_spawn_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEE_SPAWN_EGG))
                .save(this.output.withConditions(new BeeExistsCondition(burly)), recipeKey("shaped/burly"));
    }

    private void buildReactorCentrifugeRecipes() {
        List<CentrifugeRecipeBuilder.RecipeConfig> ingots = new ArrayList<>();
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("blutonium", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/blutonium", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("cyanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/cyanite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("inanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/inanite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("insanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/insanite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("ludicrite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/ludicrite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("magentite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/magentite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("ridiculite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/ridiculite", new HashMap<>()));
        ingots.add(new CentrifugeRecipeBuilder.RecipeConfig("graphite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/graphite", new HashMap<>()));

        for (CentrifugeRecipeBuilder.RecipeConfig config : ingots) {
            String tagPath = config.centrifugeOutput().replace("#", "");
            var tagKey = TagKey.create(Registries.ITEM, Identifier.parse(tagPath));
            String fullBeeName = config.folder() + "/" + config.name();
            var recipe = CentrifugeRecipeBuilder.configurable(fullBeeName)
                    .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(this.items.getOrThrow(tagKey)), 1, 1, 1f))
                    .setFluidOutput(ModFluids.HONEY.get(), 50);
            if (config.centrifugeOutput().startsWith("#")) {
                recipe.withCondition(new NotCondition(new TagEmptyCondition<>(tagKey)));
            }
            recipe.save(this.output.withConditions(new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, fullBeeName))),
                    recipeKey("centrifuge/" + config.folder() + "/honeycomb_" + config.name()));
        }
    }

    private void buildHiveRecipe(String modid, String name, HiveType type) {
        try {
            Optional<? extends ItemLike> plank = type.planks() != null
                    ? Optional.of(type.planks())
                    : BuiltInRegistries.ITEM.getOptional(Identifier.parse(type.customPlank().blockName));
            if (plank.isPresent()) {
                Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, hive)
                        .group("hives")
                        .pattern("WWW").pattern("CHC").pattern("FWS")
                        .define('W', Ingredient.of(plank.get()))
                        .define('H', ModTags.Common.HIVES)
                        .define('C', ModTags.Common.HONEYCOMBS)
                        .define('F', ModTags.Common.CAMPFIRES)
                        .define('S', Tags.Items.TOOLS_SHEAR)
                        .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                        .save(this.output.withConditions(new ModLoadedCondition(modid)),
                                recipeKey("hives/advanced_" + name + "_beehive"));

                buildHiveResetRecipes(modid, hive, recipeKey("hives/advanced_" + name + "_beehive_clear"));
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("error generating hive recipe for " + name + " " + e.getMessage());
            ProductiveBees.LOGGER.warn("planks " + type.planks());
        }
    }

    private void buildBoxRecipe(String modid, String name, HiveType type) {
        try {
            Optional<? extends ItemLike> plank = type.planks() != null
                    ? Optional.of(type.planks())
                    : BuiltInRegistries.ITEM.getOptional(Identifier.parse(type.customPlank().blockName));
            if (plank.isPresent()) {
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, box)
                        .group("expansion_boxes")
                        .pattern("WWW").pattern("WCW").pattern("WWW")
                        .define('W', Ingredient.of(plank.get()))
                        .define('C', ModTags.Common.HONEYCOMBS)
                        .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                        .save(this.output.withConditions(new ModLoadedCondition(modid)),
                                recipeKey("expansion_boxes/expansion_box_" + name));
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("error generating box recipe for " + name + " " + e.getMessage());
            ProductiveBees.LOGGER.warn("planks " + type.planks());
        }
    }

    private void buildCanvasRecipes(String style) {
        Block hivein = ModBlocks.HIVES.get("advanced_" + style + "_beehive").get();
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block boxin = ModBlocks.EXPANSIONS.get("expansion_box_" + style).get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, hive)
                .group("hives")
                .pattern("PPP").pattern("PHP").pattern("PPP")
                .define('H', Ingredient.of(hivein))
                .define('P', Ingredient.of(Items.PAPER))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(this.output, recipeKey("hives/advanced_" + style + "_canvas_hive"));
        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .pattern("PPP").pattern("PHP").pattern("PPP")
                .define('H', Ingredient.of(boxin))
                .define('P', Ingredient.of(Items.PAPER))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(this.output, recipeKey("expansion_boxes/expansion_box_" + style + "_canvas"));
    }

    private void buildHiveResetRecipes(String modid, Block hive, ResourceKey<Recipe<?>> id) {
        ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.MISC, hive)
                .group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .requires(hive)
                .save(this.output.withConditions(new ModLoadedCondition(modid)), id);
    }

    private void buildCanvasStonecutterRecipes(String style) {
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.items.getOrThrow(ModTags.CANVAS_HIVES)), RecipeCategory.MISC, hive, 1)
                .group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(this.output, recipeKey("stonecutter/" + style + "_canvas_hive"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(this.items.getOrThrow(ModTags.CANVAS_BOXES)), RecipeCategory.MISC, box, 1)
                .group("expansion_boxes")
                .unlockedBy("has_box", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(this.output, recipeKey("stonecutter/" + style + "_canvas_expansion_box"));
    }

    private static ResourceKey<Recipe<?>> recipeKey(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, path));
    }

    public static class Runner extends net.minecraft.data.recipes.RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new RecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "PB Recipes";
        }
    }
}
