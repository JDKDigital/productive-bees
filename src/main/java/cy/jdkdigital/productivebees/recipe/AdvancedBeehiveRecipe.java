package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe extends TagOutputRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE = IRecipeType.register(ProductiveBees.MODID + ":advanced_beehive");

    public final ResourceLocation id;
    public final Lazy<BeeIngredient> ingredient;

    public AdvancedBeehiveRecipe(ResourceLocation id, Lazy<BeeIngredient> ingredient, Map<Ingredient, IntArrayNBT> itemOutput) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "AdvancedBeehiveRecipe{" +
                "id=" + id +
                ", bee=" + ingredient.get().getBeeEntity() +
                '}';
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && ingredient.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier();
            return beeName.equals(ingredient.get().getBeeType().toString());
        }
        if (ingredient.get() == null) {
            ProductiveBees.LOGGER.info(id + " is null");
        }

        return false;
    }

    @Override
    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        Map<ItemStack, IntArrayNBT> output = super.getRecipeOutputs();

        for (Map.Entry<ItemStack, IntArrayNBT> entry : output.entrySet()) {
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
        return ModRecipeTypes.ADVANCED_BEEHIVE.get();
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

            Lazy<BeeIngredient> beeIngredient = Lazy.of(BeeIngredientFactory.getIngredient(beeName));

            Map<Ingredient, IntArrayNBT> itemOutputs = new LinkedHashMap<>();

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "results");
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                String ingredientKey = jsonObject.has("item") ? "item" : "comb_produce";

                Ingredient produce;
                if (JSONUtils.isJsonArray(jsonObject, ingredientKey)) {
                    produce = Ingredient.deserialize(JSONUtils.getJsonArray(jsonObject, ingredientKey));
                }
                else {
                    produce = Ingredient.deserialize(JSONUtils.getJsonObject(jsonObject, ingredientKey));
                }

                int min = JSONUtils.getInt(jsonObject, "min", 1);
                int max = JSONUtils.getInt(jsonObject, "max", 1);
                int outputChance = JSONUtils.getInt(jsonObject, "chance", 100);
                IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, outputChance});

                itemOutputs.put(produce, nbt);
            });

            return this.factory.create(id, beeIngredient, itemOutputs);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                BeeIngredient ingredient = BeeIngredient.read(buffer);
                Map<Ingredient, IntArrayNBT> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.read(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                return this.factory.create(id, Lazy.of(() -> ingredient), itemOutput);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading beehive produce recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                if (recipe.ingredient.get() != null) {
                    recipe.ingredient.get().write(buffer);
                }
                else {
                    ProductiveBees.LOGGER.error("Bee produce recipe ingredient missing " + recipe.getId() + " - " + recipe.ingredient);
                }
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.write(buffer);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing beehive produce recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends AdvancedBeehiveRecipe>
        {
            T create(ResourceLocation id, Lazy<BeeIngredient> input, Map<Ingredient, IntArrayNBT> itemOutput);
        }
    }
}
