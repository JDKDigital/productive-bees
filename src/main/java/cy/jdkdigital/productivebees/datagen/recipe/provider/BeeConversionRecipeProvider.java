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

/** Emits {@code productivebees:bee_conversion} JSON recipes directly. */
public class BeeConversionRecipeProvider implements DataProvider
{
    private final PackOutput.PathProvider pathProvider;
    private final List<Entry> entries = new ArrayList<>();

    public BeeConversionRecipeProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "recipe");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        entries.clear();
        buildRecipes();
        return CompletableFuture.allOf(entries.stream()
                .map(e -> DataProvider.saveStable(cache, e.json, pathProvider.json(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_conversion/" + e.path))))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "PB Bee Conversion Recipes";
    }

    private void buildRecipes() {
        emit("productivebees:bumble_bee", "productivebees:butcher", "#c:foods/raw_meat", 0.03f, List.of(beeExists("productivebees:butcher"), not(modLoaded("productivemetalworks"))), "butcher_bee");
        emit("minecraft:bee", "productivebees:collector_bee", "minecraft:hopper", 1.0f, List.of(), "collector_bee");
        emit("productivebees:ghostly", "productivebees:creaking", "minecraft:creaking_heart", 1.0f, List.of(beeExists("productivebees:ghostly"), beeExists("productivebees:creaking")), "creaking_bee");
        emit("minecraft:bee", "productivebees:creeper_bee", "minecraft:tnt", 1.0f, List.of(), "creeper_bee");
        emit("productivebees:sweat_bee", "productivebees:frosty", "#productivebees:flowers/frozen", 1.0f, List.of(beeExists("productivebees:frosty")), "frosty_bee");
        emit("productivebees:collector_bee", "productivebees:hoarder_bee", "minecraft:shulker_shell", 1.0f, List.of(), "hoarder_bee");
        emit("productivebees:digger_bee", "productivebees:sculk", "minecraft:sculk", 0.3f, List.of(beeExists("productivebees:sculk")), "sculk_bee");
        emit("productivebees:skeletal", "productivebees:withered", "minecraft:wither_rose", 1.0f, List.of(beeExists("productivebees:skeletal"), beeExists("productivebees:withered")), "withered_bee");
        emit("productivebees:gems/crystalline", "productivebees:actuallyadditions/black_quartz", "actuallyadditions:black_quartz_block", 1.0f, List.of(modLoaded("actuallyadditions"), beeExists("productivebees:gems/crystalline"), beeExists("productivebees:actuallyadditions/black_quartz")), "actuallyadditions/black_quartz_bee");
        emit("productivebees:ad_astra/ostrum", "productivebees:ad_astra/calorite", "#c:storage_blocks/calorite", 1.0f, List.of(not(tagEmpty("c:storage_blocks/calorite")), beeExists("productivebees:ad_astra/ostrum"), beeExists("productivebees:ad_astra/calorite")), "ad_astra/calorite_bee");
        emit("productivebees:alloys/steel", "productivebees:ad_astra/desh", "#c:storage_blocks/desh", 1.0f, List.of(not(tagEmpty("c:storage_blocks/desh")), beeExists("productivebees:alloys/steel"), beeExists("productivebees:ad_astra/desh")), "ad_astra/desh_bee");
        emit("productivebees:ad_astra/desh", "productivebees:ad_astra/ostrum", "#c:storage_blocks/ostrum", 1.0f, List.of(not(tagEmpty("c:storage_blocks/ostrum")), beeExists("productivebees:ad_astra/desh"), beeExists("productivebees:ad_astra/ostrum")), "ad_astra/ostrum_bee");
        emit("productivebees:ae2/spatial", "productivebees:ae2/fluix", "ae2:fluix_pearl", 1.0f, List.of(modLoaded("ae2"), beeExists("productivebees:ae2/spatial"), beeExists("productivebees:ae2/fluix")), "appliedenergistics2/fluix_bee");
        emit("productivebees:ghostly", "productivebees:atm/soul_lava", "allthemodium:soul_lava_bucket", 1.0f, List.of(not(modLoaded("tconstruct")), not(modLoaded("productivemetalworks")), modLoaded("allthemodium"), beeExists("productivebees:ghostly"), beeExists("productivebees:atm/soul_lava")), "atm/soul_lava_bee");
        emit("productivebees:atm/patrick", "productivebees:atm/starry", "allthetweaks:atm_star_block", 1.0f, List.of(modLoaded("allthetweaks"), beeExists("productivebees:atm/patrick"), beeExists("productivebees:atm/starry")), "atm/starry_bee");
        emit("productivebees:gems/diamond", "productivebees:byg/emeraldite", "byg:emeraldite_shards", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:byg/emeraldite"), modLoaded("byg")), "byg/emeraldite_bee");
        emit("productivebees:chemlib/potassium", "productivebees:chemlib/erbium", "chemlib:erbium_metal_block", 1.0f, List.of(beeExists("productivebees:chemlib/erbium")), "chemlib/erbium_bee");
        emit("productivebees:chemlib/potassium", "productivebees:chemlib/iodine", "minecraft:dried_kelp_block", 1.0f, List.of(beeExists("productivebees:chemlib/iodine")), "chemlib/iodine_bee");
        emit("productivebees:raw_materials/iron", "productivebees:chemlib/mercury", "chemlib:mercury_bucket", 1.0f, List.of(beeExists("productivebees:chemlib/mercury")), "chemlib/mercury_bee");
        emit("productivebees:raw_materials/iron", "productivebees:chemlib/ytterbium", "chemlib:ytterbium_metal_block", 1.0f, List.of(beeExists("productivebees:chemlib/ytterbium")), "chemlib/ytterbium_bee");
        emit("productivebees:chemlib/potassium", "productivebees:chemlib/yttrium", "chemlib:yttrium_metal_block", 1.0f, List.of(beeExists("productivebees:chemlib/yttrium")), "chemlib/yttrium_bee");
        emit("productivebees:experience", "productivebees:create_enchantment_industry/super_experience", "create_enchantment_industry:super_enchanting_template", 1.0f, List.of(beeExists("productivebees:experience"), beeExists("productivebees:create_enchantment_industry/super_experience")), "create_enchantment_industry/super_experience_bee");
        emit("minecraft:bee", "productivebees:dusts/salty", "#c:dusts/salt", 1.0f, List.of(not(tagEmpty("c:dusts/salt")), beeExists("productivebees:dusts/salty")), "dusts/salty_bee");
        emit("productivebees:elementalcraft/inert_crystal", "productivebees:elementalcraft/air_crystal", "elementalcraft:aircrystal_block", 1.0f, List.of(modLoaded("elementalcraft"), beeExists("productivebees:elementalcraft/inert_crystal"), beeExists("productivebees:elementalcraft/air_crystal")), "elementalcraft/air_crystal_bee");
        emit("productivebees:elementalcraft/inert_crystal", "productivebees:elementalcraft/earth_crystal", "elementalcraft:earthcrystal_block", 1.0f, List.of(modLoaded("elementalcraft"), beeExists("productivebees:elementalcraft/inert_crystal"), beeExists("productivebees:elementalcraft/earth_crystal")), "elementalcraft/earth_crystal_bee");
        emit("productivebees:elementalcraft/inert_crystal", "productivebees:elementalcraft/fire_crystal", "elementalcraft:firecrystal_block", 1.0f, List.of(modLoaded("elementalcraft"), beeExists("productivebees:elementalcraft/inert_crystal"), beeExists("productivebees:elementalcraft/fire_crystal")), "elementalcraft/fire_crystal_bee");
        emit("productivebees:gems/diamond", "productivebees:elementalcraft/inert_crystal", "elementalcraft:inert_crystal_block", 1.0f, List.of(modLoaded("elementalcraft"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:elementalcraft/inert_crystal")), "elementalcraft/inert_crystal_bee");
        emit("productivebees:elementalcraft/inert_crystal", "productivebees:elementalcraft/water_crystal", "elementalcraft:watercrystal_block", 1.0f, List.of(modLoaded("elementalcraft"), beeExists("productivebees:elementalcraft/inert_crystal"), beeExists("productivebees:elementalcraft/water_crystal")), "elementalcraft/water_crystal_bee");
        emit("productivebees:coal", "productivebees:eternal_starlight/amaramber", "eternal_starlight:raw_amaramber", 1.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:coal"), beeExists("productivebees:eternal_starlight/amaramber")), "eternal_starlight/amaramber_bee");
        emit("productivebees:frosty", "productivebees:eternal_starlight/glacite", "eternal_starlight:glacite", 1.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:frosty"), beeExists("productivebees:eternal_starlight/glacite")), "eternal_starlight/glacite_bee");
        emit("productivebees:obsidian", "productivebees:eternal_starlight/malarite", "eternal_starlight:malarite", 1.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:obsidian"), beeExists("productivebees:eternal_starlight/malarite")), "eternal_starlight/malarite_bee");
        emit("productivebees:lava", "productivebees:eternal_starlight/starcore", "eternal_starlight:starcore", 1.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:lava"), beeExists("productivebees:eternal_starlight/starcore")), "eternal_starlight/starcore_bee");
        emit("productivebees:gems/diamond", "productivebees:eternal_starlight/starlit_diamond", "eternal_starlight:starlit_diamond", 1.0f, List.of(modLoaded("eternal_starlight"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:eternal_starlight/starlit_diamond")), "eternal_starlight/starlit_diamond_bee");
        emit("productivebees:gems/diamond", "productivebees:evilcraft/dark_gem", "evilcraft:dark_block", 1.0f, List.of(modLoaded("evilcraft"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:evilcraft/dark_gem")), "evilcraft/dark_gem_bee");
        emit("productivebees:sugarbag", "productivebees:fluids/chocolate", "minecraft:cocoa_beans", 1.0f, List.of(beeExists("productivebees:sugarbag"), beeExists("productivebees:fluids/chocolate")), "fluids/chocolate_bee");
        emit("productivebees:magmatic", "productivebees:lava", "minecraft:lava_bucket", 1.0f, List.of(beeExists("productivebees:magmatic"), beeExists("productivebees:lava")), "fluids/lava_bee");
        emit("productivebees:leafcutter_bee", "productivebees:fluids/tea", "minecraft:water_bucket", 1.0f, List.of(beeExists("productivebees:fluids/tea")), "fluids/tea_bee");
        emit("productivebees:gems/diamond", "productivebees:forbidden_arcanus/arcane_crystal", "forbidden_arcanus:arcane_crystal_block", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:forbidden_arcanus/arcane_crystal"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/arcane_crystal_bee");
        emit("productivebees:forbidden_arcanus/arcane_crystal", "productivebees:forbidden_arcanus/rune", "forbidden_arcanus:rune_block", 1.0f, List.of(beeExists("productivebees:forbidden_arcanus/arcane_crystal"), beeExists("productivebees:forbidden_arcanus/rune"), modLoaded("forbidden_arcanus")), "forbidden_arcanus/rune_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/agate", "#productivebees:flowers/agate", 1.0f, List.of(not(tagEmpty("productivebees:flowers/agate")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/agate")), "gems/agate_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/alexandrite", "#productivebees:flowers/alexandrite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/alexandrite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/alexandrite")), "gems/alexandrite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/amber_gem", "#productivebees:flowers/amber", 1.0f, List.of(not(tagEmpty("productivebees:flowers/amber")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/amber_gem")), "gems/amber_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/amethyst", "#productivebees:flowers/amethyst", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/amethyst")), "gems/amethyst_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/ametrine", "#productivebees:flowers/ametrine", 1.0f, List.of(not(tagEmpty("productivebees:flowers/ametrine")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/ametrine")), "gems/ametrine_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/ammolite", "#productivebees:flowers/ammolite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/ammolite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/ammolite")), "gems/ammolite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/apatite", "#productivebees:flowers/apatite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/apatite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/apatite")), "gems/apatite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/aquamarine", "#productivebees:flowers/aquamarine", 1.0f, List.of(not(tagEmpty("productivebees:flowers/aquamarine")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/aquamarine")), "gems/aquamarine_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/benitoite", "#productivebees:flowers/benitoite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/benitoite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/benitoite")), "gems/benitoite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/black_diamond", "#productivebees:flowers/black_diamond", 1.0f, List.of(not(tagEmpty("productivebees:flowers/black_diamond")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/black_diamond")), "gems/black_diamond_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/black_opal", "#productivebees:flowers/black_opal", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/black_opal")), "gems/black_opal_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/carnelian", "#productivebees:flowers/carnelian", 1.0f, List.of(not(tagEmpty("productivebees:flowers/carnelian")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/carnelian")), "gems/carnelian_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/cats_eye", "#productivebees:flowers/cats_eye", 1.0f, List.of(not(tagEmpty("productivebees:flowers/cats_eye")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/cats_eye")), "gems/cats_eye_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/chrysoprase", "#productivebees:flowers/chrysoprase", 1.0f, List.of(not(tagEmpty("productivebees:flowers/chrysoprase")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/chrysoprase")), "gems/chrysoprase_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/citrine", "#productivebees:flowers/citrine", 1.0f, List.of(not(tagEmpty("productivebees:flowers/citrine")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/citrine")), "gems/citrine_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/coral", "#productivebees:flowers/coral", 1.0f, List.of(not(tagEmpty("productivebees:flowers/coral")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/coral")), "gems/coral_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/euclase", "#productivebees:flowers/euclase", 1.0f, List.of(not(tagEmpty("productivebees:flowers/euclase")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/euclase")), "gems/euclase_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/fluorite", "#productivebees:flowers/fluorite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/fluorite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/fluorite")), "gems/fluorite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/garnet", "#productivebees:flowers/garnet", 1.0f, List.of(not(tagEmpty("productivebees:flowers/garnet")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/garnet")), "gems/garnet_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/green_sapphire", "#productivebees:flowers/green_sapphire", 1.0f, List.of(not(tagEmpty("productivebees:flowers/green_sapphire")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/green_sapphire")), "gems/green_sapphire_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/heliodor", "#productivebees:flowers/heliodor", 1.0f, List.of(not(tagEmpty("productivebees:flowers/heliodor")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/heliodor")), "gems/heliodor_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/iolite", "#productivebees:flowers/iolite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/iolite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/iolite")), "gems/iolite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/jade", "#productivebees:flowers/jade", 1.0f, List.of(not(tagEmpty("productivebees:flowers/jade")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/jade")), "gems/jade_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/jasper", "#productivebees:flowers/jasper", 1.0f, List.of(not(tagEmpty("productivebees:flowers/jasper")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/jasper")), "gems/jasper_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/kunzite", "#productivebees:flowers/kunzite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/kunzite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/kunzite")), "gems/kunzite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/kyanite", "#productivebees:flowers/kyanite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/kyanite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/kyanite")), "gems/kyanite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/lepidolite", "#productivebees:flowers/lepidolite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/lepidolite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/lepidolite")), "gems/lepidolite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/malachite", "#productivebees:flowers/malachite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/malachite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/malachite")), "gems/malachite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/moldavite", "#productivebees:flowers/moldavite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/moldavite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/moldavite")), "gems/moldavite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/moonstone", "#productivebees:flowers/moonstone", 1.0f, List.of(not(tagEmpty("productivebees:flowers/moonstone")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/moonstone")), "gems/moonstone_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/morganite", "#productivebees:flowers/morganite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/morganite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/morganite")), "gems/morganite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/onyx", "#productivebees:flowers/onyx", 1.0f, List.of(not(tagEmpty("productivebees:flowers/onyx")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/onyx")), "gems/onyx_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/opal", "#productivebees:flowers/opal", 1.0f, List.of(not(tagEmpty("productivebees:flowers/opal")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/opal")), "gems/opal_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/pearl", "#productivebees:flowers/pearl", 1.0f, List.of(not(tagEmpty("productivebees:flowers/pearl")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/pearl")), "gems/pearl_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/peridot", "#productivebees:flowers/peridot", 1.0f, List.of(not(tagEmpty("productivebees:flowers/peridot")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/peridot")), "gems/peridot_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/phosphophyllite", "#productivebees:flowers/phosphophyllite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/phosphophyllite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/phosphophyllite")), "gems/phosphophyllite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/pyrope", "#productivebees:flowers/pyrope", 1.0f, List.of(not(tagEmpty("productivebees:flowers/pyrope")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/pyrope")), "gems/pyrope_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/rose_quartz", "#productivebees:flowers/rose_quartz", 1.0f, List.of(not(tagEmpty("productivebees:flowers/rose_quartz")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/rose_quartz")), "gems/rose_quartz_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/ruby", "#productivebees:flowers/ruby", 1.0f, List.of(not(tagEmpty("productivebees:flowers/ruby")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/ruby")), "gems/ruby_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/sapphire", "#productivebees:flowers/sapphire", 1.0f, List.of(not(tagEmpty("productivebees:flowers/sapphire")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/sapphire")), "gems/sapphire_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/sodalite", "#productivebees:flowers/sodalite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/sodalite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/sodalite")), "gems/sodalite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/spinel", "#productivebees:flowers/spinel", 1.0f, List.of(not(tagEmpty("productivebees:flowers/spinel")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/spinel")), "gems/spinel_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/sunstone", "#productivebees:flowers/sunstone", 1.0f, List.of(not(tagEmpty("productivebees:flowers/sunstone")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/sunstone")), "gems/sunstone_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/tanzanite", "#productivebees:flowers/tanzanite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/tanzanite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/tanzanite")), "gems/tanzanite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/tektite", "#productivebees:flowers/tektite", 1.0f, List.of(not(tagEmpty("productivebees:flowers/tektite")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/tektite")), "gems/tektite_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/topaz", "#productivebees:flowers/topaz", 1.0f, List.of(not(tagEmpty("productivebees:flowers/topaz")), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/topaz")), "gems/topaz_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/tourmaline", "gemsnjewels:tourmaline_block", 1.0f, List.of(modLoaded("gemsnjewels"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/tourmaline")), "gems/tourmaline_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/turquoise", "#productivebees:flowers/turquoise", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/turquoise")), "gems/turquoise_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/white_diamond", "#productivebees:flowers/white_diamond", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/white_diamond")), "gems/white_diamond_bee");
        emit("productivebees:gems/diamond", "productivebees:gems/zircon", "#productivebees:flowers/zircon", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gems/zircon")), "gems/zircon_bee");
        emit("productivebees:gtceu/naquadah", "productivebees:gtceu/neutronium", "gtceu:nan_certificate", 1.0f, List.of(beeExists("productivebees:gtceu/naquadah"), beeExists("productivebees:gtceu/neutronium"), modLoaded("gtceu")), "gtceu/neutronium_bee");
        emit("productivebees:gems/diamond", "productivebees:gtceu/realgar", "#c:storage_blocks/realgar", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:gtceu/realgar"), modLoaded("gtceu")), "gtceu/realgar_bee");
        emit("productivebees:raw_materials/gold", "productivebees:tconstruct/rose_gold", "minecraft:copper_block", 1.0f, List.of(beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:tconstruct/rose_gold"), not(modLoaded("tconstruct"))), "gtceu/rose_gold_bee");
        emit("productivebees:industrialforegoing/pink_slimy", "productivebees:industrialforegoing/ether_gas", "industrialforegoing:ether_gas_bucket", 1.0f, List.of(beeExists("productivebees:industrialforegoing/ether_gas")), "industrial-foregoing/ether_gas_bee");
        emit("productivebees:slimy", "productivebees:industrialforegoing/pink_slimy", "industrialforegoing:pink_slime", 1.0f, List.of(beeExists("productivebees:slimy"), beeExists("productivebees:industrialforegoing/pink_slimy")), "industrial-foregoing/pink_slimy_bee");
        emit("productivebees:raw_materials/gold", "productivebees:justdirethings/blazegold", "justdirethings:gooblock_tier2", 1.0f, List.of(modLoaded("justdirethings"), beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:justdirethings/blazegold")), "justdirethings/blazegold_bee");
        emit("productivebees:gems/diamond", "productivebees:justdirethings/celestigem", "justdirethings:gooblock_tier3", 1.0f, List.of(modLoaded("justdirethings"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:justdirethings/celestigem")), "justdirethings/celestigem_bee");
        emit("productivebees:raw_materials/netherite", "productivebees:justdirethings/eclipsealloy", "justdirethings:gooblock_tier4", 1.0f, List.of(modLoaded("justdirethings"), beeExists("productivebees:raw_materials/netherite"), beeExists("productivebees:justdirethings/eclipsealloy")), "justdirethings/eclipsealloy_bee");
        emit("productivebees:raw_materials/iron", "productivebees:justdirethings/ferricore", "justdirethings:gooblock_tier1", 1.0f, List.of(modLoaded("justdirethings"), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:justdirethings/ferricore")), "justdirethings/ferricore_bee");
        emit("productivebees:wanna", "productivebees:l2hostility/chaotic", "l2hostility:hostility_orb", 30f, List.of(beeExists("productivebees:wanna"), beeExists("productivebees:l2hostility/chaotic"), modLoaded("l2hostility")), "l2hostility/chaotic_bee");
        emit("productivebees:l2hostility/chaotic", "productivebees:l2hostility/miracle", "l2hostility:miracle_powder", 10f, List.of(beeExists("productivebees:l2hostility/miracle"), beeExists("productivebees:l2hostility/chaotic"), modLoaded("l2hostility")), "l2hostility/miracle_bee");
        emit("productivebees:resin_bee", "productivebees:materials/sticky_resin", "#c:buckets/honey", 1.0f, List.of(beeExists("productivebees:materials/sticky_resin")), "materials/sticky_resin_bee");
        emit("productivebees:fluids/water", "productivebees:chemlib/lithium", "mekanism:lithium_bucket", 1.0f, List.of(modLoaded("mekanism"), beeExists("productivebees:fluids/water"), beeExists("productivebees:chemlib/lithium")), "mekanism/lithium_bee");
        emit("productivebees:dusts/sulfur", "productivebees:modern_industrialization/monazite", "modern_industrialization:monazite_block", 1.0f, List.of(modLoaded("modern_industrialization"), beeExists("productivebees:dusts/sulfur"), beeExists("productivebees:modern_industrialization/monazite")), "modern_industrialization/monazite_bee");
        emit("productivebees:gems/crystalline", "productivebees:mysticalagriculture/prosperity", "mysticalagriculture:prosperity_block", 1.0f, List.of(modLoaded("mysticalagriculture"), beeExists("productivebees:gems/crystalline"), beeExists("productivebees:mysticalagriculture/prosperity")), "mysticalagriculture/prosperity_bee");
        emit("productivebees:ghostly", "productivebees:mysticalagriculture/soulium", "mysticalagriculture:soulium_dagger", 1.0f, List.of(modLoaded("mysticalagriculture"), beeExists("productivebees:ghostly"), beeExists("productivebees:mysticalagriculture/soulium")), "mysticalagriculture/soulium_bee");
        emit("productivebees:lava", "productivebees:oritech/sheol_fire", "oritech:still_sheol_fire_bucket", 0.5f, List.of(modLoaded("oritech"), beeExists("productivebees:lava"), beeExists("productivebees:oritech/sheol_fire")), "oritech/sheol_fire_bee");
        emit("productivebees:lava", "productivebees:oritech/strange_matter", "oritech:still_strange_matter_bucket", 0.2f, List.of(modLoaded("oritech"), beeExists("productivebees:lava"), beeExists("productivebees:oritech/strange_matter")), "oritech/strange_matter_bee");
        emit("productivebees:lava", "productivebees:oritech/sulfuric_acid", "oritech:still_sulfuric_acid_bucket", 0.8f, List.of(modLoaded("oritech"), beeExists("productivebees:lava"), beeExists("productivebees:oritech/sulfuric_acid")), "oritech/sulfuric_acid_bee");
        emit("productivebees:raw_materials/radioactive", "productivebees:oritech/uranite_crystal", "oritech:plutonium_dust", 1f, List.of(modLoaded("oritech"), beeExists("productivebees:raw_materials/radioactive"), beeExists("productivebees:oritech/uranite_crystal")), "oritech/uranite_crystal_bee");
        emit("productivebees:neon_cuckoo_bee", "productivebees:pokecube/cosmic_dust", "#c:gems/cosmicdust", 1.0f, List.of(beeExists("productivebees:pokecube/cosmic_dust")), "pokecube/cosmic_dust_bee");
        emit("productivebees:gems/diamond", "productivebees:pokecube/spectrum", "#c:gems/spectrum", 1.0f, List.of(beeExists("productivebees:pokecube/spectrum")), "pokecube/spectrum_bee");
        emit("productivebees:gems/diamond", "productivebees:raw_materials/netherite", "minecraft:netherite_block", 1.0f, List.of(beeExists("productivebees:gems/diamond"), beeExists("productivebees:raw_materials/netherite")), "raw_materials/netherite_bee");
        emit("productivebees:draconic", "productivebees:reactors/anglesite_crystal", "bigreactors:anglesite_ore", 1f, List.of(modLoaded("bigreactors"), beeExists("productivebees:draconic"), beeExists("productivebees:reactors/anglesite_crystal")), "reactors/anglesite_crystal_bee");
        emit("productivebees:dusts/blazing", "productivebees:reactors/benitoite_crystal", "bigreactors:benitoite_ore", 1f, List.of(modLoaded("bigreactors"), beeExists("productivebees:dusts/blazing"), beeExists("productivebees:reactors/benitoite_crystal")), "reactors/benitoite_crystal_bee");
        emit("productivebees:coal", "productivebees:gtceu/graphite", "#c:storage_blocks/graphite", 1.0f, List.of(beeExists("productivebees:coal"), beeExists("productivebees:gtceu/graphite"), not(modLoaded("gtceu"))), "reactors/graphite_bee");
        emit("productivebees:gems/diamond", "productivebees:rftools/dimensional_shard", "rftoolsbase:infused_diamond", 1.0f, List.of(modLoaded("rftoolsbase"), beeExists("productivebees:gems/diamond"), beeExists("productivebees:rftools/dimensional_shard")), "rftoolsbase/dimensional_shard_bee");
        emit("productivebees:shroom/crimson", "productivebees:shroom/brown_shroom", "minecraft:brown_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/crimson"), beeExists("productivebees:shroom/brown_shroom")), "shroom/brown_from_crimson_bee");
        emit("productivebees:shroom/red_shroom", "productivebees:shroom/brown_shroom", "minecraft:brown_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/red_shroom"), beeExists("productivebees:shroom/brown_shroom")), "shroom/brown_from_red_bee");
        emit("productivebees:shroom/warped", "productivebees:shroom/brown_shroom", "minecraft:brown_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/warped"), beeExists("productivebees:shroom/brown_shroom")), "shroom/brown_from_warped_bee");
        emit("productivebees:shroom/brown_shroom", "productivebees:shroom/crimson", "minecraft:crimson_fungus", 1.0f, List.of(beeExists("productivebees:shroom/brown_shroom"), beeExists("productivebees:shroom/crimson")), "shroom/crimson_from_brown_bee");
        emit("productivebees:shroom/red_shroom", "productivebees:shroom/crimson", "minecraft:crimson_fungus", 1.0f, List.of(beeExists("productivebees:shroom/red_shroom"), beeExists("productivebees:shroom/crimson")), "shroom/crimson_from_red_bee");
        emit("productivebees:shroom/warped", "productivebees:shroom/crimson", "minecraft:crimson_fungus", 1.0f, List.of(beeExists("productivebees:shroom/warped"), beeExists("productivebees:shroom/crimson")), "shroom/crimson_from_warped_bee");
        emit("productivebees:shroom/brown_shroom", "productivebees:shroom/red_shroom", "minecraft:red_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/brown_shroom"), beeExists("productivebees:shroom/red_shroom")), "shroom/red_from_brown_bee");
        emit("productivebees:shroom/crimson", "productivebees:shroom/red_shroom", "minecraft:red_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/crimson"), beeExists("productivebees:shroom/red_shroom")), "shroom/red_from_crimson_bee");
        emit("productivebees:shroom/warped", "productivebees:shroom/red_shroom", "minecraft:red_mushroom", 1.0f, List.of(beeExists("productivebees:shroom/warped"), beeExists("productivebees:shroom/red_shroom")), "shroom/red_from_warped_bee");
        emit("productivebees:shroom/brown_shroom", "productivebees:shroom/warped", "minecraft:warped_fungus", 1.0f, List.of(beeExists("productivebees:shroom/brown_shroom"), beeExists("productivebees:shroom/warped")), "shroom/warped_from_brown_bee");
        emit("productivebees:shroom/crimson", "productivebees:shroom/warped", "minecraft:warped_fungus", 1.0f, List.of(beeExists("productivebees:shroom/crimson"), beeExists("productivebees:shroom/warped")), "shroom/warped_from_crimson_bee");
        emit("productivebees:shroom/red_shroom", "productivebees:shroom/warped", "minecraft:warped_fungus", 1.0f, List.of(beeExists("productivebees:shroom/red_shroom"), beeExists("productivebees:shroom/warped")), "shroom/warped_from_red_bee");
        emit("productivebees:quarry_bee", "productivebees:tetra/geode", "tetra:pristine_amethyst", 1.0f, List.of(modLoaded("tetra"), beeExists("productivebees:tetra/geode")), "tetra/geode_bee_from_pristine_amethyst");
        emit("productivebees:quarry_bee", "productivebees:tetra/geode", "tetra:pristine_diamond", 1.0f, List.of(modLoaded("tetra"), beeExists("productivebees:tetra/geode")), "tetra/geode_bee_from_pristine_diamond");
        emit("productivebees:quarry_bee", "productivebees:tetra/geode", "tetra:pristine_emerald", 1.0f, List.of(modLoaded("tetra"), beeExists("productivebees:tetra/geode")), "tetra/geode_bee_from_pristine_emerald");
        emit("productivebees:quarry_bee", "productivebees:tetra/geode", "tetra:pristine_lapis", 1.0f, List.of(modLoaded("tetra"), beeExists("productivebees:tetra/geode")), "tetra/geode_bee_from_pristine_lapis");
        emit("productivebees:raw_materials/iron", "productivebees:tetra/scrapped", "tetra:planar_stabilizer", 1.0f, List.of(modLoaded("tetra"), beeExists("productivebees:tetra/scrapped")), "tetra/scrapped_bee_from_iron");
        emit("productivebees:raw_materials/iron", "productivebees:undergarden/cloggrum", "#c:storage_blocks/cloggrum", 1.0f, List.of(not(tagEmpty("c:storage_blocks/cloggrum")), beeExists("productivebees:raw_materials/iron"), beeExists("productivebees:undergarden/cloggrum")), "undergarden/cloggrum_bee");
        emit("productivebees:raw_materials/gold", "productivebees:undergarden/regalium", "undergarden:music_disc_gloomper_anthem", 1.0f, List.of(modLoaded("undergarden"), beeExists("productivebees:raw_materials/gold"), beeExists("productivebees:undergarden/regalium")), "undergarden/regalium_bee");// total: 129 recipes
    }

    private void emit(String sourceBee, String resultBee, String itemId, float chance, List<JsonObject> conditions, String recipePath) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "productivebees:bee_conversion");
        obj.addProperty("source", sourceBee);
        obj.addProperty("result", resultBee);
        obj.addProperty("item", itemId);
        if (chance != 1.0f) obj.addProperty("chance", chance);
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
