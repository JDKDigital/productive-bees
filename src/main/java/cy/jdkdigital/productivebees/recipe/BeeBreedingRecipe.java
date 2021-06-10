package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.IntStream;

public class BeeBreedingRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeBreedingRecipe> BEE_BREEDING = IRecipeType.register(ProductiveBees.MODID + ":bee_breeding");

    public final ResourceLocation id;
    public final List<Lazy<BeeIngredient>> ingredients;
    public final Map<Lazy<BeeIngredient>, Integer> offspring;

    public BeeBreedingRecipe(ResourceLocation id, List<Lazy<BeeIngredient>> ingredients, Map<Lazy<BeeIngredient>, Integer> offspring) {
        this.id = id;
        this.ingredients = ingredients;
        this.offspring = offspring;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
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
        return ModRecipeTypes.BEE_BREEDING.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_BREEDING;
    }

    public static class Serializer<T extends BeeBreedingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            String parentName1 = JSONUtils.getAsString(json, "parent1");
            String parentName2 = JSONUtils.getAsString(json, "parent2");

            Map<Lazy<BeeIngredient>, Integer> children = new LinkedHashMap<>();
            JsonArray offspring = JSONUtils.getAsJsonArray(json, "offspring");
            offspring.forEach(el -> {
                if (el.isJsonObject()) {
                    String child = JSONUtils.getAsString(el.getAsJsonObject(), "offspring");
                    children.put(Lazy.of(BeeIngredientFactory.getIngredient(child)), JSONUtils.getAsInt(el.getAsJsonObject(), "weight"));
                } else {
                    String child = el.getAsString();
                    children.put(Lazy.of(BeeIngredientFactory.getIngredient(child)), 1);
                }
            });

            Lazy<BeeIngredient> beeIngredientParent1 = Lazy.of(BeeIngredientFactory.getIngredient(parentName1));
            Lazy<BeeIngredient> beeIngredientParent2 = Lazy.of(BeeIngredientFactory.getIngredient(parentName2));

            return this.factory.create(id, Arrays.asList(beeIngredientParent1, beeIngredientParent2), children);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                List<Lazy<BeeIngredient>> ingredients = new ArrayList<>();

                BeeIngredient ing1 = BeeIngredient.fromNetwork(buffer);
                BeeIngredient ing2 = BeeIngredient.fromNetwork(buffer);
                ingredients.add(Lazy.of(() -> ing1));
                ingredients.add(Lazy.of(() -> ing2));

                Map<Lazy<BeeIngredient>, Integer> offspring = new LinkedHashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(i -> {
                    BeeIngredient result = BeeIngredient.fromNetwork(buffer);
                    offspring.put(Lazy.of(() -> result), buffer.readInt());
                });

                return this.factory.create(id, ingredients, offspring);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee breeding recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                for (Lazy<BeeIngredient> ingredient : recipe.ingredients) {
                    if (ingredient.get() != null) {
                        ingredient.get().toNetwork(buffer);
                    } else {
                        throw new RuntimeException("Bee breeding recipe ingredient missing " + recipe.getId() + " - " + ingredient);
                    }
                }

                buffer.writeInt(recipe.offspring.size());
                recipe.offspring.forEach((child, weight) -> {
                    if (child.get() != null) {
                        child.get().toNetwork(buffer);
                        buffer.writeInt(weight);
                    } else {
                        throw new RuntimeException("Bee breeding recipe child missing " + recipe.getId() + " - " + child);
                    }
                });
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee breeding recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeBreedingRecipe>
        {
            T create(ResourceLocation id, List<Lazy<BeeIngredient>> input, Map<Lazy<BeeIngredient>, Integer> output);
        }
    }
}
