package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class PoiTagProvider extends PoiTypeTagsProvider
{
    public PoiTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ProductiveBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tagOf("productivebees:advanced_beehive").addElement(id("productivebees:advanced_beehive"));
    }

    private TagBuilder tagOf(String tagId) {
        return getOrCreateRawBuilder(TagKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.parse(tagId)));
    }

    private static Identifier id(String s) {
        return Identifier.parse(s);
    }

    @Override
    public String getName() {
        return "Productive Bees POI Type Tags Provider";
    }
}
