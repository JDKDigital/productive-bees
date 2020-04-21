package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {

    public static final String ADVANCED_BEEHIVES = "advanced_beehives";
    public static final String EXPANSION_BOXES = "expansion_boxes";

    public static final String SOLITARY_HIVES = "solitary_hives";

    public static final String SOLITARY_OVERWORLD_NESTS = "solitary_overworld_nests";
    public static final String GROUND_NESTS = "ground_nests";
    public static final String ARID_NESTS = "arid_nests";
    public static final String SLIMY_NESTS = "slimy_nests";
    public static final String GLOWSTONE_NESTS = "glowstone_nests";
    public static final String NETHER_BRICK_NESTS = "nether_brick_nests";
    public static final String END_NESTS = "end_nests";
    public static final String DRACONIC_NESTS = "draconic_nests";

    public static final String ARID_FLOWERS = "arid_flowers";
    public static final String SWAMP_FLOWERS = "swamp_flowers";
    public static final String END_FLOWERS = "end_flowers";
    public static final String DRACONIC_FLOWERS = "draconic_flowers";
    public static final String WITHER_FLOWERS = "wither_flowers";

    public static Tag<Block> getTag(String resourceLocation) {
        return BlockTags.getCollection().getOrCreate(new ResourceLocation(ProductiveBees.MODID, resourceLocation));
    }
}
