package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class ModTags
{
    public static Map<Identifier, TagKey<Block>> blockTagCache = new HashMap<>();
    public static Map<Identifier, TagKey<Item>> itemTagCache = new HashMap<>();
    public static Map<Identifier, TagKey<Fluid>> fluidTagCache = new HashMap<>();
    public static Map<Identifier, TagKey<EntityType<?>>> entityTagCache = new HashMap<>();

    public static final TagKey<Block> SOLITARY_OVERWORLD_NESTS = getBlockTag("solitary_overworld_nests");
    public static final TagKey<Block> REED_NESTS = getBlockTag("nests/reed_nests");
    public static final TagKey<Block> COLD_NESTS = getBlockTag("nests/cold_nests");
    public static final TagKey<Block> WOOD_NESTS = getBlockTag("nests/wood_nests");
    public static final TagKey<Block> BUMBLE_BEE_NESTS = getBlockTag("nests/bumble_bee");

    public static final TagKey<Block> FOREST_FLOWERS = getBlockTag("flowers/forest");
    public static final TagKey<Block> ARID_FLOWERS = getBlockTag("flowers/arid");
    public static final TagKey<Block> SWAMP_FLOWERS = getBlockTag("flowers/swamp");
    public static final TagKey<Block> SNOW_FLOWERS = getBlockTag("flowers/snow");
    public static final TagKey<Block> RIVER_FLOWERS = getBlockTag("flowers/river");
    public static final TagKey<Block> QUARRY = getBlockTag("flowers/quarry");
    public static final TagKey<Block> LUMBER = getBlockTag("flowers/lumber");
    public static final TagKey<Block> DUPE_BLACKLIST = getBlockTag("dupe_blacklist");
    public static final TagKey<Block> POWDERY = getBlockTag("flowers/powdery");
    public static final TagKey<Block> HIVES_BLOCK = getBlockTag("advanced_beehives");
    public static final TagKey<Block> DEFAULT_FLOWERING_BLOCK = getBlockTag("default_flowering");
    public static final TagKey<Block> BOXES_BLOCK = getBlockTag("expansion_boxes");
    public static final TagKey<Block> CANVAS_HIVES_BLOCK = getBlockTag("canvas_beehives");
    public static final TagKey<Block> CANVAS_BOXES_BLOCK = getBlockTag("canvas_expansion_boxes");
    public static final TagKey<Block> NOT_FLOWERS = getBlockTag("not_flowers_for_spawning_nests");

    public static final TagKey<EntityType<?>> RANCHABLES = getEntityTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "ranchables"));
    public static final TagKey<EntityType<?>> EXTERNAL_CAN_POLLINATE = getEntityTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "external_can_pollinate"));
    public static final TagKey<EntityType<?>> BEE_ENCASE_BLACKLIST = getEntityTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_encase_blacklist"));
    public static final TagKey<EntityType<?>> MAGMA_CUBES = getEntityTag(Identifier.fromNamespaceAndPath("c", "magma_cubes"));

    public static final TagKey<Fluid> HONEY = FluidTags.create(Identifier.fromNamespaceAndPath("c", "honey"));

    public static final TagKey<Item> CANVAS_HIVES = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "canvas_beehives"));
    public static final TagKey<Item> CANVAS_BOXES = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "canvas_expansion_boxes"));
    public static final TagKey<Item> HIVES = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "advanced_beehives"));
    public static final TagKey<Item> BOXES = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_boxes"));
    public static final TagKey<Item> WANNABEE_LOOT_BLACKLIST = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "wannabee_loot_blacklist"));
    public static final TagKey<Item> DEFAULT_FLOWERING = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default_flowering"));
    public static final TagKey<Item> DEFAULT_BREEDING = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "default_breeding_items"));
    public static final TagKey<Item> BEE_TEMPT_ITEMS = getItemTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_tempt_items"));

    public static final TagKey<Biome> BEEBEE_SPAWN_BIOMES = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "beebee_spawn_biomes"));

    public static TagKey<Block> getBlockTag(String name) {
        return getBlockTag(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, name));
    }

    public static TagKey<Block> getBlockTag(Identifier resourceLocation) {
        if (!blockTagCache.containsKey(resourceLocation)) {
            blockTagCache.put(resourceLocation, BlockTags.create(resourceLocation));
        }
        return blockTagCache.get(resourceLocation);
    }

    public static TagKey<Item> getItemTag(Identifier resourceLocation) {
        if (!itemTagCache.containsKey(resourceLocation)) {
            itemTagCache.put(resourceLocation, ItemTags.create(resourceLocation));
        }
        return itemTagCache.get(resourceLocation);
    }

    public static TagKey<Fluid> getFluidTag(Identifier resourceLocation) {
        if (!fluidTagCache.containsKey(resourceLocation)) {
            fluidTagCache.put(resourceLocation, FluidTags.create(resourceLocation));
        }
        return fluidTagCache.get(resourceLocation);
    }

    public static TagKey<EntityType<?>> getEntityTag(Identifier name) {
        return TagKey.create(Registries.ENTITY_TYPE, name);
    }

    public static TagKey<Biome> getBiomeTag(Identifier name) {
        return TagKey.create(Registries.BIOME, name);
    }

    public static class Common
    {
        public static final TagKey<Item> STORAGE_BLOCK_HONEYCOMBS = getItemTag(Identifier.fromNamespaceAndPath("c", "storage_blocks/honeycombs"));
        public static final TagKey<Item> HONEYCOMBS = getItemTag(Identifier.fromNamespaceAndPath("c", "honeycombs"));
        public static final TagKey<Item> HONEY_BUCKETS = getItemTag(Identifier.fromNamespaceAndPath("c", "buckets/honey"));
        public static final TagKey<Item> FISHING_RODS = getItemTag(Identifier.fromNamespaceAndPath("c", "rods/fishing"));
        public static final TagKey<Item> WAXES = getItemTag(Identifier.fromNamespaceAndPath("c", "waxes"));
        public static final TagKey<Item> WITHER_SKULL_FRAGMENTS = getItemTag(Identifier.fromNamespaceAndPath("c", "fragments/wither_skull"));
        public static final TagKey<Item> SILICON = getItemTag(Identifier.fromNamespaceAndPath("c", "silicon"));
        public static final TagKey<Item> SOURCE_GEM = getItemTag(Identifier.fromNamespaceAndPath("c", "gems/source"));
        public static final TagKey<Item> CAMPFIRES = getItemTag(Identifier.fromNamespaceAndPath("c", "campfires"));
        public static final TagKey<Item> HIVES = getItemTag(Identifier.fromNamespaceAndPath("c", "hives"));
        public static final TagKey<Item> DYES = getItemTag(Identifier.fromNamespaceAndPath("c", "dyes"));
    }
}
