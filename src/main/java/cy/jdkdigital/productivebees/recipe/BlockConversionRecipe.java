package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.Map;

public class BlockConversionRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BlockConversionRecipe> BLOCK_CONVERSION = IRecipeType.register(ProductiveBees.MODID + ":block_conversion");

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
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.BlockStateInventory && bee.get() != null) {
            String beeName = ((BeeHelper.BlockStateInventory) inv).getIdentifier(0);
            BlockState blockState = ((BeeHelper.BlockStateInventory) inv).getState();

            return bee.get().getBeeType().toString().equals(beeName) && blockState.equals(this.stateFrom);
        }
        ProductiveBees.LOGGER.warn("conversion recipe source is null " + this);
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(IInventory inv) {
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
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BLOCK_CONVERSION.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BLOCK_CONVERSION;
    }

    public static class Serializer<T extends BlockConversionRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BlockConversionRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BlockConversionRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String source = JSONUtils.getAsString(json, "bee");

            Lazy<BeeIngredient> sourceBee = Lazy.of(BeeIngredientFactory.getIngredient(source));

            BlockState from = jsonToBlockState(json.getAsJsonObject("from"));
            BlockState to = jsonToBlockState(json.getAsJsonObject("to"));

            Ingredient fromDisplay;
            if (json.has("from_display")) {
                fromDisplay = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "from_display"));
            } else {
                fromDisplay = Ingredient.of(new ItemStack(from.getBlock().asItem()));
            }
            Ingredient toDisplay;
            if (json.has("to_display")) {
                toDisplay = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "to_display"));
            } else {
                toDisplay = Ingredient.of(new ItemStack(to.getBlock().asItem()));
            }

            int chance = JSONUtils.getAsInt(json, "chance", 100);

            return this.factory.create(id, sourceBee, from, to, chance, fromDisplay, toDisplay);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                BeeIngredient source = BeeIngredient.fromNetwork(buffer);

                BlockState from = NBTUtil.readBlockState(buffer.readAnySizeNbt());
                BlockState to = NBTUtil.readBlockState(buffer.readAnySizeNbt());

                return this.factory.create(id, Lazy.of(() -> source), from, to, buffer.readInt(), Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.bee.get().toNetwork(buffer);

                buffer.writeNbt(NBTUtil.writeBlockState(recipe.stateFrom));
                buffer.writeNbt(NBTUtil.writeBlockState(recipe.stateTo));

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
        CompoundNBT tag = new CompoundNBT();

        tag.putString("Name", JSONUtils.getAsString(json, "Name"));

        if (json.has("Properties")) {
            CompoundNBT propertyTag = new CompoundNBT();

            JsonObject properties = JSONUtils.getAsJsonObject(json, "Properties");
            for(Map.Entry<String, JsonElement> entry : properties.entrySet()) {
                propertyTag.putString(entry.getKey(), entry.getValue().getAsString());
            }

            tag.put("Properties", propertyTag);
        }

        return NBTUtil.readBlockState(tag);
    }
}
