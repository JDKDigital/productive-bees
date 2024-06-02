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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class BlockConversionRecipe implements Recipe<Container>
{
    public final List<Supplier<BeeIngredient>> bees;
    public Ingredient input;
    public final BlockState stateFrom;
    public final BlockState stateTo;
    public final float chance;
    public Optional<Ingredient> fromDisplay;
    public Optional<Ingredient> toDisplay;
    public boolean pollinates;

    public BlockConversionRecipe(List<Supplier<BeeIngredient>> bees, Ingredient input, BlockState from, BlockState to, float chance, Optional<Ingredient> fromDisplay, Optional<Ingredient> toDisplay, boolean pollinates) {
        this.bees = bees;
        this.input = input;
        this.stateFrom = from;
        this.stateTo = to;
        this.chance = chance;
        this.fromDisplay = fromDisplay;
        this.toDisplay = toDisplay;
        this.pollinates = pollinates;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.BlockStateInventory && bees.size() > 0) {
            String beeName = ((BeeHelper.BlockStateInventory) inv).getIdentifier(0);
            BlockState blockState = ((BeeHelper.BlockStateInventory) inv).getState();

            boolean matchesBlock;
            if (!this.input.isEmpty()) {
                matchesBlock = !blockState.getBlock().equals(this.stateTo.getBlock()) && this.input.test(new ItemStack(blockState.getBlock()));
            } else {
                matchesBlock = (blockState.equals(this.stateFrom) || blockState.getBlock().defaultBlockState().equals(this.stateFrom));
            }

            boolean matchesBee = false;
            for (Supplier<BeeIngredient> bee: bees) {
                matchesBee = matchesBee || bee.get().getBeeType().toString().equals(beeName);
            }

            return matchesBee && matchesBlock;
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
        return ModRecipeTypes.BLOCK_CONVERSION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BLOCK_CONVERSION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BlockConversionRecipe>
    {
        private static final MapCodec<BlockConversionRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                BeeIngredient.LIST_CODEC.fieldOf("bees").forGetter(recipe -> recipe.bees),
                                Ingredient.CODEC.fieldOf("input").orElse(Ingredient.EMPTY).forGetter(recipe -> recipe.input),
                                BlockState.CODEC.fieldOf("from").orElse(Blocks.AIR.defaultBlockState()).forGetter(recipe -> recipe.stateFrom),
                                BlockState.CODEC.fieldOf("to").forGetter(recipe -> recipe.stateTo),
                                Codec.FLOAT.fieldOf("chance").forGetter(recipe -> recipe.chance),
                                Ingredient.CODEC.optionalFieldOf("from_display").forGetter(recipe -> recipe.fromDisplay),
                                Ingredient.CODEC.optionalFieldOf("to_display").forGetter(recipe -> recipe.toDisplay),
                                Codec.BOOL.fieldOf("pollinates").orElse(false).forGetter(recipe -> recipe.pollinates)
                        )
                        .apply(builder, BlockConversionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, BlockConversionRecipe> STREAM_CODEC = StreamCodec.of(
                BlockConversionRecipe.Serializer::toNetwork, BlockConversionRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<BlockConversionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BlockConversionRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static BlockConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                int beeCount = buffer.readInt();
                List<Supplier<BeeIngredient>> bees = new ArrayList<>();
                for (var i = 0;i < beeCount;i++) {
                    BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                    bees.add(Lazy.of(() -> source));
                }
                Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                BlockState from = readBlockState(buffer.readNbt());
                BlockState to = readBlockState(buffer.readNbt());

                return new BlockConversionRecipe(bees, input, from, to, buffer.readInt(), Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)), Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)), buffer.readBoolean());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading block conversion recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BlockConversionRecipe recipe) {
            try {
                buffer.writeInt(recipe.bees.size());
                recipe.bees.forEach(bee -> bee.get().toNetwork(buffer));

                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);

                buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateFrom));
                buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateTo));

                buffer.writeFloat(recipe.chance);

                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.fromDisplay.orElse(Ingredient.EMPTY));
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.toDisplay.orElse(Ingredient.EMPTY));
                buffer.writeBoolean(recipe.pollinates);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing block conversion recipe to packet. ", e);
                throw e;
            }
        }
    }

    private static BlockState readBlockState(@Nullable CompoundTag tag) {
        if (tag == null) return Blocks.AIR.defaultBlockState();

        ResourceLocation resourcelocation = new ResourceLocation(tag.getString("Name"));
        Block block = BuiltInRegistries.BLOCK.get(resourcelocation);
        BlockState blockstate = block.defaultBlockState();
        if (tag.contains("Properties", 10)) {
            CompoundTag compoundtag = tag.getCompound("Properties");
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();

            for (String propertyName : compoundtag.getAllKeys()) {
                Property<?> property = statedefinition.getProperty(propertyName);
                if (property != null) {
                    blockstate = setValueHelper(blockstate, property, propertyName, compoundtag, tag);
                }
            }
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState blockState, Property<T> property, String propertyName, CompoundTag tag, CompoundTag stateTag) {
        Optional<T> optional = property.getValue(tag.getString(propertyName));
        if (optional.isPresent()) {
            return blockState.setValue(property, optional.get());
        } else {
            ProductiveBees.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", propertyName, tag.getString(propertyName), stateTag.toString());
            return blockState;
        }
    }
}
