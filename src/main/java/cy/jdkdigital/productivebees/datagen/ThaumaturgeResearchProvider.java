package cy.jdkdigital.productivebees.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ThaumaturgeResearchProvider implements DataProvider
{
    public static final String THAUMIUM_BEE = ProductiveBees.MODID + ":thaumium_bee";
    public static final String VIS_BEE = ProductiveBees.MODID + ":vis_bee";
    public static final String VOID_BEE = ProductiveBees.MODID + ":void_bee";
    public static final String ELDRITCH_BEE = ProductiveBees.MODID + ":eldritch_bee";

    private final PackOutput output;
    private final List<Entry> entries = new ArrayList<>();

    public ThaumaturgeResearchProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildEntries();
        Path root = output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(ProductiveBees.MODID).resolve("thaumaturge").resolve("research_entry");
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, root.resolve(e.path + ".json")))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Thaumaturge Research";
    }

    private void buildEntries() {
        entry("thaumium_bee", THAUMIUM_BEE, "thaumaturge:alchemy", 1, 4,
                List.of("thaumaturge:metallurgy"),
                List.of("thaumaturge:ingot_thaumium"),
                List.of(aspect("metallum", 4), aspect("bestia", 3), aspect("praecantatio", 3)), 2,
                stage(THAUMIUM_BEE, 0, "required_knowledge", knowledge("observation", "thaumaturge:alchemy", 1)),
                stage(THAUMIUM_BEE, 1, "recipes", strings(ProductiveBees.MODID + ":thaumaturge/crucible/thaumium_bee")));

        entry("vis_bee", VIS_BEE, "thaumaturge:auromancy", -4, 5,
                List.of("thaumaturge:node_preserve"),
                List.of("thaumaturge:textures/aspects/auram.png"),
                List.of(aspect("auram", 4), aspect("bestia", 3), aspect("victus", 3)), 3,
                stage(VIS_BEE, 0, "required_knowledge", knowledge("theory", "thaumaturge:auromancy", 1)),
                stage(VIS_BEE, 1, "recipes", strings(ProductiveBees.MODID + ":thaumaturge/infusion/vis_bee")));

        entry("void_bee", VOID_BEE, "thaumaturge:eldritch", 3, 3,
                List.of("thaumaturge:base_eldritch"),
                List.of("thaumaturge:ingot_void"),
                List.of(aspect("metallum", 4), aspect("vitium", 4), aspect("bestia", 3)), 3,
                stage(VOID_BEE, 0, "required_knowledge", knowledge("theory", "thaumaturge:eldritch", 1)),
                stage(VOID_BEE, 1, "recipes", strings(ProductiveBees.MODID + ":thaumaturge/crucible/void_bee")));

        entry("eldritch_bee", ELDRITCH_BEE, "thaumaturge:eldritch", -3, -3,
                List.of(VIS_BEE, "thaumaturge:base_eldritch"),
                List.of("thaumaturge:textures/aspects/vitium.png"),
                List.of(aspect("vitium", 5), aspect("alienis", 4), aspect("bestia", 3)), 3,
                stage(ELDRITCH_BEE, 0, "required_knowledge", knowledge(
                        knowledgeEntry("observation", "thaumaturge:eldritch", 1),
                        knowledgeEntry("theory", "thaumaturge:eldritch", 2))),
                stage(ELDRITCH_BEE, 1, "recipes", strings(ProductiveBees.MODID + ":thaumaturge/infusion/eldritch_bee")));
    }

    private void entry(String path, String id, String category, int column, int row, List<String> parents, List<String> icons, List<JsonObject> noteAspects, int complexity, JsonObject... stages) {
        JsonObject json = new JsonObject();
        json.addProperty("category", category);
        json.addProperty("name", titleKey(id));
        json.add("parents", strings(parents.toArray(String[]::new)));
        json.addProperty("column", column);
        json.addProperty("row", row);
        json.add("icons", strings(icons.toArray(String[]::new)));

        JsonArray stageList = new JsonArray();
        for (JsonObject stage : stages) {
            stageList.add(stage);
        }
        json.add("stages", stageList);

        JsonArray aspectList = new JsonArray();
        noteAspects.forEach(aspectList::add);
        json.add("note_aspects", aspectList);
        json.addProperty("complexity", complexity);
        json.add("neoforge:conditions", conditions());

        entries.add(new Entry(path, json));
    }

    private static JsonObject stage(String id, int index, String key, JsonArray value) {
        JsonObject stage = new JsonObject();
        stage.addProperty("text", stageKey(id, index));
        stage.add(key, value);
        return stage;
    }

    private static JsonArray knowledge(String type, String category, int amount) {
        return knowledge(knowledgeEntry(type, category, amount));
    }

    private static JsonArray knowledge(JsonObject... entries) {
        JsonArray arr = new JsonArray();
        for (JsonObject entry : entries) {
            arr.add(entry);
        }
        return arr;
    }

    private static JsonObject knowledgeEntry(String type, String category, int amount) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", type);
        obj.addProperty("category", category);
        obj.addProperty("amount", amount);
        return obj;
    }

    private static JsonObject aspect(String name, int amount) {
        JsonObject obj = new JsonObject();
        obj.addProperty("aspect", "thaumaturge:" + name);
        obj.addProperty("amount", amount);
        return obj;
    }

    private static JsonArray strings(String... values) {
        JsonArray arr = new JsonArray();
        for (String value : values) {
            arr.add(new JsonPrimitive(value));
        }
        return arr;
    }

    private static JsonArray conditions() {
        JsonObject modLoaded = new JsonObject();
        modLoaded.addProperty("type", "neoforge:mod_loaded");
        modLoaded.addProperty("modid", "thaumaturge");
        JsonArray arr = new JsonArray();
        arr.add(modLoaded);
        return arr;
    }

    public static String titleKey(String id) {
        return "research." + id.replace(':', '.') + ".title";
    }

    public static String stageKey(String id, int stage) {
        return "research." + id.replace(':', '.') + ".stage_" + stage;
    }

    private record Entry(String path, JsonObject json) {}
}
