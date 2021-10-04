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
    public static Map<ResourceLocation, Tag<Block>> tagCache = new HashMap<>();

    public static final Tag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final Tag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final Tag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final Tag<Block> WOOD_NESTS = getTag("nests/wood_nests");
    public static final Tag<Block> BUMBLE_BEE_NESTS = getTag("nests/bumble_bee");

    public static final Tag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final Tag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final Tag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");
    public static final Tag<Block> QUARRY = getTag("flowers/quarry");
    public static final Tag<Block> POWDERY = getTag("flowers/powdery");

    public static final Tag<Item> HONEY_BUCKETS = ItemTags.createOptional(new ResourceLocation("forge", "buckets/honey"));
    public static final Tag<Item> EGGS = ItemTags.createOptional(new ResourceLocation("forge", "eggs"));

    public static final Tag<EntityType<?>> RANCHABLES = EntityTypeTags.createOptional(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static final Tag<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation("forge", "honey"));

    public static Tag<Block> getTag(String name) {
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static Tag<Block> getTag(ResourceLocation resourceLocation) {
        if (!tagCache.containsKey(resourceLocation)) {
            tagCache.put(resourceLocation, BlockTags.createOptional(resourceLocation));
        }
        return tagCache.get(resourceLocation);
    }
}
