package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeeFishingRecipe implements Recipe<RecipeInput>
{
//    static StreamCodec<ByteBuf, Biome> BIOME_STREAM = ByteBufCodecs.fromCodec(Biome.NETWORK_CODEC);
    static StreamCodec<RegistryFriendlyByteBuf, HolderSet<Biome>> BIOME_STREAM = ByteBufCodecs.holderSet(Registries.BIOME);

    private static Map<BeeFishingRecipe, List<Biome>> cachedBiomes = new HashMap<>();
    private static Map<Biome, List<BeeFishingRecipe>> cachedRecipes = new HashMap<>();
    public final Supplier<BeeIngredient> output;
    public final HolderSet<Biome> biomes;
    public final float chance;

    public BeeFishingRecipe(Supplier<BeeIngredient> output, HolderSet<Biome> biomes, float chance) {
        this.output = output;
        this.biomes = biomes;
        this.chance = chance;
    }

    @Override
    public boolean matches(RecipeInput inv, Level levelIn) {
        return false;
    }

    public boolean matches(Holder<Biome> biome, Level level) {
        if (this.biomes.size() == 0) {
            return true;
        }
        return this.biomes.contains(biome);
    }

    public static List<Biome> getBiomeList(BeeFishingRecipe recipe, Level level) {
        if (!cachedBiomes.containsKey(recipe)) {
            List<Biome> list = new ArrayList<>();

            for (Holder<Biome> biome: recipe.biomes) {
                list.add(biome.value());
            }

            cachedBiomes.put(recipe, list);
        }
        return cachedBiomes.get(recipe);
    }

    public static List<BeeFishingRecipe> getRecipeList(Holder<Biome> biome, Level level) {
        if (!cachedRecipes.containsKey(biome.value())) {
            List<BeeFishingRecipe> list = new ArrayList<>();

            List<RecipeHolder<BeeFishingRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BEE_FISHING_TYPE.get());
            for (RecipeHolder<BeeFishingRecipe> recipe: allRecipes) {
                if (recipe.value().matches(biome, level)) {
                    list.add(recipe.value());
                }
            }

            cachedRecipes.put(biome.value(), list);
        }
        return cachedRecipes.get(biome.value());
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
        return ModRecipeTypes.BEE_FISHING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_FISHING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BeeFishingRecipe>
    {
        private static final MapCodec<BeeFishingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.CODEC.fieldOf("bee").forGetter(recipe -> recipe.output),
                                Biome.LIST_CODEC.fieldOf("biomes").forGetter(recipe -> recipe.biomes),
                                Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(recipe -> recipe.chance)
                        )
                        .apply(builder, BeeFishingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BeeFishingRecipe> STREAM_CODEC = StreamCodec.of(
                BeeFishingRecipe.Serializer::toNetwork, BeeFishingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BeeFishingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeFishingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BeeFishingRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                BeeIngredient output = BeeIngredient.fromNetwork(buffer);

                return new BeeFishingRecipe(Lazy.of(() -> output), BIOME_STREAM.decode(buffer), buffer.readFloat());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee fishing recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeFishingRecipe recipe) {
            try {
                recipe.output.get().toNetwork(buffer);

                BIOME_STREAM.encode(buffer, recipe.biomes);

                buffer.writeFloat(recipe.chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee fishing recipe to packet. ", e);
                throw e;
            }
        }
    }
}
