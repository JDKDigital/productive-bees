package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class BeeFishingRecipe implements Recipe<Container>
{
    private static Map<BeeFishingRecipe, List<Biome>> cachedBiomes = new HashMap<>();
    private static Map<Biome, List<BeeFishingRecipe>> cachedRecipes = new HashMap<>();
    private final ResourceLocation id;
    public final Lazy<BeeIngredient> output;
    public final List<String> biomes;
    public final double chance;

    public BeeFishingRecipe(ResourceLocation id, Lazy<BeeIngredient> output, List<String> biomes, double chance) {
        this.id = id;
        this.output = output;
        this.biomes = biomes;
        this.chance = chance;
    }

    @Override
    public boolean matches(Container inv, Level levelIn) {
        return false;
    }

    public boolean matches(Biome biome, Level level) {
        if (this.biomes.isEmpty()) {
            return true;
        }
        var key = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        for (String biomeId: this.biomes) {
            if (key != null && key.toString().equals(biomeId)) {
                return true;
            }
        }
        return false;
    }

    public static List<Biome> getBiomeList(BeeFishingRecipe recipe, Level level) {
        var biomeRegistry = level.registryAccess().registryOrThrow(Registries.BIOME);
        if (!cachedBiomes.containsKey(recipe)) {
            List<Biome> list = new ArrayList<>();

            for (String biomeId: recipe.biomes) {
                Biome biome = biomeRegistry.get(new ResourceLocation(biomeId));
                list.add(biome);
            }

            cachedBiomes.put(recipe, list);
        }
        return cachedBiomes.get(recipe);
    }

    public static List<BeeFishingRecipe> getRecipeList(Biome biome, Level level) {
        if (!cachedRecipes.containsKey(biome)) {
            List<BeeFishingRecipe> list = new ArrayList<>();

            Map<ResourceLocation, BeeFishingRecipe> allRecipes = level.getRecipeManager().byType(ModRecipeTypes.BEE_FISHING_TYPE.get());
            for (Map.Entry<ResourceLocation, BeeFishingRecipe> recipe: allRecipes.entrySet()) {
                if (recipe.getValue().matches(biome, level)) {
                    list.add(recipe.getValue());
                }
            }

            cachedRecipes.put(biome, list);
        }
        return cachedRecipes.get(biome);
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
        return ModRecipeTypes.BEE_FISHING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_FISHING_TYPE.get();
    }

    public static class Serializer<T extends BeeFishingRecipe> implements RecipeSerializer<T>
    {
        final BeeFishingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeFishingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Lazy<BeeIngredient> output = Lazy.of(BeeIngredientFactory.getIngredient(GsonHelper.getAsString(json, "bee")));

            List<String> biomes = new ArrayList<>();
            if (json.has("biomes")) {
                GsonHelper.getAsJsonArray(json, "biomes").forEach(jsonElement -> {
                    biomes.add(jsonElement.getAsString());
                });
            }

            double chance = GsonHelper.getAsDouble(json, "chance", 0.05D);

            return this.factory.create(id, output, biomes, chance);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                BeeIngredient output = BeeIngredient.fromNetwork(buffer);

                List<String> biomes = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> {
                            biomes.add(buffer.readUtf());
                        }
                );

                return this.factory.create(id, Lazy.of(() -> output), biomes, buffer.readDouble());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee fishing recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.output.get().toNetwork(buffer);

                buffer.writeInt(recipe.biomes.size());
                recipe.biomes.forEach(buffer::writeUtf);

                buffer.writeDouble(recipe.chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee fishing recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeFishingRecipe>
        {
            T create(ResourceLocation id, Lazy<BeeIngredient> output, List<String> biomes, double chance);
        }
    }
}
