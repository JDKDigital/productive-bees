package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, ProductiveBees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var hives = tag(ModTags.HIVES_BLOCK);
        var boxes = tag(ModTags.BOXES_BLOCK);
        ModBlocks.HIVELIST.forEach((modid, strings) -> {
            strings.forEach((name, type) -> {
                name = modid.equals(ProductiveBees.MODID) ? name : modid + "_" + name;
                hives.addOptional(new ResourceLocation(ProductiveBees.MODID, "advanced_" + name + "_beehive"));
                boxes.addOptional(new ResourceLocation(ProductiveBees.MODID, "expansion_box_" + name));
            });
            hives.addTag(ModTags.CANVAS_HIVES_BLOCK);
            boxes.addTag(ModTags.CANVAS_BOXES_BLOCK);
        });

        var canvasHives = tag(ModTags.CANVAS_HIVES_BLOCK);
        ModBlocks.hiveStyles.forEach(style -> canvasHives.addOptional(new ResourceLocation(ProductiveBees.MODID, "advanced_" + style + "_canvas_beehive")));
        var canvasBoxes = tag(ModTags.CANVAS_BOXES_BLOCK);
        ModBlocks.hiveStyles.forEach(style -> canvasBoxes.addOptional(new ResourceLocation(ProductiveBees.MODID, "expansion_box_" + style + "_canvas")));
    }

    @Override
    public String getName() {
        return "Productive Bees Block Tags Provider";
    }
}
