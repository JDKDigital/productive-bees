package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

public class EntityTagProvider extends EntityTypeTagsProvider
{
    public EntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, ProductiveBees.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var magmaCubes = tag(ModTags.MAGMA_CUBES);
        var frogFood = tag(EntityTypeTags.FROG_FOOD);

        magmaCubes.add(EntityType.MAGMA_CUBE);
        frogFood.add(EntityType.BEE);

        tagOf("productivebees:animals").addTag(id("productivebees:ranchables")).addElement(id("minecraft:cat")).addElement(id("minecraft:chicken")).addElement(id("minecraft:dolphin")).addElement(id("minecraft:fox")).addElement(id("minecraft:hoglin")).addElement(id("minecraft:mule")).addElement(id("minecraft:mooshroom")).addElement(id("minecraft:ocelot")).addElement(id("minecraft:panda")).addElement(id("minecraft:parrot")).addElement(id("minecraft:pig")).addElement(id("minecraft:polar_bear")).addElement(id("minecraft:rabbit")).addElement(id("minecraft:turtle")).addElement(id("minecraft:wolf"));
        tagOf("productivebees:bee_encase_blacklist").addOptionalTag(id("c:capturing_not_supported")).addTag(id("minecraft:beehive_inhabitors")).addOptionalElement(id("minecolonies:citizen")).addOptionalElement(id("minecolonies:visitor")).addOptionalElement(id("minecolonies:mercenary")).addOptionalElement(id("ars_nouveau:starbuncle")).addOptionalElement(id("cobblemon:pokemon"));
        tagOf("productivebees:external_can_pollinate").addTag(id("productivebees:solitary_bees"));
        tagOf("productivebees:ranchables").addElement(id("minecraft:cow")).addElement(id("minecraft:donkey")).addElement(id("minecraft:horse")).addElement(id("minecraft:llama")).addElement(id("minecraft:goat")).addElement(id("minecraft:sheep")).addOptionalElement(id("dyenamics:sheep")).addOptionalElement(id("earthmobsmod:wooly_cow")).addOptionalElement(id("earthmobsmod:umbra_cow")).addOptionalElement(id("earthmobsmod:albino_cow")).addOptionalElement(id("earthmobsmod:cream_cow")).addOptionalElement(id("earthmobsmod:moobloom")).addOptionalElement(id("earthmobsmod:moolip")).addOptionalElement(id("earthmobsmod:horned_sheep")).addOptionalElement(id("earthmobsmod:jolly_llama"));
        tagOf("productivebees:solitary_bees").addElement(id("productivebees:ashy_mining_bee")).addElement(id("productivebees:blue_banded_bee")).addElement(id("productivebees:green_carpenter_bee")).addElement(id("productivebees:yellow_black_carpenter_bee")).addElement(id("productivebees:chocolate_mining_bee")).addElement(id("productivebees:digger_bee")).addElement(id("productivebees:leafcutter_bee")).addElement(id("productivebees:mason_bee")).addElement(id("productivebees:neon_cuckoo_bee")).addElement(id("productivebees:nomad_bee")).addElement(id("productivebees:reed_bee")).addElement(id("productivebees:resin_bee")).addElement(id("productivebees:sweat_bee")).addElement(id("productivebees:bumble_bee"));
        tagOf("c:withers").addElement(id("minecraft:wither"));
    }

    private TagBuilder tagOf(String tagId) {
        return getOrCreateRawBuilder(TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(tagId)));
    }

    private static Identifier id(String s) {
        return Identifier.parse(s);
    }

    @Override
    public String getName() {
        return "Productive Bees Entity Type Tags Provider";
    }
}
