package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableCombBlockRecipe implements CraftingRecipe
{
    public static final MapCodec<ConfigurableCombBlockRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                            Codec.INT.fieldOf("count").orElse(4).forGetter(recipe -> recipe.count)
                    )
                    .apply(builder, ConfigurableCombBlockRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigurableCombBlockRecipe> STREAM_CODEC = StreamCodec.of(
            ConfigurableCombBlockRecipe::toNetwork, ConfigurableCombBlockRecipe::fromNetwork
    );

    public static final RecipeSerializer<ConfigurableCombBlockRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Integer count;

    public ConfigurableCombBlockRecipe(Integer count) {
        this.count = count;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        // If we have one single configurable comb block, it's valid
        if (stacks.size() == 1 && stacks.get(0).getItem().equals(ModItems.CONFIGURABLE_COMB_BLOCK.get())) {
            return stacks.get(0).has(ModDataComponents.BEE_TYPE);
        }

        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingInput inv) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() > 0) {
            ItemStack inStack = stacks.get(0);

            ItemStack outStack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), count);

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
    public RecipeSerializer<ConfigurableCombBlockRecipe> getSerializer() {
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

    public static ConfigurableCombBlockRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            return new ConfigurableCombBlockRecipe(buffer.readInt());
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading config comb block recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, ConfigurableCombBlockRecipe recipe) {
        try {
            buffer.writeInt(recipe.count);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing config comb block recipe to packet. ", e);
            throw e;
        }
    }
}
