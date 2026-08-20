package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends BiomeTagsProvider
{
    public BiomeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ProductiveBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tagOf("productivebees:beebee_spawn_biomes").addOptionalElement(id("allthemodium:soul_sand_valley")).addOptionalElement(id("allthemodium:desert_hills")).addOptionalElement(id("allthemodium:desert")).addOptionalElement(id("allthemodium:the_other"));
        tagOf("productivebees:is_beach_or_river").addTag(id("minecraft:is_beach")).addTag(id("minecraft:is_river"));
        tagOf("productivebees:is_mountain_or_hill").addTag(id("minecraft:is_mountain")).addTag(id("minecraft:is_hill"));
        tagOf("productivebees:warm_ocean").addElement(id("minecraft:warm_ocean"));
    }

    private TagBuilder tagOf(String tagId) {
        return getOrCreateRawBuilder(TagKey.create(Registries.BIOME, Identifier.parse(tagId)));
    }

    private static Identifier id(String s) {
        return Identifier.parse(s);
    }

    @Override
    public String getName() {
        return "Productive Bees Biome Tags Provider";
    }
}
