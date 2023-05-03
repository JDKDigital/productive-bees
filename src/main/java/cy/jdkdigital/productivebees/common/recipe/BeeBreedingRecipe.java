package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonArray;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeeBreedingRecipe implements Recipe<Container>
{
    public final ResourceLocation id;
    public final List<Lazy<BeeIngredient>> ingredients;
    public final Lazy<BeeIngredient> offspring;

    public BeeBreedingRecipe(ResourceLocation id, List<Lazy<BeeIngredient>> ingredients, Lazy<BeeIngredient> offspring) {
        this.id = id;
        this.ingredients = ingredients;
        this.offspring = offspring;
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory) {
            String beeName1 = ((BeeHelper.IdentifierInventory) inv).getIdentifier(0);
            String beeName2 = ((BeeHelper.IdentifierInventory) inv).getIdentifier(1);
            for (Lazy<BeeIngredient> parent : ingredients) {
                if (parent.get() != null) {
                    String parentName = parent.get().getBeeType().toString();
                    if (!parentName.equals(beeName1) && !parentName.equals(beeName2)) {
                        return false;
                    }
                } else {
                    ProductiveBees.LOGGER.warn("Bee not found in breeding recipe " + id);
                    return false;
                }
            }
            return true;
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
        return ModRecipeTypes.BEE_BREEDING.get();
    }

    @Nonnull
    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BEE_BREEDING_TYPE.get();
    }

    public static class Serializer<T extends BeeBreedingRecipe> implements RecipeSerializer<T>
    {
        final BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String parentName1 = GsonHelper.getAsString(json, "parent1");
            String parentName2 = GsonHelper.getAsString(json, "parent2");

            Lazy<BeeIngredient> offspringIngredient = null;
            JsonArray offspring = GsonHelper.getAsJsonArray(json, "offspring");
            if (!offspring.isEmpty()) {
                offspringIngredient = Lazy.of(BeeIngredientFactory.getIngredient(offspring.get(0).getAsString()));
            }

            Lazy<BeeIngredient> beeIngredientParent1 = Lazy.of(BeeIngredientFactory.getIngredient(parentName1));
            Lazy<BeeIngredient> beeIngredientParent2 = Lazy.of(BeeIngredientFactory.getIngredient(parentName2));

            return this.factory.create(id, Arrays.asList(beeIngredientParent1, beeIngredientParent2), offspringIngredient);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                List<Lazy<BeeIngredient>> ingredients = new ArrayList<>();

                BeeIngredient ing1 = BeeIngredient.fromNetwork(buffer);
                BeeIngredient ing2 = BeeIngredient.fromNetwork(buffer);
                ingredients.add(Lazy.of(() -> ing1));
                ingredients.add(Lazy.of(() -> ing2));

                BeeIngredient offspring = BeeIngredient.fromNetwork(buffer);

                return this.factory.create(id, ingredients, Lazy.of(() -> offspring));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee breeding recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
                    if (ingredient.get() != null) {
                        ingredient.get().toNetwork(buffer);
                    } else {
                        throw new RuntimeException("Bee breeding recipe ingredient missing " + recipe.getId() + " - " + ingredient);
                    }
                }

                if (recipe.offspring.get() != null) {
                    recipe.offspring.get().toNetwork(buffer);
                } else {
                    throw new RuntimeException("Bee breeding recipe child missing " + recipe.getId() + " - " + recipe.offspring.get());
                }
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee breeding recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeBreedingRecipe>
        {
            T create(ResourceLocation id, List<Lazy<BeeIngredient>> input, Lazy<BeeIngredient> output);
        }
    }
}
