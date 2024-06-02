package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.compat.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeHelper;
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
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemConversionRecipe implements Recipe<Container>
{
    public final List<Supplier<BeeIngredient>> bees;
    public Ingredient ingredient;
    public ItemStack output;
    public final float chance;
    public final boolean pollinates;

    public ItemConversionRecipe(List<Supplier<BeeIngredient>> bees, Ingredient ingredient, ItemStack output, float chance, boolean pollinates) {
        this.bees = bees;
        this.ingredient = ingredient;
        this.output = output;
        this.chance = chance;
        this.pollinates = pollinates;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.ItemInventory && bees.size() > 0) {
            String beeName = ((BeeHelper.ItemInventory) inv).getIdentifier(0);
            ItemStack inputItem = ((BeeHelper.ItemInventory) inv).getInput();

            boolean matchesInput = this.ingredient.test(inputItem);

            boolean matchesBee = false;
            for (Supplier<BeeIngredient> bee: bees) {
                matchesBee = matchesBee || bee.get().getBeeType().toString().equals(beeName);
            }

            return matchesBee && matchesInput;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    public List<BeeIngredient> getBees() {
        List<BeeIngredient> list = new ArrayList<>();
        bees.forEach(bee -> list.add(bee.get()));
        return list;
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
        return ModRecipeTypes.ITEM_CONVERSION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ITEM_CONVERSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ItemConversionRecipe>
    {
        private static final MapCodec<ItemConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.LIST_CODEC.fieldOf("bees").forGetter(recipe -> recipe.bees),
                                Ingredient.CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.ingredient),
                                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                                Codec.FLOAT.fieldOf("chance").forGetter(recipe -> recipe.chance),
                                Codec.BOOL.fieldOf("pollinates").orElse(false).forGetter(recipe -> recipe.pollinates)
                        )
                        .apply(builder, ItemConversionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemConversionRecipe> STREAM_CODEC = StreamCodec.of(
                ItemConversionRecipe.Serializer::toNetwork, ItemConversionRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ItemConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ItemConversionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ItemConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                int beeCount = buffer.readInt();
                List<Supplier<BeeIngredient>> bees = new ArrayList<>();
                for (var i = 0;i < beeCount;i++) {
                    BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                    bees.add(Lazy.of(() -> source));
                }

                return new ItemConversionRecipe(bees, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer), buffer.readInt(), buffer.readBoolean());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading item conversion recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, ItemConversionRecipe recipe) {
            try {
                buffer.writeInt(recipe.bees.size());
                recipe.bees.forEach(bee -> bee.get().toNetwork(buffer));

                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.output);

                buffer.writeFloat(recipe.chance);

                buffer.writeBoolean(recipe.pollinates);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing item conversion recipe to packet. ", e);
                throw e;
            }
        }
    }
}
