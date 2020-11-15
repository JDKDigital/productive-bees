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
    public static final INamedTag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final INamedTag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final INamedTag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final INamedTag<Block> WOOD_NESTS = getTag("nests/wood_nests");

    public static final INamedTag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final INamedTag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final INamedTag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final INamedTag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final INamedTag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");
    public static final INamedTag<Block> QUARRY = getTag("flowers/quarry");

    public static final INamedTag<Item> HONEYCOMBS = ItemTags.createOptional(new ResourceLocation("forge", "honeycombs"));

    public static final INamedTag<Fluid> HONEY = FluidTags.createOptional(new ResourceLocation("forge", "honey"));

    public static final INamedTag<EntityType<?>> RANCHABLES = EntityTypeTags.createOptional(new ResourceLocation(ProductiveBees.MODID, "ranchables"));

    public static INamedTag<Block> getTag(String name) {
        if (name.equals("nether_quarts_nests")) {
            name = "nether_quartz_nests";
        }
        else if (name.equals("glowtone_nests")) {
            name = "glowstone_nests";
        }
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static INamedTag<Block> getTag(ResourceLocation resourceLocation) {
        if (resourceLocation.getPath().equals("nether_quarts_nests")) {
            resourceLocation = new ResourceLocation(ProductiveBees.MODID, "nether_quartz_nests");
        }
        else if (resourceLocation.getPath().equals("glowtone_nests")) {
            resourceLocation = new ResourceLocation(ProductiveBees.MODID, "glowstone_nests");
        }
        return BlockTags.createOptional(resourceLocation);
    }
}
