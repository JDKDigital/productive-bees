package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Optional;

public class BottlerRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Pair<String, Integer> fluidInput;
    public final Ingredient itemInput;
    public final ItemStack result;

    public BottlerRecipe(ResourceLocation id, Pair<String, Integer> fluidInput, Ingredient itemInput, ItemStack result) {
        this.id = id;
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.result = result;
    }

    public boolean matches(FluidStack fluid, ItemStack inputStack) {
        if (!itemInput.test(inputStack)) {
            return false;
        }

        if (fluid.getAmount() < fluidInput.getSecond()) {
            return false;
        }

        Fluid recipeFluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidInput.getFirst()));
        if (recipeFluid != null && recipeFluid.equals(fluid.getFluid())) {
            return true;
        }

        TagKey<Fluid> fluidTag = ModTags.getFluidTag(new ResourceLocation(fluidInput.getFirst()));
        if (fluid.getFluid().is(fluidTag)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
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
        return this.result;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOTTLER.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BOTTLER_TYPE;
    }

    public static class Serializer<T extends BottlerRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T>
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
                int amount = GsonHelper.getAsInt(json, "amount", 250);

                JsonObject fluid = GsonHelper.getAsJsonObject(json, "fluid");
                String fluidResourceLocation = "";
                if (fluid.has("tag")) {
                    fluidResourceLocation = GsonHelper.getAsString(fluid, "tag");
                } else if (fluid.has("fluid")) {
                    fluidResourceLocation = GsonHelper.getAsString(fluid, "fluid");
                }

                fluidInput = Pair.of(fluidResourceLocation, amount);
            }

            Ingredient input;
            if (GsonHelper.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            } else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            return this.factory.create(id, fluidInput, input, output);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                Pair<String, Integer> fluidInput = Pair.of(buffer.readUtf(), buffer.readInt());
                return this.factory.create(id, fluidInput, Ingredient.fromNetwork(buffer), buffer.readItem());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bottler recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeUtf(recipe.fluidInput.getFirst());
                buffer.writeInt(recipe.fluidInput.getSecond());
                recipe.itemInput.toNetwork(buffer);
                buffer.writeItem(recipe.result);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bottler recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BottlerRecipe>
        {
            T create(ResourceLocation id, Pair<String, Integer> fluidInput, Ingredient input, ItemStack output);
        }
    }
}
