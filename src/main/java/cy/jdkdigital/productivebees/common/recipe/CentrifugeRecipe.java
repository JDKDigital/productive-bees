package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements Recipe<RecipeInput>, TimedRecipeInterface
{
    public static final MapCodec<CentrifugeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                            Codec.list(ChancedOutput.CODEC).fieldOf("outputs").forGetter(recipe -> recipe.itemOutput),
                            SizedFluidIngredient.CODEC.fieldOf("fluid").orElse(SizedFluidIngredient.of(ModFluids.HONEY.get(), 100)).forGetter(recipe -> recipe.fluidOutput),
                            Codec.INT.fieldOf("processingTime").orElse(0).forGetter(recipe -> recipe.processingTime)
                    )
                    .apply(builder, CentrifugeRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.of(
            CentrifugeRecipe::toNetwork, CentrifugeRecipe::fromNetwork
    );

    public static final RecipeSerializer<CentrifugeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Ingredient ingredient;
    public final SizedFluidIngredient fluidOutput;
    private final Integer processingTime;

    public CentrifugeRecipe(Ingredient ingredient, List<ChancedOutput> itemOutput, SizedFluidIngredient fluidOutput, int processingTime) {
        super(itemOutput);
        this.ingredient = ingredient;
        this.fluidOutput = fluidOutput;
        this.processingTime = processingTime;
    }

    @Override
    public int getProcessingTime() {
        return processingTime > 0 ? processingTime : ProductiveBeesConfig.GENERAL.centrifugeProcessingTime.get();
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (this.ingredient.isEmpty()) {
            return false;
        }
        ItemStack invStack = inv.getItem(InventoryHandlerHelper.INPUT_SLOT);
        return this.ingredient.test(invStack);
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

    public FluidStack getFluidOutputs() {
        return getPreferredFluidStackByMod(fluidOutput);
    }

    @Override
    public RecipeSerializer<CentrifugeRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<CentrifugeRecipe> getType() {
        return ModRecipeTypes.CENTRIFUGE_TYPE.get();
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

    public static CentrifugeRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

            List<ChancedOutput> itemOutput = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> itemOutput.add(ChancedOutput.read(buffer)));

            return new CentrifugeRecipe(ingredient, itemOutput, SizedFluidIngredient.STREAM_CODEC.decode(buffer), buffer.readInt());
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading centrifuge recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, @Nonnull CentrifugeRecipe recipe) {
        try {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);

            buffer.writeInt(recipe.itemOutput.size());
            recipe.itemOutput.forEach(chancedRecipe -> {
                ChancedOutput.write(buffer, chancedRecipe);
            });

            SizedFluidIngredient.STREAM_CODEC.encode(buffer, recipe.fluidOutput);

            buffer.writeInt(recipe.getProcessingTime());

        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing centrifuge recipe to packet.", e);
            throw e;
        }
    }
}
