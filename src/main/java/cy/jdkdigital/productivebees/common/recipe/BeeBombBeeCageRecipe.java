package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeBomb;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BeeBombBeeCageRecipe implements CraftingRecipe
{
    public final ResourceLocation id;
    public final ItemStack beeBomb;

    public BeeBombBeeCageRecipe(ResourceLocation id, ItemStack beeBomb) {
        this.id = id;
        this.beeBomb = beeBomb;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        // Valid if inv contains 1 bee bomb and any number of bee cages up to 10 (configurable)
        ItemStack beeBombStack = null;
        int beeCount = 0;
        int bombBeeCount = 0;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (beeBombStack == null && (itemstack.getItem().equals(ModItems.BEE_BOMB.get()) || itemstack.getItem().equals(ModItems.BEE_BOMB_ANGRY.get()))) {
                    beeBombStack = itemstack;

                    // Read existing bee list from bomb
                    ListTag bees = BeeBomb.getBees(beeBombStack);

                    beeCount += bees.size();
                    bombBeeCount = bees.size();
                }
                else if (itemstack.getItem().equals(ModItems.BEE_CAGE.get()) && BeeCage.isFilled(itemstack)) {
                    beeCount++;
                }
                else {
                    return false;
                }
            }
        }
        if (beeBombStack == null) {
            return false;
        }

        if (bombBeeCount == beeCount) {
            return false;
        }

        return beeCount > 0 && beeCount <= ProductiveBeesConfig.GENERAL.numberOfBeesPerBomb.get();
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        // Combine bee cages with bee bomb
        ItemStack bomb = null;
        List<ItemStack> beeCages = new ArrayList<>();

        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.BEE_BOMB.get()) || itemstack.getItem().equals(ModItems.BEE_BOMB_ANGRY.get())) {
                    bomb = itemstack;
                }
                else if (itemstack.getItem().equals(ModItems.BEE_CAGE.get())) {
                    beeCages.add(itemstack);
                }
            }
        }

        if (bomb != null) {
            final ItemStack beeBomb = bomb.copy();
            beeCages.forEach(beeCage -> {
                BeeBomb.addBee(beeBomb, beeCage);
            });
            beeBomb.setCount(1);

            return beeBomb;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.beeBomb;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        list.add(Ingredient.of(beeBomb.copy()));

        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());

        CompoundTag nbt = new CompoundTag();
        nbt.putString("entity", EntityType.getKey(EntityType.BEE).toString());
        cage.setTag(nbt);
        list.add(Ingredient.of(cage));

        return list;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_CAGE_BOMB.get();
    }

    public static class Serializer<T extends BeeBombBeeCageRecipe> implements RecipeSerializer<T>
    {
        final BeeBombBeeCageRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBombBeeCageRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            return this.factory.create(id, new ItemStack(ModItems.BEE_BOMB.get()));
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, buffer.readItem());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee bomb cage recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeItem(recipe.beeBomb);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee bomb cage recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeBombBeeCageRecipe>
        {
            T create(ResourceLocation id, ItemStack beeBomb);
        }
    }
}