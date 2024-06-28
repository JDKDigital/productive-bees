package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivebees.util.GeneValue;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CombineGeneRecipe implements CraftingRecipe
{
    public CombineGeneRecipe(String name) {
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
    public boolean matches(CraftingInput inv, Level worldIn) {
        // Valid if inv contains one or more genes of the same type
        // genes must not be mutually exclusive (2 levels of the same attribute are not allowed)
        int numberOfIngredients = 0;
        GeneValue addedGene = null;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    var gene = Gene.getGene(itemstack);
                    numberOfIngredients++;

                    if (addedGene == null) {
                        addedGene = GeneValue.byName(gene.value());
                    } else if (!addedGene.equals(GeneValue.byName(gene.value())) || Gene.getPurity(itemstack) == 100) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return numberOfIngredients > 1;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        // Combine genes
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.size(); ++j) {
            stacks.add(inv.getItem(j));
        }

        return mergeGenes(stacks);
    }

    public static ItemStack mergeGenes(List<ItemStack> stacks) {
        GeneGroup geneGroup = null;
        int purity = 0;

        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.getItem().equals(ModItems.GENE.get())) {
                    geneGroup = Gene.getGene(stack);
                    purity = Math.min(100, purity + Gene.getPurity(stack));
                }
            }
        }

        if (geneGroup != null) {
            return Gene.getStack(geneGroup.attribute(), geneGroup.value(), 1, purity);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GENE_GENE.get();
    }

    public static class Serializer implements RecipeSerializer<CombineGeneRecipe>
    {
        private static final MapCodec<CombineGeneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.STRING.fieldOf("id").orElse("").forGetter(recipe -> "")
                        )
                        .apply(builder, CombineGeneRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CombineGeneRecipe> STREAM_CODEC = StreamCodec.of(
                CombineGeneRecipe.Serializer::toNetwork, CombineGeneRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CombineGeneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CombineGeneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static CombineGeneRecipe fromNetwork(@Nonnull FriendlyByteBuf buffer) {
            try {
                return new CombineGeneRecipe("");
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading gene recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull FriendlyByteBuf buffer, CombineGeneRecipe recipe) {
        }
    }
}