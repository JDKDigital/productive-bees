package cy.jdkdigital.productivebees.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class AdvancedBeehiveRecipe implements IRecipe<IInventory>, IProductiveBeesRecipe {

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final List<Pair<ItemStack,Double>> outputs;
    public final int chance;

    public AdvancedBeehiveRecipe(ResourceLocation id, Ingredient ingredient, List<Pair<ItemStack,Double>> outputs, int chance) {
        this.id = id;
        this.ingredient = ingredient;
        this.outputs = outputs;
        this.chance = chance;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return null;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return null;
    }
}
