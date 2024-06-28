package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableHoneycombRecipe implements CraftingRecipe
{
    public final Integer count;

    public ConfigurableHoneycombRecipe(Integer count) {
        this.count = count;
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
    public boolean matches(CraftingInput inv, Level level) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        // Honeycombs must match the defined number in the prototype recipe and have the same NBT data
        ResourceLocation type = null;
        if (stacks.size() == count) {
            for (ItemStack itemstack : stacks) {
                if (!itemstack.isEmpty() && itemstack.getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get()) && itemstack.has(ModDataComponents.BEE_TYPE)) {
                    if (type == null) {
                        type = itemstack.get(ModDataComponents.BEE_TYPE);
                    }
                    if (type != null && !type.equals(itemstack.get(ModDataComponents.BEE_TYPE))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() > 0) {
            ItemStack inStack = stacks.get(0);

            ItemStack outStack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());

            outStack.set(ModDataComponents.BEE_TYPE, inStack.get(ModDataComponents.BEE_TYPE));

            return outStack;
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> getItemsInInventory(CraftingInput inv) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.size(); ++j) {
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
    public ItemStack getResultItem(HolderLookup.Provider registryAccess) {
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
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CONFIGURABLE_HONEYCOMB.get();
    }

    public static class Serializer implements RecipeSerializer<ConfigurableHoneycombRecipe>
    {
        private static final MapCodec<ConfigurableHoneycombRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Codec.INT.fieldOf("count").orElse(4).forGetter(recipe -> recipe.count)
                        )
                        .apply(builder, ConfigurableHoneycombRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ConfigurableHoneycombRecipe> STREAM_CODEC = StreamCodec.of(
                ConfigurableHoneycombRecipe.Serializer::toNetwork, ConfigurableHoneycombRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<ConfigurableHoneycombRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ConfigurableHoneycombRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static ConfigurableHoneycombRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new ConfigurableHoneycombRecipe(buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading config honeycomb recipe from packet. ", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, ConfigurableHoneycombRecipe recipe) {
            try {
                buffer.writeInt(recipe.count);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing config honeycomb recipe to packet. ", e);
                throw e;
            }
        }
    }
}