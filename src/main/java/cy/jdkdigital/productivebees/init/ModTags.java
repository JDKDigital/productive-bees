package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class ModTags
{
    public static final INamedTag<Block> SOLITARY_NESTS = getTag("solitary_nests");
    public static final INamedTag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final INamedTag<Block> REED_NESTS = getTag("reed_nests");
    public static final INamedTag<Block> COLD_NESTS = getTag("cold_nests");
    public static final INamedTag<Block> WOOD_NESTS = getTag("wood_nests");
    public static final INamedTag<Block> SLIMY_NESTS = getTag("slimy_nests");
    public static final INamedTag<Block> GLOWING_NESTS = getTag("glowstone_nests");
    public static final INamedTag<Block> NETHER_QUARTZ_NESTS = getTag("nether_quarts_nests");
    public static final INamedTag<Block> NETHER_GOLD_NESTS = getTag("nether_quarts_nests");
    public static final INamedTag<Block> NETHER_BRICK_NESTS = getTag("nether_brick_nests");
    public static final INamedTag<Block> END_NESTS = getTag("end_nests");
    public static final INamedTag<Block> DRACONIC_NESTS = getTag("draconic_nests");

    public static final INamedTag<Block> FOREST_FLOWERS = getTag("forest_flowers");
    public static final INamedTag<Block> ARID_FLOWERS = getTag("arid_flowers");
    public static final INamedTag<Block> SWAMP_FLOWERS = getTag("swamp_flowers");
    public static final INamedTag<Block> SNOW_FLOWERS = getTag("snow_flowers");
    public static final INamedTag<Block> RIVER_FLOWERS = getTag("river_flowers");
    public static final INamedTag<Block> GLOWING_FLOWERS = getTag("glowing_flowers");
    public static final INamedTag<Block> MAGMATIC_FLOWERS = getTag("magmatic_flowers");
    public static final INamedTag<Block> CRYSTALLINE_FLOWERS = getTag("crystalline_flowers");
    public static final INamedTag<Block> GILDED_FLOWERS = getTag("gilded_flowers");
    public static final INamedTag<Block> FERRIC_FLOWERS = getTag("ferric_flowers");
    public static final INamedTag<Block> END_FLOWERS = getTag("end_flowers");
    public static final INamedTag<Block> DRACONIC_FLOWERS = getTag("draconic_flowers");
    public static final INamedTag<Block> WITHER_FLOWERS = getTag("wither_flowers");

    public static final INamedTag<Item> HONEYCOMBS = ItemTags.makeWrapperTag("forge:honeycombs");
    public static final INamedTag<Item> HONEY_BUCKETS = ItemTags.makeWrapperTag("forge:honey_buckets");

    public static final INamedTag<Fluid> HONEY = FluidTags.makeWrapperTag("forge:honey");

    public static final INamedTag<EntityType<?>> RANCHABLES = EntityTypeTags.func_232896_a_(ProductiveBees.MODID + ":ranchables");

    public static INamedTag<Block> getTag(String name) {
        return BlockTags.makeWrapperTag(ProductiveBees.MODID + ":" + name);
    }

    public static INamedTag<Block> getTag(ResourceLocation resourceLocation) {
        return BlockTags.makeWrapperTag(resourceLocation.toString());
    }
}
