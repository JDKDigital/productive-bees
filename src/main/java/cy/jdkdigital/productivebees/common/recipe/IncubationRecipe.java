package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class IncubationRecipe implements Recipe<RecipeInput>, TimedRecipeInterface
{
    public final Ingredient input;
    public final Ingredient catalyst;
    public final ItemStack result;
    private final int processingTime;

    public IncubationRecipe(Ingredient input, Ingredient catalyst, ItemStack result, int processingTime) {
        this.input = input;
        this.catalyst = catalyst;
        this.result = result;
        this.processingTime = processingTime;
    }

    @Override
    public int getProcessingTime() {
        return processingTime > 0 ? processingTime : ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get();
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
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
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.INCUBATION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.INCUBATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<IncubationRecipe>
    {
        private static final MapCodec<IncubationRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                                Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.catalyst),
                                ItemStack.CODEC.fieldOf("output").forGetter(recipe -> recipe.result),
                                Codec.INT.fieldOf("processingTime").orElse(0).forGetter(recipe -> recipe.processingTime)
                        )
                        .apply(builder, IncubationRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, IncubationRecipe> STREAM_CODEC = StreamCodec.of(
                IncubationRecipe.Serializer::toNetwork, IncubationRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<IncubationRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IncubationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static IncubationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new IncubationRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer), buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, IncubationRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
                buffer.writeInt(recipe.getProcessingTime());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee incubation recipe to packet. ", e);
                throw e;
            }
        }
    }


}
