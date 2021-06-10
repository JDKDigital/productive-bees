package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class BottlerRecipe extends TagOutputRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BottlerRecipe> BOTTLER = IRecipeType.register(ProductiveBees.MODID + ":bottler");

    public final ResourceLocation id;
    public final Pair<String, Integer> fluidInput;
    public final Ingredient itemInput;
    public final Ingredient result;

    public BottlerRecipe(ResourceLocation id, Pair<String, Integer> fluidInput, Ingredient itemInput, Ingredient result) {
        super(result);
        this.id = id;
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.result = result;
    }

    public boolean matches(FluidStack fluid, ItemStack inputStack) {
        if (!itemInput.test(inputStack)) {
            return false;
        }
        if (!getPreferredFluidByMod(fluidInput.getFirst()).equals(fluid.getFluid())) {
            return false;
        }
        return fluid.getAmount() >= fluidInput.getSecond();
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
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
        return getRecipeOutputs().entrySet().iterator().next().getKey().copy();
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOTTLER.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BOTTLER;
    }

    public static class Serializer<T extends BottlerRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BottlerRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BottlerRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Pair<String, Integer> fluidInput = null;
            if (json.has("fluid")) {
                int amount = JSONUtils.getAsInt(json, "amount", 250);

                JsonObject fluid = JSONUtils.getAsJsonObject(json, "fluid");
                String fluidResourceLocation = "";
                if (fluid.has("tag")) {
                    fluidResourceLocation = JSONUtils.getAsString(fluid, "tag");
                } else if (fluid.has("fluid")) {
                    fluidResourceLocation = JSONUtils.getAsString(fluid, "fluid");
                }

                fluidInput = Pair.of(fluidResourceLocation, amount);
            }

            Ingredient input;
            if (JSONUtils.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "input"));
            } else {
                input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input"));
            }

            Ingredient output;
            if (JSONUtils.isArrayNode(json, "output")) {
                output = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "output"));
            } else {
                output = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "output"));
            }

            return this.factory.create(id, fluidInput, input, output);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Pair<String, Integer> fluidInput = Pair.of(buffer.readUtf(), buffer.readInt());
                return this.factory.create(id, fluidInput, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee bottler recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                buffer.writeUtf(recipe.fluidInput.getFirst());
                buffer.writeInt(recipe.fluidInput.getSecond());
                recipe.itemInput.toNetwork(buffer);
                recipe.result.toNetwork(buffer);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee bottler recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BottlerRecipe>
        {
            T create(ResourceLocation id, Pair<String, Integer> fluidInput, Ingredient input, Ingredient output);
        }
    }
}
