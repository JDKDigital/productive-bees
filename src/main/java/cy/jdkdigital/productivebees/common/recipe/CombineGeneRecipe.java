package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.GeneGroup;
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
    public static final MapCodec<CombineGeneRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Codec.STRING.fieldOf("id").orElse("").forGetter(recipe -> "")
                    )
                    .apply(builder, CombineGeneRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CombineGeneRecipe> STREAM_CODEC = StreamCodec.of(
            CombineGeneRecipe::toNetwork, CombineGeneRecipe::fromNetwork
    );

    public static final RecipeSerializer<CombineGeneRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public CombineGeneRecipe(String name) {
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        // Valid if inv contains one or more genes of the same type
        // genes must not be mutually exclusive (2 levels of the same attribute are not allowed)
        int numberOfIngredients = 0;
        String addedGene = "";
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    if (Gene.getPurity(itemstack) == 100) {
                        return false;
                    }
                    GeneGroup gene = Gene.getGene(itemstack);
                    numberOfIngredients++;
                    if (addedGene.isEmpty()) {
                        addedGene = gene.value();
                    } else if (!addedGene.equals(gene.value())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return numberOfIngredients > 1;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv) {
        // Combine genes
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.size(); ++j) {
            stacks.add(inv.getItem(j));
        }

        return mergeGenes(stacks).getFirst();
    }

    public static Pair<ItemStack, ItemStack> mergeGenes(List<ItemStack> stacks) {
        GeneGroup geneGroup = null;
        int purity = 0;

        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.getItem().equals(ModItems.GENE.get())) {
                    geneGroup = Gene.getGene(stack);
                    purity = purity + Gene.getPurity(stack);
                }
            }
        }

        if (geneGroup != null) {
            var combineStack = Gene.getStack(geneGroup.attribute(), geneGroup.value(), 1, Math.min(100, purity));
            var leftoverStack = purity > 100 ? Gene.getStack(geneGroup.attribute(), geneGroup.value(), 1, purity - 100) : ItemStack.EMPTY;
            return Pair.of(combineStack, leftoverStack);
        }
        return Pair.of(ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<CombineGeneRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    public static CombineGeneRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new CombineGeneRecipe("");
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading gene recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, CombineGeneRecipe recipe) {
    }
}
