package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
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

public class BlockConversionRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<BlockConversionRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            BeeIngredient.LIST_CODEC.fieldOf("bees").forGetter(recipe -> recipe.bees),
                            Ingredient.CODEC.optionalFieldOf("input").forGetter(recipe -> recipe.input),
                            BlockState.CODEC.fieldOf("from").orElse(Blocks.AIR.defaultBlockState()).forGetter(recipe -> recipe.stateFrom),
                            BlockState.CODEC.fieldOf("to").forGetter(recipe -> recipe.stateTo),
                            Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(recipe -> recipe.chance),
                            Ingredient.CODEC.optionalFieldOf("from_display").forGetter(recipe -> recipe.fromDisplay),
                            Ingredient.CODEC.optionalFieldOf("to_display").forGetter(recipe -> recipe.toDisplay),
                            Codec.BOOL.fieldOf("pollinates").orElse(false).forGetter(recipe -> recipe.pollinates)
                    )
                    .apply(builder, BlockConversionRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockConversionRecipe> STREAM_CODEC = StreamCodec.of(
            BlockConversionRecipe::toNetwork, BlockConversionRecipe::fromNetwork
    );

    public static final RecipeSerializer<BlockConversionRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final List<Supplier<BeeIngredient>> bees;
    public Optional<Ingredient> input;
    public final BlockState stateFrom;
    public final BlockState stateTo;
    public final float chance;
    public Optional<Ingredient> fromDisplay;
    public Optional<Ingredient> toDisplay;
    public boolean pollinates;

    public BlockConversionRecipe(List<Supplier<BeeIngredient>> bees, Optional<Ingredient> input, BlockState from, BlockState to, float chance, Optional<Ingredient> fromDisplay, Optional<Ingredient> toDisplay, boolean pollinates) {
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
    public boolean matches(RecipeInput inv, Level level) {
        if (inv instanceof BeeHelper.BlockStateInventory && bees.size() > 0) {
            Identifier beeId = BeeRegistries.resolveId(Identifier.parse(((BeeHelper.BlockStateInventory) inv).getIdentifier(0)));
            BlockState blockState = ((BeeHelper.BlockStateInventory) inv).getState();

            boolean matchesBlock;
            if (this.input.isPresent()) {
                Ingredient ing = this.input.get();
                matchesBlock = !blockState.getBlock().equals(this.stateTo.getBlock()) && ing.test(new ItemStack(blockState.getBlock()));
            } else {
                matchesBlock = (blockState.equals(this.stateFrom) || blockState.getBlock().defaultBlockState().equals(this.stateFrom));
            }

            boolean matchesBee = false;
            for (Supplier<BeeIngredient> bee: bees) {
                matchesBee = matchesBee || BeeRegistries.resolveId(bee.get().getBeeType()).equals(beeId);
            }

            return matchesBee && matchesBlock;
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
    public RecipeSerializer<BlockConversionRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<BlockConversionRecipe> getType() {
        return ModRecipeTypes.BLOCK_CONVERSION_TYPE.get();
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

    public static BlockConversionRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            int beeCount = buffer.readInt();
            List<Supplier<BeeIngredient>> bees = new ArrayList<>();
            for (var i = 0;i < beeCount;i++) {
                BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                bees.add(Lazy.of(() -> source));
            }
            boolean hasInput = buffer.readBoolean();
            Optional<Ingredient> input = hasInput ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)) : Optional.empty();
            BlockState from = readBlockState(buffer.readNbt());
            BlockState to = readBlockState(buffer.readNbt());
            float chance = buffer.readFloat();
            boolean hasFromDisplay = buffer.readBoolean();
            Optional<Ingredient> fromDisplay = hasFromDisplay ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)) : Optional.empty();
            boolean hasToDisplay = buffer.readBoolean();
            Optional<Ingredient> toDisplay = hasToDisplay ? Optional.of(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer)) : Optional.empty();
            return new BlockConversionRecipe(bees, input, from, to, chance, fromDisplay, toDisplay, buffer.readBoolean());
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading block conversion recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BlockConversionRecipe recipe) {
        try {
            buffer.writeInt(recipe.bees.size());
            recipe.bees.forEach(bee -> bee.get().toNetwork(buffer));

            buffer.writeBoolean(recipe.input.isPresent());
            recipe.input.ifPresent(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing));

            buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateFrom));
            buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateTo));

            buffer.writeFloat(recipe.chance);

            buffer.writeBoolean(recipe.fromDisplay.isPresent());
            recipe.fromDisplay.ifPresent(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing));
            buffer.writeBoolean(recipe.toDisplay.isPresent());
            recipe.toDisplay.ifPresent(ing -> Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing));
            buffer.writeBoolean(recipe.pollinates);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing block conversion recipe to packet.", e);
            throw e;
        }
    }

    private static BlockState readBlockState(@Nullable CompoundTag tag) {
        if (tag == null) return Blocks.AIR.defaultBlockState();

        Identifier resourcelocation = Identifier.parse(tag.getString("Name").orElse(""));
        Block block = BuiltInRegistries.BLOCK.get(resourcelocation).map(Holder::value).orElse(Blocks.AIR);
        BlockState blockstate = block.defaultBlockState();
        Optional<CompoundTag> propsOpt = tag.getCompound("Properties");
        if (propsOpt.isPresent()) {
            CompoundTag compoundtag = propsOpt.get();
            StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();

            for (String propertyName : compoundtag.keySet()) {
                Property<?> property = statedefinition.getProperty(propertyName);
                if (property != null) {
                    blockstate = setValueHelper(blockstate, property, propertyName, compoundtag, tag);
                }
            }
        }

        return blockstate;
    }

    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState blockState, Property<T> property, String propertyName, CompoundTag tag, CompoundTag stateTag) {
        String value = tag.getString(propertyName).orElse("");
        Optional<T> optional = property.getValue(value);
        if (optional.isPresent()) {
            return blockState.setValue(property, optional.get());
        } else {
            ProductiveBees.LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", propertyName, value, stateTag.toString());
            return blockState;
        }
    }
}
