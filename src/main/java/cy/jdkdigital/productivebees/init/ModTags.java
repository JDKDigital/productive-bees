package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {

    public static final String SOLITARY_HIVES = "solitary_hives";

    public static final String GROUND_NESTS = "ground_nests";
    public static final String ARID_NESTS = "arid_nests";
    public static final String SLIMY_NESTS = "slimy_nests";
    public static final String GLOWSTONE_NESTS = "glowstone_nests";
    public static final String NETHER_BRICK_NESTS = "nether_brick_nests";
    public static final String END_NESTS = "end_nests";
    public static final String OBSIDIAN_NESTS = "obsidian_nests";

    public static final String DESERT_FLOWERS = "desert_flower";

    public static Tag<Block> getTag(String resourceLocation) {
        return BlockTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, resourceLocation));
    }
}
