package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.HiveType;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(gen, pRegistries);
    }

    @Override
    public String getName() {
        return "PB Recipes";
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
//        var mixingRecipeBuilder = new MixingRecipeGenerator();

        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                if (ProductiveBees.includeMod(modid)) {
                    name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                    buildHiveRecipe(modid, name, type, consumer);
                    buildBoxRecipe(modid, name, type, consumer);
                    if (modid.equals(ProductiveBees.MODID)) {
                        buildCanvasRecipes(name, consumer);
                    }
                }
            });
        });

        ModBlocks.hiveStyles.forEach(style -> {
            buildCanvasStonecutterRecipes(style, consumer);
            buildCanvasCorailWoodcutterRecipes(style, consumer);
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            Block h = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, dyeColor.getSerializedName() + "_petrified_honey"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, h)
                .requires(ModBlocks.PETRIFIED_HONEY.get())
                .requires(dyeColor.getTag())
                .unlockedBy("has_honey", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.PETRIFIED_HONEY.get()))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "petrified_honey/" + dyeColor.getSerializedName()));
        });

//          // Ribbeet centrifuge
//        CentrifugeRecipeBuilder.configurable("ribbeet")
//                .clearOutput()
//                .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.OCHRE_FROGLIGHT), 1, 1, 0.05f))
//                .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.PEARLESCENT_FROGLIGHT), 1, 1, 0.05f))
//                .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.VERDANT_FROGLIGHT), 1, 1, 0.05f))
////                .setFluidOutput(new FluidStack(ModFluids.HONEY, 0))
//                .save(consumer.withConditions(new BeeExistsCondition(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "ribbeet"))), ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge/honeycomb_ribbeet"));


//        var egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
//        var tag = new CompoundTag();
//        tag.putString("type", "productivebees:iron");
//        tag.putString("id", ModEntities.CONFIGURABLE_BEE.getId().toString());
//        egg.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, egg)
//                .unlockedBy("has_honey", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.PETRIFIED_HONEY.get()))
//                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "test_egg"));

        // Chemlib
//        List<String> chemicals = Arrays.stream(new String[]{"actinium", "americium", "antimony", "argon", "arsenic", "astatine", "barium", "berkelium", "beryllium", "bohrium", "boron", "bromine", "cadmium", "calcium", "californium", "cerium", "cesium", "chlorine", "chromium", "copernicium", "curium", "darmstadtium", "dubnium", "dysprosium", "einsteinium", "erbium", "europium", "fermium", "flerovium", "fluorine", "francium", "gadolinium", "gallium", "germanium", "hafnium", "hassium", "helium", "holmium", "hydrogen", "indium", "iodine", "krypton", "lanthanum", "lawrencium", "lithium", "livermorium", "lutetium", "magnesium", "manganese", "meitnerium", "mendelevium", "mercury", "molybdenum", "moscovium", "neodymium", "neon", "neptunium", "nihonium", "niobium", "nitrogen", "nobelium", "oganesson", "oxygen", "palladium", "phosphorus", "plutonium", "polonium", "potassium", "praseodymium", "promethium", "protactinium", "radium", "radon", "rhenium", "rhodium", "roentgenium", "rubidium", "ruthenium", "rutherfordium", "samarium", "scandium", "seaborgium", "selenium", "silicium", "sodium", "strontium", "tantalum", "technetium", "tellurium", "tennessine", "terbium", "thallium", "thorium", "thulium", "vanadium", "xenon", "ytterbium", "yttrium", "zirconium"}).toList();
//        chemicals.forEach(name -> {
//            boolean isGtCompatBee = name.equals("molybdenum") || name.equals("neodymium") || name.equals("palladium");
//            var r = CentrifugeRecipeBuilder.configurable(name)
//                    .addOutput(new AbstractRecipeBuilder.ModItemOutput("chemlib:" + (name.equals("silicium") ? "silicon" : name), 80))
//                    .withCondition(new ModLoadedCondition("chemlib"))
//                    .withCondition(new BeeExistsCondition(ProductiveBees.MODID + ":" + name))
//                    .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey"));
//            if (isGtCompatBee) {
//                r.withCondition(new TagEmptyCondition("c:raw_materials/" + name));
//            }
//            r.save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge/chemlib/honeycomb_" + name));

