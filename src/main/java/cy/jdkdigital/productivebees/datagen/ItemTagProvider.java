package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> future, CompletableFuture<TagLookup<Block>> provider, ExistingFileHelper helper) {
        super(output, future, provider, ProductiveBees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(ModTags.HIVES_BLOCK, ModTags.HIVES);
        copy(ModTags.BOXES_BLOCK, ModTags.BOXES);
        copy(ModTags.CANVAS_HIVES_BLOCK, ModTags.CANVAS_HIVES);
        copy(ModTags.CANVAS_BOXES_BLOCK, ModTags.CANVAS_BOXES);
    }

    @Override
    public String getName() {
        return "Productive Bees Item Tags Provider";
    }
}
