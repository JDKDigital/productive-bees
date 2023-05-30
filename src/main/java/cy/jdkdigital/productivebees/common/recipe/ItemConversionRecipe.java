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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemConversionRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final List<Lazy<BeeIngredient>> bees;
    public Ingredient ingredient;
    public ItemStack output;
    public final int chance;
    public final boolean pollinates;

    public ItemConversionRecipe(ResourceLocation id, List<Lazy<BeeIngredient>> bees, Ingredient ingredient, ItemStack output, int chance, boolean pollinates) {
        this.id = id;
        this.bees = bees;
        this.ingredient = ingredient;
        this.output = output;
        this.chance = chance;
        this.pollinates = pollinates;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.ItemInventory && bees.size() > 0) {
            String beeName = ((BeeHelper.ItemInventory) inv).getIdentifier(0);
            ItemStack inputItem = ((BeeHelper.ItemInventory) inv).getInput();

            boolean matchesInput = this.ingredient.test(inputItem);

            boolean matchesBee = false;
            for (Lazy<BeeIngredient> bee: bees) {
                matchesBee = matchesBee || bee.get().getBeeType().toString().equals(beeName);
            }

            return matchesBee && matchesInput;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
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
        return ModRecipeTypes.ITEM_CONVERSION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ITEM_CONVERSION_TYPE.get();
    }

    public static class Serializer<T extends ItemConversionRecipe> implements RecipeSerializer<T>
    {
        final ItemConversionRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(ItemConversionRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            List<Lazy<BeeIngredient>> bees = new ArrayList<>();

            if (json.has("bee")) {
                String source = GsonHelper.getAsString(json, "bee");
                Lazy<BeeIngredient> sourceBee = Lazy.of(BeeIngredientFactory.getIngredient(source));
                bees.add(sourceBee);
            } else if (json.has("bees")) {
                var beeArray = GsonHelper.getAsJsonArray(json, "bees");
                beeArray.forEach(jsonElement -> {
                    Lazy<BeeIngredient> sourceBee = Lazy.of(BeeIngredientFactory.getIngredient(jsonElement.getAsString()));
                    bees.add(sourceBee);
                });
            }

            Ingredient input;
            if (GsonHelper.isArrayNode(json, "ingredients")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            } else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredients"));
            }
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            int chance = GsonHelper.getAsInt(json, "chance", 100);
            boolean pollinates = GsonHelper.getAsBoolean(json, "pollinates", false);

            return this.factory.create(id, bees, input, output, chance, pollinates);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                int beeCount = buffer.readInt();
                List<Lazy<BeeIngredient>> bees = new ArrayList<>();
                for (var i = 0;i < beeCount;i++) {
                    BeeIngredient source = BeeIngredient.fromNetwork(buffer);
                    bees.add(Lazy.of(() -> source));
                }

                return this.factory.create(id, bees, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readInt(), buffer.readBoolean());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading item conversion recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeInt(recipe.bees.size());
                recipe.bees.forEach(bee -> bee.get().toNetwork(buffer));

                recipe.ingredient.toNetwork(buffer);
                buffer.writeItem(recipe.output);

                buffer.writeInt(recipe.chance);

                buffer.writeBoolean(recipe.pollinates);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing item conversion recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends ItemConversionRecipe>
        {
            T create(ResourceLocation id, List<Lazy<BeeIngredient>> beeInput, Ingredient input, ItemStack output, int chance, boolean pollinates);
        }
    }
}
