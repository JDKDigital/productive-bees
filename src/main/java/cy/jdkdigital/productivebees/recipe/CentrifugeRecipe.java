package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE = IRecipeType.register(ProductiveBees.MODID + ":centrifuge");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final boolean requireBottle;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Ingredient, IntArrayNBT> itemOutput, Map<Ingredient, IntArrayNBT> tagOutput, boolean requireBottle) {
        super(itemOutput, tagOutput);
        this.id = id;
        this.ingredient = ingredient;
        this.requireBottle = requireBottle;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (this.ingredient.getMatchingStacks().length > 0) {
            Item invItem = inv.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT).getItem();
            for (ItemStack possibleInput: this.ingredient.getMatchingStacks()) {
                if (possibleInput.getItem().equals(invItem)) {
                    return true;
                }
            }
        }
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
        public T read(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "outputs");
            Map<Ingredient, IntArrayNBT> itemOutputs = new HashMap<>();
            Map<Ingredient, IntArrayNBT> tagOutputs = new HashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                int min = JSONUtils.getInt(jsonObject, "min", 1);
                int max = JSONUtils.getInt(jsonObject, "max", 1);
                int chance = JSONUtils.getInt(jsonObject, "chance", 100);
                IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});

                Ingredient produce;
                if (JSONUtils.isJsonArray(json, "item")) {
                    produce = Ingredient.deserialize(JSONUtils.getJsonArray(jsonObject, "item"));
                } else {
                    produce = Ingredient.deserialize(JSONUtils.getJsonObject(jsonObject, "item"));
                }

                if (jsonObject.has("item")) {
                    itemOutputs.put(produce, nbt);
                }
                else if (jsonObject.has("tag")) {
                    tagOutputs.put(produce, nbt);
                }
            });

            boolean requireBottle = JSONUtils.getBoolean(json, "require_bottle", false);

            return this.factory.create(id, ingredient, itemOutputs, tagOutputs, requireBottle);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.read(buffer);

                Map<Ingredient, IntArrayNBT> itemOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> itemOutput.put(Ingredient.read(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                Map<Ingredient, IntArrayNBT> tagOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> tagOutput.put(Ingredient.read(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                boolean requireBottle = buffer.readBoolean();

                return this.factory.create(id, ingredient, itemOutput, tagOutput, requireBottle);
            } catch (Exception e) {
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.ingredient.write(buffer);
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.write(buffer);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

                buffer.writeInt(recipe.tagOutput.size());

                recipe.tagOutput.forEach((key, value) -> {
                    key.write(buffer);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

                buffer.writeBoolean(recipe.requireBottle);

            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Map<Ingredient, IntArrayNBT> itemOutput, Map<Ingredient, IntArrayNBT> tagOutput, boolean requireBottle);
        }
    }
}