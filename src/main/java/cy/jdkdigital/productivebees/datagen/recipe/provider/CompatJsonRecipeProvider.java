package cy.jdkdigital.productivebees.datagen.recipe.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cy.jdkdigital.productivebees.ProductiveBees;
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
