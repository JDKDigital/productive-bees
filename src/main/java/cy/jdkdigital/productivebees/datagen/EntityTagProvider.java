package cy.jdkdigital.productivebees.datagen;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EntityTagProvider extends EntityTypeTagsProvider
{
    public EntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper helper) {
        super(output, provider, ProductiveBees.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var magmaCubes = tag(ModTags.MAGMA_CUBES);
        var frogFood = tag(EntityTypeTags.FROG_FOOD);

        magmaCubes.add(EntityType.MAGMA_CUBE);
        frogFood.add(EntityType.BEE);
    }

    @Override
    public String getName() {
        return "Productive Bees Entity Type Tags Provider";
    }
}
