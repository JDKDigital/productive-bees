package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableHoneycombRecipe implements CraftingRecipe
{
    public final ResourceLocation id;
    public final Integer count;

    public ConfigurableHoneycombRecipe(ResourceLocation id, Integer count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        // Honeycombs must match the defined number in the prototype recipe and have the same NBT data
        CompoundTag type = null;
        if (stacks.size() == count) {
            for (ItemStack itemstack : stacks) {
                if (!itemstack.isEmpty() && itemstack.getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get()) && itemstack.hasTag()) {
                    if (type == null) {
                        type = itemstack.getTag();
                    }
                    if (type != null && !type.equals(itemstack.getTag())) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() > 0) {
            ItemStack inStack = stacks.get(0);

            ItemStack outStack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());

            outStack.setTag(inStack.getTag());

            return outStack;
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> getItemsInInventory(CraftingContainer inv) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                stacks.add(itemstack);
            }
        }
        return stacks;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= count;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        for (int i = 0; i < count; i++) {
            nonnulllist.add(Ingredient.of(new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get())));
        }
        return nonnulllist;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CONFIGURABLE_HONEYCOMB.get();
    }

    public static class Serializer<T extends ConfigurableHoneycombRecipe> implements RecipeSerializer<T>
    {
        final ConfigurableHoneycombRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(ConfigurableHoneycombRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            return this.factory.create(id, 4);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading config honeycomb recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeInt(recipe.count);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing config honeycomb recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends ConfigurableHoneycombRecipe>
        {
            T create(ResourceLocation id, Integer count);
        }
    }
}