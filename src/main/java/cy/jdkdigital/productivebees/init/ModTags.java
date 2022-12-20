package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.Registry;
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

    public static final TagKey<Block> FOREST_FLOWERS = getBlockTag("flowers/forest_flowers");
    public static final TagKey<Block> ARID_FLOWERS = getBlockTag("flowers/arid_flowers");
    public static final TagKey<Block> SWAMP_FLOWERS = getBlockTag("flowers/swamp_flowers");
    public static final TagKey<Block> SNOW_FLOWERS = getBlockTag("flowers/snow_flowers");
    public static final TagKey<Block> RIVER_FLOWERS = getBlockTag("flowers/river_flowers");
    public static final TagKey<Block> QUARRY = getBlockTag("flowers/quarry");
    public static final TagKey<Block> LUMBER = getBlockTag("flowers/lumber");
    public static final TagKey<Block> POWDERY = getBlockTag("flowers/powdery");

    public static final TagKey<EntityType<?>> RANCHABLES = getEntityTag(new ResourceLocation(ProductiveBees.MODID, "ranchables"));
    public static final TagKey<EntityType<?>> EXTERNAL_CAN_POLLINATE = getEntityTag(new ResourceLocation(ProductiveBees.MODID, "external_can_pollinate"));

    public static final TagKey<Fluid> HONEY = FluidTags.create(new ResourceLocation("forge", "honey"));

    public static TagKey<Block> getBlockTag(String name) {
        return getBlockTag(new ResourceLocation(ProductiveBees.MODID, name));
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

    public static class Forge {
        public static final TagKey<Item> COMBS = getItemTag(new ResourceLocation("forge", "storage_blocks/honeycombs"));
        public static final TagKey<Item> HONEY_BUCKETS = getItemTag(new ResourceLocation("forge", "buckets/honey"));
        public static final TagKey<Item> EGGS = getItemTag(new ResourceLocation("forge", "eggs"));
        public static final TagKey<Item> FISHING_RODS = getItemTag(new ResourceLocation("forge", "rods/fishing"));
        public static final TagKey<Item> WAX = getItemTag(new ResourceLocation("forge", "wax"));
        public static final TagKey<Item> WITHER_SKULL_FRAGMENTS = getItemTag(new ResourceLocation("forge", "fragments/wither_skull"));
        public static final TagKey<Item> SILICON = getItemTag(new ResourceLocation("forge", "silicon"));
        public static final TagKey<Item> SOURCE_GEM = getItemTag(new ResourceLocation("forge", "gems/source"));
    }
}
