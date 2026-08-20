package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.common.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.datagen.BeeProvider;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BeeProduceRecipeProvider extends RecipeProvider
{
    private static final TagKey<Item> POLLENS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "pollens"));

    public BeeProduceRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    // Bees whose comb carries an extra mod-item roll, so they are emitted separately from the
    // generic loop. Those whose mod is on the datagen classpath go through emitWithBonus; the
    // rest are still hand-written JSON in src/main/resources/... .
    private static final Set<String> CUSTOM_BONUS_COMBS = Set.of(
            "neovitae/hellfire",
            "forbidden_arcanus/stellarite",
            "irons_spellbooks/arcane_essence",
            "tombstone/grave"
    );

    // Bees whose comb roll isn't guaranteed. Anything absent rolls at 1.0.
    private static final Map<String, Float> COMB_CHANCE = Map.of(
            "enderio_endergy/crystalline_alloy", 0.8f,
            "enderio_endergy/melodic_alloy", 0.4f,
            "enderio_endergy/stellar_alloy", 0.2f,
            "enderio_endergy/vivid_alloy", 0.6f
    );

    @Override
    protected void buildRecipes() {
        for (BeeProvider.BeeConfig config : BeeProvider.uniqueConfigs()) {
            if (!config.createComb()) {
                continue;
            }
            if (CUSTOM_BONUS_COMBS.contains(config.name())) {
                continue;
            }
            emit(config.name());
        }
        // Bonus items resolvable now that neovitae and forbidden_arcanus are on the datagen classpath.
        emitWithBonus("neovitae/hellfire", "neovitae:hellforged_parts", 0.01f);
        emitWithBonus("forbidden_arcanus/stellarite", "forbidden_arcanus:eternal_stella", 0.01f);
        emitVanillaBee();
        emitSpecialBees();
    }

    private void emitVanillaBee() {
        Identifier beeId = Identifier.fromNamespaceAndPath("minecraft", "bee");
        List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
        outputs.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.HONEYCOMB), 1, 1, 1f));
        outputs.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(POLLENS)), 1, 1, 0.15f));

        BeeIngredient vanillaIng = new BeeIngredient(EntityType.BEE, beeId, false);
        AdvancedBeehiveRecipe recipe = new AdvancedBeehiveRecipe(() -> vanillaIng, outputs);

        this.output.accept(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_produce/bee")),
                recipe, null, new ICondition[0]);
    }

    /** Bees whose produce isn't a {@code configurable_honeycomb[bee_type=...]} — entity-type subclasses
     * and configurable bees with noComb() but custom outputs. */
    /** Same as {@link #emit} but with a mod item slotted between the comb and the pollen roll. */
    private void emitWithBonus(String beeName, String bonusItemId, float chance) {
        Identifier beeId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, beeName);
        DataComponentPatch patch = DataComponentPatch.builder().set(ModDataComponents.BEE_TYPE.get(), beeId).build();
        var items = this.registries.lookupOrThrow(Registries.ITEM);

        List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
        outputs.add(new TagOutputRecipe.ChancedOutput(ComponentIngredient.of(patch, ModItems.CONFIGURABLE_HONEYCOMB.get()), 1, 1, 1f));
        outputs.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(items.getOrThrow(
                ResourceKey.create(Registries.ITEM, Identifier.parse(bonusItemId))).value()), 1, 1, chance));
        outputs.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(items.getOrThrow(POLLENS)), 1, 1, 0.05f));

        this.output.accept(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_produce/" + beeName + "_bee")),
                new AdvancedBeehiveRecipe(stubBeeIngredient(beeId), outputs), null,
                new ICondition[] { new BeeExistsCondition(beeId) });
    }

    private void emitSpecialBees() {
        Ingredient pollens = Ingredient.of(this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(POLLENS));

        // Entity-type bees (own EntityType, not ConfigurableBee).
        emitSpecial("creeper_bee", ModEntities.CREEPER_BEE.get(), "productivebees:creeper_bee", List.of(
                outputItem(ModItems.HONEYCOMB_POWDERY.get(), 1f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.05f)
        ));
        emitSpecial("rancher_bee", ModEntities.RANCHER_BEE.get(), "productivebees:rancher_bee", List.of(
                outputItem(ModItems.HONEYCOMB_MILKY.get(), 1f)
        ));

        // Burly bees fill a hive with plain honeycomb instead of a comb of their own.
        emitSpecial("burly_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:burly", List.of(
                new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.HONEYCOMB), 4, 4, 1f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.05f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "burly")));

        // Configurable bees with noComb() flag but custom produce.
        emitSpecial("ghostly_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:ghostly", List.of(
                outputItem(ModItems.HONEYCOMB_GHOSTLY.get(), 1f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.05f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "ghostly")));
        emitSpecial("sponge_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:sponge", List.of(
                outputItem(Items.SPONGE, 0.05f),
                outputItem(Items.WET_SPONGE, 0.05f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "sponge")));
        emitSpecial("sugarbag_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:sugarbag", List.of(
                outputItem(ModItems.SUGARBAG_HONEYCOMB.get(), 0.3f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.15f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "sugarbag")));
        emitSpecial("pepto_bismol_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:pepto_bismol", List.of(
                outputItem(ModItems.SUGARBAG_HONEYCOMB.get(), 0.4f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.05f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "sugarbag")),
           new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "pepto_bismol")));
        emitSpecial("ribbeet_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:ribbeet", List.of(
                outputItem(Items.OCHRE_FROGLIGHT, 0.05f),
                outputItem(Items.PEARLESCENT_FROGLIGHT, 0.05f),
                outputItem(Items.VERDANT_FROGLIGHT, 0.05f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "ribbeet")));
        emitSpecial("water_bee", ModEntities.CONFIGURABLE_BEE.get(), "productivebees:fluids/water", List.of(
                outputItem(Items.COD, 0.1f),
                outputItem(Items.SALMON, 0.1f),
                outputItem(Items.TROPICAL_FISH, 0.1f),
                outputItem(Items.PUFFERFISH, 0.05f),
                outputItem(Items.KELP, 0.25f)
        ), new BeeExistsCondition(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "fluids/water")));

        // External-mod bee — entity type and id come from the shiny mod, gated behind mod_loaded.
        emitSpecial("shiny_bee", EntityType.BEE, "shiny:shiny_bee", List.of(
                new TagOutputRecipe.ChancedOutput(Ingredient.of(Items.HONEYCOMB), 4, 4, 1f),
                new TagOutputRecipe.ChancedOutput(pollens, 1, 1, 0.15f)
        ), new ModLoadedCondition("shiny"));
    }

    private static TagOutputRecipe.ChancedOutput outputItem(ItemLike item, float chance) {
        return new TagOutputRecipe.ChancedOutput(Ingredient.of(item), 1, 1, chance);
    }

    private void emitSpecial(String recipePath, EntityType<? extends Bee> entityType, String beeId, List<TagOutputRecipe.ChancedOutput> outputs, ICondition... conditions) {
        Identifier id = Identifier.parse(beeId);
        BeeIngredient ing = new BeeIngredient(entityType, id, entityType == ModEntities.CONFIGURABLE_BEE.get());
        AdvancedBeehiveRecipe recipe = new AdvancedBeehiveRecipe(() -> ing, outputs);
        this.output.accept(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_produce/" + recipePath)),
                recipe, null, conditions);
    }

    private void emit(String beeName) {
        Identifier beeId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, beeName);

        DataComponentPatch patch = DataComponentPatch.builder()
                .set(ModDataComponents.BEE_TYPE.get(), beeId)
                .build();

        List<TagOutputRecipe.ChancedOutput> outputs = new ArrayList<>();
        outputs.add(new TagOutputRecipe.ChancedOutput(ComponentIngredient.of(patch, ModItems.CONFIGURABLE_HONEYCOMB.get()), 1, 1, COMB_CHANCE.getOrDefault(beeName, 1f)));
        outputs.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(this.registries.lookupOrThrow(Registries.ITEM).getOrThrow(POLLENS)), 1, 1, 0.05f));

        AdvancedBeehiveRecipe recipe = new AdvancedBeehiveRecipe(stubBeeIngredient(beeId), outputs);

        ICondition[] conditions = new ICondition[] { new BeeExistsCondition(beeId) };
        this.output.accept(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_produce/" + beeName + "_bee")),
                recipe, null, conditions);
    }

    private static Supplier<BeeIngredient> stubBeeIngredient(Identifier beeId) {
        BeeIngredient ing = new BeeIngredient(ModEntities.CONFIGURABLE_BEE.get(), beeId, true);
        return () -> ing;
    }

    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new BeeProduceRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "PB Bee Produce Recipes";
        }
    }
}
