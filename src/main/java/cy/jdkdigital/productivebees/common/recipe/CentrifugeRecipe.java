package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements Recipe<Container>, TimedRecipeInterface
{
    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final Pair<String, Integer> fluidOutput;
    private final Integer processingTime;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Ingredient, IntArrayTag> itemOutput, Pair<String, Integer> fluidOutput, int processingTime) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
        this.fluidOutput = fluidOutput;
        this.processingTime = processingTime;
    }

    @Override
    public int getProcessingTime() {
        return processingTime > 0 ? processingTime : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get();
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (this.ingredient.getItems().length > 0) {
            ItemStack invStack = inv.getItem(InventoryHandlerHelper.INPUT_SLOT);

            if (!this.ingredient.test(invStack)) {
                return false;
            }

            for (ItemStack stack : this.ingredient.getItems()) {
                if (ItemHandlerHelper.canItemStacksStack(invStack, stack)) {
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

    @Nullable
    public Pair<Fluid, Integer> getFluidOutputs() {
        if (fluidOutput != null) {
            Fluid fluid = getPreferredFluidByMod(fluidOutput.getFirst());

            if (fluid != Fluids.EMPTY) {
                return Pair.of(fluid, fluidOutput.getSecond());
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
        return ModRecipeTypes.CENTRIFUGE_TYPE.get();
    }

    public static class Serializer<T extends CentrifugeRecipe> implements RecipeSerializer<T>
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

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, "outputs");
            Map<Ingredient, IntArrayTag> itemOutputs = new LinkedHashMap<>();
            AtomicReference<Pair<String, Integer>> fluidOutputs = new AtomicReference<>();
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

                    JsonObject fluidJson = GsonHelper.getAsJsonObject(jsonObject, "fluid");
                    String fluidResourceLocation = "";
                    if (fluidJson.has("tag")) {
                        fluidResourceLocation = GsonHelper.getAsString(fluidJson, "tag");
                    } else if (fluidJson.has("fluid")) {
                        fluidResourceLocation = GsonHelper.getAsString(fluidJson, "fluid");
                    }

                    fluidOutputs.set(Pair.of(fluidResourceLocation, amount));
                }
            });

            int processingTime = json.has("processingTime") ? json.get("processingTime").getAsInt() : 0;

            return this.factory.create(id, ingredient, itemOutputs, fluidOutputs.get(), processingTime);
        }

        @Override
        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);

                Map<Ingredient, IntArrayTag> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.fromNetwork(buffer), new IntArrayTag(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                Pair<String, Integer> fluidOutput = null;
                if (buffer.readBoolean()) {
                    fluidOutput = Pair.of(buffer.readUtf(), buffer.readInt());
                }

                return this.factory.create(id, ingredient, itemOutput, fluidOutput, buffer.readInt());
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

                buffer.writeBoolean(recipe.fluidOutput != null);
                if (recipe.fluidOutput != null) {
                    buffer.writeUtf(recipe.fluidOutput.getFirst());
                    buffer.writeInt(recipe.fluidOutput.getSecond());
                }

                buffer.writeInt(recipe.getProcessingTime());

            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing centrifuge recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Map<Ingredient, IntArrayTag> itemOutput, Pair<String, Integer> fluidOutput, Integer processingTime);
        }
    }
}