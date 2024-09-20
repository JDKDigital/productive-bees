package cy.jdkdigital.productivebees.datagen.recipe.builder;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeeProduceRecipeBuilder extends AbstractRecipeBuilder {
    private final Ingredient input;
    private final List<TagOutputRecipe.ChancedOutput> output;
    private FluidStack fluid;
    private final List<ICondition> conditions;
    private boolean isConfigurable = false;

    private BeeProduceRecipeBuilder(Ingredient input, List<TagOutputRecipe.ChancedOutput> output, FluidStack fluid, List<ICondition> conditions, boolean isConfigurable) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.conditions = conditions;
        this.isConfigurable = isConfigurable;
    }

    public static BeeProduceRecipeBuilder item(Item item) {
        return new BeeProduceRecipeBuilder(Ingredient.of(item), new ArrayList<>(), null, new ArrayList<>(), false);
    }

    public static BeeProduceRecipeBuilder configurable(String beeName, List<TagOutputRecipe.ChancedOutput> output, FluidStack fluid, List<ICondition> conditions) {
        ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
        BeeCreator.setType(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, beeName), stack);
        if (output.isEmpty()) {
            output.add(new TagOutputRecipe.ChancedOutput(Ingredient.of(ModTags.Common.WAXES), 1, 1, 1f));
        }
        if (fluid == null) {
            fluid = new FluidStack(ModFluids.HONEY, 100);
        }
        return new BeeProduceRecipeBuilder(ComponentIngredient.of(stack), output, fluid, conditions, true);
    }

    public static BeeProduceRecipeBuilder configurable(String beeName) {
        return configurable(beeName, new ArrayList<>(), null, new ArrayList<>());
    }

    public BeeProduceRecipeBuilder addOutput(TagOutputRecipe.ChancedOutput output) {
        this.output.add(output);
        return this;
    }

    public BeeProduceRecipeBuilder withCondition(ICondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public BeeProduceRecipeBuilder setFluidOutput(FluidStack output) {
        this.fluid = output;
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
    public Item getResult() {
        return null;
    }

    @Override
    public void save(RecipeOutput consumer, ResourceLocation id) {
        consumer.accept(id, new CentrifugeRecipe(input, output, SizedFluidIngredient.of(fluid), 0), null, conditions.toArray(new ICondition[0]));
    }

    public BeeProduceRecipeBuilder clearOutput() {
        output.clear();
        return this;
    }

    public record RecipeConfig(String name, String folder, String[] mods, String centrifugeOutput, Map<String, String> mixingOutputs) {
    }
}
