package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableCombRecipe implements ICraftingRecipe
{
    public final ResourceLocation id;
    public final Integer count;

    public ConfigurableCombRecipe(ResourceLocation id, Integer count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        // If we have one configurable comb block it's valid
        if (stacks.size() == 1 && stacks.get(0).getItem().equals(ModItems.CONFIGURABLE_COMB_BLOCK.get())) {
            return stacks.get(0).hasTag();
        }

        // Honeycombs must match the defined number in the prototype recipe and have the same NBT data
        CompoundNBT type = null;
        if (stacks.size() == count) {
            for (ItemStack itemstack: stacks) {
                if (!itemstack.isEmpty() && itemstack.getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get()) && itemstack.hasTag()) {
                    if (type == null) {
                        type = itemstack.getTag();
                    }
                    if (type != null && !type.equals(itemstack.getTag())) {
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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        List<ItemStack> stacks = getItemsInInventory(inv);

        if (stacks.size() > 0) {
            ItemStack inStack = stacks.get(0);

            ItemStack outStack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
            if (inStack.getItem().equals(ModItems.CONFIGURABLE_COMB_BLOCK.get())) {
                outStack = new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get(), count);
            }

            outStack.setTag(inStack.getTag());
            ProductiveBees.LOGGER.info("ConfigurableCombRecipe outStack " + outStack);

            return outStack;
        }
        return ItemStack.EMPTY;
    }

    private List<ItemStack> getItemsInInventory(CraftingInventory inv) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                stacks.add(itemstack);
            }
        }
        return stacks;
    }

    @Override
    public boolean canFit(int width, int height) {
        int min = count > 4 ? 3 : 2;
        return width >= min && height >= min;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ModItems.CONFIGURABLE_HONEYCOMB.get());
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CONFIGURABLE_COMB.get();
    }

    public static class Serializer<T extends ConfigurableCombRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final ConfigurableCombRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(ConfigurableCombRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            Integer count = JSONUtils.getInt(json, "count", 4);

            return this.factory.create(id, count);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id, buffer.readInt());
            } catch (Exception e) {
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                buffer.writeInt(recipe.count);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends ConfigurableCombRecipe>
        {
            T create(ResourceLocation id, Integer count);
        }
    }
}