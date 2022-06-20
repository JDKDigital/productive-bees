package cy.jdkdigital.productivebees.datagen.recipe.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CentrifugeRecipeBuilder extends AbstractRecipeBuilder {
    private final Ingredient input;
    private final List<IngredientOutput> output;
    private FluidOutput fluid;
    private final List<ICondition> conditions;
    private boolean isConfigurable = false;

    private CentrifugeRecipeBuilder(Ingredient input, List<IngredientOutput> output, FluidOutput fluid, List<ICondition> conditions, boolean isConfigurable) {
        this.input = input;
        this.output = output;
        this.fluid = fluid;
        this.conditions = conditions;
        this.isConfigurable = isConfigurable;
    }

    public static CentrifugeRecipeBuilder item(Item item) {
        return new CentrifugeRecipeBuilder(Ingredient.of(item), new ArrayList<>(), null, new ArrayList<>(), false);
    }

    public static CentrifugeRecipeBuilder configurable(String beeName, List<IngredientOutput> output, FluidOutput fluid, List<ICondition> conditions) {
        ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
        BeeCreator.setTag(ProductiveBees.MODID + ":" + beeName, stack);
        if (output.isEmpty()) {
            output.add(new AbstractRecipeBuilder.IngredientOutput(Ingredient.of(ModTags.Forge.WAX)));
        }
        if (fluid != null) {
            fluid = new AbstractRecipeBuilder.FluidOutput("productivebees:honey");
        }
        return new CentrifugeRecipeBuilder(NBTIngredient.of(stack), output, fluid, conditions, true);
    }

    public static CentrifugeRecipeBuilder configurable(String beeName) {
        return configurable(beeName, new ArrayList<>(), null, new ArrayList<>());
    }

    public CentrifugeRecipeBuilder addOutput(IngredientOutput output) {
        this.output.add(output);
        return this;
    }

    public CentrifugeRecipeBuilder withCondition(ICondition condition) {
        this.conditions.add(condition);
        return this;
    }

    public CentrifugeRecipeBuilder setFluidOutput(FluidOutput output) {
        this.fluid = output;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String criteria, CriterionTriggerInstance trigger) {
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
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new Result(id, input, output, fluid, conditions));
    }

    static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient input;
        private List<IngredientOutput> output;
        private FluidOutput fluid;
        private List<ICondition> conditions;

        public Result(ResourceLocation id, Ingredient input, List<IngredientOutput> output, FluidOutput fluid, List<ICondition> conditions) {
            this.id = id;
            this.input = input;
            this.output = output;
            this.fluid = fluid;
            this.conditions = conditions;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("ingredient", input.toJson());

            JsonArray jsonarray = new JsonArray();
            for (IngredientOutput ingredient : this.output) {
                jsonarray.add(ingredient.toJson());
            }
            if (fluid != null) {
                jsonarray.add(fluid.toJson());
            }
            json.add("outputs", jsonarray);

            if (conditions.size() > 0) {
                JsonArray cJson = new JsonArray();

                conditions.forEach(condition -> {
                    cJson.add(CraftingHelper.serialize(condition));
                });

                json.add("conditions", cJson);
            }
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipeTypes.CENTRIFUGE.get();
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
