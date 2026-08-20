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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HoneyTreatGeneRecipe implements CraftingRecipe
{
    public static final MapCodec<HoneyTreatGeneRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            ItemStackTemplate.CODEC.optionalFieldOf("item").forGetter(recipe -> Optional.ofNullable(recipe.honeyTreatOverride))
                    )
                    .apply(builder, HoneyTreatGeneRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, HoneyTreatGeneRecipe> STREAM_CODEC = StreamCodec.of(
            HoneyTreatGeneRecipe::toNetwork, HoneyTreatGeneRecipe::fromNetwork
    );

    public static final RecipeSerializer<HoneyTreatGeneRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final ItemStackTemplate honeyTreatOverride;
    private ItemStack resolvedTreat;

    public HoneyTreatGeneRecipe(Optional<ItemStackTemplate> honeyTreat) {
        this.honeyTreatOverride = honeyTreat.orElse(null);
    }

    public ItemStack honeyTreat() {
        if (resolvedTreat == null) {
            resolvedTreat = honeyTreatOverride != null ? honeyTreatOverride.create() : new ItemStack(ModItems.HONEY_TREAT.get());
        }
        return resolvedTreat;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        Map<GeneAttribute, String> addedGenes = new HashMap<>();
        ItemStack honeyTreatStack = null;
        boolean hasAddedGenes = false;
        boolean hasTypeGene = false;
        for (int j = 0; j < inv.size(); ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.HONEY_TREAT.get()) && honeyTreatStack == null) {
                    honeyTreatStack = itemstack;
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
                    if (attribute.equals(GeneAttribute.TYPE)) {
                        if (addedGenes.size() > 0 && !addedGenes.containsKey(attribute)) {
                            return false;
                        }
                        addedGenes.put(attribute, Gene.getValue(itemstack));
                        hasAddedGenes = true;
                        hasTypeGene = true;
                    } else if (!hasTypeGene) {
                        if (addedGenes.containsKey(attribute) && !addedGenes.get(attribute).equals(Gene.getValue(itemstack))) {
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv) {
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
    public RecipeSerializer<HoneyTreatGeneRecipe> getSerializer() {
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

    public static HoneyTreatGeneRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new HoneyTreatGeneRecipe(ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).decode(buffer));
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading honey treat gene recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, HoneyTreatGeneRecipe recipe) {
        try {
            ByteBufCodecs.optional(ItemStackTemplate.STREAM_CODEC).encode(buffer, Optional.ofNullable(recipe.honeyTreatOverride));
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing honey treat gene recipe to packet. ", e);
            throw e;
        }
    }
}
