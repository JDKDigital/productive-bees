package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoneyTreatGeneRecipe implements CraftingRecipe
{
    public final ItemStack honeyTreat;

    public HoneyTreatGeneRecipe(ItemStack honeyTreat) {
        this.honeyTreat = honeyTreat;
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
        // Valid if inv contains 1 honey treat and any number of genes
        // genes must not be mutually exclusive (2 levels of the same attribute are not allowed)
        Map<GeneAttribute, String> addedGenes = new HashMap<>();
        ItemStack honeyTreatStack = null;
        boolean hasAddedGenes = false;
        boolean hasTypeGene = false;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.HONEY_TREAT.get()) && honeyTreatStack == null) {
                    honeyTreatStack = itemstack;
                    // Read existing attributes from treat
                    List<GeneGroup> genes = HoneyTreat.getGenes(honeyTreatStack);
                    for (GeneGroup GeneGroup : genes) {
                        GeneAttribute attribute = GeneGroup.attribute();
                        if (addedGenes.containsKey(attribute) && !addedGenes.get(attribute).equals(GeneGroup.value())) {
                            return false;
                        }
                        addedGenes.put(attribute, GeneGroup.value());
                        if (attribute.equals(GeneAttribute.TYPE)) {
                            hasTypeGene = true;
                        }
                    }
                } else if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    GeneAttribute attribute = Gene.getAttribute(itemstack);
                    // Treats can have either 1 type gene or a mix of other genes
                    if (attribute.equals(GeneAttribute.TYPE)) {
                        if (addedGenes.size() > 0 && !addedGenes.containsKey(attribute)) {
                            return false;
                        }
                        addedGenes.put(attribute, Gene.getValue(itemstack));
                        hasAddedGenes = true;
                        hasTypeGene = true;
                    } else if (!hasTypeGene) {
                        if (addedGenes.containsKey(attribute) && !addedGenes.get(attribute).equals(Gene.getValue(itemstack))) {
                            // Disallow adding genes of the same type with different strengths
                            return false;
                        } else {
                            addedGenes.put(attribute, Gene.getValue(itemstack));
                            hasAddedGenes = true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        if (honeyTreatStack == null) {
            return false;
        }
        return hasAddedGenes;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        // Combine genes with honey treat
        ItemStack treat = null;
        List<ItemStack> genes = new ArrayList<>();

        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.HONEY_TREAT.get())) {
                    treat = itemstack;
                } else if (itemstack.getItem().equals(ModItems.GENE.get())) {
                    genes.add(itemstack);
                }
            }
        }

        if (treat != null) {
            final ItemStack honeyTreat = treat.copy();
            genes.forEach(gene -> {
                HoneyTreat.addGene(honeyTreat, gene);
            });
            honeyTreat.setCount(1);

            return honeyTreat;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return this.honeyTreat;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        list.add(Ingredient.of(honeyTreat.copy()));
        list.add(Ingredient.of(new ItemStack(ModItems.GENE.get())));

        return list;
    }

    @Nonnull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.GENE_TREAT.get();
    }

    public static class Serializer implements RecipeSerializer<HoneyTreatGeneRecipe>
    {
        private static final MapCodec<HoneyTreatGeneRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                ItemStack.CODEC.fieldOf("item").orElse(new ItemStack(ModItems.HONEY_TREAT.get())).forGetter(recipe -> recipe.honeyTreat)
                        )
                        .apply(builder, HoneyTreatGeneRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HoneyTreatGeneRecipe> STREAM_CODEC = StreamCodec.of(
                HoneyTreatGeneRecipe.Serializer::toNetwork, HoneyTreatGeneRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<HoneyTreatGeneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HoneyTreatGeneRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static HoneyTreatGeneRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new HoneyTreatGeneRecipe(ItemStack.STREAM_CODEC.decode(buffer));
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading honey treat gene recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, HoneyTreatGeneRecipe recipe) {
            try {
                ItemStack.STREAM_CODEC.encode(buffer, recipe.honeyTreat);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing honey treat gene recipe to packet. ", e);
                throw e;
            }
        }
    }
}