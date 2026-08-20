package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.util.LangUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnglishLangProvider extends LanguageProvider
{
    public EnglishLangProvider(PackOutput output) {
        super(output, ProductiveBees.MODID, "en_us");
    }

    private static final Map<String, String> BEE_NAME_OVERRIDES = Map.ofEntries(
            Map.entry("aluminum", "Aluminium Bee"),
            Map.entry("anglesite_crystal", "Anglesite Bee"),
            Map.entry("arcane_crystal", "Arcanus Bee"),
            Map.entry("basalz", "BazBee"),
            Map.entry("beebee", "BeeBee"),
            Map.entry("benitoite_crystal", "Benitoite Bee"),
            Map.entry("blazing", "BlazBee"),
            Map.entry("blitz", "BitzBee"),
            Map.entry("blizz", "BizBee"),
            Map.entry("breeze", "BreezBee"),
            Map.entry("brown_shroom", "Brown Shroombee"),
            Map.entry("chaotic", "Chaos Bee"),
            Map.entry("cheese", "CheezyB"),
            Map.entry("chocolate", "Choco Bee"),
            Map.entry("cosmic_dust", "Cosmic Bee"),
            Map.entry("crimson", "Crimson Shroombee"),
            Map.entry("depth_ingot", "Bee of the Depths"),
            Map.entry("dimensional_shard", "Dimensional Bee"),
            Map.entry("eclipsealloy", "Eclipse Alloy Bee"),
            Map.entry("etherium_ore", "Etherium Bee"),
            Map.entry("fbi", "F. Bee I."),
            Map.entry("flux", "Flux Dust Bee"),
            Map.entry("grave", "Grave's Bee"),
            Map.entry("gregstar", "GregStar Bee"),
            Map.entry("hop_graphite", "HOP Graphite Bee"),
            Map.entry("infinity", "Bee of Infinity"),
            Map.entry("kamikaz", "KamikazBee"),
            Map.entry("netherite", "Ancient Bee"),
            Map.entry("pepto_bismol", "Pepto Beesmol"),
            Map.entry("phil", "Philbee"),
            Map.entry("prosperity", "ProsperiBee"),
            Map.entry("red_shroom", "Red Shroombee"),
            Map.entry("ribbeet", "Ribbeet"),
            Map.entry("royal", "Her Royal Beeness"),
            Map.entry("ruby", "RuBee"),
            Map.entry("sky_ingot", "Bee of the Sky"),
            Map.entry("soul_shard", "Soul Bee"),
            Map.entry("time_crystal", "Time Bee"),
            Map.entry("villager", "Mistake"),
            Map.entry("wanna", "WannaBee"),
            Map.entry("warped", "Warped Shroombee"),
            Map.entry("zombie", "ZomBee")
    );

    /** Display-name overrides for bee entities registered via {@link ModEntities#HIVE_BEES} / {@link ModEntities#SOLITARY_BEES}. */
    private static final Map<String, String> BEE_ENTITY_OVERRIDES = Map.ofEntries(
            Map.entry("configurable_bee", "Productive Bee"),
            Map.entry("creeper_bee", "CreeBee"),
            Map.entry("cupid_bee", "CuBee"),
            Map.entry("yellow_black_carpenter_bee", "Yellow Carpenter Bee")
    );

    /** Block display-name overrides for paths where {@link #deriveBlockName} doesn't produce the desired text. */
    private static final Map<String, String> BLOCK_NAME_OVERRIDES = Map.ofEntries(
            Map.entry("coarse_dirt_nest", "Dirt Nest"),
            Map.entry("comb_configurable", "%s Comb Block"),
            Map.entry("configurable_comb", "Comb Block"),
            Map.entry("feeder", "Feeding Slab"),
            Map.entry("feeder_double", "Double Feeding Slab"),
            Map.entry("incubator", "BaBee Incubator"),
            Map.entry("jar_oak", "Bee Jar"),
            Map.entry("nether_quartz_nest", "Quartz Nest"),
            Map.entry("quartz_netherrack", "Quartz in Netherrack"),
            Map.entry("sugar_cane_nest", "Reed Nest")
    );

    /** Third-party mod prefixes that appear inside hive/expansion block paths (e.g. {@code advanced_byg_ebony_beehive}) but are dropped from display names. */
    private static final List<String> KNOWN_HIVE_MOD_PREFIXES = List.of(
            "regions_unexplored", "upgrade_aquatic", "biomesoplenty", "atmospheric",
            "environmental", "endergetic", "autumnity", "byg", "quark"
    );

    /** Apply the same path-stripping logic NeoForge/Utilitarian-style auto-naming uses, plus PB-specific patterns (canvas, wood-nest, comb-X, etc.). */
    private static String deriveBlockName(String path) {
        if (path.endsWith("_canvas_beehive") || path.equals("advanced_canvas_beehive")) {
            return "Advanced Canvas Beehive";
        }
        if (path.startsWith("expansion_box_") && path.endsWith("_canvas")) {
            return "Canvas Expansion Box";
        }
        if (path.endsWith("_wood_nest")) {
            return LangUtil.capName(path.substring(0, path.length() - "_wood_nest".length())) + " Nest";
        }
        if (path.startsWith("advanced_") && path.endsWith("_beehive")) {
            String middle = stripHiveModPrefix(path.substring("advanced_".length(), path.length() - "_beehive".length()));
            return middle.isEmpty() ? "Advanced Beehive" : "Advanced " + LangUtil.capName(middle) + " Beehive";
        }
        if (path.startsWith("expansion_box_")) {
            String rest = stripHiveModPrefix(path.substring("expansion_box_".length()));
            return rest.isEmpty() ? "Expansion Box" : LangUtil.capName(rest) + " Expansion Box";
        }
        if (path.startsWith("comb_")) {
            return LangUtil.capName(path.substring("comb_".length())) + " Comb Block";
        }
        return LangUtil.capName(path);
    }

    private static String stripHiveModPrefix(String s) {
        for (String prefix : KNOWN_HIVE_MOD_PREFIXES) {
            if (s.startsWith(prefix + "_")) {
                return s.substring(prefix.length() + 1);
            }
        }
        return s;
    }

    @Override
    protected void addTranslations() {
        // Derived block names from the BLOCKS DeferredRegister (with overrides + pattern-derived names via deriveBlockName).
        Set<String> emittedBlockPaths = new HashSet<>();
        for (DeferredHolder<Block, ?> holder : ProductiveBees.BLOCKS.getEntries()) {
            String path = holder.getId().getPath();
            String display = BLOCK_NAME_OVERRIDES.getOrDefault(path, deriveBlockName(path));
            add(holder.get(), display);
            emittedBlockPaths.add(path);
        }
        // Hive/expansion variants from compat mods register conditionally, so their blocks aren't
        // present in datagen. Drive their lang entries from HIVELIST so all variants get names.
        // Canvas variants are registered via hiveStyles (always under PB namespace) and picked up by the BLOCKS loop above.
        ModBlocks.HIVELIST.forEach((modid, woods) -> woods.forEach((wood, type) -> {
            String name = modid.equals(ProductiveBees.MODID) ? wood : modid + "_" + wood;
            for (String path : List.of("advanced_" + name + "_beehive", "expansion_box_" + name)) {
                if (emittedBlockPaths.add(path)) {
                    String display = BLOCK_NAME_OVERRIDES.getOrDefault(path, deriveBlockName(path));
                    add("block.productivebees." + path, display);
                }
            }
        }));
        // Virtual block lang keys (no Block registered, referenced only as translation strings).
        add("block.productivebees.comb_configurable", "%s Comb Block");
        add("block.productivebees.feeder_double", "Double Feeding Slab");

        // Derived entity + spawn-egg names from ModEntities (HIVE_BEES + SOLITARY_BEES).
        for (DeferredHolder<EntityType<?>, ?> holder : List.of(ModEntities.HIVE_BEES, ModEntities.SOLITARY_BEES)
                .stream().flatMap(reg -> reg.getEntries().stream()).toList()) {
            String name = holder.getId().getPath();
            String display = BEE_ENTITY_OVERRIDES.getOrDefault(name, LangUtil.capName(name));
            add("entity.productivebees." + name, display);
            add("item.productivebees.spawn_egg_" + name, display + " Spawn Egg");
        }

        // Derived bee names from BeeProvider config (with overrides for non-standard display names).
        Set<String> seenSimple = new HashSet<>();
        for (BeeProvider.BeeConfig config : BeeProvider.uniqueConfigs()) {
            String fullPath = config.name();
            int slash = fullPath.lastIndexOf('/');
            String simple = slash >= 0 ? fullPath.substring(slash + 1) : fullPath;
            if (!seenSimple.add(simple)) continue;
            String display = BEE_NAME_OVERRIDES.getOrDefault(simple, LangUtil.capName(simple) + " Bee");
            add(BeeHelper.beeNameKey(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, fullPath)), display);
        }

        // Non-config PB bee entities + special-case keys (configurable, bee).
        add("entity.productivebees.bee_bee", "Bee");

        add("advancements.husbandry.advanced_beehive.description", "Craft an advanced beehive");
        add("advancements.husbandry.advanced_beehive.title", "Gotta work, bees!");
        add("advancements.husbandry.bee_cage.description", "Craft a Bee Cage");
        add("advancements.husbandry.bee_cage.title", "Careful does it.");
        add("advancements.husbandry.breed_all_productive_bees.description", "Breed all the vanilla typed productive bees");
        add("advancements.husbandry.breed_all_productive_bees.title", "They call me Melittologist");
        add("advancements.husbandry.breed_iron_bee.description", "Breed an iron bee");
        add("advancements.husbandry.breed_iron_bee.title", "It's precious!");
        add("advancements.husbandry.bumblebee_rider.description", "It's not the destination, it's the journBee! Ride a Bumble Bee.");
        add("advancements.husbandry.bumblebee_rider.title", "Bumbling along");
        add("advancements.husbandry.calm_bee.description", "Use a Honey Treat on an angry bee to calm it down.");
        add("advancements.husbandry.calm_bee.title", "Clear Their Head.");
        add("advancements.husbandry.catch_any_bee.description", "Catch a bee in a bee cage");
        add("advancements.husbandry.catch_any_bee.title", "Catchy");
        add("advancements.husbandry.catch_crystalline_bee.description", "Catch a Crystalline Bee from the Nether");
        add("advancements.husbandry.catch_crystalline_bee.title", "Important Foundations");
        add("advancements.husbandry.consume_sugarbag_comb.description", "Eat a Sugarbag Honeycomb");
        add("advancements.husbandry.consume_sugarbag_comb.title", "Honey Honey Yum Yum!");
        add("advancements.husbandry.convert_egg.description", "Activate an Inactive Dragon Egg using Dragon Breath");
        add("advancements.husbandry.convert_egg.title", "Good thing it can't hatch");
        add("advancements.husbandry.dragon_egg_hive.description", "Craft a Dragon Egg Hive");
        add("advancements.husbandry.dragon_egg_hive.title", "A Special Home for a Special Bee");
        add("advancements.husbandry.expansion_box.description", "Get an expansion box for your advanced beehive");
        add("advancements.husbandry.expansion_box.title", "Bigger is always better.");
        add("advancements.husbandry.feeding_slab.description", "Craft a Feeding Slab. You can put multiple flowering blocks in the Feeding Slab for your bees to have a centralized flowering spot.");
        add("advancements.husbandry.feeding_slab.title", "I want that one and that one and that one.");
        add("advancements.husbandry.fishy_bees.description", "Catch a fish-bee?");
        add("advancements.husbandry.fishy_bees.title", "Fishy Bees-ness");
        add("advancements.husbandry.hive_upgrades.description", "Craft an upgrade base");
        add("advancements.husbandry.hive_upgrades.title", "Make it count, 1, 2, 4.");
        add("advancements.husbandry.honeylogged.description", "Place honey fluid on the Feeding Slab.");
        add("advancements.husbandry.honeylogged.title", "Double Whammy!");
        add("advancements.husbandry.nest_locator.description", "Craft a Nest Locator");
        add("advancements.husbandry.nest_locator.title", "Locate the Buzz.");
        add("advancements.husbandry.overworld_nest.description", "Pick up or craft a solitary nest.");
        add("advancements.husbandry.overworld_nest.title", "Solitary");
        add("advancements.husbandry.professional_bee.description", "Breed a Rancher Bee, Farmer Bee, Lumber Bee or Quarry Bee.");
        add("advancements.husbandry.professional_bee.title", "I'm a special little bee-flake");
        add("advancements.husbandry.quartz_nest.description", "Craft or pick up a Nether Quartz nest.");
        add("advancements.husbandry.quartz_nest.title", "Wild wild bees.");
        add("advancements.husbandry.treat_on_nest.description", "Use a Honey Treat on a nest.");
        add("advancements.husbandry.treat_on_nest.title", "Treat me nice");
        add("advancements.husbandry.treat_on_stick.description", "Craft a Treat on a Stick to steer your new favorite ride.");
        add("advancements.husbandry.treat_on_stick.title", "Directional Buzzing");
        add("block.productivebees.feeder.tooltip", "Slap some more slab in your slab by slapping another slab on the slab");
        add("entity.minecraft.villager.productivebees.beekeeper", "Beekeeper");
        add("entity.productivebees.bee_configurable", "%s");
        add("fluid_type.productivebees.honey", "Honey");
        add("item.productivebees.bee_bomb", "Bee Bomb");
        add("item.productivebees.bee_bomb_angry", "Aggravating Bee Bomb");
        add("item.productivebees.bee_cage", "Bee Cage");
        add("item.productivebees.configurable_honeycomb", "Honeycomb");
        add("item.productivebees.draconic_chunk", "Draconic Chunk");
        add("item.productivebees.draconic_dust", "Draconic Dust");
        add("item.productivebees.gene", "Gene Sample");
        add("item.productivebees.gene_bottle", "Squashed Bee Material");
        add("item.productivebees.honey_bucket", "Honey Bucket");
        add("item.productivebees.honey_treat", "Honey Treat");
        add("item.productivebees.bee_nest_diamond_helmet", "Bee Nest Helmet");
        add("item.productivebees.honeycomb_alfsteel", "Alfsteel Comb");
        add("item.productivebees.honeycomb_allthemodium", "Allthemodium Comb");
        add("item.productivebees.honeycomb_amber", "Amber Comb");
        add("item.productivebees.honeycomb_apatite", "Apatite Comb");
        add("item.productivebees.honeycomb_basalz", "Basalz Comb");
        add("item.productivebees.honeycomb_bauxite", "Bauxite Comb");
        add("item.productivebees.honeycomb_bismuth", "Bismuth Comb");
        add("item.productivebees.honeycomb_blazing", "Blazing Comb");
        add("item.productivebees.honeycomb_blitz", "Blitz Comb");
        add("item.productivebees.honeycomb_blizz", "Blizz Comb");
        add("item.productivebees.honeycomb_bloody", "Bloody Comb");
        add("item.productivebees.honeycomb_bone", "Bone Comb");
        add("item.productivebees.honeycomb_brazen", "Brazen Comb");
        add("item.productivebees.honeycomb_bronze", "Bronze Comb");
        add("item.productivebees.honeycomb_chocolate", "Choco Comb");
        add("item.productivebees.honeycomb_cinnabar", "Cinnabar Comb");
        add("item.productivebees.honeycomb_cobalt", "Cobalt Comb");
        add("item.productivebees.honeycomb_configurable", "%s Comb");
        add("item.productivebees.honeycomb_ghostly", "Ghostly Comb");
        add("item.productivebees.honeycomb_milky", "Milky Comb");
        add("item.productivebees.honeycomb_powdery", "Powdery Comb");
        add("item.productivebees.milk_bottle", "Milk");
        add("item.productivebees.nest_locator", "Nest Locator");
        add("item.productivebees.spawn_egg_configurable", "%s Spawn Egg");
        add("item.productivebees.sturdy_bee_cage", "Sturdy Bee Cage");
        add("item.productivebees.sugarbag_honeycomb", "Sugarbag Honeycomb");
        add("item.productivebees.treat_on_a_stick", "Treat on a Stick");
        add("item.productivebees.wax", "Wax");
        add("item.productivebees.wither_skull_chip", "Wither Skull Chip");
        add("item.productivebees.obsidian_shard", "Obsidian Shard");
        add("itemGroup.productivebees", "Productive Bees");
        add("jei.productivebees.advanced_beehive", "Advanced Beehive");
        add("jei.productivebees.bee_breeding", "Bee Breeding");
        add("jei.productivebees.bee_conversion", "Bee Conversion");
        add("jei.productivebees.bee_flowering", "Bee Flowering");
        add("jei.productivebees.bee_fishing", "Bee Fishing");
        add("jei.productivebees.bee_spawning", "Bee Spawning");
        add("jei.productivebees.block_conversion", "Block Conversion");
        add("jei.productivebees.block_conversion.chance", "Chance: %s%%");
        add("jei.productivebees.item_conversion", "Item Conversion");
        add("jei.productivebees.bottler", "Bottler");
        add("jei.productivebees.centrifuge", "Centrifuge");
        add("jei.productivebees.heated_centrifuge", "Heated Centrifuge");
        add("jei.productivebees.incubation", "Incubation");
        add("emi.category.productivebees.bee_produce", "Advanced Beehive");
        add("emi.category.productivebees.bee_breeding", "Bee Breeding");
        add("emi.category.productivebees.bee_conversion", "Bee Conversion");
        add("emi.category.productivebees.bee_flowering", "Bee Flowering");
        add("emi.category.productivebees.bee_fishing", "Bee Fishing");
        add("emi.category.productivebees.bee_spawning", "Bee Spawning");
        add("emi.category.productivebees.block_conversion", "Block Conversion");
        add("emi.category.productivebees.block_conversion.chance", "Chance: %s%%");
        add("emi.category.productivebees.item_conversion", "Item Conversion");
        add("emi.category.productivebees.bottler", "Bottler");
        add("emi.category.productivebees.centrifuge", "Centrifuge");
        add("emi.category.productivebees.block_centrifuge", "Centrifuge");
        add("emi.category.productivebees.heated_centrifuge", "Heated Centrifuge");
        add("emi.category.productivebees.incubator", "Incubation");
        add("productivebees.centrifuge.tooltip.amount", "Amount: %s");
        add("productivebees.centrifuge.tooltip.chance", "Chance: %s");
        add("productivebees.incubator.tooltip.treat_item", "Here goes honey treats");
        add("productivebees.advanced_hive.tooltip.bee_cage", "Put empty bee cages to pull out bees or filled bee cages to insert bees");
        add("productivebees.heated_centrifuge.tooltip", "\"It's ruining the honey\" - some vegan");
        add("productivebees.heated_centrifuge.tooltip2", "Too hot for wax");
        add("productivebees.breeding_chamber.tooltip.cage", "Put empty bee cages here");
        add("productivebees.breeding_chamber.tooltip.progress", "%ss remaining");
        add("productivebees.breeding_chamber.tooltip.next_bee", "Remove and put back a cage to change output.");
        add("productivebees.hive.tooltip.bees", "Bees:");
        add("productivebees.hive.tooltip.empty", "Empty");
        add("productivebees.hive.tooltip.honey_level", "Honey Level: %s");
        add("productivebees.hive.tooltip.nest_inactive", "Nest will no longer attract cuckoo bees. Craft with itself to reset the cuckoo counter.");
        add("productivebees.amber.name.contained_entity", "Amber Encased %s");
        add("productivebees.amber.tooltip.heating", "Heating the amber block over a cozy fire might give some relief to the trapped mob.");
        add("productivebees.amber.tooltip.contained_entity", "Contained entity: %s");
        add("productivebees.indexer.tooltip.redstone", "I get turned on by redstone");
        add("productivebees.gene_bottle.tooltip.bee", "Material: %s");
        add("productivebees.gene_bottle.tooltip.use", "Throw me into a centrifuge.");
        add("productivebees.information.age.adult", "Adult");
        add("productivebees.information.age.child", "Child");
        add("productivebees.information.attribute.empty", "None");
        add("productivebees.information.attribute.behavior", "Behavior: %s");
        add("productivebees.information.attribute.behavior.diurnal", "Diurnal");
        add("productivebees.information.attribute.behavior.metaturnal", "Metaturnal");
        add("productivebees.information.attribute.behavior.nocturnal", "Nocturnal");
        add("productivebees.information.attribute.endurance", "Endurance: %s");
        add("productivebees.information.attribute.endurance.medium", "Medium");
        add("productivebees.information.attribute.endurance.normal", "Normal");
        add("productivebees.information.attribute.endurance.strong", "Strong");
        add("productivebees.information.attribute.endurance.weak", "Weak");
        add("productivebees.information.attribute.health", "Health: %s/%s");
        add("productivebees.information.attribute.productivity", "Productivity: %s");
        add("productivebees.information.attribute.productivity.high", "High");
        add("productivebees.information.attribute.productivity.medium", "Medium");
        add("productivebees.information.attribute.productivity.normal", "Normal");
        add("productivebees.information.attribute.productivity.very_high", "Very High");
        add("productivebees.information.attribute.temper", "Temper: %s");
        add("productivebees.information.attribute.temper.aggressive", "Aggressive");
        add("productivebees.information.attribute.temper.hostile", "Hostile");
        add("productivebees.information.attribute.temper.normal", "Normal");
        add("productivebees.information.attribute.temper.passive", "Passive");
        add("productivebees.information.attribute.type", "Type: %s");
        add("productivebees.information.attribute.type.hive", "Hive");
        add("productivebees.information.attribute.type.solitary", "Solitary");
        add("productivebees.information.attribute.weather_tolerance", "Weather Tolerance: %s");
        add("productivebees.information.attribute.weather_tolerance.any", "Any");
        add("productivebees.information.attribute.weather_tolerance.none", "None");
        add("productivebees.information.attribute.weather_tolerance.rain", "Rain");
        add("productivebees.information.cage_release", "Sneak while releasing the bee to reset its hive position");
        add("productivebees.information.health.dying", "Dying");
        add("productivebees.information.hold_shift", "Hold shift for more info");
        add("productivebees.information.home_position", "Hive position: %s, %s, %s");
        add("productivebees.information.breeding_item", "Breeding items: %s");
        add("productivebees.information.breeding_item_default", "Any flower");
        add("productivebees.information.selfbreed_disabled", "This bee species cannot breed amongst themselves");
        add("productivebees.information.jar.bee", "Bee: %s");
        add("productivebees.information.jar.fill_tip", "Put a BeeCage containing the bee you want displayed inside using a hopper or a pipe.");
        add("productivebees.information.nestlocator.configured", "Will locate nests of type %s. Right click to search the nearby area.");
        add("productivebees.information.nestlocator.unconfigured", "Not tuned. Shift right click on a block of the same type as the nest you are looking for.");
        add("productivebees.information.upgrade.warning", "Please put me in a crafting grid or I will fade away in time...pretty please with sugar on top.");
        add("productivebees.information.upgrade.unconfigured", "Unconfigured");
        add("productivebees.information.upgrade.upgrade_anti_teleport.advanced_beehive", "Prevents bees living in this hive from teleporting.");
        add("productivebees.information.upgrade.upgrade_productivity.advanced_beehive", "Increase the production output of bees in the hive by %s%%.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_productivity.honey_generator", "Lowers the consumption rate of honey.\n   Multiple upgrades can be installed for a greater reduction.");
        add("productivebees.information.upgrade.upgrade_productivity.centrifuge", "Increases stack processing rate by 4.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_productivity_2.advanced_beehive", "Increase the production output of bees in this hive by %s%%.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_productivity_2.centrifuge", "Increases stack processing rate by 8.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_productivity_3.advanced_beehive", "Increase the production output of bees in this hive by %s%%.\n   Multiple upgrades can be installed for a greater increase.\n   Also functions as a simulator upgrade.");
        add("productivebees.information.upgrade.upgrade_productivity_3.centrifuge", "Increases stack processing rate by 16.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_productivity_4.advanced_beehive", "Increase the production output of bees in this hive by %s%%.\n   Multiple upgrades can be installed for a greater increase.\n   Also functions as a simulator upgrade and a block upgrade.");
        add("productivebees.information.upgrade.upgrade_productivity_4.centrifuge", "Increases stack processing rate by 32.\n   Multiple upgrades can be installed for a greater increase.");
        add("productivebees.information.upgrade.upgrade_range.advanced_beehive", "Gives the bees in the hive a bigger work area.\n   Multiple upgrades can be installed for a greater range increase.");
        add("productivebees.information.upgrade.upgrade_range.catcher", "Gives the catcher more breathing space by increasing the operation area.\n   Multiple upgrades can be installed for a greater range increase.");
        add("productivebees.information.upgrade.upgrade_time.advanced_beehive", "Gives a %s%% decrease in time spent in the hive.\n   Multiple upgrades can be installed for a greater time decrease.\n   For the Germans out there, this still applies for simmed hives!");
        add("productivebees.information.upgrade.upgrade_time.breeding_chamber", "Brings down the reset time for bee shenanigans.\n   Multiple upgrades can be installed.");
        add("productivebees.information.upgrade.upgrade_time.incubator", "Makes it faster.\n   Multiple upgrades can be installed.");
        add("productivebees.information.upgrade.upgrade_time.centrifuge", "Makes the centrifuge go more brrrrrrrrr.\n   Multiple upgrades can be installed.");
        add("productivebees.information.upgrade.upgrade_time.honey_generator", "Increases power output by %s%% at the cost of higher fuel consumption.\n   Multiple upgrades can be installed.");
        add("productivebees.information.upgrade.upgrade_time_2.centrifuge", "It's twice as good as the other one.");
        add("productivebees.information.upgrade.upgrade_time_2.breeding_chamber", "It's twice as good as the other one.");
        add("productivebees.information.upgrade.upgrade_time_2.incubator", "It's twice as good as the other one.");
        add("productivebees.information.upgrade.upgrade_time_2.honey_generator", "It's twice as good as the other one.");
        add("productivebees.information.upgrade.upgrade_simulator.advanced_beehive", "Bees will no longer physically exit the hive. A trip to its flower block will instead be simulated.\n   Make sure to put the required flower block or a feeding slab in front of the hive containing the required flower blocks for the bee in the hive.");
        add("productivebees.information.upgrade.upgrade_stability.centrifuge", "Increases the success rate for combs that have a non-guaranteed output by %s%%.");
        add("productivebees.information.upgrade.upgrade_entity_filter.advanced_beehive", "Filter which bees are allowed to enter the hive.");
        add("productivebees.information.upgrade.upgrade_entity_filter.catcher", "Filter which bees the catcher can catch.");
        add("productivebees.information.upgrade.upgrade_entity_filter.centrifuge", "Only allow combs produced by the bees in the filter.");
        add("productivebees.information.upgrade.upgrade_gene_sampler.advanced_beehive", "Slowly extracts genes from visiting bees totally not hurting them in the process.\n   Chance to extract a random gene is %s%%.\n   Multiple upgrades can be installed for a greater gene output.");
        add("productivebees.information.upgrade.upgrade_child.advanced_beehive", "When installed in a hive it gives a chance for a new baby bee to be spawned from the hive every time honey is delivered.\n   The type of bee is based on which bees are present in the hive.\n   This does not work with bees that can't self breed or require special breeding items.\n   Multiple upgrades can be installed for a greater chance.");
        add("productivebees.information.upgrade.upgrade_child.catcher", "When installed in a Catcher only baby bees will be caught.");
        add("productivebees.information.upgrade.upgrade_adult.catcher", "When installed in a Catcher only adult bees will be caught.");
        add("productivebees.information.upgrade.upgrade_block.advanced_beehive", "Changes the produced output to blocks instead of honeycombs.");
        add("productivebees.information.bee_helmet.info1", "We are in it together");
        add("productivebees.information.bee_helmet.info2", "Angry bees won't attack you");
        add("productivebees.information.bee_helmet.info3", "%s%% chance to spawn a KamikazBee when hit");
        add("productivebees.information.canvas.style", "Style: %s");
        add("productivebees.information.beebee_death", "BeeBee got you. You should put on a Bee Nest Helmet next time");
        add("productivebees.ingredient.description.inactive_dragon_egg", "Looks cool, but something is missing. Try pouring some dragon stuff on it.");
        add("productivebees.ingredient.description.acacia_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.birch_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.dark_oak_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.jungle_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.oak_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.spruce_wood_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.bumble_bee_nest", "This nest will attract bumble bees in a plains biome.");
        add("productivebees.ingredient.description.coarse_dirt_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.end_stone_nest", "This nest attracts bees native to the end. Nest must be placed in the end to attract bees.");
        add("productivebees.ingredient.description.glowstone_nest", "Attracts glowing bees in the nether. Nest must be placed in the nether to attract bees.");
        add("productivebees.ingredient.description.gravel_nest", "Bees native to warm rivers and beaches are attracted to this nest. Nest must be placed in a river or beach biome to attract bees.");
        add("productivebees.ingredient.description.nether_brick_nest", "Attracts magmatic bees in the nether. Nest must be placed in the nether to attract bees.");
        add("productivebees.ingredient.description.nether_quartz_nest", "Attracts crystalline bees in the nether. Nest must be placed in the nether to attract bees.");
        add("productivebees.ingredient.description.nether_gold_nest", "Attracts gold bees in the nether. Nest must be placed in the nether to attract bees.");
        add("productivebees.ingredient.description.obsidian_nest", "Tough living environment only suitable for some bees native to the end.");
        add("productivebees.ingredient.description.sand_nest", "Bees native to deserts are attracted to this nest.");
        add("productivebees.ingredient.description.slimy_nest", "Attracts wild slimy bees. Nest must be placed in a swamp biome.");
        add("productivebees.ingredient.description.snow_nest", "Bees native to colder areas are attracted to this nest. Nest must be places in a cold biome to attract bees.");
        add("productivebees.ingredient.description.soul_sand_nest", "Attracts ghostly bees in the nether. Nest must be placed in the nether to attract bees.");
        add("productivebees.ingredient.description.stone_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.sugar_cane_nest", "This nest will attract bees in any overworld biome.");
        add("productivebees.ingredient.description.ashy_mining_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.blue_banded_bee", "This bee is solitary and can be found living alone in forests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.chocolate_mining_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.digger_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.green_carpenter_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.leafcutter_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.mason_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.neon_cuckoo_bee", "This sneaky bee will lay its eggs in nests belonging to the Blue Banded Bee.");
        add("productivebees.ingredient.description.nomad_bee", "This sneaky bee will lay its eggs in nests belonging to the Ashy Mining Bee.");
        add("productivebees.ingredient.description.reed_bee", "Can be found nesting in reeds all over the overworld.");
        add("productivebees.ingredient.description.redstone_bee", "Escape artist.");
        add("productivebees.ingredient.description.resin_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.spirit_bee", "There's a soul transformation recipe for this bee. Check the uses of the Soul Pedestal to see it.");
        add("productivebees.ingredient.description.yellow_black_carpenter_bee", "This bee is solitary and can be found living alone in nests around the overworld. Look up recipe for this bee to see which nests it can spawn in.");
        add("productivebees.ingredient.description.bumble_bee", "A big rideable bee found naturally in Plains. Saddle up and hold a Treat on a Stick to glide around. Right click to boost.");
        add("productivebees.ingredient.description.collector_bee", "If you leave your items on the ground, this bee will collect and store them in its hive.");
        add("productivebees.ingredient.description.creeper_bee", "Explodes! when angered\n...might be the diet.\n\nYou should keep this one to itself.");
        add("productivebees.ingredient.description.cupid_bee", "The pheromones secreted from this bee gets animals in the mood.");
        add("productivebees.ingredient.description.dye_bee", "Brings home more flowery bits than just nectar.");
        add("productivebees.ingredient.description.dragon_egg_hive", "A special hive made for Draconic bees that allows the user to harvest the otherwise hard to obtain dragon breath.");
        add("productivebees.ingredient.description.draconic_bee", "Draconic bees operating in the End, do not need any flowering as they are absorbing Draconic essence from the air. While operating in any other location, the Draconic bees must have another pollination source.");
        add("productivebees.ingredient.description.farmer_bee", "Harvests crops and leaves a mess. To keep your farm clean you might want to invest in a Hoarder bee.");
        add("productivebees.ingredient.description.hoarder_bee", "If you leave your items on the ground, this bee will collect and store them in its hive. Can carry more than its lesser cousin, the Collector Bee.");
        add("productivebees.ingredient.description.lumber_bee", "Has a healthy obsession with wood logs.");
        add("productivebees.ingredient.description.oily_bee", "This bee is nowhere to be found on land. Go fishing in deep waters if you want this thicc oily apid.");
        add("productivebees.ingredient.description.pepto_bismol_bee", "Cures what ails ya");
        add("productivebees.ingredient.description.prismarine_bee", "This bee is nowhere to be found on land. Fishermen tell tales of this bee nibbling on their bait when fishing in deep waters.");
        add("productivebees.ingredient.description.quarry_bee", "Has a healthy obsession with stone, sand and gravel.");
        add("productivebees.ingredient.description.rancher_bee", "In bee-school its nickname was BooBee.");
        add("productivebees.ingredient.description.skeletal_bee", "Be sure to keep your hives well lit to prevent this guy from moving in.");
        add("productivebees.ingredient.description.sugarbag_bee", "This little warm and moist bee can bee found in tropical areas. It produces a special type of super delicious comb. It can find its way into cocoa beans even when grown outside of their natural climate.");
        add("productivebees.ingredient.description.spatial_bee", "The combs of this bee can be centrifuged to get certus quartz seeds which require further processing.");
        add("productivebees.ingredient.description.sweat_bee", "Lives in cold areas.");
        add("productivebees.ingredient.description.wasted_radioactive_bee", "Radioactive bees dying to radiation has a chance to turn into wasted radioactive bees. Wasted radioactive bees don't pollinate on any flower, instead they absorb radiation from their surroundings.");
        add("productivebees.ingredient.description.zombie_bee", "Be sure to keep your hives well lit to prevent this guy from moving in.");
        add("productivebees.ingredient.description.hematophagous_bee", "It pollinates from animals, I just haven't found a way to show that in JEI yet. To acquire this bee, look up the crafting recipe for its spawn egg.");
        add("productivebees.ingredient.description.amber_bee", "Will seek out and encase mobs in amber dooming them to a life in suspended animation.");
        add("productivebees.ingredient.description.wanna_bee", "Has a thing for trapped mobs. Provide it with an amber encased mob to safely extract drops.");
        add("productivebees.ingredient.description.water_bee", "This bee is only found when fishing in rivers.");
        add("productivebees.ingredient.description.ribbeet_bee", "Frogs have a taste for regular bees and when you give them what they want you get this...thing in return.");
        add("productivebees.ingredient.description.utheric_bee", "Beware! Swarms of these bees hide in Gloomgourds, found in the Undergarden.");
        add("productivebees.ingredient.description.forgotten_bee", "This bee seems to be lost to time. Rumors however point to eggs rarely found in the Catacombs, found in the Undergarden.");
        add("productivebees.ingredient.description.regalium_bee", "Gold bees who get a special disc traded from the Stoneborns, who are found in the Undergarden, will find themselves becoming quite regal.");
        add("productivebees.ingredient.description.neptunium_bee", "This bees egg is rarely found in Neptune's Bounties.");
        add("productivebees.ingredient.description.crimson_bee", "Its nest can be found in Crimson Forests");
        add("productivebees.ingredient.description.warped_bee", "Its nest can be found in Warped Forests");
        add("productivebees.ingredient.description.beebee_bee", "RUN!");
        add("productivebees.ingredient.description.only_spawnegg", "To acquire this bee, look up the crafting recipe for its spawn egg.");
        add("productivebees.ingredient.description.selfbreed", "%s\n\nThis species of bees can't breed amongst themselves.");
        add("productivebees.nest_locator.found_hive", "A nest was located %s meters that way!");
        add("productivebees.nest_locator.not_found_hive", "No nests of the type %s could be found nearby.");
        add("productivebees.nest_locator.tuned", "Nest Locator tuned to %s");
        add("productivebees.honey_treat.invalid_use", "Treats containing type genes cannot be fed to bees.");
        add("productivebees.screen.empty", "Empty");
        add("productivebees.screen.energy_level", "Energy: %s");
        add("productivebees.screen.fluid_level", "%s: %s");
        add("productivebees.top.solitary.bee", "Inhabitant: %s");
        add("productivebees.top.solitary.can_repopulate_false", "There are no bees in this location that will move into this nest.");
        add("productivebees.top.solitary.can_repopulate_true", "The nest can attract bees in this location.");
        add("productivebees.top.solitary.egg_count", "Eggs: %s");
        add("productivebees.top.solitary.repopulation_countdown", "Repopulation cooldown %s. Use additional items to speed up");
        add("productivebees.top.solitary.repopulation_countdown_inactive", "Nest is empty. Use a spawn item on it to attract a bee.");
        add("config.jade.plugin_productivebees.bee", "Productive Bees");
        add("config.jade.plugin_productivebees.solitary_nest", "Solitary Nest");
        add("config.jade.plugin_productivebees.jar", "Bee Jar");
        add("config.jade.plugin_productivebees.bee_attributes", "Attributes");
        add("config.jade.plugin_productivebees.canvas_beehive", "Canvas Beehive");
        add("config.jade.plugin_productivebees.canvas_expansion_box", "Canvas Expansion Box");
        add("productivebees.top.jar.bee", "%s");
        add("tag.item.productivebees.advanced_beehives", "Advanced Beehives");
        add("tag.item.productivebees.expansion_boxes", "Expansion Boxes");
        add("tag.item.productivebees.canvas_beehives", "Canvas Advanced Beehives");
        add("tag.item.productivebees.canvas_expansion_boxes", "Canvas Expansion Boxes");
        add("tag.item.productivebees.flowers.amethyst", "Amethyst Flower Blocks");
        add("tag.item.productivebees.flowers.diamond", "Diamond Flower Blocks");
        add("tag.item.productivebees.flowers.emerald", "Emerald Flower Blocks");
        add("tag.item.productivebees.flowers.frozen", "Frozen Flower Blocks");
        add("tag.item.productivebees.flowers.lumber", "Lumber Bee Flower Blocks");
        add("tag.item.productivebees.flowers.quarry", "Quarry Bee Flower Blocks");
        add("tag.item.productivebees.flowers.powdery", "Powdery Flower Blocks");
        add("tag.item.productivebees.flowers.rose_quartz", "Rose Quartz Flower Blocks");
        add("tag.item.productivebees.solitary_overworld_nests", "Overworld Nests");
        add("tag.fluid.forge.honey", "Honey Fluids");
        add("productivebees.devices.advanced_beehive", "Advanced Beehive");
        add("productivebees.devices.breeding_chamber", "Breeding Chamber");
        add("productivebees.devices.catcher", "Catcher");
        add("productivebees.devices.centrifuge", "Centrifuge");
        add("productivebees.devices.honey_generator", "Honey Generator");
        add("productivebees.devices.incubator", "Incubator");
    }
}