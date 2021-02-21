package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class IncubationRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<IncubationRecipe> BEE_CONVERSION = IRecipeType.register(ProductiveBees.MODID + ":incubation");

    public final ResourceLocation id;
    public final Ingredient input;
    public final Ingredient catalyst;
    public final Ingredient result;

    public IncubationRecipe(ResourceLocation id, Ingredient input, Ingredient catalyst, Ingredient result) {
        this.id = id;
        this.input = input;
        this.catalyst = catalyst;
        this.result = result;
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
        return ModRecipeTypes.BEE_CONVERSION.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_CONVERSION;
    }

    public static class Serializer<T extends IncubationRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final IncubationRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(IncubationRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T read(ResourceLocation id, JsonObject json) {
            Ingredient input;
            if (JSONUtils.isJsonArray(json, "input")) {
                input = Ingredient.deserialize(JSONUtils.getJsonArray(json, "input"));
            } else {
                input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
            }

            Ingredient catalyst;
            if (JSONUtils.isJsonArray(json, "catalyst")) {
                catalyst = Ingredient.deserialize(JSONUtils.getJsonArray(json, "catalyst"));
            } else {
                catalyst = Ingredient.deserialize(JSONUtils.getJsonObject(json, "catalyst"));
            }

            Ingredient output;
            if (JSONUtils.isJsonArray(json, "output")) {
                output = Ingredient.deserialize(JSONUtils.getJsonArray(json, "output"));
            } else {
                output = Ingredient.deserialize(JSONUtils.getJsonObject(json, "output"));
            }

            return this.factory.create(id, input, catalyst, output);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id, Ingredient.read(buffer), Ingredient.read(buffer), Ingredient.read(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.input.write(buffer);
                recipe.catalyst.write(buffer);
                recipe.result.write(buffer);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee incubation recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends IncubationRecipe>
        {
            T create(ResourceLocation id, Ingredient item, Ingredient catalyst, Ingredient output);
        }
    }
}
