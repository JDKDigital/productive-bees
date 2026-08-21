package cy.jdkdigital.productivebees.datagen.recipe.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.tagEmpty;

/** Emits {@code productivebees:bee_breeding} JSON recipes directly. */
public class BeeBreedingRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public BeeBreedingRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_breeding/" + e.path))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Bee Breeding Recipes";
    }

    private void buildRecipes() {
        emit("productivebees:magmatic", "productivebees:fluids/water", "productivebees:breeze", 0.0f, List.of(beeExists("productivebees:magmatic"), beeExists("productivebees:fluids/water"), beeExists("productivebees:breeze")), "breeze_bee");
        emit("productivebees:magmatic", "productivebees:leafcutter_bee", "productivebees:coal", 0.0f, List.of(beeExists("productivebees:magmatic"), beeExists("productivebees:coal")), "coal_bee");
        emit("productivebees:farmer_bee", "productivebees:rancher_bee", "productivebees:cupid_bee", 0.0f, List.of(), "cupid_bee");
        emit("productivebees:gems/lapis", "productivebees:skeletal", "productivebees:dye_bee", 0.0f, List.of(beeExists("productivebees:gems/lapis"), beeExists("productivebees:skeletal")), "dye_bee");
        emit("productivebees:gems/emerald", "productivebees:gems/lapis", "productivebees:experience", 0.0f, List.of(beeExists("productivebees:gems/emerald"), beeExists("productivebees:gems/lapis"), beeExists("productivebees:experience")), "experience_bee");
        emit("productivebees:lumber_bee", "productivebees:rancher_bee", "productivebees:farmer_bee", 0.0f, List.of(), "farmer_bee");
        emit("productivebees:yellow_black_carpenter_bee", "productivebees:green_carpenter_bee", "productivebees:lumber_bee", 0.0f, List.of(), "lumber_bee");
        emit("productivebees:lava", "productivebees:fluids/water", "productivebees:obsidian", 0.0f, List.of(beeExists("productivebees:lava"), beeExists("productivebees:fluids/water"), beeExists("productivebees:obsidian")), "obsidian_bee");
        emit("productivebees:sugarbag", "productivebees:neon_cuckoo_bee", "productivebees:pepto_bismol", 0.0f, List.of(beeExists("productivebees:pepto_bismol"), beeExists("productivebees:sugarbag")), "pepto_bismol_bee");
        emit("productivebees:chocolate_mining_bee", "productivebees:digger_bee", "productivebees:quarry_bee", 0.0f, List.of(), "quarry_bee");
        emit("productivebees:lumber_bee", "productivebees:sweat_bee", "productivebees:rancher_bee", 0.0f, List.of(), "rancher_bee");
        emit("productivebees:reed_bee", "productivebees:resin_bee", "productivebees:silky", 0.0f, List.of(beeExists("productivebees:silky")), "silky_bee");
        emit("productivebees:raw_materials/copper", "productivebees:raw_materials/zinc", "productivebees:alloys/brass", 0.0f, List.of(beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:raw_materials/zinc"), beeExists("productivebees:alloys/brass"), not(modLoaded("productivemetalworks"))), "alloys/brass_bee");
        emit("productivebees:raw_materials/copper", "productivebees:raw_materials/tin", "productivebees:alloys/bronze", 0.0f, List.of(beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:raw_materials/tin"), beeExists("productivebees:alloys/bronze"), not(modLoaded("productivemetalworks"))), "alloys/bronze_bee");
        emit("productivebees:raw_materials/copper", "productivebees:raw_materials/nickel", "productivebees:alloys/constantan", 0.0f, List.of(beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:raw_materials/nickel"), beeExists("productivebees:alloys/constantan"), not(modLoaded("productivemetalworks"))), "alloys/constantan_bee");
        emit("productivebees:raw_materials/gold", "productivebees:raw_materials/silver", "productivebees:alloys/electrum", 0.0f, List.of(beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:raw_materials/silver"), beeExists("productivebees:alloys/electrum"), not(modLoaded("productivemetalworks"))), "alloys/electrum_bee");
        emit("productivebees:raw_materials/lead", "productivebees:gems/diamond", "productivebees:alloys/enderium", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:alloys/enderium"), not(modLoaded("productivemetalworks"))), "alloys/enderium_bee");
        emit("productivebees:raw_materials/lead", "productivebees:raw_materials/platinum", "productivebees:alloys/enderium", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:raw_materials/platinum"), beeExists("productivebees:alloys/enderium"), not(modLoaded("productivemetalworks"))), "alloys/enderium_bee_platinum");
        emit("productivebees:raw_materials/iron", "productivebees:raw_materials/nickel", "productivebees:alloys/invar", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/nickel"), beeExists("productivebees:alloys/invar"), not(modLoaded("productivemetalworks"))), "alloys/invar_bee");
        emit("productivebees:raw_materials/silver", "productivebees:dusts/glowing", "productivebees:alloys/lumium", 0.0f, List.of(beeExists("productivebees:raw_materials/silver"), beeExists("productivebees:raw_materials/tin"), beeExists("productivebees:alloys/lumium"), not(modLoaded("productivemetalworks"))), "alloys/lumium_bee");
        emit("productivebees:alloys/steel", "productivebees:alloys/bronze", "productivebees:raw_materials/mithril", 0.0f, List.of(beeExists("productivebees:alloys/steel"), beeExists("productivebees:alloys/bronze"), beeExists("productivebees:raw_materials/mithril")), "alloys/mithril_bee");
        emit("productivebees:raw_materials/silver", "productivebees:raw_materials/copper", "productivebees:alloys/signalum", 0.0f, List.of(beeExists("productivebees:raw_materials/silver"), beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:alloys/signalum"), not(modLoaded("productivemetalworks"))), "alloys/signalum_bee");
        emit("productivebees:raw_materials/iron", "productivebees:coal", "productivebees:alloys/steel", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:coal"), beeExists("productivebees:alloys/steel"), not(modLoaded("productivemetalworks"))), "alloys/steel_bee");
        emit("productivebees:nomad_bee", "productivebees:reed_bee", "productivebees:ae2/silicon", 0.0f, List.of(beeExists("productivebees:ae2/silicon")), "appliedenergistics2/silicon_bee");
        emit("productivebees:gems/crystalline", "productivebees:ender", "productivebees:ae2/spatial", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:ender"), beeExists("productivebees:ae2/spatial")), "appliedenergistics2/spatial_bee");
        emit("productivebees:raw_materials/netherite", "productivebees:withered", "productivebees:atm/allthemodium", 0.0f, List.of(beeExists("productivebees:raw_materials/netherite"), beeExists("productivebees:withered"), beeExists("productivebees:atm/allthemodium"), not(modLoaded("productivemetalworks"))), "atm/allthemodium_bee");
        emit("productivebees:atm/allthemodium", "productivebees:atm/vibranium", "productivebees:atm/unobtainium", 0.0f, List.of(beeExists("productivebees:atm/allthemodium"), beeExists("productivebees:atm/vibranium"), beeExists("productivebees:atm/unobtainium"), not(modLoaded("productivemetalworks"))), "atm/unobtainium_bee");
        emit("productivebees:raw_materials/netherite", "productivebees:draconic", "productivebees:atm/vibranium", 0.0f, List.of(beeExists("productivebees:raw_materials/netherite"), beeExists("productivebees:draconic"), beeExists("productivebees:atm/vibranium"), not(modLoaded("productivemetalworks"))), "atm/vibranium_bee");
        emit("productivebees:gems/diamond", "productivebees:byg/emeraldite", "productivebees:byg/pendorite", 0.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:byg/emeraldite"), beeExists("productivebees:byg/pendorite")), "byg/pendorite_bee");
        emit("productivebees:raw_materials/lead", "productivebees:raw_materials/tin", "productivebees:chemlib/antimony", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:raw_materials/tin"), beeExists("productivebees:chemlib/antimony")), "chemlib/antimony_bee");
        emit("productivebees:chemlib/chlorine", "productivebees:chemlib/potassium", "productivebees:modern_industrialization/beryllium", 0.0f, List.of(beeExists("productivebees:chemlib/chlorine"), beeExists("productivebees:chemlib/potassium"), beeExists("productivebees:modern_industrialization/beryllium"), not(modLoaded("modern_industrialization"))), "chemlib/beryllium_bee");
        emit("productivebees:fluids/water", "productivebees:coal", "productivebees:chemlib/boron", 0.0f, List.of(beeExists("productivebees:fluids/water"), beeExists("productivebees:coal"), beeExists("productivebees:chemlib/boron")), "chemlib/boron_bee");
        emit("productivebees:raw_materials/zinc", "productivebees:prismarine", "productivebees:chemlib/cadmium", 0.0f, List.of(beeExists("productivebees:raw_materials/zinc"), beeExists("productivebees:prismarine"), beeExists("productivebees:chemlib/cadmium")), "chemlib/cadmium_bee");
        emit("productivebees:chemlib/thorium", "productivebees:chemlib/oxygen", "productivebees:chemlib/cerium", 0.0f, List.of(beeExists("productivebees:chemlib/thorium"), beeExists("productivebees:chemlib/oxygen"), beeExists("productivebees:chemlib/cerium")), "chemlib/cerium_bee");
        emit("productivebees:raw_materials/iron", "productivebees:fluids/water", "productivebees:chemlib/cesium", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:fluids/water"), beeExists("productivebees:chemlib/cesium")), "chemlib/cesium_bee");
        emit("productivebees:fluids/water", "productivebees:chemlib/oxygen", "productivebees:chemlib/chlorine", 0.0f, List.of(beeExists("productivebees:fluids/water"), beeExists("productivebees:chemlib/oxygen"), beeExists("productivebees:chemlib/chlorine")), "chemlib/chlorine_bee");
        emit("productivebees:raw_materials/lead", "productivebees:raw_materials/iron", "productivebees:modern_industrialization/chromium", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:modern_industrialization/chromium"), not(modLoaded("modern_industrialization"))), "chemlib/chromium_bee");
        emit("productivebees:chemlib/erbium", "productivebees:chemlib/holmium", "productivebees:chemlib/dysprosium", 0.0f, List.of(beeExists("productivebees:chemlib/erbium"), beeExists("productivebees:chemlib/holmium"), beeExists("productivebees:chemlib/dysprosium")), "chemlib/dysprosium_bee");
        emit("productivebees:chemlib/samarium", "productivebees:chemlib/gadolinium", "productivebees:chemlib/europium", 0.0f, List.of(beeExists("productivebees:chemlib/samarium"), beeExists("productivebees:chemlib/gadolinium"), beeExists("productivebees:chemlib/europium")), "chemlib/europium_bee");
        emit("productivebees:raw_materials/iron", "productivebees:tconstruct/cobalt", "productivebees:chemlib/gadolinium", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:tconstruct/cobalt"), beeExists("productivebees:chemlib/gadolinium")), "chemlib/gadolinium_bee");
        emit("productivebees:raw_materials/zinc", "productivebees:chemlib/germanium", "productivebees:chemlib/gallium", 0.0f, List.of(beeExists("productivebees:raw_materials/zinc"), beeExists("productivebees:chemlib/germanium"), beeExists("productivebees:chemlib/gallium")), "chemlib/gallium_bee");
        emit("productivebees:raw_materials/copper", "productivebees:raw_materials/zinc", "productivebees:chemlib/germanium", 0.0f, List.of(beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:raw_materials/zinc"), beeExists("productivebees:chemlib/germanium")), "chemlib/germanium_bee");
        emit("productivebees:chemlib/erbium", "productivebees:chemlib/oxygen", "productivebees:chemlib/holmium", 0.0f, List.of(beeExists("productivebees:chemlib/erbium"), beeExists("productivebees:chemlib/oxygen"), beeExists("productivebees:chemlib/holmium")), "chemlib/holmium_bee");
        emit("productivebees:chemlib/cerium", "productivebees:dusts/sulfur", "productivebees:chemlib/lanthanum", 0.0f, List.of(beeExists("productivebees:chemlib/cerium"), beeExists("productivebees:dusts/sulfur"), beeExists("productivebees:chemlib/lanthanum")), "chemlib/lanthanum_bee");
        emit("productivebees:skeletal", "productivebees:raw_materials/iron", "productivebees:chemlib/magnesium", 0.0f, List.of(beeExists("productivebees:skeletal"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:chemlib/magnesium")), "chemlib/magnesium_bee");
        emit("productivebees:skeletal", "productivebees:sugarbag", "productivebees:modern_industrialization/manganese", 0.0f, List.of(beeExists("productivebees:skeletal"), beeExists("productivebees:sugarbag"), beeExists("productivebees:modern_industrialization/manganese"), not(modLoaded("modern_industrialization"))), "chemlib/manganese_bee");
        emit("productivebees:coal", "productivebees:chemlib/oxygen", "productivebees:chemlib/molybdenum", 0.0f, List.of(beeExists("productivebees:coal"), beeExists("productivebees:chemlib/oxygen"), beeExists("productivebees:chemlib/molybdenum"), not(modLoaded("gtceu"))), "chemlib/molybdenum_bee");
        emit("productivebees:raw_materials/iron", "productivebees:modern_industrialization/manganese", "productivebees:chemlib/niobium", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:modern_industrialization/manganese"), beeExists("productivebees:chemlib/niobium")), "chemlib/niobium_bee");
        emit("productivebees:raw_materials/copper", "productivebees:raw_materials/nickel", "productivebees:chemlib/palladium", 0.0f, List.of(beeExists("productivebees:raw_materials/copper"), beeExists("productivebees:raw_materials/nickel"), beeExists("productivebees:chemlib/palladium"), tagEmpty("c:raw_materials/palladium")), "chemlib/palladium_bee");
        emit("productivebees:chemlib/thorium", "productivebees:raw_materials/radioactive", "productivebees:chemlib/polonium", 0.0f, List.of(beeExists("productivebees:chemlib/thorium"), beeExists("productivebees:raw_materials/radioactive"), beeExists("productivebees:chemlib/polonium")), "chemlib/polonium_bee");
        emit("productivebees:chemlib/samarium", "productivebees:chemlib/neodymium", "productivebees:chemlib/praseodymium", 0.0f, List.of(beeExists("productivebees:chemlib/samarium"), beeExists("productivebees:chemlib/neodymium"), beeExists("productivebees:chemlib/praseodymium")), "chemlib/praseodymium_bee");
        emit("productivebees:chemlib/neodymium", "productivebees:chemlib/praseodymium", "productivebees:chemlib/promethium", 0.0f, List.of(beeExists("productivebees:chemlib/neodymium"), beeExists("productivebees:chemlib/praseodymium"), beeExists("productivebees:chemlib/promethium")), "chemlib/promethium_bee");
        emit("productivebees:chemlib/lithium", "productivebees:chemlib/potassium", "productivebees:chemlib/rubidium", 0.0f, List.of(beeExists("productivebees:chemlib/lithium"), beeExists("productivebees:chemlib/potassium"), beeExists("productivebees:chemlib/rubidium")), "chemlib/rubidium_bee");
        emit("productivebees:tconstruct/cobalt", "productivebees:chemlib/lanthanum", "productivebees:chemlib/samarium", 0.0f, List.of(beeExists("productivebees:tconstruct/cobalt"), beeExists("productivebees:chemlib/lanthanum"), beeExists("productivebees:chemlib/samarium")), "chemlib/samarium_bee");
        emit("productivebees:chemlib/yttrium", "productivebees:chemlib/silicium", "productivebees:chemlib/scandium", 0.0f, List.of(beeExists("productivebees:chemlib/yttrium"), beeExists("productivebees:chemlib/silicium"), beeExists("productivebees:chemlib/scandium")), "chemlib/scandium_bee");
        emit("productivebees:chemlib/fluorine", "productivebees:chemlib/phosphorus", "productivebees:chemlib/silicium", 0.0f, List.of(beeExists("productivebees:chemlib/fluorine"), beeExists("productivebees:chemlib/phosphorus"), beeExists("productivebees:chemlib/silicium")), "chemlib/silicium_bee");
        emit("productivebees:fluids/water", "productivebees:chemlib/phosphorus", "productivebees:chemlib/sodium", 0.0f, List.of(beeExists("productivebees:fluids/water"), beeExists("productivebees:chemlib/phosphorus"), beeExists("productivebees:chemlib/sodium")), "chemlib/sodium_bee");
        emit("productivebees:raw_materials/iron", "productivebees:modern_industrialization/manganese", "productivebees:chemlib/tantalum", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:modern_industrialization/manganese"), beeExists("productivebees:chemlib/tantalum")), "chemlib/tantalum_bee");
        emit("productivebees:dusts/sulfur", "productivebees:raw_materials/lead", "productivebees:chemlib/thallium", 0.0f, List.of(beeExists("productivebees:dusts/sulfur"), beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:chemlib/thallium")), "chemlib/thallium_bee");
        emit("productivebees:digger_bee", "productivebees:raw_materials/radioactive", "productivebees:chemlib/thorium", 0.0f, List.of(beeExists("productivebees:raw_materials/radioactive"), beeExists("productivebees:chemlib/thorium")), "chemlib/thorium_bee");
        emit("productivebees:raw_materials/iron", "productivebees:raw_materials/titanium", "productivebees:chemlib/vanadium", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/titanium"), beeExists("productivebees:chemlib/vanadium")), "chemlib/vanadium_bee");
        emit("productivebees:gems/diamond", "productivebees:raw_materials/iron", "productivebees:chemlib/zirconium", 0.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:chemlib/zirconium")), "chemlib/zirconium_bee");
        emit("productivebees:magmatic", "productivebees:nomad_bee", "productivebees:dusts/blazing", 0.0f, List.of(beeExists("productivebees:magmatic"), beeExists("productivebees:dusts/blazing")), "dusts/blazing_bee");
        emit("productivebees:creeper_bee", "productivebees:coal", "productivebees:dusts/niter", 0.0f, List.of(beeExists("productivebees:coal"), beeExists("productivebees:dusts/niter")), "dusts/niter_bee");
        emit("productivebees:dusts/glowing", "productivebees:chocolate_mining_bee", "productivebees:dusts/redstone", 0.0f, List.of(beeExists("productivebees:dusts/glowing"), beeExists("productivebees:dusts/redstone")), "dusts/redstone_bee");
        emit("productivebees:magmatic", "productivebees:coal", "productivebees:dusts/sulfur", 0.0f, List.of(beeExists("productivebees:magmatic"), beeExists("productivebees:coal"), beeExists("productivebees:dusts/sulfur")), "dusts/sulfur_bee");
        emit("productivebees:eidolon/soul_shard", "productivebees:raw_materials/gold", "productivebees:eidolon/arcane_gold", 0.0f, List.of(beeExists("productivebees:eidolon/soul_shard"), beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:eidolon/arcane_gold")), "eidolon/arcane_gold_bee");
        emit("productivebees:raw_materials/lead", "productivebees:raw_materials/iron", "productivebees:eidolon/pewter", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:eidolon/pewter")), "eidolon/pewter_bee");
        emit("productivebees:ender", "productivebees:draconic", "productivebees:enigmaticlegacyplus/etherium_ore", 0.0f, List.of(beeExists("productivebees:ender"), beeExists("productivebees:draconic"), beeExists("productivebees:enigmaticlegacyplus/etherium_ore")), "enigmaticlegacy/etherium_ore_bee");
        emit("productivebees:eternal_starlight/amaramber", "productivebees:raw_materials/iron", "productivebees:eternal_starlight/deepsilver", 0.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:eternal_starlight/amaramber"), beeExists("productivebees:eternal_starlight/deepsilver")), "eternal_starlight/deepsilver_bee");
        emit("productivebees:eternal_starlight/amaramber", "productivebees:lava", "productivebees:eternal_starlight/thermal", 0.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:lava"), beeExists("productivebees:eternal_starlight/amaramber"), beeExists("productivebees:eternal_starlight/thermal")), "eternal_starlight/thermal_bee");
        emit("productivebees:gems/crystalline", "productivebees:resin_bee", "productivebees:gems/amber_gem", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:gems/amber_gem")), "gems/amber_gem_bee");
        emit("productivebees:dusts/redstone", "productivebees:sweat_bee", "productivebees:gems/cinnabar", 0.0f, List.of(beeExists("productivebees:dusts/redstone"), beeExists("productivebees:gems/cinnabar")), "gems/cinnabar_bee");
        emit("productivebees:ender", "productivebees:gems/lapis", "productivebees:gems/diamond", 0.0f, List.of(beeExists("productivebees:ender"), beeExists("productivebees:gems/lapis"), beeExists("productivebees:gems/diamond")), "gems/diamond_bee");
        emit("productivebees:gems/diamond", "productivebees:slimy", "productivebees:gems/emerald", 0.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:slimy"), beeExists("productivebees:gems/emerald")), "gems/emerald_bee");
        emit("productivebees:dusts/redstone", "productivebees:blue_banded_bee", "productivebees:gems/lapis", 0.0f, List.of(beeExists("productivebees:dusts/redstone"), beeExists("productivebees:gems/lapis")), "gems/lapis_bee");
        emit("productivebees:gobber/nether_gobber", "productivebees:ender", "productivebees:gobber/end_gobber", 0.0f, List.of(beeExists("productivebees:gobber/nether_gobber"), beeExists("productivebees:ender"), beeExists("productivebees:gobber/end_gobber")), "gobber/end_gobber_bee");
        emit("productivebees:gems/diamond", "productivebees:raw_materials/gold", "productivebees:gobber/gobber", 0.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:gobber/gobber")), "gobber/gobber_bee");
        emit("productivebees:gobber/gobber", "productivebees:raw_materials/netherite", "productivebees:gobber/nether_gobber", 0.0f, List.of(beeExists("productivebees:gobber/gobber"), beeExists("productivebees:raw_materials/netherite"), beeExists("productivebees:gobber/nether_gobber")), "gobber/nether_gobber_bee");
        emit("productivebees:gems/crystalline", "productivebees:neon_cuckoo_bee", "productivebees:integrateddynamics/menril", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:integrateddynamics/menril")), "integrateddynamics/menril_bee");
        emit("productivebees:raw_materials/iron", "productivebees:irons_spellbooks/arcane_essence", "productivebees:raw_materials/mithril", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:irons_spellbooks/arcane_essence"), beeExists("productivebees:raw_materials/mithril")), "irons_spellbooks/mithril_bee");
        emit("productivebees:resin_bee", "productivebees:lumber_bee", "productivebees:materials/plastic", 0.0f, List.of(beeExists("productivebees:materials/plastic")), "materials/plastic_bee");
        emit("productivebees:raw_materials/osmium", "productivebees:dusts/glowing", "productivebees:mekanism/refined_glowstone", 0.0f, List.of(beeExists("productivebees:raw_materials/osmium"), beeExists("productivebees:dusts/glowing"), beeExists("productivebees:mekanism/refined_glowstone")), "mekanism/refined_glowstone_bee");
        emit("productivebees:raw_materials/osmium", "productivebees:obsidian", "productivebees:mekanism/refined_obsidian", 0.0f, List.of(beeExists("productivebees:raw_materials/osmium"), beeExists("productivebees:obsidian"), beeExists("productivebees:mekanism/refined_obsidian")), "mekanism/refined_obsidian_bee");
        emit("productivebees:raw_materials/lead", "productivebees:raw_materials/silver", "productivebees:chemlib/antimony", 0.0f, List.of(beeExists("productivebees:raw_materials/lead"), beeExists("productivebees:raw_materials/silver"), beeExists("productivebees:chemlib/antimony")), "modern_industrialization/antimony_bee");
        emit("productivebees:creeper_bee", "productivebees:raw_materials/iron", "productivebees:powah/uraninite", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:powah/uraninite")), "powah/uraninite_bee");
        emit("productivebees:gems/crystalline", "productivebees:ashy_mining_bee", "productivebees:raw_materials/aluminum", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/aluminum")), "raw_materials/aluminium_bee");
        emit("productivebees:raw_materials/gold", "productivebees:gems/crystalline", "productivebees:raw_materials/bismuth", 0.0f, List.of(beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/bismuth")), "raw_materials/bismuth_bee");
        emit("productivebees:gems/crystalline", "productivebees:ashy_mining_bee", "productivebees:raw_materials/copper", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/copper")), "raw_materials/copper_bee");
        emit("productivebees:gems/crystalline", "productivebees:mason_bee", "productivebees:raw_materials/gold", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/gold")), "raw_materials/gold_bee");
        emit("productivebees:gems/crystalline", "productivebees:neon_cuckoo_bee", "productivebees:raw_materials/iridium", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/iridium"), not(modLoaded("ftbic"))), "raw_materials/iridium_bee");
        emit("productivebees:gems/crystalline", "productivebees:ashy_mining_bee", "productivebees:raw_materials/iron", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/iron")), "raw_materials/iron_bee");
        emit("productivebees:raw_materials/iron", "productivebees:blue_banded_bee", "productivebees:raw_materials/lead", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/lead")), "raw_materials/lead_bee");
        emit("productivebees:raw_materials/iron", "productivebees:sweat_bee", "productivebees:raw_materials/nickel", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/nickel")), "raw_materials/nickel_bee");
        emit("productivebees:raw_materials/iron", "productivebees:neon_cuckoo_bee", "productivebees:raw_materials/osmium", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/osmium")), "raw_materials/osmium_bee");
        emit("productivebees:ender", "productivebees:raw_materials/gold", "productivebees:raw_materials/platinum", 0.0f, List.of(beeExists("productivebees:ender"), beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:raw_materials/platinum")), "raw_materials/platinum_bee");
        emit("productivebees:creeper_bee", "productivebees:raw_materials/iron", "productivebees:raw_materials/radioactive", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/radioactive")), "raw_materials/radioactive_bee");
        emit("productivebees:raw_materials/iron", "productivebees:mason_bee", "productivebees:raw_materials/silver", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/silver")), "raw_materials/silver_bee");
        emit("productivebees:gems/crystalline", "productivebees:ashy_mining_bee", "productivebees:raw_materials/tin", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/tin")), "raw_materials/tin_bee");
        emit("productivebees:gems/diamond", "productivebees:raw_materials/iron", "productivebees:raw_materials/titanium", 0.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/titanium")), "raw_materials/titanium_bee");
        emit("productivebees:ender", "productivebees:coal", "productivebees:raw_materials/tungsten", 0.0f, List.of(beeExists("productivebees:ender"), beeExists("productivebees:coal"), beeExists("productivebees:raw_materials/tungsten")), "raw_materials/tungsten_bee");
        emit("productivebees:raw_materials/iron", "productivebees:sweat_bee", "productivebees:raw_materials/zinc", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:raw_materials/zinc")), "raw_materials/zinc_bee");
        emit("productivebees:gems/crystalline", "productivebees:raw_materials/iron", "productivebees:refinedstorage/quartz_enriched_iron", 0.0f, List.of(beeExists("productivebees:gems/crystalline"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:refinedstorage/quartz_enriched_iron")), "refinedstorage/quartz_enriched_iron_bee");
        emit("productivebees:silentgear/crimson_iron", "productivebees:ender", "productivebees:silentgear/azure_silver", 0.0f, List.of(beeExists("productivebees:silentgear/crimson_iron"), beeExists("productivebees:ender"), beeExists("productivebees:silentgear/azure_silver")), "silentgear/azure_silver_bee");
        emit("productivebees:raw_materials/iron", "productivebees:dusts/blazing", "productivebees:silentgear/crimson_iron", 0.0f, List.of(beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:dusts/blazing"), beeExists("productivebees:silentgear/crimson_iron")), "silentgear/crimson_iron_bee");
        emit("productivebees:prismarine", "productivebees:raw_materials/nickel", "productivebees:thermalendergy/prismalium", 0.0f, List.of(beeExists("productivebees:prismarine"), beeExists("productivebees:raw_materials/nickel"), beeExists("productivebees:thermalendergy/prismalium")), "thermalendergy/prismalium_bee");
        emit("productivebees:ghostly", "productivebees:skeletal", "productivebees:tombstone/grave", 0.0f, List.of(beeExists("productivebees:ghostly"), beeExists("productivebees:skeletal"), beeExists("productivebees:tombstone/grave")), "tombstone/grave_bee_skeletal");
        emit("productivebees:ghostly", "productivebees:zombie", "productivebees:tombstone/grave", 0.0f, List.of(beeExists("productivebees:ghostly"), beeExists("productivebees:zombie"), beeExists("productivebees:tombstone/grave")), "tombstone/grave_bee_zombie");
        emit("productivebees:undergarden/cloggrum", "productivebees:frosty", "productivebees:undergarden/froststeel", 0.0f, List.of(beeExists("productivebees:frosty"), beeExists("productivebees:undergarden/cloggrum"), beeExists("productivebees:undergarden/froststeel")), "undergarden/froststeel_bee");// total: 107 recipes
    }

    private void emit(String parent1, String parent2, String offspring, float deathChance, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:bee_breeding");
        obj.addProperty("parent1", parent1);
        obj.addProperty("parent2", parent2);
        obj.addProperty("offspring", offspring);
        if (deathChance != 0.0f) obj.addProperty("parentDeathChance", deathChance);
        if (!conditions.isEmpty()) {
            JsonArray arr = new JsonArray();
            for (JsonObject c : conditions) arr.add(c);
            obj.add("neoforge:conditions", arr);
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
