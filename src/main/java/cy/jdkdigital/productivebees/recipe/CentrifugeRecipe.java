package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.tileentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.init.ModItemGroups;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
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
        if (this.ingredient.getMatchingStacks().length > 0) {
            ItemStack invStack = inv.getStackInSlot(InventoryHandlerHelper.INPUT_SLOT);

            for (ItemStack stack: this.ingredient.getMatchingStacks()) {
                if (stack.getItem().equals(invStack.getItem())) {
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
    public boolean isDynamic() {
        return true;
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

    @Nullable
    public Pair<Fluid, Integer> getFluidOutputs() {
        for(Map.Entry<String, Integer> entry: fluidOutput.entrySet()) {
            ProductiveBees.LOGGER.info("loading fluid " + entry.getKey());
            // Try loading from fluid registry
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(entry.getKey()));

            ProductiveBees.LOGGER.info("loaded fluid " + fluid);

            // Try loading fluid from fluid tag
            if (fluid == Fluids.EMPTY) {
                try {
                    ITag<Fluid> fluidTag = FluidTags.getCollection().get(new ResourceLocation(entry.getKey()));
                    if (fluidTag.getAllElements().size() > 0) {
                        fluid = fluidTag.getAllElements().iterator().next();
                    }
                } catch (Exception e) {
                    // Who cares
                }
            }

            if (fluid != Fluids.EMPTY) {
//                if (fluid instanceof FlowingFluid) {
//                    fluid = ((FlowingFluid) fluid).getStillFluid();
//                }
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
        public T read(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            String type = JSONUtils.getString(json, "comb_type", "");
            if (!type.isEmpty()) {
                ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
                ModItemGroups.ModItemGroup.setTag(type, stack);
                ingredient = Ingredient.fromStacks(stack);
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "outputs");
            Map<Ingredient, IntArrayNBT> itemOutputs = new HashMap<>();
            Map<String, Integer> fluidOutputs = new HashMap<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                if (jsonObject.has("item")) {
                    int min = JSONUtils.getInt(jsonObject, "min", 1);
                    int max = JSONUtils.getInt(jsonObject, "max", 1);
                    int chance = JSONUtils.getInt(jsonObject, "chance", 100);
                    IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});

                    Ingredient produce;
                    if (JSONUtils.isJsonArray(jsonObject, "item")) {
                        produce = Ingredient.deserialize(JSONUtils.getJsonArray(jsonObject, "item"));
                    }
                    else {
                        produce = Ingredient.deserialize(JSONUtils.getJsonObject(jsonObject, "item"));
                    }

                    itemOutputs.put(produce, nbt);
                } else if (jsonObject.has("fluid")) {
                    int amount = JSONUtils.getInt(jsonObject, "amount", 250);

                    JsonObject fluid = JSONUtils.getJsonObject(jsonObject, "fluid");
                    String fluidResourceLocation = "";
                    if (fluid.has("tag")) {
                        fluidResourceLocation = JSONUtils.getString(fluid, "tag");
                    } else if (fluid.has("fluid")) {
                        fluidResourceLocation = JSONUtils.getString(fluid, "fluid");
                    }

                    fluidOutputs.put(fluidResourceLocation, amount);
                }
            });

            // Default fluid output
            if (fluidOutputs.isEmpty()) {
                fluidOutputs.put("forge:honey", 250);
            }

            return this.factory.create(id, ingredient, itemOutputs, fluidOutputs);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.read(buffer);

                Map<Ingredient, IntArrayNBT> itemOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> itemOutput.put(Ingredient.read(buffer), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );

                Map<String, Integer> fluidOutput = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> fluidOutput.put(buffer.readString(), buffer.readInt())
                );

                return this.factory.create(id, ingredient, itemOutput, fluidOutput);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading centrifuge recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, @Nonnull T recipe) {
            try {
                recipe.ingredient.write(buffer);
                buffer.writeInt(recipe.itemOutput.size());

                recipe.itemOutput.forEach((key, value) -> {
                    key.write(buffer);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

                buffer.writeInt(recipe.fluidOutput.size());
                recipe.fluidOutput.forEach((key, value) -> {
                    buffer.writeString(key);
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