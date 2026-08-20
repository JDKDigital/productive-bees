package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BeeConversionRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<BeeConversionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            BeeIngredient.CODEC.fieldOf("source").forGetter(recipe -> recipe.source),
                            BeeIngredient.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                            Ingredient.CODEC.fieldOf("item").forGetter(recipe -> recipe.item),
                            Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(recipe -> recipe.chance)
                    )
                    .apply(builder, BeeConversionRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeConversionRecipe> STREAM_CODEC = StreamCodec.of(
            BeeConversionRecipe::toNetwork, BeeConversionRecipe::fromNetwork
    );

    public static final RecipeSerializer<BeeConversionRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Supplier<BeeIngredient> source;
    public final Supplier<BeeIngredient> result;
    public final Ingredient item;
    public final float chance;

    public BeeConversionRecipe(Supplier<BeeIngredient> ingredients, Supplier<BeeIngredient> result, Ingredient item, float chance) {
        this.source = ingredients;
        this.result = result;
        this.item = item;
        this.chance = chance;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && source.get() != null) {
            Identifier beeId = BeeRegistries.resolveId(Identifier.parse(((BeeHelper.IdentifierInventory) inv).getIdentifier(0)));
            String itemName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);

            Identifier parentId = BeeRegistries.resolveId(source.get().getBeeType());

            boolean matchesItem = false;
            for (var holder : this.item.items().toList()) {
                if (BuiltInRegistries.ITEM.getKey(holder.value()).toString().equals(itemName)) {
                    matchesItem = true;
                }
            }

            return parentId.equals(beeId) && matchesItem;
        }
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
    public RecipeSerializer<BeeConversionRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<BeeConversionRecipe> getType() {
        return ModRecipeTypes.BEE_CONVERSION_TYPE.get();
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

    public static BeeConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            BeeIngredient source = BeeIngredient.fromNetwork(buffer);
            BeeIngredient result = BeeIngredient.fromNetwork(buffer);
            return new BeeConversionRecipe(Lazy.of(() -> source), Lazy.of(() -> result), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readFloat());
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
            buffer.writeFloat(recipe.chance);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing bee conversion recipe to packet.", e);
            throw e;
        }
    }
}
