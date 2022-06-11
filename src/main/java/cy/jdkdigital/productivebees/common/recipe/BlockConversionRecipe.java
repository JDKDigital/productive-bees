package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockConversionRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final Lazy<BeeIngredient> bee;
    public final BlockState stateFrom;
    public final BlockState stateTo;
    public final int chance;
    public Ingredient fromDisplay;
    public Ingredient toDisplay;

    public BlockConversionRecipe(ResourceLocation id, Lazy<BeeIngredient> bee, BlockState from, BlockState to, int chance, Ingredient fromDisplay, Ingredient toDisplay) {
        this.id = id;
        this.bee = bee;
        this.stateFrom = from;
        this.stateTo = to;
        this.chance = chance;
        this.fromDisplay = fromDisplay;
        this.toDisplay = toDisplay;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.BlockStateInventory && bee.get() != null) {
            String beeName = ((BeeHelper.BlockStateInventory) inv).getIdentifier(0);
            BlockState blockState = ((BeeHelper.BlockStateInventory) inv).getState();

            return bee.get().getBeeType().toString().equals(beeName) && (blockState.equals(this.stateFrom) || blockState.getBlock().defaultBlockState().equals(this.stateFrom));
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
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

    public static class Serializer<T extends BlockConversionRecipe> implements RecipeSerializer<T>
    {
        final BlockConversionRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BlockConversionRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String source = GsonHelper.getAsString(json, "bee");

            Lazy<BeeIngredient> sourceBee = Lazy.of(BeeIngredientFactory.getIngredient(source));

            BlockState from = jsonToBlockState(json.getAsJsonObject("from"));
            BlockState to = jsonToBlockState(json.getAsJsonObject("to"));

            Ingredient fromDisplay;
            if (json.has("from_display")) {
                fromDisplay = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "from_display"));
            } else {
                fromDisplay = Ingredient.of(new ItemStack(from.getBlock().asItem()));
            }
            Ingredient toDisplay;
            if (json.has("to_display")) {
                toDisplay = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "to_display"));
            } else {
                toDisplay = Ingredient.of(new ItemStack(to.getBlock().asItem()));
            }

            int chance = GsonHelper.getAsInt(json, "chance", 100);

            return this.factory.create(id, sourceBee, from, to, chance, fromDisplay, toDisplay);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                BeeIngredient source = BeeIngredient.fromNetwork(buffer);

                BlockState from = NbtUtils.readBlockState(buffer.readAnySizeNbt());
                BlockState to = NbtUtils.readBlockState(buffer.readAnySizeNbt());

                return this.factory.create(id, Lazy.of(() -> source), from, to, buffer.readInt(), Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.bee.get().toNetwork(buffer);

                buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateFrom));
                buffer.writeNbt(NbtUtils.writeBlockState(recipe.stateTo));

                buffer.writeInt(recipe.chance);

                recipe.fromDisplay.toNetwork(buffer);
                recipe.toDisplay.toNetwork(buffer);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee conversion recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BlockConversionRecipe>
        {
            T create(ResourceLocation id, Lazy<BeeIngredient> input, BlockState from, BlockState to, int chance, Ingredient fromDisplay, Ingredient toDisplay);
        }
    }

    private static BlockState jsonToBlockState(JsonObject json) {
        CompoundTag tag = new CompoundTag();

        tag.putString("Name", GsonHelper.getAsString(json, "Name"));

        if (json.has("Properties")) {
            CompoundTag propertyTag = new CompoundTag();

            JsonObject properties = GsonHelper.getAsJsonObject(json, "Properties");
            for(Map.Entry<String, JsonElement> entry : properties.entrySet()) {
                propertyTag.putString(entry.getKey(), entry.getValue().getAsString());
            }

            tag.put("Properties", propertyTag);
        }

        return NbtUtils.readBlockState(tag);
    }
}
