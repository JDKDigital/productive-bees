package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.RegistryAccess;
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
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;

public class BeeNBTChangerRecipe implements Recipe<Container>
{
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
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.ItemInventory && bee.get() != null) {
            String beeName = ((BeeHelper.ItemInventory) inv).getIdentifier(0);
            ItemStack item = ((BeeHelper.ItemInventory) inv).getInput();

            String parentName = bee.get().getBeeType().toString();

            boolean matchesItem = false;
            for (ItemStack stack : this.item.getItems()) {
                if (ItemStack.isSameItem(stack, item)) {
                    var tag = item.getTag();
                    if (tag != null && tag.contains(attribute) && tag.getInt(attribute) > min && tag.getInt(attribute) < max) {
                        matchesItem = true;
                    }
                }
            }

            return parentName.equals(beeName) && matchesItem;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
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
        return ModRecipeTypes.BEE_NBT_CHANGER.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_NBT_CHANGER_TYPE.get();
    }

    public static class Serializer<T extends BeeNBTChangerRecipe> implements RecipeSerializer<T>
    {
        final BeeNBTChangerRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeNBTChangerRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String beeName = GsonHelper.getAsString(json, "bee");

            Lazy<BeeIngredient> bee = Lazy.of(BeeIngredientFactory.getIngredient(beeName));

            Ingredient item;
            if (GsonHelper.isArrayNode(json, "item")) {
                item = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "item"));
            } else {
                item = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "item"));
            }

            String attribute = GsonHelper.getAsString(json, "attribute");
            String method = GsonHelper.getAsString(json, "method");
            int value = GsonHelper.getAsInt(json, "value", 0);
            int min = GsonHelper.getAsInt(json, "min", 0);
            int max = GsonHelper.getAsInt(json, "max", 100);

            return this.factory.create(id, bee, item, attribute, method, value, min, max);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                BeeIngredient bee = BeeIngredient.fromNetwork(buffer);
                return this.factory.create(id, Lazy.of(() -> bee), Ingredient.fromNetwork(buffer), buffer.readUtf(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee conversion recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
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
