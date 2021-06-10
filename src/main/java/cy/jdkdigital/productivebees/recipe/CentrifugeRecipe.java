package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE = IRecipeType.register(ProductiveBees.MODID + ":centrifuge");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final Map<String, Integer> fluidOutput;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Ingredient, IntArrayNBT> itemOutput, Map<String, Integer> fluidOutput) {
        super(itemOutput);
        this.id = id;
        this.ingredient = ingredient;
        this.fluidOutput = fluidOutput;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
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
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CENTRIFUGE.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return CENTRIFUGE;
    }

    public static class Serializer<T extends CentrifugeRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final CentrifugeRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(CentrifugeRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isArrayNode(json, "ingredient")) {
                ingredient = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "ingredient"));
            }

            String type = JSONUtils.getAsString(json, "comb_type", "");
            if (!type.isEmpty()) {
                ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                BeeCreator.setTag(type, stack);
                ingredient = Ingredient.of(stack);
            }

            JsonArray jsonArray = JSONUtils.getAsJsonArray(json, "outputs");
            Map<Ingredient, IntArrayNBT> itemOutputs = new LinkedHashMap<>();
            Map<String, Integer> fluidOutputs = new LinkedHashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                if (jsonObject.has("item")) {
                    int min = JSONUtils.getAsInt(jsonObject, "min", 1);
                    int max = JSONUtils.getAsInt(jsonObject, "max", 1);
                    int chance = JSONUtils.getAsInt(jsonObject, "chance", 100);
                    IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});

                    Ingredient produce;
                    if (JSONUtils.isArrayNode(jsonObject, "item")) {
                        produce = Ingredient.fromJson(JSONUtils.getAsJsonArray(jsonObject, "item"));
                    } else {
                        produce = Ingredient.fromJson(JSONUtils.getAsJsonObject(jsonObject, "item"));
                    }

                    itemOutputs.put(produce, nbt);
                } else if (jsonObject.has("fluid")) {
                    int amount = JSONUtils.getAsInt(jsonObject, "amount", 250);

                    JsonObject fluid = JSONUtils.getAsJsonObject(jsonObject, "fluid");
                    String fluidResourceLocation = "";
                    if (fluid.has("tag")) {
                        fluidResourceLocation = JSONUtils.getAsString(fluid, "tag");
                    } else if (fluid.has("fluid")) {
                        fluidResourceLocation = JSONUtils.getAsString(fluid, "fluid");
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
        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.fromNetwork(buffer);

                Map<Ingredient, IntArrayNBT> itemOutput = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> itemOutput.put(Ingredient.fromNetwork(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
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
        public void toNetwork(@Nonnull PacketBuffer buffer, @Nonnull T recipe) {
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
            T create(ResourceLocation id, Ingredient input, Map<Ingredient, IntArrayNBT> itemOutput, Map<String, Integer> fluidOutput);
        }
    }
}