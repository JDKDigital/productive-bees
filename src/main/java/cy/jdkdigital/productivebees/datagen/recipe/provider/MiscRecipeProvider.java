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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.modLoaded;
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.not;

/** Emits comb_block, bottler, bee_fishing, and nests JSON recipes directly (cross-mod fluid/item refs prevent standard builder usage). */
public class MiscRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public MiscRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, e.fullPath))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Misc Recipes (comb_block, bottler, bee_fishing, nests)";
    }

    private void buildRecipes() {
        combBlock(4, List.of(), "configurable_comb_block");
        configurableHoneycomb(4, List.of(), "configurable_honeycomb");
        combBlockShapeless(List.of("productivebees:comb_ghostly"), "productivebees:honeycomb_ghostly", 4, List.of(), "ghostly_block_to_comb");
        combBlockShaped(List.of("HH", "HH"), Map.of("H", "productivebees:honeycomb_ghostly"), "productivebees:comb_ghostly", 1, List.of(), "ghostly_comb_to_block");
        combBlockShapeless(List.of("productivebees:comb_milky"), "productivebees:honeycomb_milky", 4, List.of(), "milky_block_to_comb");
        combBlockShaped(List.of("HH", "HH"), Map.of("H", "productivebees:honeycomb_milky"), "productivebees:comb_milky", 1, List.of(), "milky_comb_to_block");
        combBlockShapeless(List.of("productivebees:comb_powdery"), "productivebees:honeycomb_powdery", 4, List.of(), "powdery_block_to_comb");
        combBlockShaped(List.of("HH", "HH"), Map.of("H", "productivebees:honeycomb_powdery"), "productivebees:comb_powdery", 1, List.of(), "powdery_comb_to_block");

        bottler("create:tea", 250, "minecraft:glass_bottle", "create:builders_tea", 1, List.of(modLoaded("create")), "builders_tea");
        bottler("#c:experience", 250, "minecraft:glass_bottle", "minecraft:experience_bottle", 1, List.of(not(fluidTagEmpty("c:experience"))), "experience_bottle");
        bottler("productivebees:honey", 250, "minecraft:glass_bottle", "minecraft:honey_bottle", 1, List.of(), "honey_bottle");
        bottler("productivebees:honey", 250, "minecraft:honeycomb", "productivebees:honey_treat", 1, List.of(), "honey_treat");
        bottler("minecraft:milk", 250, "minecraft:glass_bottle", "productivebees:milk_bottle", 1, List.of(), "milk_bottle");
        bottler("create:tea", 250, "minecraft:glass_bottle", "create:builders_tea", 1, List.of(modLoaded("create")), "miners_tea");
        bottler("minecraft:water", 250, "minecraft:glass_bottle", "minecraft:potion", 1, List.of(), "water_bottle");

        beeFishing("productivebees:prismarine", "#minecraft:is_deep_ocean", 0.05f, List.of(beeExists("productivebees:prismarine")), "prismarine_bee");
        beeFishing("productivebees:sponge", "#minecraft:is_deep_ocean", 0.03f, List.of(beeExists("productivebees:sponge")), "sponge_bee");
        beeFishing("productivebees:fluids/water", "#minecraft:is_river", 0.05f, List.of(beeExists("productivebees:fluids/water")), "water_bee");
        beeFishing("productivebees:atm/patrick", List.of("minecraft:warm_ocean"), 0.01f, List.of(beeExists("productivebees:atm/patrick")), "atm/patrick_bee");
        beeFishing("productivebees:fluids/oily", "#minecraft:is_deep_ocean", 0.05f, List.of(beeExists("productivebees:fluids/oily")), "fluids/oily_bee");

        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:acacia_log"), "productivebees:acacia_wood_nest", 1, List.of(), "acacia_wood_nest");
        nestShapeless(List.of("productivebees:acacia_wood_nest"), "productivebees:acacia_wood_nest", 1, List.of(), "acacia_wood_nest_clear");
        nestShaped(List.of("BBB", "SBS", "BBB"), Map.of("B", "minecraft:bamboo", "S", "minecraft:string"), "productivebees:bamboo_nest", 1, List.of(), "bamboo_nest");
        nestShapeless(List.of("productivebees:bamboo_nest"), "productivebees:bamboo_nest", 1, List.of(), "bamboo_nest_clear");
        nestShaped(List.of("PPP", "HHH", "PPP"), Map.of("P", "#minecraft:planks", "H", "#c:honeycombs"), "minecraft:beehive", 1, List.of(), "beehive");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:birch_log"), "productivebees:birch_wood_nest", 1, List.of(), "birch_wood_nest");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:grass_block"), "productivebees:bumble_bee_nest", 1, List.of(), "bumble_bee_nest");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:cherry_log"), "productivebees:cherry_wood_nest", 1, List.of(), "cherry_wood_nest");
        nestShapeless(List.of("productivebees:cherry_wood_nest"), "productivebees:cherry_wood_nest", 1, List.of(), "cherry_wood_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:coarse_dirt"), "productivebees:coarse_dirt_nest", 1, List.of(), "coarse_dirt_nest");
        nestShapeless(List.of("productivebees:coarse_dirt_nest"), "productivebees:coarse_dirt_nest", 1, List.of(), "coarse_dirt_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:dark_oak_log"), "productivebees:dark_oak_wood_nest", 1, List.of(), "dark_oak_wood_nest");
        nestShapeless(List.of("productivebees:dark_oak_wood_nest"), "productivebees:dark_oak_wood_nest", 1, List.of(), "dark_oak_wood_nest_clear");
        nestShapeless(List.of("minecraft:dragon_egg", "minecraft:beehive"), "productivebees:dragon_egg_hive", 1, List.of(), "dragon_egg_hive");
        nestShapeless(List.of("productivebees:dragon_egg_hive"), "productivebees:dragon_egg_hive", 1, List.of(), "dragon_egg_hive_clear");
        nestShapeless(List.of("minecraft:iron_sword", "minecraft:end_stone"), "productivebees:end_stone_nest", 1, List.of(), "end_stone_nest");
        nestShapeless(List.of("productivebees:end_stone_nest"), "productivebees:end_stone_nest", 1, List.of(), "end_stone_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:glowstone"), "productivebees:glowstone_nest", 1, List.of(), "glowstone_nest");
        nestShapeless(List.of("productivebees:glowstone_nest"), "productivebees:glowstone_nest", 1, List.of(), "glowstone_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:gravel"), "productivebees:gravel_nest", 1, List.of(), "gravel_nest");
        nestShapeless(List.of("productivebees:gravel_nest"), "productivebees:gravel_nest", 1, List.of(), "gravel_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:jungle_log"), "productivebees:jungle_wood_nest", 1, List.of(), "jungle_wood_nest");
        nestShapeless(List.of("productivebees:jungle_wood_nest"), "productivebees:jungle_wood_nest", 1, List.of(), "jungle_wood_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:mangrove_log"), "productivebees:mangrove_wood_nest", 1, List.of(), "mangrove_wood_nest");
        nestShapeless(List.of("productivebees:mangrove_wood_nest"), "productivebees:mangrove_wood_nest", 1, List.of(), "mangrove_wood_nest_clear");
        nestShapeless(List.of("minecraft:iron_sword", "minecraft:nether_bricks"), "productivebees:nether_brick_nest", 1, List.of(), "nether_brick_nest");
        nestShapeless(List.of("productivebees:nether_brick_nest"), "productivebees:nether_brick_nest", 1, List.of(), "nether_brick_nest_clear");
        nestShapeless(List.of("minecraft:diamond_sword", "minecraft:nether_gold_ore"), "productivebees:nether_gold_nest", 1, List.of(), "nether_gold_nest");
        nestShapeless(List.of("productivebees:nether_gold_nest"), "productivebees:nether_gold_nest", 1, List.of(), "nether_gold_nest_clear");
        nestShapeless(List.of("minecraft:iron_sword", "minecraft:nether_quartz_ore"), "productivebees:nether_quartz_nest", 1, List.of(), "nether_quartz_nest");
        nestShapeless(List.of("productivebees:nether_quartz_nest"), "productivebees:nether_quartz_nest", 1, List.of(), "nether_quartz_nest_clear");
        nestShapeless(List.of("minecraft:iron_sword", "productivebees:quartz_netherrack"), "productivebees:nether_quartz_nest", 1, List.of(), "nether_quartz_nest_quartz_netherrack");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:oak_log"), "productivebees:oak_wood_nest", 1, List.of(), "oak_wood_nest");
        nestShapeless(List.of("productivebees:oak_wood_nest"), "productivebees:oak_wood_nest", 1, List.of(), "oak_wood_nest_clear");
        nestShapeless(List.of("minecraft:diamond_sword", "minecraft:obsidian"), "productivebees:obsidian_nest", 1, List.of(), "obsidian_nest");
        nestShapeless(List.of("productivebees:obsidian_nest"), "productivebees:obsidian_nest", 1, List.of(), "obsidian_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:sand"), "productivebees:sand_nest", 1, List.of(), "sand_nest");
        nestShapeless(List.of("productivebees:sand_nest"), "productivebees:sand_nest", 1, List.of(), "sand_nest_clear");
        nestShapeless(List.of("minecraft:iron_sword", "minecraft:slime_block"), "productivebees:slimy_nest", 1, List.of(), "slimy_nest");
        nestShapeless(List.of("productivebees:slimy_nest"), "productivebees:slimy_nest", 1, List.of(), "slimy_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:snow_block"), "productivebees:snow_nest", 1, List.of(), "snow_nest");
        nestShapeless(List.of("productivebees:snow_nest"), "productivebees:snow_nest", 1, List.of(), "snow_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:soul_sand"), "productivebees:soul_sand_nest", 1, List.of(), "soul_sand_nest");
        nestShapeless(List.of("productivebees:soul_sand_nest"), "productivebees:soul_sand_nest", 1, List.of(), "soul_sand_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:spruce_log"), "productivebees:spruce_wood_nest", 1, List.of(), "spruce_wood_nest");
        nestShapeless(List.of("productivebees:spruce_wood_nest"), "productivebees:spruce_wood_nest", 1, List.of(), "spruce_wood_nest_clear");
        nestShapeless(List.of("minecraft:iron_sword", "minecraft:stone"), "productivebees:stone_nest", 1, List.of(), "stone_nest");
        nestShapeless(List.of("productivebees:stone_nest"), "productivebees:stone_nest", 1, List.of(), "stone_nest_clear");
        nestShapeless(List.of("minecraft:wooden_sword", "minecraft:sugar_cane"), "productivebees:sugar_cane_nest", 1, List.of(), "sugar_cane_nest");
        nestShapeless(List.of("productivebees:sugar_cane_nest"), "productivebees:sugar_cane_nest", 1, List.of(), "sugar_cane_nest_clear");// total: 70
    }

    private void combBlock(int count, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:configurable_comb_block");
        obj.addProperty("count", count);
        appendConditions(obj, conditions);
        entries.add(new Entry("comb_block/" + recipePath, obj));
    }

    private void configurableHoneycomb(int count, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:configurable_honeycomb");
        obj.addProperty("count", count);
        appendConditions(obj, conditions);
        entries.add(new Entry("comb_block/" + recipePath, obj));
    }

    private void combBlockShapeless(List<String> ingredients, String resultId, int count, List<JsonObject> conditions, String recipePath) {
        entries.add(new Entry("comb_block/" + recipePath, shapelessJson(ingredients, resultId, count, conditions)));
    }

    private void combBlockShaped(List<String> pattern, Map<String, String> key, String resultId, int count, List<JsonObject> conditions, String recipePath) {
        entries.add(new Entry("comb_block/" + recipePath, shapedJson(pattern, key, resultId, count, conditions)));
    }

    private void nestShapeless(List<String> ingredients, String resultId, int count, List<JsonObject> conditions, String recipePath) {
        entries.add(new Entry("nests/" + recipePath, shapelessJson(ingredients, resultId, count, conditions)));
    }

    private void nestShaped(List<String> pattern, Map<String, String> key, String resultId, int count, List<JsonObject> conditions, String recipePath) {
        entries.add(new Entry("nests/" + recipePath, shapedJson(pattern, key, resultId, count, conditions)));
    }

    private static JsonObject shapelessJson(List<String> ingredients, String resultId, int count, List<JsonObject> conditions) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shapeless");
        JsonArray arr = new JsonArray();
        for (String i : ingredients) arr.add(i);
        obj.add("ingredients", arr);
        obj.add("result", result(resultId, count));
        appendConditions(obj, conditions);
        return obj;
    }

    private static JsonObject shapedJson(List<String> pattern, Map<String, String> key, String resultId, int count, List<JsonObject> conditions) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "minecraft:crafting_shaped");
        JsonArray pat = new JsonArray();
        for (String p : pattern) pat.add(p);
        obj.add("pattern", pat);
        JsonObject keys = new JsonObject();
        for (Map.Entry<String, String> e : key.entrySet()) keys.addProperty(e.getKey(), e.getValue());
        obj.add("key", keys);
        obj.add("result", result(resultId, count));
        appendConditions(obj, conditions);
        return obj;
    }

    private void bottler(String fluidIngredient, int fluidAmount, String itemIngredient, String resultId, int resultCount, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:bottler");
        JsonObject fluid = new JsonObject();
        fluid.addProperty("ingredient", fluidIngredient);
        fluid.addProperty("amount", fluidAmount);
        obj.add("fluid", fluid);
        obj.addProperty("ingredient", itemIngredient);
        obj.add("result", result(resultId, resultCount));
        appendConditions(obj, conditions);
        entries.add(new Entry("bottler/" + recipePath, obj));
    }

    private void beeFishing(String beeFullId, Object biomes, float chance, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:bee_fishing");
        obj.addProperty("bee", beeFullId);
        if (biomes instanceof String tag) {
            obj.add("biomes", new JsonPrimitive(tag));
        } else if (biomes instanceof List<?> list) {
            JsonArray arr = new JsonArray();
            for (Object b : list) arr.add(b.toString());
            obj.add("biomes", arr);
        }
        if (chance != 1.0f) obj.addProperty("chance", chance);
        appendConditions(obj, conditions);
        entries.add(new Entry("bee_fishing/" + recipePath, obj));
    }

    private static JsonObject result(String id, int count) {
        JsonObject r = new JsonObject();
        r.addProperty("id", id);
        if (count != 1) r.addProperty("count", count);
        return r;
    }

    private static void appendConditions(JsonObject obj, List<JsonObject> conditions) {
        if (conditions.isEmpty()) return;
        JsonArray arr = new JsonArray();
        for (JsonObject c : conditions) arr.add(c);
        obj.add("neoforge:conditions", arr);
    }

    private static JsonObject beeExists(String fullBeeId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivebees:bee_exists");
        c.addProperty("bee", fullBeeId);
        return c;
    }

    private static JsonObject fluidTagEmpty(String tagId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivelib:fluid_tag_empty");
        c.addProperty("tag", tagId);
        return c;
    }

    private record Entry(String fullPath, JsonObject json) {}
}
