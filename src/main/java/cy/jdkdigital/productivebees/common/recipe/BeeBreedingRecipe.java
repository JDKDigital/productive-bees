package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public class BeeBreedingRecipe implements Recipe<RecipeInput>, TimedRecipeInterface
{
    public final Supplier<BeeIngredient> parent1;
    public final Supplier<BeeIngredient> parent2;
    public final Supplier<BeeIngredient> offspring;

    public BeeBreedingRecipe(Supplier<BeeIngredient> parent1, Supplier<BeeIngredient> parent2, Supplier<BeeIngredient> offspring) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.offspring = offspring;
    }

    @Override
    public int getProcessingTime() {
        return ProductiveBeesConfig.GENERAL.breedingChamberProcessingTime.get();
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory) {
            String beeName1 = ((BeeHelper.IdentifierInventory) inv).getIdentifier(0);
            String beeName2 = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);
            for (Supplier<BeeIngredient> parent : List.of(parent1, parent2)) {
                if (parent.get() != null) {
                    String parentName = parent.get().getBeeType().toString();
                    if (!parentName.equals(beeName1) && !parentName.equals(beeName2)) {
                        return false;
                    }
                } else {
                    ProductiveBees.LOGGER.warn("Bee not found in breeding recipe");
                    return false;
                }
            }
            return true;
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

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_BREEDING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_BREEDING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BeeBreedingRecipe>
    {
        private static final MapCodec<BeeBreedingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.CODEC.fieldOf("parent1").forGetter(recipe -> recipe.parent1),
                                BeeIngredient.CODEC.fieldOf("parent2").forGetter(recipe -> recipe.parent2),
                                BeeIngredient.CODEC.fieldOf("offspring").forGetter(recipe -> recipe.offspring)
                        )
                        .apply(builder, BeeBreedingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BeeBreedingRecipe> STREAM_CODEC = StreamCodec.of(
                BeeBreedingRecipe.Serializer::toNetwork, BeeBreedingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BeeBreedingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeBreedingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BeeBreedingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                BeeIngredient ing1 = BeeIngredient.fromNetwork(buffer);
                BeeIngredient ing2 = BeeIngredient.fromNetwork(buffer);
                BeeIngredient offspring = BeeIngredient.fromNetwork(buffer);

                return new BeeBreedingRecipe(Lazy.of(() -> ing1), Lazy.of(() -> ing2), Lazy.of(() -> offspring));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee breeding recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeBreedingRecipe recipe) {
            try {
                recipe.parent1.get().toNetwork(buffer);
                recipe.parent2.get().toNetwork(buffer);
                recipe.offspring.get().toNetwork(buffer);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee breeding recipe to packet. ", e);
                throw e;
            }
        }
    }
}
