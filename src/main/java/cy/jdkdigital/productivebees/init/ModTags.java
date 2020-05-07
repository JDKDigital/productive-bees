package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {
    public static final Tag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final Tag<Block> REED_NESTS = getTag("reed_nests");
    public static final Tag<Block> WOOD_NESTS = getTag("wood_nests");
    public static final Tag<Block> SLIMY_NESTS = getTag("slimy_nests");
    public static final Tag<Block> GLOWSTONE_NESTS = getTag("glowstone_nests");
    public static final Tag<Block> NETHER_QUARTZ_NESTS = getTag("nether_quarts_nests");
    public static final Tag<Block> NETHER_BRICK_NESTS = getTag("nether_brick_nests");
    public static final Tag<Block> END_NESTS = getTag("end_nests");
    public static final Tag<Block> DRACONIC_NESTS = getTag("draconic_nests");

    public static final Tag<Block> ARID_FLOWERS = getTag("arid_flowers");
    public static final Tag<Block> SWAMP_FLOWERS = getTag("swamp_flowers");
    public static final Tag<Block> RIVER_FLOWERS = getTag("river_flowers");
    public static final Tag<Block> NETHER_FLOWERS = getTag("nether_flowers");
    public static final Tag<Block> END_FLOWERS = getTag("end_flowers");
    public static final Tag<Block> DRACONIC_FLOWERS = getTag("draconic_flowers");
    public static final Tag<Block> WITHER_FLOWERS = getTag("wither_flowers");

    private static Tag<Block> getTag(String resourceLocation) {
        return new BlockTags.Wrapper(new ResourceLocation(ProductiveBees.MODID, resourceLocation));
    }
}
