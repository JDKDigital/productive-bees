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
    public static final IRecipeType<IncubationRecipe> INCUBATION = IRecipeType.register(ProductiveBees.MODID + ":incubation");

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

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.INCUBATION.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return INCUBATION;
    }

    public static class Serializer<T extends IncubationRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final IncubationRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(IncubationRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input;
            if (JSONUtils.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "input"));
            }
            else {
                input = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "input"));
            }

            Ingredient catalyst;
            if (JSONUtils.isArrayNode(json, "catalyst")) {
                catalyst = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "catalyst"));
            }
            else {
                catalyst = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "catalyst"));
            }

            Ingredient output;
            if (JSONUtils.isArrayNode(json, "output")) {
                output = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "output"));
            }
            else {
                output = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "output"));
            }

            return this.factory.create(id, input, catalyst, output);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.input.toNetwork(buffer);
                recipe.catalyst.toNetwork(buffer);
                recipe.result.toNetwork(buffer);
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
