package cy.jdkdigital.productivebees.datagen.recipe.provider;

import com.simibubi.create.foundation.data.recipe.MixingRecipeGen;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.datagen.recipe.builder.AbstractRecipeBuilder;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.HiveType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.woodcutter.registry.ModRecipeSerializers;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider implements IConditionBuilder
{
    public RecipeProvider(PackOutput gen) {
        super(gen);
    }

    @Override
    public String getName() {
        return "PB Recipes";
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                buildHiveRecipe(modid, name, type, consumer);
                buildBoxRecipe(modid, name, type, consumer);
                if (modid.equals(ProductiveBees.MODID)) {
                    buildCanvasRecipes(name, consumer);
                }
            });
        });

        ModBlocks.hiveStyles.forEach(style -> {
            buildCanvasStonecutterRecipes(style, consumer);
            buildCanvasCorailWoodcutterRecipes(style, consumer);
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            Block h = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(ProductiveBees.MODID, dyeColor.getSerializedName() + "_petrified_honey"));
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, h)
                .requires(ModBlocks.PETRIFIED_HONEY.get())
                .requires(dyeColor.getTag())
                .unlockedBy("has_honey", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.PETRIFIED_HONEY.get()))
                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "petrified_honey/" + dyeColor.getSerializedName()));
        });

        // Chemlib
        List<String> chemicals = Arrays.stream(new String[]{"actinium", "americium", "antimony", "argon", "arsenic", "astatine", "barium", "berkelium", "beryllium", "bohrium", "boron", "bromine", "cadmium", "calcium", "californium", "cerium", "cesium", "chlorine", "chromium", "copernicium", "curium", "darmstadtium", "dubnium", "dysprosium", "einsteinium", "erbium", "europium", "fermium", "flerovium", "fluorine", "francium", "gadolinium", "gallium", "germanium", "hafnium", "hassium", "helium", "holmium", "hydrogen", "indium", "iodine", "krypton", "lanthanum", "lawrencium", "lithium", "livermorium", "lutetium", "magnesium", "manganese", "meitnerium", "mendelevium", "mercury", "molybdenum", "moscovium", "neodymium", "neon", "neptunium", "nihonium", "niobium", "nitrogen", "nobelium", "oganesson", "oxygen", "palladium", "phosphorus", "plutonium", "polonium", "potassium", "praseodymium", "promethium", "protactinium", "radium", "radon", "rhenium", "rhodium", "roentgenium", "rubidium", "ruthenium", "rutherfordium", "samarium", "scandium", "seaborgium", "selenium", "silicium", "sodium", "strontium", "tantalum", "technetium", "tellurium", "tennessine", "terbium", "thallium", "thorium", "thulium", "vanadium", "xenon", "ytterbium", "yttrium", "zirconium"}).toList();
        List<String> dusts = Arrays.stream(new String[]{"americium", "antimony", "arsenic", "barium", "beryllium", "boron", "cadmium", "calcium", "cerium", "cesium", "chromium", "darmstadtium", "europium", "gallium", "indium", "lanthanum", "lithium", "lutetium", "magnesium", "manganese", "molybdenum", "neodymium", "niobium", "palladium", "phosphorus", "plutonium", "potassium", "rhodium", "ruthenium", "samarium", "silicium", "sodium", "tantalum", "thorium", "vanadium", "yttrium", "zirconium"}).toList();
        chemicals.forEach(name -> {
            var recipe = CentrifugeRecipeBuilder.configurable(name)
                    .addOutput(new AbstractRecipeBuilder.ModItemOutput("chemlib:" + name, 80))
                    .withCondition(new ModLoadedCondition("chemlib"))

                    .withCondition(new BeeExistsCondition(name))
                    .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey"));
            if (dusts.contains(name)) {
                recipe.withCondition(new NotCondition(new ModLoadedCondition("gtceu")));
            }
            recipe.save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/chemlib/honeycomb_" + name));

//            MixingRecipeGen.create()
        });
        dusts.forEach(name -> {
            CentrifugeRecipeBuilder.configurable(name)
                    .addOutput(new AbstractRecipeBuilder.ModItemOutput("chemlib:" + name, 80))
                    .withCondition(new ModLoadedCondition("chemlib"))
                    .withCondition(new ModLoadedCondition("gtceu"))
                    .withCondition(new BeeExistsCondition(name))
                    .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("#forge:honey"))
                    .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/chemlib/honeycomb_" + name + "_dust"));
        });
    }

    private void buildHiveRecipe(String modid, String name, HiveType type, Consumer<FinishedRecipe> consumer) {
        Block hive = ModBlocks.HIVES.get("advanced_" + name + "_beehive").get();
        ConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .addRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, hive).group("hives").pattern("WWW").pattern("CHC").pattern("FWS")
                                .define('W', type.planks())
                                .define('H', Ingredient.of(ModTags.Forge.HIVES))
                                .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                                .define('F', Ingredient.of(ModTags.Forge.CAMPFIRES))
                                .define('S', Ingredient.of(Tags.Items.SHEARS))
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                ::save
                )
                .build(consumer, new ResourceLocation(ProductiveBees.MODID, "hives/advanced_" + name + "_beehive"));

        buildHiveResetRecipes(modid, hive, new ResourceLocation(ProductiveBees.MODID, "hives/advanced_" + name + "_beehive_clear"), consumer);
    }

    private void buildBoxRecipe(String modid, String name, HiveType type, Consumer<FinishedRecipe> consumer) {
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + name).get();

        ConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                )
                .addRecipe(
                        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box).group("expansion_boxes").pattern("WWW").pattern("WCW").pattern("WWW")
                                .define('W', type.planks())
                                .define('C', Ingredient.of(ModTags.Forge.HONEYCOMBS))
                                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                                ::save
                )
                .build(consumer, new ResourceLocation(ProductiveBees.MODID, "expansion_boxes/expansion_box_" + name));
    }

    private void buildCanvasRecipes(String style, Consumer<FinishedRecipe> consumer) {
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
                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "hives/advanced_" + style + "_canvas_hive"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .pattern("PPP").pattern("PHP").pattern("PPP")
                .define('H', Ingredient.of(boxin))
                .define('P', Ingredient.of(Items.PAPER))
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "expansion_boxes/expansion_box_" + style + "_canvas"));
    }

    private void buildHiveResetRecipes(String modid, Block hive, ResourceLocation location, Consumer<FinishedRecipe> consumer) {
        ConditionalRecipe.builder()
                .addCondition(
                        modLoaded(modid)
                ).addRecipe(
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, hive).group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .requires(hive)
                ::save
        ).build(consumer, location);
    }

    private void buildCanvasStonecutterRecipes(String style, Consumer<FinishedRecipe> consumer) {
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModTags.CANVAS_HIVES), RecipeCategory.MISC, hive)
                .group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "stonecutter/" + style + "_canvas_hive"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(ModTags.CANVAS_BOXES), RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .unlockedBy("has_box", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_stonecutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "stonecutter/" + style + "_canvas_expansion_box"));
    }

    private void buildCanvasCorailWoodcutterRecipes(String style, Consumer<FinishedRecipe> consumer) {
        Block hive = ModBlocks.HIVES.get("advanced_" + style + "_canvas_beehive").get();
        Block box = ModBlocks.EXPANSIONS.get("expansion_box_" + style + "_canvas").get();

        ConditionalRecipe.builder().addCondition(
            modLoaded("corail_woodcutter")
        ).addRecipe(
            woodcutter(Ingredient.of(ModTags.CANVAS_HIVES), RecipeCategory.MISC, hive)
                .group("hives")
                .unlockedBy("has_hive", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_woodcutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                ::save
        )
        .build(consumer, new ResourceLocation(ProductiveBees.MODID, "corail/woodcutter/" + style + "_canvas_hive"));
        ConditionalRecipe.builder().addCondition(
            modLoaded("corail_woodcutter")
        ).addRecipe(
            woodcutter(Ingredient.of(ModTags.CANVAS_BOXES), RecipeCategory.MISC, box)
                .group("expansion_boxes")
                .unlockedBy("has_box", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BEEHIVE))
                .unlockedBy("has_woodcutter", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STONECUTTER))
                ::save
        )
        .build(consumer, new ResourceLocation(ProductiveBees.MODID, "corail/woodcutter/" + style + "_canvas_expansion_box"));
    }

    public static SingleItemRecipeBuilder woodcutter(Ingredient ingredient, RecipeCategory category, ItemLike output) {
        return new SingleItemRecipeBuilder(category, ModRecipeSerializers.WOODCUTTING, ingredient, output, 1);
    }
}
