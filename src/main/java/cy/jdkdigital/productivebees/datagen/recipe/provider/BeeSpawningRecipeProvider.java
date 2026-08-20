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
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.not;

/** Emits {@code productivebees:bee_spawning} JSON recipes directly. */
public class BeeSpawningRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public BeeSpawningRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_spawning/" + e.path))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Bee Spawning Recipes";
    }

    private void buildRecipes() {
        emit("productivebees:coarse_dirt_nest", List.of("productivebees:ashy_mining_bee"), "#c:is_overworld", null, List.of(), "ashy_mining_coarse_dirt_nest");
        emit("productivebees:gravel_nest", List.of("productivebees:ashy_mining_bee"), "#productivebees:is_beach_or_river", null, List.of(), "ashy_mining_gravel_nest");
        emit("productivebees:sand_nest", List.of("productivebees:ashy_mining_bee"), "#c:is_sandy", null, List.of(), "ashy_mining_sand_nest");
        emit("productivebees:acacia_wood_nest", List.of("productivebees:blue_banded_bee"), "#c:is_overworld", null, List.of(), "blue_banded_acacia_wood_nest");
        emit("productivebees:cherry_wood_nest", List.of("productivebees:blue_banded_bee"), "#c:is_overworld", null, List.of(), "blue_banded_cherry_wood_nest");
        emit("productivebees:dark_oak_wood_nest", List.of("productivebees:blue_banded_bee"), "#c:is_overworld", null, List.of(), "blue_banded_dark_oak_wood_nest");
        emit("productivebees:bumble_bee_nest", List.of("productivebees:bumble_bee"), "#c:is_plains", null, List.of(), "bumble_bee_bumble_bee_nest");
        emit("productivebees:coarse_dirt_nest", List.of("productivebees:chocolate_mining_bee"), "#c:is_overworld", null, List.of(), "chocolate_mining_coarse_dirt_nest");
        emit("productivebees:gravel_nest", List.of("productivebees:chocolate_mining_bee"), "#productivebees:is_beach_or_river", null, List.of(), "chocolate_mining_gravel_nest");
        emit("productivebees:sand_nest", List.of("productivebees:chocolate_mining_bee"), "#c:is_sandy", null, List.of(), "chocolate_mining_sand_nest");
        emit("productivebees:nether_quartz_nest", List.of("productivebees:gems/crystalline"), "#minecraft:is_nether", "#c:gems/quartz", List.of(beeExists("productivebees:gems/crystalline")), "crystalline_nether_quartz_nest");
        emit("productivebees:gravel_nest", List.of("productivebees:digger_bee"), "#productivebees:is_beach_or_river", null, List.of(), "digger_gravel_nest");
        emit("productivebees:stone_nest", List.of("productivebees:digger_bee"), "#c:is_overworld", null, List.of(), "digger_stone_nest");
        emit("productivebees:obsidian_nest", List.of("productivebees:draconic"), "#minecraft:is_end", "minecraft:dragon_breath", List.of(beeExists("productivebees:draconic")), "draconic_obsidian_nest");
        emit("productivebees:end_stone_nest", List.of("productivebees:ender"), "#minecraft:is_end", "minecraft:popped_chorus_fruit", List.of(beeExists("productivebees:ender")), "ender_end_stone_nest");
        emit("productivebees:soul_sand_nest", List.of("productivebees:ghostly"), "#minecraft:is_nether", "minecraft:ghast_tear", List.of(beeExists("productivebees:ghostly")), "ghostly_soul_sand_nest");
        emit("productivebees:glowstone_nest", List.of("productivebees:dusts/glowing"), "#minecraft:is_nether", "#c:dusts/glowstone", List.of(beeExists("productivebees:dusts/glowing")), "glowing_glowstone_nest");
        emit("productivebees:nether_gold_nest", List.of("productivebees:raw_materials/gold"), "#minecraft:is_nether", "#c:ingots/gold", List.of(beeExists("productivebees:raw_materials/gold")), "gold_nether_gold_nest");
        emit("productivebees:birch_wood_nest", List.of("productivebees:green_carpenter_bee"), "#c:is_overworld", null, List.of(), "green_carpenter_birch_wood_nest");
        emit("productivebees:dark_oak_wood_nest", List.of("productivebees:green_carpenter_bee"), "#c:is_overworld", null, List.of(), "green_carpenter_dark_oak_wood_nest");
        emit("productivebees:jungle_wood_nest", List.of("productivebees:green_carpenter_bee"), "#c:is_overworld", null, List.of(), "green_carpenter_jungle_wood_nest");
        emit("productivebees:oak_wood_nest", List.of("productivebees:green_carpenter_bee"), "#c:is_overworld", null, List.of(), "green_carpenter_oak_wood_nest");
        emit("productivebees:coarse_dirt_nest", List.of("productivebees:leafcutter_bee"), "#c:is_overworld", null, List.of(), "leafcutter_coarse_dirt_nest");
        emit("productivebees:nether_brick_nest", List.of("productivebees:magmatic"), "#minecraft:is_nether", "minecraft:magma_cream", List.of(beeExists("productivebees:magmatic")), "magmatic_nether_brick_nest");
        emit("productivebees:stone_nest", List.of("productivebees:mason_bee"), "#c:is_overworld", null, List.of(), "mason_stone_nest");
        emit("productivebees:sugar_cane_nest", List.of("productivebees:mason_bee"), "#c:is_overworld", null, List.of(), "mason_sugar_cane_nest");
        emit("productivebees:sugar_cane_nest", List.of("productivebees:reed_bee"), "#c:is_overworld", null, List.of(), "reed_sugar_cane_nest");
        emit("productivebees:spruce_wood_nest", List.of("productivebees:resin_bee"), "#c:is_overworld", null, List.of(), "resin_spruce_wood_nest");
        emit("productivebees:slimy_nest", List.of("productivebees:slimy"), "#c:is_swamp", null, List.of(beeExists("productivebees:slimy")), "slimy_slimy_nest");
        emit("productivebees:snow_nest", List.of("productivebees:sweat_bee"), "#c:is_snowy", null, List.of(), "sweat_snow_nest");
        emit("productivebees:birch_wood_nest", List.of("productivebees:yellow_black_carpenter_bee"), "#c:is_overworld", null, List.of(), "yellow_black_carpenter_birch_wood_nest");
        emit("productivebees:dark_oak_wood_nest", List.of("productivebees:yellow_black_carpenter_bee"), "#c:is_overworld", null, List.of(), "yellow_black_carpenter_dark_oak_wood_nest");
        emit("productivebees:mangrove_wood_nest", List.of("productivebees:green_carpenter_bee", "productivebees:yellow_black_carpenter_bee"), "#c:is_overworld", null, List.of(), "yellow_black_carpenter_mangrove_wood_nest");
        emit("productivebees:oak_wood_nest", List.of("productivebees:yellow_black_carpenter_bee"), "#c:is_overworld", null, List.of(), "yellow_black_carpenter_oak_wood_nest");
        emit("productivebees:spruce_wood_nest", List.of("productivebees:yellow_black_carpenter_bee"), "#c:is_overworld", null, List.of(), "yellow_black_carpenter_spruce_wood_nest");
    }

    private void emit(String ingredient, List<String> results, String biomes, String spawnItem, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:bee_spawning");
        obj.addProperty("ingredient", ingredient);
        JsonArray arr = new JsonArray();
        for (String r : results) arr.add(r);
        obj.add("results", arr);
        obj.add("biomes", new JsonPrimitive(biomes));
        if (spawnItem != null) obj.addProperty("spawn_item", spawnItem);
        if (!conditions.isEmpty()) {
            JsonArray carr = new JsonArray();
            for (JsonObject c : conditions) carr.add(c);
            obj.add("neoforge:conditions", carr);
        }
        entries.add(new Entry(recipePath, obj));
    }

    private static JsonObject beeExists(String fullBeeId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivebees:bee_exists");
        c.addProperty("bee", fullBeeId);
        return c;
    }

    private record Entry(String path, JsonObject json) {}
}
