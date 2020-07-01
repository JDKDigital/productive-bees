package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ModTags
{
    public static final ITag<Block> SOLITARY_NESTS = getTag("solitary_nests");
    public static final ITag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final ITag<Block> REED_NESTS = getTag("reed_nests");
    public static final ITag<Block> WOOD_NESTS = getTag("wood_nests");
    public static final ITag<Block> SLIMY_NESTS = getTag("slimy_nests");
    public static final ITag<Block> GLOWSTONE_NESTS = getTag("glowstone_nests");
    public static final ITag<Block> NETHER_QUARTZ_NESTS = getTag("nether_quarts_nests");
    public static final ITag<Block> NETHER_BRICK_NESTS = getTag("nether_brick_nests");
    public static final ITag<Block> END_NESTS = getTag("end_nests");
    public static final ITag<Block> DRACONIC_NESTS = getTag("draconic_nests");

    public static final ITag<Block> FOREST_FLOWERS = getTag("forest_flowers");
    public static final ITag<Block> ARID_FLOWERS = getTag("arid_flowers");
    public static final ITag<Block> SWAMP_FLOWERS = getTag("swamp_flowers");
    public static final ITag<Block> RIVER_FLOWERS = getTag("river_flowers");
    public static final ITag<Block> NETHER_FLOWERS = getTag("nether_flowers");
    public static final ITag<Block> END_FLOWERS = getTag("end_flowers");
    public static final ITag<Block> DRACONIC_FLOWERS = getTag("draconic_flowers");
    public static final ITag<Block> WITHER_FLOWERS = getTag("wither_flowers");

    public static final ITag<Item> HONEYCOMBS = ItemTags.getCollection().getOrCreate(new ResourceLocation("forge", "honeycombs"));

    public static final ITag<Fluid> HONEY = FluidTags.getCollection().getOrCreate(new ResourceLocation("forge", "honey"));

    private static ITag<Block> getTag(String resourceLocation) {
        return BlockTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, resourceLocation));
    }
}
