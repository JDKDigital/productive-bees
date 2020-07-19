package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.tileentity.InventoryHandlerHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
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
    public final Map<Item, IntArrayNBT> itemOutput;
    public final Map<INamedTag<Item>, IntArrayNBT> tagOutput;
    public final boolean requireBottle;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Item, IntArrayNBT> itemOutput, Map<INamedTag<Item>, IntArrayNBT> tagOutput, boolean requireBottle) {
        this.id = id;
        this.ingredient = ingredient;
        this.itemOutput = itemOutput;
        this.tagOutput = tagOutput;
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

    public Map<ItemStack, IntArrayNBT> getRecipeOutputs() {
        Map<ItemStack, IntArrayNBT> output = new HashMap<>();

        if (!itemOutput.isEmpty()) {
            itemOutput.forEach((item, intNBTS) -> {
                output.put(new ItemStack(item), intNBTS);
            });
        }
        if (!tagOutput.isEmpty()) {
            tagOutput.forEach((tag, intNBTS) -> {
                try {
                    if (!tag.getAllElements().isEmpty()) {
                        output.put(new ItemStack(tag.getAllElements().stream().findFirst().orElse(Items.AIR)), intNBTS);
                    }
                } catch (IllegalStateException ise) {
                    // Tag not initialized
                    ProductiveBees.LOGGER.warn("Tag " + tag.getName() + " not initialized");
                }
            });
        }

        return output;
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
            Map<Item, IntArrayNBT> itemOutputs = new HashMap<>();
            Map<INamedTag<Item>, IntArrayNBT> tagOutputs = new HashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                int min = JSONUtils.getInt(jsonObject, "min", 1);
                int max = JSONUtils.getInt(jsonObject, "max", 1);
                int chance = JSONUtils.getInt(jsonObject, "chance", 100);
                IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});

                if (jsonObject.has("item")) {
                    String registryName = JSONUtils.getString(jsonObject, "item");
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName));
                    itemOutputs.put(item, nbt);
                }
                else if (jsonObject.has("tag")) {
                    String registryName = JSONUtils.getString(jsonObject, "tag");
                    INamedTag<Item> tag = ItemTags.makeWrapperTag(registryName);
                    tagOutputs.put(tag, nbt);
                }
            });

            boolean requireBottle = JSONUtils.getBoolean(json, "require_bottle", false);

            return this.factory.create(id, ingredient, itemOutputs, tagOutputs, requireBottle);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.read(buffer);

                Map<Item, IntArrayNBT> itemOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> itemOutput.put(buffer.readItemStack().getItem(), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                Map<INamedTag<Item>, IntArrayNBT> tagOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> tagOutput.put(ItemTags.makeWrapperTag(buffer.readResourceLocation().toString()), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
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
                    buffer.writeItemStack(new ItemStack(key));
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

                buffer.writeInt(recipe.tagOutput.size());

                recipe.tagOutput.forEach((key, value) -> {
                    buffer.writeResourceLocation(key.getName());
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
            T create(ResourceLocation id, Ingredient input, Map<Item, IntArrayNBT> itemOutput, Map<INamedTag<Item>, IntArrayNBT> tagOutput, boolean requireBottle);
        }
    }
}