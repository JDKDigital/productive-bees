package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ModTags
{
    public static Map<ResourceLocation, ITag<Block>> tagCache = new HashMap<>();

    public static final ITag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final ITag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final ITag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final ITag<Block> WOOD_NESTS = getTag("nests/wood_nests");
    public static final ITag<Block> BUMBLE_BEE_NESTS = getTag("nests/bumble_bee");

    public static final ITag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final ITag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final ITag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final ITag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final ITag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");
    public static final ITag<Block> QUARRY = getTag("flowers/quarry");

    public static final ITag<Item> HONEY_BUCKETS = ItemTags.createOptional(new ResourceLocation("forge", "honey_buckets"));
    public static final ITag<Item> EGGS = ItemTags.createOptional(new ResourceLocation("forge", "eggs"));

    public static final ITag<EntityType<?>> RANCHABLES = EntityTypeTags.createOptional(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static final ITag<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation("forge", "honey"));

    public static ITag<Block> getTag(String name) {
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static ITag<Block> getTag(ResourceLocation resourceLocation) {
        if (!tagCache.containsKey(resourceLocation)) {
            tagCache.put(resourceLocation, BlockTags.createOptional(resourceLocation));
        }
        return tagCache.get(resourceLocation);
    }
}
