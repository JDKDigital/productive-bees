package cy.jdkdigital.productivebees.datagen.recipe.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.datagen.ThaumaturgeResearchProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.modLoaded;

/**
 * Emits compat recipes whose upstream recipe class cannot be encoded through its own codec.
 * {@code mysticalagriculture:awakening} writes its interleaved essence-plus-pedestal list back
 * into {@code ingredients}, so building the object and encoding it corrupts the recipe.
 */
public class CompatJsonRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public CompatJsonRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, e.path))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Compat JSON Recipes";
    }

    private void buildRecipes() {
        awakening("mysticalagriculture/awakened_supremium_bee", "mysticalagriculture/supremium",
                "mysticalagriculture/awakened_supremium", "mysticalagriculture:awakened_supremium_block", 20);
        mysticalAgricultureInfusion("inferium_bee", "mysticalagriculture/prosperity", "mysticalagriculture/inferium",
                "mysticalagriculture:inferium_block", "mysticalagriculture:inferium_essence", "mysticalagriculture");
        mysticalAgricultureInfusion("prudentium_bee", "mysticalagriculture/inferium", "mysticalagriculture/prudentium",
                "mysticalagriculture:prudentium_block", "mysticalagriculture:prudentium_essence", "mysticalagriculture");
        mysticalAgricultureInfusion("tertium_bee", "mysticalagriculture/prudentium", "mysticalagriculture/tertium",
                "mysticalagriculture:tertium_block", "mysticalagriculture:tertium_essence", "mysticalagriculture");
        mysticalAgricultureInfusion("imperium_bee", "mysticalagriculture/tertium", "mysticalagriculture/imperium",
                "mysticalagriculture:imperium_block", "mysticalagriculture:imperium_essence", "mysticalagriculture");
        mysticalAgricultureInfusion("supremium_bee", "mysticalagriculture/imperium", "mysticalagriculture/supremium",
                "mysticalagriculture:supremium_block", "mysticalagriculture:supremium_essence", "mysticalagriculture");
        mysticalAgricultureInfusion("insanium_bee", "mysticalagriculture/supremium", "mysticalagriculture/insanium",
                "mysticalagradditions:insanium_block", "mysticalagradditions:insanium_essence", "mysticalagradditions");
        crystalAssembler("extendedae/crystal_assembler/entro_bee", "ae2/fluix", "ae2/entro", "#c:dusts/entro", 16, 8000);
        crucible("thaumaturge/crucible/thaumium_bee", "raw_materials/iron", "thaumaturge/thaumium",
                List.of(aspect("praecantatio", 20), aspect("terra", 20), aspect("metallum", 10), aspect("bestia", 15)),
                ThaumaturgeResearchProvider.THAUMIUM_BEE, 1);
        crucible("thaumaturge/crucible/void_bee", "thaumaturge/thaumium", "thaumaturge/void",
                List.of(aspect("metallum", 40), aspect("vitium", 20), aspect("bestia", 15)),
                ThaumaturgeResearchProvider.VOID_BEE, 1);
        thaumaturgeInfusion("thaumaturge/infusion/vis_bee", "ghostly", "thaumaturge/vis",
                List.of(aspect("auram", 32), aspect("praecantatio", 16), aspect("bestia", 16), aspect("victus", 12)),
                List.of("thaumaturge:salis_mundus", "thaumaturge:vishroom", "minecraft:honeycomb",
                        "thaumaturge:salis_mundus", "thaumaturge:vishroom", "minecraft:honeycomb"),
                8, ThaumaturgeResearchProvider.VIS_BEE, 1);
        thaumaturgeInfusion("thaumaturge/infusion/eldritch_bee", "thaumaturge/vis", "thaumaturge/eldritch",
                List.of(aspect("vitium", 48), aspect("alienis", 24), aspect("tenebrae", 24), aspect("bestia", 16)),
                List.of("thaumaturge:bottle_taint", "thaumaturge:crystal_vitium", "thaumaturge:void_seed",
                        "thaumaturge:bottle_taint", "thaumaturge:crystal_vitium", "thaumaturge:void_seed"),
                10, ThaumaturgeResearchProvider.ELDRITCH_BEE, 1);
    }

    private void crystalAssembler(String recipePath, String inputBee, String resultBee, String dustTag, int dustCount, int energy) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "extendedae:crystal_assembler");
        obj.addProperty("energy", energy);

        JsonObject fluid = new JsonObject();
        fluid.addProperty("amount", 1000);
        fluid.addProperty("ingredient", "minecraft:water");
        obj.add("input_fluid", fluid);

        JsonObject beeInput = new JsonObject();
        beeInput.add("ingredient", spawnEggIng(ProductiveBees.MODID + ":" + inputBee));
        JsonObject dustInput = new JsonObject();
        dustInput.addProperty("amount", dustCount);
        dustInput.addProperty("ingredient", dustTag);
        JsonArray inputs = new JsonArray();
        inputs.add(beeInput);
        inputs.add(dustInput);
        obj.add("input_items", inputs);

        JsonObject result = spawnEggResult(ProductiveBees.MODID + ":" + resultBee);
        result.remove("count");
        obj.add("output", result);

        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded("extendedae"));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + inputBee));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + resultBee));
        obj.add("neoforge:conditions", conditions);

        entries.add(new Entry(recipePath, obj));
    }

    /** The altar takes the bee; the eight pedestals alternate storage block and essence. */
    private void mysticalAgricultureInfusion(String name, String inputBee, String resultBee, String block, String essence, String modid) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "mysticalagriculture:infusion");

        JsonArray pedestals = new JsonArray();
        for (int i = 0; i < 4; i++) {
            pedestals.add(new JsonPrimitive(block));
            pedestals.add(new JsonPrimitive(essence));
        }
        obj.add("ingredients", pedestals);

        obj.add("input", spawnEggIng(ProductiveBees.MODID + ":" + inputBee));
        obj.add("result", spawnEggResult(ProductiveBees.MODID + ":" + resultBee));

        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded(modid));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + inputBee));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + resultBee));
        obj.add("neoforge:conditions", conditions);

        entries.add(new Entry("mysticalagriculture/" + name, obj));
    }

    private void thaumaturgeInfusion(String recipePath, String inputBee, String resultBee, List<JsonObject> aspects, List<String> components, int instability, String researchEntry, int researchStage) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "thaumaturge:infusion");

        JsonArray aspectArray = new JsonArray();
        aspects.forEach(aspectArray::add);
        obj.add("aspects", aspectArray);

        obj.add("catalyst", spawnEggIng(ProductiveBees.MODID + ":" + inputBee));

        JsonArray componentArray = new JsonArray();
        components.forEach(componentArray::add);
        obj.add("components", componentArray);

        obj.addProperty("instability", instability);
        obj.add("research", research(researchEntry, researchStage));
        obj.add("result", spawnEggResult(ProductiveBees.MODID + ":" + resultBee));
        obj.add("neoforge:conditions", thaumaturgeConditions(inputBee, resultBee));

        entries.add(new Entry(recipePath, obj));
    }

    private void crucible(String recipePath, String inputBee, String resultBee, List<JsonObject> aspects, String researchEntry, int researchStage) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "thaumaturge:crucible");

        JsonArray aspectArray = new JsonArray();
        aspects.forEach(aspectArray::add);
        obj.add("aspects", aspectArray);

        obj.add("catalyst", spawnEggIng(ProductiveBees.MODID + ":" + inputBee));
        obj.add("research", research(researchEntry, researchStage));
        obj.add("result", spawnEggResult(ProductiveBees.MODID + ":" + resultBee));
        obj.add("neoforge:conditions", thaumaturgeConditions(inputBee, resultBee));

        entries.add(new Entry(recipePath, obj));
    }

    private static JsonObject research(String entry, int stage) {
        JsonObject research = new JsonObject();
        research.addProperty("entry", entry);
        research.addProperty("stage", stage);
        return research;
    }

    private static JsonArray thaumaturgeConditions(String inputBee, String resultBee) {
        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded("thaumaturge"));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + inputBee));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + resultBee));
        return conditions;
    }

    private static JsonObject aspect(String name, int amount) {
        JsonObject a = new JsonObject();
        a.addProperty("aspect", "thaumaturge:" + name);
        a.addProperty("amount", amount);
        return a;
    }

    private void awakening(String recipePath, String inputBee, String resultBee, String pedestalItem, int essenceCount) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "mysticalagriculture:awakening");

        JsonArray essences = new JsonArray();
        for (String element : new String[]{"air", "earth", "water", "fire"}) {
            JsonObject essence = new JsonObject();
            essence.addProperty("ingredient", "mysticalagriculture:" + element + "_essence");
            essence.addProperty("count", essenceCount);
            essences.add(essence);
        }
        obj.add("essences", essences);

        obj.add("input", spawnEggIng(ProductiveBees.MODID + ":" + inputBee));

        JsonArray pedestals = new JsonArray();
        for (int i = 0; i < 4; i++) pedestals.add(new JsonPrimitive(pedestalItem));
        obj.add("ingredients", pedestals);

        obj.add("result", spawnEggResult(ProductiveBees.MODID + ":" + resultBee));

        JsonArray conditions = new JsonArray();
        conditions.add(modLoaded("mysticalagriculture"));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + inputBee));
        conditions.add(beeExists(ProductiveBees.MODID + ":" + resultBee));
        obj.add("neoforge:conditions", conditions);

        entries.add(new Entry(recipePath, obj));
    }

    private static JsonObject spawnEggIng(String beeFullId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("neoforge:ingredient_type", "productivebees:component");
        ing.add("components", entityDataComponents(beeFullId));
        ing.addProperty("items", "productivebees:spawn_egg_configurable_bee");
        return ing;
    }

    private static JsonObject spawnEggResult(String beeFullId) {
        JsonObject r = new JsonObject();
        r.addProperty("id", "productivebees:spawn_egg_configurable_bee");
        r.addProperty("count", 1);
        r.add("components", entityDataComponents(beeFullId));
        return r;
    }

    private static JsonObject entityDataComponents(String beeFullId) {
        JsonObject entityData = new JsonObject();
        entityData.addProperty("type", beeFullId);
        entityData.addProperty("id", "productivebees:configurable_bee");
        JsonObject components = new JsonObject();
        components.add("minecraft:entity_data", entityData);
        return components;
    }

    private static JsonObject beeExists(String fullBeeId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivebees:bee_exists");
        c.addProperty("bee", fullBeeId);
        return c;
    }

    private record Entry(String path, JsonObject json) {}
}
