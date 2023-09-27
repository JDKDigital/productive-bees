package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.common.crafting.conditions.FluidTagEmptyCondition;
import cy.jdkdigital.productivebees.datagen.recipe.builder.AbstractRecipeBuilder;
import cy.jdkdigital.productivebees.datagen.recipe.builder.CentrifugeRecipeBuilder;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CentrifugeRecipeProvider extends RecipeProvider {
    public CentrifugeRecipeProvider(PackOutput gen) {
        super(gen);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Vanilla
//        CentrifugeRecipeBuilder.item(Items.HONEYCOMB)
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.WAX)))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey", 100))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb"));
//
//        CentrifugeRecipeBuilder.configurable("skeletal")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.BONE_MEAL), 50))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_bone"));
//
//        CentrifugeRecipeBuilder.configurable("draconic")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModItems.DRACONIC_DUST.get()), 30))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_draconic"));
//
//        CentrifugeRecipeBuilder.configurable("ender")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.ENDER_PEARL), 20))
//                .withCondition(new NotCondition(new ModLoadedCondition("integrateddynamics")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_ender"));
//
//        CentrifugeRecipeBuilder.configurable("experience")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.EXPERIENCE_BOTTLE), 30))
//                .withCondition(new FluidTagEmptyCondition("forge:experience"))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey", 0))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_experience"));
//
//        CentrifugeRecipeBuilder.configurable("experience")
//                .withCondition(new FluidTagEmptyCondition("forge:experience"))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("#forge:experience", 100))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_experience_fluid"));
//
//        CentrifugeRecipeBuilder.configurable("coal")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.COAL), 60))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_fossilised"));
//
//        CentrifugeRecipeBuilder.configurable("frosty")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.SNOWBALL), 60, 2, 4))
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.ICE), 40))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_frosty"));
//
//        CentrifugeRecipeBuilder.item(ModItems.HONEYCOMB_GHOSTLY.get())
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.GHAST_TEAR), 5))
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.WAX)))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_ghostly"));
//
//        CentrifugeRecipeBuilder.configurable("magmatic")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.MAGMA_CREAM), 30))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_magmatic"));
//
//        CentrifugeRecipeBuilder.configurable("obsidian")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.OBSIDIAN), 40))
//                .withCondition(new TagEmptyCondition("forge:dusts/obsidian"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_obsidian"));
//
//        CentrifugeRecipeBuilder.configurable("obsidian")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.OBSIDIAN), 50, 1, 5))
//                .withCondition(new NotCondition(new TagEmptyCondition("forge:dusts/obsidian")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_obsidian_dust"));
//
//        CentrifugeRecipeBuilder.item(ModItems.HONEYCOMB_GHOSTLY.get())
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.GUNPOWDER), 50))
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.WAX)))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("productivebees:honey"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_powdery"));
//
//        CentrifugeRecipeBuilder.configurable("prismarine")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.PRISMARINE_SHARD), 20))
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.PRISMARINE_CRYSTALS), 5))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_prismarine"));
//
//        CentrifugeRecipeBuilder.configurable("zombie")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.ROTTEN_FLESH), 60))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_rotten"));
//
//        CentrifugeRecipeBuilder.configurable("silky")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.STRING), 50))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_silky"));
//
//        CentrifugeRecipeBuilder.configurable("slimy")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.SLIME_BALL), 20))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_slimy"));
//
//        CentrifugeRecipeBuilder.configurable("withered")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.WITHER_SKULL_FRAGMENTS), 30))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/honeycomb_withered"));
//
//        // Dusts
//        CentrifugeRecipeBuilder.configurable("blazing")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.BLAZE_POWDER), 30))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/dusts/honeycomb_blazing"));
//
//        CentrifugeRecipeBuilder.configurable("glowing")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.GLOWSTONE_DUST)))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/dusts/honeycomb_glowing"));
//
//        CentrifugeRecipeBuilder.configurable("niter")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.GLOWSTONE_DUST)))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/dusts/honeycomb_niter"));
//
//        // Material
//        CentrifugeRecipeBuilder.configurable("silicon")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.SILICON), 50))
//                .withCondition(new NotCondition(new TagEmptyCondition("forge:silicon")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/material/honeycomb_silicon"));
//
//        // Fluid
////        CentrifugeRecipeBuilder.configurable("oily")
////                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.SILICON), 50))
////                .withCondition(new NotCondition(new FluidTagEmptyCondition("forge:oil")))
////                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/fluid/honeycomb_oily"));
//
//
//        // AE2
//        CentrifugeRecipeBuilder.configurable("spacial")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("ae2:certus_crystal_seed", 100, 1, 2))
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("ae2:certus_quartz_dust", 50))
//                .withCondition(new ModLoadedCondition("ae2"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ae2/honeycomb_spacial"));
//
//        CentrifugeRecipeBuilder.configurable("fluix")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("ae2:fluix_crystal", 20))
//                .withCondition(new ModLoadedCondition("ae2"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ae2/honeycomb_fluix"));
//
//        // Ars Nouveau
//        CentrifugeRecipeBuilder.configurable("arcane")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.SOURCE_GEM), 50))
//                .withCondition(new ModLoadedCondition("ars_nouveau"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ars_nouveau/honeycomb_arcane"));
//
//        // Astral
//        CentrifugeRecipeBuilder.configurable("starmetal")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("astralsorcery:starmetal_ingot", 10))
//                .withCondition(new ModLoadedCondition("astralsorcery"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/astralsorcery/honeycomb_starmetal"));
//
//        // ATM
//        CentrifugeRecipeBuilder.configurable("allthemodium")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("allthemodium:allthemodium_nugget", 10, 1, 2))
//                .withCondition(new ModLoadedCondition("allthemodium"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/allthemodium/honeycomb_allthemodium"));
//
//        CentrifugeRecipeBuilder.configurable("vibranium")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("allthemodium:vibranium_nugget", 10, 1, 2))
//                .withCondition(new ModLoadedCondition("allthemodium"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/allthemodium/honeycomb_vibranium"));
//
//        CentrifugeRecipeBuilder.configurable("unobtainium")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("allthemodium:unobtainium_nugget", 10, 1, 2))
//                .withCondition(new ModLoadedCondition("allthemodium"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/allthemodium/honeycomb_unobtainium"));
//
//        // Beyond Earth
//        CentrifugeRecipeBuilder.configurable("desh")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("#forge:raw_materials/desh", 30))
//                .withCondition(new NotCondition(new TagEmptyCondition("forge:raw_materials/desh")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ad_astra/honeycomb_desh"));
//
//        CentrifugeRecipeBuilder.configurable("ostrum")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("#forge:raw_materials/ostrum", 30))
//                .withCondition(new NotCondition(new TagEmptyCondition("forge:raw_materials/ostrum")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ad_astra/honeycomb_ostrum"));
//
//        CentrifugeRecipeBuilder.configurable("calorite")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("#forge:raw_materials/calorite", 30))
//                .withCondition(new NotCondition(new TagEmptyCondition("forge:raw_materials/calorite")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/ad_astra/honeycomb_calorite"));
//
//        CentrifugeRecipeBuilder.configurable("oily")
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("ad_astra:oil"))
//                .withCondition(new ModLoadedCondition("ad_astra"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/fluid/honeycomb_oily"));
//
//        // Blood Magic
//        CentrifugeRecipeBuilder.configurable("bloody")
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("#forge:life"))
//                .withCondition(new NotCondition(new FluidTagEmptyCondition("forge:life")))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/blodmagic/honeycomb_bloody"));
//
//        // Draconic Evolution
//        CentrifugeRecipeBuilder.configurable("draconium")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("#forge:nuggets/draconium", 100, 1, 3))
//                .withCondition(new ModLoadedCondition("draconicevolution"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/draconicevolution/honeycomb_draconium"));
//
//        CentrifugeRecipeBuilder.configurable("awakened")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("#forge:nuggets/draconium_awakened", 100, 1, 3))
//                .withCondition(new ModLoadedCondition("draconicevolution"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/draconicevolution/honeycomb_awakened"));
//
//        CentrifugeRecipeBuilder.configurable("chaos")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("draconicevolution:small_chaos_frag", 10))
//                .withCondition(new ModLoadedCondition("draconicevolution"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/draconicevolution/honeycomb_chaos"));
//
//        // Integrated Dynamics
//        CentrifugeRecipeBuilder.configurable("ender")
//                .addOutput(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(Items.ENDER_PEARL), 20))
//                .withCondition(new ModLoadedCondition("integrateddynamics"))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("integrateddynamics:liquid_chorus"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/integrateddynamics/honeycomb_ender"));
//
//        CentrifugeRecipeBuilder.configurable("menril")
//                .addOutput(new AbstractRecipeBuilder.ModItemOutput("integrateddynamics:crystalized_menril_chunk", 10))
//                .withCondition(new ModLoadedCondition("integrateddynamics"))
//                .setFluidOutput(new AbstractRecipeBuilder.FluidOutput("integrateddynamics:menril_resin"))
//                .save(consumer, new ResourceLocation(ProductiveBees.MODID, "centrifuge/integrateddynamics/honeycomb_menril"));


    }
}
