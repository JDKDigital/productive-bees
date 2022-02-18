package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class ModTags
{
    public static Map<ResourceLocation, Tag<Block>> blockTagCache = new HashMap<>();
    public static Map<ResourceLocation, Tag<Item>> itemTagCache = new HashMap<>();
    public static Map<ResourceLocation, Tag<EntityType<?>>> entityTagCache = new HashMap<>();

    public static final Tag<Block> SOLITARY_OVERWORLD_NESTS = getBlockTag("solitary_overworld_nests");
    public static final Tag<Block> REED_NESTS = getBlockTag("nests/reed_nests");
    public static final Tag<Block> COLD_NESTS = getBlockTag("nests/cold_nests");
    public static final Tag<Block> WOOD_NESTS = getBlockTag("nests/wood_nests");
    public static final Tag<Block> BUMBLE_BEE_NESTS = getBlockTag("nests/bumble_bee");

    public static final Tag<Block> FOREST_FLOWERS = getBlockTag("flowers/forest_flowers");
    public static final Tag<Block> ARID_FLOWERS = getBlockTag("flowers/arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getBlockTag("flowers/swamp_flowers");
    public static final Tag<Block> SNOW_FLOWERS = getBlockTag("flowers/snow_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getBlockTag("flowers/river_flowers");
    public static final Tag<Block> QUARRY = getBlockTag("flowers/quarry");
    public static final Tag<Block> LUMBER = getBlockTag("flowers/lumber");
    public static final Tag<Block> POWDERY = getBlockTag("flowers/powdery");

    public static final Tag<Item> HONEY_BUCKETS = getItemTag(new ResourceLocation("forge", "buckets/honey"));
    public static final Tag<Item> EGGS = getItemTag(new ResourceLocation("forge", "eggs"));

    public static final Tag<EntityType<?>> RANCHABLES = EntityTypeTags.createOptional(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static final Tag<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation("forge", "honey"));

    public static Tag<Block> getBlockTag(String name) {
        return getBlockTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static Tag<Block> getBlockTag(ResourceLocation resourceLocation) {
        if (!blockTagCache.containsKey(resourceLocation)) {
            blockTagCache.put(resourceLocation, BlockTags.createOptional(resourceLocation));
        }
        return blockTagCache.get(resourceLocation);
    }

    public static Tag<Item> getItemTag(ResourceLocation resourceLocation) {
        if (!itemTagCache.containsKey(resourceLocation)) {
            itemTagCache.put(resourceLocation, ItemTags.createOptional(resourceLocation));
        }
        return itemTagCache.get(resourceLocation);
    }

    public static Tag<EntityType<?>> getEntityTag(ResourceLocation name) {
        return EntityTypeTags.createOptional(name);
    }
}
