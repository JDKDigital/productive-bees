package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemConversionRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<ItemConversionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            BeeIngredient.LIST_CODEC.fieldOf("bees").forGetter(recipe -> recipe.bees),
                            Ingredient.CODEC.fieldOf("ingredients").forGetter(recipe -> recipe.ingredient),
                            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.outputTemplate),
                            Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(recipe -> recipe.chance),
                            Codec.BOOL.fieldOf("pollinates").orElse(false).forGetter(recipe -> recipe.pollinates)
                    )
                    .apply(builder, ItemConversionRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemConversionRecipe> STREAM_CODEC = StreamCodec.of(
            ItemConversionRecipe::toNetwork, ItemConversionRecipe::fromNetwork
    );

    public static final RecipeSerializer<ItemConversionRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final List<Supplier<BeeIngredient>> bees;
    public Ingredient ingredient;
    public final ItemStackTemplate outputTemplate;
    public final float chance;
    public final boolean pollinates;

    private ItemStack outputCache;

    public ItemConversionRecipe(List<Supplier<BeeIngredient>> bees, Ingredient ingredient, ItemStackTemplate outputTemplate, float chance, boolean pollinates) {
        this.bees = bees;
        this.ingredient = ingredient;
        this.outputTemplate = outputTemplate;
        this.chance = chance;
        this.pollinates = pollinates;
    }

    public ItemStack getResult() {
        if (outputCache == null) {
            outputCache = outputTemplate.create();
        }
        return outputCache;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv instanceof BeeHelper.ItemInventory && bees.size() > 0) {
            Identifier beeId = BeeRegistries.resolveId(Identifier.parse(((BeeHelper.ItemInventory) inv).getIdentifier(0)));
            ItemStack inputItem = ((BeeHelper.ItemInventory) inv).getInput();

            boolean matchesInput = this.ingredient.test(inputItem);

            boolean matchesBee = false;
            for (Supplier<BeeIngredient> bee: bees) {
                matchesBee = matchesBee || BeeRegistries.resolveId(bee.get().getBeeType()).equals(beeId);
            }

            return matchesBee && matchesInput;
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

    public List<BeeIngredient> getBees() {
        List<BeeIngredient> list = new ArrayList<>();
        bees.forEach(bee -> list.add(bee.get()));
        return list;
    }

    @Override
    public RecipeSerializer<ItemConversionRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<ItemConversionRecipe> getType() {
        return ModRecipeTypes.ITEM_CONVERSION_TYPE.get();
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

    public static ItemConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            int beeCount = buffer.readInt();
            List<Supplier<BeeIngredient>> bees = new ArrayList<>();
            for (var i = 0;i < beeCount;i++) {
                BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                bees.add(Lazy.of(() -> source));
            }

            return new ItemConversionRecipe(bees, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStackTemplate.STREAM_CODEC.decode(buffer), buffer.readFloat(), buffer.readBoolean());
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
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.outputTemplate);

            buffer.writeFloat(recipe.chance);

            buffer.writeBoolean(recipe.pollinates);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing item conversion recipe to packet. ", e);
            throw e;
        }
    }
}
