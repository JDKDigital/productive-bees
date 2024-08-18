package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe extends TagOutputRecipe implements Recipe<RecipeInput>
{
    public final Supplier<BeeIngredient> ingredient;

    public AdvancedBeehiveRecipe(Supplier<BeeIngredient> ingredient, List<ChancedOutput> itemOutput) {
        super(itemOutput);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && ingredient.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier();
            return beeName.equals(ingredient.get().getBeeType().toString());
        }
        return false;
    }

    @Override
    public Map<ItemStack, ChancedOutput> getRecipeOutputs() {
        Map<ItemStack, ChancedOutput> output = super.getRecipeOutputs();

        for (Map.Entry<ItemStack, ChancedOutput> entry : output.entrySet()) {
            if (ingredient.get().isConfigurable()) {
                if (entry.getKey().getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
                    BeeCreator.setType(ingredient.get().getBeeType(), entry.getKey());
                }
            }
        }

        return output;
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
        return ModRecipeTypes.ADVANCED_BEEHIVE.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<AdvancedBeehiveRecipe>
    {
        public static final MapCodec<AdvancedBeehiveRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        BeeIngredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                        Codec.list(ChancedOutput.CODEC).fieldOf("results").forGetter(recipe -> recipe.itemOutput)
                )
                .apply(builder, AdvancedBeehiveRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedBeehiveRecipe> STREAM_CODEC = StreamCodec.of(
                AdvancedBeehiveRecipe.Serializer::toNetwork, AdvancedBeehiveRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<AdvancedBeehiveRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AdvancedBeehiveRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static AdvancedBeehiveRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                BeeIngredient ingredient = BeeIngredient.fromNetwork(buffer);
                List<ChancedOutput> itemOutput = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(i -> itemOutput.add(ChancedOutput.read(buffer)));

                return new AdvancedBeehiveRecipe(() -> ingredient, itemOutput);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading beehive produce recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, AdvancedBeehiveRecipe recipe) {
            try {
                if (recipe.ingredient.get() != null) {
                    recipe.ingredient.get().toNetwork(buffer);
                } else {
                    throw new RuntimeException("Bee produce recipe ingredient missing");
                }

                buffer.writeInt(recipe.itemOutput.size());
                recipe.itemOutput.forEach(chancedRecipe -> {
                    ChancedOutput.write(buffer, chancedRecipe);
                });
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing beehive produce recipe to packet.", e);
                throw e;
            }
        }
    }
}
