package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.tileentity.ItemHandlerHelper;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class CentrifugeRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE = IRecipeType.register(ProductiveBees.MODID + ":centrifuge");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final Map<ItemStack, IntArrayNBT> output;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<ItemStack, IntArrayNBT> output) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (this.ingredient.getMatchingStacks().length > 0) {
            return inv.getStackInSlot(ItemHandlerHelper.INPUT_SLOT).getItem().equals(this.ingredient.getMatchingStacks()[0].getItem());
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
            }
            else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "output");
            Map<ItemStack, IntArrayNBT> outputs = new HashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                int min = JSONUtils.getInt(jsonObject, "min", 1);
                int max = JSONUtils.getInt(jsonObject, "max", 1);
                int chance = JSONUtils.getInt(jsonObject, "chance", 100);

                if (jsonObject.has("item")) {
                    IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});
                    String registryname = JSONUtils.getString(jsonObject, "item");
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryname));
                    outputs.put(new ItemStack(item), nbt);
                }
                else if (jsonObject.has("tag")) {
                    IntArrayNBT nbt = new IntArrayNBT(new int[]{1, 1, chance});
                    String registryname = JSONUtils.getString(jsonObject, "tag");
                    Tag<Item> tag = ItemTags.getCollection().getOrCreate(new ResourceLocation(registryname));
                    if (!tag.getAllElements().isEmpty()) {
                        outputs.put(new ItemStack(tag.getAllElements().iterator().next()), nbt);
                    }
                }
            });

            return this.factory.create(id, ingredient, outputs);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.read(buffer);

                Map<ItemStack, IntArrayNBT> output = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> output.put(buffer.readItemStack(), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                return this.factory.create(id, ingredient, output);
            } catch (Exception e) {
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.ingredient.write(buffer);
                buffer.writeInt(recipe.output.size());

                recipe.output.forEach((key, value) -> {
                    buffer.writeItemStack(key);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Map<ItemStack, IntArrayNBT> output);
        }
    }
}