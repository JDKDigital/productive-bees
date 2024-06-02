package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class IncubationRecipe implements Recipe<Container>, TimedRecipeInterface
{
    public final Ingredient input;
    public final Ingredient catalyst;
    public final Ingredient result;

    public IncubationRecipe(Ingredient input, Ingredient catalyst, Ingredient result) {
        this.input = input;
        this.catalyst = catalyst;
        this.result = result;
    }

    @Override
    public int getProcessingTime() {
        // TODO implement
        return ProductiveBeesConfig.GENERAL.incubatorProcessingTime.get();
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
                                Ingredient.CODEC.fieldOf("output").forGetter(recipe -> recipe.input)
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
                return new IncubationRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, IncubationRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.result);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee incubation recipe to packet. ", e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends IncubationRecipe>
        {
            T create(ResourceLocation id, Ingredient item, Ingredient catalyst, Ingredient output);
        }
    }
}
