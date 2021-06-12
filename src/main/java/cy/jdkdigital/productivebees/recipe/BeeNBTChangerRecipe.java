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
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class BeeNBTChangerRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeNBTChangerRecipe> BEE_NBT_CHANGER = IRecipeType.register(ProductiveBees.MODID + ":bee_nbt_changer");

    public final ResourceLocation id;
    public final Lazy<BeeIngredient> bee;
    public final Ingredient item;
    public String attribute;
    public String method;
    public int value;
    public int min;
    public int max;

    public BeeNBTChangerRecipe(ResourceLocation id, Lazy<BeeIngredient> ingredients, Ingredient item, String attribute, String method, int value, int min, int max) {
        this.id = id;
        this.bee = ingredients;
        this.item = item;
        this.attribute = attribute;
        this.method = method;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && bee.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(0);
            String itemName = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);

            String parentName = bee.get().getBeeType().toString();

            boolean matchesItem = false;
            for (ItemStack stack : this.item.getItems()) {
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
        return ModRecipeTypes.BEE_NBT_CHANGER.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_NBT_CHANGER;
    }

    public static class Serializer<T extends BeeNBTChangerRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeNBTChangerRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeNBTChangerRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String beeName = JSONUtils.getAsString(json, "bee");

            Lazy<BeeIngredient> bee = Lazy.of(BeeIngredientFactory.getIngredient(beeName));

            Ingredient item;
            if (JSONUtils.isArrayNode(json, "ingredient")) {
                item = Ingredient.fromJson(JSONUtils.getAsJsonArray(json, "item"));
            } else {
                item = Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "item"));
            }

            String attribute = JSONUtils.getAsString(json, "attribute");
            String method = JSONUtils.getAsString(json, "method");
            int value = JSONUtils.getAsInt(json, "value", 0);
            int min = JSONUtils.getAsInt(json, "min", 0);
            int max = JSONUtils.getAsInt(json, "max", 100);

            return this.factory.create(id, bee, item, attribute, method, value, min, max);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                BeeIngredient bee = BeeIngredient.fromNetwork(buffer);
                return this.factory.create(id, Lazy.of(() -> bee), Ingredient.fromNetwork(buffer), buffer.readUtf(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.bee.get().toNetwork(buffer);
                recipe.item.toNetwork(buffer);
                buffer.writeUtf(recipe.attribute);
                buffer.writeUtf(recipe.method);
                buffer.writeInt(recipe.value);
                buffer.writeInt(recipe.min);
                buffer.writeInt(recipe.max);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee conversion recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeNBTChangerRecipe>
        {
            T create(ResourceLocation id, Lazy<BeeIngredient> input, Ingredient item, String attribute, String method, int value, int min, int max);
        }
    }
}
