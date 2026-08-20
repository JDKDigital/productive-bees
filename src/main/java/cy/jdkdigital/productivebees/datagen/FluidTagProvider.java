package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class FluidTagProvider extends FluidTagsProvider
{
    public FluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ProductiveBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tagOf("c:crude_oil").addOptionalTag(id("c:fuels/crude_oil")).addOptionalElement(id("oritech:still_oil"));
        tagOf("c:honey").addElement(id("productivebees:honey")).addElement(id("productivebees:flowing_honey"));
    }

    private TagBuilder tagOf(String tagId) {
        return getOrCreateRawBuilder(TagKey.create(Registries.FLUID, Identifier.parse(tagId)));
    }

    private static Identifier id(String s) {
        return Identifier.parse(s);
    }

    @Override
    public String getName() {
        return "Productive Bees Fluid Tags Provider";
    }
}
