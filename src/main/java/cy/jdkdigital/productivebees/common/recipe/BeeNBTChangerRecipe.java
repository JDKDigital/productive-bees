package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BeeNBTChangerRecipe implements Recipe<RecipeInput>
{
    public final Supplier<BeeIngredient> bee;
    public final Ingredient item;
    public String attribute;
    public String method;
    public int value;
    public int min;
    public int max;

    public BeeNBTChangerRecipe(Supplier<BeeIngredient> ingredients, Ingredient item, String attribute, String method, int value, int min, int max) {
        this.bee = ingredients;
        this.item = item;
        this.attribute = attribute;
        this.method = method;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        // TODO 1.21 reimplement with components somehow
//        if (inv instanceof BeeHelper.ItemInventory && bee.get() != null) {
//            String beeName = ((BeeHelper.ItemInventory) inv).getIdentifier(0);
//            ItemStack item = ((BeeHelper.ItemInventory) inv).getInput();
//
//            String parentName = bee.get().getBeeType().toString();
//
//            boolean matchesItem = false;
//            for (ItemStack stack : this.item.getItems()) {
//                if (ItemStack.isSameItem(stack, item)) {
//                    var tag = item.getTag();
//                    if (tag != null && tag.contains(attribute) && tag.getInt(attribute) > min && tag.getInt(attribute) < max) {
//                        matchesItem = true;
//                    }
//                }
//            }
//
//            return parentName.equals(beeName) && matchesItem;
//        }
        return false;
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
        return ModRecipeTypes.BEE_NBT_CHANGER.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_NBT_CHANGER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BeeNBTChangerRecipe>
    {
        private static final MapCodec<BeeNBTChangerRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.CODEC.fieldOf("bee").forGetter(recipe -> recipe.bee),
                                Ingredient.CODEC.fieldOf("item").forGetter(recipe -> recipe.item),
                                Codec.STRING.fieldOf("attribute").forGetter(recipe -> recipe.attribute),
                                Codec.STRING.fieldOf("method").forGetter(recipe -> recipe.method),
                                Codec.INT.fieldOf("value").orElse(0).forGetter(recipe -> recipe.value),
                                Codec.INT.fieldOf("min").orElse(0).forGetter(recipe -> recipe.min),
                                Codec.INT.fieldOf("max").orElse(100).forGetter(recipe -> recipe.max)
                        )
                        .apply(builder, BeeNBTChangerRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BeeNBTChangerRecipe> STREAM_CODEC = StreamCodec.of(
                BeeNBTChangerRecipe.Serializer::toNetwork, BeeNBTChangerRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BeeNBTChangerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeNBTChangerRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BeeNBTChangerRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                BeeIngredient bee = BeeIngredient.fromNetwork(buffer);
                return new BeeNBTChangerRecipe(Lazy.of(() -> bee), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readUtf(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeNBTChangerRecipe recipe) {
            try {
                recipe.bee.get().toNetwork(buffer);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.item);
                buffer.writeUtf(recipe.attribute);
                buffer.writeUtf(recipe.method);
                buffer.writeInt(recipe.value);
                buffer.writeInt(recipe.min);
                buffer.writeInt(recipe.max);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee conversion recipe to packet. ", e);
                throw e;
            }
        }
    }
}
