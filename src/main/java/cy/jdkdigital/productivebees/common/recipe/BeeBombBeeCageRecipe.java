package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.item.BeeBomb;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BeeBombBeeCageRecipe implements CraftingRecipe
{
    public static final MapCodec<BeeBombBeeCageRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            ItemStackTemplate.CODEC.fieldOf("bee_bomb").forGetter(recipe -> recipe.beeBombTemplate)
                    )
                    .apply(builder, BeeBombBeeCageRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeBombBeeCageRecipe> STREAM_CODEC = StreamCodec.of(
            BeeBombBeeCageRecipe::toNetwork, BeeBombBeeCageRecipe::fromNetwork
    );

    public static final RecipeSerializer<BeeBombBeeCageRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final ItemStackTemplate beeBombTemplate;

    public BeeBombBeeCageRecipe(ItemStackTemplate beeBombTemplate) {
        this.beeBombTemplate = beeBombTemplate;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput inv, Level worldIn) {
        // Valid if inv contains 1 bee bomb and any number of bee cages up to 10 (configurable)
        ItemStack beeBombStack = null;
        int beeCount = 0;
        int bombBeeCount = 0;
        for (int j = 0; j < inv.size(); ++j) {
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv) {
        // Combine bee cages with bee bomb
        ItemStack bomb = null;
        List<ItemStack> beeCages = new ArrayList<>();

        for (int j = 0; j < inv.size(); ++j) {
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
    public RecipeSerializer<BeeBombBeeCageRecipe> getSerializer() {
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

    public static BeeBombBeeCageRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new BeeBombBeeCageRecipe(ItemStackTemplate.STREAM_CODEC.decode(buffer));
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading bee bomb cage recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, BeeBombBeeCageRecipe recipe) {
        try {
            ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.beeBombTemplate);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing bee bomb cage recipe to packet. ", e);
            throw e;
        }
    }
}
