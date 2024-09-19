package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
    public static Map<ResourceLocation, TagKey<Block>> blockTagCache = new HashMap<>();
    public static Map<ResourceLocation, TagKey<Item>> itemTagCache = new HashMap<>();
    public static Map<ResourceLocation, TagKey<Fluid>> fluidTagCache = new HashMap<>();
    public static Map<ResourceLocation, TagKey<EntityType<?>>> entityTagCache = new HashMap<>();

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
    public static final TagKey<Block> BOXES_BLOCK = getBlockTag("expansion_boxes");
    public static final TagKey<Block> CANVAS_HIVES_BLOCK = getBlockTag("canvas_beehives");
    public static final TagKey<Block> CANVAS_BOXES_BLOCK = getBlockTag("canvas_expansion_boxes");
    public static final TagKey<Block> NOT_FLOWERS = getBlockTag("not_flowers_for_spawning_nests");

    public static final TagKey<EntityType<?>> RANCHABLES = getEntityTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "ranchables"));
    public static final TagKey<EntityType<?>> EXTERNAL_CAN_POLLINATE = getEntityTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "external_can_pollinate"));
    public static final TagKey<EntityType<?>> BEE_ENCASE_BLACKLIST = getEntityTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_encase_blacklist"));
    public static final TagKey<EntityType<?>> MAGMA_CUBES = getEntityTag(ResourceLocation.fromNamespaceAndPath("c", "magma_cubes"));

    public static final TagKey<Fluid> HONEY = FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", "honey"));

    public static final TagKey<Item> CANVAS_HIVES = getItemTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "canvas_beehives"));
    public static final TagKey<Item> CANVAS_BOXES = getItemTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "canvas_expansion_boxes"));
    public static final TagKey<Item> HIVES = getItemTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "advanced_beehives"));
    public static final TagKey<Item> BOXES = getItemTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "expansion_boxes"));
    public static final TagKey<Item> WANNABEE_LOOT_BLACKLIST = getItemTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "wannabee_loot_blacklist"));

    public static TagKey<Block> getBlockTag(String name) {
        return getBlockTag(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, name));
    }

    public static TagKey<Block> getBlockTag(ResourceLocation resourceLocation) {
        if (!blockTagCache.containsKey(resourceLocation)) {
            blockTagCache.put(resourceLocation, BlockTags.create(resourceLocation));
        }
        return blockTagCache.get(resourceLocation);
    }

    public static TagKey<Item> getItemTag(ResourceLocation resourceLocation) {
        if (!itemTagCache.containsKey(resourceLocation)) {
            itemTagCache.put(resourceLocation, ItemTags.create(resourceLocation));
        }
        return itemTagCache.get(resourceLocation);
    }

    public static TagKey<Fluid> getFluidTag(ResourceLocation resourceLocation) {
        if (!fluidTagCache.containsKey(resourceLocation)) {
            fluidTagCache.put(resourceLocation, FluidTags.create(resourceLocation));
        }
        return fluidTagCache.get(resourceLocation);
    }

    public static TagKey<EntityType<?>> getEntityTag(ResourceLocation name) {
        return TagKey.create(Registries.ENTITY_TYPE, name);
    }

    public static TagKey<Biome> getBiomeTag(ResourceLocation name) {
        return TagKey.create(Registries.BIOME, name);
    }

    public static class Common
    {
        public static final TagKey<Item> STORAGE_BLOCK_HONEYCOMBS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "storage_blocks/honeycombs"));
        public static final TagKey<Item> HONEYCOMBS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "honeycombs"));
        public static final TagKey<Item> HONEY_BUCKETS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "buckets/honey"));
        public static final TagKey<Item> EGGS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "eggs"));
        public static final TagKey<Item> FISHING_RODS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "rods/fishing"));
        public static final TagKey<Item> WAX = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "wax"));
        public static final TagKey<Item> WITHER_SKULL_FRAGMENTS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "fragments/wither_skull"));
        public static final TagKey<Item> SILICON = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "silicon"));
        public static final TagKey<Item> SOURCE_GEM = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "gems/source"));
        public static final TagKey<Item> CAMPFIRES = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "campfires"));
        public static final TagKey<Item> HIVES = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "hives"));
        public static final TagKey<Item> DYES = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "dyes"));
        public static final TagKey<Item> SHEARS = getItemTag(ResourceLocation.fromNamespaceAndPath("c", "tools/shears"));
    }
}
