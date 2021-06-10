package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeSpawningRecipe> BEE_SPAWNING = IRecipeType.register(ProductiveBees.MODID + ":bee_spawning");

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
    public boolean matches(IInventory inv, World worldIn) {
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
    public ItemStack assemble(IInventory inv) {
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
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_SPAWNING.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_SPAWNING;
    }

    public static class Serializer<T extends BeeSpawningRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "ingredient"));
            }
            else {
                ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getAsJsonArray(json, "results");
            List<Lazy<BeeIngredient>> output = new ArrayList<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                String beeName = JSONUtils.getAsString(jsonObject, "bee");
                Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeName));
                output.add(beeIngredient);
            });

            List<String> biomes = new ArrayList<>();
            if (json.has("biomes")) {
                JSONUtils.getAsJsonArray(json, "biomes").forEach(jsonElement -> {
                    biomes.add(jsonElement.getAsString());
                });
            }

            String temperature = json.has("temperature") ? json.get("temperature").getAsString() : "any";

            return this.factory.create(id, ingredient, output, biomes, temperature);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
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

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
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
