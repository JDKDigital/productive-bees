package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<BeeSpawningRecipe> BEE_SPAWNING = IRecipeType.register(ProductiveBees.MODID + ":bee_spawning");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final List<BeeIngredient> output;
    public final int repopulationCooldown;

    public BeeSpawningRecipe(ResourceLocation id, Ingredient ingredient, List<BeeIngredient> output, int repopulationCooldown) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.repopulationCooldown = repopulationCooldown;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
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
        return ModRecipeTypes.BEE_SPAWNING.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_SPAWNING;
    }

    public static class Serializer<T extends BeeSpawningRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(BeeSpawningRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T read(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            }
            else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "results");
            List<BeeIngredient> output = new ArrayList<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                String beeName = JSONUtils.getString(jsonObject, "bee");
                output.add(BeeIngredientFactory.getOrCreateList().get(beeName));
            });

            int repopulationCooldown = JSONUtils.getInt(json, "repopulation_cooldown", 36000);

            return this.factory.create(id, ingredient, output, repopulationCooldown);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                Ingredient ingredient = Ingredient.read(buffer);

                List<BeeIngredient> output = new ArrayList<>();
                IntStream.range(0, buffer.readInt()).forEach(
                        i -> output.add(BeeIngredient.read(buffer))
                );

                int repopulationCooldown = buffer.readInt();

                return this.factory.create(id, ingredient, output, repopulationCooldown);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading recipe from packet.", e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.ingredient.write(buffer);

                buffer.writeInt(recipe.output.size());
                for (BeeIngredient beeOutput : recipe.output) {
                    beeOutput.write(buffer);
                }

                buffer.writeInt(recipe.repopulationCooldown);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing recipe to packet.", e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends BeeSpawningRecipe>
        {
            T create(ResourceLocation id, Ingredient input, List<BeeIngredient> output, int repopulationCooldown);
        }
    }
}
