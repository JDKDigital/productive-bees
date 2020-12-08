package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class CombineGeneRecipe implements ICraftingRecipe
{
    public final ResourceLocation id;

    public CombineGeneRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // Valid if inv contains 1 honey treat and any number of genes
        // genes must not be mutually exclusive (2 levels of the same attribute are not allowed)
        Pair<String, Integer> addedGene = null;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    String attribute = Gene.getAttributeName(itemstack);

                    if (addedGene == null) {
                        addedGene = Pair.of(attribute, Gene.getValue(itemstack));
                    }
                    else if (!addedGene.getFirst().equals(attribute) || !addedGene.getSecond().equals(Gene.getValue(itemstack)) || Gene.getPurity(itemstack) == 100) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        // Combine genes
        String attribute = null;
        int value = 0;
        int purity = 0;

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    attribute = Gene.getAttributeName(itemstack);
                    value = Gene.getValue(itemstack);
                    purity = Math.min(100, purity + Gene.getPurity(itemstack));
                }
            }
        }
        if (attribute != null) {
            return Gene.getStack(attribute, value, 1, purity);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ModItems.GENE.get());
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        list.add(Ingredient.fromStacks(new ItemStack(ModItems.GENE.get())));
        list.add(Ingredient.fromStacks(new ItemStack(ModItems.GENE.get())));

        return list;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GENE_GENE.get();
    }

    public static class Serializer<T extends CombineGeneRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final CombineGeneRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(CombineGeneRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            return this.factory.create(id);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading gene recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {}

        public interface IRecipeFactory<T extends CombineGeneRecipe>
        {
            T create(ResourceLocation id);
        }
    }
}