package cy.jdkdigital.productivebees.common.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
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

public class CombineGeneRecipe implements CraftingRecipe
{
    public final ResourceLocation id;

    public CombineGeneRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        // Valid if inv contains one or more genes of the same type
        // genes must not be mutually exclusive (2 levels of the same attribute are not allowed)
        int numberOfIngredients = 0;
        Pair<String, Integer> addedGene = null;
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    numberOfIngredients++;
                    String attribute = Gene.getAttributeName(itemstack);

                    if (addedGene == null) {
                        addedGene = Pair.of(attribute, Gene.getValue(itemstack));
                    }
                    else if (!addedGene.getFirst().equals(attribute) || !addedGene.getSecond().equals(Gene.getValue(itemstack)) || Gene.getPurity(itemstack) == 100) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
        }
        return numberOfIngredients > 1;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
        // Combine genes
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.getContainerSize(); ++j) {
            stacks.add(inv.getItem(j));
        }

        return mergeGenes(stacks);
    }

    public static ItemStack mergeGenes(List<ItemStack> stacks) {
        String attribute = null;
        int value = 0;
        int purity = 0;

        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.getItem().equals(ModItems.GENE.get())) {
                    attribute = Gene.getAttributeName(stack);
                    value = Gene.getValue(stack);
                    purity = Math.min(100, purity + Gene.getPurity(stack));
                }
            }
        }

        if (attribute != null) {
            return Gene.getStack(attribute, value, 1, purity);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return new ItemStack(ModItems.GENE.get());
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        list.add(Ingredient.of(new ItemStack(ModItems.GENE.get())));
        list.add(Ingredient.of(new ItemStack(ModItems.GENE.get())));

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
        return ModRecipeTypes.GENE_GENE.get();
    }

    public static class Serializer<T extends CombineGeneRecipe> implements RecipeSerializer<T>
    {
        final CombineGeneRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(CombineGeneRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            return this.factory.create(id);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading gene recipe from packet. " + id, e);
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
        }

        public interface IRecipeFactory<T extends CombineGeneRecipe>
        {
            T create(ResourceLocation id);
        }
    }
}