//            ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
//            BeeCreator.setTag(ProductiveBees.MODID + ":" + name, stack);
//            var b = mixingRecipeBuilder.builder(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "create/mixing/chemlib/honeycomb_" + name))
//                    .require(StrictNBTIngredient.of(stack))
//                    .output(0.8f, new ResourceLocation("chemlib:" + (name.equals("silicium") ? "silicon" : name)), 1)
//                    .output(ModFluids.HONEY.get(), 50)
//                    .output(ModItems.WAX.get())
//                    .whenModLoaded("chemlib")
//                    .whenModLoaded("create")
//                    .requiresHeat(HeatCondition.HEATED);
//            if (isGtCompatBee) {
//                b.withCondition(new TagEmptyCondition("c:raw_materials/" + name));
//            }
//            b.build(consumer);
//        }

        // reactors
        List<CentrifugeRecipeBuilder.RecipeConfig> ingots = new ArrayList<>() {{
            add(new CentrifugeRecipeBuilder.RecipeConfig("blutonium", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/blutonium", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("cyanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/cyanite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("inanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/inanite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("insanite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/insanite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("ludicrite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/ludicrite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("magentite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/magentite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("ridiculite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/ridiculite", new HashMap<>()));
            add(new CentrifugeRecipeBuilder.RecipeConfig("graphite", "reactors", new String[]{"extremereactors", "biggerreactors"}, "#c:ingots/graphite", new HashMap<>()));
        }};
        ingots.forEach((config) -> {
            var recipe = CentrifugeRecipeBuilder.configurable(config.name())
                    .addOutput(new TagOutputRecipe.ChancedOutput(Ingredient.of(ItemTags.create(ResourceLocation.parse(config.centrifugeOutput().replace("#", "")))), 1, 1, 1f))
//                    .addOutput(new TagOutputRecipe.ChancedOutput(DataComponentIngredient.of(false, BeeCreator.getSpawnEgg(ResourceLocation.parse("productivebees:iron"))), 1, 1, 1f))
                    .setFluidOutput(new FluidStack(ModFluids.HONEY, 50));
            if (config.centrifugeOutput().startsWith("#")) {
                recipe.withCondition(new NotCondition(new TagEmptyCondition(config.centrifugeOutput().replace("#", ""))));
            }
            recipe.save(consumer.withConditions(new BeeExistsCondition(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, config.name()))), ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge/" + config.folder() + "/honeycomb_" + config.name()));

//            ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
//            BeeCreator.setType(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, config.name()), stack);
//            Arrays.stream(config.mods()).forEach(s -> {
//                mixingRecipeBuilder.builder(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "create/mixing/" + s + "/honeycomb_" + config.name()))
//                        .require(StrictNBTIngredient.of(stack))
//                        .output(1.0f, new ResourceLocation(s + ":" + config.name()), 1)
//                        .output(ModFluids.HONEY.get(), 50)
//                        .output(ModItems.WAX.get())
//                        .withCondition(new BeeExistsCondition(ProductiveBees.MODID + ":" + config.name()))
//                        .whenModLoaded("create")
//                        .whenModLoaded(s)
//                        .requiresHeat(HeatCondition.HEATED)
//                        .build(consumer);
//            });
        });

        // GTCEu Modern
//        List<String> gtceuBees = Arrays.stream(new String[]{"barite", "bastnasite", "bauxite", "chromite", "cobaltite", "electrotine", "galena", "graphite", "ilmenite", "lepidolite", "molybdenum", "naquadah", "neodymium", "oilsands", "palladium", "pyrochlore", "pyrolusite", "realgar", "scheelite", "sheldonite", "sphalerite", "stibnite", "tantalite", "tetrahedrite", "tricalcium_phosphate", "tungstate", "vanadium_magnetite"}).toList();
//        gtceuBees.forEach(name -> {
//            String resourceName = name.equals("sheldonite") ? "cooperite" : name;
//            var r = CentrifugeRecipeBuilder.configurable(name)
//                    .addOutput(new AbstractRecipeBuilder.ModItemOutput("#c:raw_materials/" + resourceName, 80))
//                    .withCondition(new ModLoadedCondition("gtceu"))
//                    .withCondition(new NotCondition(new TagEmptyCondition("c:raw_materials/" + resourceName)))
//                    .withCondition(new BeeExistsCondition(ProductiveBees.MODID + ":gtceu/" + name))
//                    .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey"));
//            r.save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge/gtceu/honeycomb_" + name));

//            ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
//            BeeCreator.setTag(ProductiveBees.MODID + ":" + name, stack);
//            var b = mixingRecipeBuilder.builder(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "create/mixing/gtceu/honeycomb_" + name))
//                    .require(StrictNBTIngredient.of(stack))
//                    .output(0.8f, new ResourceLocation("gtceu:raw_" + resourceName), 1)
//                    .output(ModFluids.HONEY.get(), 50)
//                    .output(ModItems.WAX.get())
//                    .whenModLoaded("create")
//                    .whenModLoaded("gtceu")
//                    .withCondition(new BeeExistsCondition(ProductiveBees.MODID + ":gtceu/" + name))
//                    .requiresHeat(HeatCondition.HEATED);
//            b.build(consumer);
//        });
    }

    private void buildHiveRecipe(String modid, String name, HiveType type, RecipeOutput consumer) {
        try {
            var plank = type.planks() != null ? Optional.of(type.planks()) : BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(type.customPlank().blockName));
            if (plank.isPresent()) {
                Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                        .define('W', Ingredient.of(plank.get()))
                        .define('H', Ingredient.of(ModTags.Common.HIVES))
                        .define('C', Ingredient.of(ModTags.Common.HONEYCOMBS))
                        .define('F', Ingredient.of(ModTags.Common.CAMPFIRES))
                        .define('S', Ingredient.of(Tags.Items.TOOLS_SHEAR))
                        .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                        .save(consumer.withConditions(modLoaded(modid)), ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "hives/advanced_" + name + "_beehive"));

                buildHiveResetRecipes(modid, hive, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "hives/advanced_" + name + "_beehive_clear"), consumer);
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("error generating hive recipe for " + name + " " + e.getMessage());
            ProductiveBees.LOGGER.warn("planks " + type.planks());
        }
    }

    private void buildBoxRecipe(String modid, String name, HiveType type, RecipeOutput consumer) {
        try {
            var plank = type.planks() != null ? Optional.of(type.planks()) : BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(type.customPlank().blockName));
            if (plank.isPresent()) {
                Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();
                ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                        .define('W', Ingredient.of(plank.get()))
                        .define('C', Ingredient.of(ModTags.Common.HONEYCOMBS))
                        .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                        .save(consumer.withConditions(modLoaded(modid)), ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_boxes/expansion_box_" + name));
            }
        } catch (Exception e) {
            ProductiveBees.LOGGER.warn("error generating box recipe for " + name + " " + e.getMessage());
            ProductiveBees.LOGGER.warn("planks " + type.planks());
        }
    }

    private void buildCanvasRecipes(String style, RecipeOutput consumer) {
        Block hivein = ModBlocks.HIVES.get("advanced_" + style + "_beehive").get();
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block boxin = ModBlocks.EXPANSIONS.get("expansion_box_" + style).get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive)
                .group("hives")
                .pattern("PPP").pattern("PHP").pattern("PPP")
                .define('H', Ingredient.of(hivein))
                .define('P', Ingredient.of(Items.PAPER))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "hives/advanced_" + style + "_canvas_hive"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .pattern("PPP").pattern("PHP").pattern("PPP")
                .define('H', Ingredient.of(boxin))
                .define('P', Ingredient.of(Items.PAPER))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_boxes/expansion_box_" + style + "_canvas"));
    }

    private void buildHiveResetRecipes(String modid, Block hive, ResourceLocation location, RecipeOutput consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, hive).group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .requires(hive)
        .save(consumer.withConditions(modLoaded(modid)), location);
    }

    private void buildCanvasStonecutterRecipes(String style, RecipeOutput consumer) {
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModTags.CANVAS_HIVES), RecipeCategory.MISC, hive)
                .group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "stonecutter/" + style + "_canvas_hive"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModTags.CANVAS_BOXES), RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .unlockedBy("has_box", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "stonecutter/" + style + "_canvas_expansion_box"));
    }

    private void buildCanvasCorailWoodcutterRecipes(String style, RecipeOutput consumer) {
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

//        ConditionalRecipe.builder().addCondition(
//            modLoaded("corail_woodcutter")
//        ).addRecipe(
//            woodcutter(Ingredient.of(ModTags.CANVAS_HIVES), RecipeCategory.MISC, hive)
//                .group("hives")
//                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
//                .unlockedBy("has_woodcutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
//                ::save
//        )
//        .build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "corail/woodcutter/" + style + "_canvas_hive"));
//        ConditionalRecipe.builder().addCondition(
//            modLoaded("corail_woodcutter")
//        ).addRecipe(
//            woodcutter(Ingredient.of(ModTags.CANVAS_BOXES), RecipeCategory.MISC, box)
//                .group("expansion_boxes")
//                .unlockedBy("has_box", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
//                .unlockedBy("has_woodcutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
//                ::save
//        )
//        .build(consumer, ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "corail/woodcutter/" + style + "_canvas_expansion_box"));
    }

//    public static SingleItemRecipeBuilder woodcutter(Ingredient ingredient, RecipeCategory category, ItemLike output) {
//        return new SingleItemRecipeBuilder(category, ModRecipeSerializers.WOODCUTTING, ingredient, output, 1);
//    }
}
