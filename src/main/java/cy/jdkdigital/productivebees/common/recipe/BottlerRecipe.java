package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nonnull;

public class BottlerRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<BottlerRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        builder -> builder.group(
            SizedFluidIngredient.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.fluidInput),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.itemInput),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.resultTemplate)
        )
        .apply(builder, BottlerRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BottlerRecipe> STREAM_CODEC = StreamCodec.of(
            BottlerRecipe::toNetwork, BottlerRecipe::fromNetwork
    );

    public static final RecipeSerializer<BottlerRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final SizedFluidIngredient fluidInput;
    public final Ingredient itemInput;
    public final ItemStackTemplate resultTemplate;

    private ItemStack resultCache;

    public BottlerRecipe(SizedFluidIngredient fluidInput, Ingredient itemInput, ItemStackTemplate resultTemplate) {
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.resultTemplate = resultTemplate;
    }

    public ItemStack getResult() {
        if (resultCache == null) {
            resultCache = resultTemplate.create();
        }
        return resultCache;
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
    public ItemStack assemble(RecipeInput inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<BottlerRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<BottlerRecipe> getType() {
        return ModRecipeTypes.BOTTLER_TYPE.get();
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

    public static BottlerRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new BottlerRecipe(SizedFluidIngredient.STREAM_CODEC.decode(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer));
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading bottler recipe from packet.", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BottlerRecipe recipe) {
        try {
            SizedFluidIngredient.STREAM_CODEC.encode(buffer, recipe.fluidInput);
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.itemInput);
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.resultTemplate);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing bottler recipe to packet.", e);
            throw e;
        }
    }
}
