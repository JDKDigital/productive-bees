package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BeeSpawningRecipe implements Recipe<RecipeInput>
{
    static StreamCodec<RegistryFriendlyByteBuf, HolderSet<Biome>> BIOME_STREAM = ByteBufCodecs.holderSet(Registries.BIOME);

    public final Ingredient ingredient;
    public final Ingredient spawnItem;
    public final List<Supplier<BeeIngredient>> output;
    public final HolderSet<Biome> biomes;

    public BeeSpawningRecipe(Ingredient ingredient, Ingredient spawnItem, List<Supplier<BeeIngredient>> output, HolderSet<Biome> biomes) {
        this.ingredient = ingredient;
        this.spawnItem = spawnItem;
        this.output = output;
        this.biomes = biomes;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        return false;
    }

    public boolean matches(ItemStack nest, ItemStack heldItem, Holder<Biome> biome) {
        boolean anyBiome = false;
        if (this.biomes.size() == 0) {
            anyBiome = true;
        }
         return ingredient.test(nest) && (heldItem.equals(ItemStack.EMPTY) || spawnItem.test(heldItem)) && (anyBiome || biomes.contains(biome));
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
        return ModRecipeTypes.BEE_SPAWNING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_SPAWNING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BeeSpawningRecipe>
    {
        private static final MapCodec<BeeSpawningRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                        Ingredient.CODEC.fieldOf("spawn_item").orElse(Ingredient.of(ModItems.HONEY_TREAT.get())).forGetter(recipe -> recipe.spawnItem),
                        BeeIngredient.LIST_CODEC.fieldOf("results").forGetter(recipe -> recipe.output),
                        Biome.LIST_CODEC.fieldOf("biomes").orElse(HolderSet.empty()).forGetter(recipe -> recipe.biomes)
                )
                .apply(builder, BeeSpawningRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BeeSpawningRecipe> STREAM_CODEC = StreamCodec.of(
                BeeSpawningRecipe.Serializer::toNetwork, BeeSpawningRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BeeSpawningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeSpawningRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BeeSpawningRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                Ingredient spawnItem = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);

                List<Supplier<BeeIngredient>> output = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> {
                            BeeIngredient ing = BeeIngredient.fromNetwork(buffer);
                            output.add(Lazy.of(() -> ing));
                        }
                );

                return new BeeSpawningRecipe(ingredient, spawnItem, output, BIOME_STREAM.decode(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee spawning recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeSpawningRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.spawnItem);

                buffer.writeInt(recipe.output.size());
                for (Supplier<BeeIngredient> beeOutput : recipe.output) {
                    if (beeOutput.get() != null) {
                        beeOutput.get().toNetwork(buffer);
                    } else {
                        ProductiveBees.LOGGER.error("Bee spawning recipe output missing - " + beeOutput);
                    }
                }

                BIOME_STREAM.encode(buffer, recipe.biomes);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee spawning recipe to packet. ", e);
                throw e;
            }
        }
    }
}
