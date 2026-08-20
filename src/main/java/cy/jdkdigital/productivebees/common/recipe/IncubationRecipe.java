package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class IncubationRecipe implements Recipe<RecipeInput>, TimedRecipeInterface
{
    public static final MapCodec<IncubationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.input),
                            Ingredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.catalyst),
                            ItemStackTemplate.CODEC.fieldOf("output").forGetter(recipe -> recipe.resultTemplate),
                            Codec.INT.fieldOf("processingTime").orElse(0).forGetter(recipe -> recipe.processingTime)
                    )
                    .apply(builder, IncubationRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, IncubationRecipe> STREAM_CODEC = StreamCodec.of(
            IncubationRecipe::toNetwork, IncubationRecipe::fromNetwork
    );

    public static final RecipeSerializer<IncubationRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Ingredient input;
    public final Ingredient catalyst;
    public final ItemStackTemplate resultTemplate;
    private final int processingTime;

    private ItemStack resultCache;

    public IncubationRecipe(Ingredient input, Ingredient catalyst, ItemStackTemplate resultTemplate, int processingTime) {
        this.input = input;
        this.catalyst = catalyst;
        this.resultTemplate = resultTemplate;
        this.processingTime = processingTime;
    }

    public ItemStack getResult() {
        if (resultCache == null) {
            resultCache = resultTemplate.create();
        }
        return resultCache;
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
    public ItemStack assemble(RecipeInput inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<IncubationRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<IncubationRecipe> getType() {
        return ModRecipeTypes.INCUBATION_TYPE.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static IncubationRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new IncubationRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer), buffer.readInt());
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, IncubationRecipe recipe) {
        try {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.catalyst);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.resultTemplate);
            buffer.writeInt(recipe.getProcessingTime());
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing bee incubation recipe to packet. ", e);
            throw e;
        }
    }
}
