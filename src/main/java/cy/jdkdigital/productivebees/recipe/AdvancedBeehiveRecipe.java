package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe implements IRecipe<IInventory>
{
    public static final IRecipeType<AdvancedBeehiveRecipe> ADVANCED_BEEHIVE = IRecipeType.register(ProductiveBees.MODID + ":advanced_beehive");

    public final ResourceLocation id;
    public final BeeIngredient ingredient;
    public final Map<ItemStack, IntArrayNBT> output;
    public final double chance;

    public AdvancedBeehiveRecipe(ResourceLocation id, BeeIngredient ingredient, Map<ItemStack, IntArrayNBT> output, double chance) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
        this.chance = chance;
    }

    @Override
    public String toString() {
        return "AdvancedBeehiveRecipe{" +
                "id=" + id +
                ", bee=" + ingredient.getBeeType() +
                ", outputs=" + output +
                ", chance=" + chance +
                '}';
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && ingredient != null) {
            String beeName = ((BeeHelper.IdentifierInventory)inv).getIdentifier();
            return beeName.equals(ingredient.getBeeType().getRegistryName().toString());
        }
        if (ingredient == null) {
            ProductiveBees.LOGGER.info(id + " is null");
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
        return ModRecipeTypes.ADVANCED_BEEHIVE.get();
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return ADVANCED_BEEHIVE;
    }

    public static class Serializer<T extends AdvancedBeehiveRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T>
    {
        final IRecipeFactory<T> factory;

        public Serializer(Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            String beeName = JSONUtils.getString(json, "ingredient");

            BeeIngredient beeIngredient = BeeIngredientFactory.getOrCreateList().get(beeName);

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "results");
            Map<ItemStack, IntArrayNBT> outputs = new HashMap<>();
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                Ingredient produce;
                String ingredientKey = "item_produce";
                if (ProductiveBeesConfig.GENERAL.enableCombProduce.get()) {
                    ingredientKey = "comb_produce";
                }

                if (JSONUtils.isJsonArray(json, ingredientKey)) {
                    produce = Ingredient.deserialize(JSONUtils.getJsonArray(jsonObject, ingredientKey));
                }
                else {
                    produce = Ingredient.deserialize(JSONUtils.getJsonObject(jsonObject, ingredientKey));
                }

                ItemStack[] stacks = produce.getMatchingStacks();

                if (stacks.length > 0) {
                    int min = 1;
                    int max = 1;
                    if (ingredientKey.equals("item_produce")) {
                        min = JSONUtils.getInt(jsonObject, "min", 1);
                        max = JSONUtils.getInt(jsonObject, "max", 1);
                    }
                    int chance = JSONUtils.getInt(jsonObject, "chance", 100);

                    IntArrayNBT nbt = new IntArrayNBT(new int[]{min, max, chance});
                    outputs.put(stacks[0], nbt);
                } else {
                    ProductiveBees.LOGGER.debug("Empty " + ingredientKey + " recipe " + id);
                }
            });

            double chance = ProductiveBeeEntity.getProductionChance(beeName, 0.65D);

            return this.factory.create(id, beeIngredient, outputs, chance);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            try {
                BeeIngredient ingredient = BeeIngredient.read(buffer);
                Map<ItemStack, IntArrayNBT> output = new HashMap<>();
                IntStream.range(0, buffer.readInt()).forEach(
                    i -> output.put(buffer.readItemStack(), new IntArrayNBT(new int[]{buffer.readInt(), buffer.readInt(), buffer.readInt()}))
                );
                double chance = buffer.readDouble();
                return this.factory.create(id, ingredient, output, chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error reading recipe from packet.", e);
                throw e;
            }
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            try {
                recipe.ingredient.write(buffer);
                buffer.writeInt(recipe.output.size());

                recipe.output.forEach((key, value) -> {
                    buffer.writeItemStack(key);
                    buffer.writeInt(value.get(0).getInt());
                    buffer.writeInt(value.get(1).getInt());
                    buffer.writeInt(value.get(2).getInt());
                });

                buffer.writeDouble(recipe.chance);
            } catch (Exception e) {
                ProductiveBees.LOGGER.error("Error writing recipe to packet.", e);
                throw e;
            }
        }

        public interface IRecipeFactory<T extends AdvancedBeehiveRecipe>
        {
            T create(ResourceLocation id, BeeIngredient input, Map<ItemStack, IntArrayNBT> output, double chance);
        }
    }
}
