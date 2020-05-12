package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
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
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BeeSpawningBigRecipe extends BeeSpawningRecipe {

    public static final IRecipeType<BeeSpawningBigRecipe> BEE_SPAWNING = IRecipeType.register(ProductiveBees.MODID + ":bee_spawning_big");

    public BeeSpawningBigRecipe(ResourceLocation id, Ingredient ingredient, List<BeeIngredient> output, int repopulationCooldown) {
        super(id, ingredient, output, repopulationCooldown);
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ForgeRegistries.RECIPE_SERIALIZERS.getValue(ProduciveBeesJeiPlugin.CATEGORY_BEE_SPAWNING_BIG_UID);
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return BEE_SPAWNING;
    }
}
