package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
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
import java.util.List;
import java.util.stream.IntStream;

public class BeeBreedingRecipe implements IRecipe<IInventory>, IProductiveBeesRecipe {

    public static final IRecipeType<BeeBreedingRecipe> BEE_BREEDING = IRecipeType.register(ProductiveBees.MODID + ":bee_breeding");

    public final ResourceLocation id;
    public final List<ProduciveBeesJeiPlugin.BeeIngredient> ingredients;
    public final ProduciveBeesJeiPlugin.BeeIngredient output;

    public BeeBreedingRecipe(ResourceLocation id, List<ProduciveBeesJeiPlugin.BeeIngredient> ingredients, ProduciveBeesJeiPlugin.BeeIngredient output) {
        this.id = id;
        this.ingredients = ingredients;
        this.output = output;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        ProductiveBees.LOGGER.info("Comparing recipe: " + inv + " - " + this.ingredients);
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
        return null;
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_BREEDING;
    }

    public static class Serializer<T extends BeeBreedingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        final BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeBreedingRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            List<ProduciveBeesJeiPlugin.BeeIngredient> ingredients = new ArrayList<>();
            ProduciveBeesJeiPlugin.BeeIngredient output = null;

            return this.factory.create(id, ingredients, output);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            List<ProduciveBeesJeiPlugin.BeeIngredient> ingredients = new ArrayList<>();
            ingredients.add(ProduciveBeesJeiPlugin.BeeIngredient.read(buffer));
            ingredients.add(ProduciveBeesJeiPlugin.BeeIngredient.read(buffer));
            ProduciveBeesJeiPlugin.BeeIngredient output = ProduciveBeesJeiPlugin.BeeIngredient.read(buffer);
            return this.factory.create(id, ingredients, output);
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            for(ProduciveBeesJeiPlugin.BeeIngredient ingredient: recipe.ingredients) {
                ingredient.write(buffer);
            }
            recipe.output.write(buffer);
        }

        public interface IRecipeFactory<T extends BeeBreedingRecipe> {
            T create(ResourceLocation id, List<ProduciveBeesJeiPlugin.BeeIngredient> input, ProduciveBeesJeiPlugin.BeeIngredient output);
        }
    }
}
