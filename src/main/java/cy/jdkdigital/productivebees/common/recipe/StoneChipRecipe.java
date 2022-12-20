package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.StoneChip;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;

public class StoneChipRecipe implements CraftingRecipe
{
    public final ResourceLocation id;
    public final Integer count;

    public StoneChipRecipe(ResourceLocation id, Integer count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        Block chipBlock = null;

        int matchingStacks = 0;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                // Set the recipe criteria to the first chip
                if (chipBlock == null && itemstack.getItem().equals(ModItems.STONE_CHIP.get())) {
                    chipBlock = StoneChip.getBlock(itemstack);
                }

                if (itemstack.getItem().equals(ModItems.STONE_CHIP.get()) && StoneChip.getBlock(itemstack).equals(chipBlock)) {
                    matchingStacks++;
                } else {
                    return false;
                }
            }
        }

        return matchingStacks == count;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack stack = inv.getItem(0);

        return new ItemStack(StoneChip.getBlock(stack));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        int min = count > 4 ? 3 : 2;
        return width >= min && height >= min;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem() {
        return new ItemStack(Items.STONE);
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        for (int i = 0; i < count; i++) {
            list.add(Ingredient.of(StoneChip.getStack(Blocks.STONE)));
        }

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
        return ModRecipeTypes.STONE_CHIP.get();
    }

    public static class Serializer<T extends StoneChipRecipe> implements RecipeSerializer<T>
    {
        final StoneChipRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(StoneChipRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Integer count = GsonHelper.getAsInt(json, "count", 9);

            return this.factory.create(id, count);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading stone chip recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                buffer.writeInt(recipe.count);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing stone chip recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends StoneChipRecipe>
        {
            T create(ResourceLocation id, Integer count);
        }
    }
}