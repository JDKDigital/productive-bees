package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.item.WoodChip;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;

public class WoodChipRecipe implements ICraftingRecipe
{
    public final ResourceLocation id;
    public final Integer count;

    public WoodChipRecipe(ResourceLocation id, Integer count) {
        this.id = id;
        this.count = count;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        Block chipBlock = null;

        int matchingStacks = 0;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                // Set the recipe criteria to the first wood chip
                if (chipBlock == null && itemstack.getItem().equals(ModItems.WOOD_CHIP.get())) {
                    chipBlock = WoodChip.getWoodBlock(itemstack);
                }

                if (itemstack.getItem().equals(ModItems.WOOD_CHIP.get()) && WoodChip.getWoodBlock(itemstack).equals(chipBlock)) {
                    matchingStacks++;
                } else {
                    return false;
                }
            }
        }

        return matchingStacks == count;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack stack = inv.getStackInSlot(0);

        return new ItemStack(WoodChip.getWoodBlock(stack));
    }

    @Override
    public boolean canFit(int width, int height) {
        int min = count > 4 ? 3 : 2;
        return width >= min && height >= min;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(Items.OAK_LOG);
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        for (int i = 0; i < count; i++) {
            list.add(Ingredient.fromStacks(WoodChip.getStack(Blocks.OAK_LOG)));
        }

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
        return ModRecipeTypes.WOOD_CHIP.get();
    }

    public static class Serializer<T extends WoodChipRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final WoodChipRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(WoodChipRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            Integer count = JSONUtils.getInt(json, "count", 9);

            return this.factory.create(id, count);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id, buffer.readInt());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading woodchip recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                buffer.writeInt(recipe.count);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing woodchip recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends WoodChipRecipe>
        {
            T create(ResourceLocation id, Integer count);
        }
    }
}