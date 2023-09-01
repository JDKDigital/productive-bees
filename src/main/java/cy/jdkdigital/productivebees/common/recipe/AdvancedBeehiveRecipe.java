package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe extends TagOutputRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Lazy<BeeIngredient> ingredient;

    public AdvancedBeehiveRecipe(ResourceLocation id, Lazy<BeeIngredient> ingredient, Map<Ingredient, IntArrayTag> itemOutput) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "AdvancedBeehiveRecipe{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && ingredient.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier();
            return beeName.equals(ingredient.get().getBeeType().toString());
        }
        if (ingredient.get() == null) {
            ProductiveBees.LOGGER.info("bee ingredient for " + id + " is null");
        }
        return false;
    }

    @Override
    public Map<ItemStack, IntArrayTag> getRecipeOutputs() {
        Map<ItemStack, IntArrayTag> output = super.getRecipeOutputs();

        for (Map.Entry<ItemStack, IntArrayTag> entry : output.entrySet()) {
            if (ingredient.get().isConfigurable()) {
                if (entry.getKey().getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
                    BeeCreator.setTag(ingredient.get().getBeeType().toString(), entry.getKey());
                }
            }
        }

        return output;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.ADVANCED_BEEHIVE.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get();
    }

    public static class Serializer<T extends AdvancedBeehiveRecipe> implements RecipeSerializer<T>
    {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String beeName = GsonHelper.getAsString(json, "ingredient");

            Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeName));

            Map<Ingredient, IntArrayTag> itemOutputs = new LinkedHashMap<>();

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "results");
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                String ingredientKey = jsonObject.has("item") ? "item" : "comb_produce";

                Ingredient produce;
                if (GsonHelper.isArrayNode(jsonObject, ingredientKey)) {
                    produce = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, ingredientKey));
                } else {
                    produce = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, ingredientKey));
                }

                int min = GsonHelper.getAsInt(jsonObject, "min", 1);
                int max = GsonHelper.getAsInt(jsonObject, "max", 1);
                int outputChance = GsonHelper.getAsInt(jsonObject, "chance", 100);
                IntArrayTag nbt = new IntArrayTag(new int[]{min, max, outputChance});

                itemOutputs.put(produce, nbt);
            });

            return this.factory.create(id, beeIngredient, itemOutputs);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                BeeIngredient ingredient = BeeIngredient.fromNetwork(buffer);
                Map<Ingredient, IntArrayTag> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.fromNetwork(buffer), new IntArrayTag(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                return this.factory.create(id, Lazy.of(() -> ingredient), itemOutput);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading beehive produce recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                if (recipe.ingredient.get() != null) {
                    recipe.ingredient.get().toNetwork(buffer);
                } else {
                    throw new RuntimeException("Bee produce recipe ingredient missing " + recipe.getId() + " - " + recipe.ingredient);
                }
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.toNetwork(buffer);
                    buffer.writeInt(value.get(0).getAsInt());
                    buffer.writeInt(value.get(1).getAsInt());
                    buffer.writeInt(value.get(2).getAsInt());
                });
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing beehive produce recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends AdvancedBeehiveRecipe>
        {
            T create(ResourceLocation id, Lazy<BeeIngredient> input, Map<Ingredient, IntArrayTag> itemOutput);
        }
    }
}
