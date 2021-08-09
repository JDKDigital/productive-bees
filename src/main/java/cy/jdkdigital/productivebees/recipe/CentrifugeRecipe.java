package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeCreator;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements Recipe<Container>
{
    public static final RecipeType<CentrifugeRecipe> CENTRIFUGE = RecipeType.register(ProductiveBees.MODID + ":centrifuge");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final Map<String, Integer> fluidOutput;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Ingredient, IntArrayTag> itemOutput, Map<String, Integer> fluidOutput) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
        this.fluidOutput = fluidOutput;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (this.ingredient.getItems().length > 0) {
            ItemStack invStack = inv.getItem(InventoryHandlerHelper.INPUT_SLOT);

            for (ItemStack stack : this.ingredient.getItems()) {
                if (stack.getItem().equals(invStack.getItem())) {
                    // Check configurable honeycombs
                    if (stack.hasTag() && invStack.hasTag()) {
                        return stack.getTag().equals(invStack.getTag());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
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

    @Nullable
    public Pair<Fluid, Integer> getFluidOutputs() {
        for (Map.Entry<String, Integer> entry : fluidOutput.entrySet()) {
            Fluid fluid = getPreferredFluidByMod(entry.getKey());

            if (fluid != Fluids.EMPTY) {
                return Pair.of(fluid, entry.getValue());
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CENTRIFUGE.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return CENTRIFUGE;
    }

    public static class Serializer<T extends CentrifugeRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T>
    {
        final CentrifugeRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(CentrifugeRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (GsonHelper.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            }

            String type = GsonHelper.getAsString(json, "comb_type", "");
            if (!type.isEmpty()) {
                ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                BeeCreator.setTag(type, stack);
                ingredient = Ingredient.of(stack);
            }

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "outputs");
            Map<Ingredient, IntArrayTag> itemOutputs = new LinkedHashMap<>();
            Map<String, Integer> fluidOutputs = new LinkedHashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                if (jsonObject.has("item")) {
                    int min = GsonHelper.getAsInt(jsonObject, "min", 1);
                    int max = GsonHelper.getAsInt(jsonObject, "max", 1);
                    int chance = GsonHelper.getAsInt(jsonObject, "chance", 100);
                    IntArrayTag nbt = new IntArrayTag(new int[]{min, max, chance});

                    Ingredient produce;
                    if (GsonHelper.isArrayNode(jsonObject, "item")) {
                        produce = Ingredient.fromJson(GsonHelper.getAsJsonArray(jsonObject, "item"));
                    } else {
                        produce = Ingredient.fromJson(GsonHelper.getAsJsonObject(jsonObject, "item"));
                    }

                    itemOutputs.put(produce, nbt);
                } else if (jsonObject.has("fluid")) {
                    int amount = GsonHelper.getAsInt(jsonObject, "amount", 250);

                    JsonObject fluid = GsonHelper.getAsJsonObject(jsonObject, "fluid");
                    String fluidResourceLocation = "";
                    if (fluid.has("tag")) {
                        fluidResourceLocation = GsonHelper.getAsString(fluid, "tag");
                    } else if (fluid.has("fluid")) {
                        fluidResourceLocation = GsonHelper.getAsString(fluid, "fluid");
                    }

                    fluidOutputs.put(fluidResourceLocation, amount);
                }
            });

            // Default fluid output
            if (fluidOutputs.isEmpty()) {
                fluidOutputs.put("productivebees:honey", 100);
            }

            return this.factory.create(id, ingredient, itemOutputs, fluidOutputs);
        }

        @Override
        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);

                Map<Ingredient, IntArrayTag> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.fromNetwork(buffer), new IntArrayTag(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                Map<String, Integer> fluidOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> fluidOutput.put(buffer.readUtf(), buffer.readInt())
                );

                return this.factory.create(id, ingredient, itemOutput, fluidOutput);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading centrifuge recipe from packet. " + id, e);
                throw e;
            }
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer, @Nonnull T recipe) {
            try {
                recipe.ingredient.toNetwork(buffer);
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.toNetwork(buffer);
                    buffer.writeInt(value.get(0).getAsInt());
                    buffer.writeInt(value.get(1).getAsInt());
                    buffer.writeInt(value.get(2).getAsInt());
                });

                buffer.writeInt(recipe.fluidOutput.size());
                recipe.fluidOutput.forEach((key, value) -> {
                    buffer.writeUtf(key);
                    buffer.writeInt(value);
                });

            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing centrifuge recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Map<Ingredient, IntArrayTag> itemOutput, Map<String, Integer> fluidOutput);
        }
    }
}