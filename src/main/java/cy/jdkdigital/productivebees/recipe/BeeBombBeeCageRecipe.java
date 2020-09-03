package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.item.BeeBomb;
import cy.jdkdigital.productivebees.item.BeeCage;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BeeBombBeeCageRecipe implements ICraftingRecipe
{
    public final ResourceLocation id;
    public final ItemStack beeBomb;

    public BeeBombBeeCageRecipe(ResourceLocation id, ItemStack beeBomb) {
        this.id = id;
        this.beeBomb = beeBomb;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        // Valid if inv contains 1 bee bomb and any number of bee cages up to 10 (configurable)
        ItemStack beeBombStack = null;
        int beeCount = 0;
        int bombBeeCount = 0;
        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (beeBombStack == null && (itemstack.getItem().equals(ModItems.BEE_BOMB.get()) || itemstack.getItem().equals(ModItems.BEE_BOMB_ANGRY.get()))) {
                    beeBombStack = itemstack;

                    // Read existing bee list from bomb
                    ListNBT bees = BeeBomb.getBees(beeBombStack);

                    beeCount += bees.size();
                    bombBeeCount = bees.size();
                } else if (itemstack.getItem().equals(ModItems.BEE_CAGE.get()) && BeeCage.isFilled(itemstack)) {
                    beeCount++;
                } else {
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

    @Nonnull
    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        // Combine bee cages with bee bomb
        ItemStack bomb = null;
        List<ItemStack> beeCages = new ArrayList<>();

        for(int j = 0; j < inv.getSizeInventory(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().equals(ModItems.BEE_BOMB.get()) || itemstack.getItem().equals(ModItems.BEE_BOMB_ANGRY.get())) {
                    bomb = itemstack;
                } else if (itemstack.getItem().equals(ModItems.BEE_CAGE.get())) {
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
    public boolean canFit(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return this.beeBomb;
    }

    @Nonnull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();

        list.add(Ingredient.fromStacks(beeBomb.copy()));

        ItemStack cage = new ItemStack(ModItems.BEE_CAGE.get());

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(EntityType.BEE).toString());
        cage.setTag(nbt);
        list.add(Ingredient.fromStacks(cage));

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
        return ModRecipeTypes.BEE_CAGE_BOMB.get();
    }

    public static class Serializer<T extends BeeBombBeeCageRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeBombBeeCageRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBombBeeCageRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            return this.factory.create(id, new ItemStack(ModItems.BEE_BOMB.get()));
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                return this.factory.create(id, buffer.readItemStack());
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading bee bomb cage recipe from packet. " + id, e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                buffer.writeItemStack(recipe.beeBomb);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing bee bomb cage recipe to packet. " + recipe.getId(), e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeBombBeeCageRecipe>
        {
            T create(ResourceLocation id, ItemStack beeBomb);
        }
    }
}