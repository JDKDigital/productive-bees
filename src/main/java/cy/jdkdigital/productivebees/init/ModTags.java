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

    public static final ITag<Block> SOLITARY_OVERWORLD_NESTS = getTag("solitary_overworld_nests");
    public static final ITag<Block> REED_NESTS = getTag("nests/reed_nests");
    public static final ITag<Block> COLD_NESTS = getTag("nests/cold_nests");
    public static final ITag<Block> WOOD_NESTS = getTag("nests/wood_nests");

    public static final ITag<Block> FOREST_FLOWERS = getTag("flowers/forest_flowers");
    public static final ITag<Block> ARID_FLOWERS = getTag("flowers/arid_flowers");
    public static final ITag<Block> SWAMP_FLOWERS = getTag("flowers/swamp_flowers");
    public static final ITag<Block> SNOW_FLOWERS = getTag("flowers/snow_flowers");
    public static final ITag<Block> RIVER_FLOWERS = getTag("flowers/river_flowers");
    public static final ITag<Block> QUARRY = getTag("flowers/quarry");

    public static final ITag<Item> HONEYCOMBS = ItemTags.makeWrapperTag("forge:honeycombs");

    public static final ITag<Fluid> HONEY = FluidTags.makeWrapperTag("forge:honey");

    public static final ITag<EntityType<?>> RANCHABLES = EntityTypeTags.func_232896_a_(ProductiveBees.MODID + ":ranchables");

    public static ITag<Block> getTag(String name) {
        if (name.equals("nether_quarts_nests")) {
            name = "nether_quartz_nests";
        }
        else if (name.equals("glowtone_nests")) {
            name = "glowstone_nests";
        }
        return getTag(new ResourceLocation(ProductiveBees.MODID, name));
    }

    public static ITag<Block> getTag(ResourceLocation resourceLocation) {
        if (resourceLocation.getPath().equals("nether_quarts_nests")) {
            resourceLocation = new ResourceLocation(ProductiveBees.MODID, "nether_quartz_nests");
        }
        else if (resourceLocation.getPath().equals("glowtone_nests")) {
            resourceLocation = new ResourceLocation(ProductiveBees.MODID, "glowstone_nests");
        }
        return BlockTags.getCollection().getOrCreate(resourceLocation);
    }
}
