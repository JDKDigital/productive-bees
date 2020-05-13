package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE = IRecipeType.register(ProductiveBees.MODID + ":advanced_beehive");

    public final ResourceLocation id;
    public final BeeIngredient ingredient;
    public final Map<ItemStack, Pair<Integer, Integer>> outputs;
    public final double chance;

    public AdvancedBeehiveRecipe(ResourceLocation id, BeeIngredient ingredient, Map<ItemStack, Pair<Integer, Integer>> outputs, double chance) {
        this.id = id;
        this.ingredient = ingredient;
        this.outputs = outputs;
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "AdvancedBeehiveRecipe{" +
                "id=" + id +
                ", bee=" + ingredient.getBeeType() +
                ", outputs=" + outputs +
                ", chance=" + chance +
                '}';
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
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
        return ForgeRegistries.RECIPE_SERIALIZERS.getValue(ProduciveBeesJeiPlugin.CATEGORY_ADVANCED_BEEHIVE_UID);
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return ADVANCED_BEEHIVE;
    }

    public static class Serializer<T extends AdvancedBeehiveRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            String beeName = JSONUtils.getString(json, "ingredient");

            BeeIngredient beeIngredient = BeeIngredientFactory.getOrCreateList().get(beeName);

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "results");
            Map<ItemStack, Pair<Integer, Integer>> outputs = new HashMap<>();
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                Ingredient produce;
                String ingredientKey = "item_produce";
                if (ProductiveBeesConfig.GENERAL.enableCombProduce.get()) {
                    ingredientKey = "comb_produce";
                }
                if (JSONUtils.isJsonArray(json, ingredientKey)) {
                    produce = Ingredient.deserialize(JSONUtils.getJsonArray(jsonObject, ingredientKey));
                }
                else {
                    produce = Ingredient.deserialize(JSONUtils.getJsonObject(jsonObject, ingredientKey));
                }

                ItemStack[] stacks = produce.getMatchingStacks();

                if (stacks.length > 0) {
                    int min = JSONUtils.getInt(jsonObject, "min", 1);
                    int max = JSONUtils.getInt(jsonObject, "max", 1);

                    outputs.put(stacks[0], Pair.of(min, max));
                }
            });

            double chance = ProductiveBeeEntity.getProductionRate(beeName, 0.25D);

            return this.factory.create(id, beeIngredient, outputs, chance);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                BeeIngredient ingredient = BeeIngredient.read(buffer);
                Map<ItemStack, Pair<Integer, Integer>> outputs = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> outputs.put(buffer.readItemStack(), Pair.of(buffer.readInt(), buffer.readInt()))
                );
                double chance = buffer.readDouble();
                return this.factory.create(id, ingredient, outputs, chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading recipe from packet.", e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.ingredient.write(buffer);
                buffer.writeInt(recipe.outputs.size());

                recipe.outputs.forEach((key, value) -> {
                    buffer.writeItemStack(key);
                    buffer.writeInt(value.getLeft());
                    buffer.writeInt(value.getRight());
                });

                buffer.writeDouble(recipe.chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing recipe to packet.", e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends AdvancedBeehiveRecipe>
        {
            T create(ResourceLocation id, BeeIngredient input, Map<ItemStack, Pair<Integer, Integer>> output, double chance);
        }
    }
}
