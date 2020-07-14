package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class BeeBreedingRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeBreedingRecipe> BEE_BREEDING = IRecipeType.register(ProductiveBees.MODID + ":bee_breeding");

    public final ResourceLocation id;
    public final List<BeeIngredient> ingredients;
    public final List<BeeIngredient> offspring;

    public BeeBreedingRecipe(ResourceLocation id, List<BeeIngredient> ingredients, List<BeeIngredient> offspring) {
        this.id = id;
        this.ingredients = ingredients;
        this.offspring = offspring;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.BeeInventory) {
            String beeName1 = ((BeeHelper.BeeInventory)inv).getBeeIdentifier(0);
            String beeName2 = ((BeeHelper.BeeInventory)inv).getBeeIdentifier(1);
            boolean matches = true;
            for (BeeIngredient parent: ingredients) {
                String parentName = parent.getBeeType().getRegistryName().getPath();
                if (!parentName.equals(beeName1 + "_bee") && !parentName.equals(beeName2 + "_bee")) {
                    matches = false;
                    break;
                }
            }
            return matches;
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BEE_BREEDING.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_BREEDING;
    }

    public static class Serializer<T extends BeeBreedingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T read(ResourceLocation id, JsonObject json) {
            String parentName1 = JSONUtils.getString(json, "parent1");
            String parentName2 = JSONUtils.getString(json, "parent2");

            List<BeeIngredient> children = new ArrayList<>();
            JsonArray offspring = JSONUtils.getJsonArray(json, "offspring");
            offspring.forEach(el -> {
                String child = el.getAsString();
                children.add(BeeIngredientFactory.getOrCreateList().get(child));
            });

            BeeIngredient beeIngredientParent1 = BeeIngredientFactory.getOrCreateList().get(parentName1);
            BeeIngredient beeIngredientParent2 = BeeIngredientFactory.getOrCreateList().get(parentName2);

            return this.factory.create(id, Arrays.asList(beeIngredientParent1, beeIngredientParent2), children);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            List<BeeIngredient> ingredients = new ArrayList<>();
            ingredients.add(BeeIngredient.read(buffer));
            ingredients.add(BeeIngredient.read(buffer));

            List<BeeIngredient> offspring = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(
                i -> offspring.add(BeeIngredient.read(buffer))
            );

            return this.factory.create(id, ingredients, offspring);
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            for (BeeIngredient ingredient : recipe.ingredients) {
                ingredient.write(buffer);
            }

            buffer.writeInt(recipe.offspring.size());
            recipe.offspring.forEach((child) -> {
                child.write(buffer);
            });
        }

        public interface IRecipeFactory<T extends BeeBreedingRecipe>
        {
            T create(ResourceLocation id, List<BeeIngredient> input, List<BeeIngredient> output);
        }
    }
}
