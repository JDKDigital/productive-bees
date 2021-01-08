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
    public static Map<ResourceLocation, Tag<Block>> tagCache = new HashMap<>();

    public static final Tag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final Tag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final Tag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final Tag<Block> WOOD_NESTS = getTag("nests/wood_nests");

    public static final Tag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final Tag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final Tag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");
    public static final Tag<Block> QUARRY = getTag("flowers/quarry");

    public static final Tag<Item> HONEY_BUCKETS = new ItemTags.Wrapper(new ResourceLocation(ProductiveBees.MODID, "honey_buckets"));

    public static final Tag<EntityType<?>> RANCHABLES = EntityTypeTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static Tag<Block> getTag(String name) {
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static Tag<Block> getTag(ResourceLocation resourceLocation) {
        return new BlockTags.Wrapper(resourceLocation);
    }
}
