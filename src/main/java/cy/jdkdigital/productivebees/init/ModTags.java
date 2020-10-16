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
    public static final Tag<Block> REED_NESTS = getTag("reed_nests");
    public static final Tag<Block> COLD_NESTS = getTag("cold_nests");
    public static final Tag<Block> WOOD_NESTS = getTag("wood_nests");
    public static final Tag<Block> SLIMY_NESTS = getTag("slimy_nests");
    public static final Tag<Block> GLOWSTONE_NESTS = getTag("glowstone_nests");
    public static final Tag<Block> NETHER_QUARTZ_NESTS = getTag("nether_quartz_nests");
    public static final Tag<Block> NETHER_BRICK_NESTS = getTag("nether_brick_nests");
    public static final Tag<Block> SOUL_SAND_NESTS = getTag("soul_sand_nests");
    public static final Tag<Block> END_NESTS = getTag("end_nests");
    public static final Tag<Block> DRACONIC_NESTS = getTag("draconic_nests");

    public static final Tag<Block> FOREST_FLOWERS = getTag("forest_flowers");
    public static final Tag<Block> ARID_FLOWERS = getTag("arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getTag("swamp_flowers");
    public static final Tag<Block> SNOW_FLOWERS = getTag("snow_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getTag("river_flowers");
    public static final Tag<Block> GLOWING_FLOWERS = getTag("glowing_flowers");
    public static final Tag<Block> MAGMATIC_FLOWERS = getTag("magmatic_flowers");
    public static final Tag<Block> CRYSTALLINE_FLOWERS = getTag("crystalline_flowers");
    public static final Tag<Block> SOULED_FLOWERS = getTag("souled_flowers");
    public static final Tag<Block> GILDED_FLOWERS = getTag("gilded_flowers");
    public static final Tag<Block> FERRIC_FLOWERS = getTag("ferric_flowers");
    public static final Tag<Block> END_FLOWERS = getTag("end_flowers");
    public static final Tag<Block> DRACONIC_FLOWERS = getTag("draconic_flowers");
    public static final Tag<Block> WITHER_FLOWERS = getTag("wither_flowers");

    public static final Tag<Item> HONEYCOMBS = ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "honeycombs"));
    public static final Tag<Item> HONEY_BUCKETS = ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "honey_buckets"));

    public static final Tag<Fluid> HONEY = FluidTags.getCollection().getOrCreate(new ResourceLocation("forge", "honey"));

    public static final Tag<EntityType<?>> RANCHABLES = EntityTypeTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static Tag<Block> getTag(String name) {
        if (name.equals("nether_quarts_nests")) {
            name = "nether_quartz_nests";
        }
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static Tag<Block> getTag(ResourceLocation resourceLocation) {
        if (resourceLocation.getPath().equals("nether_quarts_nests")) {
            resourceLocation = new ResourceLocation(ProductiveBees.MODID, "nether_quartz_nests");
        }
        return BlockTags.getCollection().getOrCreate(resourceLocation);
    }
}
