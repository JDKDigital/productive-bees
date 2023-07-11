package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final Ingredient spawnItem;
    public final List<Lazy<BeeIngredient>> output;
    public final String biomes;
    public final TagKey<Biome> biomeKey;

    public BeeSpawningRecipe(ResourceLocation id, Ingredient ingredient, Ingredient spawnItem, List<Lazy<BeeIngredient>> output, String biomes) {
        this.id = id;
        this.ingredient = ingredient;
        this.spawnItem = spawnItem;
        this.output = output;
        this.biomes = biomes.replace("#", "");
        this.biomeKey = TagKey.create(Registries.BIOME, new ResourceLocation(this.biomes));
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    public boolean matches(ItemStack nest, ItemStack heldItem, Holder<Biome> biome, Level level) {
         return ingredient.test(nest) && (heldItem.equals(ItemStack.EMPTY) || spawnItem.test(heldItem)) && (this.biomes.equals("any") || biome.is(biomeKey));
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
        return ModRecipeTypes.BEE_SPAWNING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_SPAWNING_TYPE.get();
    }

    public static class Serializer<T extends BeeSpawningRecipe> implements RecipeSerializer<T>
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
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }
            Ingredient spawnItem;
            if (json.has("spawn_item")) {
                if (GsonHelper.isArrayNode(json, "spawn_item")) {
                    spawnItem = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "spawn_item"));
                } else {
                    spawnItem = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "spawn_item"));
                }
            } else {
                spawnItem = Ingredient.of(ModItems.HONEY_TREAT.get());
            }

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "results");
            List<Lazy<BeeIngredient>> output = new ArrayList<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                String beeName = GsonHelper.getAsString(jsonObject, "bee");
                Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeName));
                output.add(beeIngredient);
            });

            String biomes = "any";
            if (json.has("biomes")) {
                biomes = GsonHelper.getAsString(json, "biomes");
            }

            return this.factory.create(id, ingredient, spawnItem, output, biomes);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);
                Ingredient spawnItem = Ingredient.fromNetwork(buffer);

                List<Lazy<BeeIngredient>> output = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> {
                            BeeIngredient ing = BeeIngredient.fromNetwork(buffer);
                            output.add(Lazy.of(() -> ing));
                        }
                );

                return this.factory.create(id, ingredient, spawnItem, output, buffer.readUtf());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee spawning recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.ingredient.toNetwork(buffer);
                recipe.spawnItem.toNetwork(buffer);

                buffer.writeInt(recipe.output.size());
                for (Lazy<BeeIngredient> beeOutput : recipe.output) {
                    if (beeOutput.get() != null) {
                        beeOutput.get().toNetwork(buffer);
                    }
                    else {
                        ProductiveBees.LOGGER.error("Bee spawning recipe output missing " + recipe.getId() + " - " + beeOutput);
                    }
                }

                buffer.writeUtf(recipe.biomes);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee spawning recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeSpawningRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Ingredient spawnItem, List<Lazy<BeeIngredient>> output, String biomes);
        }
    }
}
