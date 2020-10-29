package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;

public class ModTags
{
    public static final Tag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final Tag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final Tag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final Tag<Block> WOOD_NESTS = getTag("nests/wood_nests");

    public static final Tag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final Tag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final Tag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");

    public static final Tag<Item> HONEYCOMBS = ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "honeycombs"));
    public static final Tag<Item> HONEY_BUCKETS = ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "honey_buckets"));

    public static final Tag<Fluid> HONEY = FluidTags.getCollection().getOrCreate(new ResourceLocation("forge", "honey"));

    public static final Tag<EntityType<?>> RANCHABLES = EntityTypeTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static Tag<Block> getTag(String name) {
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static Tag<Block> getTag(ResourceLocation resourceLocation) {
        return BlockTags.getCollection().getOrCreate(resourceLocation);
    }
}
