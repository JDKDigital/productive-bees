package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BeeConversionRecipe implements Recipe<Container>
{
    public final Supplier<BeeIngredient> source;
    public final Supplier<BeeIngredient> result;
    public final Ingredient item;
    public final int chance;

    public BeeConversionRecipe(Supplier<BeeIngredient> ingredients, Lazy<BeeIngredient> result, Ingredient item, int chance) {
        this.source = ingredients;
        this.result = result;
        this.item = item;
        this.chance = chance;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && source.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(0);
            String itemName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);

            String parentName = source.get().getBeeType().toString();

            boolean matchesItem = false;
            for (ItemStack stack : this.item.getItems()) {
                if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(itemName)) {
                    matchesItem = true;
                }
            }

            return parentName.equals(beeName) && matchesItem;
        }
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
        return ModRecipeTypes.BEE_CONVERSION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_CONVERSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BeeConversionRecipe>
    {
        private static final MapCodec<BeeConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.CODEC.fieldOf("source").forGetter(recipe -> recipe.source),
                                BeeIngredient.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                                Ingredient.CODEC.fieldOf("item").forGetter(recipe > recipe.item),
                                Codec.FLOAT.fieldOf("chance").forGetter(recipe > recipe.chance)
                        )
                        .apply(builder, BeeConversionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BeeConversionRecipe> STREAM_CODEC = StreamCodec.of(
                BeeConversionRecipe.Serializer::toNetwork, BeeConversionRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BeeConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeConversionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BeeConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                BeeIngredient result = BeeIngredient.fromNetwork(buffer);
                return new BeeConversionRecipe(Lazy.of(() -> source), Lazy.of(() -> result), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet.", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeConversionRecipe recipe) {
            try {
                recipe.source.get().toNetwork(buffer);
                recipe.result.get().toNetwork(buffer);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.item);
                buffer.writeInt(recipe.chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee conversion recipe to packet.", e);
                throw e;
            }
        }
    }
}
