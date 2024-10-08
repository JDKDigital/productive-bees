package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nonnull;

public class BottlerRecipe implements Recipe<RecipeInput>
{
    public final SizedFluidIngredient fluidInput;
    public final Ingredient itemInput;
    public final ItemStack result;

    public BottlerRecipe(SizedFluidIngredient fluidInput, Ingredient itemInput, ItemStack result) {
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.result = result;
    }

    public boolean matches(FluidStack fluid, ItemStack inputStack) {
        if (!itemInput.test(inputStack)) {
            return false;
        }

        if (fluidInput.test(fluid)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(RecipeInput inv, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.result;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BOTTLER.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BOTTLER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BottlerRecipe>
    {
        private static final MapCodec<BottlerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluidInput),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.itemInput),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
            )
            .apply(builder, BottlerRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BottlerRecipe> STREAM_CODEC = StreamCodec.of(
                BottlerRecipe.Serializer::toNetwork, BottlerRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BottlerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BottlerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BottlerRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new BottlerRecipe(SizedFluidIngredient.STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bottler recipe from packet.", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BottlerRecipe recipe) {
            try {
                SizedFluidIngredient.STREAM_CODEC.encode(buffer, recipe.fluidInput);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.itemInput);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bottler recipe to packet.", e);
                throw e;
            }
        }
    }
}
