package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class BottlerRecipe implements Recipe<Container>
{
    public final FluidStack fluidInput;
    public final Ingredient itemInput;
    public final ItemStack result;

    public BottlerRecipe(FluidStack fluidInput, Ingredient itemInput, ItemStack result) {
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.result = result;
    }

    public boolean matches(FluidStack fluid, ItemStack inputStack) {
        if (!itemInput.test(inputStack)) {
            return false;
        }

        if (fluid.getAmount() < fluidInput.getAmount()) {
            return false;
        }

        if (fluidInput.getFluid().equals(fluid.getFluid())) {
            return true;
        }

//        TagKey<Fluid> fluidTag = ModTags.getFluidTag(new ResourceLocation(fluidInput.getFirst()));
//        if (fluid.getFluid().is(fluidTag)) {
//            return true;
//        }

        return false;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv, HolderLookup.Provider pRegistries) {
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
        // TODO support tag fluid again
        private static final MapCodec<BottlerRecipe> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                FluidStack.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluidInput),
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
                return new BottlerRecipe(FluidStack.STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bottler recipe from packet.", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BottlerRecipe recipe) {
            try {
                FluidStack.STREAM_CODEC.encode(buffer, recipe.fluidInput);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.itemInput);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bottler recipe to packet.", e);
                throw e;
            }
        }
    }
}
