package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends BlockTagCopyingItemTagProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagsProvider.TagLookup<Block>> provider) {
        super(output, future, provider, ProductiveBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(ModTags.HIVES_BLOCK, ModTags.HIVES);
        copy(ModTags.BOXES_BLOCK, ModTags.BOXES);
        copy(ModTags.CANVAS_HIVES_BLOCK, ModTags.CANVAS_HIVES);
        copy(ModTags.CANVAS_BOXES_BLOCK, ModTags.CANVAS_BOXES);
        copy(ModTags.DEFAULT_FLOWERING_BLOCK, ModTags.DEFAULT_FLOWERING);
        tag(ModTags.DEFAULT_BREEDING).addTag(ItemTags.BEE_FOOD);
        tag(ModTags.BEE_TEMPT_ITEMS).addTag(ItemTags.BEE_FOOD).add(ModItems.HONEY_TREAT.get());

        tag(ItemTags.HEAD_ARMOR).add(ModItems.BEE_NEST_DIAMOND_HELMET.get());

        tagOf("productivebees:solitary_overworld_nests").addElement(id("productivebees:sand_nest")).addElement(id("productivebees:stone_nest")).addElement(id("productivebees:snow_nest")).addElement(id("productivebees:coarse_dirt_nest")).addElement(id("productivebees:gravel_nest")).addElement(id("productivebees:sugar_cane_nest")).addElement(id("productivebees:oak_wood_nest")).addElement(id("productivebees:spruce_wood_nest")).addElement(id("productivebees:dark_oak_wood_nest")).addElement(id("productivebees:birch_wood_nest")).addElement(id("productivebees:jungle_wood_nest")).addElement(id("productivebees:acacia_wood_nest"));
        tagOf("productivebees:wannabee_loot_blacklist");
        tagOf("productivebees:flowers/agate").addOptionalTag(id("c:storage_blocks/agate"));
        tagOf("productivebees:flowers/alexandrite").addOptionalTag(id("c:storage_blocks/alexandrite")).addOptionalElement(id("silentgems:alexandrite_glowrose")).addOptionalElement(id("gemsnjewels:alexandrite_block"));
        tagOf("productivebees:flowers/amber").addOptionalTag(id("c:storage_blocks/amber")).addOptionalElement(id("betterendforge:amber_block"));
        tagOf("productivebees:flowers/amethyst").addElement(id("minecraft:amethyst_block")).addElement(id("minecraft:budding_amethyst")).addElement(id("minecraft:amethyst_cluster")).addOptionalElement(id("silentgems:amethyst_glowrose")).addOptionalElement(id("oresabovediamonds:amethyst_block"));
        tagOf("productivebees:flowers/ametrine").addOptionalTag(id("c:storage_blocks/ametrine")).addOptionalElement(id("gemsnjewels:ametrine_block"));
        tagOf("productivebees:flowers/ammolite").addOptionalTag(id("c:storage_blocks/ammolite")).addOptionalElement(id("silentgems:ammolite_glowrose"));
        tagOf("productivebees:flowers/apatite").addOptionalTag(id("c:storage_blocks/apatite"));
        tagOf("productivebees:flowers/aquamarine").addOptionalTag(id("c:storage_blocks/aquamarine")).addOptionalElement(id("gemsnjewels:aquamarine_block")).addOptionalElement(id("astralsorcery:aquamarine_sand_ore"));
        tagOf("productivebees:flowers/arid").addTag(id("minecraft:flowers")).addElement(id("minecraft:cactus")).addElement(id("minecraft:dead_bush"));
        tagOf("productivebees:flowers/benitoite").addOptionalTag(id("c:storage_blocks/benitoite"));
        tagOf("productivebees:flowers/black_diamond").addOptionalTag(id("c:storage_blocks/black_diamond")).addOptionalElement(id("silentgems:black_diamond_glowrose"));
        tagOf("productivebees:flowers/black_opal").addOptionalTag(id("c:storage_blocks/black_opal")).addOptionalElement(id("gemsnjewels:black_opal_block")).addOptionalElement(id("oresabovediamonds:black_opal_block"));
        tagOf("productivebees:flowers/burning").addElement(id("minecraft:magma_block"));
        tagOf("productivebees:flowers/carnelian").addOptionalTag(id("c:storage_blocks/carnelian")).addOptionalElement(id("silentgems:carnelian_glowrose"));
        tagOf("productivebees:flowers/cats_eye").addOptionalTag(id("c:storage_blocks/cats_eye"));
        tagOf("productivebees:flowers/chrysoprase").addOptionalTag(id("c:storage_blocks/chrysoprase"));
        tagOf("productivebees:flowers/citrine").addOptionalTag(id("c:storage_blocks/citrine")).addOptionalElement(id("silentgems:citrine_glowrose")).addOptionalElement(id("gemsnjewels:citrine_block"));
        tagOf("productivebees:flowers/coral").addOptionalTag(id("c:storage_blocks/coral"));
        tagOf("productivebees:flowers/crystalline").addOptionalTag(id("c:ores/quartz")).addElement(id("minecraft:quartz_pillar")).addElement(id("minecraft:quartz_block")).addElement(id("productivebees:quartz_netherrack"));
        tagOf("productivebees:flowers/cupric").addElement(id("minecraft:copper_ore")).addElement(id("minecraft:deepslate_copper_ore")).addElement(id("minecraft:copper_block")).addElement(id("minecraft:raw_copper_block")).addElement(id("minecraft:cut_copper")).addElement(id("minecraft:exposed_cut_copper")).addElement(id("minecraft:weathered_cut_copper")).addElement(id("minecraft:oxidized_cut_copper")).addElement(id("minecraft:exposed_copper")).addElement(id("minecraft:weathered_copper")).addElement(id("minecraft:oxidized_copper")).addElement(id("minecraft:waxed_copper_block")).addElement(id("minecraft:waxed_exposed_copper")).addElement(id("minecraft:waxed_weathered_copper")).addElement(id("minecraft:waxed_oxidized_copper")).addElement(id("minecraft:lightning_rod"));
        tagOf("productivebees:flowers/diamond").addOptionalTag(id("c:storage_blocks/diamond")).addOptionalTag(id("c:ores/diamond"));
        tagOf("productivebees:flowers/draconic").addElement(id("minecraft:dragon_egg"));
        tagOf("productivebees:flowers/emerald").addOptionalTag(id("c:storage_blocks/emerald")).addOptionalTag(id("c:ores/emerald")).addOptionalElement(id("gemsnjewels:emerald_block"));
        tagOf("productivebees:flowers/ender").addElement(id("minecraft:chorus_flower")).addElement(id("minecraft:chorus_plant"));
        tagOf("productivebees:flowers/euclase").addOptionalTag(id("c:storage_blocks/euclase"));
        tagOf("productivebees:flowers/ferric").addElement(id("minecraft:iron_ore")).addElement(id("minecraft:deepslate_iron_ore")).addElement(id("minecraft:raw_iron_block")).addElement(id("minecraft:iron_block")).addElement(id("minecraft:iron_bars")).addElement(id("minecraft:iron_door")).addElement(id("minecraft:iron_trapdoor")).addElement(id("minecraft:cauldron")).addElement(id("minecraft:hopper")).addElement(id("minecraft:anvil")).addElement(id("minecraft:chipped_anvil")).addElement(id("minecraft:damaged_anvil"));
        tagOf("productivebees:flowers/fiery").addElement(id("minecraft:magma_block"));
        tagOf("productivebees:flowers/fluorite").addOptionalTag(id("c:storage_blocks/fluorite")).addOptionalElement(id("mekanism:fluorite_ore")).addOptionalTag(id("c:ores/fluorite"));
        tagOf("productivebees:flowers/frozen").addElement(id("minecraft:packed_ice")).addElement(id("minecraft:blue_ice")).addElement(id("minecraft:ice")).addElement(id("minecraft:snow")).addElement(id("minecraft:snow_block"));
        tagOf("productivebees:flowers/garnet").addOptionalTag(id("c:storage_blocks/garnet")).addOptionalElement(id("gemsnjewels:garnet_block"));
        tagOf("productivebees:flowers/gilded").addElement(id("minecraft:nether_gold_ore")).addElement(id("minecraft:deepslate_gold_ore")).addElement(id("minecraft:gold_ore")).addElement(id("minecraft:gold_block")).addElement(id("minecraft:raw_gold_block")).addElement(id("minecraft:gilded_blackstone")).addElement(id("minecraft:light_weighted_pressure_plate"));
        tagOf("productivebees:flowers/glowing").addElement(id("minecraft:shroomlight")).addElement(id("minecraft:glowstone")).addElement(id("minecraft:redstone_lamp"));
        tagOf("productivebees:flowers/graves").addOptionalElement(id("tombstone:decorative_grave_simple")).addOptionalElement(id("tombstone:decorative_grave_normal")).addOptionalElement(id("tombstone:decorative_grave_cross")).addOptionalElement(id("tombstone:decorative_grave_original")).addOptionalElement(id("tombstone:decorative_subaraki_grave")).addOptionalElement(id("tombstone:decorative_tombstone"));
        tagOf("productivebees:flowers/green_sapphire").addOptionalTag(id("c:storage_blocks/green_sapphire"));
        tagOf("productivebees:flowers/heliodor").addOptionalTag(id("c:storage_blocks/heliodor")).addOptionalElement(id("silentgems:heliodor_glowrose"));
        tagOf("productivebees:flowers/iolite").addOptionalTag(id("c:storage_blocks/iolite")).addOptionalElement(id("silentgems:iolite_glowrose")).addOptionalElement(id("gemsnjewels:iolite_block"));
        tagOf("productivebees:flowers/jade").addOptionalTag(id("c:storage_blocks/jade"));
        tagOf("productivebees:flowers/jasper").addOptionalTag(id("c:storage_blocks/jasper"));
        tagOf("productivebees:flowers/kunzite").addOptionalTag(id("c:storage_blocks/kunzite")).addOptionalElement(id("gemsnjewels:kunzite_block"));
        tagOf("productivebees:flowers/kyanite").addOptionalTag(id("c:storage_blocks/kyanite")).addOptionalElement(id("silentgems:kyanite_glowrose"));
        tagOf("productivebees:flowers/lepidolite").addOptionalTag(id("c:storage_blocks/lepidolite"));
        tagOf("productivebees:flowers/lumber").addTag(id("minecraft:logs")).addTag(id("minecraft:leaves"));
        tagOf("productivebees:flowers/magmatic").addTag(id("productivebees:flowers/fiery")).addElement(id("minecraft:nether_wart"));
        tagOf("productivebees:flowers/malachite").addOptionalTag(id("c:storage_blocks/malachite"));
        tagOf("productivebees:flowers/mithril").addOptionalTag(id("c:storage_blocks/mithril")).addOptionalElement(id("irons_spellbooks:spellbreaker"));
        tagOf("productivebees:flowers/moldavite").addOptionalTag(id("c:storage_blocks/moldavite")).addOptionalElement(id("silentgems:moldavite_glowrose"));
        tagOf("productivebees:flowers/moonstone").addOptionalTag(id("c:storage_blocks/moonstone"));
        tagOf("productivebees:flowers/morganite").addOptionalTag(id("c:storage_blocks/morganite")).addOptionalElement(id("gemsnjewels:morganite_block"));
        tagOf("productivebees:flowers/nether").addElement(id("minecraft:nether_wart")).addElement(id("minecraft:nether_wart_block")).addElement(id("minecraft:crimson_fungus")).addElement(id("minecraft:warped_fungus")).addElement(id("minecraft:crimson_roots")).addElement(id("minecraft:warped_roots")).addElement(id("minecraft:weeping_vines")).addElement(id("minecraft:twisting_vines"));
        tagOf("productivebees:flowers/onyx").addOptionalTag(id("c:storage_blocks/onyx"));
        tagOf("productivebees:flowers/opal").addOptionalTag(id("c:storage_blocks/opal")).addOptionalElement(id("gemsnjewels:opal_block"));
        tagOf("productivebees:flowers/pearl").addOptionalTag(id("c:storage_blocks/pearl"));
        tagOf("productivebees:flowers/peridot").addOptionalTag(id("c:storage_blocks/peridot")).addOptionalElement(id("silentgems:peridot_glowrose")).addOptionalElement(id("gemsnjewels:peridot_block"));
        tagOf("productivebees:flowers/phosphophyllite").addOptionalTag(id("c:storage_blocks/phosphophyllite"));
        tagOf("productivebees:flowers/powdery").addOptionalTag(id("c:storage_blocks/gunpowder")).addElement(id("minecraft:coal_ore")).addElement(id("minecraft:tnt"));
        tagOf("productivebees:flowers/prismarine").addElement(id("minecraft:prismarine")).addElement(id("minecraft:dark_prismarine")).addElement(id("minecraft:prismarine_bricks")).addElement(id("minecraft:sea_lantern"));
        tagOf("productivebees:flowers/pyrope").addOptionalTag(id("c:storage_blocks/pyrope"));
        tagOf("productivebees:flowers/quarry").addTag(id("minecraft:dirt")).addOptionalTag(id("c:stones")).addOptionalTag(id("c:sands")).addOptionalTag(id("c:gravels")).addElement(id("minecraft:cobblestone")).addElement(id("minecraft:end_stone")).addElement(id("minecraft:netherrack")).addElement(id("minecraft:clay")).addElement(id("minecraft:blackstone")).addElement(id("minecraft:basalt")).addElement(id("minecraft:calcite")).addElement(id("minecraft:cobbled_deepslate")).addOptionalElement(id("ae2:sky_stone_block")).addOptionalElement(id("create:asurine")).addOptionalElement(id("create:crimsite")).addOptionalElement(id("create:limestone")).addOptionalElement(id("create:ochrum")).addOptionalElement(id("create:scoria")).addOptionalElement(id("create:scorchia")).addOptionalElement(id("create:veridium")).addOptionalElement(id("quark:corundum"));
        tagOf("productivebees:flowers/radioactive").addOptionalTag(id("c:storage_blocks/uranium")).addOptionalTag(id("c:ores/uranium")).addOptionalTag(id("c:storage_blocks/yellorium")).addOptionalTag(id("c:ores/yellorite"));
        tagOf("productivebees:flowers/redstone").addOptionalTag(id("c:storage_blocks/redstone")).addOptionalTag(id("c:ores/redstone")).addElement(id("minecraft:redstone_torch")).addElement(id("minecraft:redstone_lamp"));
        tagOf("productivebees:flowers/river").addTag(id("minecraft:flowers")).addElement(id("minecraft:lily_pad")).addElement(id("minecraft:sugar_cane"));
        tagOf("productivebees:flowers/rose_quartz").addOptionalTag(id("c:storage_blocks/rose_quartz")).addOptionalElement(id("silentgems:rose_quartz_glowrose")).addOptionalElement(id("create:rose_quartz_block"));
        tagOf("productivebees:flowers/ruby").addOptionalTag(id("c:storage_blocks/ruby")).addOptionalElement(id("silentgems:ruby_glowrose")).addOptionalElement(id("gemsnjewels:ruby_block"));
        tagOf("productivebees:flowers/sapphire").addOptionalTag(id("c:storage_blocks/sapphire")).addOptionalElement(id("silentgems:sapphire_glowrose")).addOptionalElement(id("gemsnjewels:sapphire_block")).addOptionalElement(id("iceandfire:sapphire_block"));
        tagOf("productivebees:flowers/slimy").addTag(id("productivebees:flowers/swamp")).addElement(id("minecraft:slime_block"));
        tagOf("productivebees:flowers/snow").addTag(id("minecraft:flowers")).addElement(id("minecraft:snow")).addElement(id("minecraft:snow_block"));
        tagOf("productivebees:flowers/sodalite").addOptionalTag(id("c:storage_blocks/sodalite")).addOptionalElement(id("silentgems:sodalite_glowrose")).addOptionalElement(id("gemsnjewels:sodalite_block"));
        tagOf("productivebees:flowers/souled").addElement(id("minecraft:soul_sand")).addElement(id("minecraft:soul_soil"));
        tagOf("productivebees:flowers/spinel").addOptionalTag(id("c:storage_blocks/spinel")).addOptionalElement(id("gemsnjewels:spinel_block"));
        tagOf("productivebees:flowers/sulfur").addOptionalTag(id("c:storage_blocks/sulfur")).addOptionalTag(id("c:dusts/sulfur")).addElement(id("minecraft:coal_ore"));
        tagOf("productivebees:flowers/sunstone").addOptionalTag(id("c:storage_blocks/sunstone"));
        tagOf("productivebees:flowers/swamp").addTag(id("minecraft:flowers")).addElement(id("minecraft:lily_pad"));
        tagOf("productivebees:flowers/tanzanite").addOptionalTag(id("c:storage_blocks/tanzanite")).addOptionalElement(id("gemsnjewels:tanzanite_block"));
        tagOf("productivebees:flowers/tektite").addOptionalTag(id("c:storage_blocks/tektite"));
        tagOf("productivebees:flowers/topaz").addOptionalTag(id("c:storage_blocks/topaz")).addOptionalElement(id("silentgems:topaz_glowrose")).addOptionalElement(id("gemsnjewels:topaz_block"));
        tagOf("productivebees:flowers/tourmaline").addOptionalTag(id("c:storage_blocks/tourmaline")).addOptionalElement(id("gemsnjewels:tourmaline_block"));
        tagOf("productivebees:flowers/turquoise").addOptionalTag(id("c:storage_blocks/turquoise")).addOptionalElement(id("silentgems:turquoise_glowrose"));
        tagOf("productivebees:flowers/white_diamond").addOptionalTag(id("c:storage_blocks/white_diamond")).addOptionalElement(id("silentgems:white_diamond_glowrose"));
        tagOf("productivebees:flowers/wither").addElement(id("minecraft:wither_rose"));
        tagOf("productivebees:flowers/zircon").addOptionalTag(id("c:storage_blocks/zircon")).addOptionalElement(id("gemsnjewels:zircon_block"));
        tagOf("c:campfires").addElement(id("minecraft:campfire")).addElement(id("minecraft:soul_campfire"));
        tagOf("c:dirt").addElement(id("minecraft:dirt")).addElement(id("minecraft:grass_block")).addElement(id("minecraft:coarse_dirt")).addElement(id("minecraft:podzol")).addElement(id("minecraft:mycelium")).addElement(id("minecraft:rooted_dirt"));
        tagOf("c:hives").addElement(id("minecraft:beehive")).addOptionalElement(id("friendsandfoes:acacia_beehive")).addOptionalElement(id("friendsandfoes:bamboo_beehive")).addOptionalElement(id("friendsandfoes:birch_beehive")).addOptionalElement(id("friendsandfoes:cherry_beehive")).addOptionalElement(id("friendsandfoes:crimson_beehive")).addOptionalElement(id("friendsandfoes:dark_oak_beehive")).addOptionalElement(id("friendsandfoes:jungle_beehive")).addOptionalElement(id("friendsandfoes:mangrove_beehive")).addOptionalElement(id("friendsandfoes:spruce_beehive")).addOptionalElement(id("friendsandfoes:warped_beehive"));
        tagOf("c:honeycombs").addElement(id("minecraft:honeycomb")).addElement(id("productivebees:honeycomb_milky")).addElement(id("productivebees:honeycomb_powdery")).addElement(id("productivebees:honeycomb_ghostly")).addElement(id("productivebees:configurable_honeycomb"));
        tagOf("c:milk").addOptionalTag(id("c:milk/freshmilk")).addOptionalTag(id("c:milk/milk_bottle"));
        tagOf("c:milks").addOptionalTag(id("c:milk/freshmilk")).addOptionalTag(id("c:milk/milk_bottle"));
        tagOf("c:pollens");
        tagOf("c:storage_blocks").addOptionalTag(id("c:storage_blocks/honeycombs")).addOptionalTag(id("c:storage_blocks/wax"));
        tagOf("c:waxes").addElement(id("productivebees:wax"));
        tagOf("c:workbenches").addElement(id("minecraft:crafting_table"));
        tagOf("c:buckets/honey").addElement(id("productivebees:honey_bucket"));
        tagOf("c:dusts/draconic").addElement(id("productivebees:draconic_dust"));
        tagOf("c:fragments/draconic").addElement(id("productivebees:draconic_chunk"));
        tagOf("c:fragments/wither_skull").addElement(id("productivebees:wither_skull_chip"));
        tagOf("c:gems/amethyst").addElement(id("minecraft:amethyst_shard"));
        tagOf("c:milk/freshmilk").addElement(id("productivebees:milk_bottle"));
        tagOf("c:milk/milk_bottle").addElement(id("productivebees:milk_bottle"));
        tagOf("c:rods/fishing").addElement(id("minecraft:fishing_rod"));
        tagOf("c:storage_blocks/honeycombs").addElement(id("minecraft:honeycomb_block")).addElement(id("productivebees:configurable_comb")).addElement(id("productivebees:comb_ghostly")).addElement(id("productivebees:comb_milky")).addElement(id("productivebees:comb_powdery"));
        tagOf("c:storage_blocks/wax").addElement(id("productivebees:wax_block"));
    }

    private TagBuilder tagOf(String tagId) {
        return getOrCreateRawBuilder(TagKey.create(Registries.ITEM, Identifier.parse(tagId)));
    }

    private static Identifier id(String s) {
        return Identifier.parse(s);
    }

    @Override
    public String getName() {
        return "Productive Bees Item Tags Provider";
    }
}
