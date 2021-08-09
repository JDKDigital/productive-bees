package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
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
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class IncubationRecipe implements Recipe<Container>
{
    public static final RecipeType<IncubationRecipe> INCUBATION = RecipeType.register(ProductiveBees.MODID + ":incubation");

    public final ResourceLocation id;
    public final Ingredient input;
    public final Ingredient catalyst;
    public final Ingredient result;

    public IncubationRecipe(ResourceLocation id, Ingredient input, Ingredient catalyst, Ingredient result) {
        this.id = id;
        this.input = input;
        this.catalyst = catalyst;
        this.result = result;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
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
        return ModRecipeTypes.INCUBATION.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return INCUBATION;
    }

    public static class Serializer<T extends IncubationRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T>
    {
        final IncubationRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(IncubationRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input;
            if (GsonHelper.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            }
            else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }

            Ingredient catalyst;
            if (GsonHelper.isArrayNode(json, "catalyst")) {
                catalyst = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "catalyst"));
            }
            else {
                catalyst = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "catalyst"));
            }

            Ingredient output;
            if (GsonHelper.isArrayNode(json, "output")) {
                output = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "output"));
            }
            else {
                output = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "output"));
            }

            return this.factory.create(id, input, catalyst, output);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer), Ingredient.fromNetwork(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee incubation recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.input.toNetwork(buffer);
                recipe.catalyst.toNetwork(buffer);
                recipe.result.toNetwork(buffer);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee incubation recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends IncubationRecipe>
        {
            T create(ResourceLocation id, Ingredient item, Ingredient catalyst, Ingredient output);
        }
    }
}
