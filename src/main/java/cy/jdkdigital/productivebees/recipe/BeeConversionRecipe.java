package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class BeeConversionRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeConversionRecipe> BEE_CONVERSION = IRecipeType.register(ProductiveBees.MODID + ":bee_conversion");

    public final ResourceLocation id;
    public final BeeIngredient source;
    public final BeeIngredient result;
    public final Ingredient item;

    public BeeConversionRecipe(ResourceLocation id, BeeIngredient ingredients, BeeIngredient result, Ingredient item) {
        this.id = id;
        this.source = ingredients;
        this.result = result;
        this.item = item;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(0);
            String itemName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);

            String parentName = source.getBeeType().toString();

            boolean matchesItem = false;
            for (ItemStack stack : this.item.getMatchingStacks()) {
                if (stack.getItem().getRegistryName().toString().equals(itemName)) {
                    matchesItem = true;
                }
            }

            return parentName.equals(beeName) && matchesItem;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
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
        return ModRecipeTypes.BEE_CONVERSION.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_CONVERSION;
    }

    public static class Serializer<T extends BeeConversionRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeConversionRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeConversionRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T read(ResourceLocation id, JsonObject json) {
            String source = JSONUtils.getString(json, "source");
            String result = JSONUtils.getString(json, "result");

            BeeIngredient sourceBee = BeeIngredientFactory.getOrCreateList().get(source);
            BeeIngredient resultBee = BeeIngredientFactory.getOrCreateList().get(result);

            Ingredient item;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                item = Ingredient.deserialize(JSONUtils.getJsonArray(json, "item"));
            }
            else {
                item = Ingredient.deserialize(JSONUtils.getJsonObject(json, "item"));
            }

            return this.factory.create(id, sourceBee, resultBee, item);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            return this.factory.create(id, BeeIngredient.read(buffer), BeeIngredient.read(buffer), Ingredient.read(buffer));
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            recipe.source.write(buffer);
            recipe.result.write(buffer);
            recipe.item.write(buffer);
        }

        public interface IRecipeFactory<T extends BeeConversionRecipe>
        {
            T create(ResourceLocation id, BeeIngredient input, BeeIngredient output, Ingredient item);
        }
    }
}
