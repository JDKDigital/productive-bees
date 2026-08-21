package cy.jdkdigital.productivebees.datagen.recipe.builder;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CentrifugeRecipeBuilder extends AbstractRecipeBuilder {
    private final Ingredient input;
    private final List<TagOutputRecipe.ChancedOutput> output;
    private Fluid fluid;
    private int fluidAmount;
    private final List<ICondition> conditions;
    private boolean isConfigurable = false;

    private CentrifugeRecipeBuilder(Ingredient input, List<TagOutputRecipe.ChancedOutput> output, Fluid fluid, int fluidAmount, List<ICondition> conditions, boolean isConfigurable) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.fluidAmount = fluidAmount;
        this.conditions = conditions;
        this.isConfigurable = isConfigurable;
    }

    public static CentrifugeRecipeBuilder item(Item item) {
        return new CentrifugeRecipeBuilder(Ingredient.of(item), new ArrayList<>(), null, 0, new ArrayList<>(), false);
    }

    public static CentrifugeRecipeBuilder configurable(String beeName, List<TagOutputRecipe.ChancedOutput> output, Fluid fluid, int fluidAmount, List<ICondition> conditions) {
        // Item.components()/Fluid.components() aren't bound while datagen providers run, so
        // constructing ItemStack/FluidStack via `new ItemStack(item)` / `new FluidStack(fluid, n)`
        // throws "Components not bound yet". Build DataComponentPatch directly + store the raw
        // Fluid + amount; defer materialization to inside SizedFluidIngredient.of(...) at save-time.
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(ModDataComponents.BEE_TYPE.get(), Identifier.fromNamespaceAndPath(ProductiveBees.MODID, beeName))
                .build();
        if (output.isEmpty()) {
            // c:waxes tag isn't loaded during datagen — fall back to the productivebees wax item.
            output.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(ModItems.WAX.get()), 1, 1, 1f));
        }
        if (fluid == null) {
            fluid = ModFluids.HONEY.get();
            fluidAmount = 100;
        }
        return new CentrifugeRecipeBuilder(ComponentIngredient.of(patch, ModItems.CONFIGURABLE_HONEYCOMB.get()), output, fluid, fluidAmount, conditions, true);
    }

    public static CentrifugeRecipeBuilder configurable(String beeName) {
        return configurable(beeName, new ArrayList<>(), null, 0, new ArrayList<>());
    }

    public CentrifugeRecipeBuilder addOutput(TagOutputRecipe.ChancedOutput output) {
        this.output.add(output);
        return this;
    }

    public CentrifugeRecipeBuilder withCondition(ICondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public CentrifugeRecipeBuilder setFluidOutput(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.fluidAmount = amount;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String pName, Criterion<?> pCriterion) {
        return null;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return null;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "centrifuge_default"));
    }

    @Override
    public void save(RecipeOutput consumer, ResourceKey<Recipe<?>> id) {
        consumer.accept(id, new CentrifugeRecipe(input, output, SizedFluidIngredient.of(fluid, fluidAmount), 0), null, conditions.toArray(new ICondition[0]));
    }

    public void save(RecipeOutput consumer, Identifier id) {
        save(consumer, ResourceKey.create(Registries.RECIPE, id));
    }

    public CentrifugeRecipeBuilder clearOutput() {
        output.clear();
        return this;
    }

    public record RecipeConfig(String name, String folder, String[] mods, String centrifugeOutput, Map<String, String> mixingOutputs) {
    }
}
