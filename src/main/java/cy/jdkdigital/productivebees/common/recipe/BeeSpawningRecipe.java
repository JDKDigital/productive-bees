package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final List<Lazy<BeeIngredient>> output;
    public final List<String> biomes;
    public final String temperature;

    public BeeSpawningRecipe(ResourceLocation id, Ingredient ingredient, List<Lazy<BeeIngredient>> output, List<String> biomes, String temperature) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.biomes = biomes;
        this.temperature = temperature;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        ItemStack inventoryItem = null;
        for(int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (inventoryItem != null) {
                    return false;
                }
                inventoryItem = itemstack;
            }
        }
        return matches(inventoryItem);
    }

    public boolean matches(ItemStack nest) {
        return ingredient.test(nest);
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
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
        return ModRecipeTypes.BEE_SPAWNING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_SPAWNING_TYPE;
    }

    public static class Serializer<T extends BeeSpawningRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T>
    {
        final BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
            }
            else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "results");
            List<Lazy<BeeIngredient>> output = new ArrayList<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                String beeName = GsonHelper.getAsString(jsonObject, "bee");
                Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeName));
                output.add(beeIngredient);
            });

            List<String> biomes = new ArrayList<>();
            if (json.has("biomes")) {
                GsonHelper.getAsJsonArray(json, "biomes").forEach(jsonElement -> {
                    biomes.add(jsonElement.getAsString());
                });
            }

            String temperature = json.has("temperature") ? json.get("temperature").getAsString() : "any";

            return this.factory.create(id, ingredient, output, biomes, temperature);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);

                List<Lazy<BeeIngredient>> output = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> {
                            BeeIngredient ing = BeeIngredient.fromNetwork(buffer);
                            output.add(Lazy.of(() -> ing));
                        }
                );

                List<String> biomes = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> {
                            biomes.add(buffer.readUtf());
                        }
                );

                String temperature = buffer.readUtf();

                return this.factory.create(id, ingredient, output, biomes, temperature);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee spawning recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.ingredient.toNetwork(buffer);

                buffer.writeInt(recipe.output.size());
                for (Lazy<BeeIngredient> beeOutput : recipe.output) {
                    if (beeOutput.get() != null) {
                        beeOutput.get().toNetwork(buffer);
                    }
                    else {
                        ProductiveBees.LOGGER.error("Bee spawning recipe output missing " + recipe.getId() + " - " + beeOutput);
                    }
                }

                buffer.writeInt(recipe.biomes.size());
                recipe.biomes.forEach(buffer::writeUtf);

                buffer.writeUtf(recipe.temperature);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee spawning recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeSpawningRecipe>
        {
            T create(ResourceLocation id, Ingredient input, List<Lazy<BeeIngredient>> output, List<String> biomes, String temperature);
        }
    }
}
