package cy.jdkdigital.productivebees.datagen.recipe.provider;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.conditions.BeeExistsCondition;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import org.jspecify.annotations.Nullable;
import rearth.oritech.datagen.builders.AtomicForgeRecipeBuilder;
import rearth.oritech.datagen.builders.FoundryRecipeBuilder;
import rearth.oritech.datagen.builders.FragmentForgeRecipeBuilder;
import rearth.oritech.datagen.builders.OritechRecipeBuilder;
import rearth.oritech.init.ItemContent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Emits the Oritech machine recipes that upgrade a bee into an Oritech bee, using Oritech's own
 * recipe builders so the JSON shape follows theirs. Each time is Oritech's own for the material
 * the bee stands in for, which is not always the builder default.
 */
public class OritechRecipeProvider extends RecipeProvider
{
    /** Oritech's builders write to their machine's own folder; PB keeps compat recipes under the mod's name. */
    private static final String FOLDER = "oritech";

    private static final int FOUNDRY_TIME = 80;
    private static final int FRAGMENT_FORGE_TIME = 60;
    private static final int ATOMIC_FORGE_TIME = 240;

    public OritechRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        foundry("adamant_bee", "oritech/adamant", bee("gems/diamond"), bee("raw_materials/nickel"));
        foundry("biosteel_bee", "oritech/biosteel", item(ItemContent.RAW_BIOPOLYMER), bee("raw_materials/iron"));
        foundry("duratium_bee", "oritech/duratium", bee("raw_materials/platinum"), bee("raw_materials/netherite"));
        foundry("energite_bee", "oritech/energite", bee("raw_materials/nickel"), bee("oritech/fluxite"));
        fragmentForge("fluxite_bee", "oritech/fluxite", bee("raw_materials/platinum"));
        atomicForge("prometheum_bee", "oritech/prometheum", bee("oritech/strange_matter"), item(ItemContent.OVERCHARGED_CRYSTAL), item(ItemContent.OVERCHARGED_CRYSTAL));
    }

    private void foundry(String name, String resultBee, Input... inputs) {
        export(new FoundryRecipeBuilder(this.registries).time(FOUNDRY_TIME), name, resultBee, inputs);
    }

    private void fragmentForge(String name, String resultBee, Input... inputs) {
        export(new FragmentForgeRecipeBuilder(this.registries).time(FRAGMENT_FORGE_TIME), name, resultBee, inputs);
    }

    private void atomicForge(String name, String resultBee, Input... inputs) {
        export(new AtomicForgeRecipeBuilder(this.registries).time(ATOMIC_FORGE_TIME), name, resultBee, inputs);
    }

    private void export(OritechRecipeBuilder builder, String name, String resultBee, Input... inputs) {
        Identifier resultBeeId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, resultBee);

        List<ICondition> conditions = new ArrayList<>();
        conditions.add(new ModLoadedCondition("oritech"));
        conditions.add(new BeeExistsCondition(resultBeeId));
        for (Input input : inputs) {
            builder.input(input.ingredient);
            if (input.beeId != null) conditions.add(new BeeExistsCondition(input.beeId));
        }
        builder.result(new ItemStackTemplate(ModItems.CONFIGURABLE_SPAWN_EGG.get(), BeeCreator.getSpawnEggPatch(resultBeeId)));

        RecipeOutput output = new FolderedOutput(this.output.withConditions(conditions.toArray(ICondition[]::new)), FOLDER);
        builder.export(output, name, ProductiveBees.MODID);
    }

    /** A configurable bee's spawn egg, matched by its bee type. */
    private static Input bee(String beePath) {
        Identifier beeId = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, beePath);
        return new Input(BeeCreator.getSpawnEggIngredient(beeId, true), beeId);
    }

    /** A plain item. */
    private static Input item(ItemLike item) {
        return new Input(Ingredient.of(item), null);
    }

    /** One {@code itemInputs} entry; {@code beeId} is set when it is a bee, for the bee_exists condition. */
    private record Input(Ingredient ingredient, @Nullable Identifier beeId) {}

    /** Moves recipes Oritech's builders emit into a folder, since they name the file after the machine alone. */
    private record FolderedOutput(RecipeOutput delegate, String folder) implements RecipeOutput
    {
        @Override
        public void accept(ResourceKey<Recipe<?>> id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            delegate.accept(ResourceKey.create(Registries.RECIPE, id.identifier().withPrefix(folder + "/")), recipe, advancement, conditions);
        }

        @Override
        public Advancement.Builder advancement() {
            return delegate.advancement();
        }

        @Override
        public void includeRootAdvancement() {
            delegate.includeRootAdvancement();
        }
    }

    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new OritechRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "PB Oritech Recipes";
        }
    }
}
