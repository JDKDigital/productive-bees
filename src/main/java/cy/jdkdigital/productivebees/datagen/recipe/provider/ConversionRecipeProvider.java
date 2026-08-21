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

/** Emits {@code productivebees:block_conversion} and {@code productivebees:item_conversion} JSON recipes directly. */
public class ConversionRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public ConversionRecipeProvider(PackOutput output) {
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
        return "PB Block/Item Conversion Recipes";
    }

    private void buildRecipes() {
        blockConv(List.of("productivebees:fluids/water"), blockState("minecraft:air"), blockState("minecraft:water"), 1.0f, List.of(beeExists("productivebees:fluids/water")), "air_to_water");
        blockConv(List.of("productivebees:raw_materials/iron"), blockState("minecraft:chipped_anvil"), blockState("minecraft:anvil"), 0.7f, List.of(beeExists("productivebees:raw_materials/iron")), "anvil_repair");
        blockConv(List.of("productivebees:raw_materials/iron"), blockState("minecraft:damaged_anvil"), blockState("minecraft:chipped_anvil"), 0.7f, List.of(beeExists("productivebees:raw_materials/iron")), "anvil_repair_chipper");
        blockConv(List.of("productivebees:raw_materials/gold"), blockState("minecraft:blackstone"), blockState("minecraft:gilded_blackstone"), 1.0f, List.of(beeExists("productivebees:raw_materials/gold")), "blackstone_to_gilded_blackstone");
        blockConv(List.of("productivebees:magmatic"), blockState("minecraft:cobblestone"), blockState("minecraft:lava"), 0.3f, List.of(beeExists("productivebees:magmatic")), "cobble_to_lava");
        blockConv(List.of("productivebees:sussy"), blockState("minecraft:gravel"), blockState("minecraft:suspicious_gravel"), 0.5f, List.of(beeExists("productivebees:sussy")), "gravel_to_sussy_gravel");
        blockConv(List.of("productivebees:draconic"), blockState("minecraft:obsidian"), blockState("minecraft:crying_obsidian"), 0.1f, List.of(beeExists("productivebees:draconic")), "obsidian_to_crying_obsidian");
        blockConv(List.of("productivebees:amber"), blockState("minecraft:honey_block"), blockState("productivebees:petrified_honey"), 1.0f, List.of(beeExists("productivebees:amber")), "petrified_honey");
        blockConv(List.of("productivebees:ghostly"), blockState("minecraft:sand"), blockState("minecraft:soul_sand"), 0.7f, List.of(beeExists("productivebees:ghostly")), "sand_to_soulsand");
        blockConv(List.of("productivebees:sussy"), blockState("minecraft:sand"), blockState("minecraft:suspicious_sand"), 0.5f, List.of(beeExists("productivebees:sussy")), "sand_to_sussy_sand");
        blockConv(List.of("productivebees:sponge"), blockState("minecraft:water"), blockState("minecraft:air"), 1.0f, List.of(beeExists("productivebees:sponge")), "water_to_air");
        blockConv(List.of("productivebees:frosty"), blockState("minecraft:water"), blockState("minecraft:ice"), 1.0f, List.of(beeExists("productivebees:frosty")), "water_to_ice");
        blockConv(List.of("productivebees:astralsorcery/rock_crystal"), blockState("minecraft:stone"), blockState("astralsorcery:rock_crystal_ore"), 0.15f, List.of(beeExists("productivebees:astralsorcery/rock_crystal")), "astralsorcery/stone_to_rock_crystal_ore");
        blockConv(List.of("productivebees:sussy"), blockState("minecraft:clay"), blockState("allthemodium:suspicious_clay"), 0.2f, List.of(modLoaded("allthemodium"), beeExists("productivebees:sussy")), "atm/clay_to_sussy_clay");
        blockConv(List.of("productivebees:sussy"), blockState("minecraft:soul_sand"), blockState("allthemodium:suspicious_soul_sand"), 0.1f, List.of(modLoaded("allthemodium"), beeExists("productivebees:sussy")), "atm/soul_sand_to_sussy_soul_sand");
        blockConv(List.of("productivebees:botania/pure"), blockState("botania:blaze_block"), blockState("minecraft:obsidian"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/blaze_block_to_obsidian");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:end_stone"), blockState("minecraft:cobbled_deepslate"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/end_stone_to_cobbled_deepslate");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:ice"), blockState("minecraft:packed_ice"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/ice_to_packed_ice");
        blockConvInput(List.of("productivebees:botania/pure"), "#minecraft:logs", blockState("botania:livingwood_log"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/log_to_livingwood");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:netherrack"), blockState("minecraft:cobblestone"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/netherrack_to_cobblestone");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:packed_ice"), blockState("minecraft:blue_ice"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/packed_ice_to_blue_ice");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:soul_sand"), blockState("minecraft:sand"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/soul_sand_to_sand");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:stone"), blockState("botania:livingrock"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/stone_to_livingrock");
        blockConv(List.of("productivebees:botania/pure"), blockState("minecraft:water"), blockState("minecraft:snow_block"), 1.0f, List.of(modLoaded("botania"), beeExists("productivebees:botania/pure")), "botania/water_to_snow_block");
        blockConv(List.of("productivebees:sussy"), blockState("lootr:suspicious_gravel", "dusted", "0"), blockState("minecraft:suspicious_gravel"), 0.5f, List.of(beeExists("productivebees:sussy"), modLoaded("lootr")), "lootr/sussy_gravel_to_sussy_gravel");
        blockConv(List.of("productivebees:sussy"), blockState("lootr:suspicious_sand", "dusted", "0"), blockState("minecraft:suspicious_sand"), 0.5f, List.of(beeExists("productivebees:sussy"), modLoaded("lootr")), "lootr/sussy_sand_to_sussy_sand");
        blockConv(List.of("productivebees:special/phil"), blockState("minecraft:stone"), blockState("minecraft:white_concrete"), 1.0f, List.of(beeExists("productivebees:special/phil")), "special/stone_to_white_concrete");
        blockConv(List.of("productivebees:tetra/geode"), blockState("minecraft:deepslate"), blockState("tetra:block_geode"), 0.15f, List.of(beeExists("productivebees:tetra/geode")), "tetra/deepslate_to_geode");
        blockConv(List.of("productivebees:thermal/blizz"), blockState("minecraft:ice"), blockState("minecraft:blue_ice"), 0.8f, List.of(beeExists("productivebees:thermal/blizz")), "thermal/ice_to_blue_ice");

        itemConvSpawnEgg(List.of("productivebees:rancher_bee"), List.of(ing("ad_astra:cheese_block")), "productivebees:ad_astra/cheese", 1, 1.0f, List.of(modLoaded("ad_astra"), beeExists("productivebees:ad_astra/cheese")), "ad_astra/cheese_bee");
        itemConvSpawnEgg(List.of("productivebees:dusts/blazing"), List.of(spawnEggIng("productivebees:obsidian")), "productivebees:enderio/infinity", 1, 1.0f, List.of(beeExists("productivebees:enderio/infinity"), beeExists("productivebees:dusts/blazing"), beeExists("productivebees:obsidian")), "enderio/infinity_bee");
        itemConvSpawnEgg(List.of("productivebees:obsidian"), List.of(spawnEggIng("productivebees:dusts/redstone")), "productivebees:fluxnetworks/flux", 1, 1.0f, List.of(beeExists("productivebees:fluxnetworks/flux"), beeExists("productivebees:dusts/redstone"), beeExists("productivebees:obsidian")), "fluxnetworks/flux_bee");
        itemConvSpawnEgg(List.of("productivebees:raw_materials/radioactive"), List.of(spawnEggIng("productivebees:reactors/cyanite")), "productivebees:reactors/blutonium", 1, 1.0f, List.of(not(modLoaded("bigreactors")), beeExists("productivebees:reactors/cyanite"), beeExists("productivebees:reactors/blutonium")), "reactors/blutonium_bee");
        itemConvSpawnEgg(List.of("productivebees:reactors/ridiculite"), List.of(spawnEggIng("productivebees:reactors/ludicrite")), "productivebees:reactors/inanite", 1, 1.0f, List.of(not(modLoaded("bigreactors")), beeExists("productivebees:reactors/inanite")), "reactors/inanite_bee");
        itemConvSpawnEgg(List.of("productivebees:reactors/inanite"), List.of(spawnEggIng("productivebees:reactors/ridiculite")), "productivebees:reactors/insanite", 1, 1.0f, List.of(not(modLoaded("bigreactors")), beeExists("productivebees:reactors/insanite")), "reactors/insanite_bee");
        itemConvSpawnEgg(List.of("productivebees:reactors/cyanite"), List.of(spawnEggIng("productivebees:reactors/blutonium")), "productivebees:reactors/ludicrite", 1, 1.0f, List.of(not(modLoaded("bigreactors")), beeExists("productivebees:reactors/ludicrite")), "reactors/ludicrite_bee");
        itemConvSpawnEgg(List.of("productivebees:reactors/ludicrite"), List.of(spawnEggIng("productivebees:reactors/magentite")), "productivebees:reactors/ridiculite", 1, 1.0f, List.of(not(modLoaded("bigreactors")), beeExists("productivebees:reactors/ridiculite")), "reactors/ridiculite_bee");
    }

    private void blockConv(List<String> bees, JsonObject from, JsonObject to, float chance, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = baseBlockConv(bees, to, chance, conditions);
        obj.add("from", from);
        entries.add(new Entry("block_conversion/" + recipePath, obj));
    }

    private void blockConvInput(List<String> bees, String input, JsonObject to, float chance, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = baseBlockConv(bees, to, chance, conditions);
        obj.addProperty("input", input);
        entries.add(new Entry("block_conversion/" + recipePath, obj));
    }

    private JsonObject baseBlockConv(List<String> bees, JsonObject to, float chance, List<JsonObject> conditions) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:block_conversion");
        JsonArray bArr = new JsonArray();
        for (String b : bees) bArr.add(b);
        obj.add("bees", bArr);
        obj.add("to", to);
        if (chance != 1.0f) obj.addProperty("chance", chance);
        if (!conditions.isEmpty()) {
            JsonArray cArr = new JsonArray();
            for (JsonObject c : conditions) cArr.add(c);
            obj.add("neoforge:conditions", cArr);
        }
        return obj;
    }

    private void itemConvSpawnEgg(List<String> bees, List<JsonObject> ingredients, String resultBee, int count, float chance, List<JsonObject> conditions, String recipePath) {
        if (ingredients.size() != 1) {
            throw new IllegalArgumentException("itemConvSpawnEgg expects exactly one ingredient");
        }
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:item_conversion");
        JsonArray bArr = new JsonArray();
        for (String b : bees) bArr.add(b);
        obj.add("bees", bArr);
        obj.add("ingredients", ingredients.get(0));
        obj.add("result", spawnEggResult(resultBee, count));
        if (chance != 1.0f) obj.addProperty("chance", chance);
        if (!conditions.isEmpty()) {
            JsonArray cArr = new JsonArray();
            for (JsonObject c : conditions) cArr.add(c);
            obj.add("neoforge:conditions", cArr);
        }
        entries.add(new Entry("item_conversion/" + recipePath, obj));
    }

    private static JsonObject spawnEggResult(String beeFullId, int count) {
        JsonObject r = new JsonObject();
        r.addProperty("id", "productivebees:spawn_egg_configurable_bee");
        r.addProperty("count", count);
        JsonObject components = new JsonObject();
        JsonObject entityData = new JsonObject();
        entityData.addProperty("type", beeFullId);
        entityData.addProperty("id", "productivebees:configurable_bee");
        components.add("minecraft:entity_data", entityData);
        r.add("components", components);
        return r;
    }

    /** Plain item ingredient — emits as a JsonObject (e.g. `{ "item": "ad_astra:cheese_block" }`) wrapped as Ingredient. */
    private static JsonObject ing(String itemId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("item", itemId);
        return ing;
    }

    /** ComponentIngredient picking up a configurable_bee spawn egg of a specific bee. */
    private static JsonObject spawnEggIng(String beeFullId) {
        JsonObject ing = new JsonObject();
        ing.addProperty("neoforge:ingredient_type", "productivebees:component");
        JsonObject components = new JsonObject();
        JsonObject entityData = new JsonObject();
        entityData.addProperty("type", beeFullId);
        entityData.addProperty("id", "productivebees:configurable_bee");
        components.add("minecraft:entity_data", entityData);
        ing.add("components", components);
        ing.addProperty("items", "productivebees:spawn_egg_configurable_bee");
        return ing;
    }

    private static JsonObject blockState(String name, String... propPairs) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Name", name);
        if (propPairs.length > 0) {
            JsonObject props = new JsonObject();
            for (int i = 0; i < propPairs.length; i += 2) {
                props.add(propPairs[i], new JsonPrimitive(propPairs[i + 1]));
            }
            obj.add("Properties", props);
        }
        return obj;
    }

    private static JsonObject beeExists(String fullBeeId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivebees:bee_exists");
        c.addProperty("bee", fullBeeId);
        return c;
    }

    private record Entry(String fullPath, JsonObject json) {}
}
