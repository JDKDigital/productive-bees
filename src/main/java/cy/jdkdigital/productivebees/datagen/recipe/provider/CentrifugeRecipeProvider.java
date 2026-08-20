package cy.jdkdigital.productivebees.datagen.recipe.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.tagEmpty;
import static cy.jdkdigital.productivelib.datagen.recipe.ConditionsHelper.or;

/** Emits {@code productivebees:centrifuge} JSON recipes directly, bypassing Ingredient codec restrictions on unloaded cross-mod items. */
public class CentrifugeRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public CentrifugeRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge/" + e.path))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Centrifuge Recipes";
    }

    private void buildRecipes() {
        bee("breeze", List.of(item("minecraft:breeze_rod", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "comb_breeze");
        plain("minecraft:honeycomb", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("productivebees:honey", 100), List.of(), "honeycomb");
        bee("skeletal", List.of(item("minecraft:bone_meal", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_bone");
        bee("breeze", List.of(item("minecraft:wind_charge", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_breeze");
        bee("butcher", List.of(item("minecraft:beef", 1, 1, 0.2f), item("minecraft:porkchop", 1, 1, 0.2f), item("minecraft:chicken", 1, 1, 0.2f), item("minecraft:mutton", 1, 1, 0.2f)), fluid("productivebees:honey", 0), List.of(beeExists("butcher"), not(modLoaded("productivemetalworks"))), "honeycomb_butcher");
        bee("creaking", List.of(item("minecraft:resin_clump", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("creaking")), "honeycomb_creaking");
        bee("draconic", List.of(item("productivebees:draconic_dust", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("draconic")), "honeycomb_draconic");
        bee("ender", List.of(item("minecraft:ender_pearl", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(modLoaded("integrateddynamics"))), "honeycomb_ender");
        bee("experience", List.of(item("minecraft:experience_bottle", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), fluid("productivebees:honey", 0), List.of(fluidTagEmpty("c:experience")), "honeycomb_experience");
        bee("experience", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:experience", 100), List.of(not(fluidTagEmpty("c:experience"))), "honeycomb_experience_fluid");
        bee("coal", List.of(item("minecraft:coal", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_fossilised");
        bee("frosty", List.of(item("minecraft:snowball", 2, 1, 1.0f), item("minecraft:ice", 1, 1, 0.4f)), null, List.of(), "honeycomb_frosty");
        plain("productivebees:honeycomb_ghostly", List.of(item("minecraft:ghast_tear", 1, 1, 0.05f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_ghostly");
        bee("lava", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("minecraft:lava", 100), List.of(), "honeycomb_lava");
        bee("magmatic", List.of(item("minecraft:magma_cream", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_magmatic");
        bee("obsidian", List.of(item("productivebees:obsidian_shard", 3, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_obsidian");
        plain("productivebees:honeycomb_powdery", List.of(item("minecraft:gunpowder", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_powdery");
        bee("prismarine", List.of(item("minecraft:prismarine_shard", 1, 1, 0.2f), item("minecraft:prismarine_crystals", 1, 1, 0.05f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_prismarine");
        bee("zombie", List.of(item("minecraft:rotten_flesh", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "honeycomb_rotten");
        bee("sculk", List.of(item("minecraft:echo_shard", 1, 1, 0.3f), item("minecraft:disc_fragment_5", 1, 1, 0.005f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("sculk")), "honeycomb_sculk");
        bee("silky", List.of(item("minecraft:string", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("silky")), "honeycomb_silky");
        bee("slimy", List.of(item("minecraft:slime_ball", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("slimy")), "honeycomb_slimy");
        bee("withered", List.of(tag("c:fragments/wither_skull", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("withered")), "honeycomb_withered");
        bee("actuallyadditions/black_quartz", List.of(item("actuallyadditions:black_quartz", 1, 1, 1f)), null, List.of(beeExists("actuallyadditions/black_quartz"), modLoaded("actuallyadditions")), "actuallyadditions/honeycomb_black_quartz");
        bee("ad_astra/calorite", List.of(tag("c:raw_materials/calorite", 1, 1, 0.3f)), null, List.of(not(tagEmpty("c:raw_materials/calorite")), beeExists("ad_astra/calorite")), "ad_astra/honeycomb_calorite");
        bee("ad_astra/cheese", List.of(item("ad_astra:cheese", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("ad_astra"), beeExists("ad_astra/cheese")), "ad_astra/honeycomb_cheese");
        bee("ad_astra/desh", List.of(tag("c:raw_materials/desh", 1, 1, 0.3f)), null, List.of(not(tagEmpty("c:raw_materials/desh")), beeExists("ad_astra/desh")), "ad_astra/honeycomb_desh");
        bee("ad_astra/ostrum", List.of(tag("c:raw_materials/ostrum", 1, 1, 0.3f)), null, List.of(not(tagEmpty("c:raw_materials/ostrum")), beeExists("ad_astra/ostrum")), "ad_astra/honeycomb_ostrum");
        bee("ae2/entro", List.of(item("extendedae:entro_crystal", 1, 1, 0.5f)), null, List.of(beeExists("ae2/entro"), modLoaded("extendedae")), "ae2/honeycomb_entro");
        bee("ae2/fluix", List.of(item("ae2:fluix_crystal", 1, 1, 0.2f), item("ae2:fluix_dust", 1, 1, 0.05f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("ae2")), "ae2/honeycomb_fluix");
        bee("ae2/redstone_crystal", List.of(item("appflux:redstone_crystal", 1, 1, 0.7f), item("appflux:charged_redstone", 1, 1, 0.1f)), null, List.of(beeExists("ae2/redstone_crystal"), modLoaded("appflux")), "ae2/honeycomb_redstone_crystal");
        bee("ae2/sky_bronze", List.of(item("megacells:sky_bronze_ingot", 1, 1, 0.4f)), null, List.of(beeExists("ae2/sky_bronze"), modLoaded("megacells")), "ae2/honeycomb_sky_bronze");
        bee("ae2/sky_osmium", List.of(item("megacells:sky_osmium_ingot", 1, 1, 0.3f)), null, List.of(beeExists("ae2/sky_osmium"), modLoaded("megacells")), "ae2/honeycomb_sky_osmium");
        bee("ae2/sky_steel", List.of(item("megacells:sky_steel_ingot", 1, 1, 0.25f)), null, List.of(beeExists("ae2/sky_steel"), modLoaded("megacells")), "ae2/honeycomb_sky_steel");
        bee("ae2/spatial", List.of(item("ae2:certus_quartz_crystal", 1, 2, 1.0f), item("ae2:certus_quartz_dust", 1, 1, 0.5f)), null, List.of(modLoaded("ae2")), "ae2/honeycomb_spatial");
        bee("alloys/brass", List.of(tag("c:nuggets/brass", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/brass")), beeExists("alloys/brass")), "alloys/honeycomb_brass");
        bee("alloys/bronze", List.of(tag("c:nuggets/bronze", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/bronze")), beeExists("alloys/bronze")), "alloys/honeycomb_bronze");
        bee("alloys/constantan", List.of(tag("c:nuggets/constantan", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/constantan")), beeExists("alloys/constantan")), "alloys/honeycomb_constantan");
        bee("alloys/electrum", List.of(tag("c:nuggets/electrum", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/electrum")), beeExists("alloys/electrum")), "alloys/honeycomb_electrum");
        bee("alloys/enderium", List.of(tag("c:nuggets/enderium", 3, 5, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/enderium")), beeExists("alloys/enderium")), "alloys/honeycomb_enderium");
        bee("alloys/invar", List.of(tag("c:nuggets/invar", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/invar")), beeExists("alloys/invar")), "alloys/honeycomb_invar");
        bee("alloys/lumium", List.of(tag("c:nuggets/lumium", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/lumium")), beeExists("alloys/lumium")), "alloys/honeycomb_lumium");
        bee("raw_materials/mithril", List.of(tag("c:nuggets/mithril", 3, 5, 1.0f)), null, List.of(beeExists("raw_materials/mithril"), not(tagEmpty("neoforge:nuggets/mithril"))), "alloys/honeycomb_mitril");
        bee("alloys/signalum", List.of(tag("c:nuggets/signalum", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/signalum")), beeExists("alloys/signalum")), "alloys/honeycomb_signalum");
        bee("alloys/steel", List.of(tag("c:nuggets/steel", 7, 12, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/steel")), beeExists("alloys/steel")), "alloys/honeycomb_steel");
        bee("aquaculture/neptunium", List.of(item("aquaculture:neptunium_nugget", 1, 3, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("aquaculture/neptunium"), modLoaded("aquaculture")), "aquaculture/honeycomb_neptunium");
        bee("ars_nouveau/air_essence", List.of(item("ars_nouveau:air_essence", 1, 1, 0.25f)), null, List.of(beeExists("ars_nouveau/air_essence"), modLoaded("ars_nouveau")), "ars_nouveau/honeycomb_air_essence");
        bee("ars_nouveau/arcane", List.of(item("ars_nouveau:source_gem", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("ars_nouveau")), "ars_nouveau/honeycomb_arcane");
        bee("ars_nouveau/earth_essence", List.of(item("ars_nouveau:earth_essence", 1, 1, 0.25f)), null, List.of(beeExists("ars_nouveau/earth_essence"), modLoaded("ars_nouveau")), "ars_nouveau/honeycomb_earth_essence");
        bee("ars_nouveau/fire_essence", List.of(item("ars_nouveau:fire_essence", 1, 1, 0.25f)), null, List.of(beeExists("ars_nouveau/fire_essence"), modLoaded("ars_nouveau")), "ars_nouveau/honeycomb_fire_essence");
        bee("ars_nouveau/water_essence", List.of(item("ars_nouveau:water_essence", 1, 1, 0.25f)), null, List.of(beeExists("ars_nouveau/water_essence"), modLoaded("ars_nouveau")), "ars_nouveau/honeycomb_water_essence");
        bee("astralsorcery/starmetal", List.of(item("astralsorcery:starmetal_ingot", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("astralsorcery")), "astralsorcery/honeycomb_starmetal");
        bee("atm/allthemodium", List.of(item("allthemodium:allthemodium_nugget", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("allthemodium")), "atm/honeycomb_allthemodium");
        bee("atm/gregstar", List.of(), fluid("productivebees:honey", 500), List.of(beeExists("atm/gregstar")), "atm/honeycomb_gregstar");
        bee("atm/patrick", List.of(item("allthetweaks:patrick_star", 1, 1, 0.05f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("allthetweaks")), "atm/honeycomb_patrick");
        bee("atm/soul_lava", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("allthemodium:soul_lava", 20), List.of(modLoaded("allthemodium")), "atm/honeycomb_soul_lava");
        bee("atm/starry", List.of(item("allthetweaks:atm_star_shard", 1, 1, 0.05f), item("mysticalagradditions:nether_star_shard", 1, 1, 0.5f)), null, List.of(modLoaded("allthetweaks"), modLoaded("mysticalagradditions")), "atm/honeycomb_starry");
        bee("atm/unobtainium", List.of(item("allthemodium:unobtainium_nugget", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("allthemodium")), "atm/honeycomb_unobtainium");
        bee("atm/vibranium", List.of(item("allthemodium:vibranium_nugget", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("allthemodium")), "atm/honeycomb_vibranium");
        bee("neovitae/hellfire", List.of(item("neovitae:raw_demonite", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("neovitae/hellfire"), modLoaded("neovitae")), "neovitae/honeycomb_hellfire");
        bee("neovitae/hematophagous", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("neovitae:essentia_vitae", 100), List.of(not(fluidTagEmpty("neovitae:essentia_vitae"))), "neovitae/honeycomb_hematophagous");
        bee("botania/elementium", List.of(tag("c:nuggets/elementium", 1, 2, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("botania")), "botania/honeycomb_elementium");
        bee("botania/manasteel", List.of(tag("c:nuggets/manasteel", 1, 2, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("botania")), "botania/honeycomb_manasteel");
        bee("botania/terrasteel", List.of(tag("c:nuggets/terrasteel", 1, 2, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("botania")), "botania/honeycomb_terrasteel");
        bee("botanicadds/gaiasteel", List.of(item("botanicadds:gaiasteel_nugget", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("botanicadds")), "botanicadds/honeycomb_gaiasteel");
        bee("byg/emeraldite", List.of(item("byg:emeraldite_shards", 1, 3, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("byg"), beeExists("byg/emeraldite")), "byg/honeycomb_emeraldite");
        bee("byg/pendorite", List.of(item("byg:pendorite_scraps", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("byg"), beeExists("byg/pendorite")), "byg/honeycomb_pendorite");
        bee("draconicevolution/awakened", List.of(tag("c:nuggets/draconium_awakened", 1, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("draconicevolution")), "draconicevolution/honeycomb_awakened");
        bee("draconicevolution/chaos", List.of(item("draconicevolution:small_chaos_frag", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("draconicevolution")), "draconicevolution/honeycomb_chaos");
        bee("draconicevolution/draconium", List.of(tag("c:nuggets/draconium", 1, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("draconicevolution")), "draconicevolution/honeycomb_draconium");
        bee("dusts/blazing", List.of(item("minecraft:blaze_rod", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "dusts/comb_blazing");
        bee("raw_materials/aluminum", List.of(tag("c:dusts/aluminum", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/aluminum")), tagEmpty("c:raw_materials/aluminum")), "dusts/honeycomb_aluminium");
        bee("raw_materials/bismuth", List.of(tag("c:dusts/bismuth", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/bismuth")), tagEmpty("c:raw_materials/bismuth")), "dusts/honeycomb_bismuth");
        bee("dusts/blazing", List.of(item("minecraft:blaze_powder", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "dusts/honeycomb_blazing");
        bee("dusts/glowing", List.of(tag("c:dusts/glowstone", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "dusts/honeycomb_glowing");
        bee("raw_materials/lead", List.of(tag("c:dusts/lead", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/lead")), tagEmpty("c:raw_materials/lead")), "dusts/honeycomb_leaden");
        bee("raw_materials/nickel", List.of(tag("c:dusts/nickel", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/nickel")), tagEmpty("c:raw_materials/nickel")), "dusts/honeycomb_nickel");
        bee("dusts/niter", List.of(tag("c:dusts/niter", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/niter"))), "dusts/honeycomb_niter");
        bee("raw_materials/osmium", List.of(tag("c:dusts/osmium", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/osmium")), tagEmpty("c:raw_materials/osmium")), "dusts/honeycomb_osmium");
        bee("raw_materials/platinum", List.of(tag("c:dusts/platinum", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/platinum")), tagEmpty("c:raw_materials/platinum")), "dusts/honeycomb_platinum");
        bee("raw_materials/radioactive", List.of(tag("c:dusts/uranium", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/uranium")), tagEmpty("c:raw_materials/uranium")), "dusts/honeycomb_radioactive");
        bee("dusts/redstone", List.of(tag("c:dusts/redstone", 1, 2, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "dusts/honeycomb_redstone");
        bee("dusts/niter", List.of(tag("c:dusts/saltpeter", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/saltpeter")), tagEmpty("c:dusts/niter")), "dusts/honeycomb_saltpeter");
        bee("dusts/salty", List.of(tag("c:dusts/salt", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("dusts/salty"), not(tagEmpty("c:dusts/salt"))), "dusts/honeycomb_salty");
        bee("raw_materials/silver", List.of(tag("c:dusts/silver", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/silver")), tagEmpty("c:raw_materials/silver")), "dusts/honeycomb_silver");
        bee("dusts/sulfur", List.of(tag("c:dusts/sulfur", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/sulfur"))), "dusts/honeycomb_sulfur");
        bee("raw_materials/tin", List.of(tag("c:dusts/tin", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/tin")), tagEmpty("c:raw_materials/tin")), "dusts/honeycomb_tin");
        bee("raw_materials/titanium", List.of(tag("c:dusts/titanium", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/titanium")), tagEmpty("c:raw_materials/titanium"), beeExists("raw_materials/titanium")), "dusts/honeycomb_titanium");
        bee("raw_materials/tungsten", List.of(tag("c:dusts/tungsten", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/tungsten")), tagEmpty("c:raw_materials/tungsten")), "dusts/honeycomb_tungsten");
        bee("raw_materials/zinc", List.of(tag("c:dusts/zinc", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/zinc")), tagEmpty("c:raw_materials/zinc")), "dusts/honeycomb_zinc");
        bee("eidolon/arcane_gold", List.of(item("eidolon:arcane_gold_nugget", 3, 7, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("eidolon/arcane_gold"), modLoaded("eidolon")), "eidolon/honeycomb_arcane_gold");
        bee("eidolon/pewter", List.of(item("eidolon:pewter_blend", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("eidolon/pewter"), modLoaded("eidolon")), "eidolon/honeycomb_pewter");
        bee("eidolon/soul_shard", List.of(item("eidolon:soul_shard", 1, 1, 0.25f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("eidolon/soul_shard"), modLoaded("eidolon")), "eidolon/honeycomb_soul_shard");
        bee("elementalcraft/air_crystal", List.of(item("elementalcraft:aircrystal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/air_crystal")), "elementalcraft/honeycomb_air_crystal");
        bee("elementalcraft/drenched_iron", List.of(item("elementalcraft:drenched_iron_ingot", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/drenched_iron")), "elementalcraft/honeycomb_drenched_iron");
        bee("elementalcraft/earth_crystal", List.of(item("elementalcraft:earthcrystal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/earth_crystal")), "elementalcraft/honeycomb_earth_crystal");
        bee("elementalcraft/fire_crystal", List.of(item("elementalcraft:firecrystal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/fire_crystal")), "elementalcraft/honeycomb_fire_crystal");
        bee("elementalcraft/fireite", List.of(item("elementalcraft:fireite_nugget", 4, 7, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/fireite")), "elementalcraft/honeycomb_fireite");
        bee("elementalcraft/inert_crystal", List.of(item("elementalcraft:inert_crystal", 1, 1, 0.9f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/inert_crystal")), "elementalcraft/honeycomb_inert_crystal");
        bee("elementalcraft/pure_crystal", List.of(item("elementalcraft:purecrystal", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/pure_crystal")), "elementalcraft/honeycomb_pure_crystal");
        bee("elementalcraft/springaline", List.of(item("elementalcraft:springaline_shard", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/springaline")), "elementalcraft/honeycomb_springaline");
        bee("elementalcraft/swift_alloy", List.of(item("elementalcraft:swift_alloy_nugget", 5, 8, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/swift_alloy")), "elementalcraft/honeycomb_swift_alloy");
        bee("elementalcraft/water_crystal", List.of(item("elementalcraft:watercrystal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("elementalcraft"), beeExists("elementalcraft/water_crystal")), "elementalcraft/honeycomb_water_crystal");
        bee("enderio/conductive_alloy", List.of(item("enderio:conductive_alloy_nugget", 5, 7, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/conductive_alloy")), "enderio/honeycomb_conductive_alloy");
        bee("enderio/dark_steel", List.of(item("enderio:dark_steel_nugget", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/dark_steel")), "enderio/honeycomb_dark_steel");
        bee("enderio/end_steel", List.of(item("enderio:end_steel_nugget", 1, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/end_steel")), "enderio/honeycomb_end_steel");
        bee("enderio/energetic_alloy", List.of(item("enderio:energetic_alloy_nugget", 6, 8, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/energetic_alloy")), "enderio/honeycomb_energetic_alloy");
        bee("enderio/infinity", List.of(item("enderio:grains_of_infinity", 1, 1, 0.75f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/infinity")), "enderio/honeycomb_infinity");
        bee("enderio/pulsating_alloy", List.of(item("enderio:pulsating_alloy_nugget", 1, 12, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/pulsating_alloy")), "enderio/honeycomb_pulsating_alloy");
        bee("enderio/redstone_alloy", List.of(item("enderio:redstone_alloy_nugget", 3, 8, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/redstone_alloy")), "enderio/honeycomb_redstone_alloy");
        bee("enderio/soularium", List.of(item("enderio:soularium_nugget", 5, 5, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/soularium")), "enderio/honeycomb_soularium");
        bee("enderio/vibrant_alloy", List.of(item("enderio:vibrant_alloy_nugget", 5, 7, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio"), beeExists("enderio/vibrant_alloy")), "enderio/honeycomb_vibrant_alloy");
        bee("enderio_endergy/crude_steel", List.of(item("enderio_endergy:crude_steel_nugget", 6, 8, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio_endergy"), beeExists("enderio_endergy/crude_steel")), "enderio_endergy/honeycomb_crude_steel");
        bee("enderio_endergy/crystalline_alloy", List.of(item("enderio_endergy:crystalline_alloy_nugget", 4, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio_endergy"), beeExists("enderio_endergy/crystalline_alloy")), "enderio_endergy/honeycomb_crystalline_alloy");
        bee("enderio_endergy/melodic_alloy", List.of(item("enderio_endergy:melodic_alloy_nugget", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio_endergy"), beeExists("enderio_endergy/melodic_alloy")), "enderio_endergy/honeycomb_melodic_alloy");
        bee("enderio_endergy/stellar_alloy", List.of(item("enderio_endergy:stellar_alloy_nugget", 1, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio_endergy"), beeExists("enderio_endergy/stellar_alloy")), "enderio_endergy/honeycomb_stellar_alloy");
        bee("enderio_endergy/vivid_alloy", List.of(item("enderio_endergy:vivid_alloy_nugget", 3, 7, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enderio_endergy"), beeExists("enderio_endergy/vivid_alloy")), "enderio_endergy/honeycomb_vivid_alloy");
        bee("enigmaticlegacyplus/astral", List.of(item("enigmaticlegacyplus:astral_dust", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enigmaticlegacyplus"), beeExists("enigmaticlegacyplus/astral")), "enigmaticlegacy/honeycomb_astral");
        bee("enigmaticlegacyplus/etherium_ore", List.of(item("enigmaticlegacyplus:etherium_ore", 1, 1, 0.75f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("enigmaticlegacyplus"), beeExists("enigmaticlegacyplus/etherium_ore")), "enigmaticlegacy/honeycomb_etherium_ore");
        bee("eternal_starlight/amaramber", List.of(item("eternal_starlight:raw_amaramber", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/amaramber")), "eternal_starlight/honeycomb_amaramber");
        bee("eternal_starlight/deepsilver", List.of(item("eternal_starlight:raw_deepsilver", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/deepsilver")), "eternal_starlight/honeycomb_deepsilver");
        bee("eternal_starlight/glacite", List.of(item("eternal_starlight:glacite_shard", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/glacite")), "eternal_starlight/honeycomb_glacite");
        bee("eternal_starlight/malarite", List.of(item("eternal_starlight:malarite", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/malarite")), "eternal_starlight/honeycomb_malarite");
        bee("eternal_starlight/starcore", List.of(item("eternal_starlight:starcore", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/starcore")), "eternal_starlight/honeycomb_starcore");
        bee("eternal_starlight/starlit_diamond", List.of(item("eternal_starlight:starlit_diamond", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/starlit_diamond")), "eternal_starlight/honeycomb_starlit_diamond");
        bee("eternal_starlight/thermal", List.of(item("eternal_starlight:thermal_springstone_ingot", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("eternal_starlight"), beeExists("eternal_starlight/thermal")), "eternal_starlight/honeycomb_thermal");
        bee("evilcraft/bloody", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("evilcraft:blood", 100), List.of(modLoaded("evilcraft")), "evilcraft/honeycomb_bloody");
        bee("evilcraft/dark_gem", List.of(item("evilcraft:dark_gem", 1, 1, 0.7f), item("evilcraft:dark_gem_crushed", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("evilcraft"), beeExists("evilcraft/dark_gem")), "evilcraft/honeycomb_dark_gem");
        plain("feywild:honeycomb", List.of(item("feywild:fey_dust", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("feywild")), "feywild/honeycomb_feywild");
        bee("butcher", List.of(item("minecraft:beef", 1, 1, 0.05f), item("minecraft:porkchop", 1, 1, 0.05f), item("minecraft:chicken", 1, 1, 0.05f), item("minecraft:mutton", 1, 1, 0.05f)), fluidTag("c:meat", 30), List.of(beeExists("butcher"), not(fluidTagEmpty("c:meat"))), "fluids/honeycomb_butcher");
        bee("fluids/chocolate", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:chocolate", 100), List.of(not(fluidTagEmpty("c:chocolate"))), "fluids/honeycomb_chocolate");
        bee("materials/plastic", List.of(tag("c:plastics", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:latex", 50), List.of(not(tagEmpty("c:plastics")), not(fluidTagEmpty("c:latex"))), "fluids/honeycomb_latex");
        plain("productivebees:honeycomb_milky", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("minecraft:milk", 100), List.of(), "fluids/honeycomb_milky_fluid");
        bee("fluids/oily", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:crude_oil", 50), List.of(beeExists("fluids/oily")), "fluids/honeycomb_oily");
        bee("fluids/tea", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("create:tea", 100), List.of(modLoaded("create")), "fluids/honeycomb_tea");
        bee("fluxnetworks/flux", List.of(item("fluxnetworks:flux_dust", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("fluxnetworks"), beeExists("fluxnetworks/flux")), "fluxnetworks/honeycomb_flux");
        bee("forbidden_arcanus/arcane_crystal", List.of(item("forbidden_arcanus:arcane_crystal", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("forbidden_arcanus/arcane_crystal"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/honeycomb_arcane_crystal");
        bee("forbidden_arcanus/deorum", List.of(item("forbidden_arcanus:deorum_nugget", 4, 8, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("forbidden_arcanus/deorum"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/honeycomb_deorum");
        bee("forbidden_arcanus/rune", List.of(item("forbidden_arcanus:rune", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("forbidden_arcanus/rune"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/honeycomb_rune");
        bee("forbidden_arcanus/stellarite", List.of(item("forbidden_arcanus:condensed_experience", 1, 1, 0.3f), item("forbidden_arcanus:stellarite_piece", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("forbidden_arcanus/stellarite"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/honeycomb_stellarite");
        bee("gems/agate", List.of(tag("c:gems/agate", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/agate")), beeExists("gems/agate")), "gems/honeycomb_agate");
        bee("gems/alexandrite", List.of(tag("c:gems/alexandrite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/alexandrite")), beeExists("gems/alexandrite")), "gems/honeycomb_alexandrite");
        bee("gems/amber_gem", List.of(tag("c:gems/amber", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/amber")), beeExists("gems/amber_gem")), "gems/honeycomb_amber_gem");
        bee("gems/amethyst", List.of(item("minecraft:amethyst_shard", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "gems/honeycomb_amethyst");
        bee("gems/ametrine", List.of(tag("c:gems/ametrine", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/ametrine")), beeExists("gems/ametrine")), "gems/honeycomb_ametrine");
        bee("gems/ammolite", List.of(tag("c:gems/ammolite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/ammolite")), beeExists("gems/ammolite")), "gems/honeycomb_ammolite");
        bee("gems/apatite", List.of(tag("c:gems/apatite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/apatite")), beeExists("gems/apatite")), "gems/honeycomb_apatite");
        bee("gems/aquamarine", List.of(tag("c:gems/aquamarine", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("gems/aquamarine"), not(tagEmpty("c:gems/aquamarine"))), "gems/honeycomb_aquamarine");
        bee("gems/benitoite", List.of(tag("c:gems/benitoite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/benitoite")), beeExists("gems/benitoite")), "gems/honeycomb_benitoite");
        bee("gems/black_diamond", List.of(tag("c:gems/black_diamond", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/black_diamond")), beeExists("gems/black_diamond")), "gems/honeycomb_black_diamond");
        bee("gems/black_opal", List.of(tag("c:gems/black_opal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/black_opal")), beeExists("gems/black_opal")), "gems/honeycomb_black_opal");
        bee("gems/carnelian", List.of(tag("c:gems/carnelian", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/carnelian")), beeExists("gems/carnelian")), "gems/honeycomb_carnelian");
        bee("gems/cats_eye", List.of(tag("c:gems/cats_eye", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/cats_eye")), beeExists("gems/cats_eye")), "gems/honeycomb_cats_eye");
        bee("gems/chrysoprase", List.of(tag("c:gems/chrysoprase", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/chrysoprase")), beeExists("gems/chrysoprase")), "gems/honeycomb_chrysoprase");
        bee("gems/cinnabar", List.of(tag("c:gems/cinnabar", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/cinnabar")), tagEmpty("c:dusts/cinnabar"), beeExists("gems/cinnabar")), "gems/honeycomb_cinnabar");
        bee("gems/cinnabar", List.of(tag("c:dusts/cinnabar", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/cinnabar")), beeExists("gems/cinnabar")), "gems/honeycomb_cinnabar_dust");
        bee("gems/citrine", List.of(tag("c:gems/citrine", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/citrine")), beeExists("gems/citrine")), "gems/honeycomb_citrine");
        bee("gems/coral", List.of(tag("c:gems/coral", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/coral")), beeExists("gems/coral")), "gems/honeycomb_coral");
        bee("gems/diamond", List.of(tag("c:gems/diamond", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "gems/honeycomb_diamond");
        bee("gems/emerald", List.of(tag("c:gems/emerald", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "gems/honeycomb_emerald");
        bee("gems/euclase", List.of(tag("c:gems/euclase", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/euclase")), beeExists("gems/euclase")), "gems/honeycomb_euclase");
        bee("gems/fluorite", List.of(tag("c:gems/fluorite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/fluorite")), beeExists("gems/fluorite")), "gems/honeycomb_fluorite");
        bee("gems/garnet", List.of(tag("c:gems/garnet", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/garnet")), beeExists("gems/garnet")), "gems/honeycomb_garnet");
        bee("gems/green_sapphire", List.of(tag("c:gems/green_sapphire", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/green_sapphire")), beeExists("gems/green_sapphire")), "gems/honeycomb_green_sapphire");
        bee("gems/heliodor", List.of(tag("c:gems/heliodor", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/heliodor")), beeExists("gems/heliodor")), "gems/honeycomb_heliodor");
        bee("gems/iolite", List.of(tag("c:gems/iolite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/iolite")), beeExists("gems/iolite")), "gems/honeycomb_iolite");
        bee("gems/jade", List.of(tag("c:gems/jade", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/jade")), beeExists("gems/jade")), "gems/honeycomb_jade");
        bee("gems/jasper", List.of(tag("c:gems/jasper", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/jasper")), beeExists("gems/jasper")), "gems/honeycomb_jasper");
        bee("gems/kunzite", List.of(tag("c:gems/kunzite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/kunzite")), beeExists("gems/kunzite")), "gems/honeycomb_kunzite");
        bee("gems/kyanite", List.of(tag("c:gems/kyanite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/kyanite")), beeExists("gems/kyanite")), "gems/honeycomb_kyanite");
        bee("gems/lapis", List.of(tag("c:gems/lapis", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "gems/honeycomb_lapis");
        bee("gems/lepidolite", List.of(tag("c:gems/lepidolite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/lepidolite")), beeExists("gems/lepidolite")), "gems/honeycomb_lepidolite");
        bee("gems/malachite", List.of(tag("c:gems/malachite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/malachite")), beeExists("gems/malachite")), "gems/honeycomb_malachite");
        bee("gems/moldavite", List.of(tag("c:gems/moldavite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/moldavite")), beeExists("gems/moldavite")), "gems/honeycomb_moldavite");
        bee("gems/moonstone", List.of(tag("c:gems/moonstone", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/moonstone")), beeExists("gems/moonstone")), "gems/honeycomb_moonstone");
        bee("gems/morganite", List.of(tag("c:gems/morganite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/morganite")), beeExists("gems/morganite")), "gems/honeycomb_morganite");
        bee("gems/onyx", List.of(tag("c:gems/onyx", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/onyx")), beeExists("gems/onyx")), "gems/honeycomb_onyx");
        bee("gems/opal", List.of(tag("c:gems/opal", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/opal")), beeExists("gems/opal")), "gems/honeycomb_opal");
        bee("gems/pearl", List.of(tag("c:gems/pearl", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/pearl")), beeExists("gems/pearl")), "gems/honeycomb_pearl");
        bee("gems/peridot", List.of(tag("c:gems/peridot", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/peridot")), beeExists("gems/peridot")), "gems/honeycomb_peridot");
        bee("gems/phosphophyllite", List.of(tag("c:gems/phosphophyllite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/phosphophyllite")), beeExists("gems/phosphophyllite")), "gems/honeycomb_phosphophyllite");
        bee("gems/pyrope", List.of(tag("c:gems/pyrope", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/pyrope")), beeExists("gems/pyrope")), "gems/honeycomb_pyrope");
        bee("gems/crystalline", List.of(tag("c:gems/quartz", 1, 3, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "gems/honeycomb_quartz");
        bee("gems/rose_quartz", List.of(tag("c:gems/rose_quartz", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/rose_quartz")), beeExists("gems/rose_quartz")), "gems/honeycomb_rose_quartz");
        bee("gems/ruby", List.of(tag("c:gems/ruby", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/ruby")), beeExists("gems/ruby")), "gems/honeycomb_ruby");
        bee("gems/sapphire", List.of(tag("c:gems/sapphire", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/sapphire")), beeExists("gems/sapphire")), "gems/honeycomb_sapphire");
        bee("gems/sodalite", List.of(tag("c:gems/sodalite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/sodalite")), beeExists("gems/sodalite")), "gems/honeycomb_sodalite");
        bee("gems/spinel", List.of(tag("c:gems/spinel", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/spinel")), beeExists("gems/spinel")), "gems/honeycomb_spinel");
        bee("gems/sunstone", List.of(tag("c:gems/sunstone", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/sunstone")), beeExists("gems/sunstone")), "gems/honeycomb_sunstone");
        bee("gems/tanzanite", List.of(tag("c:gems/tanzanite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/tanzanite")), beeExists("gems/tanzanite")), "gems/honeycomb_tanzanite");
        bee("gems/tektite", List.of(tag("c:gems/tektite", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/tektite")), beeExists("gems/tektite")), "gems/honeycomb_tektite");
        bee("gems/topaz", List.of(tag("c:gems/topaz", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/topaz")), beeExists("gems/topaz")), "gems/honeycomb_topaz");
        bee("gems/tourmaline", List.of(tag("c:gems/tourmaline", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/tourmaline")), beeExists("gems/tourmaline")), "gems/honeycomb_tourmaline");
        bee("gems/turquoise", List.of(tag("c:gems/turquoise", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/turquoise")), beeExists("gems/turquoise")), "gems/honeycomb_turquoise");
        bee("gems/white_diamond", List.of(tag("c:gems/white_diamond", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/white_diamond")), beeExists("gems/white_diamond")), "gems/honeycomb_white_diamond");
        bee("gems/zircon", List.of(tag("c:gems/zircon", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/zircon")), beeExists("gems/zircon")), "gems/honeycomb_zircon");
        bee("gobber/end_gobber", List.of(item("gobber2:gobber2_globette_end", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("gobber2")), "gobber/honeycomb_end_gobber");
        bee("gobber/gobber", List.of(item("gobber2:gobber2_globette", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("gobber2")), "gobber/honeycomb_gobber");
        bee("gobber/nether_gobber", List.of(item("gobber2:gobber2_globette_nether", 1, 2, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("gobber2")), "gobber/honeycomb_nether_gobber");
        bee("gtceu/neutronium", List.of(item("gtceu:neutronium_nugget", 1, 1, 0.02f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("gtceu"), beeExists("gtceu/neutronium")), "gtceu/honeycomb_neutronium");
        bee("iceandfire/fire_dragonsteel", List.of(item("iceandfire:dragonsteel_fire_ingot", 1, 1, 0.15f), item("iceandfire:fire_dragon_blood", 1, 1, 0.05f)), fluid("productivebees:honey", 0), List.of(modLoaded("iceandfire"), beeExists("iceandfire/fire_dragonsteel")), "iceandfire/honeycomb_fire_dragonsteel");
        bee("iceandfire/ice_dragonsteel", List.of(item("iceandfire:dragonsteel_ice_ingot", 1, 1, 0.15f), item("iceandfire:ice_dragon_blood", 1, 1, 0.05f)), fluid("productivebees:honey", 0), List.of(modLoaded("iceandfire"), beeExists("iceandfire/ice_dragonsteel")), "iceandfire/honeycomb_ice_dragonsteel");
        bee("iceandfire/lightning_dragonsteel", List.of(item("iceandfire:dragonsteel_lightning_ingot", 1, 1, 0.15f), item("iceandfire:lightning_dragon_blood", 1, 1, 0.05f)), fluid("productivebees:honey", 0), List.of(modLoaded("iceandfire"), beeExists("iceandfire/lightning_dragonsteel")), "iceandfire/honeycomb_lightning_dragonsteel");
        bee("immersiveengineering/hop_graphite", List.of(item("immersiveengineering:dust_hop_graphite", 1, 1, 0.25f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("immersiveengineering/hop_graphite"), modLoaded("immersiveengineering")), "immersiveengineering/honeycomb_hop_graphite");
        bee("industrialforegoing/ether_gas", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("industrialforegoing:ether_gas", 10), List.of(modLoaded("industrialforegoing"), beeExists("industrialforegoing/ether_gas")), "industrialforegoing/honeycomb_ether_gas");
        bee("industrialforegoing/pink_slimy", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:pink_slime", 100), List.of(not(fluidTagEmpty("c:pink_slime"))), "industrialforegoing/honeycomb_pink_slimy");
        bee("ender", List.of(item("productivebees:wax", 1, 1, 1.0f), item("minecraft:ender_pearl", 1, 1, 0.2f)), fluid("integrateddynamics:liquid_chorus", 50), List.of(modLoaded("integrateddynamics")), "integrateddynamics/honeycomb_ender_liquid_chorus");
        bee("integrateddynamics/menril", List.of(item("productivebees:wax", 1, 1, 1.0f), item("integrateddynamics:crystalized_menril_chunk", 1, 1, 0.1f)), fluid("integrateddynamics:menril_resin", 50), List.of(modLoaded("integrateddynamics")), "integrateddynamics/honeycomb_menril");
        bee("irons_spellbooks/arcane_essence", List.of(item("irons_spellbooks:arcane_essence", 1, 1, 0.75f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("irons_spellbooks/arcane_essence"), modLoaded("irons_spellbooks")), "irons_spellbooks/honeycomb_arcane_essence");
        bee("justdirethings/blazegold", List.of(item("justdirethings:raw_blazegold", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("justdirethings/blazegold"), modLoaded("justdirethings")), "justdirethings/honeycomb_blazegold");
        bee("justdirethings/celestigem", List.of(item("justdirethings:celestigem", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("justdirethings/celestigem"), modLoaded("justdirethings")), "justdirethings/honeycomb_celestigem");
        bee("justdirethings/eclipsealloy", List.of(item("justdirethings:raw_eclipsealloy", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("justdirethings/eclipsealloy"), modLoaded("justdirethings")), "justdirethings/honeycomb_eclipsealloy");
        bee("justdirethings/ferricore", List.of(item("justdirethings:raw_ferricore", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("justdirethings/ferricore"), modLoaded("justdirethings")), "justdirethings/honeycomb_ferricore");
        bee("justdirethings/time_crystal", List.of(item("justdirethings:time_crystal", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), fluid("justdirethings:time_fluid_source", 25), List.of(beeExists("justdirethings/time_crystal"), modLoaded("justdirethings")), "justdirethings/honeycomb_time_crystal");
        bee("l2hostility/chaotic", List.of(item("l2hostility:chaos_ingot", 1, 1, 25f)), null, List.of(modLoaded("l2hostility"), beeExists("l2hostility/chaotic")), "l2hostility/honeycomb_chaotic");
        bee("l2hostility/miracle", List.of(item("l2hostility:miracle_ingot", 1, 1, 0.05f)), null, List.of(modLoaded("l2hostility"), beeExists("l2hostility/miracle")), "l2hostility/honeycomb_miracle");
        bee("materials/plastic", List.of(tag("c:plastics", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:plastics")), fluidTagEmpty("c:latex")), "materials/honeycomb_plastic");
        bee("ae2/silicon", List.of(item("productivebees:wax", 1, 1, 1.0f), tag("c:silicon", 1, 1, 0.5f)), null, List.of(not(tagEmpty("c:silicon"))), "materials/honeycomb_silicon");
        bee("materials/sticky_resin", List.of(item("gtceu:sticky_resin", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("gtceu"), beeExists("materials/sticky_resin")), "materials/honeycomb_sticky_resin_gtceu");
        bee("materials/sticky_resin", List.of(item("ic2:sticky_resin", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("ic2"), beeExists("materials/sticky_resin")), "materials/honeycomb_sticky_resin_ic2");
        bee("mekanism/refined_glowstone", List.of(tag("c:nuggets/refined_glowstone", 1, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/refined_glowstone")), modLoaded("mekanism")), "mekanism/honeycomb_refined_glowstone");
        bee("mekanism/refined_obsidian", List.of(tag("c:dusts/refined_obsidian", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:dusts/refined_obsidian")), modLoaded("mekanism")), "mekanism/honeycomb_refined_obsidian");
        bee("chemlib/antimony", List.of(item("modern_industrialization:raw_antimony", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("chemlib/antimony"), modLoaded("modern_industrialization")), "modern_industrialization/honeycomb_antimony");
        bee("modern_industrialization/beryllium", List.of(item("modern_industrialization:beryllium_tiny_dust", 1, 3, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("modern_industrialization/beryllium"), modLoaded("modern_industrialization")), "modern_industrialization/honeycomb_beryllium");
        bee("modern_industrialization/chromium", List.of(item("modern_industrialization:chromium_tiny_dust", 1, 3, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("modern_industrialization/chromium"), modLoaded("modern_industrialization")), "modern_industrialization/honeycomb_chromium");
        bee("modern_industrialization/manganese", List.of(item("modern_industrialization:manganese_tiny_dust", 1, 3, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("modern_industrialization/manganese"), modLoaded("modern_industrialization")), "modern_industrialization/honeycomb_manganese");
        bee("modern_industrialization/monazite", List.of(item("modern_industrialization:monazite_dust", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("modern_industrialization/monazite"), modLoaded("modern_industrialization")), "modern_industrialization/honeycomb_monazite");
        bee("modularbees/imperial", List.of(item("modularbees:honey_jelly", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("modularbees/imperial"), modLoaded("modularbees")), "modularbees/honeycomb_imperial");
        bee("mysticalagriculture/awakened_supremium", List.of(item("mysticalagriculture:awakened_supremium_essence", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_awakened_supremium");
        bee("mysticalagriculture/imperium", List.of(item("mysticalagriculture:imperium_essence", 1, 2, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_imperium");
        bee("mysticalagriculture/inferium", List.of(item("mysticalagriculture:inferium_essence", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_inferium");
        bee("mysticalagriculture/insanium", List.of(item("mysticalagradditions:insanium_essence", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagradditions")), "mysticalagriculture/honeycomb_insanium");
        bee("mysticalagriculture/prosperity", List.of(item("mysticalagriculture:prosperity_shard", 1, 2, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_prosperity");
        bee("mysticalagriculture/prudentium", List.of(item("mysticalagriculture:prudentium_essence", 1, 2, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_prudentium");
        bee("mysticalagriculture/soulium", List.of(item("mysticalagriculture:soulium_dust", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_soulium");
        bee("mysticalagriculture/supremium", List.of(item("mysticalagriculture:supremium_essence", 1, 1, 0.3f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_supremium");
        bee("mysticalagriculture/tertium", List.of(item("mysticalagriculture:tertium_essence", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mysticalagriculture")), "mysticalagriculture/honeycomb_tertium");
        bee("mythicbotany/alfsteel", List.of(item("mythicbotany:alfsteel_nugget", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("mythicbotany")), "mythicbotany/honeycomb_alfsteel");
        bee("naturesaura/depth_ingot", List.of(item("naturesaura:depth_ingot", 1, 1, 0.5f)), null, List.of(beeExists("naturesaura/depth_ingot"), modLoaded("naturesaura")), "naturesaura/honeycomb_depht_ingot");
        bee("naturesaura/infused_iron", List.of(item("naturesaura:infused_iron", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("naturesaura/infused_iron"), modLoaded("naturesaura")), "naturesaura/honeycomb_infused_iron");
        bee("naturesaura/sky_ingot", List.of(item("naturesaura:sky_ingot", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("naturesaura/sky_ingot"), modLoaded("naturesaura")), "naturesaura/honeycomb_sky_ingot");
        bee("naturesaura/tainted_gold", List.of(item("naturesaura:tainted_gold", 1, 1, 0.75f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("naturesaura/tainted_gold"), modLoaded("naturesaura")), "naturesaura/honeycomb_tainted_gold");
        bee("occultism/iesnium", List.of(tag("c:raw_materials/iesnium", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/iesnium")), beeExists("occultism/iesnium")), "occultism/honeycomb_iesnium");
        bee("oritech/adamant", List.of(item("oritech:adamant_ingot", 1, 1, 0.8f)), null, List.of(beeExists("oritech/adamant"), modLoaded("oritech")), "oritech/honeycomb_adamant");
        bee("oritech/biosteel", List.of(item("oritech:biosteel_ingot", 1, 1, 0.6f)), null, List.of(beeExists("oritech/biosteel"), modLoaded("oritech")), "oritech/honeycomb_biosteel");
        bee("oritech/duratium", List.of(item("oritech:duratium_ingot", 1, 1, 0.4f)), null, List.of(beeExists("oritech/duratium"), modLoaded("oritech")), "oritech/honeycomb_duratium");
        bee("oritech/energite", List.of(item("oritech:energite_ingot", 1, 1, 0.5f)), null, List.of(beeExists("oritech/energite"), modLoaded("oritech")), "oritech/honeycomb_energite");
        bee("oritech/fluxite", List.of(item("oritech:fluxite", 1, 1, 0.9f)), null, List.of(beeExists("oritech/fluxite"), modLoaded("oritech")), "oritech/honeycomb_fluxite");
        bee("oritech/prometheum", List.of(item("oritech:prometheum_ingot", 1, 1, 0.05f)), null, List.of(beeExists("oritech/prometheum"), modLoaded("oritech")), "oritech/honeycomb_prometheum");
        bee("oritech/sheol_fire", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("oritech:still_sheol_fire", 50), List.of(beeExists("oritech/sheol_fire"), modLoaded("oritech")), "oritech/honeycomb_sheol_fire");
        bee("oritech/strange_matter", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("oritech:still_strange_matter", 20), List.of(beeExists("oritech/strange_matter"), modLoaded("oritech")), "oritech/honeycomb_strange_matter");
        bee("oritech/sulfuric_acid", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("oritech:still_sulfuric_acid", 100), List.of(beeExists("oritech/sulfuric_acid"), modLoaded("oritech")), "oritech/honeycomb_sulfuric_acid");
        bee("oritech/uranite_crystal", List.of(item("oritech:uranite_crystal", 1, 1, 0.75f)), null, List.of(beeExists("oritech/uranite_crystal"), modLoaded("oritech")), "oritech/honeycomb_uranite_crystal");
        bee("pneumaticcraft/compressed_iron", List.of(tag("c:ingots/compressed_iron", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:ingots/compressed_iron"))), "pneumaticcraft/honeycomb_compressed_iron");
        bee("pokecube/cosmic_dust", List.of(tag("c:gems/cosmicdust", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/cosmicdust")), not(tagEmpty("c:ores/cosmic"))), "pokecube/honeycomb_cosmic_dust");
        bee("pokecube/spectrum", List.of(tag("c:gems/spectrum", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:gems/spectrum")), not(tagEmpty("c:storage_blocks/spectrum"))), "pokecube/honeycomb_spectrum");
        bee("powah/blazing_crystal", List.of(item("powah:crystal_blazing", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_blazing_crystal");
        bee("powah/energized_steel", List.of(item("powah:steel_energized", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_energized_steel");
        bee("powah/niotic_crystal", List.of(item("powah:crystal_niotic", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_niotic_crystal");
        bee("powah/nitro_crystal", List.of(item("powah:crystal_nitro", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_nitro_crystal");
        bee("powah/spirited_crystal", List.of(item("powah:crystal_spirited", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_spirited_crystal");
        bee("powah/uraninite", List.of(item("powah:uraninite_raw", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("powah")), "powah/honeycomb_uraninite");
        bee("raw_materials/aluminum", List.of(tag("c:raw_materials/aluminum", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/aluminum"))), "raw_materials/honeycomb_aluminium");
        bee("raw_materials/bismuth", List.of(tag("c:raw_materials/bismuth", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/bismuth")), tagEmpty("c:dusts/bismuth")), "raw_materials/honeycomb_bismuth");
        bee("raw_materials/copper", List.of(item("minecraft:raw_copper", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "raw_materials/honeycomb_copper");
        bee("raw_materials/gold", List.of(item("minecraft:raw_gold", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "raw_materials/honeycomb_gold");
        bee("raw_materials/iridium", List.of(tag("c:nuggets/iridium", 1, 3, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/iridium"))), "raw_materials/honeycomb_iridium");
        bee("raw_materials/iron", List.of(item("minecraft:raw_iron", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "raw_materials/honeycomb_iron");
        bee("raw_materials/lead", List.of(tag("c:raw_materials/lead", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/lead"))), "raw_materials/honeycomb_leaden");
        bee("raw_materials/mithril", List.of(tag("c:raw_materials/mithril", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("raw_materials/mithril"), not(tagEmpty("c:raw_materials/mithril"))), "raw_materials/honeycomb_mithril");
        bee("raw_materials/netherite", List.of(item("minecraft:netherite_scrap", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "raw_materials/honeycomb_netherite");
        bee("raw_materials/nickel", List.of(tag("c:raw_materials/nickel", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/nickel"))), "raw_materials/honeycomb_nickel");
        bee("raw_materials/osmium", List.of(tag("c:raw_materials/osmium", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/osmium"))), "raw_materials/honeycomb_osmium");
        bee("raw_materials/platinum", List.of(tag("c:raw_materials/platinum", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/platinum"))), "raw_materials/honeycomb_platinum");
        bee("raw_materials/radioactive", List.of(tag("c:raw_materials/uranium", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/uranium"))), "raw_materials/honeycomb_radioactive");
        bee("raw_materials/silver", List.of(tag("c:raw_materials/silver", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/silver"))), "raw_materials/honeycomb_silver");
        bee("raw_materials/tin", List.of(tag("c:raw_materials/tin", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/tin"))), "raw_materials/honeycomb_tin");
        bee("raw_materials/titanium", List.of(tag("c:raw_materials/titanium", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/titanium")), beeExists("raw_materials/titanium")), "raw_materials/honeycomb_titanium");
        bee("raw_materials/tungsten", List.of(tag("c:raw_materials/tungsten", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/tungsten"))), "raw_materials/honeycomb_tungsten");
        bee("raw_materials/zinc", List.of(tag("c:raw_materials/zinc", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:raw_materials/zinc"))), "raw_materials/honeycomb_zinc");
        bee("reactors/anglesite_crystal", List.of(item("bigreactors:anglesite_crystal", 1, 1, 0.5f)), null, List.of(beeExists("reactors/anglesite_crystal"), modLoaded("bigreactors")), "reactors/honeycomb_anglesite_crystal");
        bee("reactors/benitoite_crystal", List.of(item("bigreactors:benitoite_crystal", 1, 1, 0.75f)), null, List.of(beeExists("reactors/benitoite_crystal"), modLoaded("bigreactors")), "reactors/honeycomb_benitoite_crystal");
        bee("refinedstorage/quartz_enriched_iron", List.of(item("refinedstorage:quartz_enriched_iron", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("refinedstorage")), "refinedstorage/honeycomb_quartz_enriched_iron");
        bee("rftools/dimensional_shard", List.of(item("rftoolsbase:dimensionalshard", 1, 1, 0.8f)), null, List.of(beeExists("rftools/dimensional_shard"), modLoaded("rftoolsbase")), "rftoolsbase/honeycomb_dimensional_shard");
        bee("shroom/brown_shroom", List.of(item("minecraft:brown_mushroom", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "shroom/honeycomb_brown_shroom");
        bee("shroom/crimson", List.of(item("minecraft:crimson_fungus", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "shroom/honeycomb_crimson");
        bee("shroom/red_shroom", List.of(item("minecraft:red_mushroom", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "shroom/honeycomb_red_shroom");
        bee("shroom/warped", List.of(item("minecraft:warped_fungus", 1, 1, 0.7f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(), "shroom/honeycomb_warped");
        bee("silentgear/azure_silver", List.of(item("silentgear:raw_azure_silver", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("silentgear/azure_silver"), modLoaded("silentgear")), "silentgear/honeycomb_azure_silver");
        bee("silentgear/crimson_iron", List.of(item("silentgear:raw_crimson_iron", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("silentgear/crimson_iron"), modLoaded("silentgear")), "silentgear/honeycomb_crimson_iron");
        bee("spirit/spirit", List.of(item("spirit:soul_steel_ingot", 1, 1, 0.5f), item("spirit:soul_powder", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("spirit")), "spirit/honeycomb_spirit");
        bee("tconstruct/amethyst_bronze", List.of(tag("c:nuggets/amethyst_bronze", 3, 6, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/amethyst_bronze"))), "tconstruct/honeycomb_amethyst_bronze");
        bee("tconstruct/cobalt", List.of(tag("c:nuggets/cobalt", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/cobalt"))), "tconstruct/honeycomb_cobalt");
        bee("tconstruct/ender_slimy", List.of(tag("c:slimeball/ender", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:slimeball/ender"))), "tconstruct/honeycomb_ender_slimy");
        bee("tconstruct/hepatizon", List.of(tag("c:nuggets/hepatizon", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/hepatizon"))), "tconstruct/honeycomb_hepatizon");
        bee("tconstruct/ichor_slimy", List.of(tag("c:slimeball/ichor", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:slimeball/ichor"))), "tconstruct/honeycomb_ichor_slimy");
        bee("tconstruct/knightslime", List.of(tag("c:nuggets/knightslime", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/knightslime"))), "tconstruct/honeycomb_knightslime");
        bee("tconstruct/manyullyn", List.of(tag("c:nuggets/manyullyn", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/manyullyn"))), "tconstruct/honeycomb_manyullyn");
        bee("tconstruct/pig_iron", List.of(tag("c:nuggets/pig_iron", 2, 4, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/pig_iron"))), "tconstruct/honeycomb_pig_iron");
        bee("tconstruct/queens_slime", List.of(tag("c:nuggets/queens_slime", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/queens_slime"))), "tconstruct/honeycomb_queens_slime");
        bee("tconstruct/rose_gold", List.of(tag("c:nuggets/rose_gold", 3, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/rose_gold"))), "tconstruct/honeycomb_rose_gold");
        bee("tconstruct/sky_slime", List.of(tag("c:slimeball/sky", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:slimeball/sky"))), "tconstruct/honeycomb_sky_slime");
        bee("tconstruct/slimesteel", List.of(tag("c:nuggets/slimesteel", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/slimesteel"))), "tconstruct/honeycomb_slimesteel");
        bee("tconstruct/soulsteel", List.of(tag("c:nuggets/soulsteel", 2, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(not(tagEmpty("c:nuggets/soulsteel"))), "tconstruct/honeycomb_soulsteel");
        bee("tetra/scrapped", List.of(item("tetra:metal_scrap", 1, 1, 0.1f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("tetra")), "tetra/honeycomb_scrapped");
        bee("thermal/basalz", List.of(item("thermal:basalz_powder", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("thermal")), "thermal/honeycomb_basalz");
        bee("thermal/blitz", List.of(item("thermal:blitz_powder", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("thermal")), "thermal/honeycomb_blitz");
        bee("thermal/blizz", List.of(item("thermal:blizz_powder", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("thermal")), "thermal/honeycomb_blizz");
        bee("thermal/destabilized_redstone", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:redstone", 50), List.of(modLoaded("thermal")), "thermal/honeycomb_destabilized_redstone");
        bee("thermal/energized_glowstone", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:glowstone", 50), List.of(modLoaded("thermal")), "thermal/honeycomb_energized_glowstone");
        bee("thermal/resonant_ender", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluidTag("c:ender", 50), List.of(modLoaded("thermal")), "thermal/honeycomb_resonant_ender");
        bee("thermalendergy/melodium", List.of(item("thermalendergy:melodium_dust", 1, 1, 0.5f)), null, List.of(modLoaded("thermalendergy"), beeExists("thermalendergy/melodium")), "thermalendergy/honeycomb_melodium");
        bee("thermalendergy/prismalium", List.of(item("thermalendergy:prismalium_dust", 1, 1, 75f)), null, List.of(modLoaded("thermalendergy"), beeExists("thermalendergy/prismalium")), "thermalendergy/honeycomb_prismalium");
        bee("thermalendergy/stellarium", List.of(item("thermalendergy:stellarium_dust", 1, 1, 25f)), fluid("productivebees:honey", 25), List.of(modLoaded("thermalendergy"), beeExists("thermalendergy/stellarium")), "thermalendergy/honeycomb_stellarium");
        bee("thermal_extra/dragonsteel", List.of(item("thermal_extra:dragonsteel_dust", 1, 1, 0.25f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("thermal_extra/dragonsteel"), modLoaded("thermal_extra")), "thermal_extra/honeycomb_dragonsteel");
        bee("thermal_extra/shellite", List.of(item("thermal_extra:shellite_dust", 1, 1, 0.75f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("thermal_extra/shellite"), modLoaded("thermal_extra")), "thermal_extra/honeycomb_shellite");
        bee("thermal_extra/soul_infused", List.of(item("thermal_extra:soul_infused_dust", 1, 1, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("thermal_extra/soul_infused"), modLoaded("thermal_extra")), "thermal_extra/honeycomb_soul_infused");
        bee("thermal_extra/twinite", List.of(item("thermal_extra:twinite_dust", 1, 1, 0.5f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("thermal_extra/twinite"), modLoaded("thermal_extra")), "thermal_extra/honeycomb_twinite");
        bee("the_bumblezone/royal", List.of(item("productivebees:wax", 1, 1, 1.0f)), fluid("the_bumblezone:royal_jelly_fluid_still", 10), List.of(beeExists("the_bumblezone/royal"), modLoaded("the_bumblezone")), "the_bumblezone/honeycomb_royal");
        bee("tombstone/grave", List.of(item("tombstone:grave_dust", 1, 1, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(modLoaded("tombstone")), "tombstone/honeycomb_grave");
        bee("undergarden/cloggrum", List.of(item("undergarden:raw_cloggrum", 1, 1, 0.8f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("undergarden/cloggrum"), modLoaded("undergarden")), "undergarden/honeycomb_cloggrum");
        bee("undergarden/forgotten", List.of(item("undergarden:forgotten_nugget", 1, 2, 0.2f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("undergarden/forgotten"), modLoaded("undergarden")), "undergarden/honeycomb_forgotten");
        bee("undergarden/froststeel", List.of(item("undergarden:raw_froststeel", 1, 1, 0.6f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("undergarden/froststeel"), modLoaded("undergarden")), "undergarden/honeycomb_froststeel");
        bee("undergarden/regalium", List.of(item("undergarden:regalium_crystal", 1, 1, 0.4f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("undergarden/regalium"), modLoaded("undergarden")), "undergarden/honeycomb_regalium");
        bee("undergarden/utheric", List.of(item("undergarden:utheric_shard", 1, 3, 1.0f), item("productivebees:wax", 1, 1, 1.0f)), null, List.of(beeExists("undergarden/utheric"), modLoaded("undergarden")), "undergarden/honeycomb_utheric");
    }

    private void bee(String beeFullPath, List<JsonObject> outputs, JsonObject fluid, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:centrifuge");
        obj.add("ingredient", componentBee(beeFullPath));
        addOutputs(obj, outputs);
        if (fluid != null) obj.add("fluid", fluid);
        addConditions(obj, conditions);
        entries.add(new Entry(recipePath, obj));
    }

    private void plain(String itemId, List<JsonObject> outputs, JsonObject fluid, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:centrifuge");
        obj.addProperty("ingredient", itemId);
        addOutputs(obj, outputs);
        if (fluid != null) obj.add("fluid", fluid);
        addConditions(obj, conditions);
        entries.add(new Entry(recipePath, obj));
    }

    private static void addOutputs(JsonObject obj, List<JsonObject> outputs) {
        JsonArray arr = new JsonArray();
        for (JsonObject o : outputs) arr.add(o);
        obj.add("outputs", arr);
    }

    private static void addConditions(JsonObject obj, List<JsonObject> conditions) {
        if (conditions.isEmpty()) return;
        JsonArray arr = new JsonArray();
        for (JsonObject c : conditions) arr.add(c);
        obj.add("neoforge:conditions", arr);
    }

    private static JsonObject componentBee(String beeFullPath) {
        JsonObject ing = new JsonObject();
        ing.addProperty("neoforge:ingredient_type", "productivebees:component");
        JsonObject components = new JsonObject();
        components.addProperty("productivebees:bee_type", ProductiveBees.MODID + ":" + beeFullPath);
        ing.add("components", components);
        ing.addProperty("items", "productivebees:configurable_honeycomb");
        return ing;
    }

    private static JsonObject item(String id, int min, int max, float chance) {
        return chancedOutput(toItem(id), min, max, chance);
    }

    private static JsonObject tag(String tagId, int min, int max, float chance) {
        return chancedOutput(toTag(tagId), min, max, chance);
    }

    private static JsonObject chancedOutput(JsonElement item, int min, int max, float chance) {
        JsonObject o = new JsonObject();
        o.add("item", item);
        if (min != 1) o.addProperty("min", min);
        if (max != 1) o.addProperty("max", max);
        if (chance != 1.0f) o.addProperty("chance", chance);
        return o;
    }

    private static JsonElement toItem(String id) {
        return new JsonPrimitive(id);
    }

    private static JsonElement toTag(String tagId) {
        return new JsonPrimitive("#" + tagId);
    }

    private static JsonObject fluid(String fluidId, int amount) {
        JsonObject o = new JsonObject();
        o.addProperty("ingredient", fluidId);
        o.addProperty("amount", amount);
        return o;
    }

    private static JsonObject fluidTag(String tagId, int amount) {
        JsonObject o = new JsonObject();
        o.addProperty("ingredient", "#" + tagId);
        o.addProperty("amount", amount);
        return o;
    }

    private static JsonObject beeExists(String beeFullPath) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivebees:bee_exists");
        c.addProperty("bee", ProductiveBees.MODID + ":" + beeFullPath);
        return c;
    }

    private static JsonObject fluidTagEmpty(String tagId) {
        JsonObject c = new JsonObject();
        c.addProperty("type", "productivelib:fluid_tag_empty");
        c.addProperty("tag", tagId);
        return c;
    }

    private record Entry(String path, JsonObject json) {}
}
