package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;

public class ModTags
{
    public static final INamedTag<Block> SOLITARY_NESTS = getTag("solitary_nests");
    public static final INamedTag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final INamedTag<Block> REED_NESTS = getTag("reed_nests");
    public static final INamedTag<Block> COLD_NESTS = getTag("cold_nests");
    public static final INamedTag<Block> WOOD_NESTS = getTag("wood_nests");
    public static final INamedTag<Block> SLIMY_NESTS = getTag("slimy_nests");
    public static final INamedTag<Block> GLOWSTONE_NESTS = getTag("glowstone_nests");
    public static final INamedTag<Block> NETHER_QUARTZ_NESTS = getTag("nether_quarts_nests");
    public static final INamedTag<Block> NETHER_BRICK_NESTS = getTag("nether_brick_nests");
    public static final INamedTag<Block> END_NESTS = getTag("end_nests");
    public static final INamedTag<Block> DRACONIC_NESTS = getTag("draconic_nests");

    public static final INamedTag<Block> FOREST_FLOWERS = getTag("forest_flowers");
    public static final INamedTag<Block> ARID_FLOWERS = getTag("arid_flowers");
    public static final INamedTag<Block> SWAMP_FLOWERS = getTag("swamp_flowers");
    public static final INamedTag<Block> SNOW_FLOWERS = getTag("snow_flowers");
    public static final INamedTag<Block> RIVER_FLOWERS = getTag("river_flowers");
    public static final INamedTag<Block> NETHER_FLOWERS = getTag("nether_flowers");
    public static final INamedTag<Block> END_FLOWERS = getTag("end_flowers");
    public static final INamedTag<Block> DRACONIC_FLOWERS = getTag("draconic_flowers");
    public static final INamedTag<Block> WITHER_FLOWERS = getTag("wither_flowers");

    public static final INamedTag<Item> HONEYCOMBS = ItemTags.makeWrapperTag("forge:honeycombs");
    public static final INamedTag<Item> HONEY_BUCKETS = ItemTags.makeWrapperTag("forge:honey_buckets");

    public static final INamedTag<Fluid> HONEY = FluidTags.makeWrapperTag("forge:honey");

    private static INamedTag<Block> getTag(String resourceLocation) {
        return BlockTags.makeWrapperTag(ProductiveBees.MODID + ":" + resourceLocation);
    }
}
