package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModFluids;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class CentrifugeRecipe extends TagOutputRecipe implements Recipe<RecipeInput>, TimedRecipeInterface
{
    public final Ingredient ingredient;
    public final Optional<FluidStack> fluidOutput;
    private final Integer processingTime;

    public CentrifugeRecipe(Ingredient ingredient, List<ChancedOutput> itemOutput, Optional<FluidStack> fluidOutput, int processingTime) {
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
        if (this.ingredient.getItems().length > 0) {
            ItemStack invStack = inv.getItem(InventoryHandlerHelper.INPUT_SLOT);

            if (!this.ingredient.test(invStack)) {
                return false;
            }

            for (ItemStack stack : this.ingredient.getItems()) {
                if (ItemStack.isSameItemSameComponents(invStack, stack)) {
                    return true;
                }
            }
        }
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

    @Nullable
    public Pair<Fluid, Integer> getFluidOutputs() { // TODO 1.21 use fluidstack
        if (fluidOutput.isPresent()) {
//            Fluid fluid = getPreferredFluidByMod(fluidOutput.getFirst());

            if (fluidOutput.get().getFluid() != Fluids.EMPTY) {
                return Pair.of(fluidOutput.get().getFluid(), fluidOutput.get().getAmount());
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CENTRIFUGE.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CENTRIFUGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CentrifugeRecipe>
    {
        // TODO 1.21 support fluid tags
        private static final MapCodec<CentrifugeRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                                Codec.list(ChancedOutput.CODEC).fieldOf("outputs").forGetter(recipe -> recipe.itemOutput),
                                FluidStack.CODEC.optionalFieldOf("fluid").orElse(Optional.of(new FluidStack(ModFluids.HONEY, 100))).forGetter(recipe -> recipe.fluidOutput),
                                Codec.INT.fieldOf("processingTime").orElse(0).forGetter(recipe -> recipe.processingTime)
                        )
                        .apply(builder, CentrifugeRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> STREAM_CODEC = StreamCodec.of(
                CentrifugeRecipe.Serializer::toNetwork, CentrifugeRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CentrifugeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CentrifugeRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static CentrifugeRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

                List<ChancedOutput> itemOutput = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(i -> itemOutput.add(ChancedOutput.read(buffer)));

                return new CentrifugeRecipe(ingredient, itemOutput, Optional.of(FluidStack.OPTIONAL_STREAM_CODEC.decode(buffer)), buffer.readInt());
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

                FluidStack.OPTIONAL_STREAM_CODEC.encode(buffer, recipe.fluidOutput.orElse(FluidStack.EMPTY));

                buffer.writeInt(recipe.getProcessingTime());

            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing centrifuge recipe to packet.", e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe>
        {
            T create(ResourceLocation id, Ingredient input, Map<Ingredient, IntArrayTag> itemOutput, Pair<String, Integer> fluidOutput, Integer processingTime);
        }
    }
}